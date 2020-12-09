package com.foreveross.atwork.infrastructure.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.TimeZone;

/**
 * Created by reyzhang22 on 2017/12/14.
 */

public class LocalCalendar implements Parcelable {

    public long mId;

    public String mName;

    public String mDisplayName;

    public String mAccountName;

    public String mOwnerAccount;

    public String mLocation;

    public TimeZone mTimezone;

    public TimeZone mModifyTimezone;


    public LocalCalendar() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mName);
        dest.writeString(this.mDisplayName);
    }

    protected LocalCalendar(Parcel in) {
        this.mId = in.readLong();
        this.mName = in.readString();
        this.mDisplayName = in.readString();
    }

    public static final Creator<LocalCalendar> CREATOR = new Creator<LocalCalendar>() {
        @Override
        public LocalCalendar createFromParcel(Parcel source) {
            return new LocalCalendar(source);
        }

        @Override
        public LocalCalendar[] newArray(int size) {
            return new LocalCalendar[size];
        }
    };
}
