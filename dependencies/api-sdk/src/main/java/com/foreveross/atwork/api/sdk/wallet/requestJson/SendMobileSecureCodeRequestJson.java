package com.foreveross.atwork.api.sdk.wallet.requestJson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/12/31.
 */

public class SendMobileSecureCodeRequestJson {

    @SerializedName("phone")
    public String mMobile;


    public static SendMobileSecureCodeRequestJson newInstance() {
        return new SendMobileSecureCodeRequestJson();
    }


    public SendMobileSecureCodeRequestJson setMobile(String mobile) {
        mMobile = mobile;
        return this;
    }
}
