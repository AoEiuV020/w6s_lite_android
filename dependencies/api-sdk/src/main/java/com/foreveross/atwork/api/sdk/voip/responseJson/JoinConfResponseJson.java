package com.foreveross.atwork.api.sdk.voip.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/7/15.
 */
public class JoinConfResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public Result mResult;

    public class Result {
        @SerializedName("token")
        public String mToken;

        @SerializedName("user_id")
        public String mUserId;
    }
}
