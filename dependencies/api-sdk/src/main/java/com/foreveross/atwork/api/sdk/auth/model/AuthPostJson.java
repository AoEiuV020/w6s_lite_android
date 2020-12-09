package com.foreveross.atwork.api.sdk.auth.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 16/6/7.
 */
public class AuthPostJson implements Parcelable {
    public static final String PASSWORD_GRANT_TYPE = "password";

    public static final String USER_SCOPT = "user";

    @SerializedName(value = "device_id")
    public String deviceId = AtworkConfig.getDeviceId();

    @SerializedName(value = "grant_type")
    public String grantType = PASSWORD_GRANT_TYPE;

    @SerializedName(value = "client_secret")
    public String clientSecret;

    @SerializedName("scope")
    public String scope = USER_SCOPT;


    @SerializedName("client_secret_encrypt")
    public boolean clientSecretEncrypt = false;

    @SerializedName("captcha")
    public String secureCode;

    public String originalPassword;


    public AuthPostJson() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deviceId);
        dest.writeString(this.grantType);
        dest.writeString(this.clientSecret);
        dest.writeString(this.scope);
        dest.writeByte(this.clientSecretEncrypt ? (byte) 1 : (byte) 0);
        dest.writeString(this.secureCode);
    }

    protected AuthPostJson(Parcel in) {
        this.deviceId = in.readString();
        this.grantType = in.readString();
        this.clientSecret = in.readString();
        this.scope = in.readString();
        this.clientSecretEncrypt = in.readByte() != 0;
        this.secureCode = in.readString();
    }

    public static final Creator<AuthPostJson> CREATOR = new Creator<AuthPostJson>() {
        @Override
        public AuthPostJson createFromParcel(Parcel source) {
            return new AuthPostJson(source);
        }

        @Override
        public AuthPostJson[] newArray(int size) {
            return new AuthPostJson[size];
        }
    };
}
