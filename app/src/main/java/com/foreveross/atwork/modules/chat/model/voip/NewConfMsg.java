package com.foreveross.atwork.modules.chat.model.voip;

/**
 * 会议消息
 */
public class NewConfMsg {

    /**
     * 会议HME日志级别
     */
    public int nHmeLevel = ConfLogLevel.LOG_LEVEL_WARNING;

    /**
     * 会议SDK日志级别
     */
    public int nSdkLevel = ConfLogLevel.LOG_LEVEL_WARNING;


    public int mHandle;

    public String mConfId;

    public long mUserId;

    public int mUserType;

    public int mDeviceType;

    public String mStrHostRole = "111111";

    public String mSiteUrl;

    public String mSiteID;

    public String mUsername;

    public String mConfTitle;

    public String mServerIP;

    public String mEncrytionKey;

    private String mUserLogUri;

    public int option;

    /**
     * *
     * 会议日志级别
     */
    public static class ConfLogLevel {
        public static final int LOG_LEVEL_ERROR = 0;
        public static final int LOG_LEVEL_WARNING = 1;
        public static final int LOG_LEVEL_INFO = 2;
        public static final int LOG_LEVEL_DEBUG = 3;
        public static final int LOG_LEVEL_FUNC = 4;
    }

    /**
     * 加载的组件类型
     */
    public int componentFlag = 0;

    /**
     * 会议组件日志路径
     */
    public String confLogPath = null;

    /**
     * 会议组件临时文件路径
     */
    public String confTempDir = "D:";


    /**
     * *
     * 前置摄像头
     */
    public String cameraBack = "";

    /**
     * 后置摄像头名称
     */
    public String cameraFront = "";


    public void setnSdkLevel(int nSdkLevel) {
        if (nSdkLevel < 0 || nSdkLevel > 3) {
            this.nSdkLevel = ConfLogLevel.LOG_LEVEL_WARNING;
        } else {
            this.nSdkLevel = nSdkLevel;
        }
    }

    /**
     * 重置数据
     */
    public void clear() {
        this.componentFlag = 0;
        this.confLogPath = null;
        this.nHmeLevel = ConfLogLevel.LOG_LEVEL_WARNING;
        this.nSdkLevel = ConfLogLevel.LOG_LEVEL_WARNING;
    }


}
