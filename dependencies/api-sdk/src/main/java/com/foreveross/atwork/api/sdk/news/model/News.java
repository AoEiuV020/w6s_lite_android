package com.foreveross.atwork.api.sdk.news.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by reyzhang22 on 15/9/9.
 */
public class News implements Parcelable{

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

    public News(){

    }

    protected News(Parcel in) {
        mTime = in.readString();
        mTitle = in.readString();
        mDesc = in.readString();
        mPicUrl = in.readString();
        mUrl = in.readString();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTime);
        dest.writeString(mTitle);
        dest.writeString(mDesc);
        dest.writeString(mPicUrl);
        dest.writeString(mUrl);
    }
}
