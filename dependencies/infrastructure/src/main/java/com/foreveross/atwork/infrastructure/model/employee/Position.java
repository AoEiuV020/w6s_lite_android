package com.foreveross.atwork.infrastructure.model.employee;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Position implements Parcelable {

    @SerializedName("employee_id")
    public String employeeId;

    @SerializedName("org_id")
    public String orgId;

    @SerializedName("org_name")
    public String orgName;

    @SerializedName("corp_name")
    public String corpName;

    @Expose
    @SerializedName("job_title")
    public String jobTitle;

    @Expose
    @SerializedName("path")
    public String path;

    @SerializedName("type")
    public String type;

    @SerializedName("primary")
    public boolean primary;

    @SerializedName("chief")
    public boolean chief;

    @Expose
    @SerializedName("display_nodes")
    public List<PositionDisplayNode> displayNodes;

    @SerializedName("full_name_path")
    public String fullNamePath = "";


    public Position() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.employeeId);
        dest.writeString(this.orgId);
        dest.writeString(this.orgName);
        dest.writeString(this.corpName);
        dest.writeString(this.jobTitle);
        dest.writeString(this.path);
        dest.writeString(this.type);
        dest.writeByte(this.primary ? (byte) 1 : (byte) 0);
        dest.writeByte(this.chief ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.displayNodes);
        dest.writeString(this.fullNamePath);
    }

    protected Position(Parcel in) {
        this.employeeId = in.readString();
        this.orgId = in.readString();
        this.orgName = in.readString();
        this.corpName = in.readString();
        this.jobTitle = in.readString();
        this.path = in.readString();
        this.type = in.readString();
        this.primary = in.readByte() != 0;
        this.chief = in.readByte() != 0;
        this.displayNodes = in.createTypedArrayList(PositionDisplayNode.CREATOR);
        this.fullNamePath = in.readString();
    }

    public static final Creator<Position> CREATOR = new Creator<Position>() {
        @Override
        public Position createFromParcel(Parcel source) {
            return new Position(source);
        }

        @Override
        public Position[] newArray(int size) {
            return new Position[size];
        }
    };
}
