package com.foreveross.atwork.infrastructure.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.employee.EmployeeManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.organizationSetting.OrganizationSettings;
import com.foreveross.atwork.infrastructure.model.employee.Position;
import com.foreveross.atwork.infrastructure.model.organizationSetting.CustomizationScope;
import com.foreveross.atwork.infrastructure.model.organizationSetting.CustomizationScopeType;
import com.foreveross.atwork.infrastructure.model.organizationSetting.DiscussionSettings;
import com.foreveross.atwork.infrastructure.model.organizationSetting.ThemeSettings;
import com.foreveross.atwork.infrastructure.model.organizationSetting.VpnSettings;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * Created by reyzhang22 on 16/7/13.
 */
public class OrganizationSettingsManager {

    private static final String TAG = OrganizationSettingsManager.class.getSimpleName();

    private static OrganizationSettingsManager sInstance = new OrganizationSettingsManager();

    public static final String ORG_SETTINGS_CHANGE = "ORG_SETTINGS_CHANGE";

    public static final int ORG_SELECT_NODE_EMPLOYEE = 0;

    public static final int ORG_SELECT_NODE_ORG = 1;

    public static final int ORG_SELECT_NODE_BOTH = 2;

    public static long checkOrgSettingsUpdateTime = 0;

    public static OrganizationSettingsManager getInstance() {
        return sInstance;
    }

    public Map<String, OrganizationSettings> mCurrentUserOrganizationsSettings;


    @Nullable
    public Map<String, OrganizationSettings> getCurrentUserOrganizationsSettings() {
        if (null == mCurrentUserOrganizationsSettings) {
            mCurrentUserOrganizationsSettings = PersonalShareInfo.getInstance().getCurrentUserOrganizationsSettings(BaseApplicationLike.baseContext);
        }

        return mCurrentUserOrganizationsSettings;
    }

    @Nullable
    public OrganizationSettings getCurrentUserOrgSetting(String orgCode) {
        Map<String, OrganizationSettings> organizationSettingsMap = getCurrentUserOrganizationsSettings();
        if (null != organizationSettingsMap) {
            return organizationSettingsMap.get(orgCode);
        }

        return null;
    }

    public Map<String, String> getEnvironmentVariables(String orgCode) {
        OrganizationSettings organizationSetting = getCurrentUserOrgSetting(orgCode);
        if (null == organizationSetting) {
            return null;
        }
        return organizationSetting.mEnvironmentVariables;
    }


    public long getModifyTime() {
        if (null == mCurrentUserOrganizationsSettings || mCurrentUserOrganizationsSettings.isEmpty()) {
            return -1;
        }
        Set<String> keys = mCurrentUserOrganizationsSettings.keySet();
        long modifyTime = -1;
        int i = 0;
        for (String key : keys) {
            OrganizationSettings org = mCurrentUserOrganizationsSettings.get(key);
            if (null == org) {
                continue;
            }
            if (0 == i) {
                modifyTime = org.mModifyTime;
                continue;
            }
            modifyTime = Math.max(modifyTime, org.mModifyTime);
            i++;
        }
        return modifyTime;
    }



