package com.foreveross.atwork.cordova.plugin.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dasunsy on 2017/5/18.
 */

public class ChooseFilesRequest implements Parcelable {

    @SerializedName("multiple")
    public boolean mMultiple = false;

    @SerializedName("file_limit")
    public FileLimit mFileLimit = new FileLimit();

    @SerializedName("file_keys")
    public List<String> mFileKeys;

    public boolean mFromCordova;


    public boolean isSingleType() {
        return !mMultiple || null != mFileLimit && 1 == mFileLimit.mMaxSelectCount;
    }

    public boolean checkLegal() {
        //非法的传参都使用默认值
        if(0 >= mFileLimit.mMaxSelectCount) {
            mFileLimit.mMaxSelectCount = 9;
        }

        if(0 >= mFileLimit.mSingleSelectSize) {
            mFileLimit.mSingleSelectSize = -1;
        }

        if(0 >= mFileLimit.mTotalSelectSize) {
            mFileLimit.mTotalSelectSize = -1;
        }

        return mFileLimit.mTotalSelectSize >= mFileLimit.mSingleSelectSize;
    }

    public static class FileLimit implements Parcelable {

        @SerializedName("max_select_count")
        public int mMaxSelectCount = 9;

        @SerializedName("single_select_size")
        public long mSingleSelectSize = -1;


        @SerializedName("total_select_size")
        public long mTotalSelectSize = -1;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mMaxSelectCount);
            dest.writeLong(this.mSingleSelectSize);
            dest.writeLong(this.mTotalSelectSize);
        }

        public FileLimit() {
        }

        protected FileLimit(Parcel in) {
            this.mMaxSelectCount = in.readInt();
            this.mSingleSelectSize = in.readLong();
            this.mTotalSelectSize = in.readLong();
        }

        public static final Creator<FileLimit> CREATOR = new Creator<FileLimit>() {
            @Override
            public FileLimit createFromParcel(Parcel source) {
                return new FileLimit(source);
            }

            @Override
            public FileLimit[] newArray(int size) {
                return new FileLimit[size];
            }
        };
    }

    public ChooseFilesRequest() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.mMultiple ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.mFileLimit, flags);
        dest.writeStringList(this.mFileKeys);
        dest.writeByte(this.mFromCordova ? (byte) 1 : (byte) 0);
    }

    protected ChooseFilesRequest(Parcel in) {
        this.mMultiple = in.readByte() != 0;
        this.mFileLimit = in.readParcelable(FileLimit.class.getClassLoader());
        this.mFileKeys = in.createStringArrayList();
        this.mFromCordova = in.readByte() != 0;
    }

    public static final Creator<ChooseFilesRequest> CREATOR = new Creator<ChooseFilesRequest>() {
        @Override
        public ChooseFilesRequest createFromParcel(Parcel source) {
            return new ChooseFilesRequest(source);
        }

        @Override
        public ChooseFilesRequest[] newArray(int size) {
            return new ChooseFilesRequest[size];
        }
    };
}
