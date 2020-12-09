package com.foreveross.atwork.infrastructure.beeworks.share;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class QQShare implements Parcelable {

    @SerializedName("android")
    public BasicShare mShareAndroid;

    public QQShare() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mShareAndroid, flags);
    }

    protected QQShare(Parcel in) {
        this.mShareAndroid = in.readParcelable(BasicShare.class.getClassLoader());
    }

    public static final Creator<QQShare> CREATOR = new Creator<QQShare>() {
        @Override
        public QQShare createFromParcel(Parcel source) {
            return new QQShare(source);
        }

        @Override
        public QQShare[] newArray(int size) {
            return new QQShare[size];
        }
    };
}
