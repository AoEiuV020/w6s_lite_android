package com.foreveross.atwork.infrastructure.shared;

import android.content.Context;

import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;

/**
 * Created by reyzhang22 on 17/9/16.
 */
@Deprecated
public class ApiSettingInfo {

    private static final ApiSettingInfo sInstance = new ApiSettingInfo();

    private static final String SP_API_SETTING = "_SP_API_SETTING_";

    private static final String API_SETTING_URL = "API_SETTING_URL";

    private static final String API_SETTING_DOMAIN_ID = "API_SETTING_DOMAIN_ID";

    private static final String ADMIN_URL_SETTING_URL = "ADMIN_URL_SETTING_URL";

    private static final String LOGIN_ENCRYPT = "LOGIN_ENCRYPT";

    private static final String API_SETTING_PROFILE = "API_SETTING_PROFILE";



    public static ApiSettingInfo getInstance() {
        return sInstance;
    }

    public void setApiUrl(Context context, String url) {
        PreferencesUtils.putString(context, SP_API_SETTING, API_SETTING_URL, url);
    }

    public void setAdminUrl(Context context, String adminUrl) {
        PreferencesUtils.putString(context, SP_API_SETTING, ADMIN_URL_SETTING_URL, adminUrl);
    }

    public void setDomainId(Context context, String domainId) {
        PreferencesUtils.putString(context, SP_API_SETTING, API_SETTING_DOMAIN_ID, domainId);
    }

    public String getApiUrl(Context context) {
        return PreferencesUtils.getString(context, SP_API_SETTING, API_SETTING_URL, "");
    }

    public String getDomainId(Context context) {
        return PreferencesUtils.getString(context, SP_API_SETTING, API_SETTING_DOMAIN_ID, "");
    }


    public String getAdminUrl(Context context) {
        return PreferencesUtils.getString(context, SP_API_SETTING, ADMIN_URL_SETTING_URL, "");
    }

    public boolean isLoginEncrypt(Context context) {
        return 1 == getLoginEncrypt(context);
    }

    public int getLoginEncrypt(Context context) {
        return PreferencesUtils.getInt(context, SP_API_SETTING, LOGIN_ENCRYPT, -1);
    }


    public void setLoginEncrypt(Context context, boolean encrypt) {
        int encryptInt = 0;
        if(encrypt) {
            encryptInt = 1;
        }
        PreferencesUtils.putInt(context, SP_API_SETTING, LOGIN_ENCRYPT, encryptInt);
    }

    public void setProfile(Context context, String profile) {
        PreferencesUtils.putString(context, SP_API_SETTING, API_SETTING_PROFILE, profile);
    }

    public String getProfile(Context context) {
        return PreferencesUtils.getString(context, SP_API_SETTING, API_SETTING_PROFILE, "");
    }


    public void clear(Context context) {
        PreferencesUtils.clear(context, SP_API_SETTING);
    }

}
