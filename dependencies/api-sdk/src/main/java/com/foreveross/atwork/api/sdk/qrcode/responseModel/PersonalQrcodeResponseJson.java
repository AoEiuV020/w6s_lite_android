package com.foreveross.atwork.api.sdk.qrcode.responseModel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shadow on 2016/5/17.
 */
public class PersonalQrcodeResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public Result mResult;

    public class Result {
        @SerializedName("survivalSeconds")
        private Long mSurvivalSeconds = 0L;

        @SerializedName("survival_seconds")
        private Long mSurvival_seconds;

        @SerializedName("content")
        public String mContent;

        public long getSurvivalSeconds() {
            if(null == mSurvival_seconds) {
                return mSurvivalSeconds;
            }

            return mSurvival_seconds;
        }
    }

}
