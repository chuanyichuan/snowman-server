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
package cc.kevinlu.snow.server.service;

import cc.kevinlu.snow.client.instance.pojo.ServiceInfo;
import cc.kevinlu.snow.client.instance.pojo.ServiceInstance;
import cc.kevinlu.snow.client.instance.pojo.ServiceQuery;

/**
 * 
 * @author chuan
 */
public interface ServiceInstanceService {

    /**
     * logic: <br />
     * <p>1. get the field of group code for lock</p>
     * <p>2. verify the group code, if it not in ConcurrentHashMapA, to 3, else to 4</p>
     * <p>3. insert the group code into the database, and put it into ConcurrentHashMapA for cache</p>
     * <p>4. verify the instance code, if it not in ConcurrentHashMapB, to 5, else to 6</p>
     * <p>5. insert the instance code into the database, and put it into ConcurrentHashMapB for cache</p>
     * 
     * @param instance
     * @return
     */
    boolean registerService(ServiceInstance instance);

    /**
     * get all instance information from the database
     * 
     * @param params
     * @return
     */
    ServiceInfo services(ServiceQuery params);
}
