package com.foreveross.atwork.api.sdk.auth.model;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

public class WangwangAuthResponse {

    @SerializedName("code")
    public int mResult;

    @SerializedName("data")
    public WangwangAuthInnerResponse mWangwangAuthInnerResponse;

    public class WangwangAuthInnerResponse {
        @SerializedName("token")
        public String mToken;
    }




    public String getToken() {
        if(0 == mResult && null != mWangwangAuthInnerResponse) {
            return mWangwangAuthInnerResponse.mToken;
        }

        return StringUtils.EMPTY;
    }
}
