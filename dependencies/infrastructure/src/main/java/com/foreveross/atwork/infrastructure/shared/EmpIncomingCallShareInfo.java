package com.foreveross.atwork.infrastructure.shared;

import android.content.Context;

import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;

public class EmpIncomingCallShareInfo extends PersonalShareInfo {

    private static final EmpIncomingCallShareInfo sInstance = new EmpIncomingCallShareInfo();

    /**
     * 企业来电助手
     * false    关闭
     * true    开启
     */
    public static final String EMP_INCOMING_CALL_SETTING = "EMP_INCOMING_CALL_SETTING";

    //企业助手同步时间戳
    public static final String LAST_TIME_EMP_INCOMING_CALLER = "LAST_TIME_EMP_INCOMING_CALLER";

    /**
     * 企业助手同步状态
     *  0  成功
     *  1  同步中
     * -1  同步失败
     */
    public static final String EMP_INCOMING_CALL_SYNC_STATUS = "EMP_INCOMING_CALL_SYNC_STATUS";

    /**
     * 企业助手上次同步完成时间
     */
    public static final String EMP_INCOMING_CALL_LAST_SYNC_FINISH_TIME = "EMP_INCOMING_CALL_LAST_SYNC_FINISH_TIME";


    public static EmpIncomingCallShareInfo getInstance() {
        return sInstance;
    }

    public boolean getEmpIncomingCallAssistantSetting(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), EMP_INCOMING_CALL_SETTING, false);
    }

    public void setEmpIncomingCallAssistantSetting(Context context, boolean isOpen) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), EMP_INCOMING_CALL_SETTING, isOpen);
    }

    public long getLastTimeFetchEmpCaller(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getLong(context, getPersonalSpName(username), LAST_TIME_EMP_INCOMING_CALLER, -1);
    }

    public void setLastTimeFetchEmpCaller(Context context, long time) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putLong(context, getPersonalSpName(username), LAST_TIME_EMP_INCOMING_CALLER, time);
    }

    public void setEmpIncomingCallSyncStatus(Context context, int status) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putInt(context, getPersonalSpName(username), EMP_INCOMING_CALL_SYNC_STATUS, status);
    }

    public int getEmpIncomingCallSyncStatus(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getInt(context, getPersonalSpName(username), EMP_INCOMING_CALL_SYNC_STATUS, -1);
    }


    public long getLastSyncFinishTime(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getLong(context, getPersonalSpName(username), EMP_INCOMING_CALL_LAST_SYNC_FINISH_TIME, -1);
    }

    public void setlastSyncFinishTime(Context context, long time) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putLong(context, getPersonalSpName(username), EMP_INCOMING_CALL_LAST_SYNC_FINISH_TIME, time);
    }

}
