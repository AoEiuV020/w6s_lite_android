package com.foreveross.atwork.cordova.plugin.model;

/**
 * Created by lingen on 15/5/6.
 * Description:
 */
public class PluginError {

    private String result = "FAIL";

    private String msg;

    public static PluginError createInstance(String msg) {
        PluginError pluginError = new PluginError();
        pluginError.msg = msg;
        return pluginError;
    }
}
