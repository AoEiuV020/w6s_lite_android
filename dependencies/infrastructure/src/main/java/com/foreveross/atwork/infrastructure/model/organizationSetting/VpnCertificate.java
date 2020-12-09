package com.foreveross.atwork.infrastructure.model.organizationSetting;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/11/15.
 */

public class VpnCertificate implements Parcelable {

    @SerializedName("media_id")
    public String mMediaId;

    @SerializedName("expire_time")
    public String mExpireTime;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMediaId);
        dest.writeString(this.mExpireTime);
    }

    public VpnCertificate() {
    }

    protected VpnCertificate(Parcel in) {
        this.mMediaId = in.readString();
        this.mExpireTime = in.readString();
    }

    public static final Creator<VpnCertificate> CREATOR = new Creator<VpnCertificate>() {
        @Override
        public VpnCertificate createFromParcel(Parcel source) {
            return new VpnCertificate(source);
        }

        @Override
        public VpnCertificate[] newArray(int size) {
            return new VpnCertificate[size];
        }
    };
}
