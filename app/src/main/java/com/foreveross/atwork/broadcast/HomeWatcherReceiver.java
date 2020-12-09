package com.foreveross.atwork.broadcast;

/**
 * Created by dasunsy on 16/1/14.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.listener.HomeActionListener;

public class HomeWatcherReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "HomeReceiver";
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
    private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

    private HomeActionListener mHomeActionListener;

    public HomeWatcherReceiver(HomeActionListener homeActionListener) {
        this.mHomeActionListener = homeActionListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.i(LOG_TAG, "onReceive: action: " + action);
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {

            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

            LogUtil.i(LOG_TAG, "onReceive: reason: " + reason);

            if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {

                // 短按Home键

                if (null != mHomeActionListener) {
                    mHomeActionListener.onHome();
                }


            } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                // 长按Home键 或者 activity切换键
                if (null != mHomeActionListener) {
                    mHomeActionListener.onRecentApps();
                }

            } else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
                // 锁屏

            } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                // samsung 长按Home键
            }

        }
    }




}
