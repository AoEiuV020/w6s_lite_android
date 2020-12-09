package com.foreveross.atwork.services.support;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.foreveross.atwork.manager.BasicNotificationManager;

import java.lang.ref.WeakReference;

/**
 * Created by dasunsy on 16/8/27.
 */
public class KeepLiveSupport {

    private static WeakReference<KeepAliveActivity> sKeepLiveActivityWeakRef = null;

    public static void setKeepLiveActivity(KeepAliveActivity activity) {
        sKeepLiveActivityWeakRef = new WeakReference<>(activity);
    }

    public static void clearKeepLiveActivity() {
        sKeepLiveActivityWeakRef = null;
    }

    public static void removeKeepLiveActivity() {
        if(null != sKeepLiveActivityWeakRef) {
            KeepAliveActivity keepAliveActivity = sKeepLiveActivityWeakRef.get();

            if(null != keepAliveActivity) {
                try {
                    keepAliveActivity.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public static void startForeground(Service coreService, Class<?> innerServiceCls) {
        if (supportHackNotification()) {
            coreService.startForeground(1, getNotification(coreService));
            coreService.startService(new Intent(coreService, innerServiceCls));
        }
    }

    public static boolean supportHackNotification() {
        return Build.VERSION_CODES.JELLY_BEAN_MR2 <= Build.VERSION.SDK_INT && Build.VERSION_CODES.P > Build.VERSION.SDK_INT;
    }

    public static void stopForeground(Service innerService) {
        if (supportHackNotification()) {
            innerService.startForeground(1, getNotification(innerService));
            innerService.stopSelf();
        }

    }

    @NonNull
    public static Notification getNotification(Context context) {
        if (Build.VERSION_CODES.O < Build.VERSION.SDK_INT) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, BasicNotificationManager.IM_SERVICE_CHANNEL_ID);
            return builder.build();
        }

        return new Notification();
    }

    public static void setForeground(Service keepLiveService, Service innerService) {
        final int foregroundPushId = 66666;

        if(null != keepLiveService) {
            if(Build.VERSION_CODES.JELLY_BEAN_MR2 > Build.VERSION.SDK_INT) {
                keepLiveService.startForeground(foregroundPushId, getNotification(keepLiveService));
                return;
            }


            if(supportHackNotification()) {
                keepLiveService.startForeground(foregroundPushId, getNotification(keepLiveService));

                if(null != innerService) {
                    innerService.startForeground(foregroundPushId, getNotification(keepLiveService));
                    innerService.stopSelf();
                }

            }

        }
    }

}
