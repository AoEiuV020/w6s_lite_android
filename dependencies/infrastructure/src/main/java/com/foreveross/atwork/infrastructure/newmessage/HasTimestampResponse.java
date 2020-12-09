package com.foreveross.atwork.infrastructure.newmessage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/6/9.
 * Description:
 */
public abstract class HasTimestampResponse extends NoBodyMessage {

    public static final String OTHERS = "others";

    @Expose
    @SerializedName("others")
    public List<OthersInfo> mOthers = new ArrayList<>();

    public long timestamp;

    public static class OthersInfo implements Serializable{
        @SerializedName("device_id")
        public String mDeviceId;

        @SerializedName("device_platform")
        public String mDevicePlatform;

        @SerializedName("device_system")
        public String mDeviceSystem;
    }

}
