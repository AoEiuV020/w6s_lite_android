package com.foreveross.atwork.modules.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by reyzhang22 on 2017/12/5.
 */

public class OutFieldPunchInfo {

    @SerializedName("ticket")
    public String mTicket;

    @SerializedName("userId")
    public String mUserId;

    @SerializedName("orgId")
    public String mOrgId;

    @SerializedName("domainId")
    public String mDomainId;

    @SerializedName("longitude")
    public double mLongitude;

    @SerializedName("latitude")
    public double mLatitude;

    @SerializedName("address")
    public String mAddress;

    @SerializedName("name")
    public String mName;

    @SerializedName("type")
    public String mType;
}
