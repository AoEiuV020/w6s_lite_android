package com.foreveross.atwork.infrastructure.model.dropbox;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ShareItem implements Parcelable {

    @SerializedName("path")
    public String mPath;

    @SerializedName("type")
    public String mType;

    @SerializedName("state")
    public String mState;

    @SerializedName("id")
    public String mId;

    @SerializedName("downloads")
    public int mDownloads;

    @SerializedName("downloads_limit")
    public int mDownloadsLimit;

    @SerializedName("name")
    public String mName;

    @SerializedName("extension")
    public String mExtension;

    @SerializedName("size")
    public long mSize;

    @SerializedName("is_private")
    public boolean mIsPrivate;

    @SerializedName("thumbnail")
    public boolean mThumbnail;

    @SerializedName("modify_time")
    public long mModifyTime;

    @SerializedName("create_time")
    public long mCreateTime;

    @SerializedName("expire_time")
    public long mExpireTime;

    @SerializedName("password")
    public String mPassword;

    public boolean mIsTimeLine;

    public ShareItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPath);
        dest.writeString(this.mType);
        dest.writeString(this.mState);
        dest.writeString(this.mId);
        dest.writeInt(this.mDownloads);
        dest.writeInt(this.mDownloadsLimit);
        dest.writeString(this.mName);
        dest.writeString(this.mExtension);
        dest.writeLong(this.mSize);
        dest.writeByte(this.mIsPrivate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mThumbnail ? (byte) 1 : (byte) 0);
        dest.writeLong(this.mModifyTime);
        dest.writeLong(this.mExpireTime);
    }

    protected ShareItem(Parcel in) {
        this.mPath = in.readString();
        this.mType = in.readString();
        this.mState = in.readString();
        this.mId = in.readString();
        this.mDownloads = in.readInt();
        this.mDownloadsLimit = in.readInt();
        this.mName = in.readString();
        this.mExtension = in.readString();
        this.mSize = in.readLong();
        this.mIsPrivate = in.readByte() != 0;
        this.mThumbnail = in.readByte() != 0;
        this.mModifyTime = in.readLong();
        this.mExpireTime = in.readLong();
    }

    public static final Creator<ShareItem> CREATOR = new Creator<ShareItem>() {
        @Override
        public ShareItem createFromParcel(Parcel source) {
            return new ShareItem(source);
        }

        @Override
        public ShareItem[] newArray(int size) {
            return new ShareItem[size];
        }
    };


}
