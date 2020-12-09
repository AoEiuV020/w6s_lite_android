package com.foreveross.atwork.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.workplus.amap.AmapManager;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.app.AppAsyncNetService;
import com.foreveross.atwork.api.sdk.cordova.CordovaAsyncNetService;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.model.location.GetLocationRequest;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.modules.app.model.OutFieldPunchInfo;
import com.foreveross.atwork.services.receivers.OutFieldPunchReceiver;
import com.foreveross.atwork.services.support.AlarmMangerHelper;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by reyzhang22 on 2017/12/6.
 */

public class OutFieldPunchHelper {

    public enum IntervalType {
        start {
            @Override
            public String toStringValue() {
                return "start";
            }
        },
        normal {
            @Override
            public String toStringValue() {
                return "normal";
            }
        },
        end {
            @Override
            public String toStringValue() {
                return "end";
            }
        };

        public abstract String toStringValue();
    }

    public static void startOutFieldIntervalPunch(Context context, String orgId, int requestCode, int interval) {

        AlarmMangerHelper.setOutFieldIntervalPunch(context, orgId, requestCode, interval);
    }

    public static void stopOutFieldIntervalPunch(Context context, String orgId, int requestCode) {
        Intent intent = new Intent(context, OutFieldPunchReceiver.class);
        intent.putExtra(AlarmMangerHelper.OUT_FIELD_ORG_ID, orgId);
        AlarmMangerHelper.stopAlarm(context, intent, requestCode);
    }


    public static void startAllOutFieldIntervalPunch(Context context) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        if (TextUtils.isEmpty(accessToken)) {
            return;
        }
        OrganizationManager.getInstance().getLocalOrganizations(context, localData -> {
            List<Organization> organizations = (List<Organization>) localData[0];
            if (ListUtil.isEmpty(organizations)) {
                return;
            }
            for(Organization org : organizations) {
                if (org == null) {
                    continue;
                }
                int requestCode = PersonalShareInfo.getInstance().getOrgOutFieldPunchRequestCode(context, org.mOrgCode);
                int intervalTime = PersonalShareInfo.getInstance().getOrgOutFieldPunchIntervalTime(context, org.mOrgCode);
                if (requestCode == -1 || intervalTime == -1) {
                    continue;
                }
                startOutFieldIntervalPunch(context, org.mOrgCode, requestCode, intervalTime);
            }
        });
    }

    public static void stopAllOutFieldIntervalPunch(Context context) {
        OrganizationManager.getInstance().getLocalOrganizations(context, localData -> {
            List<Organization> organizations = (List<Organization>) localData[0];
            if (ListUtil.isEmpty(organizations)) {
                return;
            }
            for(Organization org : organizations) {
                if (org == null) {
                    continue;
                }
                int requestCode = PersonalShareInfo.getInstance().getOrgOutFieldPunchRequestCode(context, org.mOrgCode);
                int intervalTime = PersonalShareInfo.getInstance().getOrgOutFieldPunchIntervalTime(context, org.mOrgCode);
                if (requestCode == -1 || intervalTime == -1) {
                    continue;
                }
                stopOutFieldIntervalPunch(context, org.mOrgCode, requestCode);
            }
        });
    }

    public static void removeShareInfo(Context context, String orgId) {
        PersonalShareInfo.getInstance().removeOrgOutFieldPunchIntervalTime(context, orgId);
        PersonalShareInfo.getInstance().removeOrgOutFieldPunchRequestCode(context, orgId);
    }

    public static int requestCounter = 0;
    public static void onOutFieldNetRequest(Context context, String orgId, IntervalType type, AppAsyncNetService.OnOutFieldIntervalListener listener) {

        if(!BeeWorks.getInstance().config.enableCheckIn) {
            return;
        }


        CordovaAsyncNetService.getUserTicket(context, new CordovaAsyncNetService.GetUserTicketListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void getUserTicketSuccess(String userTicket) {
                if (TextUtils.isEmpty(userTicket)) {
                    if (listener != null) {
                        listener.onOutFieldInterval(-1);
                    }
                    return;
                }
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        int count = 0;
                        AmapManager amap = AmapManager.getInstance();
                        amap.startLocation(context, GetLocationRequest.Accuracy.HIGH);
                        while (!amap.isLocated()) {
                            try {
                                Thread.sleep(500l);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            count++;
                            if (count > 30) {
                                break;
                            }
                        }
                        if (!amap.isLocated() && requestCounter < 2) {
                            Logger.e(OutFieldPunchHelper.class.getName(), "request location fail retrying...");
                            requestCounter++;
                            onOutFieldNetRequest(context, orgId, type, listener);
                            return null;
                        }

                        AppAsyncNetService service = new AppAsyncNetService(context);
                        String url = UrlConstantManager.getInstance().getOutFieldIntervalPunch();
                        OutFieldPunchInfo info = new OutFieldPunchInfo();
                        info.mDomainId = AtworkConfig.DOMAIN_ID;
                        info.mTicket = userTicket;
                        info.mOrgId = orgId;
                        info.mUserId = LoginUserInfo.getInstance().getLoginUserId(context);
                        info.mLatitude = amap.mLatitude;
                        info.mLongitude = amap.mLongitude;
                        info.mAddress = amap.mAddress;
                        info.mName = amap.mAoiName;
                        info.mType = type.toStringValue();
                        service.outFieldIntervalPunch(url, new Gson().toJson(info), outFieldInterval -> {
                            if (outFieldInterval == -1) {
                                PersonalShareInfo.getInstance().setOrgOutFieldPunchIntervalTime(context, orgId, -1);
                                Intent pendingIntent = new Intent(context, OutFieldPunchReceiver.class);
                                int requestCode = PersonalShareInfo.getInstance().getOrgOutFieldPunchRequestCode(context, orgId);
                                AlarmMangerHelper.stopAlarm(context, pendingIntent, requestCode);

                            }
                            if (listener != null) {
                                listener.onOutFieldInterval(outFieldInterval);
                            }
                            requestCounter = 0;
                        });
                        return null;
                    }
                }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
            }
        });
    }

}
