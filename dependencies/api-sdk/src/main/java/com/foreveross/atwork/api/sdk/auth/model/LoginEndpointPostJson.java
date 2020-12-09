package com.foreveross.atwork.api.sdk.auth.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**
 * Created by dasunsy on 16/5/9.
 */
public class LoginEndpointPostJson implements Parcelable {
    @SerializedName("product_version")
    public String productVersion;
    @SerializedName("system_version")
    public String systemVersion = Build.VERSION.RELEASE;
    @SerializedName("system_model")
    public String systemModel = android.os.Build.MODEL;
    @SerializedName("system_name")
    public String systemName = "android";
    @SerializedName("locale")
    public Locale locale;
    @SerializedName("encrypt_type")
    public int encryptType;
    @SerializedName("voip_token")
    public String voipToken;
    @SerializedName("push_token")
    public String pushToken;
    @SerializedName("voip_enabled")
    public boolean voipEnable = true;
    @SerializedName("push_details")
    public boolean pushDetail = true;
    @SerializedName("push_enabled")
    public boolean pushEnable = true;
    @SerializedName("push_sound")
    public String pushSound;
    @SerializedName("channel_id")
    public String channelId;
    @SerializedName("channel_vendor")
    public String channelVendor;
    @SerializedName("route_push")
    public boolean mRoutePush = true;
    @SerializedName("channel_name")
    public String channelName = "im_push";

    public LoginEndpointPostJson() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.productVersion);
        dest.writeString(this.systemVersion);
        dest.writeString(this.systemModel);
        dest.writeString(this.systemName);
        dest.writeSerializable(this.locale);
        dest.writeInt(this.encryptType);
        dest.writeString(this.voipToken);
        dest.writeString(this.pushToken);
        dest.writeByte(this.voipEnable ? (byte) 1 : (byte) 0);
        dest.writeByte(this.pushDetail ? (byte) 1 : (byte) 0);
        dest.writeByte(this.pushEnable ? (byte) 1 : (byte) 0);
        dest.writeString(this.pushSound);
        dest.writeString(this.channelId);
        dest.writeString(this.channelVendor);
        dest.writeByte(this.mRoutePush ? (byte) 1 : (byte) 0);
        dest.writeString(this.channelName);
    }

    protected LoginEndpointPostJson(Parcel in) {
        this.productVersion = in.readString();
        this.systemVersion = in.readString();
        this.systemModel = in.readString();
        this.systemName = in.readString();
        this.locale = (Locale) in.readSerializable();
        this.encryptType = in.readInt();
        this.voipToken = in.readString();
        this.pushToken = in.readString();
        this.voipEnable = in.readByte() != 0;
        this.pushDetail = in.readByte() != 0;
        this.pushEnable = in.readByte() != 0;
        this.pushSound = in.readString();
        this.channelId = in.readString();
        this.channelVendor = in.readString();
        this.mRoutePush = in.readByte() != 0;
        this.channelName = in.readString();
    }

    public static final Creator<LoginEndpointPostJson> CREATOR = new Creator<LoginEndpointPostJson>() {
        @Override
        public LoginEndpointPostJson createFromParcel(Parcel source) {
            return new LoginEndpointPostJson(source);
        }

        @Override
        public LoginEndpointPostJson[] newArray(int size) {
            return new LoginEndpointPostJson[size];
        }
    };
}
