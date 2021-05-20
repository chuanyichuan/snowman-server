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
package cc.kevinlu.snow.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import cc.kevinlu.snow.server.config.Constants;
import cc.kevinlu.snow.server.data.mapper.GroupMapper;
import cc.kevinlu.snow.server.data.model.GroupDO;
import cc.kevinlu.snow.server.data.model.GroupDOExample;
import cc.kevinlu.snow.server.generate.GenerateAlgorithmFactory;
import cc.kevinlu.snow.server.listener.pojo.PreGenerateBO;
import cc.kevinlu.snow.server.processor.AlgorithmProcessor;
import cc.kevinlu.snow.server.processor.pojo.RecordAcquireBO;
import cc.kevinlu.snow.server.processor.redis.RedisProcessor;
import cc.kevinlu.snow.server.processor.task.CheckChunkProcessor;
import cc.kevinlu.snow.server.processor.task.RedisMessageSender;
import cc.kevinlu.snow.server.processor.task.pojo.RegenerateBO;
import cc.kevinlu.snow.server.service.SnowflakeService;
import cc.kevinlu.snow.server.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chuan
 */
@Slf4j
@Service
public class SnowflakeServiceImpl implements SnowflakeService {

    @Autowired
    private GroupMapper              groupMapper;
    @Autowired
    private ThreadPoolTaskExecutor   taskExecutor;
    @Autowired
    private RedisProcessor           redisProcessor;
    @Autowired
    private AlgorithmProcessor       algorithmProcessor;
    @Autowired
    private CheckChunkProcessor      checkChunkProcessor;
    @Autowired
    private GenerateAlgorithmFactory generateAlgorithmFactory;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Object> generate(String groupCode, String instanceCode) {

        // acquire lock
        int lockTimes = 0;
        String key = String.format(Constants.CACHE_GENERATE_LOCK_PATTERN, groupCode);
        String value = Thread.currentThread().getId() + "_" + instanceCode;
        while (!redisProcessor.tryLockWithSet(key, value, Constants.DEFAULT_TIMEOUT)) {
            log.warn("[{}] - [{}] 第[{}]次尝试加锁失败", groupCode, instanceCode, ++lockTimes);
            if (lockTimes > 3) {
                throw new CannotAcquireLockException("acquire lock timeout!");
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
            }
        }
        try {
            log.warn("[{}] - [{}] 第[{}]次尝试加锁成功", groupCode, value, ++lockTimes);
            GroupDOExample groupExample = new GroupDOExample();
            groupExample.createCriteria().andGroupCodeEqualTo(groupCode);
            groupExample.setOrderByClause("id desc limit 1");
            List<GroupDO> groupList = groupMapper.selectByExample(groupExample);
            if (CollectionUtils.isEmpty(groupList)) {
                return null;
            }
            GroupDO group = groupList.get(0);

            // get from redis
            RecordAcquireBO acquireBO = RecordAcquireBO.builder().groupId(group.getId()).instanceCode(instanceCode)
                    .mode(group.getMode()).chunk(group.getChunk()).build();
            List<Object> recordList = algorithmProcessor.getRecords(acquireBO);
            if (CollectionUtils.isEmpty(recordList)) {
                // generate
                RegenerateBO regenerate = RegenerateBO.builder().groupId(group.getId()).group(groupCode)
                        .mode(group.getMode()).instance(instanceCode).chunk(group.getChunk())
                        .lastValue(group.getLastValue()).times(0).build();
                recordList = generateAlgorithmFactory.factory(group.getMode()).generate(regenerate);
            }
            return recordList;
        } catch (Exception e) {
            log.error("generate error!", e);
        } finally {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    redisProcessor.releaseLock(key, value);
                    log.warn("[{}] - [{}] 释放锁成功", groupCode, instanceCode);
                }

                @Override
                public void afterCompletion(int status) {
                    // check next chunk
                    PreGenerateBO preGenerateBO = PreGenerateBO.builder().group(groupCode).instance(instanceCode)
                            .times(1).build();
                    taskExecutor.execute(new RedisMessageSender(redisProcessor, preGenerateBO));
                }
            });
        }
        return null;
    }
}
