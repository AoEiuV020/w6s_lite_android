package com.foreveross.atwork.infrastructure.beeworks.share;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class BasicShare implements Parcelable {

    @SerializedName("enabled")
    public boolean enable;

    @SerializedName("appId")
    public String appId;


    public BasicShare() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.enable ? (byte) 1 : (byte) 0);
        dest.writeString(this.appId);
    }

    protected BasicShare(Parcel in) {
        this.enable = in.readByte() != 0;
        this.appId = in.readString();
    }

    public static final Creator<BasicShare> CREATOR = new Creator<BasicShare>() {
        @Override
        public BasicShare createFromParcel(Parcel source) {
            return new BasicShare(source);
        }

        @Override
        public BasicShare[] newArray(int size) {
            return new BasicShare[size];
        }
    };
}
