package com.foreveross.atwork.crash;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lingen on 15/4/9.
 * Description:
 * 崩溃信息
 */
public class CrashInfo implements Serializable {

    public String packageName;

    public int appVersion;

    public String versionName;

    /**
     * 错误日志
     */
    public String error;
    /**
     * 设备信息
     */
    private Map<String, String> deviceInfo = new HashMap<>();

    public void addDeviceInfo(String key, String value) {
        deviceInfo.put(key, value);
    }


    /**
     * 输出日志文件模式
     *
     * @return
     */
    public String printLog() {
        StringBuffer sb = new StringBuffer();
        sb.append("packageName: " + packageName + "\n");
        sb.append("appVersion: " + appVersion + "\n");
        sb.append("versionName: " + versionName + "\n");
        for (String key : deviceInfo.keySet()) {
            String value = deviceInfo.get(key);
            sb.append(key + ": " + value + "\n");
        }
        sb.append("stackTrace: " + error + "\n");
        return sb.toString();
    }
}
