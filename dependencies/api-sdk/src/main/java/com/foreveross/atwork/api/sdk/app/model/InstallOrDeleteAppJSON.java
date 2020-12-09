package com.foreveross.atwork.api.sdk.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/5/14.
 * Description:
 */
public class InstallOrDeleteAppJSON implements Parcelable {

    @SerializedName("org_id")
    public String mOrgId;

    public boolean mInstall = true;

    @SerializedName("manual")
    public boolean mManual = false;

    @SerializedName("app_entries")
    public List<AppEntrances> mAppEntrances = new ArrayList<>();

    public InstallOrDeleteAppJSON(){

    }

    public static InstallOrDeleteAppJSON createInstance(List<AppEntrances> appEntrances, String orgID, boolean install, boolean manual){
        InstallOrDeleteAppJSON installAppJSON = new InstallOrDeleteAppJSON();
        installAppJSON.mManual = manual;
        installAppJSON.mInstall = install;
        installAppJSON.mAppEntrances = appEntrances;
        installAppJSON.mOrgId = orgID;
        return installAppJSON;
    }

    public static class AppEntrances implements Parcelable {
        @SerializedName("app_id")
        public String mAppId = "";

        @SerializedName("entries")
        public List<String> mEntries = new ArrayList<>();

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mAppId);
            dest.writeStringList(this.mEntries);
        }

        public AppEntrances() {
        }

        protected AppEntrances(Parcel in) {
            this.mAppId = in.readString();
            this.mEntries = in.createStringArrayList();
        }

        public static final Creator<AppEntrances> CREATOR = new Creator<AppEntrances>() {
            @Override
            public AppEntrances createFromParcel(Parcel source) {
                return new AppEntrances(source);
            }

            @Override
            public AppEntrances[] newArray(int size) {
                return new AppEntrances[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mOrgId);
        dest.writeByte(this.mManual ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.mAppEntrances);
    }

    protected InstallOrDeleteAppJSON(Parcel in) {
        this.mOrgId = in.readString();
        this.mManual = in.readByte() != 0;
        this.mAppEntrances = in.createTypedArrayList(AppEntrances.CREATOR);
    }

    public static final Creator<InstallOrDeleteAppJSON> CREATOR = new Creator<InstallOrDeleteAppJSON>() {
        @Override
        public InstallOrDeleteAppJSON createFromParcel(Parcel source) {
            return new InstallOrDeleteAppJSON(source);
        }

        @Override
        public InstallOrDeleteAppJSON[] newArray(int size) {
            return new InstallOrDeleteAppJSON[size];
        }
    };
}
