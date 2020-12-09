package com.foreveross.atwork.api.sdk.wallet.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2018/1/16.
 */

public class GiveRedEnvelopeResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public Result mResult;

    public static class Result {
        @SerializedName("id")
        public String mTransactionId;
    }
}
