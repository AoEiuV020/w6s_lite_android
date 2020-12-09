package com.foreveross.atwork.api.sdk.app.model;

import android.os.Parcel;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InstallOrRemoveAppResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public List<AppResult> result;


    public static class AppResult implements android.os.Parcelable {
        @SerializedName("domain_id")
        public String domainId;

        @SerializedName("org_id")
        public String orgId;

        @SerializedName("app_id")
        public String appId;

        @SerializedName("follow_ids")
        public List<String> followIds;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.domainId);
            dest.writeString(this.orgId);
            dest.writeString(this.appId);
            dest.writeStringList(this.followIds);
        }

        public AppResult() {
        }

        protected AppResult(Parcel in) {
            this.domainId = in.readString();
            this.orgId = in.readString();
            this.appId = in.readString();
            this.followIds = in.createStringArrayList();
        }

        public static final Creator<AppResult> CREATOR = new Creator<AppResult>() {
            @Override
            public AppResult createFromParcel(Parcel source) {
                return new AppResult(source);
            }

            @Override
            public AppResult[] newArray(int size) {
                return new AppResult[size];
            }
        };
    }

    public static InstallOrRemoveAppResponseJson toResponseJson(String json) {

        InstallOrRemoveAppResponseJson appResponseJson = null;
        try {
            appResponseJson = (InstallOrRemoveAppResponseJson) NetGsonHelper.fromNetJson(json, true, InstallOrRemoveAppResponseJson.class);

        } catch (Exception e) {

            BasicResponseJSON base = JsonUtil.fromJson(json, BasicResponseJSON.class);
            appResponseJson = new InstallOrRemoveAppResponseJson();
            appResponseJson.status = base.status;
            appResponseJson.message = base.message;

            e.printStackTrace();
        }

        return appResponseJson;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.result);
    }

    public InstallOrRemoveAppResponseJson() {
    }

    protected InstallOrRemoveAppResponseJson(Parcel in) {
        super(in);
        this.result = in.createTypedArrayList(AppResult.CREATOR);
    }

    public static final Creator<InstallOrRemoveAppResponseJson> CREATOR = new Creator<InstallOrRemoveAppResponseJson>() {
        @Override
        public InstallOrRemoveAppResponseJson createFromParcel(Parcel source) {
            return new InstallOrRemoveAppResponseJson(source);
        }

        @Override
        public InstallOrRemoveAppResponseJson[] newArray(int size) {
            return new InstallOrRemoveAppResponseJson[size];
        }
    };
}
