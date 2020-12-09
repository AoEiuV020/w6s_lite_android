package com.foreveross.atwork.infrastructure.model.orgization;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Department implements Parcelable, ShowListItem {

    @SerializedName("id")
    public String mId;

    @SerializedName("domain_id")
    public String mDomainId;

    @SerializedName("org_code")
    public String mOrgCode;

    @SerializedName("parent_org_id")
    public String mParentOrgId;

    @SerializedName("type")
    public String mType;

    @SerializedName("path")
    public String mPath;

    @SerializedName("name")
    public String mName;

    @SerializedName("sort_order")
    public int mSortOrder;

    @SerializedName("level")
    public String mLevel;

    @SerializedName("owner_id")
    public String mOwnerId;

    @SerializedName("owner_name")
    public String mOwnerName;

    @SerializedName("disabled")
    public String mDisabled;

    @SerializedName("employee_count")
    public int mEmployeeCount;

    @SerializedName("all_employee_count")
    public int mAllEmployeeCount;

    @SerializedName("employees")
    public List<Employee> mEmployees;

    @SerializedName("children")
    public List<Department> mChildren;

    @SerializedName("full_name_path")
    public String mFullNamePath;

    public Department() {
    }

    @Override
    public String getTitle() {
        return mName;
    }

    @Override
    public String getTitleI18n(Context context) {
        return mName;
    }

    @Override
    public String getTitlePinyin() {
        return "";
    }

    @Override
    public String getParticipantTitle() {
        return "";
    }

    @Override
    public String getInfo() {
        String info = mFullNamePath;
        if (info.startsWith("/")) {
            info = info.substring(1, info.length());
        }
        if (info.endsWith("/")) {
            info = info.substring(0, info.length() - 1);
        }
        info = info.replaceAll("/", "-");
        return info;
    }

    @Override
    public String getAvatar() {
        return mOrgCode;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public String getDomainId() {
        return mDomainId;
    }

    @Override
    public String getStatus() {
        return "";
    }

    @Override
    public boolean isSelect() {
        return false;
    }

    @Override
    public void select(boolean isSelect) {

    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mDomainId);
        dest.writeString(this.mOrgCode);
        dest.writeString(this.mParentOrgId);
        dest.writeString(this.mType);
        dest.writeString(this.mPath);
        dest.writeString(this.mName);
        dest.writeInt(this.mSortOrder);
        dest.writeString(this.mLevel);
        dest.writeString(this.mOwnerId);
        dest.writeString(this.mOwnerName);
        dest.writeString(this.mDisabled);
        dest.writeInt(this.mEmployeeCount);
        dest.writeInt(this.mAllEmployeeCount);
        dest.writeTypedList(this.mEmployees);
        dest.writeTypedList(this.mChildren);
        dest.writeString(this.mFullNamePath);
    }

    protected Department(Parcel in) {
        this.mId = in.readString();
        this.mDomainId = in.readString();
        this.mOrgCode = in.readString();
        this.mParentOrgId = in.readString();
        this.mType = in.readString();
        this.mPath = in.readString();
        this.mName = in.readString();
        this.mSortOrder = in.readInt();
        this.mLevel = in.readString();
        this.mOwnerId = in.readString();
        this.mOwnerName = in.readString();
        this.mDisabled = in.readString();
        this.mEmployeeCount = in.readInt();
        this.mAllEmployeeCount = in.readInt();
        this.mEmployees = in.createTypedArrayList(Employee.CREATOR);
        this.mChildren = in.createTypedArrayList(Department.CREATOR);
        this.mFullNamePath = in.readString();
    }

    public static final Creator<Department> CREATOR = new Creator<Department>() {
        @Override
        public Department createFromParcel(Parcel source) {
            return new Department(source);
        }

        @Override
        public Department[] newArray(int size) {
            return new Department[size];
        }
    };
}
