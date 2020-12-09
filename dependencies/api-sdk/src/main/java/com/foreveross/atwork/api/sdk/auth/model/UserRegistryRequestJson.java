package com.foreveross.atwork.api.sdk.auth.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/10/11.
 */

public class UserRegistryRequestJson {

    @SerializedName("domain_id")
    public String mDomainId;

    @SerializedName("pin_code")
    public String mPinCode;

    @SerializedName("username")
    public String mUsername;

    @SerializedName("password")
    public String mPassword;

    @SerializedName("birthday")
    public long mBirthday;

    @SerializedName("gender")
    public String mGender;

    @SerializedName("name")
    public String mName;

    @SerializedName("phone")
    public String mPhone;

    @SerializedName("device_id")
    public String mDeviceId;

    @SerializedName("device_platform")
    public String mDevicePlatform;

    @SerializedName("product_version")
    public int mProductVersion;

    @SerializedName("system_model")
    public String mSystemModel;

    @SerializedName("system_version")
    public String mSystemVersion;

    public UserRegistryRequestJson setDomainId(String domainId) {
        mDomainId = domainId;
        return this;
    }

    public UserRegistryRequestJson setPinCode(String pinCode) {
        mPinCode = pinCode;
        return this;
    }

    public UserRegistryRequestJson setUsername(String username) {
        mUsername = username;
        return this;
    }

    public UserRegistryRequestJson setPassword(String password) {
        mPassword = password;
        return this;
    }

    public UserRegistryRequestJson setBirthday(long birthday) {
        mBirthday = birthday;
        return this;
    }

    public UserRegistryRequestJson setGender(String gender) {
        mGender = gender;
        return this;
    }

    public UserRegistryRequestJson setName(String name) {
        mName = name;
        return this;
    }

    public UserRegistryRequestJson setPhone(String phone) {
        mPhone = phone;
        return this;
    }

    public UserRegistryRequestJson setDeviceId(String deviceId) {
        mDeviceId = deviceId;
        return this;
    }

    public UserRegistryRequestJson setDevicePlatform(String devicePlatform) {
        mDevicePlatform = devicePlatform;
        return this;
    }

    public UserRegistryRequestJson setProductVersion(int productVersion) {
        mProductVersion = productVersion;
        return this;
    }

    public UserRegistryRequestJson setSystemModel(String systemModel) {
        mSystemModel = systemModel;
        return this;
    }

    public UserRegistryRequestJson setSystemVersion(String systemVersion) {
        mSystemVersion = systemVersion;
        return this;
    }
}
