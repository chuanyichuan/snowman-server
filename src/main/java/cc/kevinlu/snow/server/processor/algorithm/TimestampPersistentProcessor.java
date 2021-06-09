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

import cc.kevinlu.snow.client.enums.IdAlgorithmEnums;
import cc.kevinlu.snow.server.config.Constants;
import cc.kevinlu.snow.server.config.anno.AlgorithmAnno;
import cc.kevinlu.snow.server.config.anno.AlgorithmInject;
import cc.kevinlu.snow.server.data.mapper.BatchMapper;
import cc.kevinlu.snow.server.data.mapper.TimeStampMapper;
import cc.kevinlu.snow.server.data.model.TimeStampDO;
import cc.kevinlu.snow.server.data.model.TimeStampDOExample;
import cc.kevinlu.snow.server.pojo.PersistentBO;
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
@AlgorithmAnno(IdAlgorithmEnums.TIMESTAMP)
public class TimestampPersistentProcessor extends PersistentProcessor<Long> {

    @AlgorithmInject(clazz = BatchMapper.class)
    private BatchMapper        batchMapper;
    @AlgorithmInject(clazz = RedisProcessor.class)
    private RedisProcessor     redisProcessor;
    @AlgorithmInject(clazz = TimeStampMapper.class)
    private TimeStampMapper    timeStampMapper;
    @AlgorithmInject(clazz = AsyncTaskProcessor.class)
    private AsyncTaskProcessor asyncTaskProcessor;

    public static final String TABLE = "sm_timestamp";

    @Override
    public void asyncToCache(AsyncCacheBO asyncCacheBO) {
        super.asyncToCacheCall(asyncCacheBO, TABLE);
    }

    @Override
    public void syncToDb(PersistentBO<Long> persistent) {
        long instanceId = persistent.getInstanceId();
        List<Long> idList = persistent.getIdList();
        int status = persistent.getUsed() ? 1 : 0;
        int chunk = idList.size();
        Date date = new Date();
        List<TimeStampDO> records = new ArrayList<>();
        int index = 1;
        for (Long id : idList) {
            TimeStampDO uuid = new TimeStampDO();
            uuid.setChunk(chunk);
            uuid.setServiceInstanceId(instanceId);
            uuid.setGValue(String.valueOf(id));
            uuid.setStatus(status);
            uuid.setGmtCreated(date);
            records.add(uuid);
            if (index++ % Constants.BATCH_INSERT_SIZE == 0) {
                batchMapper.insertTimestamp(records);
                records.clear();
            }
        }
        if (!CollectionUtils.isEmpty(records)) {
            batchMapper.insertTimestamp(records);
        }
    }

    @Override
    public List<Long> getRecords(RecordAcquireBO acquireBO) {
        String key = String.format(Constants.CACHE_ID_LOCK_PATTERN, acquireBO.getGroupId(), acquireBO.getInstanceId(),
                acquireBO.getMode());
        List records = redisProcessor.lGet(key, 0, acquireBO.getChunk() - 1);
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        TimeStampDOExample example = new TimeStampDOExample();
        example.createCriteria().andIdIn(records);
        List<TimeStampDO> dataList = timeStampMapper.selectByExample(example);
        List<Long> result = dataList.stream().map(v -> Long.parseLong(v.getGValue())).collect(Collectors.toList());
        asyncTaskProcessor.timestampStatus(records);
        redisProcessor.lTrim(key, acquireBO.getChunk(), -1);
        return result;
    }

}
