package com.foreveross.atwork.api.sdk.dropbox.responseJson;

import android.os.Parcel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

public class ShareFileResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public Result mResult = new Result();

    public static class Result implements android.os.Parcelable {

        @SerializedName("name")
        public String mName;

        @SerializedName("private")
        public boolean mIsPrivate;

        @SerializedName("url")
        public String mShareUrl;

        @SerializedName("expire_after_minutes")
        public long mExpireAfterMinutes;

        @SerializedName("expire_after_hours")
        public long mExpireAfterHours;

        @SerializedName("expire_after_days")
        public long mExpireAfterDays;

        @SerializedName("limit")
        public int mLimit;

        @SerializedName("password")
        public String mPassword;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mName);
            dest.writeByte(this.mIsPrivate ? (byte) 1 : (byte) 0);
            dest.writeString(this.mShareUrl);
            dest.writeLong(this.mExpireAfterMinutes);
            dest.writeLong(this.mExpireAfterHours);
            dest.writeLong(this.mExpireAfterDays);
            dest.writeInt(this.mLimit);
            dest.writeString(this.mPassword);
        }

        public Result() {
        }

        protected Result(Parcel in) {
            this.mName = in.readString();
            this.mIsPrivate = in.readByte() != 0;
            this.mShareUrl = in.readString();
            this.mExpireAfterMinutes = in.readLong();
            this.mExpireAfterHours = in.readLong();
            this.mExpireAfterDays = in.readLong();
            this.mLimit = in.readInt();
            this.mPassword = in.readString();
        }

        public static final Creator<Result> CREATOR = new Creator<Result>() {
            @Override
            public Result createFromParcel(Parcel source) {
                return new Result(source);
            }

            @Override
            public Result[] newArray(int size) {
                return new Result[size];
            }
        };
    }
}
