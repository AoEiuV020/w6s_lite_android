package com.foreveross.atwork.cordova.plugin.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2018/3/2.
 */

public class RefreshUserInfoRequest {

    @SerializedName("userId")
    public String mUserId;


    @SerializedName("domainId")
    public String mDomainId;
}
