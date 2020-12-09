package com.foreveross.atwork.infrastructure.newmessage.post;

import android.util.Log;

import com.foreveross.atwork.infrastructure.newmessage.HasBodyMessage;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public abstract class DeviceInfoMessage extends HasBodyMessage {

    public static final String DEVICE_ID = "device_id";

    public static final String DEVICE_PLATFORM = "device_platform";

    public static final String TIMESTAMP = "timestamp";

    public static final String DEVICE_SYSTEM = "device_system";

    @Expose
    @SerializedName("device_id")
    public String mDeviceId;

    @Expose
    @SerializedName("device_platform")
    public String mDevicePlatform;

    @Expose
    @SerializedName("timestamp")
    public String mTimestamp;

    @Expose
    @SerializedName("device_system")
    public String mDeviceSystem;

    @Override
    public Map<String, Object> getMessageBody() {
        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put(DEVICE_ID, mDeviceId);
        messageBody.put(DEVICE_PLATFORM, mDevicePlatform);
        messageBody.put(TIMESTAMP, mTimestamp);
        messageBody.put(DEVICE_SYSTEM, mDeviceSystem);
        return messageBody;
    }

    //在ReceiveRunnable中调用打印，看是否有数据
    public void showMessage(){
        String strMessage = "("+mDeviceId+";"+mDevicePlatform+";"+mTimestamp+";"+mDeviceSystem+")";
    }

}
