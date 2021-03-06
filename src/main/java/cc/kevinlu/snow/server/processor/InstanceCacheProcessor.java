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
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.nacos.common.utils.CollectionUtils;

import cc.kevinlu.snow.server.config.Constants;
import cc.kevinlu.snow.server.data.mapper.GroupMapper;
import cc.kevinlu.snow.server.data.mapper.ServiceInstanceMapper;
import cc.kevinlu.snow.server.data.model.GroupDO;
import cc.kevinlu.snow.server.data.model.GroupDOExample;
import cc.kevinlu.snow.server.data.model.ServiceInstanceDO;
import cc.kevinlu.snow.server.data.model.ServiceInstanceDOExample;
import cc.kevinlu.snow.server.processor.redis.RedisProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chuan
 */
@Slf4j
@Component
public class InstanceCacheProcessor implements InitializingBean {

    private static final String   INSTANCE_PATTERN = "%s_%s";

    @Autowired
    private RedisProcessor        redisProcessor;

    @Autowired
    private GroupMapper           groupMapper;

    @Autowired
    private ServiceInstanceMapper serviceInstanceMapper;

    /**
     * Let groupCode be added to the cache
     * 
     * @param groupCode
     */
    public void putGroupCode(String groupCode, Long id) {
        redisProcessor.hset(Constants.CACHE_GROUP_MAP, groupCode, id);
    }

    /**
     * Let instanceCode be added to the cache
     * 
     * @param instanceCode
     */
    public void putInstanceCode(Long groupId, String instanceCode, Long id) {
        redisProcessor.hset(Constants.CACHE_GROUP_INSTANT_MAP, String.format(INSTANCE_PATTERN, groupId, instanceCode),
                id);
    }

    /**
     * Verify that the group exists
     * 
     * @param groupCode
     * @return true: exists, false: not exists
     */
    public boolean hasGroup(String groupCode) {
        return redisProcessor.hHasKey(Constants.CACHE_GROUP_MAP, groupCode);
    }

    /**
     * Verify that the instance exists
     * 
     * @param groupId
     * @param instanceCode
     * @return true: exists, false: not exists
     */
    public boolean hasInstance(Long groupId, String instanceCode) {
        return redisProcessor.hHasKey(Constants.CACHE_GROUP_INSTANT_MAP,
                String.format(INSTANCE_PATTERN, groupId, instanceCode));
    }

    /**
     * remove key from group cache
     * 
     * @param groupCode
     */
    public void removeGroup(String groupCode) {
        redisProcessor.hdel(Constants.CACHE_GROUP_MAP, groupCode);
    }

    /**
     * remove key from instance cache
     * 
     * @param groupId
     * @param instanceCode
     */
    public void removeInstance(Long groupId, String instanceCode) {
        redisProcessor.hdel(Constants.CACHE_GROUP_INSTANT_MAP, String.format(INSTANCE_PATTERN, groupId, instanceCode));
    }

    /**
     * get id of group
     * 
     * @param groupCode
     * @return
     */
    public Long getGroupId(String groupCode) {
        return getGroupIdWithInit(groupCode, 0);
    }

    /**
     * get id of group, and will init cache if cache is empty
     * 
     * @param groupCode
     * @return
     */
    public Long getGroupIdWithInit(String groupCode, int times) {
        Object id = redisProcessor.hget(Constants.CACHE_GROUP_MAP, groupCode);
        if (id == null) {
            if (times == 0) {
                initCache();
                return getGroupIdWithInit(groupCode, ++times);
            }
            return null;
        }
        return Long.parseLong(String.valueOf(id));
    }

    /**
     * get id of service instance
     * 
     * @param groupId
     * @param instanceCode
     * @return
     */
    public Long getInstanceId(Long groupId, String instanceCode) {
        return getInstanceIdWithInit(groupId, instanceCode, 0);
    }

    /**
     * get id of service instance, and will init cache if cache is empty
     * 
     * @param groupId
     * @param instanceCode
     * @return
     */
    public Long getInstanceIdWithInit(Long groupId, String instanceCode, int times) {
        Object id = redisProcessor.hget(Constants.CACHE_GROUP_INSTANT_MAP,
                String.format(INSTANCE_PATTERN, groupId, instanceCode));
        if (id == null) {
            if (times == 0) {
                initCache();
                return getInstanceIdWithInit(groupId, instanceCode, ++times);
            }
            return null;
        }
        return Long.parseLong(String.valueOf(id));
    }

    private void initCache() {
        if (!redisProcessor.hasKey(Constants.CACHE_GROUP_MAP)) {
            List<GroupDO> groupList = groupMapper.selectByExample(new GroupDOExample());
            if (CollectionUtils.isNotEmpty(groupList)) {
                Map<String, Object> map = groupList.stream()
                        .collect(Collectors.toMap(GroupDO::getGroupCode, GroupDO::getId));
                redisProcessor.hmset(Constants.CACHE_GROUP_MAP, map);
            }
        }
        if (!redisProcessor.hasKey(Constants.CACHE_GROUP_INSTANT_MAP)) {
            List<ServiceInstanceDO> instanceList = serviceInstanceMapper
                    .selectByExample(new ServiceInstanceDOExample());
            if (CollectionUtils.isNotEmpty(instanceList)) {
                Map<String, Object> map = instanceList.stream().collect(Collectors.toMap(
                        v -> String.format(INSTANCE_PATTERN, v.getGroupId(), v.getServerCode()), v -> v.getId()));
                redisProcessor.hmset(Constants.CACHE_GROUP_INSTANT_MAP, map);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initCache();
    }
}
