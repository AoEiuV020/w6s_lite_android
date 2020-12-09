package com.foreverht.db.service;

import com.foreverht.threadGear.DbThreadPoolExecutor;

/**
 * Created by dasunsy on 16/2/21.
 */
public abstract class BaseDbService {
    public DbThreadPoolExecutor mDbExecutor = DbThreadPoolExecutor.getInstance();
}
