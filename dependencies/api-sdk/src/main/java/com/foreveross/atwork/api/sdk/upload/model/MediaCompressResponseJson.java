package com.foreveross.atwork.api.sdk.upload.model;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/6/13.
 */

public class MediaCompressResponseJson extends BasicResponseJSON{

    @SerializedName("result")
    public MediaCompressInfo mMediaCompressInfo;

    public boolean isLegal() {
        return null != mMediaCompressInfo && null != mMediaCompressInfo.mOriginalImg && null != mMediaCompressInfo.mCompressImg;
    }

    public class MediaCompressInfo {
        @SerializedName("original_media")
        public ImageMediaInfo mOriginalImg;

        @SerializedName("media")
        public ImageMediaInfo mCompressImg;
    }

    public class ImageMediaInfo {

        @SerializedName("media_id")
        public String mMediaId;

        @SerializedName("size")
        public long mSize;

        @SerializedName("width")
        public int mWidth;

        @SerializedName("height")
        public int mHeight;
    }
}
