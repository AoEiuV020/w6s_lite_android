package com.foreveross.atwork.infrastructure.model.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class AppProfile implements Parcelable {

    @SerializedName("profile_id")
    public ProfileId mProfileId;

    @SerializedName("profile")
    public String mProfile;

    @SerializedName("name")
    public String mName;

    @SerializedName("icon")
    public String mIcon;

    @SerializedName("android_url")
    public String mAndroidUrl;

    @SerializedName("download_url")
    public String mDownloadUrl;

    @SerializedName("primary")
    public boolean mPrimary;

    @SerializedName("disabled")
    public boolean mDisabled;



    public static class ProfileId implements Parcelable {
        @SerializedName("domain_id")
        public String mDomainId;

        @SerializedName("id")
        public String mId;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mDomainId);
            dest.writeString(this.mId);
        }

        public ProfileId() {
        }

        protected ProfileId(Parcel in) {
            this.mDomainId = in.readString();
            this.mId = in.readString();
        }

        public static final Parcelable.Creator<ProfileId> CREATOR = new Parcelable.Creator<ProfileId>() {
            @Override
            public ProfileId createFromParcel(Parcel source) {
                return new ProfileId(source);
            }

            @Override
            public ProfileId[] newArray(int size) {
                return new ProfileId[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mProfileId, flags);
        dest.writeString(this.mProfile);
        dest.writeString(this.mName);
        dest.writeString(this.mIcon);
        dest.writeString(this.mAndroidUrl);
        dest.writeString(this.mDownloadUrl);
        dest.writeByte(this.mPrimary ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mDisabled ? (byte) 1 : (byte) 0);
    }

    public AppProfile() {
    }

    protected AppProfile(Parcel in) {
        this.mProfileId = in.readParcelable(ProfileId.class.getClassLoader());
        this.mProfile = in.readString();
        this.mName = in.readString();
        this.mIcon = in.readString();
        this.mAndroidUrl = in.readString();
        this.mDownloadUrl = in.readString();
        this.mPrimary = in.readByte() != 0;
        this.mDisabled = in.readByte() != 0;
    }

    public static final Parcelable.Creator<AppProfile> CREATOR = new Parcelable.Creator<AppProfile>() {
        @Override
        public AppProfile createFromParcel(Parcel source) {
            return new AppProfile(source);
        }

        @Override
        public AppProfile[] newArray(int size) {
            return new AppProfile[size];
        }
    };
}
