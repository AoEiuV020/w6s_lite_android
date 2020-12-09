package com.foreveross.atwork.cordova.plugin;

import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.infrastructure.model.umeeting.UmeetingJoinRequest;
import com.foreveross.atwork.infrastructure.model.umeeting.UmeetingStartRequest;
import com.foreveross.atwork.modules.meeting.service.UmeetingReflectService;
import com.foreveross.atwork.utils.CordovaHelper;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;

/**
 * Created by dasunsy on 2017/11/10.
 */

public class UmeetingPlugin extends WorkPlusCordovaPlugin {

    public static final String ACTION_START_MEETING = "startMeeting";

    public static final String ACTION_JOIN_MEETING = "joinMeeting";

    @Override
    public boolean execute(String action, String rawArgs, CallbackContext callbackContext) throws JSONException {
        if(!requestCordovaAuth()){
            callbackContext.error(CORDOVA_NOT_AUTH);
            return true;
        }

        if(ACTION_START_MEETING.equals(action)) {
            UmeetingStartRequest umeetingStartRequest = NetGsonHelper.fromCordovaJson(rawArgs, UmeetingStartRequest.class);

            if(null != umeetingStartRequest) {
                umeetingStartRequest.setContext(cordova.getActivity());
                UmeetingHandleResult umeetingHandleResult = new UmeetingHandleResult();

                umeetingHandleResult.mResult = UmeetingReflectService.startMeeting(umeetingStartRequest);
                CordovaHelper.doSuccess(umeetingHandleResult, callbackContext);


                return true;
            }

        } else if(ACTION_JOIN_MEETING.equals(action)) {
            UmeetingJoinRequest umeetingJoinRequest = NetGsonHelper.fromCordovaJson(rawArgs, UmeetingJoinRequest.class);
            if(null != umeetingJoinRequest) {
                umeetingJoinRequest.setContext(cordova.getActivity());
                UmeetingHandleResult umeetingHandleResult = new UmeetingHandleResult();

                umeetingHandleResult.mResult = UmeetingReflectService.joinMeeting(umeetingJoinRequest);
                CordovaHelper.doSuccess(umeetingHandleResult, callbackContext);

                return true;
            }

        }
        return false;
    }

}
