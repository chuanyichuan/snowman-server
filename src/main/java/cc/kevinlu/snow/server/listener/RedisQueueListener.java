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
package cc.kevinlu.snow.server.listener;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.alibaba.fastjson.JSONObject;

import cc.kevinlu.snow.server.config.Constants;
import cc.kevinlu.snow.server.data.mapper.GroupMapper;
import cc.kevinlu.snow.server.data.mapper.ServiceInstanceMapper;
import cc.kevinlu.snow.server.data.model.GroupDO;
import cc.kevinlu.snow.server.data.model.GroupDOExample;
import cc.kevinlu.snow.server.data.model.ServiceInstanceDOExample;
import cc.kevinlu.snow.server.listener.pojo.PreGenerateBO;
import cc.kevinlu.snow.server.processor.redis.RedisProcessor;
import cc.kevinlu.snow.server.processor.task.CheckChunkProcessor;
import cc.kevinlu.snow.server.processor.task.pojo.RegenerateBO;
import cc.kevinlu.snow.server.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis消息接收
 * 
 * @author chuan
 */
@Slf4j
@Component
public class RedisQueueListener {

    @Autowired
    private RedisProcessor              redisProcessor;
    @Autowired
    private GroupMapper                 groupMapper;
    @Autowired
    private ServiceInstanceMapper       serviceInstanceMapper;
    @Autowired
    private CheckChunkProcessor         checkChunkProcessor;
    @Autowired
    private Jackson2JsonRedisSerializer jacksonSerializer;
    @Autowired
    private RedisQueueListener          redisQueueListener;

    public void onMessage(String content) {
        log.info("receive check message from redis: content = [{}]", content);
        PreGenerateBO preGenerateBO = (PreGenerateBO) jacksonSerializer
                .deserialize(content.getBytes(StandardCharsets.UTF_8));
        redisQueueListener.checkChunkSize(preGenerateBO);
    }

    /**
     * 检查缓存中数据是否达标
     * 
     * @param preGenerateBO
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED, transactionManager = "code_transaction")
    public void checkChunkSize(PreGenerateBO preGenerateBO) {
        String groupCode = preGenerateBO.getGroup();
        String instanceCode = preGenerateBO.getInstance();
        String lock = String.format(Constants.CHECK_CHUNK_LOCK_PATTERN, groupCode);
        int time = 0;
        String value = Thread.currentThread().getId() + "_" + instanceCode;
        while (!redisProcessor.tryLockWithLua(lock, value, 3000000)) {
            log.warn("[{}] - [{}] 第[{}]次尝试加锁失败", groupCode, instanceCode, ++time);
            if (time >= 3) {
                return;
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
            }
        }

        try {
            // 校验数据
            log.warn("[{}] - [{}] 第[{}]次尝试加锁成功", groupCode, value, ++time);
            GroupDOExample groupExample = new GroupDOExample();
            groupExample.createCriteria().andGroupCodeEqualTo(groupCode);
            groupExample.setOrderByClause("id asc limit 1");
            List<GroupDO> groupList = groupMapper.selectByExample(groupExample);
            if (CollectionUtils.isEmpty(groupList)) {
                log.warn("group [{}] 不存在", groupCode);
                return;
            }
            GroupDO group = groupList.get(0);
            ServiceInstanceDOExample instanceExample = new ServiceInstanceDOExample();
            instanceExample.createCriteria().andGroupIdEqualTo(group.getId()).andServerCodeEqualTo(instanceCode);
            instanceExample.setOrderByClause("id asc limit 1");
            long instanceCount = serviceInstanceMapper.countByExample(instanceExample);
            if (instanceCount == 0L) {
                log.warn("instance [{}] 不存在", instanceCode);
                return;
            }
            // 检查当前是否需要扩容
            RegenerateBO regenerate = RegenerateBO.builder().groupId(group.getId()).group(groupCode)
                    .mode(group.getMode()).instance(instanceCode).chunk(group.getChunk())
                    .lastValue(group.getLastValue()).times(preGenerateBO.getTimes()).build();
            log.info("regenerate-object = [{}]", JSONObject.toJSONString(regenerate));
            boolean redo = checkChunkProcessor.preRegenerate(regenerate);
            if (redo) {
                // 生成ID
                checkChunkProcessor.startRegenerate(regenerate);
            }

        } catch (Exception e) {
            log.warn("regenerate error! msg = [{}]", e.getMessage(), e);
        } finally {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    redisProcessor.releaseLock(lock, value);
                    log.warn("[{}] - [{}] 释放锁成功", groupCode, instanceCode);
                }
            });
        }
    }
}
