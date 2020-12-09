package com.foreveross.atwork.api.sdk.auth.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.user.EndPoint;
import com.foreveross.atwork.infrastructure.shared.EndPointInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lingen on 15/4/9.
 * Description:
 * 登录
 */
public class LoginEndpointResponseJSON extends BasicResponseJSON {


    @SerializedName("result")
    public EndpointResult result;

    public boolean apiSuccess() {
        return status == 0;
    }

    public void saveToShared(Context context) {
        String host = result.sessionEndpoint.substring(9, result.sessionEndpoint.lastIndexOf(":"));
        int port = Integer.parseInt(result.sessionEndpoint.substring(result.sessionEndpoint.lastIndexOf(":") + 1));
        EndPoint endPoint = new EndPoint();
        endPoint.mSecret = result.sessionSecret;
        endPoint.mSessionHost = host;
        endPoint.mSessionPort = String.valueOf(port);
        endPoint.mSslEnabled = result.sslEnabled;
        endPoint.mSslVerify = result.sslVerify;
        EndPointInfo.getInstance().setEndpointInfo(context, endPoint);
    }

    private static class EndpointResult implements Parcelable {
        @SerializedName("session_endpoint")
        public String sessionEndpoint;

        @SerializedName("session_secret")
        public String sessionSecret;


        @SerializedName("ssl_enabled")
        public boolean sslEnabled;

        @SerializedName("ssl_verify")
        public boolean sslVerify;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.sessionEndpoint);
            dest.writeString(this.sessionSecret);
            dest.writeByte(this.sslEnabled ? (byte) 1 : (byte) 0);
            dest.writeByte(this.sslVerify ? (byte) 1 : (byte) 0);
        }

        public EndpointResult() {
        }

        protected EndpointResult(Parcel in) {
            this.sessionEndpoint = in.readString();
            this.sessionSecret = in.readString();
            this.sslEnabled = in.readByte() != 0;
            this.sslVerify = in.readByte() != 0;
        }

        public static final Creator<EndpointResult> CREATOR = new Creator<EndpointResult>() {
            @Override
            public EndpointResult createFromParcel(Parcel source) {
                return new EndpointResult(source);
            }

            @Override
            public EndpointResult[] newArray(int size) {
                return new EndpointResult[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeString(this.message);
        dest.writeParcelable(this.result, flags);
    }

    public LoginEndpointResponseJSON() {
    }

    protected LoginEndpointResponseJSON(Parcel in) {
        this.status = in.readInt();
        this.message = in.readString();
        this.result = in.readParcelable(EndpointResult.class.getClassLoader());
    }

    public static final Parcelable.Creator<LoginEndpointResponseJSON> CREATOR = new Parcelable.Creator<LoginEndpointResponseJSON>() {
        @Override
        public LoginEndpointResponseJSON createFromParcel(Parcel source) {
            return new LoginEndpointResponseJSON(source);
        }

        @Override
        public LoginEndpointResponseJSON[] newArray(int size) {
            return new LoginEndpointResponseJSON[size];
        }
    };
}
