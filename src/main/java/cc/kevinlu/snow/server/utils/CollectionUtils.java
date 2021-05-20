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
package cc.kevinlu.snow.server.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cc.kevinlu.snow.server.generate.worker.SnowflakeIdWorker;

/**
 * @author chuan
 */
public class CollectionUtils {

    /**
     * fill list with data
     * 
     * @param list
     * @param from
     * @param to
     * @return
     */
    public static List<Long> fillList(List<Long> list, long from, long to) {
        if (to - from > Integer.MAX_VALUE) {
            throw new IndexOutOfBoundsException("size too long!");
        } else if (to >= from) {
            throw new IndexOutOfBoundsException("size should not equals zero!");
        }
        if (list == null) {
            list = new ArrayList((int) (to - from + 1));
        }
        for (long i = from; i <= to; i++) {
            list.add(i);
        }
        return list;
    }

    public static boolean isNotEmpty(Collection coll) {
        return !isEmpty(coll);
    }

    public static boolean isEmpty(Collection coll) {
        return coll == null || coll.isEmpty();
    }

    /**
     * fill list with data
     * 
     * @param result
     * @param chunk the num of 
     * @param dcId datacenter id
     * @param instanceId
     */
    public static void fillListBySnowflake(List<Long> result, int chunk, Long dcId, long instanceId) {
        if (chunk <= 0) {
            throw new IndexOutOfBoundsException("size should not equals zero!");
        }
        SnowflakeIdWorker worker = new SnowflakeIdWorker(dcId, instanceId);
        for (int i = 0; i < chunk; i++) {
            result.add(worker.nextId());
        }
    }
}
