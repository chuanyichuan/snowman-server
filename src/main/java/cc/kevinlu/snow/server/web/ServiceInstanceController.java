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
package cc.kevinlu.snow.server.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import cc.kevinlu.snow.client.exceptions.ParamIllegalException;
import cc.kevinlu.snow.client.instance.ServiceInstanceClient;
import cc.kevinlu.snow.client.instance.pojo.ServiceInfo;
import cc.kevinlu.snow.client.instance.pojo.ServiceInstance;
import cc.kevinlu.snow.client.instance.pojo.ServiceQuery;
import cc.kevinlu.snow.server.service.ServiceInstanceService;

/**
 * @author chuan
 */
@RestController
public class ServiceInstanceController implements ServiceInstanceClient {

    @Autowired
    private ServiceInstanceService serviceInstanceService;

    @Override
    public boolean registerService(ServiceInstance instance) {
        // 校验参数
        if (StringUtils.isAnyBlank(instance.getName(), instance.getGroupCode(), instance.getInstanceCode())) {
            throw new ParamIllegalException("name,groupCode,instanceCode all need!");
        }
        return serviceInstanceService.registerService(instance);
    }

    @Override
    public ServiceInfo services(ServiceQuery params) {
        return serviceInstanceService.services(params);
    }
}
