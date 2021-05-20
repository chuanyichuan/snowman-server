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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import cc.kevinlu.snow.server.data.mapper.DigitMapper;
import cc.kevinlu.snow.server.data.mapper.SnowflakeMapper;
import cc.kevinlu.snow.server.data.mapper.UuidMapper;
import cc.kevinlu.snow.server.data.model.*;
import cc.kevinlu.snow.server.pojo.enums.StatusEnums;

/**
 * @author chuan
 */
@Component
@Async
public class AsyncTaskProcessor {

    @Autowired
    private UuidMapper             uuidMapper;
    @Autowired
    private DigitMapper            digitMapper;
    @Autowired
    private SnowflakeMapper        snowflakeMapper;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    public void uuidStatus(List records) {
        taskExecutor.execute(() -> {
            UuidDO uuidDO = new UuidDO();
            uuidDO.setStatus(StatusEnums.USED.getStatus());

            UuidDOExample example = new UuidDOExample();
            example.createCriteria().andIdIn(records);
            uuidMapper.updateByExampleSelective(uuidDO, example);
        });
    }

    public void snowflakeStatus(List records) {
        taskExecutor.execute(() -> {
            SnowflakeDO snowflakeDO = new SnowflakeDO();
            snowflakeDO.setStatus(StatusEnums.USED.getStatus());

            SnowflakeDOExample example = new SnowflakeDOExample();
            example.createCriteria().andIdIn(records);
            snowflakeMapper.updateByExampleSelective(snowflakeDO, example);
        });
    }

    public void digitStatus(Long id) {
        taskExecutor.execute(() -> {
            DigitDO digitDO = new DigitDO();
            digitDO.setStatus(StatusEnums.USED.getStatus());
            digitDO.setId(id);

            digitMapper.updateByPrimaryKeySelective(digitDO);
        });
    }

}
