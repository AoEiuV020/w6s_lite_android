package com.foreveross.atwork.modules.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by reyzhang22 on 2017/12/4.
 */


public class WifiPunchListInfo {

    @SerializedName("data")
    public List<WifiPunchInfo> mList = new ArrayList<>();

    public static class WifiPunchInfo implements Serializable {

        @SerializedName("ticket")
        public String mTicket;

        @SerializedName("userId")
        public String mUserId;

        @SerializedName("orgId")
        public String mOrgId;

        @SerializedName("domainId")
        public String mDomainId;

        @SerializedName("macAddress")
        public String mMacAddress;

        @SerializedName("deviceId")
        public String mDeviceId;
    }
}
