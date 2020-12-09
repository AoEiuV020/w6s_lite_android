package com.foreveross.atwork.api.sdk.sticker.requestJson;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CheckStickerRequest implements Parcelable {

    @SerializedName("album_ids")
    public List<String> mAlbumIds = new ArrayList<>();

    @SerializedName("refresh_time")
    public long mRefreshTime = -1;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.mAlbumIds);
        dest.writeLong(this.mRefreshTime);
    }

    public CheckStickerRequest() {
    }

    protected CheckStickerRequest(Parcel in) {
        this.mAlbumIds = in.createStringArrayList();
        this.mRefreshTime = in.readLong();
    }

    public static final Parcelable.Creator<CheckStickerRequest> CREATOR = new Parcelable.Creator<CheckStickerRequest>() {
        @Override
        public CheckStickerRequest createFromParcel(Parcel source) {
            return new CheckStickerRequest(source);
        }

        @Override
        public CheckStickerRequest[] newArray(int size) {
            return new CheckStickerRequest[size];
        }
    };
}
