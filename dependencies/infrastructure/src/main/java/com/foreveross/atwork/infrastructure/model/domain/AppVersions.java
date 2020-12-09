package com.foreveross.atwork.infrastructure.model.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 * Created by reyzhang22 on 16/7/11.
 */
public class AppVersions implements Parcelable {

    @SerializedName("domain_id")
    public String mDomainId;

    @SerializedName("platform")
    public String mPlatform;

    @SerializedName("pkg_name")
    public String mPkgName;

    @SerializedName("release")
    public String mRelease;

    @SerializedName("bundle")
    public String mBundle;

    @SerializedName("build_no")
    public int mBuildNo;

    @SerializedName("intro")
    public String mIntro;

    @SerializedName("forced_update")
    public boolean mForcedUpdate;

    @SerializedName("release_time")
    public long mReleaseTime;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDomainId);
        dest.writeString(this.mPlatform);
        dest.writeString(this.mPkgName);
        dest.writeString(this.mRelease);
        dest.writeString(this.mBundle);
        dest.writeInt(this.mBuildNo);
        dest.writeString(this.mIntro);
        dest.writeByte(this.mForcedUpdate ? (byte) 1 : (byte) 0);
        dest.writeLong(this.mReleaseTime);
    }

    public AppVersions() {
    }

    protected AppVersions(Parcel in) {
        this.mDomainId = in.readString();
        this.mPlatform = in.readString();
        this.mPkgName = in.readString();
        this.mRelease = in.readString();
        this.mBundle = in.readString();
        this.mBuildNo = in.readInt();
        this.mIntro = in.readString();
        this.mForcedUpdate = in.readByte() != 0;
        this.mReleaseTime = in.readLong();
    }

    public static final Parcelable.Creator<AppVersions> CREATOR = new Parcelable.Creator<AppVersions>() {
        @Override
        public AppVersions createFromParcel(Parcel source) {
            return new AppVersions(source);
        }

        @Override
        public AppVersions[] newArray(int size) {
            return new AppVersions[size];
        }
    };
}
