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


public class ImageThreadPoolExecutor extends ThreadPoolExecutor {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = CPU_COUNT * 2 + 1;

    public static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    private static volatile ImageThreadPoolExecutor sInstance;

    public static ImageThreadPoolExecutor getInstance() {
           if (null == sInstance) {
               synchronized (ImageThreadPoolExecutor.class) {
                   if (null == sInstance) {
                       sInstance = new ImageThreadPoolExecutor();
                   }
               }
           }
        return sInstance;
    }


    private ImageThreadPoolExecutor() {
        super(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                0L, TimeUnit.MILLISECONDS, //terminated idle thread
                new LinkedBlockingQueue(), new DefaultThreadFactory(3, "uil-pool-"));
    }


    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            this.group = Thread.currentThread().getThreadGroup();
            this.namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
            if(t.isDaemon()) {
                t.setDaemon(false);
            }

            t.setPriority(this.threadPriority);
            return t;
        }
    }

}
