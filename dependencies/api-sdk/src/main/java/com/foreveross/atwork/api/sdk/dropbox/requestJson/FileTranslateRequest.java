package com.foreveross.atwork.api.sdk.dropbox.requestJson;

import android.os.Parcel;
import android.os.Parcelable;

public class FileTranslateRequest implements Parcelable {

    public String mMediaId;

    public String mFileType;

    public int mSkip = 0;

    public int mLimit = 5;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMediaId);
        dest.writeString(this.mFileType);
        dest.writeInt(this.mSkip);
        dest.writeInt(this.mLimit);
    }

    public FileTranslateRequest() {
    }

    protected FileTranslateRequest(Parcel in) {
        this.mMediaId = in.readString();
        this.mFileType = in.readString();
        this.mSkip = in.readInt();
        this.mLimit = in.readInt();
    }

    public static final Parcelable.Creator<FileTranslateRequest> CREATOR = new Parcelable.Creator<FileTranslateRequest>() {
        @Override
        public FileTranslateRequest createFromParcel(Parcel source) {
            return new FileTranslateRequest(source);
        }

        @Override
        public FileTranslateRequest[] newArray(int size) {
            return new FileTranslateRequest[size];
        }
    };
}
