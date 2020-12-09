package com.foreveross.atwork.api.sdk.dropbox.responseJson;

import android.os.Parcel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.dropbox.ShareItem;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ShareItemsRespJson extends BasicResponseJSON {

    @SerializedName("result")
    public Result mResult = new Result();

    public static class Result implements android.os.Parcelable {

        @SerializedName("total_count")
        public int mTotalCount;

        @SerializedName("items")
        public List<ShareItem> mShareItemList = new ArrayList<>();

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mTotalCount);
            dest.writeList(this.mShareItemList);
        }

        public Result() {
        }

        protected Result(Parcel in) {
            this.mTotalCount = in.readInt();
            this.mShareItemList = new ArrayList<ShareItem>();
            in.readList(this.mShareItemList, ShareItem.class.getClassLoader());
        }

        public static final Creator<Result> CREATOR = new Creator<Result>() {
            @Override
            public Result createFromParcel(Parcel source) {
                return new Result(source);
            }

            @Override
            public Result[] newArray(int size) {
                return new Result[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.mResult, flags);
    }

    public ShareItemsRespJson() {
    }

    protected ShareItemsRespJson(Parcel in) {
        super(in);
        this.mResult = in.readParcelable(Result.class.getClassLoader());
    }

    public static final Creator<ShareItemsRespJson> CREATOR = new Creator<ShareItemsRespJson>() {
        @Override
        public ShareItemsRespJson createFromParcel(Parcel source) {
            return new ShareItemsRespJson(source);
        }

        @Override
        public ShareItemsRespJson[] newArray(int size) {
            return new ShareItemsRespJson[size];
        }
    };
}
