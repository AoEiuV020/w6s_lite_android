package com.foreverht.threadGear;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * 针对按会话拉取消息的场景使用的线程池(假如会话接口获取100个会话后, 会调用100次离线消息接口(每个会话))
 * */
public class OfflineMessagesSessionStrategyThreadPool extends ThreadPoolExecutor {
    private static final int CORE_POOL_SIZE = 1;

    private static volatile OfflineMessagesSessionStrategyThreadPool sInstance;

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<>(128);

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };


    private OfflineMessagesSessionStrategyThreadPool() {
        super(CORE_POOL_SIZE, CORE_POOL_SIZE,
                1000, TimeUnit.MILLISECONDS, //terminated idle thread
                sPoolWorkQueue, sThreadFactory, new DiscardPolicy());
    }


    public static OfflineMessagesSessionStrategyThreadPool getInstance() {
        if (null == sInstance) {
            synchronized (OfflineMessagesSessionStrategyThreadPool.class) {
                if (null == sInstance) {
                    sInstance = new OfflineMessagesSessionStrategyThreadPool();
                }
            }
        }
        return sInstance;
    }


}
