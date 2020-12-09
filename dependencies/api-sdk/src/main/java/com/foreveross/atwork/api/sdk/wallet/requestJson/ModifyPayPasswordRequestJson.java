package com.foreveross.atwork.api.sdk.wallet.requestJson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2018/1/1.
 */

public class ModifyPayPasswordRequestJson {

    @SerializedName("password")
    public String mOriginalPassword;


    @SerializedName("new_password")
    public String mNewPassword;

    public static ModifyPayPasswordRequestJson newInstance() {
        return new ModifyPayPasswordRequestJson();
    }

    public ModifyPayPasswordRequestJson setOriginalPassword(String originalPassword) {
        mOriginalPassword = originalPassword;
        return this;
    }

    public ModifyPayPasswordRequestJson setNewPassword(String newPassword) {
        mNewPassword = newPassword;
        return this;
    }
}
