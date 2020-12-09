package com.foreveross.atwork.cordova.plugin.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/10/23.
 */

public class GetWifiInfoResponse {
    @SerializedName("bssid")
    public String mKeyId;

    @SerializedName("name")
    public String mName;
}
