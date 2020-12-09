package com.foreveross.atwork.api.sdk.upload.model;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/4/23.
 */
public class MediaUploadResultResponseJson extends BasicResponseJSON{

    @SerializedName("result")
    public String mediaId;
}
