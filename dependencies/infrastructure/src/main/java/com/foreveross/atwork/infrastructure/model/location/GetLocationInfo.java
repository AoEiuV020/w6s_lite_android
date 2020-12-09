package com.foreveross.atwork.infrastructure.model.location;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetLocationInfo implements Parcelable {

    @SerializedName("poiId")
    private String mPoiId;

    @SerializedName("result")
    private String mResult;

    @SerializedName("longitude")
    public double mLongitude;

    @SerializedName("latitude")
    public double mLatitude;

    @SerializedName("address")
    public String mAddress;

    @SerializedName("country")
    public String mCountry;


    @SerializedName("city")
    public String mCity;

    @SerializedName("district")
    public String mDistrict;

    @SerializedName("street")
    public String mStreet;

    @SerializedName("province")
    public String mProvince;

    @SerializedName("aoiName")
    public String mAoiName;

    @SerializedName("exception")
    public String mException;

    @SerializedName("illegal_installed_list")
    public List<String> mIllegalInstallList;

    public boolean isSuccess() {
        return -1 != mLongitude && -1 != mLatitude && 0.0 != mLongitude && 0.0 != mLatitude;
    }

    public GetLocationInfo setLongitude(double longitude) {
        mLongitude = longitude;
        return this;
    }

    public GetLocationInfo setLatitude(double latitude) {
        mLatitude = latitude;
        return this;
    }

    public GetLocationInfo setAddress(String address) {
        mAddress = address;
        return this;
    }

    public GetLocationInfo setCountry(String country) {
        this.mCountry = country;
        return this;
    }

    public GetLocationInfo setProvince(String province) {
        this.mProvince = province;
        return this;
    }

    public GetLocationInfo setCity(String city) {
        mCity = city;
        return this;
    }

    public GetLocationInfo setDistrict(String district) {
        mDistrict = district;
        return this;
    }

    public GetLocationInfo setStreet(String street) {
        mStreet = street;
        return this;
    }

    public GetLocationInfo setAoiName(String aoiName) {
        mAoiName = aoiName;
        return this;
    }

    public GetLocationInfo setException(String exception) {
        mException = exception;
        return this;
    }

    public GetLocationInfo setIllegalInstallList(List<String> illegalInstallList) {
        mIllegalInstallList = illegalInstallList;
        return this;
    }

    public String getResult() {
        return mResult;
    }

    public GetLocationInfo setResult(String result) {
        mResult = result;

        return this;

    }


    @Override
    public String toString() {
        return "GetLocationInfo{" +
                "mResult='" + mResult + '\'' +
                ", mLongitude=" + mLongitude +
                ", mLatitude=" + mLatitude +
                ", mAddress='" + mAddress + '\'' +
                ", mCountry='" + mCountry + '\'' +
                ", mProvince='" + mProvince + '\'' +
                ", mCity='" + mCity + '\'' +
                ", mDistrict='" + mDistrict + '\'' +
                ", mStreet='" + mStreet + '\'' +
                ", mAoiName='" + mAoiName + '\'' +
                ", mException='" + mException + '\'' +
                ", mIllegalInstallList=" + mIllegalInstallList +
                '}';
    }

    public GetLocationInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPoiId);
        dest.writeString(this.mResult);
        dest.writeDouble(this.mLongitude);
        dest.writeDouble(this.mLatitude);
        dest.writeString(this.mAddress);
        dest.writeString(this.mCity);
        dest.writeString(this.mDistrict);
        dest.writeString(this.mStreet);
        dest.writeString(this.mProvince);
        dest.writeString(this.mAoiName);
        dest.writeString(this.mException);
        dest.writeStringList(this.mIllegalInstallList);
    }

    protected GetLocationInfo(Parcel in) {
        this.mPoiId = in.readString();
        this.mResult = in.readString();
        this.mLongitude = in.readDouble();
        this.mLatitude = in.readDouble();
        this.mAddress = in.readString();
        this.mCity = in.readString();
        this.mDistrict = in.readString();
        this.mStreet = in.readString();
        this.mProvince = in.readString();
        this.mAoiName = in.readString();
        this.mException = in.readString();
        this.mIllegalInstallList = in.createStringArrayList();
    }

    public static final Creator<GetLocationInfo> CREATOR = new Creator<GetLocationInfo>() {
        @Override
        public GetLocationInfo createFromParcel(Parcel source) {
            return new GetLocationInfo(source);
        }

        @Override
        public GetLocationInfo[] newArray(int size) {
            return new GetLocationInfo[size];
        }
    };
}
