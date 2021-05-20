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

import org.apache.commons.lang3.StringUtils;

import cc.kevinlu.snow.server.config.Constants;
import cc.kevinlu.snow.server.listener.pojo.PreGenerateBO;
import cc.kevinlu.snow.server.processor.redis.RedisProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chuan
 */
@Slf4j
public class RedisMessageSender implements Runnable {

    private final static ThreadLocal<PreGenerateBO> THREAD_LOCAL = new ThreadLocal<>();
    private PreGenerateBO                           preGenerateBO;

    private RedisProcessor                          redisProcessor;

    public RedisMessageSender(RedisProcessor redisProcessor, PreGenerateBO preGenerateBO) {
        this.redisProcessor = redisProcessor;
        THREAD_LOCAL.set(preGenerateBO);
        this.preGenerateBO = preGenerateBO;
    }

    /**
     * the max times for regenerate message
     */
    private static final int REGENERATE_TIMES_LIMIT = 3;

    @Override
    public void run() {
        //        PreGenerateBO preGenerateBO = THREAD_LOCAL.get();
        if (StringUtils.isAnyBlank(preGenerateBO.getGroup(), preGenerateBO.getInstance())) {
            log.warn("Reject Illegal Call!");
            return;
        }
        if (preGenerateBO.getTimes() > REGENERATE_TIMES_LIMIT) {
            log.warn("Times overflow!");
            return;
        }
        redisProcessor.sendMessage(Constants.CHECK_CHUNK_TOPIC, preGenerateBO);
        //        THREAD_LOCAL.remove();
    }
}
