package com.foreveross.atwork.api.sdk.organization.responseModel;

import android.os.Parcel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.orgization.Department;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchDepartmentResp extends BasicResponseJSON {

    @SerializedName("result")
    public List<Department> mResult;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.mResult);
    }

    public SearchDepartmentResp() {
    }

    protected SearchDepartmentResp(Parcel in) {
        super(in);
        this.mResult = in.createTypedArrayList(Department.CREATOR);
    }

    public static final Creator<SearchDepartmentResp> CREATOR = new Creator<SearchDepartmentResp>() {
        @Override
        public SearchDepartmentResp createFromParcel(Parcel source) {
            return new SearchDepartmentResp(source);
        }

        @Override
        public SearchDepartmentResp[] newArray(int size) {
            return new SearchDepartmentResp[size];
        }
    };
}
