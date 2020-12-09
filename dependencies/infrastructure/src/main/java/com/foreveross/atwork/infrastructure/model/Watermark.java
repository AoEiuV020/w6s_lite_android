package com.foreveross.atwork.infrastructure.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by reyzhang22 on 17/3/9.
 */

public class Watermark implements Parcelable {

    public String mSourceId;

    public Type mType;



    public enum Type {
        DISCUSSION {
            @Override
            public int toInt() {
                return 0;
            }

        },
        DROPBOX {
            @Override
            public int toInt() {
                return 1;
            }

        };

        public abstract int toInt();

        public static Type toType(int i) {
            if (i == 0) {
                return DISCUSSION;
            }
            return DROPBOX;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mSourceId);
        dest.writeInt(this.mType == null ? -1 : this.mType.ordinal());
    }

    public Watermark() {
    }

    public Watermark(String sourceId, Type type) {
        this.mSourceId = sourceId;
        this.mType = type;
    }

    protected Watermark(Parcel in) {
        this.mSourceId = in.readString();
        int tmpMType = in.readInt();
        this.mType = tmpMType == -1 ? null : Type.values()[tmpMType];
    }

    public static final Parcelable.Creator<Watermark> CREATOR = new Parcelable.Creator<Watermark>() {
        @Override
        public Watermark createFromParcel(Parcel source) {
            return new Watermark(source);
        }

        @Override
        public Watermark[] newArray(int size) {
            return new Watermark[size];
        }
    };
}
