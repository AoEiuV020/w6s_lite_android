package com.foreveross.atwork.api.sdk.auth.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lingen on 15/4/9.
 * Description:
 * 登录请求认证的参数MODEL
 */
public class LoginTokenJSON implements Parcelable {

    public static final String PASSWORD_GRANT_TYPE = "password";

    public static final String USER_SCOPT = "user";

    @Expose
    @SerializedName(value = "grant_type")
    public String grantType = PASSWORD_GRANT_TYPE;

    @Expose
    @SerializedName(value = "scope")
    public String scope = USER_SCOPT;

    @Expose
    @SerializedName(value = "domain_id")
    public String tenantId = AtworkConfig.DOMAIN_ID;

    @Expose
    @SerializedName(value = "device_platform")
    public String devicePlatform = AtworkConfig.ANDROID_PLATFORM;

    @Expose
    @SerializedName(value = "client_id")
    public String clientId;

    @Expose
    @SerializedName(value = "client_secret")
    public String clientSecret;

    @Expose
    @SerializedName("captcha")
    public String secureCode;

    @Expose
    @SerializedName(value = "device_id")
    public String deviceId = AtworkConfig.getDeviceId();

    @Expose
    @SerializedName("product_version")
    public String productVersion;

    @Expose
    @SerializedName("system_version")
    public String systemVersion = Build.VERSION.RELEASE;

    @Expose
    @SerializedName("system_model")
    public String systemModel = android.os.Build.MODEL;

    @Expose
    @SerializedName("system_name")
    public String systemName = "android";

    @Expose
    @SerializedName("device_authenticate")
    public boolean deviceAuthenticate = true;

    @Expose
    @SerializedName("device_name")
    public String deviceName;

    @Expose
    @SerializedName("device_system_info")
    public String deviceSystemInfo;

    @Expose
    @SerializedName("channel_id")
    public String channelId;

    @Expose
    @SerializedName("channel_vendor")
    public String channelVendor;

    @Expose
    @SerializedName("channel_name")
    public String channelName = "im_push";

    @Expose
    @SerializedName("client_secret_encrypt")
    public boolean clientSecretEncrypt = false;

    @Expose
    @SerializedName("device_silently")
    public boolean silently = true;

//    @SerializedName("device_settings")
//    public DeviceSettings mDeviceSettings;

    public String originalPassword = "";


    public LoginTokenJSON() {
        String romChannel = RomUtil.getRomChannel();
        if (!StringUtils.isEmpty(romChannel)) {
            channelVendor = romChannel;
            channelId = AppUtil.getPackageName(BaseApplicationLike.baseContext);
            productVersion = AppUtil.getVersionName(BaseApplicationLike.baseContext);

        }

    }


    public static class DeviceSettings {

        @SerializedName("push_token")
        public String mPushToken;

        @SerializedName("push_sound")
        public String mPushSound ;

        @SerializedName("push_enabled")
        public boolean mPushEnable = true;

        @SerializedName("voip_token")
        public String mVoipToken;

        @SerializedName("voip_enabled")
        public boolean mVoipEnable = true;

        @SerializedName("push_details")
        public boolean mPushDetails = true;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.grantType);
        dest.writeString(this.scope);
        dest.writeString(this.tenantId);
        dest.writeString(this.devicePlatform);
        dest.writeString(this.clientId);
        dest.writeString(this.clientSecret);
        dest.writeString(this.secureCode);
        dest.writeString(this.deviceId);
        dest.writeString(this.productVersion);
        dest.writeString(this.systemVersion);
        dest.writeString(this.systemModel);
        dest.writeString(this.deviceName);
        dest.writeString(this.deviceSystemInfo);
        dest.writeString(this.channelId);
        dest.writeString(this.channelVendor);
        dest.writeByte(this.clientSecretEncrypt ? (byte) 1 : (byte) 0);
    }

    protected LoginTokenJSON(Parcel in) {
        this.grantType = in.readString();
        this.scope = in.readString();
        this.tenantId = in.readString();
        this.devicePlatform = in.readString();
        this.clientId = in.readString();
        this.clientSecret = in.readString();
        this.secureCode = in.readString();
        this.deviceId = in.readString();
        this.productVersion = in.readString();
        this.systemVersion = in.readString();
        this.systemModel = in.readString();
        this.deviceName = in.readString();
        this.deviceSystemInfo = in.readString();
        this.channelId = in.readString();
        this.channelVendor = in.readString();
        this.clientSecretEncrypt = in.readByte() != 0;
    }

    public static final Creator<LoginTokenJSON> CREATOR = new Creator<LoginTokenJSON>() {
        @Override
        public LoginTokenJSON createFromParcel(Parcel source) {
            return new LoginTokenJSON(source);
        }

        @Override
        public LoginTokenJSON[] newArray(int size) {
            return new LoginTokenJSON[size];
        }
    };
}
