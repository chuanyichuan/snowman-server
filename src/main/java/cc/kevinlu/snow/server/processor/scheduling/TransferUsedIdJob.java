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
package cc.kevinlu.snow.server.processor.scheduling;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import cc.kevinlu.snow.server.config.Constants;
import cc.kevinlu.snow.server.data.mapper.TransferMapper;
import cc.kevinlu.snow.server.processor.redis.RedisProcessor;
import cc.kevinlu.snow.server.utils.helper.ThreadPoolHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chuan
 */
@Slf4j
@Configuration
public class TransferUsedIdJob {

    @Autowired
    private RedisProcessor                    redisProcessor;
    @Autowired
    private RestTemplate                      restTemplate;
    @Autowired
    private TransferMapper                    transferMapper;

    private static final ExecutorService      SINGLE_POOL      = ThreadPoolHelper.newFixedThreadPool(1,
            "sownman-transfer-");

    private static final ThreadLocal<Integer> PING_TIMES_LOCAL = new ThreadLocal<>();

    /**
     * retry 100 times, then clear the cache
     */
    public static final int                   INTERVAL_TIME    = 100;

    private static final String               HOST_ADDRESS;
    @Value("${server.port:8080}")
    private static int                        port             = 8080;
    @Value("#{'${snowman.transfer.table}'.split(',')}")
    private String[]                          tableList;

    public static final String                IP_PORT_SPLIT    = ":";

    public static final String                PING_URI         = "/cluster/ping";
    public static final String                PONG             = "PONG";
    public static final String                PING_URL_PATTERN = "http://%s%s/%s";

    static {
        String temp;
        try {
            temp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            temp = "127.0.0.1";
        }
        HOST_ADDRESS = temp + IP_PORT_SPLIT + port;
    }

    /**
     * one clock
     */
    @Scheduled(cron = "0 0/1 * * * ?", zone = "GMT+8:00")
    public void startTransfer() {
        // verify this client is active
        if (!verifyActive()) {
            log.info("skip scheduled job!");
            return;
        }
        log.info("Start scanning the used data...");
        //todo sync
        SINGLE_POOL.execute(new TransferUsedIdTask(100, tableList, transferMapper));
    }

    /**
     * verify the client is active
     * 
     * @return
     */
    private boolean verifyActive() {
        // Try to lock down the leadership identity at first
        boolean lock = redisProcessor.tryLockWithSet(Constants.SCHEDULED_CLIENT_KEY, HOST_ADDRESS, 3, TimeUnit.DAYS);
        if (!lock) {
            // Verify that it has acquired the lock
            String leaderAddress = (String) redisProcessor.get(Constants.SCHEDULED_CLIENT_KEY);
            if (HOST_ADDRESS.equals(leaderAddress)) {
                return true;
            }
            int pingTimes = PING_TIMES_LOCAL.get() == null ? 1 : PING_TIMES_LOCAL.get();
            if (pingTimes > INTERVAL_TIME) {
                pingTimes = 1;
                PING_TIMES_LOCAL.remove();
            }
            if (pingTimes > 3) {
                PING_TIMES_LOCAL.set(pingTimes + 1);
                return false;
            }
            PING_TIMES_LOCAL.set(pingTimes + 1);
            // verify ping-pong

            if (!verifyAddress(leaderAddress) || !ping(leaderAddress)) {
                // remove the lock if the leader is wraith node.
                // Tip: a b c, d is wraith node,then a remove the lock, b get the lock, and c remove it.
                // Allow temporary dirty data
                redisProcessor.del(Constants.SCHEDULED_CLIENT_KEY);
                return verifyActive();
            }
        }
        return true;
    }

    /**
     * ping-pong switching
     * 
     * @param leaderAddress
     * @return
     */
    private boolean ping(String leaderAddress) {
        String url = String.format(PING_URL_PATTERN, leaderAddress, PING_URI, HOST_ADDRESS);
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(new URI(url), String.class);
            if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                return PONG.equals(responseEntity.getBody());
            }
        } catch (Exception e) {
            log.warn("Missed connection to master node!");
        }
        return false;
    }

    /**
     * Verify the IP and the port of the leader are legal.
     * 
     * @param leaderAddress
     * @return
     */
    private boolean verifyAddress(String leaderAddress) {
        if (StringUtils.isBlank(leaderAddress)) {
            return false;
        }
        String[] ipPort = leaderAddress.split(IP_PORT_SPLIT);
        if (ipPort.length != 2) {
            return false;
        }
        try {
            String ip = ipPort[0];
            int port = Integer.parseInt(ipPort[1]);
            return ip.split("\\.").length == 4 && port > 0 && port < 65535;
        } catch (Exception e) {
            return false;
        }
    }

}
