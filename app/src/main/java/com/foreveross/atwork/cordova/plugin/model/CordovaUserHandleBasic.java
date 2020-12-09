package com.foreveross.atwork.cordova.plugin.model;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/5/6.
 */
public class CordovaUserHandleBasic {
    @SerializedName("userId")
    public String mUserId = StringUtils.EMPTY;

    @SerializedName("domainId")
    public String mDomainId = StringUtils.EMPTY;

}
