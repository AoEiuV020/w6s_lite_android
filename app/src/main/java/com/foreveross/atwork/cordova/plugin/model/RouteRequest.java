package com.foreveross.atwork.cordova.plugin.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Created by dasunsy on 2017/6/27.
 */

public class RouteRequest {

    @SerializedName("scheme_url")
    public String mSchemeUrl;

    @SerializedName("android_explicit_intent")
    public ExplicitIntent mExplicitIntent;

    @SerializedName("content")
    public String mContent;

    public class ExplicitIntent {
        @SerializedName("package")
        public String mPackage;

        @SerializedName("init_url")
        public String mInitUrl;

        @SerializedName("params")
        public HashMap<String, String> mParams;


    }
}
