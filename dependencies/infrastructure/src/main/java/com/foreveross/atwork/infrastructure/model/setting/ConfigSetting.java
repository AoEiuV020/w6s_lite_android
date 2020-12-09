package com.foreveross.atwork.infrastructure.model.setting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dasunsy on 2017/3/31.
 */

public class ConfigSetting implements Parcelable{

    public String mSourceId;

    public SourceType mSourceType;

    public BusinessCase mBusinessCase;

    public int mValue;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigSetting that = (ConfigSetting) o;

        if (!mSourceId.equals(that.mSourceId)) return false;
        return mBusinessCase == that.mBusinessCase;
    }

    @Override
    public int hashCode() {
        int result = mSourceId.hashCode();
        result = 31 * result + mBusinessCase.hashCode();
        return result;
    }

    public ConfigSetting() {
    }

    public ConfigSetting(String sourceId, SourceType sourceType, BusinessCase businessCase) {
        this.mSourceId = sourceId;
        this.mSourceType = sourceType;
        this.mBusinessCase = businessCase;
    }


    public boolean isOpen() {
        return 1 == mValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mSourceId);
        dest.writeInt(this.mSourceType == null ? -1 : this.mSourceType.ordinal());
        dest.writeInt(this.mBusinessCase == null ? -1 : this.mBusinessCase.ordinal());
        dest.writeInt(this.mValue);
    }

    protected ConfigSetting(Parcel in) {
        this.mSourceId = in.readString();
        int tmpMSourceType = in.readInt();
        this.mSourceType = tmpMSourceType == -1 ? null : SourceType.values()[tmpMSourceType];
        int tmpMBusinessCase = in.readInt();
        this.mBusinessCase = tmpMBusinessCase == -1 ? null : BusinessCase.values()[tmpMBusinessCase];
        this.mValue = in.readInt();
    }

    public static final Creator<ConfigSetting> CREATOR = new Creator<ConfigSetting>() {
        @Override
        public ConfigSetting createFromParcel(Parcel source) {
            return new ConfigSetting(source);
        }

        @Override
        public ConfigSetting[] newArray(int size) {
            return new ConfigSetting[size];
        }
    };
}
