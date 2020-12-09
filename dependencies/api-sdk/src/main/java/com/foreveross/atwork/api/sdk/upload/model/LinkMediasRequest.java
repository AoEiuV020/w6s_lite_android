package com.foreveross.atwork.api.sdk.upload.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LinkMediasRequest {

    @SerializedName("expire_time")
    public long expireTime = -1;

    @SerializedName("media_ids")
    public List<String> mediaIds = new ArrayList<>();
}
