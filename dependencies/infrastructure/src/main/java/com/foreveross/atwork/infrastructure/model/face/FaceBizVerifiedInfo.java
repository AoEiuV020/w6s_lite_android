package com.foreveross.atwork.infrastructure.model.face;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class FaceBizVerifiedInfo implements Parcelable {

    @SerializedName("user_id")
    public String mUserId;

    @SerializedName("domain_id")
    public String mDomainId;

//    0 : 成功
//    1 : 失败请重试
//    2 ：网络错误，请求不通face++
//   -1 ：账号已被锁定
    @SerializedName("face_status")
    public int mFaceStatus;

    @SerializedName("request_id")
    public String mRequestId;

    @SerializedName("biz_no")
    public String mBizNo;

    @SerializedName("time_used")
    public int mTimeUsed;

    @SerializedName("result_code")
    public String mResultCode;

    @SerializedName("result_message")
    public String mResultMsg;

    @SerializedName("error")
    public String mError;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUserId);
        dest.writeString(this.mDomainId);
        dest.writeInt(this.mFaceStatus);
        dest.writeString(this.mRequestId);
        dest.writeString(this.mBizNo);
        dest.writeInt(this.mTimeUsed);
        dest.writeString(this.mResultCode);
        dest.writeString(this.mResultMsg);
        dest.writeString(this.mError);
    }

    public FaceBizVerifiedInfo() {
    }

    protected FaceBizVerifiedInfo(Parcel in) {
        this.mUserId = in.readString();
        this.mDomainId = in.readString();
        this.mFaceStatus = in.readInt();
        this.mRequestId = in.readString();
        this.mBizNo = in.readString();
        this.mTimeUsed = in.readInt();
        this.mResultCode = in.readString();
        this.mResultMsg = in.readString();
        this.mError = in.readString();
    }

    public static final Parcelable.Creator<FaceBizVerifiedInfo> CREATOR = new Parcelable.Creator<FaceBizVerifiedInfo>() {
        @Override
        public FaceBizVerifiedInfo createFromParcel(Parcel source) {
            return new FaceBizVerifiedInfo(source);
        }

        @Override
        public FaceBizVerifiedInfo[] newArray(int size) {
            return new FaceBizVerifiedInfo[size];
        }
    };
}
