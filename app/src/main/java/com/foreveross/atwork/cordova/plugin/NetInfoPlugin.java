package com.foreveross.atwork.cordova.plugin;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.foreveross.atwork.cordova.plugin.model.GetWifiInfoResponse;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.utils.CordovaHelper;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;

/**
 * Created by dasunsy on 2017/10/23.
 */

public class NetInfoPlugin extends WorkPlusCordovaPlugin {

    public static final String GET_WIFI_INFO = "getWifiInfo";

    @Override
    public boolean execute(String action, String rawArgs, final CallbackContext callbackContext) throws JSONException {
        if(!requestCordovaAuth()){
            callbackContext.error(CORDOVA_NOT_AUTH);
            return true;
        }

        if(GET_WIFI_INFO.equals(action)) {

            if(!NetworkStatusUtil.isWifiConnectedOrConnecting(BaseApplicationLike.baseContext)) {
                callbackContext.error("");
            }

            WifiManager wifi = (WifiManager) BaseApplicationLike.baseContext.getSystemService(Context.WIFI_SERVICE);
            if (null != wifi) {
                WifiInfo info = wifi.getConnectionInfo();
                String bssid = info.getBSSID();
                String ssid = info.getSSID();
                ssid = ssid.replace("\"", "");


                GetWifiInfoResponse getWifiInfoResponse = new GetWifiInfoResponse();
                getWifiInfoResponse.mKeyId = bssid;
                getWifiInfoResponse.mName = ssid;

                CordovaHelper.doSuccess(getWifiInfoResponse, callbackContext);
            }
        }

        return false;
    }



}
