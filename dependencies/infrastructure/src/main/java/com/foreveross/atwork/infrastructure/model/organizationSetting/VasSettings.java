package com.foreveross.atwork.infrastructure.model.organizationSetting;

import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class VasSettings implements Parcelable {

    /**
     * 声网APP ID
     */
    public static String AGORA_APP_ID = "agora.access_id";

    public static String ZOOM_APP_KEY = "zoom.app-key";

    public static String ZOOM_APP_SECRET = "zoom.app-secret";

    public static String ZOOM_WEB_DOMAIN = "zoom.web-domain";

    @SerializedName("service_type")
    public String serviceType;

    @SerializedName("service_settings")
    public Map<String,String> serviceSettings;

    protected VasSettings(Parcel in) {
        serviceType = in.readString();
        serviceSettings = in.readHashMap(String.class.getClassLoader());
    }

    public static final Creator<VasSettings> CREATOR = new Creator<VasSettings>() {
        @Override
        public VasSettings createFromParcel(Parcel in) {
            return new VasSettings(in);
        }

        @Override
        public VasSettings[] newArray(int size) {
            return new VasSettings[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(this.serviceType);
        parcel.writeMap(this.serviceSettings);
    }

    public boolean isAgoraEnabled(){
        return this.serviceType.equalsIgnoreCase("voip") && this.serviceSettings.get("provider").equalsIgnoreCase("agora");
    }

    public boolean isZoomEnabled(){
        return this.serviceType.equalsIgnoreCase("voip") && this.serviceSettings.get("provider").equalsIgnoreCase("zoom");
    }

    public String getValue(String key){
        return this.serviceSettings.get(key);
    }
}
