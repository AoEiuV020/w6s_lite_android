package com.foreveross.atwork.cordova.plugin.sms;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.cordova.plugin.WorkPlusCordovaPlugin;
import com.foreveross.atwork.cordova.plugin.model.RouteRequest;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by wuzejie on 2020/2/12.
 */
public class SmsPlugin extends WorkPlusCordovaPlugin {
    private CallbackContext mContextCall;

    private static final String ACTION_SEND = "send";


    @Override
    public boolean execute(String action, JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {
        if(!requestCordovaAuth()){
            callbackContext.error(CORDOVA_NOT_AUTH);
            return true;
        }

        mContextCall = callbackContext;

        if (ACTION_SEND.equals(action)) {
            send(jsonArray, callbackContext);
            return true;
        }

        return false;
    }


    private void send(JSONArray jsonArray, CallbackContext callbackContext) {
        RouteRequest routeRequest = NetGsonHelper.fromCordovaJson(jsonArray, RouteRequest.class);
            try {
                jumpToShoreMessage(routeRequest.mContent);
                callbackContext.success();

            } catch (Exception e) {
                e.printStackTrace();
            }

        callbackContext.error();
    }

    /**
     * 发送短信
     * @param content
     * @throws ActivityNotFoundException
     */
    private void jumpToShoreMessage(String  content) throws ActivityNotFoundException {
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(Uri.parse("smsto:"));
        sendIntent.putExtra("sms_body", content);

        cordova.getActivity().startActivity(sendIntent);

    }


}

