/*
 * Copyright 2015 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreverht.threadGear;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class DbThreadPoolExecutor extends ThreadPoolExecutor {
    //参照 AsyncTask
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2 + 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "db #" + mCount.getAndIncrement());
        }
    };


    private static volatile DbThreadPoolExecutor sInstance;

    public static DbThreadPoolExecutor getInstance() {
           if (null == sInstance) {
               synchronized (DbThreadPoolExecutor.class) {
                   if (null == sInstance) {
                       sInstance = new DbThreadPoolExecutor();
                   }
               }
           }
        return sInstance;
    }

    private DbThreadPoolExecutor() {
        super(CORE_POOL_SIZE, CORE_POOL_SIZE,
                0L, TimeUnit.MILLISECONDS, //terminated idle thread
                new LinkedBlockingQueue(), sThreadFactory);
    }



}
