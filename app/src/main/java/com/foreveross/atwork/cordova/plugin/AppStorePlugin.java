package com.foreveross.atwork.cordova.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lingen on 15/5/1.
 * APP STORE插件
 */
public class AppStorePlugin extends CordovaPlugin {

    private CallbackContext mContextCall;

    //退出APP
    private static final String EXIT_WEB_VIEW = "exit";

    //安装应用接口
    private static final String APP_INSTALL_ACTION = "installApp";

    //卸载应用接口
    private static final String APP_REMOVE_ACTION = "removeApp";

    @Override
    public boolean execute(String action, JSONArray jsonArray, CallbackContext callbackContext) throws JSONException {

        if (EXIT_WEB_VIEW.equals(action)) {
            cordova.getActivity().finish();
            return true;
        }

        final JSONObject json = jsonArray.optJSONObject(0);
        if (json == null) {
            return false;
        }

        mContextCall = callbackContext;

        if (APP_INSTALL_ACTION.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
//                    onAppInstall(json);
                }
            });
            return true;
        }

        if (APP_REMOVE_ACTION.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {

                }
            });
            return true;
        }

        return false;
    }


}

