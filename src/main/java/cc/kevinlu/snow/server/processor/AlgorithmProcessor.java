/**
 * MIT License
 *
 * Copyright (c) 2021 chuanyichuan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cc.kevinlu.snow.server.processor;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import cc.kevinlu.snow.client.enums.IdAlgorithmEnums;
import cc.kevinlu.snow.server.config.Constants;
import cc.kevinlu.snow.server.config.anno.AlgorithmAnno;
import cc.kevinlu.snow.server.config.anno.AlgorithmInject;
import cc.kevinlu.snow.server.data.mapper.BatchMapper;
import cc.kevinlu.snow.server.data.mapper.GroupMapper;
import cc.kevinlu.snow.server.data.model.GroupDO;
import cc.kevinlu.snow.server.pojo.PersistentBO;
import cc.kevinlu.snow.server.processor.algorithm.PersistentProcessor;
import cc.kevinlu.snow.server.processor.pojo.AsyncCacheBO;
import cc.kevinlu.snow.server.processor.pojo.RecordAcquireBO;
import cc.kevinlu.snow.server.processor.redis.RedisProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chuan
 */
@Slf4j
@Component
public class AlgorithmProcessor implements ApplicationContextAware {

    @Autowired
    private InstanceCacheProcessor instanceCacheProcessor;
    @Autowired
    private RedisProcessor         redisProcessor;

    @Autowired
    private GroupMapper            groupMapper;
    @Autowired
    private BatchMapper            batchMapper;

    private ApplicationContext     applicationContext;

    /**
     * get instance id
     *
     * @param groupId
     * @param instanceCode
     * @return
     */
    public Long instanceId(Long groupId, String instanceCode) {
        return instanceCacheProcessor.getInstanceId(groupId, instanceCode);
    }

    public void persistentToDb(PersistentBO persistent) {
        PersistentProcessor persistentProcessor = getProcessor(persistent.getMode());
        persistentProcessor.syncToDb(persistent);
    }

    /**
     * record snow times
     *
     * @param instanceId
     */
    public void recordSnowTimes(long instanceId) {
        batchMapper.updateSnowTimes(instanceId);
    }

    /**
     * record the last value
     *
     * @param groupId
     * @param value
     */
    public void recordGroupLastValue(Long groupId, String value) {
        GroupDO group = new GroupDO();
        group.setId(groupId);
        group.setLastValue(value);
        groupMapper.updateByPrimaryKeySelective(group);

        String key = String.format(Constants.GROUP_RECENT_MAX_VALUE_ITEM_PATTERN, groupId);
        redisProcessor.hset(Constants.GROUP_RECENT_MAX_VALUE_QUEUE, key, value);
    }

    public void asyncDataToCache(AsyncCacheBO asyncCacheBO) {
        PersistentProcessor persistentProcessor = getProcessor(asyncCacheBO.getMode());
        persistentProcessor.asyncToCache(asyncCacheBO);
    }

    private PersistentProcessor getProcessor(int mode) {
        IdAlgorithmEnums algorithm = IdAlgorithmEnums.getEnumByAlgorithm(mode);

        ServiceLoader<PersistentProcessor> serviceLoader = ServiceLoader.load(PersistentProcessor.class);
        Iterator<PersistentProcessor> iter = serviceLoader.iterator();
        while (iter.hasNext()) {
            PersistentProcessor persistent = iter.next();
            AlgorithmAnno anno = persistent.getClass().getAnnotation(AlgorithmAnno.class);
            Assert.notNull(anno, "algorithm name can not be empty!");
            if (algorithm.equals(anno.value())) {
                // 获取fields
                Field[] fields = persistent.getClass().getDeclaredFields();
                if (fields.length == 0) {
                    return persistent;
                }
                for (Field field : fields) {
                    field.setAccessible(true);
                    AlgorithmInject inject = field.getAnnotation(AlgorithmInject.class);
                    if (inject == null) {
                        continue;
                    }
                    Class clazz = inject.clazz();
                    Object instance = null;
                    // manual
                    if (inject.manual()) {
                        try {
                            instance = clazz.newInstance();
                        } catch (Exception e) {
                            log.error("manual build instance occur error!", e);
                        }
                        continue;
                    } else {
                        // get instance from spring
                        instance = applicationContext.getBean(clazz);
                    }
                    try {
                        field.set(persistent, instance);
                    } catch (IllegalAccessException e) {
                        log.error("");
                    }
                }

                return persistent;
            }
        }
        throw new RuntimeException("error!");
    }

    public List<Object> getRecords(RecordAcquireBO acquireBO) {
        long instanceId = instanceCacheProcessor.getInstanceId(acquireBO.getGroupId(), acquireBO.getInstanceCode());
        acquireBO.setInstanceId(instanceId);
        return getProcessor(acquireBO.getMode()).getRecords(acquireBO);
    }

    public String queryGroupDigitLastValue(Long groupId) {
        String key = String.format(Constants.GROUP_RECENT_MAX_VALUE_ITEM_PATTERN, groupId);
        String maxValue = (String) redisProcessor.hget(Constants.GROUP_RECENT_MAX_VALUE_QUEUE, key);
        if (StringUtils.isBlank(maxValue)) {
            GroupDO group = groupMapper.selectByPrimaryKey(groupId);
            maxValue = group.getLastValue();
            redisProcessor.hset(Constants.GROUP_RECENT_MAX_VALUE_QUEUE, key, maxValue);
        }
        return maxValue;
    }

    public Long timeStamp() {
        return redisProcessor.getTimestamp() * 10;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
