package com.foreveross.atwork.infrastructure.beeworks.share;

import android.os.Parcel;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;


public class WBShare extends BasicShare {

    @SerializedName("redirectURI")
    public String redirectURI  = StringUtils.EMPTY;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.redirectURI);
        dest.writeByte(this.enable ? (byte) 1 : (byte) 0);
        dest.writeString(this.appId);
    }

    public WBShare() {
    }

    protected WBShare(Parcel in) {
        super(in);
        this.redirectURI = in.readString();
        this.enable = in.readByte() != 0;
        this.appId = in.readString();
    }

    public static final Creator<WBShare> CREATOR = new Creator<WBShare>() {
        @Override
        public WBShare createFromParcel(Parcel source) {
            return new WBShare(source);
        }

        @Override
        public WBShare[] newArray(int size) {
            return new WBShare[size];
        }
    };
}