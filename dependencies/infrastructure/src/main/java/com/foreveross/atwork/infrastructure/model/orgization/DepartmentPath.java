package com.foreveross.atwork.infrastructure.model.orgization;

import android.os.Parcel;
import android.os.Parcelable;

public class DepartmentPath implements Parcelable {

    public String mDepartmentPathName;

    public String mDepartmentPathId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDepartmentPathName);
        dest.writeString(this.mDepartmentPathId);
    }

    public DepartmentPath() {
    }

    protected DepartmentPath(Parcel in) {
        this.mDepartmentPathName = in.readString();
        this.mDepartmentPathId = in.readString();
    }

    public static final Parcelable.Creator<DepartmentPath> CREATOR = new Parcelable.Creator<DepartmentPath>() {
        @Override
        public DepartmentPath createFromParcel(Parcel source) {
            return new DepartmentPath(source);
        }

        @Override
        public DepartmentPath[] newArray(int size) {
            return new DepartmentPath[size];
        }
    };
}
