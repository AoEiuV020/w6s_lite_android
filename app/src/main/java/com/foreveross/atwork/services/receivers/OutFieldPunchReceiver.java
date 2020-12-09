package com.foreveross.atwork.services.receivers;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.legacy.content.WakefulBroadcastReceiver;

import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.services.support.AlarmMangerHelper;
import com.foreveross.atwork.utils.OutFieldPunchHelper;

/**
 * Created by reyzhang22 on 2017/12/5.
 */

public class OutFieldPunchReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String orgId = intent.getStringExtra(AlarmMangerHelper.OUT_FIELD_ORG_ID);
        int interval = PersonalShareInfo.getInstance().getOrgOutFieldPunchIntervalTime(context, orgId);
        Logger.e("OutFieldPunchReceiver", "out field punc orgid = " + orgId);
        if (TextUtils.isEmpty(orgId) || interval == -1) {
            return;
        }
        OutFieldPunchHelper.onOutFieldNetRequest(context, orgId, OutFieldPunchHelper.IntervalType.normal, outFieldInterval -> {
            int requestCode = PersonalShareInfo.getInstance().getOrgOutFieldPunchRequestCode(context, orgId);
            AlarmMangerHelper.setOutFieldIntervalPunch(context, orgId, requestCode, outFieldInterval);
        });
    }
}
