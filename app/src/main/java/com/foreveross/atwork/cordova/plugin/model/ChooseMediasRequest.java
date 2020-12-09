package com.foreveross.atwork.cordova.plugin.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by reyzhang22 on 17/2/21.
 */

public class ChooseMediasRequest extends ChooseFilesRequest {

    @SerializedName("editable")
    public boolean mEditable;

    @SerializedName("medias")
    public int mMedias;

    public ChooseMediasRequest() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(this.mEditable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mMedias);
    }

    protected ChooseMediasRequest(Parcel in) {
        super(in);
        this.mEditable = in.readByte() != 0;
        this.mMedias = in.readInt();
    }

    public static final Creator<ChooseMediasRequest> CREATOR = new Creator<ChooseMediasRequest>() {
        @Override
        public ChooseMediasRequest createFromParcel(Parcel source) {
            return new ChooseMediasRequest(source);
        }

        @Override
        public ChooseMediasRequest[] newArray(int size) {
            return new ChooseMediasRequest[size];
        }
    };
}
