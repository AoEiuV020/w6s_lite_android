package com.foreveross.atwork.infrastructure.model.organizationSetting;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/11/16.
 */

public class VpnCredentials implements Parcelable {

    @SerializedName("type")
    public String mType;

    @SerializedName("username")
    public String mUsername;

    @SerializedName("password")
    public String mPassword;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mType);
        dest.writeString(this.mUsername);
        dest.writeString(this.mPassword);
    }

    public VpnCredentials() {
    }

    protected VpnCredentials(Parcel in) {
        this.mType = in.readString();
        this.mUsername = in.readString();
        this.mPassword = in.readString();
    }

    public static final Creator<VpnCredentials> CREATOR = new Creator<VpnCredentials>() {
        @Override
        public VpnCredentials createFromParcel(Parcel source) {
            return new VpnCredentials(source);
        }

        @Override
        public VpnCredentials[] newArray(int size) {
            return new VpnCredentials[size];
        }
    };
}

