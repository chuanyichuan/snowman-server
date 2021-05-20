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
package cc.kevinlu.snow.server.generate.alogrithm;

import java.util.List;

import cc.kevinlu.snow.server.generate.AbstractAlgorithm;
import cc.kevinlu.snow.server.generate.worker.SnowflakeIdWorker;
import cc.kevinlu.snow.server.processor.AlgorithmProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chuan
 */
@Slf4j
public class SnowflakeAlgorithm extends AbstractAlgorithm<Long> {

    public SnowflakeAlgorithm(AlgorithmProcessor algorithmProcessor) {
        super(algorithmProcessor);
    }

    @Override
    protected void generateDistributedId(List<Long> idList, long groupId, long instanceId, long fromValue, int chunk) {
        if (chunk <= 0) {
            throw new IndexOutOfBoundsException("size should not equals zero!");
        }
        SnowflakeIdWorker worker = new SnowflakeIdWorker(groupId, instanceId);
        for (int i = 0; i < chunk; i++) {
            idList.add(worker.nextId());
        }
    }

}
