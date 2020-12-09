package com.foreveross.atwork.api.sdk.auth.model;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/10/11.
 */

public class PreUserRegistryResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public Result mResult;

    class Result {

        @SerializedName("pin_code")
        public String mPinCode;

        @SerializedName("user")
        public User mUser;

    }
}
