package com.foreveross.atwork.api.sdk.dropbox.requestJson;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ShareFileRequestJson implements Parcelable {

    @SerializedName("id")
    public String mId = "";

    @SerializedName("limit")
    public int mLimit = 10;

    @SerializedName("type")
    public String mType;

    @SerializedName("expire_after_days")
    public int mExpireAfterDays = 0;

    @SerializedName("password")
    public String mPassword= null;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeInt(this.mLimit);
        dest.writeString(this.mType);
        dest.writeInt(this.mExpireAfterDays);
        dest.writeString(this.mPassword);
    }

    public ShareFileRequestJson() {
    }

    protected ShareFileRequestJson(Parcel in) {
        this.mId = in.readString();
        this.mLimit = in.readInt();
        this.mType = in.readString();
        this.mExpireAfterDays = in.readInt();
        this.mPassword = in.readString();
    }

    public static final Parcelable.Creator<ShareFileRequestJson> CREATOR = new Parcelable.Creator<ShareFileRequestJson>() {
        @Override
        public ShareFileRequestJson createFromParcel(Parcel source) {
            return new ShareFileRequestJson(source);
        }

        @Override
        public ShareFileRequestJson[] newArray(int size) {
            return new ShareFileRequestJson[size];
        }
    };
}
