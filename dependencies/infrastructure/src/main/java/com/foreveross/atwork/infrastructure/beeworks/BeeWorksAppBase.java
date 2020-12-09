package com.foreveross.atwork.infrastructure.beeworks;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by lingen on 15/12/21.
 */
public class BeeWorksAppBase implements Parcelable {

    @SerializedName("bundleId")
    public String mBundleId;

    @SerializedName("appName")
    public String mAppName;

    @SerializedName("buildNo")
    public int mBuildNo;

    @SerializedName("customerBuildNo")
    public CustomerBuildNo mCustomerBuildNo;

    public static BeeWorksAppBase createInstance(JSONObject jsonObject){
        BeeWorksAppBase beeWorksAppBase = new Gson().fromJson(jsonObject.toString(), BeeWorksAppBase.class);
        return beeWorksAppBase;
    }

    public static class CustomerBuildNo implements Parcelable {

        @SerializedName("android")
        public String mAndroidBuildNo;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mAndroidBuildNo);
        }

        public CustomerBuildNo() {
        }

        protected CustomerBuildNo(Parcel in) {
            this.mAndroidBuildNo = in.readString();
        }

        public static final Creator<CustomerBuildNo> CREATOR = new Creator<CustomerBuildNo>() {
            @Override
            public CustomerBuildNo createFromParcel(Parcel source) {
                return new CustomerBuildNo(source);
            }

            @Override
            public CustomerBuildNo[] newArray(int size) {
                return new CustomerBuildNo[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mBundleId);
        dest.writeString(this.mAppName);
        dest.writeInt(this.mBuildNo);
        dest.writeParcelable(this.mCustomerBuildNo, flags);
    }

    public BeeWorksAppBase() {
    }

    protected BeeWorksAppBase(Parcel in) {
        this.mBundleId = in.readString();
        this.mAppName = in.readString();
        this.mBuildNo = in.readInt();
        this.mCustomerBuildNo = in.readParcelable(CustomerBuildNo.class.getClassLoader());
    }

    public static final Parcelable.Creator<BeeWorksAppBase> CREATOR = new Parcelable.Creator<BeeWorksAppBase>() {
        @Override
        public BeeWorksAppBase createFromParcel(Parcel source) {
            return new BeeWorksAppBase(source);
        }

        @Override
        public BeeWorksAppBase[] newArray(int size) {
            return new BeeWorksAppBase[size];
        }
    };
}
