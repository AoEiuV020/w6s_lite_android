package com.foreveross.atwork.api.sdk.faceBio.facep.responseModel;

import android.os.Parcel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.face.FaceBizInfo;
import com.google.gson.annotations.SerializedName;

public class BizTokenResponse extends BasicResponseJSON {

    @SerializedName("result")
    public FaceBizInfo result = new FaceBizInfo();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.result, flags);
    }

    public BizTokenResponse() {
    }

    protected BizTokenResponse(Parcel in) {
        super(in);
        this.result = in.readParcelable(FaceBizInfo.class.getClassLoader());
    }

    public static final Creator<BizTokenResponse> CREATOR = new Creator<BizTokenResponse>() {
        @Override
        public BizTokenResponse createFromParcel(Parcel source) {
            return new BizTokenResponse(source);
        }

        @Override
        public BizTokenResponse[] newArray(int size) {
            return new BizTokenResponse[size];
        }
    };
}
