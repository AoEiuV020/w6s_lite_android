package com.foreveross.atwork.api.sdk.wallet.requestJson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2018/1/1.
 */

public class ForcedSetPayPasswordRequestJson {

    @SerializedName("captcha_key")
    public String mCaptchaKey;


    @SerializedName("new_password")
    public String mNewPassword;


    public static ForcedSetPayPasswordRequestJson newInstance() {
        return new ForcedSetPayPasswordRequestJson();
    }

    public ForcedSetPayPasswordRequestJson setCaptchaKey(String captchaKey) {
        mCaptchaKey = captchaKey;
        return this;
    }

    public ForcedSetPayPasswordRequestJson setNewPassword(String newPassword) {
        mNewPassword = newPassword;
        return this;
    }
}
