package com.foreveross.atwork.api.sdk.sticker.responseJson;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class StickerAlbumData extends BasicResponseJSON implements Parcelable {

    @SerializedName("id")
    public String mId = StringUtils.EMPTY;

    @SerializedName("domain_id")
    public String mDomainId = StringUtils.EMPTY;

    @SerializedName("name")
    public String mName = StringUtils.EMPTY;

    @SerializedName("tw_name")
    public String mTwName = StringUtils.EMPTY;

    @SerializedName("en_name")
    public String mEnName = StringUtils.EMPTY;

    @SerializedName("icon")
    public String mIcon = StringUtils.EMPTY;

    @SerializedName("sort_order")
    public int mSortOrder = 1;

    @SerializedName("disabled")
    public boolean mDisabled = false;

    @SerializedName("total_size")
    public int mTotalSize = -1;

    @SerializedName("stickers")
    public List<StickerData> mStickers = new ArrayList<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mDomainId);
        dest.writeString(this.mName);
        dest.writeString(this.mTwName);
        dest.writeString(this.mEnName);
        dest.writeString(this.mIcon);
        dest.writeInt(this.mSortOrder);
        dest.writeByte(this.mDisabled ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mTotalSize);
        dest.writeTypedList(this.mStickers);
    }

    public StickerAlbumData() {
    }

    protected StickerAlbumData(Parcel in) {
        this.mId = in.readString();
        this.mDomainId = in.readString();
        this.mName = in.readString();
        this.mTwName = in.readString();
        this.mEnName = in.readString();
        this.mIcon = in.readString();
        this.mSortOrder = in.readInt();
        this.mDisabled = in.readByte() != 0;
        this.mTotalSize = in.readInt();
        this.mStickers = in.createTypedArrayList(StickerData.CREATOR);
    }

    public static final Parcelable.Creator<StickerAlbumData> CREATOR = new Parcelable.Creator<StickerAlbumData>() {
        @Override
        public StickerAlbumData createFromParcel(Parcel source) {
            return new StickerAlbumData(source);
        }

        @Override
        public StickerAlbumData[] newArray(int size) {
            return new StickerAlbumData[size];
        }
    };
}
