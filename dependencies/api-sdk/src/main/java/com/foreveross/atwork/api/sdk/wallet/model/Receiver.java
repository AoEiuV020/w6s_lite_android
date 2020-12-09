package com.foreveross.atwork.api.sdk.wallet.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2018/1/16.
 */

public class Receiver {

    @SerializedName("to")
    public String mTo;

    @SerializedName("to_domain")
    public String mDomainId;


    @SerializedName("to_type")
    public String mToType;

    @SerializedName("to_name")
    public String mName;

    @SerializedName("org_id'")
    public String mOrgId;

    @SerializedName("avatar")
    public String mAvatar;

    public static Receiver newReceiver() {
        return new Receiver();
    }


    public Receiver setTo(String to) {
        mTo = to;
        return this;
    }

    public Receiver setDomainId(String domainId) {
        mDomainId = domainId;
        return this;
    }

    public Receiver setToType(String toType) {
        mToType = toType;
        return this;
    }

    public Receiver setName(String name) {
        mName = name;
        return this;
    }

    public Receiver setOrgId(String orgId) {
        mOrgId = orgId;
        return this;
    }

    public Receiver setAvatar(String avatar) {
        mAvatar = avatar;
        return this;
    }
}
