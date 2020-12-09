package com.foreveross.atwork.api.sdk.news.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by reyzhang22 on 15/9/9.
 */
public class NewsJson {

    @SerializedName("code")
    public int mCode;

    @SerializedName("msg")
    public String mMsg;

    @SerializedName("0")
    public NewsInfoJson mNewsInfoJson;

    public class NewsInfoJson {

        @SerializedName("time")
        public String mTime;

        @SerializedName("title")
        public String mTitle;

        @SerializedName("description")
        public String mDesc;

        @SerializedName("picUrl")
        public String mPicUrl;

        @SerializedName("url")
        public String mUrl;
    }

    public News toNews() {
        News news = new News();
        news.mDesc = mNewsInfoJson.mDesc;
        news.mPicUrl = mNewsInfoJson.mPicUrl;
        news.mTime = mNewsInfoJson.mTime;
        news.mTitle = mNewsInfoJson.mTitle;
        news.mUrl = mNewsInfoJson.mUrl;
        return  news;
    }
}
