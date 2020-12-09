package com.foreveross.atwork.infrastructure.model.organizationSetting;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/11/16.
 */

public class MomentsSettings implements Parcelable {

    @SerializedName("disabled")
    public boolean mDisabled = true;

    @SerializedName("anonymous")
    public boolean mAnonymous;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.mDisabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mAnonymous ? (byte) 1 : (byte) 0);
    }

    public MomentsSettings() {
    }

    protected MomentsSettings(Parcel in) {
        this.mDisabled = in.readByte() != 0;
        this.mAnonymous = in.readByte() != 0;
    }

    public static final Creator<MomentsSettings> CREATOR = new Creator<MomentsSettings>() {
        @Override
        public MomentsSettings createFromParcel(Parcel source) {
            return new MomentsSettings(source);
        }

        @Override
        public MomentsSettings[] newArray(int size) {
            return new MomentsSettings[size];
        }
    };
}
