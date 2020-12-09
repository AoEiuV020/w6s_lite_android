package com.foreveross.atwork.api.sdk.faceBio.facep.requestModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class BizVerifyRequest implements Parcelable {

    @SerializedName("biz_token")
    public String mBizToken;

    @SerializedName("meglive_data")
    public String mMegliveData;

//    @SerializedName("task_id")
//    public String mTaskId;

    public BizVerifyRequest() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mBizToken);
        dest.writeString(this.mMegliveData);
    }

    protected BizVerifyRequest(Parcel in) {
        this.mBizToken = in.readString();
        this.mMegliveData = in.readString();
    }

    public static final Creator<BizVerifyRequest> CREATOR = new Creator<BizVerifyRequest>() {
        @Override
        public BizVerifyRequest createFromParcel(Parcel source) {
            return new BizVerifyRequest(source);
        }

        @Override
        public BizVerifyRequest[] newArray(int size) {
            return new BizVerifyRequest[size];
        }
    };
}
