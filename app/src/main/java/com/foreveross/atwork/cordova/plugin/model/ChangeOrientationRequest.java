package com.foreveross.atwork.cordova.plugin.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/5/16.
 */

public class ChangeOrientationRequest {
    @SerializedName("landscape")
    public boolean mLandscape = false;

    @SerializedName("lock")
    public boolean mLock = false;

}
