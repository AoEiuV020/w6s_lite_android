package com.foreveross.atwork.api.sdk.sticker.responseJson;

import android.os.Parcel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

public class StickerAlbumResult extends BasicResponseJSON {

    @SerializedName("result")
    public StickerAlbumList mStickerAlbumList;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.mStickerAlbumList, flags);
    }

    public StickerAlbumResult() {
    }

    protected StickerAlbumResult(Parcel in) {
        super(in);
        this.mStickerAlbumList = in.readParcelable(StickerAlbumList.class.getClassLoader());
    }

    public static final Creator<StickerAlbumResult> CREATOR = new Creator<StickerAlbumResult>() {
        @Override
        public StickerAlbumResult createFromParcel(Parcel source) {
            return new StickerAlbumResult(source);
        }

        @Override
        public StickerAlbumResult[] newArray(int size) {
            return new StickerAlbumResult[size];
        }
    };
}
