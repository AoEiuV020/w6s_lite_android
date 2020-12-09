package com.foreveross.atwork.api.sdk.users.requestJson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/4/23.
 */
public class ModifyUserInfoJson {
    @SerializedName("name")
    public String name = null;

    @SerializedName("avatar")
    public String avatar = null;

    @SerializedName("phone")
    public String phone = null;

    @SerializedName("email")
    public String email = null;

    @SerializedName("gender")
    public String gender = null;

    @SerializedName("birthday")
    public Long birthday = null;
}
