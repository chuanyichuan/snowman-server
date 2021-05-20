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
package cc.kevinlu.snow.server.generate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cc.kevinlu.snow.client.enums.IdAlgorithmEnums;
import cc.kevinlu.snow.server.generate.alogrithm.DigitAlgorithm;
import cc.kevinlu.snow.server.generate.alogrithm.SnowflakeAlgorithm;
import cc.kevinlu.snow.server.generate.alogrithm.UuidAlgorithm;
import cc.kevinlu.snow.server.processor.AlgorithmProcessor;
import cc.kevinlu.snow.server.processor.redis.RedisProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chuan
 */
@Slf4j
@Component
public class GenerateAlgorithmFactory {

    @Autowired
    private AlgorithmProcessor          algorithmProcessor;
    @Autowired
    private RedisProcessor              redisProcessor;

    private volatile DigitAlgorithm     digitAlgorithm;
    private volatile SnowflakeAlgorithm snowflakeAlgorithm;
    private volatile UuidAlgorithm      uuidAlgorithm;
    private Object[]                    lockObjs = new Object[] { new Object(), new Object(), new Object() };

    public AbstractAlgorithm factory(Integer mode) {
        IdAlgorithmEnums algorithm = IdAlgorithmEnums.getEnumByAlgorithm(mode);
        switch (algorithm) {
            case SNOWFLAKE:
                if (snowflakeAlgorithm == null) {
                    synchronized (lockObjs[0]) {
                        if (snowflakeAlgorithm == null) {
                            snowflakeAlgorithm = new SnowflakeAlgorithm(algorithmProcessor, redisProcessor);
                        }
                    }
                }
                return snowflakeAlgorithm;
            case UUID:
                if (uuidAlgorithm == null) {
                    synchronized (lockObjs[1]) {
                        if (uuidAlgorithm == null) {
                            uuidAlgorithm = new UuidAlgorithm(algorithmProcessor);
                        }
                    }
                }
                return uuidAlgorithm;
            default:
                if (digitAlgorithm == null) {
                    synchronized (lockObjs[2]) {
                        if (digitAlgorithm == null) {
                            digitAlgorithm = new DigitAlgorithm(algorithmProcessor);
                        }
                    }
                }
                return digitAlgorithm;
        }
    }

}
