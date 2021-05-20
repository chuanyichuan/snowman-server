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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cc.kevinlu.snow.client.enums.IdAlgorithmEnums;
import cc.kevinlu.snow.server.config.Constants;
import cc.kevinlu.snow.server.data.mapper.BatchMapper;
import cc.kevinlu.snow.server.data.mapper.GroupMapper;
import cc.kevinlu.snow.server.data.model.GroupDO;
import cc.kevinlu.snow.server.pojo.PersistentBO;
import cc.kevinlu.snow.server.processor.algorithm.DigitPersistentProcessor;
import cc.kevinlu.snow.server.processor.algorithm.PersistentProcessor;
import cc.kevinlu.snow.server.processor.algorithm.SnowflakePersistentProcessor;
import cc.kevinlu.snow.server.processor.algorithm.UuidPersistentProcessor;
import cc.kevinlu.snow.server.processor.pojo.AsyncCacheBO;
import cc.kevinlu.snow.server.processor.pojo.RecordAcquireBO;
import cc.kevinlu.snow.server.processor.redis.RedisProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chuan
 */
@Slf4j
@Component
public class AlgorithmProcessor {

    @Autowired
    private InstanceCacheProcessor       instanceCacheProcessor;
    @Autowired
    private UuidPersistentProcessor      uuidPersistentProcessor;
    @Autowired
    private SnowflakePersistentProcessor snowflakePersistentProcessor;
    @Autowired
    private DigitPersistentProcessor     digitPersistentProcessor;
    @Autowired
    private RedisProcessor               redisProcessor;

    @Autowired
    private GroupMapper                  groupMapper;
    @Autowired
    private BatchMapper                  batchMapper;

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
        switch (algorithm) {
            case UUID:
                return uuidPersistentProcessor;
            case SNOWFLAKE:
                return snowflakePersistentProcessor;
            default:
                return digitPersistentProcessor;
        }
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
}
