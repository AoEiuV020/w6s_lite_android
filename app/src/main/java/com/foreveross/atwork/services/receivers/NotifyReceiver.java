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

/**
 * 监听蓝牙、网络切换、开机等事件，以启动服务
 * <p/>
 * Created by chencao on 15/6/13.
 */
public class NotifyReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        LogUtil.e(ImSocketService.TAG, "NotifyReceiver -> Received action=" + intent.getAction());

        try {
            startWakefulService(context, new Intent(context, ImSocketService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (AtworkConfig.OPEN_VOIP) {
            ServiceCompat.startServiceCompat(context, CallService.class);
        }

        if (Build.VERSION.SDK_INT >= 19) {
            LogUtil.e(ImSocketService.TAG, "NotifyReceiver -> reSetAlarm");

            AlarmMangerHelper.setServiceGuardAlarm(BaseApplicationLike.baseContext);
        }
    }

}
