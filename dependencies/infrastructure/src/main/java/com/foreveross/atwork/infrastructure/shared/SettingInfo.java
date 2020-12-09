package com.foreveross.atwork.infrastructure.shared;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.content.Context;

import com.foreveross.atwork.infrastructure.beeworks.UserSettings;
import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;

/**
 * Created by reyzhang22 on 16/4/7.
 */
public class SettingInfo  {

    private static final String TAG = SettingInfo.class.getSimpleName();

    private static SettingInfo sInstance = new SettingInfo();

    private static final String SP_USER_SETTING_FILE = "USER_SETTING_FILE";

    //----------------------SETTING ----------------------------
    private static final String SETTING_DEV_CODE = "SETTING_DEV_CODE";
    private static final String SETTING_DEV_MODE = "SETTING_DEV_MODE";
    private static final String SETTING_IP_DOMAIN = "SETTING_IP_DOMAIN";

    private UserSettings mUserSettings;


    public static SettingInfo getInstance() {
        synchronized (TAG) {
            if (sInstance == null) {
                sInstance = new SettingInfo();
            }
            return sInstance;
        }
    }



    /**
     * 保存开发者模式设置
     * @param context
     * @param isDevMode
     */
    public void setDevMode(Context context, boolean isDevMode) {
        setBooleanValue(context, SETTING_DEV_MODE, isDevMode);
        mUserSettings.mIsDevMode = isDevMode;
    }

    /**
     * 保存beeworks预览码
     * @param context
     * @param devCode
     */
    public void setDevCode(Context context, String devCode) {
        setStringValue(context, SETTING_DEV_CODE, devCode);
        mUserSettings.mDevCode = devCode;
    }


    /**
     * 获取用户设置对象
     * @param context
     * @return
     */
    public UserSettings getUserSettings(Context context) {
        if (mUserSettings == null) {
            readUserSettingsFromShared(context);
        }
        return mUserSettings;
    }

    private void setBooleanValue(Context context, String key, boolean value) {
        if (mUserSettings == null) {
            readUserSettingsFromShared(context);
        }
        PreferencesUtils.putBoolean(context, SP_USER_SETTING_FILE, key, value);
    }

    private void setStringValue(Context context, String key, String value) {
        if (mUserSettings == null) {
            readUserSettingsFromShared(context);
        }
        PreferencesUtils.putString(context, SP_USER_SETTING_FILE, key, value);
    }

    private void readUserSettingsFromShared(Context context) {
        if (mUserSettings == null) {
            mUserSettings = new UserSettings();
        }

        mUserSettings.mDevCode = PreferencesUtils.getString(context, SP_USER_SETTING_FILE, SETTING_DEV_CODE, "");
        mUserSettings.mIsDevMode = PreferencesUtils.getBoolean(context, SP_USER_SETTING_FILE, SETTING_DEV_MODE, false);
        mUserSettings.mIpDomainSwitch = PreferencesUtils.getBoolean(context, SP_USER_SETTING_FILE, SETTING_IP_DOMAIN, false);
    }



}
