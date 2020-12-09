package com.foreveross.atwork.api.sdk.auth.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/10/11.
 */

public class PreUserRegistryRequestJson {

    @SerializedName("ip")
    public String mIp;

    @SerializedName("secure_code")
    public String mSecureCode;

    @SerializedName("username")
    public String mUsername;

    @SerializedName("domain_id")
    public String mDomainId;

    public PreUserRegistryRequestJson setIp(String ip) {
        mIp = ip;
        return this;
    }

    public PreUserRegistryRequestJson setSecureCode(String secureCode) {
        mSecureCode = secureCode;
        return this;
    }

    public PreUserRegistryRequestJson setUsername(String username) {
        mUsername = username;
        return this;
    }

    public PreUserRegistryRequestJson setDomainId(String domainId) {
        mDomainId = domainId;
        return this;
    }
}
