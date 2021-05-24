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
package cc.kevinlu.snow.server.processor.task;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cc.kevinlu.snow.client.enums.IdAlgorithmEnums;
import cc.kevinlu.snow.server.config.Constants;
import cc.kevinlu.snow.server.generate.GenerateAlgorithmFactory;
import cc.kevinlu.snow.server.listener.pojo.PreGenerateBO;
import cc.kevinlu.snow.server.processor.InstanceCacheProcessor;
import cc.kevinlu.snow.server.processor.redis.RedisProcessor;
import cc.kevinlu.snow.server.processor.task.pojo.RegenerateBO;
import cc.kevinlu.snow.server.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 检查剩余预生成数据是否充裕
 * 
 * @author chuan
 */
@Slf4j
@Component
public class CheckChunkProcessor {

    @Autowired
    private RedisProcessor           redisProcessor;
    @Autowired
    private GenerateAlgorithmFactory generateAlgorithmFactory;
    @Autowired
    private InstanceCacheProcessor   instanceCacheProcessor;
    @Autowired
    private ThreadPoolTaskExecutor   taskExecutor;

    /**
     * The default pre-generate threshold
     */
    private static final int         DEFAULT_MULTIPLE_FACTOR = 7;
    /**
     * The default scaling threshold, which can also be configured by the client
     */
    private static final float       DEFAULT_LOAD_FACTOR     = 0.3f;

    /**
     * Check
     * 
     * @param regenerate
     * @return
     */
    public boolean preRegenerate(RegenerateBO regenerate) {
        // redis_key
        Long count = redisCount(regenerate);
        if (IdAlgorithmEnums.DIGIT.getAlgorithm() == regenerate.getMode()) {
            return count < DEFAULT_MULTIPLE_FACTOR * DEFAULT_LOAD_FACTOR;
        }
        return count <= regenerate.getChunk() * DEFAULT_MULTIPLE_FACTOR * DEFAULT_LOAD_FACTOR;
    }

    /**
     * query count from redis
     * 
     * @param regenerate
     * @return
     */
    private Long redisCount(RegenerateBO regenerate) {
        long instanceId = instanceCacheProcessor.getInstanceId(regenerate.getGroupId(), regenerate.getInstance());
        String key = String.format(Constants.CACHE_ID_LOCK_PATTERN, regenerate.getGroupId(), instanceId,
                regenerate.getMode());
        return redisProcessor.lGetListSize(key);
    }

    /**
     * regenerate
     * 
     * @param regenerate
     */
    @Transactional(rollbackFor = Exception.class)
    public void startRegenerate(RegenerateBO regenerate) {
        long total = regenerate.getChunk() * DEFAULT_MULTIPLE_FACTOR;
        long survivor = redisCount(regenerate);
        int size = (int) (total - survivor) / regenerate.getChunk();
        List idList = new ArrayList();
        regenerate.setChunk(regenerate.getChunk());

        for (int i = 0; i < size; i++) {
            // query newest value from db
            //            GroupDO group = groupMapper.selectByPrimaryKey(regenerate.getGroupId());
            //            regenerate.setLastValue(group.getLastValue());
            idList.addAll(generateAlgorithmFactory.factory(regenerate.getMode()).regenerate(regenerate));
        }
        if (CollectionUtils.isEmpty(idList)) {
            log.info("regenerate error!");
            PreGenerateBO preGenerateBO = new PreGenerateBO();
            preGenerateBO.setGroup(regenerate.getGroup());
            preGenerateBO.setInstance(regenerate.getInstance());
            preGenerateBO.setTimes(regenerate.getTimes() + 1);
            taskExecutor.execute(new RedisMessageSender(redisProcessor, preGenerateBO), 3000L);
            return;
        }
        // regenerate success!
    }
}
