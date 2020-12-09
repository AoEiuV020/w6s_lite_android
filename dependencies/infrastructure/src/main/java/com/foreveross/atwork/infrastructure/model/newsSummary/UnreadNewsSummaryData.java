package com.foreveross.atwork.infrastructure.model.newsSummary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnreadNewsSummaryData {
    @Expose
    @SerializedName("appId")
    public String appId;
    @Expose
    @SerializedName("masId")
    public String masId;
    @Expose
    @SerializedName("deliveryTime")
    public String deliveryTime;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMasId() {
        return masId;
    }

    public void setMasId(String masId) {
        this.masId = masId;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}
