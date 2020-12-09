package com.foreveross.atwork.infrastructure.model.organizationSetting;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/11/16.
 */

public class AdvertisementSettings implements Parcelable {

    @SerializedName("reawaken_enabled")
    public boolean mReAwakenEnabled;

    @SerializedName("reawaken_minutes")
    public int mReAwakenMinutes;

    @SerializedName("banner_interval_seconds")
    public long mAppTopBannerIntervalSeconds = 0;

    public AdvertisementSettings() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.mReAwakenEnabled ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mReAwakenMinutes);
        dest.writeLong(this.mAppTopBannerIntervalSeconds);
    }

    protected AdvertisementSettings(Parcel in) {
        this.mReAwakenEnabled = in.readByte() != 0;
        this.mReAwakenMinutes = in.readInt();
        this.mAppTopBannerIntervalSeconds = in.readLong();
    }

    public static final Creator<AdvertisementSettings> CREATOR = new Creator<AdvertisementSettings>() {
        @Override
        public AdvertisementSettings createFromParcel(Parcel source) {
            return new AdvertisementSettings(source);
        }

        @Override
        public AdvertisementSettings[] newArray(int size) {
            return new AdvertisementSettings[size];
        }
    };
}
