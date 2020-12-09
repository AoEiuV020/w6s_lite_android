package com.foreveross.atwork.api.sdk.sticker.responseJson;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class StickerAlbumList implements Parcelable {

    @SerializedName("add_list")
    public List<StickerAlbumData> mAddList = new ArrayList<>();

    @SerializedName("updated_list")
    public List<StickerAlbumData> mUpdateList = new ArrayList<>();

    @SerializedName("deleted_list")
    public List<String> mDeletedList = new ArrayList<>();

    @SerializedName("refresh_time")
    public long mRefreshTime = -1;

    public StickerAlbumList() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mAddList);
        dest.writeTypedList(this.mUpdateList);
        dest.writeStringList(this.mDeletedList);
        dest.writeLong(this.mRefreshTime);
    }

    protected StickerAlbumList(Parcel in) {
        this.mAddList = in.createTypedArrayList(StickerAlbumData.CREATOR);
        this.mUpdateList = in.createTypedArrayList(StickerAlbumData.CREATOR);
        this.mDeletedList = in.createStringArrayList();
        this.mRefreshTime = in.readLong();
    }

    public static final Creator<StickerAlbumList> CREATOR = new Creator<StickerAlbumList>() {
        @Override
        public StickerAlbumList createFromParcel(Parcel source) {
            return new StickerAlbumList(source);
        }

        @Override
        public StickerAlbumList[] newArray(int size) {
            return new StickerAlbumList[size];
        }
    };
}
