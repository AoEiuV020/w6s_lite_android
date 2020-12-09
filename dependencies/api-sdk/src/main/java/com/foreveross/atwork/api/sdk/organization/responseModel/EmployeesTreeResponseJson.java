package com.foreveross.atwork.api.sdk.organization.responseModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class EmployeesTreeResponseJson extends BasicResponseJSON implements Parcelable {
    @SerializedName("result")
    public List<OrganizationResult> result = new ArrayList<>();


    public class Options {

    }



    /**
     * 可见范围
     */
    public enum VisibleRange {

        /**
         * ALL:所有可见
         * SENIOR:高管可见
         * SELF:自己可见
         * INVISIBLE:不可见
         */
        ALL, SENIOR, SELF, INVISIBLE
    }

    /**
     * 操作范围
     */
    public enum OpsRange {
        /**
         * BOTH:都可以修改
         * TERMINAL:仅手机端可改
         * ADMIN:仅管理员可改
         * FINAL:不可修改
         */
        BOTH, TERMINAL, ADMIN, FINAL
    }

    public EmployeesTreeResponseJson() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeString(this.message);
        dest.writeList(this.result);
    }

    protected EmployeesTreeResponseJson(Parcel in) {
        this.status = in.readInt();
        this.message = in.readString();
        this.result = new ArrayList<OrganizationResult>();
        in.readList(this.result, OrganizationResult.class.getClassLoader());
    }

    public static final Creator<EmployeesTreeResponseJson> CREATOR = new Creator<EmployeesTreeResponseJson>() {
        @Override
        public EmployeesTreeResponseJson createFromParcel(Parcel source) {
            return new EmployeesTreeResponseJson(source);
        }

        @Override
        public EmployeesTreeResponseJson[] newArray(int size) {
            return new EmployeesTreeResponseJson[size];
        }
    };
}
