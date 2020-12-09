package com.foreveross.atwork.cordova.plugin.model;

import android.os.Parcel;

import com.foreveross.atwork.infrastructure.model.Employee;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by dasunsy on 16/6/29.
 */
public class GetEmployeeListRequestJson extends BasicSelectContactRequestJson {
    @SerializedName("selectedEmpList")
    public ArrayList<Employee> selectedEmpList;

    @SerializedName("hideMe")
    public boolean hideMe;

    @SerializedName("max")
    public int max = -1;

    public GetEmployeeListRequestJson() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.selectedEmpList);
        dest.writeByte(this.hideMe ? (byte) 1 : (byte) 0);
        dest.writeInt(this.max);
    }

    protected GetEmployeeListRequestJson(Parcel in) {
        super(in);
        this.selectedEmpList = in.createTypedArrayList(Employee.CREATOR);
        this.hideMe = in.readByte() != 0;
        this.max = in.readInt();
    }

    public static final Creator<GetEmployeeListRequestJson> CREATOR = new Creator<GetEmployeeListRequestJson>() {
        @Override
        public GetEmployeeListRequestJson createFromParcel(Parcel source) {
            return new GetEmployeeListRequestJson(source);
        }

        @Override
        public GetEmployeeListRequestJson[] newArray(int size) {
            return new GetEmployeeListRequestJson[size];
        }
    };
}
