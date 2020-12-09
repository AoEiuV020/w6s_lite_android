package com.foreveross.atwork.infrastructure.support;

import android.content.Context;

/**
 * Created by dasunsy on 2018/3/21.
 */

public class BehaviorLogConfig {
    /**
     * 为了避免因为各种意外(闪退, 断电, kill 进程)而导致没记录, 这里使用定时器来循环记下 "end" 时间
     */
    private long mLogProtectTimerInterval = 2 * 60 * 1000;
    //    private static final long LOG_PROTECT_TIMER_INTERVAL = 5 * 1000;



    private long mLogRemoteRequestInterval = 30 * 60 * 1000;
//    private long mLogRemoteRequestInterval = 0;

    private boolean mIsEnable = true;


    public long getLogProtectTimerInterval() {
        return mLogProtectTimerInterval;
    }

    public void setLogProtectTimerInterval(long logProtectTimerInterval) {
        mLogProtectTimerInterval = logProtectTimerInterval;
    }

    public long getLogRemoteRequestInterval(Context context) {
        if(AtworkConfig.TEST_MODE_CONFIG.isTestMode()) {
            return 0;
        }


        return mLogRemoteRequestInterval;
    }

    public void setLogRemoteRequestInterval(long logRemoteRequestInterval) {
        mLogRemoteRequestInterval = logRemoteRequestInterval;
    }


    public long getLogRemoteRequestInterval() {
        return mLogRemoteRequestInterval;
    }

    public boolean isEnable() {
        return mIsEnable;
    }

    public BehaviorLogConfig setEnable(boolean enable) {
        mIsEnable = enable;
        return this;
    }
}
