package com.foreveross.atwork.cordova.plugin.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/8/9.
 */

public class BasicSelectContactRequestJson implements Parcelable {
    @SerializedName("filterSenior")
    public int mFilterSenior = 1;

    public BasicSelectContactRequestJson() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mFilterSenior);
    }

    protected BasicSelectContactRequestJson(Parcel in) {
        this.mFilterSenior = in.readInt();
    }

    public static final Creator<BasicSelectContactRequestJson> CREATOR = new Creator<BasicSelectContactRequestJson>() {
        @Override
        public BasicSelectContactRequestJson createFromParcel(Parcel source) {
            return new BasicSelectContactRequestJson(source);
        }

        @Override
        public BasicSelectContactRequestJson[] newArray(int size) {
            return new BasicSelectContactRequestJson[size];
        }
    };
}
