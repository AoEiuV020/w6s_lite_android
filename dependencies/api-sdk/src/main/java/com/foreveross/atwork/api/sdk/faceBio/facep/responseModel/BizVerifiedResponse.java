package com.foreveross.atwork.api.sdk.faceBio.facep.responseModel;

import android.os.Parcel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.face.FaceBizVerifiedInfo;
import com.google.gson.annotations.SerializedName;

public class BizVerifiedResponse extends BasicResponseJSON {

    @SerializedName("result")
    public FaceBizVerifiedInfo result = new FaceBizVerifiedInfo();


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.result, flags);
    }

    public BizVerifiedResponse() {
    }

    protected BizVerifiedResponse(Parcel in) {
        super(in);
        this.result = in.readParcelable(FaceBizVerifiedInfo.class.getClassLoader());
    }

    public static final Creator<BizVerifiedResponse> CREATOR = new Creator<BizVerifiedResponse>() {
        @Override
        public BizVerifiedResponse createFromParcel(Parcel source) {
            return new BizVerifiedResponse(source);
        }

        @Override
        public BizVerifiedResponse[] newArray(int size) {
            return new BizVerifiedResponse[size];
        }
    };
}
