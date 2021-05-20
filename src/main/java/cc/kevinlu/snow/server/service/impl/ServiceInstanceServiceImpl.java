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
package cc.kevinlu.snow.server.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.nacos.common.utils.CollectionUtils;

import cc.kevinlu.snow.client.enums.ServiceStatusEnums;
import cc.kevinlu.snow.client.instance.pojo.ServiceInfo;
import cc.kevinlu.snow.client.instance.pojo.ServiceInstance;
import cc.kevinlu.snow.client.instance.pojo.ServiceQuery;
import cc.kevinlu.snow.server.data.mapper.DigitMapper;
import cc.kevinlu.snow.server.data.mapper.GroupMapper;
import cc.kevinlu.snow.server.data.mapper.ServiceInstanceMapper;
import cc.kevinlu.snow.server.data.model.*;
import cc.kevinlu.snow.server.processor.InstanceCacheProcessor;
import cc.kevinlu.snow.server.service.ServiceInstanceService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chuan 
 */
@Slf4j
@Service
public class ServiceInstanceServiceImpl implements ServiceInstanceService {

    @Autowired
    private GroupMapper            groupMapper;
    @Autowired
    private DigitMapper            digitMapper;
    @Autowired
    private ServiceInstanceMapper  serviceInstanceMapper;
    @Autowired
    private InstanceCacheProcessor instanceCacheProcessor;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean registerService(ServiceInstance instance) {
        String groupCode = instance.getGroupCode();
        String instanceCode = instance.getInstanceCode();
        Long groupId = null, serviceInstanceId = null;
        boolean gflag = false, iflag = false;
        Date date = new Date();
        try {
            if ((groupId = instanceCacheProcessor.getGroupId(groupCode)) == null) {
                // insert group into database
                GroupDO groupDO = new GroupDO();
                groupDO.setName(instance.getName());
                groupDO.setGroupCode(groupCode);
                groupDO.setChunk(instance.getChunk());
                groupDO.setMode(instance.getMode().getAlgorithm());
                groupDO.setLastValue("0");
                groupDO.setGmtCreated(date);
                groupDO.setGmtUpdated(date);
                groupMapper.insertSelective(groupDO);
                // set cache
                groupId = groupDO.getId();
                instanceCacheProcessor.putGroupCode(groupCode, groupId);
                gflag = true;
            } else {
                GroupDO groupDO = groupMapper.selectByPrimaryKey(groupId);
                groupDO.setChunk(instance.getChunk());
                groupDO.setGmtUpdated(date);
                groupMapper.updateByPrimaryKeySelective(groupDO);
            }
            if ((serviceInstanceId = instanceCacheProcessor.getInstanceId(groupId, instanceCode)) == null) {
                // insert instance into database
                ServiceInstanceDO instanceDO = new ServiceInstanceDO();
                instanceDO.setGroupId(groupId);
                instanceDO.setServerCode(instance.getInstanceCode());
                instanceDO.setSnowTimes(0);
                instanceDO.setStatus(ServiceStatusEnums.ONLINE.getStatus());
                instanceDO.setGmtCreated(date);
                instanceDO.setGmtUpdated(date);
                serviceInstanceMapper.insertSelective(instanceDO);
                // set cache
                serviceInstanceId = instanceDO.getId();
                instanceCacheProcessor.putInstanceCode(groupId, instanceCode, serviceInstanceId);
                iflag = true;
            }
        } catch (Exception e) {
            if (gflag) {
                instanceCacheProcessor.removeGroup(groupCode);
            }
            if (iflag) {
                instanceCacheProcessor.removeInstance(groupId, instanceCode);
            }
            return false;
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceInfo services(ServiceQuery params) {
        GroupDOExample groupExample = new GroupDOExample();
        GroupDOExample.Criteria criteria = groupExample.createCriteria();
        if (StringUtils.isNotBlank(params.getName())) {
            criteria.andNameEqualTo(params.getName());
        }
        if (StringUtils.isNotBlank(params.getGroupCode())) {
            criteria.andGroupCodeEqualTo(params.getGroupCode());
        }
        List<GroupDO> groupList = groupMapper.selectByExample(groupExample);
        if (CollectionUtils.isEmpty(groupList)) {
            return null;
        }
        GroupDO group = groupList.get(0);
        ServiceInstanceDOExample instanceExample = new ServiceInstanceDOExample();
        instanceExample.createCriteria().andGroupIdEqualTo(group.getId());
        List<ServiceInstanceDO> instanceList = serviceInstanceMapper.selectByExample(instanceExample);
        // result
        List<ServiceInfo.InstanceInfo> instanceInfoList = new ArrayList<>(instanceList.size());
        ServiceInfo result = new ServiceInfo();
        result.setName(group.getName());
        result.setGroupCode(group.getGroupCode());
        result.setInstances(instanceInfoList);

        if (CollectionUtils.isNotEmpty(instanceList)) {
            DigitDOExample snowflakeExample = new DigitDOExample();
            instanceList.stream().forEach(item -> {
                // query last snowflake
                snowflakeExample.clear();
                snowflakeExample.createCriteria().andServiceInstanceIdEqualTo(item.getId());
                snowflakeExample.setOrderByClause("id desc limit 1");
                List<DigitDO> snowflakeList = digitMapper.selectByExample(snowflakeExample);
                ServiceInfo.InstanceInfo instanceInfo = ServiceInfo.InstanceInfo.builder()
                        .serverCode(item.getServerCode()).snowTimes(item.getSnowTimes()).build();
                if (CollectionUtils.isNotEmpty(snowflakeList)) {
                    DigitDO snowflake = snowflakeList.get(0);
                    instanceInfo.setLastFromValue(snowflake.getFromValue());
                    instanceInfo.setLastToValue(snowflake.getToValue());
                }
                instanceInfoList.add(instanceInfo);
            });
        }
        return result;
    }
}
