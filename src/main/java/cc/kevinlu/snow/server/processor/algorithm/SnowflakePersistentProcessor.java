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
import cc.kevinlu.snow.server.data.mapper.SnowflakeMapper;
import cc.kevinlu.snow.server.data.model.SnowflakeDO;
import cc.kevinlu.snow.server.data.model.SnowflakeDOExample;
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
public class SnowflakePersistentProcessor implements PersistentProcessor<Long> {

    @Autowired
    private BatchMapper        batchMapper;
    @Autowired
    private SnowflakeMapper    snowflakeMapper;
    @Autowired
    private RedisProcessor     redisProcessor;
    @Autowired
    private AsyncTaskProcessor asyncTaskProcessor;

    @Override
    public void asyncToCache(AsyncCacheBO asyncCacheBO) {
        List<Long> recordList = batchMapper.selectIdFromSnowflake(asyncCacheBO.getInstanceId(),
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
    public void syncToDb(PersistentBO<Long> persistent) {
        long instanceId = persistent.getInstanceId();
        List<Long> idList = persistent.getIdList();
        int status = persistent.getUsed() ? 1 : 0;
        int chunk = idList.size();
        Date date = new Date();
        SnowflakeDO snowflake;
        List<SnowflakeDO> records = new ArrayList<>();
        int index = 1;
        for (Long id : idList) {
            snowflake = new SnowflakeDO();
            snowflake.setChunk(chunk);
            snowflake.setServiceInstanceId(instanceId);
            snowflake.setGValue(id);
            snowflake.setStatus(status);
            snowflake.setGmtCreated(date);
            records.add(snowflake);
            if (index++ % Constants.BATCH_INSERT_SIZE == 0) {
                batchMapper.insertSnowflake(records);
                records.clear();
            }
        }
        if (!CollectionUtils.isEmpty(records)) {
            batchMapper.insertSnowflake(records);
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
        SnowflakeDOExample example = new SnowflakeDOExample();
        example.createCriteria().andIdIn(records);
        List<SnowflakeDO> dataList = snowflakeMapper.selectByExample(example);
        List<Long> result = dataList.stream().map(SnowflakeDO::getGValue).collect(Collectors.toList());
        asyncTaskProcessor.snowflakeStatus(records);
        redisProcessor.lTrim(key, acquireBO.getChunk() - 1, -1);
        return result;
    }
}
