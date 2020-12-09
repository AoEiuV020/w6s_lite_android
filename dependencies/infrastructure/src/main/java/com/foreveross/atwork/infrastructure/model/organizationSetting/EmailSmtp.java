package com.foreveross.atwork.infrastructure.model.organizationSetting;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/11/16.
 */

public class EmailSmtp implements Parcelable {

    @SerializedName("server")
    public String mServer;

    @SerializedName("port")
    public String mPort;

    @SerializedName("ssl")
    public boolean mSsl;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mServer);
        dest.writeString(this.mPort);
        dest.writeByte(this.mSsl ? (byte) 1 : (byte) 0);
    }

    public EmailSmtp() {
    }

    protected EmailSmtp(Parcel in) {
        this.mServer = in.readString();
        this.mPort = in.readString();
        this.mSsl = in.readByte() != 0;
    }

    public static final Creator<EmailSmtp> CREATOR = new Creator<EmailSmtp>() {
        @Override
        public EmailSmtp createFromParcel(Parcel source) {
            return new EmailSmtp(source);
        }

        @Override
        public EmailSmtp[] newArray(int size) {
            return new EmailSmtp[size];
        }
    };
}
