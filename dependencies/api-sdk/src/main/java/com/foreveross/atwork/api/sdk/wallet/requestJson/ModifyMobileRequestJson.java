package com.foreveross.atwork.api.sdk.wallet.requestJson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2018/1/7.
 */

public class ModifyMobileRequestJson {

    @SerializedName("password")
    public String mPassword;


    @SerializedName("captcha_key")
    public String mCaptchaKey;

    public static ModifyMobileRequestJson newInstance() {
        return new ModifyMobileRequestJson();
    }

    public ModifyMobileRequestJson setPassword(String password) {
        mPassword = password;
        return this;
    }

    public ModifyMobileRequestJson setCaptchaKey(String captchaKey) {
        mCaptchaKey = captchaKey;
        return this;
    }
}
