package com.foreveross.theme.model;

import com.google.gson.annotations.SerializedName;


public class Theme {


    @SerializedName("base_color")
    public BaseColor mBaseColor;

    @SerializedName("type")
    public ThemeType mType;

    @SerializedName("name")
    public String mName;

    @SerializedName("version")
    public int mVersion;

    @SerializedName("timestamp")
    public long mTimestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Theme theme = (Theme) o;

        if (mTimestamp != theme.mTimestamp) return false;
        if (mType != theme.mType) return false;
        return mName != null ? mName.equals(theme.mName) : theme.mName == null;

    }

    @Override
    public int hashCode() {
        int result = mType != null ? mType.hashCode() : 0;
        result = 31 * result + (mName != null ? mName.hashCode() : 0);
        result = 31 * result + (int) (mTimestamp ^ (mTimestamp >>> 32));
        return result;
    }
}
