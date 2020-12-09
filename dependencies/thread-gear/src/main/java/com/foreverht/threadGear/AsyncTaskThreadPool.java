package com.foreverht.threadGear;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class AsyncTaskThreadPool extends ThreadPoolExecutor {
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2 + 1;

    private static volatile AsyncTaskThreadPool sInstance;

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<>(128);

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };


    private AsyncTaskThreadPool() {
        super(CORE_POOL_SIZE, CORE_POOL_SIZE,
                1000, TimeUnit.MILLISECONDS, //terminated idle thread
                sPoolWorkQueue, sThreadFactory, new ThreadPoolExecutor.DiscardPolicy());
    }


    public static AsyncTaskThreadPool getInstance() {
        if (null == sInstance) {
            synchronized (AsyncTaskThreadPool.class) {
                if (null == sInstance) {
                    sInstance = new AsyncTaskThreadPool();
                }
            }
        }
        return sInstance;
    }


}
