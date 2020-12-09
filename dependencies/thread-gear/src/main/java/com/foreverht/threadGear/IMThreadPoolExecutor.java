package com.foreverht.threadGear;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dasunsy on 2018/3/11.
 */

public class IMThreadPoolExecutor extends ThreadPoolExecutor {

    //参照 AsyncTask
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2 + 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "im #" + mCount.getAndIncrement());
            thread.setPriority(Thread.MAX_PRIORITY);
            return thread;
        }
    };


    private static volatile IMThreadPoolExecutor sInstance;

    public static IMThreadPoolExecutor getInstance() {
        if (null == sInstance) {
            synchronized (IMThreadPoolExecutor.class) {
                if (null == sInstance) {
                    sInstance = new IMThreadPoolExecutor();
                }
            }
        }
        return sInstance;
    }

    private IMThreadPoolExecutor() {
        super(CORE_POOL_SIZE, Integer.MAX_VALUE,
                0L, TimeUnit.MILLISECONDS, //terminated idle thread
                new LinkedBlockingQueue(), sThreadFactory);
    }

}
