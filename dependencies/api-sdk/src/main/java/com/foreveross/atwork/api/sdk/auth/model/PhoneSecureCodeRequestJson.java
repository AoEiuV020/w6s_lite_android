package com.foreveross.atwork.api.sdk.auth.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/10/11.
 */

public class PhoneSecureCodeRequestJson {

    @SerializedName("domain_id")
    public String mDomainId;

    @SerializedName("bits")
    public int mBit;

    @SerializedName("frozen_seconds")
    public long mFrozenSeconds;

    @SerializedName("survival_seconds")
    public long mSurvivalSeconds;

    @SerializedName("type")
    public String mType;

    @SerializedName("addresser")
    public String mAddresser;

    @SerializedName("subsystem")
    public String mSubsystem;

    @SerializedName("recipient")
    public String mRecipient;

    @SerializedName(("check_in"))
    public boolean checkIn = false;

    @SerializedName("template")
    public String mTemplate;

    public PhoneSecureCodeRequestJson setDomainId(String domainId) {
        mDomainId = domainId;
        return this;
    }

    public PhoneSecureCodeRequestJson setBit(int bit) {
        mBit = bit;
        return this;
    }

    public PhoneSecureCodeRequestJson setFrozenSeconds(long frozenSeconds) {
        mFrozenSeconds = frozenSeconds;
        return this;
    }

    public PhoneSecureCodeRequestJson setSurvivalSeconds(long survivalSeconds) {
        mSurvivalSeconds = survivalSeconds;
        return this;
    }

    public PhoneSecureCodeRequestJson setType(String type) {
        mType = type;
        return this;
    }

    public PhoneSecureCodeRequestJson setAddresser(String addresser) {
        mAddresser = addresser;
        return this;
    }

    public PhoneSecureCodeRequestJson setSubsystem(String subsystem) {
        mSubsystem = subsystem;
        return this;
    }

    public PhoneSecureCodeRequestJson setRecipient(String recipient) {
        mRecipient = recipient;
        return this;
    }

    public PhoneSecureCodeRequestJson setCheckIn(boolean checkIn) {
        this.checkIn = checkIn;
        return this;
    }

    public PhoneSecureCodeRequestJson setTemplate(String template) {
        this.mTemplate = template;
        return this;
    }
}
