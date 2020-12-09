package com.foreveross.atwork.api.sdk.cordova.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lingen on 15/5/12.
 * Description:
 */
public class UserTokenResponseJSON extends BasicResponseJSON {

    @SerializedName("result")
    public UserTokenResultResponseJSON token;

    public String getUserToken() {
        return token.userToken;
    }

    public class UserTokenResultResponseJSON {

        @SerializedName("temporary_token")
        public String userToken;

        @SerializedName("issued_time")
        public long issuedTime;

        @SerializedName("expire_time")
        public long expireTime;
    }


}
