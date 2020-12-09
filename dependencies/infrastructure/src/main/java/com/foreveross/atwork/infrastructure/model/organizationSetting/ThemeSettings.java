package com.foreveross.atwork.infrastructure.model.organizationSetting;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/11/16.
 */

public class ThemeSettings implements Parcelable {
    @SerializedName("type")
    public String mType;

    @SerializedName("theme")
    public String mThemeName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mType);
        dest.writeString(this.mThemeName);
    }

    public ThemeSettings() {
    }

    protected ThemeSettings(Parcel in) {
        this.mType = in.readString();
        this.mThemeName = in.readString();
    }

    public static final Creator<ThemeSettings> CREATOR = new Creator<ThemeSettings>() {
        @Override
        public ThemeSettings createFromParcel(Parcel source) {
            return new ThemeSettings(source);
        }

        @Override
        public ThemeSettings[] newArray(int size) {
            return new ThemeSettings[size];
        }
    };
}
