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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import cc.kevinlu.snow.server.data.mapper.DistributionLockMapper;
import cc.kevinlu.snow.server.data.mapper.SnowmanLockMapper;
import cc.kevinlu.snow.server.data.model.DistributionLockDO;
import cc.kevinlu.snow.server.data.model.DistributionLockDOExample;
import lombok.extern.slf4j.Slf4j;

/**
 * distribution lock util
 *
 * @author chuan
 */
@Slf4j
@Component
@ConditionalOnClass
public class DistributionLockFactory {

    @Autowired
    private SnowmanLockMapper      snowmanLockMapper;
    @Autowired
    private DistributionLockMapper distributionLockMapper;

    private static final String    HOST_NAME;

    static {
        String temp;
        try {
            temp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            temp = "127.0.0.1";
        }
        HOST_NAME = temp;
    }

    /**
     * 获取锁,如果锁被占用则循环获取锁,直到获取到锁或者超时;
     *
     * @param key 锁键
     * @param model 模块名称
     * @param expireTime 锁过期时间(毫秒)
     * @param waitTime 最大等待时间(毫秒)，超时后则获取锁失败
     * @return
     */
    public boolean tryLock(String key, String model, long expireTime, long waitTime) {
        boolean flag = false;
        long fastFailTime = System.currentTimeMillis() + waitTime;
        do {
            flag = lock(key, model, expireTime);
        } while (!flag && (System.currentTimeMillis() <= fastFailTime));
        log.info("current_time [{}]", (System.currentTimeMillis() - fastFailTime));
        return flag;
    }

    /**
     * 获取锁，如果锁被占用则返回获取失败
     *
     * @param key 锁键
     * @param model 模块名称
     * @param expireTime 锁过期时间(毫秒)
     * @return
     */
    public boolean lock(String key, String model, long expireTime) {
        String value = value(key, model);

        int result = 0;
        try {
            // try insert lock
            DistributionLockDOExample example = new DistributionLockDOExample();
            example.createCriteria().andLockKeyEqualTo(key);
            List<DistributionLockDO> datas = distributionLockMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(datas)) {
                result = snowmanLockMapper.lock(key, value, expireTime, owner());
            } else {
                DistributionLockDO data = datas.get(0);
                if (1 == data.getIsLocked()) {
                    return false;
                }
                result = snowmanLockMapper.reLock(key, value, expireTime, owner());
            }
        } catch (DuplicateKeyException e) {
            // try update lock
            try {
                result = snowmanLockMapper.reLock(key, value, expireTime, owner());
            } catch (Exception exception) {
                // lock fail
                return false;
            }
        } catch (Exception e) {
            log.error("error", e);
            return false;
        }
        return result > 0;
    }

    /**
     * 释放锁
     * @param key 锁键
     * @param model 模块名称
     * @return
     */
    public boolean release(String key, String model) {
        String value = value(key, model);
        int result = snowmanLockMapper.releaseLock(key, value);
        return result > 0;
    }

    private String value(String key, String model) {
        return key + "_" + model + "_" + owner();
    }

    private String owner() {
        return HOST_NAME;
    }

}
