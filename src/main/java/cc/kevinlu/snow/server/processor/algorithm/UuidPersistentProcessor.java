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
package cc.kevinlu.snow.server.processor.algorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cc.kevinlu.snow.server.config.Constants;
import cc.kevinlu.snow.server.data.mapper.BatchMapper;
import cc.kevinlu.snow.server.data.mapper.UuidMapper;
import cc.kevinlu.snow.server.data.model.UuidDO;
import cc.kevinlu.snow.server.data.model.UuidDOExample;
import cc.kevinlu.snow.server.pojo.PersistentBO;
import cc.kevinlu.snow.server.pojo.enums.StatusEnums;
import cc.kevinlu.snow.server.processor.pojo.AsyncCacheBO;
import cc.kevinlu.snow.server.processor.pojo.RecordAcquireBO;
import cc.kevinlu.snow.server.processor.redis.RedisProcessor;
import cc.kevinlu.snow.server.processor.task.AsyncTaskProcessor;
import cc.kevinlu.snow.server.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * persistent data to db or redis
 * 
 * @author chuan
 */
@Slf4j
@Component
public class UuidPersistentProcessor implements PersistentProcessor<String> {

    @Autowired
    private BatchMapper        batchMapper;
    @Autowired
    private RedisProcessor     redisProcessor;
    @Autowired
    private UuidMapper         uuidMapper;
    @Autowired
    private AsyncTaskProcessor asyncTaskProcessor;

    @Override
    public void asyncToCache(AsyncCacheBO asyncCacheBO) {
        List<Long> recordList = batchMapper.selectIdFromUuid(asyncCacheBO.getInstanceId(),
                StatusEnums.USABLE.getStatus());
        if (CollectionUtils.isEmpty(recordList)) {
            log.debug("async to cache empty!");
            return;
        }
        String key = String.format(Constants.CACHE_ID_LOCK_PATTERN, asyncCacheBO.getGroupId(),
                asyncCacheBO.getInstanceId(), asyncCacheBO.getMode());
        redisProcessor.del(key);
        redisProcessor.lSet(key, recordList);
    }

    @Override
    public void syncToDb(PersistentBO<String> persistent) {
        long instanceId = persistent.getInstanceId();
        List<String> idList = persistent.getIdList();
        int status = persistent.getUsed() ? 1 : 0;
        int chunk = idList.size();
        Date date = new Date();
        List<UuidDO> records = new ArrayList<>();
        int index = 1;
        for (String id : idList) {
            UuidDO uuid = new UuidDO();
            uuid.setChunk(chunk);
            uuid.setServiceInstanceId(instanceId);
            uuid.setGValue(id);
            uuid.setStatus(status);
            uuid.setGmtCreated(date);
            records.add(uuid);
            if (index++ % Constants.BATCH_INSERT_SIZE == 0) {
                batchMapper.insertUuid(records);
                records.clear();
            }
        }
        if (!CollectionUtils.isEmpty(records)) {
            batchMapper.insertUuid(records);
        }
    }

    @Override
    public List<String> getRecords(RecordAcquireBO acquireBO) {
        String key = String.format(Constants.CACHE_ID_LOCK_PATTERN, acquireBO.getGroupId(), acquireBO.getInstanceId(),
                acquireBO.getMode());
        List records = redisProcessor.lGet(key, 0, acquireBO.getChunk() - 1);
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        UuidDOExample example = new UuidDOExample();
        example.createCriteria().andIdIn(records);
        List<UuidDO> dataList = uuidMapper.selectByExample(example);
        List<String> result = dataList.stream().map(UuidDO::getGValue).collect(Collectors.toList());
        asyncTaskProcessor.uuidStatus(records);
        redisProcessor.lTrim(key, acquireBO.getChunk() - 1, -1);
        return result;
    }

}
