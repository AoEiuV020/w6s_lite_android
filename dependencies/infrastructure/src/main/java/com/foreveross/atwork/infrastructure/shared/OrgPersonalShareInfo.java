package com.foreveross.atwork.infrastructure.shared;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig;
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementKind;
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 16/7/8.
 */
public class OrgPersonalShareInfo extends PersonalShareInfo {

    private static final String TAG = OrgPersonalShareInfo.class.getSimpleName();

    private static OrgPersonalShareInfo sInstance = new OrgPersonalShareInfo();

    private static final String SP_ORG_SETTINGS_FILE = "SP_ORG_SETTINGS_FILE";

    private static final String SP_ORG_PERSONAL_NAME = "SP_ORG_CUSTOM_APP_ID_SORT_";

    private static final String ORG_SETTINGS_DATA = "ORG_SETTINGS_DATA";

    private static final String DATA_CUSTOM_APP_BUNDLE_ID_SORT = "DATA_CUSTOM_APP_BUNDLE_ID_SORT";

    private static final String DATA_APP_TOP_ADVERTISEMENTS = "DATA_APP_TOP_ADVERTISEMENTS";

    private static final String DATA_WORKBENCH = "DATA_WORKBENCH";

    private static final String DATA_WORKBENCH_ALL_CARDS_SORT = "DATA_WORKBENCH_ALL_CARD_SORT";

    private static final String DATA_WORKBENCH_DISPLAY_CARDS = "DATA_WORKBENCH_DISPLAY_CARDS";

    private static final String DATA_CURRENT_ADMIN_WORKBENCH = "DATA_CURRENT_ADMIN_WORKBENCH";



    public static OrgPersonalShareInfo getInstance() {
        return sInstance;
    }

    /**
     * 保存组织设置
     */
    public void setOrganizationsSettingsData(Context context, String userId, String data) {
        PreferencesUtils.putString(context, userId + "_" + SP_ORG_SETTINGS_FILE, ORG_SETTINGS_DATA, data);
    }

    /**
     * 读取组织设置
     */
    public String getOrganizationsSettingsData(Context context, String userId) {
        return PreferencesUtils.getString(context, userId + "_" + SP_ORG_SETTINGS_FILE, ORG_SETTINGS_DATA, "");
    }

    /**
     * 清除组织设置
     */
    public void clearOrganizationsSettingsData(Context context, String userId) {
        PreferencesUtils.clear(context, userId + "_" + SP_ORG_SETTINGS_FILE);
    }



    /**
     * 保存应用快捷方式排序列表
     * @param context
     * @param orgCode
     * @param sortStr
     */
    public void setCustomAppBundleIdSort(Context context, String orgCode, String sortStr) {

        String sp = getOrgSpInPersonalKey(context, orgCode);
        PreferencesUtils.putString(context, sp, DATA_CUSTOM_APP_BUNDLE_ID_SORT, sortStr);
    }


    public List<String> getCustomAppBundleIdSort(Context context, String orgCode) {
        String sp = getOrgSpInPersonalKey(context, orgCode);

        String customAppIdSortStr = PreferencesUtils.getString(context, sp, DATA_CUSTOM_APP_BUNDLE_ID_SORT, StringUtils.EMPTY);
        String[] customAppIdSortArray = customAppIdSortStr.split(",");
        return Arrays.asList(customAppIdSortArray);
    }


    public void setAdvertisements(Context context, String orgCode, List<AdvertisementConfig> advertisementConfigs, AdvertisementKind kind) {
        switch (kind) {
            case APP_BANNER:
                setAppTopAdvertisements(context, orgCode, advertisementConfigs);
                break;

        }

    }


    public void setAppTopAdvertisements(Context context, String orgCode, List<AdvertisementConfig> advertisementConfigs) {
        String sp = getOrgSpInPersonalKey(context, orgCode);
        String advertisementConfigStr = JsonUtil.toJson(advertisementConfigs);
        PreferencesUtils.putString(context, sp, DATA_APP_TOP_ADVERTISEMENTS, advertisementConfigStr);
    }

    public String getAppTopAdvertisementsStr(Context context, String orgCode) {
        String sp = getOrgSpInPersonalKey(context, orgCode);
        String advertisementConfigStr = PreferencesUtils.getString(context, sp, DATA_APP_TOP_ADVERTISEMENTS, StringUtils.EMPTY);
        return advertisementConfigStr;
    }


