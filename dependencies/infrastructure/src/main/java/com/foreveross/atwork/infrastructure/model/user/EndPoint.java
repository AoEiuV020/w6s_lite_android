package com.foreveross.atwork.infrastructure.model.user;/**
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
 */


import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

/**
 * 会话变量
 * Created by reyzhang22 on 16/4/7.
 */
public class EndPoint implements Parcelable {

    public String mSessionHost;

    public String mSessionPort;

    public String mSecret;

    public boolean mSslEnabled;

    public boolean mSslVerify;


    public String getSessionHostCheckConfig() {
        String imHost = BeeWorks.getInstance().config.imHost;
        if(!StringUtils.isEmpty(imHost)) {
            return imHost.split(":")[0];
        }

        return mSessionHost;
    }


    public String getSessionPortCheckConfig() {
        String imHost = BeeWorks.getInstance().config.imHost;
        if(!StringUtils.isEmpty(imHost)) {
            String[] infos = imHost.split(":");
            if(1 < infos.length) {
                return infos[1];
            }

        }

        return mSessionPort;
    }


    @Override
    public String toString() {
        return "EndPoint{" +
                "sessionHost='" + mSessionHost + '\'' +
                ", sessionPort='" + mSessionPort + '\'' +
                ", sslEnabled=" + mSslEnabled +
                ", sslVerify=" + mSslVerify +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mSessionHost);
        dest.writeString(this.mSessionPort);
        dest.writeString(this.mSecret);
        dest.writeByte(this.mSslEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mSslVerify ? (byte) 1 : (byte) 0);
    }

    public EndPoint() {
    }

    protected EndPoint(Parcel in) {
        this.mSessionHost = in.readString();
        this.mSessionPort = in.readString();
        this.mSecret = in.readString();
        this.mSslEnabled = in.readByte() != 0;
        this.mSslVerify = in.readByte() != 0;
    }

    public static final Creator<EndPoint> CREATOR = new Creator<EndPoint>() {
        @Override
        public EndPoint createFromParcel(Parcel source) {
            return new EndPoint(source);
        }

        @Override
        public EndPoint[] newArray(int size) {
            return new EndPoint[size];
        }
    };
}
