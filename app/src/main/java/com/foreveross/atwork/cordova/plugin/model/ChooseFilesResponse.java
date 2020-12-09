package com.foreveross.atwork.cordova.plugin.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/5/17.
 */

public class ChooseFilesResponse {

    @SerializedName("name")
    public String mName;

    @SerializedName("size")
    public long mSize;

    @SerializedName("filePath")
    public String mFilePath;


    @SerializedName("mediaId")
    public String mMediaId;

}
