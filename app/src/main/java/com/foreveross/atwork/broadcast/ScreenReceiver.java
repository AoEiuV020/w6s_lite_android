package com.foreveross.atwork.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.foreveross.atwork.listener.ScreenActionListener;

/**
 * Created by dasunsy on 2018/3/15.
 */

public class ScreenReceiver extends BroadcastReceiver {

    private ScreenActionListener mScreenActionListener;

    public ScreenReceiver(ScreenActionListener screenActionListener) {
        this.mScreenActionListener = screenActionListener;
    }



    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
            if(null != mScreenActionListener) {
                mScreenActionListener.onScreenOn();
            }

        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
            if(null != mScreenActionListener) {
                mScreenActionListener.onScreenOff();
            }


        } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
            if (null != mScreenActionListener) {
                mScreenActionListener.onScreenUserPresent();
            }

        }
    }
}
