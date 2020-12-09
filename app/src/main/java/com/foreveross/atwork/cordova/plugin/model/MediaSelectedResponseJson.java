package com.foreveross.atwork.cordova.plugin.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/7/4.
 */
public class MediaSelectedResponseJson implements Parcelable {
    @Expose
    @SerializedName("key")
    public String key;
    @Expose
    @SerializedName("imageURL")
    public String imageURL;

    @Expose
    @SerializedName("videoURL")
    public String videoURL;


    @Expose
    @SerializedName("imageInfo")
    public ImageInfo imageInfo;

    @SerializedName("mediaId")
    public String mediaId;

    @SerializedName("name")
    public String name = "";

    public String getFilePath() {
        String filePath = videoURL;
        if(StringUtils.isEmpty(filePath)) {
            filePath = imageURL;
        }

        if(StringUtils.isEmpty(filePath)) {
            filePath = key;
        }

        return filePath;
    }

    public static class ImageInfo implements Parcelable{
        @Expose
        @SerializedName("height")
        public int height = -1;

        @Expose
        @SerializedName("width")
        public int width = -1;

        @Expose
        @SerializedName("size")
        public long size = -1;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.height);
            dest.writeInt(this.width);
            dest.writeLong(this.size);
        }

        public ImageInfo() {
        }

        protected ImageInfo(Parcel in) {
            this.height = in.readInt();
            this.width = in.readInt();
            this.size = in.readLong();
        }

        public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
            @Override
            public ImageInfo createFromParcel(Parcel source) {
                return new ImageInfo(source);
            }

            @Override
            public ImageInfo[] newArray(int size) {
                return new ImageInfo[size];
            }
        };
    }

    public MediaSelectedResponseJson() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.imageURL);
        dest.writeString(this.videoURL);
        dest.writeParcelable(this.imageInfo, flags);
        dest.writeString(this.mediaId);
        dest.writeString(this.name);
    }

    protected MediaSelectedResponseJson(Parcel in) {
        this.key = in.readString();
        this.imageURL = in.readString();
        this.videoURL = in.readString();
        this.imageInfo = in.readParcelable(ImageInfo.class.getClassLoader());
        this.mediaId = in.readString();
        this.name = in.readString();
    }

    public static final Creator<MediaSelectedResponseJson> CREATOR = new Creator<MediaSelectedResponseJson>() {
        @Override
        public MediaSelectedResponseJson createFromParcel(Parcel source) {
            return new MediaSelectedResponseJson(source);
        }

        @Override
        public MediaSelectedResponseJson[] newArray(int size) {
            return new MediaSelectedResponseJson[size];
        }
    };
}
