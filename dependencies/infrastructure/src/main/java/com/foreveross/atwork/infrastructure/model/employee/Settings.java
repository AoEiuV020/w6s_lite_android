package com.foreveross.atwork.infrastructure.model.employee;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shadow on 2016/4/22.
 */
public class Settings implements Parcelable {


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public Settings() {
    }

    protected Settings(Parcel in) {
    }

    public static final Creator<Settings> CREATOR = new Creator<Settings>() {
        @Override
        public Settings createFromParcel(Parcel source) {
            return new Settings(source);
        }

        @Override
        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };
}
