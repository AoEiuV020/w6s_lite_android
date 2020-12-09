package com.foreveross.atwork.api.sdk.wallet.requestJson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/12/31.
 */

public class VerifyWalletMobileSecureCodeRequestJson extends SendMobileSecureCodeRequestJson {

    public static VerifyWalletMobileSecureCodeRequestJson newInstance() {
        return new VerifyWalletMobileSecureCodeRequestJson();
    }

    @SerializedName("code")
    public String mCode;

    public VerifyWalletMobileSecureCodeRequestJson setMobile(String mobile) {
        mMobile = mobile;
        return this;
    }

    public VerifyWalletMobileSecureCodeRequestJson setCode(String code) {
        mCode = code;
        return this;
    }
}