    public List<AdvertisementConfig> getAppTopAdvertisements(Context context, String orgCode) {
        return getAdvertisements(context, orgCode, AdvertisementKind.APP_BANNER);
    }


    public List<AdvertisementConfig> getAdvertisements(Context context, String orgCode, AdvertisementKind kind) {
        String advertisementConfigStr = getAdvertisementsStr(context, orgCode, kind);

        if (!StringUtils.isEmpty(advertisementConfigStr)) {
            List<AdvertisementConfig> advertisementConfigs = new Gson().fromJson(advertisementConfigStr, new TypeToken<List<AdvertisementConfig>>() {
            }.getType());

            return advertisementConfigs;
        }

        return Collections.emptyList();
    }

    public String getAdvertisementsStr(Context context, String orgCode, AdvertisementKind kind) {
        String advertisementConfigStr = null;
        switch (kind) {
            case APP_BANNER:
                advertisementConfigStr = getAppTopAdvertisementsStr(context, orgCode);
                break;

        }
        return advertisementConfigStr;
    }


    public void setCustomSortedCardIdList(Context context, String orgCode, long workbenchId, List<String> sortedCardIdList) {
        String sp = getOrgSpInPersonalKey(context, orgCode);
        String key = getWorkbenchAllCardsSortDataKey(workbenchId);

        PreferencesUtils.putStringList(context, sp, key, sortedCardIdList);

    }


    public void setCustomDisplayCardIdList(Context context, String orgCode, long workbenchId, List<String> displayCardIdList) {
        String sp = getOrgSpInPersonalKey(context, orgCode);
        String key = getWorkbenchDisplayCardsDataKey(workbenchId);

        PreferencesUtils.putStringList(context, sp, key, displayCardIdList);

    }


    @Nullable
    public List<String> getCustomSortedCardIdList(Context context, String orgCode, long workbenchId) {
        String sp = getOrgSpInPersonalKey(context, orgCode);
        String key = getWorkbenchAllCardsSortDataKey(workbenchId);
        return PreferencesUtils.getStringList(context, sp, key);
    }


    @Nullable
    public List<String> getCustomDisplayCardIdList(Context context, String orgCode, long workbenchId) {
        String sp = getOrgSpInPersonalKey(context, orgCode);
        String key = getWorkbenchDisplayCardsDataKey(workbenchId);
        return PreferencesUtils.getStringList(context, sp, key);
    }

    @NonNull
    private String getWorkbenchAllCardsSortDataKey(long workbenchId) {
        return DATA_WORKBENCH_ALL_CARDS_SORT + "_" +  workbenchId;
    }


    @NonNull
    private String getWorkbenchDisplayCardsDataKey(long workbenchId) {
        return DATA_WORKBENCH_DISPLAY_CARDS + "_" +  workbenchId;
    }


    public void setWorkbenchData(Context context, String orgCode, WorkbenchData workbenchData) {
        String sp = getOrgSpInPersonalKey(context, orgCode);
        PreferencesUtils.putString(context, sp, DATA_WORKBENCH, JsonUtil.toJson(workbenchData));
    }

    public void clearWorkBenchData(Context context, String orgCode) {
        String sp = getOrgSpInPersonalKey(context, orgCode);
        PreferencesUtils.remove(context, sp, DATA_WORKBENCH);
    }

    @Nullable
    public WorkbenchData getWorkbenchData(Context context, String orgCode) {
        String workbenchStr = getWorkbenchDataStr(context, orgCode);
        return JsonUtil.fromJson(workbenchStr, WorkbenchData.class);
    }

    public String getWorkbenchDataStr(Context context, String orgCode) {
        String sp = getOrgSpInPersonalKey(context, orgCode);
        return PreferencesUtils.getString(context, sp, DATA_WORKBENCH, StringUtils.EMPTY);
    }

    public void setCurrentAdminWorkbench(Context context, String orgCode, long id) {
        String sp = getOrgSpInPersonalKey(context, orgCode);
        PreferencesUtils.putLong(context, sp, DATA_CURRENT_ADMIN_WORKBENCH, id );
    }

    public long getCurrentAdminWorkbench(Context context, String orgCode) {
        String sp = getOrgSpInPersonalKey(context, orgCode);
        return PreferencesUtils.getLong(context,sp, DATA_CURRENT_ADMIN_WORKBENCH, -1);
    }


    private String getOrgSpInPersonalKey(Context context, String orgCode) {
        return SP_ORG_PERSONAL_NAME + "_" + orgCode + "_" + getPersonalSpName(context);
    }

}
