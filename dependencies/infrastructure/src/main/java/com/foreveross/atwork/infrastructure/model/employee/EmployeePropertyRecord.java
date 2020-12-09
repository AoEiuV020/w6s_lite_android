package com.foreveross.atwork.infrastructure.model.employee;/**
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                       __           __
 .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 |________|_____|__|  |__|__|   __||__||_____|_____|
                            |__|
 */


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by reyzhang22 on 15/12/18.
 */
public class EmployeePropertyRecord implements Parcelable {


    @SerializedName("data_schema_id")
    public String mDataSchemaId;


    @SerializedName("name")
    public String mName;


    @SerializedName("alias")
    public String mAlias;


    @SerializedName("property")
    public String mProperty;

    @SerializedName("values")
    public String[] mValues;

    @SerializedName("value")
    public String mValue;

    public String mEmployeeId;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDataSchemaId);
        dest.writeString(this.mName);
        dest.writeString(this.mAlias);
        dest.writeString(this.mProperty);
        dest.writeStringArray(this.mValues);
        dest.writeString(this.mValue);
        dest.writeString(this.mEmployeeId);
    }

    public EmployeePropertyRecord() {
    }

    protected EmployeePropertyRecord(Parcel in) {
        this.mDataSchemaId = in.readString();
        this.mName = in.readString();
        this.mAlias = in.readString();
        this.mProperty = in.readString();
        this.mValues = in.createStringArray();
        this.mValue = in.readString();
        this.mEmployeeId = in.readString();
    }

    public static final Parcelable.Creator<EmployeePropertyRecord> CREATOR = new Parcelable.Creator<EmployeePropertyRecord>() {
        @Override
        public EmployeePropertyRecord createFromParcel(Parcel source) {
            return new EmployeePropertyRecord(source);
        }

        @Override
        public EmployeePropertyRecord[] newArray(int size) {
            return new EmployeePropertyRecord[size];
        }
    };
}
