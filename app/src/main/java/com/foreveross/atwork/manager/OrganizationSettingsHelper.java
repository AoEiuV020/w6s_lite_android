package com.foreveross.atwork.manager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.api.sdk.setting.DynamicPropertiesAsyncNetService;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementKind;
import com.foreveross.atwork.infrastructure.model.organizationSetting.OrganizationSettings;
import com.foreveross.atwork.infrastructure.model.organizationSetting.ThemeSettings;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.OrgPersonalShareInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.modules.aboutme.fragment.AboutMeFragment;
import com.foreveross.atwork.modules.advertisement.manager.AdvertisementManager;
import com.foreveross.atwork.modules.advertisement.manager.BootAdvertisementManager;
import com.foreveross.atwork.modules.app.util.AppRefreshHelper;
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager;
import com.foreveross.theme.model.ThemeType;
import com.google.gson.Gson;

import java.util.Map;

import static com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager.ORG_SETTINGS_CHANGE;

/**
 * Created by reyzhang22 on 17/7/25.
 */

public class OrganizationSettingsHelper  {

    private static OrganizationSettingsHelper sInstance = new OrganizationSettingsHelper();

    public static OrganizationSettingsHelper getInstance() {
        return sInstance;
    }

    public void checkOrgSettingsUpdate(Context context, long time) {
        long current = System.currentTimeMillis();

        if (current - OrganizationSettingsManager.checkOrgSettingsUpdateTime >= AtworkConfig.INTERVAL_REFRESH_ORG_SETTINGS || time == -1) {
            refreshOrganizationSettingsByOrgCode(context, time);
            OrganizationSettingsManager.checkOrgSettingsUpdateTime = current;
        }

    }



    /**
     * @see {@link #setCurrentOrgCodeAndRefreshSetting(Context, String, boolean, boolean)}
     */
    public void setCurrentOrgCodeAndRefreshSetting(Context context, String orgCode, boolean isManual) {
        setCurrentOrgCodeAndRefreshSetting(context, orgCode, false, isManual);
    }

    /**
     * ?????????????????????????????????
     *
     * @param context
     * @param orgCode
     * @param isApplicationInvoke
     * @param isManual
     */
    public void setCurrentOrgCodeAndRefreshSetting(Context context, String orgCode, boolean isApplicationInvoke, boolean isManual) {


        //?????????????????????????????????????????????
        BootAdvertisementManager.getInstance().getRemoteBootAdvertisementsByOrgId(context, orgCode);
        AdvertisementManager.INSTANCE.requestCurrentOrgBannerAdvertisementsSilently(AdvertisementKind.APP_BANNER);

        PersonalShareInfo.getInstance().setCurrentOrg(context, orgCode);

        //????????????????????????, ??????????????????, ???????????????, ????????????????????????
        SkinManger.getInstance().load(orgCode, null);

//        OrganizationSettingsManager.getInstance().handleDomainVpnUserAccount(context, orgCode, "", "");
        if (!isApplicationInvoke) {
            AppRefreshHelper.refreshAppAbsolutely();
            WorkbenchManager.INSTANCE.notifyRefresh();
            WorkbenchManager.INSTANCE.checkWorkbenchRemote(true, null);
        }
        AboutMeFragment.refreshUserMsg();

        if (TextUtils.isEmpty(orgCode)) {
            OrganizationSettingsManager.getInstance().mCurrentUserOrganizationsSettings = null;
            return;
        }
        refreshOrganizationSettingsByOrgCode(context, 0);
    }



    /**
     * ?????????????????????????????????
     *
     * @param context
     * @param time
     */
    public void refreshOrganizationSettingsByOrgCode(@NonNull Context context, @NonNull long time) {
        String userId = LoginUserInfo.getInstance().getLoginUserId(context);

        OrganizationSettingsManager.getInstance().mCurrentUserOrganizationsSettings = PersonalShareInfo.getInstance().getCurrentUserOrganizationsSettings(context);
        DynamicPropertiesAsyncNetService service = DynamicPropertiesAsyncNetService.getInstance();
        service.getOrganizationSettingsAsync(context, userId, time == -1 ? -1 : OrganizationSettingsManager.getInstance().getModifyTime(), new DynamicPropertiesAsyncNetService.onOrganizationSettingsListener() {
            @Override
            public void onOrganizationSettingsCallback(Map<String, OrganizationSettings> organizationSettings, String orgId) {
                OrganizationSettingsManager.getInstance().mCurrentUserOrganizationsSettings = organizationSettings;
                if (null == organizationSettings) {
                    OrgPersonalShareInfo.getInstance().clearOrganizationsSettingsData(context, orgId);
                    return;
                }

//                OrganizationSettingsManager.getInstance().handleDomainVpnUserAccount(context, PersonalShareInfo.getInstance().getCurrentOrg(context), "", "");

                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ORG_SETTINGS_CHANGE));
            }

            @Override
            public void onOrganizationSettingsFail() {

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {

            }
        });
    }

    public void updateThemeSetting(Context context, String orgCode, String name) {
        OrganizationSettings organizationSetting = OrganizationSettingsManager.getInstance().getCurrentUserOrgSetting(orgCode);
        //todo ????????????????????????????????????????????????
        if (null != organizationSetting) {
            ThemeSettings themeSettings = organizationSetting.mThemeSettings;
            if (null == themeSettings) {
                themeSettings = new ThemeSettings();
                organizationSetting.mThemeSettings = themeSettings;

            }
            themeSettings.mThemeName = name;
            themeSettings.mType = ThemeType.SYSTEM.toString();
        }
        PersonalShareInfo.getInstance().setCurrentUserOrganizationsSettings(context, new Gson().toJson(OrganizationSettingsManager.getInstance().mCurrentUserOrganizationsSettings));

    }


}
