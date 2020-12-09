package com.foreveross.atwork.api.sdk.upload.model;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/4/23.
 */
public class MediaInfoResponseJson extends BasicResponseJSON {
    @SerializedName("result")
    public MediaInfo mediaInfo;

    public class MediaInfo {
        @SerializedName("id")
        public String id;

        @SerializedName("name")
        public String name;

        @SerializedName("digest")
        public String digest;

        @SerializedName("size")
        public int size;

        @SerializedName("timestamp")
        public long timestamp;

        @SerializedName("state")
        public int state;
    }


    public boolean isLegal() {
        return null != mediaInfo && !(StringUtils.isEmpty(mediaInfo.id));
    }
}
