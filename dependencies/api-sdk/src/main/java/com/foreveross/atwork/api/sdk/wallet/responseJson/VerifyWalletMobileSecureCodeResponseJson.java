package com.foreveross.atwork.api.sdk.wallet.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/12/31.
 */

public class VerifyWalletMobileSecureCodeResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public Result mResult;


    public class Result {
        @SerializedName("captcha_key")
        public String mCaptchaKey;

        @SerializedName("survival_seconds")
        public long mSurvivalSecs;
    }
}
