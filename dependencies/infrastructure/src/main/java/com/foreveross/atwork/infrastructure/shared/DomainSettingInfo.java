package com.foreveross.atwork.infrastructure.shared;

import android.content.Context;
import androidx.annotation.NonNull;

import com.foreveross.atwork.infrastructure.model.domain.AppProfile;
import com.foreveross.atwork.infrastructure.model.domain.DomainSettings;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

/**
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
 * Created by reyzhang22 on 16/7/8.
 */
public class DomainSettingInfo {

    private static final String TAG = DomainSettingInfo.class.getSimpleName();

    private static DomainSettingInfo sInstance = new DomainSettingInfo();

    private static final String SP_DOMAIN_SETTINGS_FILE = "SP_DOMAIN_SETTINGS_FILE";

    private static final String DOMAIN_SETTINGS_DATA = "DOMAIN_SETTINGS_DATA";

    private static final String APP_PROFILE_DATA = "APP_PROFILE_DATA";

    public static DomainSettingInfo getInstance() {
        synchronized (TAG) {
            if (sInstance == null) {
                sInstance = new DomainSettingInfo();
            }
            return sInstance;
        }
    }

    /**
     * @param context
     * @param data
     */
    public void setDomainSettingsData(Context context, String data) {
        PreferencesUtils.putString(context, SP_DOMAIN_SETTINGS_FILE, getDomainSettingsDataKey(), data);
    }

    public void setAppProfileData(Context context, String data) {
        PreferencesUtils.putString(context, SP_DOMAIN_SETTINGS_FILE, getAppProfileDataKey(), data);
    }

    @NonNull
    private String getDomainSettingsDataKey() {
        return DOMAIN_SETTINGS_DATA + "_" + AtworkConfig.PROFILE;
    }

    private String getAppProfileDataKey() {
        return APP_PROFILE_DATA + "_" + AtworkConfig.PROFILE;
    }

    /**
     * @param context
     * @return
     */
    public DomainSettings getDomainSettingsData(Context context) {
        DomainSettings domainSettings = null;
        String domainSettingStr = PreferencesUtils.getString(context, SP_DOMAIN_SETTINGS_FILE, getDomainSettingsDataKey(), "");
        if(!StringUtils.isEmpty(domainSettingStr)) {
            domainSettings = JsonUtil.fromJson(domainSettingStr, DomainSettings.class);
        }
        return domainSettings;
    }

    public AppProfile getAppProfileData(Context context) {
        AppProfile appProfile = null;
        String domainSettingStr = PreferencesUtils.getString(context, SP_DOMAIN_SETTINGS_FILE, getAppProfileDataKey(), "");
        if(!StringUtils.isEmpty(domainSettingStr)) {
            appProfile = JsonUtil.fromJson(domainSettingStr, AppProfile.class);
        }
        return appProfile;
    }

    /**
     * @param context
     */
    public void clearDomainSettingsData(Context context) {
        PreferencesUtils.clear(context, SP_DOMAIN_SETTINGS_FILE);
    }
}
