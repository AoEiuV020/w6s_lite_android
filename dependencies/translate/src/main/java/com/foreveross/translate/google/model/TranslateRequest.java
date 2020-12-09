package com.foreveross.translate.google.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/6/4.
 */

public class TranslateRequest {

    @SerializedName("q")
    public String mRequestText;

    @SerializedName("source")
    public String mSource;

    @SerializedName("target")
    public String mTarget;

    @SerializedName("format")
    public String mFormat;

}
