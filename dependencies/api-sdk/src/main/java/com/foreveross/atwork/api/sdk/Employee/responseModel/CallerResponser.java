package com.foreveross.atwork.api.sdk.Employee.responseModel;

import android.os.Parcel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;
import com.w6s.model.incomingCall.IncomingCaller;

import java.util.ArrayList;
import java.util.List;

/**
 * create by reyzhang22 at 2019-08-19
 */
public class CallerResponser extends BasicResponseJSON {

    @SerializedName("result")
    public Result mResult = new Result();

    public static class Result implements android.os.Parcelable {

        @SerializedName("employees")
        public List<IncomingCaller> mCaller = new ArrayList<>();

        @SerializedName("deletes")
        public List<String> mDeletes = new ArrayList<>();

        public Result() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(this.mCaller);
            dest.writeStringList(this.mDeletes);
        }

        protected Result(Parcel in) {
            this.mCaller = in.createTypedArrayList(IncomingCaller.CREATOR);
            this.mDeletes = in.createStringArrayList();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.mResult, flags);
    }

    public CallerResponser() {
    }

    protected CallerResponser(Parcel in) {
        super(in);
        this.mResult = in.readParcelable(Result.class.getClassLoader());
    }

    public static final Creator<CallerResponser> CREATOR = new Creator<CallerResponser>() {
        @Override
        public CallerResponser createFromParcel(Parcel source) {
            return new CallerResponser(source);
        }

        @Override
        public CallerResponser[] newArray(int size) {
            return new CallerResponser[size];
        }
    };
}
