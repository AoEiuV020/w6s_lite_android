package com.foreveross.atwork.infrastructure.model.user;
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
 * |__|
 */


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by reyzhang22 on 16/3/24.
 */
public class LoginToken implements Parcelable {

    @SerializedName("access_token")
    public String mAccessToken;

    @SerializedName("refresh_token")
    public String mRefreshToken;

    @SerializedName("issued_time")
    public String mIssuedTime;

    @SerializedName("expire_time")
    public long mExpireTime;

    @SerializedName("client_id")
    public String mClientId; //即 userId

    @SerializedName("initial_password")
    public boolean mNeedInitPwd = false;

    public LoginToken() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mAccessToken);
        dest.writeString(this.mRefreshToken);
        dest.writeString(this.mIssuedTime);
        dest.writeLong(this.mExpireTime);
        dest.writeString(this.mClientId);
        dest.writeByte(this.mNeedInitPwd ? (byte) 1 : (byte) 0);
    }

    protected LoginToken(Parcel in) {
        this.mAccessToken = in.readString();
        this.mRefreshToken = in.readString();
        this.mIssuedTime = in.readString();
        this.mExpireTime = in.readLong();
        this.mClientId = in.readString();
        this.mNeedInitPwd = in.readByte() != 0;
    }

    public static final Creator<LoginToken> CREATOR = new Creator<LoginToken>() {
        @Override
        public LoginToken createFromParcel(Parcel source) {
            return new LoginToken(source);
        }

        @Override
        public LoginToken[] newArray(int size) {
            return new LoginToken[size];
        }
    };
}
