package com.foreveross.atwork.api.sdk.wallet.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2018/1/3.
 */

public class GrabRedEnvelopeResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public Result mResult;

    public class Result {

        @SerializedName("user_id")
        public String mUserId;

        @SerializedName("domain_id")
        public String mDomainId;

        @SerializedName("username")
        public String mUserName;

        @SerializedName("amount")
        public long mAmount;
    }
}
