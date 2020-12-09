package com.foreveross.atwork.cordova.plugin.model;

import com.google.gson.annotations.SerializedName;
import com.tencent.mm.sdk.modelpay.PayReq;

/**
 * Created by dasunsy on 2017/7/18.
 */

public class PayRequestJson {

    @SerializedName("request_data")
    public PayReq mData;

    @SerializedName("app_id")
    public String mAppId;
}
