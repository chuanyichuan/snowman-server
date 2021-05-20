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

import java.util.ArrayList;
import java.util.List;

import cc.kevinlu.snow.client.enums.IdAlgorithmEnums;
import cc.kevinlu.snow.client.exceptions.ParamIllegalException;
import cc.kevinlu.snow.client.exceptions.ValueTooBigException;
import cc.kevinlu.snow.server.pojo.PersistentBO;
import cc.kevinlu.snow.server.processor.AlgorithmProcessor;
import cc.kevinlu.snow.server.processor.pojo.AsyncCacheBO;
import cc.kevinlu.snow.server.processor.task.pojo.RegenerateBO;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>abs class for generate.</p>
 * <p>1. get thr last value of the group</p>
 * <p>2. compute from value and</p>
 * <p>3. get instance id with groupId and instanceCode</p>
 * <p>4. generate</p>
 *
 * @author chuan
 * @see
 */
@Slf4j
public abstract class AbstractAlgorithm<T> {

    protected AlgorithmProcessor algorithmProcessor;

    public AbstractAlgorithm(AlgorithmProcessor algorithmProcessor) {
        this.algorithmProcessor = algorithmProcessor;
    }

    /**
     * get from value
     *
     * @param regenerate
     * @return
     */
    private Long fromValue(RegenerateBO regenerate) {
        if (IdAlgorithmEnums.DIGIT.getAlgorithm() == regenerate.getMode()) {
            // query newest value from cache or db
            long lastValue = Long.parseLong(algorithmProcessor.queryGroupDigitLastValue(regenerate.getGroupId()));
            if (lastValue >= Long.MAX_VALUE - regenerate.getChunk()) {
                throw new ValueTooBigException("ID has been used up!");
            }
            return lastValue + 1L;
        }
        return 0L;
    }

    /**
     * get instance id
     *
     * @param groupId
     * @param instanceCode
     * @return
     */
    private Long instanceId(Long groupId, String instanceCode) {
        Long instanceId = algorithmProcessor.instanceId(groupId, instanceCode);
        if (null == instanceId) {
            throw new ParamIllegalException("instance not exists!");
        }
        return instanceId;
    }

    /**
     * client can get records by call it
     *
     * @param regenerate
     * @return
     */
    public List<T> generate(RegenerateBO regenerate) {
        long fromValue = fromValue(regenerate);
        long instanceId = instanceId(regenerate.getGroupId(), regenerate.getInstance());
        int chunk = regenerate.getChunk();
        List<T> idList = new ArrayList<>(chunk);
        generateDistributedId(idList, regenerate.getGroupId(), instanceId, fromValue, chunk);

        PersistentBO<T> persistentBO = new PersistentBO<>();
        persistentBO.setInstanceId(instanceId);
        persistentBO.setIdList(idList);
        persistentBO.setUsed(regenerate.getTimes() == 0);
        persistentBO.setMode(regenerate.getMode());
        persistentDB(persistentBO);

        recordSnowTimes(instanceId);
        recordLastValue(regenerate.getGroupId(), idList.get(idList.size() - 1));
        return idList;
    }

    /**
     * regenerate id
     * 
     * @param regenerate
     * @return
     */
    public List<T> regenerate(RegenerateBO regenerate) {
        List<T> idList = generate(regenerate);
        asyncToCache(regenerate);
        return idList;
    }

    /**
     * Implementation
     *
     * @param idList
     * @param groupId
     * @param instanceId
     * @param fromValue
     * @param chunk
     */
    protected abstract void generateDistributedId(List<T> idList, long groupId, long instanceId, long fromValue,
                                                  int chunk);

    /**
     * persistent data
     *
     * @param persistent
     */
    protected void persistentDB(PersistentBO<T> persistent) {
        algorithmProcessor.persistentToDb(persistent);
    }

    /**
     * record last value
     * 
     * @param id
     * @param value
     */
    protected void recordLastValue(Long id, T value) {
        algorithmProcessor.recordGroupLastValue(id, String.valueOf(value));
    }

    /**
     * record times
     *
     * @param instanceId
     */
    private void recordSnowTimes(long instanceId) {
        algorithmProcessor.recordSnowTimes(instanceId);
    }

    /**
     * async id to cache
     * 
     * @param regenerate
     */
    public void asyncToCache(RegenerateBO regenerate) {
        AsyncCacheBO asyncCacheBO = new AsyncCacheBO();
        asyncCacheBO.setGroupId(regenerate.getGroupId());
        asyncCacheBO.setInstanceId(instanceId(regenerate.getGroupId(), regenerate.getInstance()));
        asyncCacheBO.setMode(regenerate.getMode());
        algorithmProcessor.asyncDataToCache(asyncCacheBO);
    }

}
