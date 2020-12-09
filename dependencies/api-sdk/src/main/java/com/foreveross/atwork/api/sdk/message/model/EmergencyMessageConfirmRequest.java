package com.foreveross.atwork.api.sdk.message.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/11/24.
 */

public class EmergencyMessageConfirmRequest {

    public String mDeliveryId;

    @Expose
    @SerializedName("type")
    public Type mType;

    @Expose
    @SerializedName("org_id")
    public String mOrgCode;

    @Expose
    @SerializedName("app_id")
    public String mAppId;

    @Expose
    @SerializedName("plan_id")
    public String mPlanId;

    public static EmergencyMessageConfirmRequest newInstance() {
        return new EmergencyMessageConfirmRequest();
    }

    public EmergencyMessageConfirmRequest setDeliveryId(String deliveryId) {
        mDeliveryId = deliveryId;
        return this;
    }

    public EmergencyMessageConfirmRequest setType(Type type) {
        mType = type;
        return this;
    }

    public EmergencyMessageConfirmRequest setOrgCode(String orgCode) {
        mOrgCode = orgCode;
        return this;
    }

    public EmergencyMessageConfirmRequest setAppId(String appId) {
        mAppId = appId;
        return this;
    }

    public EmergencyMessageConfirmRequest setPlanId(String planId) {
        mPlanId = planId;
        return this;
    }

    public enum Type {

        SERVE_NO,

        LIGHT_APP,

        NATIVE_APP
    }
}
