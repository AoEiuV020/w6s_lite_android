package com.foreverht.threadGear;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HighPriorityCachedTreadPoolExecutor extends ThreadPoolExecutor {

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "cache #" + mCount.getAndIncrement());
            thread.setPriority(Thread.MAX_PRIORITY);
            return thread;
        }
    };


    private static volatile HighPriorityCachedTreadPoolExecutor sInstance;

    public static HighPriorityCachedTreadPoolExecutor getInstance() {
        if (null == sInstance) {
            synchronized (HighPriorityCachedTreadPoolExecutor.class) {
                if (null == sInstance) {
                    sInstance = new HighPriorityCachedTreadPoolExecutor();
                }
            }
        }
        return sInstance;
    }

    private HighPriorityCachedTreadPoolExecutor() {
        super(0, Integer.MAX_VALUE,
                10 * 1000, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                sThreadFactory);
    }

}
