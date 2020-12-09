package com.foreveross.atwork.api.sdk.users.requestJson;

import android.content.Context;

import com.foreveross.atwork.api.sdk.auth.util.EncryptHelper;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.google.gson.annotations.SerializedName;

public class ModifyPasswordJson {

    @SerializedName("password")
    public String password;

    @SerializedName("new_password")
    public String newPassword;

    @SerializedName("encrypt")
    public boolean encrypt = false;


    public static ModifyPasswordJson getChangePasswordJson(Context context, String oldPassword, String newPassword) {
        ModifyPasswordJson json = new ModifyPasswordJson();
        json.password = oldPassword;
        json.newPassword = newPassword;
        return json;
    }
}
