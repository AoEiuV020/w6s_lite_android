package com.foreveross.atwork.cordova.plugin.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/7/28.
 */
public class GetCurrentUserRequestJson {
    @SerializedName("needEmpInfo")
    public boolean mNeedEmpInfo = true;
}
