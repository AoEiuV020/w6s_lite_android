package com.foreveross.atwork.cordova.plugin.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2016/9/27.
 */

public class ChangeThemeRequestJson {
    @SerializedName("orgCode")
    public String mOrgCode;

    @SerializedName("theme")
    public String mThemeName;
}
