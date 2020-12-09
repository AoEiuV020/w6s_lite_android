package com.foreveross.atwork.api.sdk.startPage.model;

import android.os.Parcel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 15/12/14.
 */
public class CheckStartPageDataResponse extends BasicResponseJSON {

    @SerializedName("result")
    public Result result;

    public static class Result implements android.os.Parcelable {

        @SerializedName("disabled")
        public boolean disabled = false;

        @SerializedName("pkgId")
        public String pkgId;


        public Result() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(this.disabled ? (byte) 1 : (byte) 0);
            dest.writeString(this.pkgId);
        }

        protected Result(Parcel in) {
            this.disabled = in.readByte() != 0;
            this.pkgId = in.readString();
        }

        public static final Creator<Result> CREATOR = new Creator<Result>() {
            @Override
            public Result createFromParcel(Parcel source) {
                return new Result(source);
            }

            @Override
            public Result[] newArray(int size) {
                return new Result[size];
            }
        };
    }

    public boolean hasUp() {
        return null != result && !(StringUtils.isEmpty(result.pkgId));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.result, flags);
    }

    public CheckStartPageDataResponse() {
    }

    protected CheckStartPageDataResponse(Parcel in) {
        super(in);
        this.result = in.readParcelable(Result.class.getClassLoader());
    }

    public static final Creator<CheckStartPageDataResponse> CREATOR = new Creator<CheckStartPageDataResponse>() {
        @Override
        public CheckStartPageDataResponse createFromParcel(Parcel source) {
            return new CheckStartPageDataResponse(source);
        }

        @Override
        public CheckStartPageDataResponse[] newArray(int size) {
            return new CheckStartPageDataResponse[size];
        }
    };
}
