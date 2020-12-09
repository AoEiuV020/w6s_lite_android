package com.foreveross.atwork.services.support;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.services.ImSocketService;
import com.foreveross.atwork.services.receivers.AlarmReceiver;
import com.foreveross.atwork.services.receivers.OutFieldPunchReceiver;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by dasunsy on 16/8/28.
 */
public class AlarmMangerHelper {

    private static int REQUEST_CODE_SERVICE_GUARD = 0;

    private static int REQUEST_CODE_HEART_BEAT = 1;

    public static final String OUT_FIELD_ORG_ID = "out_field_org_id";

    public static void initServiceGuardAlarm(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        setTime(context, true, intent, REQUEST_CODE_SERVICE_GUARD, AtworkConfig.INTERVAL_SERVICE_GUARD);
    }



    public static void setServiceGuardAlarm(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        setTime(context, false, intent, REQUEST_CODE_SERVICE_GUARD, AtworkConfig.INTERVAL_SERVICE_GUARD);
    }

    public static void setHeartBeatAlarm(Context context) {
        Intent intent = new Intent(ImSocketService.ACTION_HEART_BEAT);
        setTime(context, false, intent, REQUEST_CODE_HEART_BEAT, AtworkConfig.INTERVAL_HEART_BEAT);
    }

    public static void setOutFieldIntervalPunch(Context context, String orgId, int requestCode, int interval) {
        Intent intent = new Intent(context, OutFieldPunchReceiver.class);
        intent.putExtra(OUT_FIELD_ORG_ID, orgId);
        PersonalShareInfo.getInstance().setOrgOutFieldPunchIntervalTime(context, orgId, interval);
        setTime(context, false, intent, requestCode, interval*60*1000);
    }

    public static void setTime(Context context, boolean isRepeat, Intent intent, int requestCode, int interval) {
        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        PendingIntent pendIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        long triggerAtTime = SystemClock.elapsedRealtime() + interval;

        if (Build.VERSION.SDK_INT >= 23) {
            manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendIntent);

        } else if(Build.VERSION.SDK_INT >= 19){
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendIntent);

        } else {
            if(isRepeat) {
                manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, interval ,pendIntent);

            } else {
                manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendIntent);

            }
        }
    }

    public static void stopAlarm(Context context, Intent intent, int requestCode) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

}
