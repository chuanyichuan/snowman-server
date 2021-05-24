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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cc.kevinlu.snow.server.config.Constants;
import cc.kevinlu.snow.server.data.mapper.BatchMapper;
import cc.kevinlu.snow.server.data.mapper.DigitMapper;
import cc.kevinlu.snow.server.data.model.DigitDO;
import cc.kevinlu.snow.server.pojo.PersistentBO;
import cc.kevinlu.snow.server.processor.pojo.AsyncCacheBO;
import cc.kevinlu.snow.server.processor.pojo.RecordAcquireBO;
import cc.kevinlu.snow.server.processor.redis.RedisProcessor;
import cc.kevinlu.snow.server.processor.task.AsyncTaskProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * persistent data to db or redis
 * 
 * @author chuan
 */
@Slf4j
@Component
public class DigitPersistentProcessor extends PersistentProcessor<Long> {

    @Autowired
    private DigitMapper        digitMapper;
    @Autowired
    private BatchMapper        batchMapper;
    @Autowired
    private RedisProcessor     redisProcessor;
    @Autowired
    private AsyncTaskProcessor asyncTaskProcessor;

    public static final String TABLE = "sm_digit";

    @Override
    public void asyncToCache(AsyncCacheBO asyncCacheBO) {
        super.asyncToCacheCall(asyncCacheBO, TABLE);
    }

    @Override
    public void syncToDb(PersistentBO<Long> persistent) {
        long instanceId = persistent.getInstanceId();
        List<Long> idList = persistent.getIdList();
        int status = persistent.getUsed() ? 1 : 0;
        long from = idList.get(0);
        long to = idList.get(idList.size() - 1);
        DigitDO digit = new DigitDO();
        digit.setChunk(idList.size());
        digit.setFromValue(from);
        digit.setToValue(to);
        digit.setServiceInstanceId(instanceId);
        digit.setStatus(status);
        digit.setGmtCreated(new Date());
        digitMapper.insertSelective(digit);
    }

    @Override
    public List<Long> getRecords(RecordAcquireBO acquireBO) {
        String key = String.format(Constants.CACHE_ID_LOCK_PATTERN, acquireBO.getGroupId(), acquireBO.getInstanceId(),
                acquireBO.getMode());
        Object record = redisProcessor.lPop(key);
        if (record == null) {
            return null;
        }
        Long recordId = Long.valueOf(String.valueOf(record));
        DigitDO data = digitMapper.selectByPrimaryKey(recordId);

        List<Long> result = new ArrayList<>();
        for (long i = data.getFromValue(); i <= data.getToValue(); i++) {
            result.add(i);
        }
        asyncTaskProcessor.digitStatus(recordId);
        return result;
    }

}
