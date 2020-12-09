package com.foreveross.atwork.infrastructure.model.organizationSetting;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dasunsy on 2017/11/16.
 */

public class VpnSettings implements Parcelable {

    @SerializedName("id")
    public String mId = StringUtils.EMPTY;

    @SerializedName("name")
    public String mName = StringUtils.EMPTY;

    @SerializedName("chief")
    public boolean mChief;

    @SerializedName("type")
    public String mType;

    @SerializedName("host")
    public String mHost;

    @SerializedName("port")
    public int mPort;

    @SerializedName("credentials")
    public VpnCredentials mVpnCredentials;

    @SerializedName("certificate")
    public VpnCertificate mVpnCertificate;

    @SerializedName("white_lists")
    public List<String> mWhiteLists;

    @SerializedName("apps")
    public List<String> mApps;

    public VpnSettings() {
    }

    public boolean isSelected(String vpnSelectedId) {


        return mId.equalsIgnoreCase(vpnSelectedId);
    }


    public boolean canEdit() {
        if(VpnType.OPENVPN.toString().equalsIgnoreCase(mType)) {
            return false;
        }


        if(VpnCredentialType.OTHER.toString().equalsIgnoreCase(mVpnCredentials.mType)) {
            return true;
        }

        return false;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mName);
        dest.writeByte(this.mChief ? (byte) 1 : (byte) 0);
        dest.writeString(this.mType);
        dest.writeString(this.mHost);
        dest.writeInt(this.mPort);
        dest.writeParcelable(this.mVpnCredentials, flags);
        dest.writeParcelable(this.mVpnCertificate, flags);
        dest.writeStringList(this.mWhiteLists);
        dest.writeStringList(this.mApps);
    }

    protected VpnSettings(Parcel in) {
        this.mId = in.readString();
        this.mName = in.readString();
        this.mChief = in.readByte() != 0;
        this.mType = in.readString();
        this.mHost = in.readString();
        this.mPort = in.readInt();
        this.mVpnCredentials = in.readParcelable(VpnCredentials.class.getClassLoader());
        this.mVpnCertificate = in.readParcelable(VpnCertificate.class.getClassLoader());
        this.mWhiteLists = in.createStringArrayList();
        this.mApps = in.createStringArrayList();
    }

    public static final Creator<VpnSettings> CREATOR = new Creator<VpnSettings>() {
        @Override
        public VpnSettings createFromParcel(Parcel source) {
            return new VpnSettings(source);
        }

        @Override
        public VpnSettings[] newArray(int size) {
            return new VpnSettings[size];
        }
    };
}
