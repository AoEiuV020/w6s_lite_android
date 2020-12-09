package com.foreveross.atwork.infrastructure.model.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wuzejie on 2020/1/7.
 */

public class EnvSettings implements Parcelable {

    @SerializedName("key")
    public String mKey;

    @SerializedName("value")
    public String mValue;

    public EnvSettings() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mKey);
        dest.writeString(this.mValue);

    }

    protected EnvSettings(Parcel in) {
        this.mKey = in.readString();
        this.mValue = in.readString();

    }

    public static final Creator<EnvSettings> CREATOR = new Creator<EnvSettings>() {
        @Override
        public EnvSettings createFromParcel(Parcel source) {
            return new EnvSettings(source);
        }

        @Override
        public EnvSettings[] newArray(int size) {
            return new EnvSettings[size];
        }
    };
}
