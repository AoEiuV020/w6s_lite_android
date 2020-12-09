package com.foreveross.atwork.api.sdk.upload.model;

import com.google.gson.annotations.SerializedName;

public class MediaKeepAliveRequest {

    @SerializedName("expire_time")
    public long expireTime = -1;
}
