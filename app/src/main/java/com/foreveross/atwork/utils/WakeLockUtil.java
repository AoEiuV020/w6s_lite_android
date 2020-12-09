package com.foreveross.atwork.utils;

import android.content.Context;
import android.os.PowerManager;

import androidx.annotation.Nullable;

public class WakeLockUtil {


    public static PowerManager.WakeLock lock(Context context, @Nullable PowerManager.WakeLock wakeLockHolding) {
        if(null == wakeLockHolding) {
            PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLockHolding = mgr.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, context.getPackageName());
        }

        wakeLockHolding.acquire();

        return wakeLockHolding;
    }

    public static void unlock(@Nullable PowerManager.WakeLock wakeLockHolding) {
        if (null != wakeLockHolding) {
            wakeLockHolding.release();
        }
    }

}
