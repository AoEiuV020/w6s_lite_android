package com.foreveross.atwork.api.sdk.agreement.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/7/20.
 */

public class GetUserLoginAgreementResponse extends BasicResponseJSON {

    @SerializedName("result")
    public Result mResult;

    public class Result {
        @SerializedName("title")
        public String mTitle;

        @SerializedName("content")
        public String mContent;
    }
}
