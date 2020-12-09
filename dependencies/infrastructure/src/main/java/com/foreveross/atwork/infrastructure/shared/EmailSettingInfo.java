package com.foreveross.atwork.infrastructure.shared;

import android.content.Context;

import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;

/**
 * Created by reyzhang22 on 2018/4/26.
 */

public class EmailSettingInfo {

    private static final String TAG = EmailSettingInfo.class.getName();

    public static final EmailSettingInfo sInstance = new EmailSettingInfo();

    private static final String EMAIL_SETTING_FILE = "EMAIL_SETTING_FILE";

    //----------------------SETTING ----------------------------
    private static final String EMAIL_REMIND_SETTING = "EMAIL_REMIND_SETTING";
    private static final String EMAIL_NOTICE_VOICE = "EMAIL_NOTICE_VOICE";
    private static final String EMAIL_NOTICE_VIBRATE = "EMAIL_NOTICE_VIBRATE";

    private static final String EMAIL_SYNC_DAY = "EMAIL_SYNC_DAY";
    private static final String EMAIL_SYNC_BG = "EMAIL_SYNC_BG";

    private static final String SYNC_CALENDAR = "SYNC_CALENDAR";

    public static EmailSettingInfo getInstance() {
        return sInstance;
    }

    public void setEmailRemindSetting(Context context, boolean open) {
        setBooleanValue(context, EMAIL_REMIND_SETTING, open);
    }

    public boolean getEmailRemindSetting(Context context) {
        return PreferencesUtils.getBoolean(context, EMAIL_SETTING_FILE, EMAIL_REMIND_SETTING, true);
    }

    public void setEmailVoiceNotice(Context context, boolean voice) {
        setBooleanValue(context, EMAIL_NOTICE_VOICE, voice);
    }

    public boolean getEmailVoiceNotice(Context context) {
        return  PreferencesUtils.getBoolean(context, EMAIL_SETTING_FILE, EMAIL_NOTICE_VOICE, true);
    }


    public void setEmailVibrateNotice(Context context, boolean vibrate) {
        setBooleanValue(context, EMAIL_NOTICE_VIBRATE, vibrate);
    }

    public boolean getEmailVibrateNotice(Context context) {
        return PreferencesUtils.getBoolean(context, EMAIL_SETTING_FILE, EMAIL_NOTICE_VIBRATE, true);
    }

    public void setEmailSyncDay(Context context, String day) {
        setStringValue(context, EMAIL_SYNC_DAY, day);
    }

    public String getEmailSyncDay(Context context) {
        return PreferencesUtils.getString(context, EMAIL_SETTING_FILE, EMAIL_SYNC_DAY, "1月内");
    }

    public void setEmailSyncBg(Context context, boolean sync) {
        setBooleanValue(context, EMAIL_SYNC_BG, sync);
    }

    public boolean getEmailSyncBg(Context context) {
        return PreferencesUtils.getBoolean(context, EMAIL_SETTING_FILE, EMAIL_SYNC_BG, true);
    }

    public void setSyncCalendar(Context context, boolean sync) {
        setBooleanValue(context, SYNC_CALENDAR, sync);
    }

    public boolean getSyncCalendar(Context context) {
        return PreferencesUtils.getBoolean(context, EMAIL_SETTING_FILE, SYNC_CALENDAR, false);
    }

    private void setBooleanValue(Context context, String key, boolean value) {
        PreferencesUtils.putBoolean(context, EMAIL_SETTING_FILE, key, value);
    }

    private void setStringValue(Context context, String key, String value) {
        PreferencesUtils.putString(context, EMAIL_SETTING_FILE, key, value);
    }

    private void setIntValue(Context context, String key, int value) {
        PreferencesUtils.putInt(context, EMAIL_SETTING_FILE, key, value);
    }


}
