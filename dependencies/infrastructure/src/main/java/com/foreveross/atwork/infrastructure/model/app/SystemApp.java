package com.foreveross.atwork.infrastructure.model.app;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

/**
 * 系统应用 继承应用
 * 更新 by reyzhang22 on 16/4/11
 * Created by dasunsy on 15/10/29.
 */
public class SystemApp extends App {

    @SerializedName("target_url")
    public String mTargetUrl;


    public SystemApp() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mTargetUrl);
    }

    protected SystemApp(Parcel in) {
        super(in);
        this.mTargetUrl = in.readString();
    }

    public static final Creator<SystemApp> CREATOR = new Creator<SystemApp>() {
        @Override
        public SystemApp createFromParcel(Parcel source) {
            return new SystemApp(source);
        }

        @Override
        public SystemApp[] newArray(int size) {
            return new SystemApp[size];
        }
    };
}
