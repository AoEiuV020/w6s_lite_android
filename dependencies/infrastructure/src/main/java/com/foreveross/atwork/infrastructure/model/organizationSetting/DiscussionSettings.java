package com.foreveross.atwork.infrastructure.model.organizationSetting;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/11/16.
 */

public class DiscussionSettings implements Parcelable {

    @SerializedName("owner")
    public String mOwner;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mOwner);
    }

    public DiscussionSettings() {
    }

    protected DiscussionSettings(Parcel in) {
        this.mOwner = in.readString();
    }

    public static final Creator<DiscussionSettings> CREATOR = new Creator<DiscussionSettings>() {
        @Override
        public DiscussionSettings createFromParcel(Parcel source) {
            return new DiscussionSettings(source);
        }

        @Override
        public DiscussionSettings[] newArray(int size) {
            return new DiscussionSettings[size];
        }
    };
}
