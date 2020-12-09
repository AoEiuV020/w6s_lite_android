package com.foreveross.atwork.api.sdk.auth.model;

import com.google.gson.annotations.SerializedName;

public class WangwangAuthRequest {

    @SerializedName("userId")
    public String mUserId;

    @SerializedName("deviceId")
    public String mDeviceId;

    @SerializedName("osType")
    public String mOsType;

    @SerializedName("msgUid")
    public String mMsgUid;

    @SerializedName("version")
    public String mVersion;

    @SerializedName("Rm")
    public String mRm;

    private WangwangAuthRequest(Builder builder) {
        mUserId = builder.mUserId;
        mDeviceId = builder.mDeviceId;
        mOsType = builder.mOsType;
        mMsgUid = builder.mMsgUid;
        mVersion = builder.mVersion;
        mRm = builder.mRm;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getFormParm() {
        return "deviceId=" + mDeviceId
                + "&userId=" + mUserId
                + "&osType=" + mOsType
                + "&msgUid=" + mMsgUid
                + "&version=" + mVersion
                + "&Rm=" + mRm;
    }

    public static final class Builder {
        private String mUserId;
        private String mDeviceId;
        private String mOsType;
        private String mMsgUid;
        private String mVersion;
        private String mRm;

        private Builder() {
        }


        public Builder setUserId(String mUserId) {
            this.mUserId = mUserId;
            return this;
        }

        public Builder setDeviceId(String mDeviceId) {
            this.mDeviceId = mDeviceId;
            return this;
        }

        public Builder setOsType(String mOsType) {
            this.mOsType = mOsType;
            return this;
        }

        public Builder setMsgUid(String mMsgUid) {
            this.mMsgUid = mMsgUid;
            return this;
        }

        public Builder setVersion(String mVersion) {
            this.mVersion = mVersion;
            return this;
        }

        public Builder setRm(String mRm) {
            this.mRm = mRm;
            return this;
        }

        public WangwangAuthRequest build() {
            return new WangwangAuthRequest(this);
        }
    }
}
