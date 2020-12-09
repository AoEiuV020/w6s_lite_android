package com.foreveross.atwork.manager;

import com.foreveross.atwork.cordova.plugin.WxOrQQPlugin;
import com.foreveross.atwork.cordova.plugin.model.CordovaBasicResponse;
import com.foreveross.atwork.cordova.plugin.model.QQResponse;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.apache.cordova.CallbackContext;
import org.json.JSONObject;

public class BaseUiListener implements IUiListener {
    private static final String TAG = BaseUiListener.class.getSimpleName();

    @Override
    public void onComplete(Object o) {
        Logger.e(TAG, "onComplete");

        CallbackContext callbackContext = WxOrQQPlugin.Companion.getCurrentCallbackContext();
        if (null != callbackContext) {
            try {
                QQResponse qqResponse = new QQResponse(new JSONObject(JsonUtil.toJson(o)));
                qqResponse.setCode(0);
                callbackContext.success(qqResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onError(UiError e) {
        Logger.e(TAG, "onError");

        CallbackContext callbackContext = WxOrQQPlugin.Companion.getCurrentCallbackContext();
        if (null != callbackContext) {
            callbackContext.error(e);
        }
    }

    @Override
    public void onCancel() {
        Logger.e(TAG, "onCancel");

        CallbackContext callbackContext = WxOrQQPlugin.Companion.getCurrentCallbackContext();
        if (null != callbackContext) {
            try {
                CordovaBasicResponse qqResponse = new CordovaBasicResponse();
                qqResponse.setCode(1);
                callbackContext.success(qqResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }
}

