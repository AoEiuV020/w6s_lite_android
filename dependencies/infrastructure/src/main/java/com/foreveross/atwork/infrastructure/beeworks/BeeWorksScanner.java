package com.foreveross.atwork.infrastructure.beeworks;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class BeeWorksScanner implements Parcelable {

    public BeeWorksScanner() {

    }



    public static BeeWorksScanner createInstance(JSONObject jsonObject) {
        if (null == jsonObject) {
            return new BeeWorksScanner();
        }

        BeeWorksScanner beeWorksScanner = new Gson().fromJson(jsonObject.toString(), BeeWorksScanner.class);
        return beeWorksScanner;
    }

    @SerializedName("appCode")
    public String appCode;

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appCode);
    }

    protected BeeWorksScanner(Parcel in) {
        this.appCode = in.readString();
    }

    public static final Creator<BeeWorksScanner> CREATOR = new Creator<BeeWorksScanner>() {
        @Override
        public BeeWorksScanner createFromParcel(Parcel source) {
            return new BeeWorksScanner(source);
        }

        @Override
        public BeeWorksScanner[] newArray(int size) {
            return new BeeWorksScanner[size];
        }
    };
}
