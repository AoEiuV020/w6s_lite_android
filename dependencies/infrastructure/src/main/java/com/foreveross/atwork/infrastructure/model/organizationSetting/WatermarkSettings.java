package com.foreveross.atwork.infrastructure.model.organizationSetting;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/11/16.
 */

public class WatermarkSettings implements Parcelable {

    @SerializedName("organization")
    public String mOrganization;

    @SerializedName("employee")
    public String mEmployee;


    public WatermarkSettings() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mOrganization);
        dest.writeString(this.mEmployee);
    }

    protected WatermarkSettings(Parcel in) {
        this.mOrganization = in.readString();
        this.mEmployee = in.readString();
    }

    public static final Creator<WatermarkSettings> CREATOR = new Creator<WatermarkSettings>() {
        @Override
        public WatermarkSettings createFromParcel(Parcel source) {
            return new WatermarkSettings(source);
        }

        @Override
        public WatermarkSettings[] newArray(int size) {
            return new WatermarkSettings[size];
        }
    };
}
