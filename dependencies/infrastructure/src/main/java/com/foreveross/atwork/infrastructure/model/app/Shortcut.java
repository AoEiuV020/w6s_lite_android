package com.foreveross.atwork.infrastructure.model.app;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.model.i18n.I18nInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.language.LanguageSupport;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

/**
 * Created by dasunsy on 2017/3/9.
 */

public class Shortcut extends I18nInfo implements Parcelable {


    @SerializedName("group")
    public int mGroupId;

    @SerializedName("sort_order")
    public int mSortOrder;

    @SerializedName("icon3x")
    public String mIcon;

    @SerializedName("app_id")
    public String mAppId;

    @SerializedName("title")
    public String mTitle;

    @SerializedName("en_name")
    public String mEnTitle;

    @SerializedName("tw_name")
    public String mTwTitle;

    @SerializedName("show_in_market")
    public boolean mShowInMarket;

    public boolean mClear = false;


    @Nullable
    @Override
    public String getStringName() {
        return mTitle;
    }

    @Nullable
    @Override
    public String getStringTwName() {
        return mTwTitle;
    }

    @Nullable
    @Override
    public String getStringEnName() {
        return mEnTitle;
    }

    public String getTitleI18n(Context context) {
        return getNameI18n(context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shortcut shortcut = (Shortcut) o;

        if (mGroupId != shortcut.mGroupId) return false;
        if (mSortOrder != shortcut.mSortOrder) return false;
        if (mShowInMarket != shortcut.mShowInMarket) return false;
        if (!mIcon.equals(shortcut.mIcon)) return false;
        if (!mAppId.equals(shortcut.mAppId)) return false;
        return mTitle.equals(shortcut.mTitle);

    }

    @Override
    public int hashCode() {
        int result = mGroupId;
        result = 31 * result + mSortOrder;
        result = 31 * result + mIcon.hashCode();
        result = 31 * result + mAppId.hashCode();
        result = 31 * result + mTitle.hashCode();
        result = 31 * result + (mShowInMarket ? 1 : 0);
        return result;
    }

    public Shortcut() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mGroupId);
        dest.writeInt(this.mSortOrder);
        dest.writeString(this.mIcon);
        dest.writeString(this.mAppId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mEnTitle);
        dest.writeString(this.mTwTitle);
        dest.writeByte(this.mShowInMarket ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mClear ? (byte) 1 : (byte) 0);
    }

    protected Shortcut(Parcel in) {
        this.mGroupId = in.readInt();
        this.mSortOrder = in.readInt();
        this.mIcon = in.readString();
        this.mAppId = in.readString();
        this.mTitle = in.readString();
        this.mEnTitle = in.readString();
        this.mTwTitle = in.readString();
        this.mShowInMarket = in.readByte() != 0;
        this.mClear = in.readByte() != 0;
    }

    public static final Creator<Shortcut> CREATOR = new Creator<Shortcut>() {
        @Override
        public Shortcut createFromParcel(Parcel source) {
            return new Shortcut(source);
        }

        @Override
        public Shortcut[] newArray(int size) {
            return new Shortcut[size];
        }
    };
}
