package com.foreveross.atwork.services.receivers;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.legacy.content.WakefulBroadcastReceiver;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.ServiceCompat;
import com.foreveross.atwork.modules.voip.service.CallService;
import com.foreveross.atwork.services.ImSocketService;
import com.foreveross.atwork.services.support.AlarmMangerHelper;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        LogUtil.e(ImSocketService.TAG, "AlarmReceiver -> start services");

        try {
            startWakefulService(context, new Intent(context, ImSocketService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (AtworkConfig.OPEN_VOIP) {
            ServiceCompat.startServiceCompat(context, CallService.class);
        }



        if (Build.VERSION.SDK_INT >= 19) {
            LogUtil.e(ImSocketService.TAG, "AlarmReceiver -> reSetAlarm");

            AlarmMangerHelper.setServiceGuardAlarm(BaseApplicationLike.baseContext);
        }

    }

}