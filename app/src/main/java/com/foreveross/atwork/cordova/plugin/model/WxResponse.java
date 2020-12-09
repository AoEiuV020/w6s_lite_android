package com.foreveross.atwork.cordova.plugin.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2018/3/22.
 */

public class WxResponse {
    @SerializedName("code")
    public int mCode;

    @SerializedName("message")
    public String mMessage;

    public WxResponse(int code, String message) {
        this.mCode = code;
        this.mMessage = message;
    }
}
