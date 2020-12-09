package com.foreveross.atwork.api.sdk.organization.responseModel;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class PositionResult implements Parcelable {

    @SerializedName("employee_id")
    public String employeeId;

    @SerializedName("org_id")
    public String orgId;

    @SerializedName("corp_name")
    public String corpName;

    @SerializedName("org_name")
    public String orgName;

    @SerializedName("job_title")
    public String jobTitle;

    @SerializedName("path")
    public String path;

    @SerializedName("type")
    public String type;

    @SerializedName("primary")
    public boolean primary;

    @SerializedName("chief")
    public boolean chief;

    @SerializedName("full_name_path")
    public String fullNamePath;

    public String convertFullNamePath() {
        String path = this.fullNamePath;
        if (TextUtils.isEmpty(path)) {
            return fullNamePath;
        }
        //截取前后反斜杠
        if (path.startsWith("/")) {
            path = path.substring(1, path.length());
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path.replaceAll("/", "-");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.employeeId);
        dest.writeString(this.orgId);
        dest.writeString(this.corpName);
        dest.writeString(this.orgName);
        dest.writeString(this.jobTitle);
        dest.writeString(this.path);
        dest.writeString(this.type);
        dest.writeByte(this.primary ? (byte) 1 : (byte) 0);
        dest.writeByte(this.chief ? (byte) 1 : (byte) 0);
        dest.writeString(this.fullNamePath);
    }

    public PositionResult() {
    }

    protected PositionResult(Parcel in) {
        this.employeeId = in.readString();
        this.orgId = in.readString();
        this.corpName = in.readString();
        this.orgName = in.readString();
        this.jobTitle = in.readString();
        this.path = in.readString();
        this.type = in.readString();
        this.primary = in.readByte() != 0;
        this.chief = in.readByte() != 0;
        this.fullNamePath = in.readString();
    }

    public static final Creator<PositionResult> CREATOR = new Creator<PositionResult>() {
        @Override
        public PositionResult createFromParcel(Parcel source) {
            return new PositionResult(source);
        }

        @Override
        public PositionResult[] newArray(int size) {
            return new PositionResult[size];
        }
    };
}
