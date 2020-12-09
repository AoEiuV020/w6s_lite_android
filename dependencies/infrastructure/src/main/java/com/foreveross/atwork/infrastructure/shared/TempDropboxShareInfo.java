package com.foreveross.atwork.infrastructure.shared;

import android.content.Context;

import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;

public class TempDropboxShareInfo extends PersonalShareInfo {
    private final static String SP_KEY_TEMP_DROPBOX_SHARE_DATA = "SP_KEY_TEMP_DROPBOX_SHARE_DATA";

    private static TempDropboxShareInfo sInstance;

    public static final TempDropboxShareInfo getInstance() {
        synchronized (sLock) {
            if (null == sInstance) {
                sInstance = new TempDropboxShareInfo();
            }
            return sInstance;
        }
    }

    public void setTempShareData(Context context, String data) {
        PreferencesUtils.putString(context, getPersonalSpName(context), SP_KEY_TEMP_DROPBOX_SHARE_DATA, data);
    }

    public String getTempShareData(Context context) {
        return PreferencesUtils.getString(context, getPersonalSpName(context), SP_KEY_TEMP_DROPBOX_SHARE_DATA, "");
    }
}
