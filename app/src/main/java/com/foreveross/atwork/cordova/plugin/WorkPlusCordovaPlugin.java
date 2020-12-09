package com.foreveross.atwork.cordova.plugin;

import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.web.auth.CordovaInjectType;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

public class WorkPlusCordovaPlugin extends CordovaPlugin {

    protected static final String CORDOVA_NOT_AUTH = "cordova_not_auth";

    protected boolean requestCordovaAuth() {

        if(cordova.getActivity() instanceof WebViewActivity){
            WebViewActivity webViewActivity = (WebViewActivity) cordova.getActivity();

            CordovaInjectType injectType = webViewActivity.getInjectType();
            if(injectType == CordovaInjectType.InjectDisallow){
                return false;
            }
            else if(injectType == CordovaInjectType.InjectNeedAsked){
                cordova.getActivity().runOnUiThread(()->{
                    webViewActivity.requestCordovaAuth();
                });
                return false;
            }
            else {
                return true;
            }
        }else{
            return false;
        }
    }
}
