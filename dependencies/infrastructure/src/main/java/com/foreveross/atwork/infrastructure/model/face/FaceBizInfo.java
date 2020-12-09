package com.foreveross.atwork.infrastructure.model.face;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

public class FaceBizInfo implements Parcelable {

    @SerializedName("token")
    public String mToken = StringUtils.EMPTY;

    @SerializedName("request_id")
    public String mReqeustId = StringUtils.EMPTY;

    @SerializedName("domain_id")
    public String mDomainId = StringUtils.EMPTY;

    @SerializedName("biological_auth_enabled")
    public boolean mBiologicalAuthEnabled = true;

    public FaceBizInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mToken);
        dest.writeString(this.mReqeustId);
        dest.writeString(this.mDomainId);
        dest.writeByte(this.mBiologicalAuthEnabled ? (byte) 1 : (byte) 0);
    }

    protected FaceBizInfo(Parcel in) {
        this.mToken = in.readString();
        this.mReqeustId = in.readString();
        this.mDomainId = in.readString();
        this.mBiologicalAuthEnabled = in.readByte() != 0;
    }

    public static final Creator<FaceBizInfo> CREATOR = new Creator<FaceBizInfo>() {
        @Override
        public FaceBizInfo createFromParcel(Parcel source) {
            return new FaceBizInfo(source);
        }

        @Override
        public FaceBizInfo[] newArray(int size) {
            return new FaceBizInfo[size];
        }
    };
}