    @SuppressLint("StaticFieldLeak")
    public void checkEmailAttachmentEnable(final Context context, final OnResultListener onResultListener) {

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {


                String loginUserId = LoginUserInfo.getInstance().getLoginUserId(context);
                String currentOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(context);

                List<String> scopes = getEmailAttachScopes(currentOrgCode);
                if(ListUtil.isEmpty(scopes)) {
                    return true;
                }

                Employee employee = EmployeeManager.getInstance().queryEmpInSync(context, loginUserId, currentOrgCode);
                if(null != employee) {
                    return checkInScope(employee, scopes);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                onResultListener.onResult(result);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    private boolean checkInScope(@NonNull Employee employee, List<String> scopes) {
        for(Position position : employee.positions) {

            for(String path : scopes) {

                StringBuilder loginPath = new StringBuilder(position.path);
                if(-1 == position.path.lastIndexOf("/")) {
                    loginPath.append("/");
                }

                loginPath.append(employee.userId);

                if(countLegalRelationship(path, loginPath.toString())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean countLegalRelationship(String targetPath, String loginPath) {
        return loginPath.contains(targetPath);
    }


    @Nullable
    public List<String> getEmailAttachScopes(String orgCode) {
        List<CustomizationScope> customizationScopes = getCustomizationScopes(orgCode);
        if(ListUtil.isEmpty(customizationScopes)) {
            return null;
        }

        for(CustomizationScope customizationScope : customizationScopes) {
            if(CustomizationScopeType.EMAIL_ATTACHMENT.toString().equalsIgnoreCase(customizationScope.getType())) {
                return customizationScope.getScopes();
            }
        }

        return null;
    }

    /**
     * 获取定制化需求范围
     * */
    @Nullable
    public List<CustomizationScope> getCustomizationScopes(String orgCode) {
        OrganizationSettings organizationSetting = getCurrentUserOrgSetting(orgCode);
        if(null == organizationSetting) {
            return null;
        }

        return organizationSetting.mCustomizationScopes;
    }

    /**
     * 处理是否显示我的圈子
     *
     * @return
     */
    public boolean handleMyCircleFeature(String orgCode) {
        OrganizationSettings organizationSetting = getCurrentUserOrgSetting(orgCode);
        if (null == organizationSetting) {
            return false;
        }
        if (organizationSetting.getMomentsSettings() == null) {
            return false;
        }
        return !organizationSetting.getMomentsSettings().mDisabled;
    }

    public String getShareOrgShareUrl(String orgCode) {

        OrganizationSettings organizationSetting = getCurrentUserOrgSetting(orgCode);
        if (null == organizationSetting) {
            return StringUtils.EMPTY;
        }
        return organizationSetting.mShakeUrl;
    }

    /**
     * 组织架构选择节点
     *
     * @return
     */
    public int getOrgSelectNode(String orgCode) {
        OrganizationSettings organizationSetting = getCurrentUserOrgSetting(orgCode);
        if (null == organizationSetting) {
            return ORG_SELECT_NODE_BOTH;
        }
        if ("employee".equalsIgnoreCase(organizationSetting.mNodeSelect)) {
            return ORG_SELECT_NODE_EMPLOYEE;
        }
        if ("org".equalsIgnoreCase(organizationSetting.mNodeSelect)) {
            return ORG_SELECT_NODE_ORG;
        }
        return ORG_SELECT_NODE_BOTH;
    }

    @Nullable
    public ThemeSettings getThemeSetting(String orgCode) {
        OrganizationSettings organizationSetting = getCurrentUserOrgSetting(orgCode);
        if (null != organizationSetting) {
            return organizationSetting.mThemeSettings;
        }

        return null;
    }


    /**
     * 显示人数
     *
     * @return
     */
    public boolean handleOrgMembersCountingFeature(String orgCode) {

        OrganizationSettings organizationSettings = getCurrentUserOrgSetting(orgCode);
        if (null == organizationSettings) {
            return true;
        }
        return organizationSettings.mCounting;
    }


    public boolean isCurrentOrgVoipEnable(Context context) {
        String orgCode = PersonalShareInfo.getInstance().getCurrentOrg(context);
        OrganizationSettings organizationSettings = getCurrentUserOrgSetting(orgCode);

        return (null != organizationSettings && organizationSettings.mVoipEnable);
    }

    public boolean onOrgViewShow(String orgCode) {
        OrganizationSettings organizationSettings = getCurrentUserOrgSetting(orgCode);
        if(null == organizationSettings) {
            return true;
        }

        return organizationSettings.mViewEnable;
    }

    public boolean onAppCustomizationEnabled(String orgCode) {

        if(AtworkConfig.APP_CONFIG.isForceUseCustomAppList()) {
            return true;
        }


        OrganizationSettings organizationSettings = getCurrentUserOrgSetting(orgCode);
        if(null == organizationSettings) {
            return false;
        }

        return organizationSettings.mAppCustomizationEnabled;
    }

    /**
     * 是否打开"显示高管名单"
     * */
    public boolean isSeniorShowOpen(Context context, String orgCode) {
        OrganizationSettings organizationSettings = getCurrentUserOrgSetting(orgCode);

        return (null != organizationSettings && null != organizationSettings.mSeniorSettings && organizationSettings.mSeniorSettings.mShowSenior);
    }

    /**
     * 不允许主动跟高管聊天时弹出的提示语言
     * */
    public String getChatNonSupportPrompt(Context context, String orgCode) {
        OrganizationSettings organizationSettings = getCurrentUserOrgSetting(orgCode);

        if(null == organizationSettings || null == organizationSettings.mSeniorSettings ) {
            return StringUtils.EMPTY;
        }

        return organizationSettings.mSeniorSettings.mChatNonsupportPrompt;
    }


    @Nullable
    public String[] getChatAuthScope(Context context, String orgCode) {
        OrganizationSettings organizationSettings = getCurrentUserOrgSetting(orgCode);

        if(null == organizationSettings || null == organizationSettings.mSeniorSettings || StringUtils.isEmpty(organizationSettings.mSeniorSettings.mChatAuthScope)) {
            return null;
        }

        return organizationSettings.mSeniorSettings.mChatAuthScope.split(",");
    }

    /**
     * 获取组织水印设置
     *
     * @param orgCode
     * @return
     */
    public String getOrganizationWatermarkSettings(String orgCode) {
        if (!onWatermarkSettingsValid(orgCode)) {
            return "none";
        }
        return mCurrentUserOrganizationsSettings.get(orgCode).mWatermarkSettings.mOrganization;
    }

    /**
     * 当前组织内部群是否允许群成员自由成为群主
     */
    public boolean isInternalDiscussionOwnerCustomer(String orgCode) {

        OrganizationSettings organizationSettings = getCurrentUserOrgSetting(orgCode);
        if (null == organizationSettings) {
            return false;
        }

        DiscussionSettings discussionSettings = organizationSettings.mDiscussionSettings;

        return null != discussionSettings && "custom".equals(discussionSettings.mOwner);

    }

    /**
     * 获取雇员水印设置
     *
     * @param orgCode
     * @return
     */
    public String getEmployeeWatermarkSettings(String orgCode) {
        if (!onWatermarkSettingsValid(orgCode)) {
            return "none";
        }
        return mCurrentUserOrganizationsSettings.get(orgCode).mWatermarkSettings.mEmployee;
    }

    private boolean onWatermarkSettingsValid(String orgCode) {

        OrganizationSettings organizationSettings = getCurrentUserOrgSetting(orgCode);
        if (null == organizationSettings) {
            return false;
        }
        if (null == organizationSettings.mWatermarkSettings) {
            return false;
        }

        return true;
    }

    /**
     * 判断是否显示我的会员
     * @param orgCode
     * @return
     */
    public boolean isShowMyIntegralEnabled(String orgCode) {

        OrganizationSettings organizationSettings = getCurrentUserOrgSetting(orgCode);
        if (null == organizationSettings) {
            return false;
        }
        if (null == organizationSettings.mEnvsSettings) {
            return false;
        }
        for(int i = 0; i < organizationSettings.mEnvsSettings.size(); i++){
            if(organizationSettings.mEnvsSettings.get(i).mValue.isEmpty()){
                return false;
            }
        }

        return true;
    }

    /**
     * 获取我的会员的token
     * @param orgCode
     * @return
     */
    public String getMyIntegralToken(String orgCode){
        String token = "";
        OrganizationSettings organizationSettings = getCurrentUserOrgSetting(orgCode);
        if (null != organizationSettings && null != organizationSettings.mEnvsSettings) {
            for(int i = 0; i < organizationSettings.mEnvsSettings.size(); i++){
                if(organizationSettings.mEnvsSettings.get(i).mKey.equalsIgnoreCase("union_member_channel_token")){
                    token = organizationSettings.mEnvsSettings.get(i).mValue;
                }
            }
        }
        return token;
    }

    /**
     * 获取我的会员的channelId
     * @param orgCode
     * @return
     */
    public String getMyIntegralchannelId(String orgCode){
        String token = "";
        OrganizationSettings organizationSettings = getCurrentUserOrgSetting(orgCode);
        if (null != organizationSettings && null != organizationSettings.mEnvsSettings) {
            for(int i = 0; i < organizationSettings.mEnvsSettings.size(); i++){
                if(organizationSettings.mEnvsSettings.get(i).mKey.equalsIgnoreCase("union_member_channel_id")){
                    token = organizationSettings.mEnvsSettings.get(i).mValue;
                }
            }
        }
        return token;
    }

    /**
     * 获取我的会员的链接
     * @param orgCode
     * @return
     */
    public String getMyIntegralBaseUrl(String orgCode){
        String token = "";
        OrganizationSettings organizationSettings = getCurrentUserOrgSetting(orgCode);
        if (null != organizationSettings && null != organizationSettings.mEnvsSettings) {
            for(int i = 0; i < organizationSettings.mEnvsSettings.size(); i++){
                if(organizationSettings.mEnvsSettings.get(i).mKey.equalsIgnoreCase("union_member_url")){
                    token = organizationSettings.mEnvsSettings.get(i).mValue;
                }
            }
        }
        return token;
    }


    /**
     * 是否使用广告唤醒配置
     * @param orgCode
     * @return
     */
    public boolean isAdReAwakenEnabled(String orgCode) {
        if (!onAdSettingsValid(orgCode)) {
            return false;
        }
        return mCurrentUserOrganizationsSettings.get(orgCode).mAdvertisementSettings.mReAwakenEnabled;
    }

    /**
     * 获取广告下一次播放时间
     * @param orgCode
     * @return
     */
    public int getAdReAwakeTime(String orgCode) {
        if (!onAdSettingsValid(orgCode)) {
            return -1;
        }

        return mCurrentUserOrganizationsSettings.get(orgCode).mAdvertisementSettings.mReAwakenMinutes;
    }


    public boolean isAppTopBannerNeedAutoScroll(String orgCode) {
        return 0 < getAppTopBannerIntervalSeconds(orgCode);
    }

    public long getAppTopBannerIntervalSeconds(String orgCode) {
        if (!onAdSettingsValid(orgCode)) {
            return -1;
        }

        return mCurrentUserOrganizationsSettings.get(orgCode).mAdvertisementSettings.mAppTopBannerIntervalSeconds;
    }

    private boolean onAdSettingsValid(String orgCode) {

        OrganizationSettings organizationSettings = getCurrentUserOrgSetting(orgCode);
        if (null == organizationSettings) {
            return false;
        }
        if (null == organizationSettings.mAdvertisementSettings) {
            return false;
        }

        return true;
    }




    /**
     * 获取当前组织的 vpn 线路列表
     * @param context
     * */
    @NonNull
    public List<VpnSettings> getVpnSettingsList(Context context) {
        String orgCode = PersonalShareInfo.getInstance().getCurrentOrg(context);
        OrganizationSettings organizationSettings = getCurrentUserOrgSetting(orgCode);
        if (null == organizationSettings) {
            return new ArrayList<>();
        }

        if(null == organizationSettings.mVpnSettings) {
            return new ArrayList<>();
        }

        return organizationSettings.mVpnSettings;

    }

    public interface OnResultListener {
        void onResult(Boolean success);
    }



}
