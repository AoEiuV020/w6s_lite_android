package com.foreveross.atwork.modules.meeting.service;

import android.content.Context;

import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.model.umeeting.UmeetingJoinRequest;
import com.foreveross.atwork.infrastructure.model.umeeting.UmeetingStartRequest;
import com.foreveross.atwork.infrastructure.support.ZoomConfig;
import com.foreveross.atwork.infrastructure.utils.reflect.Reflect;
import com.foreveross.atwork.infrastructure.utils.reflect.ReflectException;

/**
 * Created by dasunsy on 2017/11/9.
 */

public class UmeetingReflectService {

    @Nullable
    private static Object getInstance() {
        try {
            return Reflect.on("us.zoom.sdk.ZoomSDK").call("getInstance").get();
        } catch (ReflectException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean isInitialized() {
        Object instance = getInstance();
        if (null == instance) {
            return false;
        }

        return Reflect.on(instance).call("isInitialized").get();
    }


    public static void initialize(Context context, ZoomConfig umeetingConfig, ZoomSDKInitializeListenerProxy zoomSDKInitializeListenerProxy) {
        Object instance = getInstance();
        if (null == instance) {
            return;
        }


        Object zoomSDKInitializeListener = Reflect.on(zoomSDKInitializeListenerProxy).as(Reflect.forName("us.zoom.sdk.ZoomSDKInitializeListener"));

        Class<?>[] types = new Class<?>[5];
        types[0] = Context.class;
        types[1] = String.class;
        types[2] = String.class;
        types[3] = String.class;
        types[4] = Reflect.forName("us.zoom.sdk.ZoomSDKInitializeListener");

        Object[] objParms = {context, umeetingConfig.getAppKey(), umeetingConfig.getAppSecret(), umeetingConfig.getWebDomain(), zoomSDKInitializeListener};

        Reflect.on(instance).callInFixedType("initialize", types, objParms);

//        ZoomSDK.getInstance().initialize(context, umeetingConfig.appKey, umeetingConfig.mAppSecret, umeetingConfig.mWebDomain, new ZoomSDKInitializeListener() {
//            @Override
//            public void onZoomSDKInitializeResult(int i, int i1) {
//                LogUtil.e("onZoomSDKInitializeResult !~!!!!!!  errorCode : " + i);
//
//            }
//        });

    }

    public static Object getMeetingService() {
        Object instance = getInstance();
        if (null == instance) {
            return null;
        }

        return Reflect.on(instance).call("getMeetingService").get();
    }

    public static int startMeeting(UmeetingStartRequest umeetingStartRequest) {
        Object service = getMeetingService();
        if (null == service) {
            return -1;
        }

        if (checkReturnMeeting(umeetingStartRequest.mContext, umeetingStartRequest.mMeetingNo)) return 0;

        Class<?>[] types = new Class<?>[7];
        types[0] = Context.class;
        types[1] = String.class;
        types[2] = String.class;
        types[3] = Integer.class;
        types[4] = String.class;
        types[5] = String.class;


        Object startMeetingOptions = Reflect.on("us.zoom.sdk.StartMeetingOptions").create().get();
        Reflect.on(startMeetingOptions).set("invite_options", Reflect.on("us.zoom.sdk.InviteOptions").field("INVITE_DISABLE_ALL").get());

        types[6] = startMeetingOptions.getClass();

        Object[] objParms = new Object[]{umeetingStartRequest.mContext, umeetingStartRequest.mUserId, umeetingStartRequest.mZoomToken, umeetingStartRequest.mUserType, umeetingStartRequest.mMeetingNo, umeetingStartRequest.mDisplayName, startMeetingOptions};

        return Reflect.on(service).callInFixedType("startMeeting", types, objParms).get();
    }

    public static int joinMeeting(UmeetingJoinRequest umeetingJoinRequest) {
        Object service = getMeetingService();
        if (null == service) {
            return -1;
        }

        if (checkReturnMeeting(umeetingJoinRequest.mContext, umeetingJoinRequest.mMeetingNo)) return 0;

        Class<?>[] types = new Class<?>[4];
        types[0] = Context.class;
        types[1] = String.class;
        types[2] = String.class;

        Object joinMeetingOptions = Reflect.on("us.zoom.sdk.JoinMeetingOptions").create().get();
        Reflect.on(joinMeetingOptions).set("invite_options", Reflect.on("us.zoom.sdk.InviteOptions").field("INVITE_DISABLE_ALL").get());

        types[3] = joinMeetingOptions.getClass();


        Object[] objParms = new Object[]{umeetingJoinRequest.mContext, umeetingJoinRequest.mMeetingNo, umeetingJoinRequest.mDisplayName, joinMeetingOptions};

        return Reflect.on(service).callInFixedType("joinMeeting", types, objParms).get();

    }

    private static boolean checkReturnMeeting(Context context, String meetingNo) {
        long currentMeetingId = getCurrentMeetingID();
        if (currentMeetingId == Long.parseLong(meetingNo)) {
            returnToMeeting(context);
            return true;
        }
        return false;
    }

    public static long getCurrentMeetingID() {
        Object service = getMeetingService();
        if (null == service) {
            return -1;
        }

        return Reflect.on(service).call("getCurrentRtcMeetingNumber").get();
    }

    public static void returnToMeeting(Context context) {
        Object service = getMeetingService();
        if (null == service) {
            return;
        }

        Class<?>[] types = new Class<?>[1];
        types[0] = Context.class;
        Object[] objParms = new Object[]{context};

        Reflect.on(service).callInFixedType("returnToMeeting", types, objParms);
    }


    public interface ZoomSDKInitializeListenerProxy {
        void onZoomSDKInitializeResult(int errorCode, int internalErrorCode);
    }


}
