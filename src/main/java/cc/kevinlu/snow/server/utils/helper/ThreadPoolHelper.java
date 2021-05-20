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
package cc.kevinlu.snow.server.utils.helper;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * thread pool utils
 * 
 * @author chuan
 */
@Slf4j
public class ThreadPoolHelper extends ThreadPoolExecutor {

    private static final String             SHUTDOWN_FORMAT      = "%s Going to shutdown. Executed tasks: %d, Running tasks: %d, Pending tasks: %d";

    private static final String             SHUTDOWN_NOW_FORMAT  = "%s Going to immediately shutdown. Executed tasks: %d, Running tasks: %d, Pending tasks: %d ";

    private static final String             AFTER_EXECUTE_FORMAT = "%s-pool-monitor: Duration: %d ms, PoolSize: %d, CorePoolSize: "
            + "%d, Active: %d, Completed: %d, Task: %d, Queue: %d, LargestPoolSize: %d, MaximumPoolSize: %d,  KeepAliveTime: %d, isShutdown: %s, isTerminated: %s";

    /**
     * 保存任务开始执行的时间，当任务结束时，用任务结束时间减去开始时间计算任务执行时间
     */
    private ConcurrentHashMap<String, Long> startTime            = new ConcurrentHashMap<>();

    /**
     * 线程池名称，一般以业务名称命名，方便区分
     */
    private String                          poolNamePrefix       = "snowman-thread-";

    /**
     * 调用父类的构造方法，并初始化HashMap和线程池名称
     *
     * @param corePoolSize
     *            线程池核心线程数
     * @param maximumPoolSize
     *            线程池最大线程数
     * @param keepAliveTime
     *            线程的最大空闲时间
     * @param unit
     *            空闲时间的单位
     * @param workQueue
     *            保存被提交任务的队列
     * @param poolNamePrefix
     *            线程池名称
     */
    public ThreadPoolHelper(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                            BlockingQueue<Runnable> workQueue, String poolNamePrefix) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new SnowmanThreadFactory(poolNamePrefix));
        this.poolNamePrefix = poolNamePrefix;
    }

    /**
     * 创建ThreadPoolTaskExecutor实例
     * 
     * @param prop 
     *            TaskExecutionProperties注入
     * @return ThreadPoolTaskExecutor对象
     */
    public static ThreadPoolTaskExecutor newTaskThreadPool(TaskExecutionProperties prop) {
        return newTaskThreadPool(prop.getPool().getCoreSize(), prop.getPool().getMaxSize(),
                prop.getPool().getQueueCapacity(), prop.getThreadNamePrefix());
    }

    /**
     * 创建ThreadPoolTaskExecutor实例
     * 
     * @param corePoolSize
     *            线程池核心线程数
     * @param maximumPoolSize
     *            线程池最大线程数
     * @param queueCapacity
     *            等待队列大小
     * @param prefixName
     *            线程池名称
     * @return ThreadPoolTaskExecutor对象
     */
    public static ThreadPoolTaskExecutor newTaskThreadPool(int corePoolSize, int maximumPoolSize, int queueCapacity,
                                                           String prefixName) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maximumPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(prefixName);
        executor.initialize();
        return executor;
    }

    /**
     * 创建固定线程池，代码源于Executors.newFixedThreadPool方法，这里增加了poolName
     *
     * @param nThreads
     *            线程数量
     * @param poolName
     *            线程池名称
     * @return ExecutorService对象
     */
    public static ExecutorService newFixedThreadPool(int nThreads, String poolName) {
        return new ThreadPoolHelper(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                poolName);
    }

    /**
     * 创建缓存型线程池，代码源于Executors.newCachedThreadPool方法，这里增加了poolName
     *
     * @param poolName
     *            线程池名称
     * @return ExecutorService对象
     */
    public static ExecutorService newCachedThreadPool(String poolName) {
        return new ThreadPoolHelper(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), poolName);
    }

    @Override
    public void shutdown() {
        if (!this.isShutdown()) {
            // 统计已执行任务、正在执行任务、未执行任务数量
            log.info(String.format(SHUTDOWN_FORMAT, this.poolNamePrefix, this.getCompletedTaskCount(),
                    this.getActiveCount(), this.getQueue().size()));
            super.shutdown();
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        if (this.isShutdown()) {
            throw new IllegalThreadStateException(this.poolNamePrefix + " is already down!");
        }
        return super.shutdownNow();
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        startTime.putIfAbsent(String.valueOf(r.hashCode()), System.currentTimeMillis());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        Long start = startTime.remove(String.valueOf(r.hashCode()));
        // 统计任务耗时、初始线程数、核心线程数、正在执行的任务数量、已完成任务数量、任务总数、队列里缓存的任务数量、池中存在的最大线程数、最大允许的线程数、线程空闲时间、线程池是否关闭、线程池是否终止
        log.info(String.format(AFTER_EXECUTE_FORMAT, this.poolNamePrefix, System.currentTimeMillis() - start,
                this.getPoolSize(), this.getCorePoolSize(), this.getActiveCount(), this.getCompletedTaskCount(),
                this.getTaskCount(), this.getQueue().size(), this.getLargestPoolSize(), this.getMaximumPoolSize(),
                this.getKeepAliveTime(TimeUnit.MILLISECONDS), this.isShutdown(), this.isTerminated()));
    }

    /**
     * 生成线程池所用的线程，只是改写了线程池默认的线程工厂，传入线程池名称，便于问题追踪
     */
    private static class SnowmanThreadFactory implements ThreadFactory {

        /**
         * pool counter
         */
        private static final AtomicLong POOL_NUMBER  = new AtomicLong(0);

        /**
         * thread counter
         */
        private final AtomicLong        threadNumber = new AtomicLong(0);

        private final ThreadGroup       group;
        private final String            poolName;

        /**
         * 初始化线程工厂
         *
         * @param poolName
         *            线程池名称
         */
        public SnowmanThreadFactory(String poolName) {
            SecurityManager s = System.getSecurityManager();
            this.group = s == null ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
            this.poolName = poolName;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, this.poolName + threadNumber.getAndIncrement(), 0);
            t.setDaemon(false);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

}
