package com.foreveross.atwork.cordova.plugin.model;

import com.foreveross.atwork.infrastructure.model.app.appEnum.DisplayMode;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2016/12/5.
 */

public class OpenWebVRequest {

    @SerializedName("url")
    public String mUrl;

    @SerializedName("title")
    public String mTitle;

    @SerializedName("needAuth")
    public boolean mNeedAuth = true;

    @SerializedName("use_android_webview")
    public boolean mUseAndroidWebview = false;

    @SerializedName("use_system_webview")
    public boolean mUseSystemWebview = false;

    @SerializedName("display_mode")
    public DisplayMode mDisplayMode;

    @SerializedName("hidden_share")
    public String mHideShare;

    @SerializedName("orientation")
    public Integer mOrientation;



    public boolean isUseSystemWebview() {
        return mUseSystemWebview || mUseAndroidWebview;
    }
}
