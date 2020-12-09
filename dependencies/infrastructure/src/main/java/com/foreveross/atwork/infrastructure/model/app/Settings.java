package com.foreveross.atwork.infrastructure.model.app;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.model.app.appEnum.BannerType;
import com.foreveross.atwork.infrastructure.model.app.appEnum.DisplayMode;
import com.foreveross.atwork.infrastructure.model.app.appEnum.ProgressBarType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/7/19.
 */
public class Settings implements Parcelable {
    @SerializedName("MOBILE")
    public Behaviour mMobileBehaviour = new Behaviour();

    public static class Behaviour implements Parcelable {
        //全屏与非全屏
        @SerializedName("screen_mode")
        public DisplayMode mScreenMode;

        //横屏与竖屏
        @SerializedName("show_mode")
        public String mShowMode;

        @SerializedName("banner_prop")
        public String mBannerProp;

        @SerializedName("banner_type")
        public BannerType mBannerType;

        @SerializedName("progress_bar_color")
        public String mProgressBarColor;

        @SerializedName("progress_bar_type")
        public ProgressBarType mProgressBarType;

        @SerializedName("release")
        public String mRelease;

        public Behaviour() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mScreenMode == null ? -1 : this.mScreenMode.ordinal());
            dest.writeString(this.mShowMode);
            dest.writeString(this.mBannerProp);
            dest.writeInt(this.mBannerType == null ? -1 : this.mBannerType.ordinal());
            dest.writeString(this.mProgressBarColor);
            dest.writeInt(this.mProgressBarType == null ? -1 : this.mProgressBarType.ordinal());
            dest.writeString(this.mRelease);
        }

        protected Behaviour(Parcel in) {
            int tmpMScreenMode = in.readInt();
            this.mScreenMode = tmpMScreenMode == -1 ? null : DisplayMode.values()[tmpMScreenMode];
            this.mShowMode = in.readString();
            this.mBannerProp = in.readString();
            int tmpMBannerType = in.readInt();
            this.mBannerType = tmpMBannerType == -1 ? null : BannerType.values()[tmpMBannerType];
            this.mProgressBarColor = in.readString();
            int tmpMProgressBarType = in.readInt();
            this.mProgressBarType = tmpMProgressBarType == -1 ? null : ProgressBarType.values()[tmpMProgressBarType];
            this.mRelease = in.readString();
        }

        public static final Creator<Behaviour> CREATOR = new Creator<Behaviour>() {
            @Override
            public Behaviour createFromParcel(Parcel source) {
                return new Behaviour(source);
            }

            @Override
            public Behaviour[] newArray(int size) {
                return new Behaviour[size];
            }
        };
    }

    public Settings() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mMobileBehaviour, flags);
    }

    protected Settings(Parcel in) {
        this.mMobileBehaviour = in.readParcelable(Behaviour.class.getClassLoader());
    }

    public static final Creator<Settings> CREATOR = new Creator<Settings>() {
        @Override
        public Settings createFromParcel(Parcel source) {
            return new Settings(source);
        }

        @Override
        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };
}
