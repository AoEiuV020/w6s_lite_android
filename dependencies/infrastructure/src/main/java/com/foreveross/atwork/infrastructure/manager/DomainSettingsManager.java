package com.foreveross.atwork.infrastructure.manager;


import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.model.domain.AppSettingsKt;
import com.foreveross.atwork.infrastructure.model.domain.ChatConnectionMode;
import com.foreveross.atwork.infrastructure.model.domain.DomainSettings;
import com.foreveross.atwork.infrastructure.model.domain.CommonUsingSetting;
import com.foreveross.atwork.infrastructure.model.domain.EnvSettings;
import com.foreveross.atwork.infrastructure.model.domain.PasswordStrength;
import com.foreveross.atwork.infrastructure.model.domain.UpgradeRemindMode;
import com.foreveross.atwork.infrastructure.model.domain.UserSchemaSettingItem;
import com.foreveross.atwork.infrastructure.model.organizationSetting.VolumeSettings;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.language.LanguageSupport;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;

import org.jetbrains.annotations.NotNull;

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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 *
 * Created by reyzhang22 on 16/7/11.
 */
public class DomainSettingsManager {

    private static final String TAG = DomainSettingsManager.class.getSimpleName();

    private static DomainSettingsManager sInstance = new DomainSettingsManager();

    public static final int SYNC_CONTACT_LOCKED = -1;

    public static final int SYNC_CONTACT_PERSONAL = 0;

    public static final int SYNC_CONTACT_UNLIMIT = 1;

    public static final String DOMAIN_SETTINGS_CHANGE = "DOMAIN_SETTINGS_CHANGE";

    public static final long DEFAULT_MAX_MOBILE_CHAT_FILE_UPLOAD_SIZE =  200 * 1024 * 1024L;

    public static final int DEFAULT_DISCUSSION_READ_FEATURE_THRESHOLD =  100;

    public enum TextReadTimeWords {

        Words15,

        Words30,

        Words100,

        Words140,

        ImageRead,

        VoiceRead
    }

    public enum PanSettingsType {

        User,

        Organization,

        InternalDiscussion,

        UserDiscussion
    }

    /**
     * ???????????????????????????(??????????????????????????????????????????????????????????????????????????????????????????????????????6-20?????????)
     * */
    public static final String[] DEFAULT_MIDDLE_PASSWORD_REGEX = new String[]{
            "^(?!\\d+$)(?![a-zA-Z]+$)[\\dA-Za-z]{6,20}$",
            "^(?!((?=[\\x21-\\x7e]+)[^A-Za-z0-9])+$)(?![a-zA-Z]+$)[^\\u4e00-\\u9fa5\\d]{6,20}$",
            "^(?!((?=[\\x21-\\x7e]+)[^A-Za-z0-9])+$)(?!\\d+$)[^\\u4e00-\\u9fa5a-zA-Z]{6,20}$",
            "^(?=.*((?=[\\x21-\\x7e]+)[^A-Za-z0-9]))(?=.*[a-zA-Z])(?=.*[0-9])[^\\u4e00-\\u9fa5]{6,20}$",
            "^(?=.*((?=[\\x21-\\x7e]+)[^A-Za-z0-9]))(?=.*[a-zA-Z])(?=.*[0-9])[^\\u4e00-\\u9fa5]{8,20}$"
    };

    public static DomainSettingsManager getInstance() {
        synchronized (TAG) {
            if (sInstance == null) {
                sInstance = new DomainSettingsManager();
            }
            return sInstance;
        }
    }


    public boolean isUserShowGender() {
        List<UserSchemaSettingItem> schemaList = getUserSchemaSettings();
        UserSchemaSettingItem genderSchema = null;
        for(UserSchemaSettingItem schema : schemaList) {
            if("gender".equals(schema.getProperty())) {
                genderSchema = schema;
                break;
            }
        }

        if(null != genderSchema) {
            return genderSchema.getVisible();
        }

        return false;
    }


    public List<UserSchemaSettingItem> getUserSchemaSettings() {

        return BaseApplicationLike.sDomainSettings.getUserSchemaSettings();
    }


    /**
     * ??????????????????????????????  ????????????
     * @return
     */
    public boolean handleOrgCreatePermission() {
        if (!onOrgSettingsValid()) {
            return true;
        }
        return "anonymous".equalsIgnoreCase(BaseApplicationLike.sDomainSettings.getOrgSettings().getPermission());
    }

    /**
     * ??????????????????"????????????", "????????????"??????,  ????????????
     * @return
     */
    public boolean handleOrgApplyFeature() {
        if (!onOrgSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getOrgSettings().getRequestEnable();
    }



    /**
     * ?????????????????????????????????????????????
     * @return
     */
    public boolean handleUserActivated() {
        if (!onUserSettingsValid()) {
            return true;
        }
        return BaseApplicationLike.sDomainSettings.getUserSettings().getActivated();
    }



    /**
     * ???????????????????????????????????????????????????
     * */
    public CommonUsingSetting handleFirstLoginPasswordSetting() {
        if (!onUserSettingsValid()) {
            return CommonUsingSetting.DISABLED;
        }

        return CommonUsingSetting.valueOfStr(BaseApplicationLike.sDomainSettings.getUserSettings().getResetPassword());
    }

    /**
     * ???????????????????????????????????????????????????
     * */
    public CommonUsingSetting handleFirstLoginGestureLockSetting() {
        if (!onUserSettingsValid()) {
            return CommonUsingSetting.DISABLED;
        }

        return CommonUsingSetting.valueOfStr(BaseApplicationLike.sDomainSettings.getUserSettings().getScreenLock());
    }

    /**
     * ????????????????????????
     * */
    public CommonUsingSetting handleBiometricAuthenticationSetting() {
        if (!onUserSettingsValid()) {
            return CommonUsingSetting.DISABLED;
        }

        return CommonUsingSetting.valueOfStr(BaseApplicationLike.sDomainSettings.getUserSettings().getBiologicalAuth());
//        return CommonUsingSetting.FORCE;
    }

    public PasswordStrength handlePasswordStrength() {
        if (!onUserSettingsValid()) {
            return PasswordStrength.MIDDLE;
        }

        return PasswordStrength.valueOfStr(BaseApplicationLike.sDomainSettings.getUserSettings().getPasswordStrength());
    }

    /**
     * ??????????????????
     * */
    public ChatConnectionMode getChatCheckPermission() {
        if (!onChatSettingsValid()) {
            return ChatConnectionMode.UN_LIMIT;
        }

        return BaseApplicationLike.sDomainSettings.getChatSettings().getConnectionMode();
    }

    /**
     * ?????????????????????????????????
     * */
    public String getConnectionNonsupportPrompt() {
        if (!onChatSettingsValid()) {
            return "?????????????????????????????????????????????????????????";
        }

        return BaseApplicationLike.sDomainSettings.getChatSettings().getConnectionNonsupportPrompt();
    }

    public int getConnectionRetainDays() {
        if (!onChatSettingsValid()) {
            return -1;
        }

        return BaseApplicationLike.sDomainSettings.getChatSettings().getConnectionRetainDays();
    }

    public long getMessagePullLatestTime() {
        int messageRoamingLimitDays = getMessageRoamingLimitDays();
        if(-1 == messageRoamingLimitDays) {
            return -1;
        }

        return TimeUtil.getTimeInMillisDaysBeforeDebug(messageRoamingLimitDays);
    }


    /**
     * ????????????????????????
     * */
    public int getMessageRoamingLimitDays() {
        if (!onChatSettingsValid()) {
            return -1;
        }

        int messageRoamingDays = BaseApplicationLike.sDomainSettings.getChatSettings().getMessageRoamingDays();

        if(0 >= messageRoamingDays) {
            messageRoamingDays = -1;
        }

        return messageRoamingDays;
    }


    /**
     * ????????????????????????
     * */
    public long getMaxUndoTime() {
        if (!onChatSettingsValid()) {
            return 120000;
        }

        long maxUndoTime = BaseApplicationLike.sDomainSettings.getChatSettings().getMaxUndoTime();

        return maxUndoTime;

    }

    /**
     * ????????????????????????????????????
     * */
    public boolean handleLoginAgreementEnable() {
        if (!onUserSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getUserSettings().getUserAgreementEnable();
    }

    /**
     * ?????????????????????????????????
     * */
    public boolean handlePersonalSignatureEnable() {

        if (!onUserSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getUserSettings().getPersonalSignatureEnabled();
    }

    /**
     * ??????????????????????????????
     * */
    public boolean handleFileAssistantEnable() {
        if (!onUserSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getUserSettings().getFileAssistantEnabled();
    }

    public long getUserFavoriteLimit() {
        if (!onUserSettingsValid()) {
            return -1;
        }
        return BaseApplicationLike.sDomainSettings.getUserSettings().getFavoriteTotalLimit();
    }

    /**
     * ??????{@link #handlePasswordStrength()}????????????????????????????????????
     * */
    public String[] handlePasswordRegex() {
        if(!onUserSettingsValid()) {
            return DEFAULT_MIDDLE_PASSWORD_REGEX;
        }

        if(null == BaseApplicationLike.sDomainSettings.getUserSettings().getPasswordRegexs()) {
            return DEFAULT_MIDDLE_PASSWORD_REGEX;
        }


        return BaseApplicationLike.sDomainSettings.getUserSettings().getPasswordRegexs();
    }


    /**
     * ???????????????????????????
     * @return
     */
    public boolean syncWeChatMessageFeature() {
        if (onDomainSettingsInValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getSyncWeChat();
    }

    /**
     * ?????????????????????????????????  ????????????
     * @return
     */
    public boolean handleMobileContactInviteFeature() {
        if (!onUserSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getUserSettings().getMobileContactsEnable();
    }

    /**
     * ?????????????????????????????????????????????
     * @return
     */
    public int handleMobileContactSyncFeature() {
        if (!onUserSettingsValid()) {
            return SYNC_CONTACT_UNLIMIT;
        }
        if ("locked".equalsIgnoreCase(BaseApplicationLike.sDomainSettings.getUserSettings().getContactSync())) {
            return SYNC_CONTACT_LOCKED;
        }
        if ("personal".equalsIgnoreCase(BaseApplicationLike.sDomainSettings.getUserSettings().getContactSync())) {
            return SYNC_CONTACT_PERSONAL;
        }
        return SYNC_CONTACT_UNLIMIT;
    }

    /**
     * ??????????????????
     * @return
     */
    public String handleUserWatermarkFeature() {
        if (!handleWatermarkSettingsFeature() || CustomerHelper.isHighSecurity(BaseApplicationLike.baseContext)) {
            return "none";
        }
        return BaseApplicationLike.sDomainSettings.getWatermarkSettings().getUser();
    }

    /**
     * ??????????????????
     * @return
     */
    public String handleDiscussionWatermarkFeature() {
        if (!handleWatermarkSettingsFeature() || CustomerHelper.isHighSecurity(BaseApplicationLike.baseContext)) {
            return "none";
        }
        return BaseApplicationLike.sDomainSettings.getWatermarkSettings().getDiscussion();
    }

    /**
     * ????????????????????????
     * @return
     * */
    public String handleBingWatermarkFeature() {
        if (!handleWatermarkSettingsFeature() || CustomerHelper.isHighSecurity(BaseApplicationLike.baseContext)) {
            return "none";
        }
        return BaseApplicationLike.sDomainSettings.getWatermarkSettings().getBing();

    }

    /**
     * ??????????????????
     * @return
     * */
    public String handleEmailWatermarkFeature() {
        if (!handleWatermarkSettingsFeature() || CustomerHelper.isHighSecurity(BaseApplicationLike.baseContext)) {
            return "none";
        }
        return BaseApplicationLike.sDomainSettings.getWatermarkSettings().getEmail();

    }

    public String handleFavoriteWaterFeater() {
        if (!handleWatermarkSettingsFeature()) {
            return "none";
        }
        return BaseApplicationLike.sDomainSettings.getWatermarkSettings().getFavorites();
    }

    /**
     * ?????????????????? ????????????
     * @return
     */
    public boolean handleFriendsRelationshipsFeature() {
        if (!onUserSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getUserSettings().getRelationshipsEnable();
    }

    /**
     * ?????????????????????????????? ???????????????
     * @return
     */
    public boolean handleUserOnlineFeature() {
        if (!onUserSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getUserSettings().getOnlineEnabled();
    }

    /**
     * ???????????????????????????????????? ????????????
     * @return
     */
    public boolean handleUsernameModifyFeature() {
        if (!onUserSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getUserSettings().getNameModificationEnable();
    }

    public boolean handleWatermarkSettingsFeature() {
        if (!onWatermarkSettingsValid() || CustomerHelper.isHighSecurity(BaseApplicationLike.baseContext)) {
            return false;
        }
        return true;
    }

    /**
     * ????????????????????????  ????????????
     * @return
     */
    public boolean handleEmailSettingsFeature() {
        if (!onEmailSettingsValid()) {
            return true;
        }
        return BaseApplicationLike.sDomainSettings.getEmailSettings().getEnable();
    }

    /**
     * ????????????????????????
     * ps: debug ???, ???????????????, ??????????????????
     * @return
     */
    public boolean handleEphemeronSettingsFeature() {
        if(BaseApplicationLike.sIsDebug) {
            return true;
        }

        if (!onEphemeronSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getEphemeronSettings().getEnabled();
    }

    /**
     * ???????????????????????????????????????
     * @param textReadTimeWords
     * @return
     */
    public int getEmphemeronReadTime(TextReadTimeWords textReadTimeWords) {
        if (!onEphemeronSettingsValid()) {
            return 0;
        }
        switch (textReadTimeWords) {
            case Words15:
                return BaseApplicationLike.sDomainSettings.getEphemeronSettings().getTextReadTimeWords15();

            case Words30:
                return BaseApplicationLike.sDomainSettings.getEphemeronSettings().getTextReadTimeWords30();

            case Words100:
                return BaseApplicationLike.sDomainSettings.getEphemeronSettings().getTextReadTimeWords100();

            case Words140:
                return BaseApplicationLike.sDomainSettings.getEphemeronSettings().getTextReadTimeWords140();

            case ImageRead:
                return BaseApplicationLike.sDomainSettings.getEphemeronSettings().getImageReadTime();

            case VoiceRead:
                return BaseApplicationLike.sDomainSettings.getEphemeronSettings().getVoiceReadTime();
        }
        return 0;
    }

    /**
     * ?????????????????????????????????
     * @return
     */
    public long getTotalRetentionTime() {
        if (!onEphemeronSettingsValid()) {
            return 0;
        }
        return BaseApplicationLike.sDomainSettings.getEphemeronSettings().getMsgRetentionTime();
    }

    public long getDeletionOnTime() {
        return TimeUtil.getCurrentTimeInMillis() + getTotalRetentionTime();
    }


    /**
     * ????????????????????????????????????
     * */
    public boolean handleChatFileOnlineViewEnabled() {
        if (!onChatFileSettingsValid()) {
            return false;
        }

        if(PersonalShareInfo.getInstance().isChatFileInWhitelist(BaseApplicationLike.baseContext)) {
            return true;
        }

        return BaseApplicationLike.sDomainSettings.getChatFileSettings().getOnlineViewEnabled();
    }

    @NotNull
    private boolean onVolumeSettingsValid() {
        return null != BaseApplicationLike.sDomainSettings.getVolumeSettings();
    }


    /**
     * ??????????????????????????????
     * */
    public boolean handleChatFileTransferEnabled() {
//        if(BaseApplicationLike.sIsDebug) {
//            return true;
//        }

        if (!onChatFileSettingsValid()) {
            return false;
        }

        if(PersonalShareInfo.getInstance().isChatFileInWhitelist(BaseApplicationLike.baseContext)) {
            return true;
        }

        return BaseApplicationLike.sDomainSettings.getChatFileSettings().getTransferEnabled();
    }

    /**
     * ??????????????????????????????
     * */
    public boolean handleChatFileDownloadEnabled() {
        if(BaseApplicationLike.sIsDebug) {
            return true;
        }

        if (!onChatFileSettingsValid()) {
            return false;
        }

        if(PersonalShareInfo.getInstance().isChatFileInWhitelist(BaseApplicationLike.baseContext)) {
            return true;
        }

        return BaseApplicationLike.sDomainSettings.getChatFileSettings().getDownloadEnabled();
    }


    /**
     * ?????????????????????????????????????????????
     * */
    public boolean handleChatFileExternalOpenEnabled() {
        if (!onChatFileSettingsValid()) {
            return false;
        }

        if(PersonalShareInfo.getInstance().isChatFileInWhitelist(BaseApplicationLike.baseContext)) {
            return true;
        }

        return BaseApplicationLike.sDomainSettings.getChatFileSettings().getExternalOpenEnabled();
    }



    /**
     * ????????????????????????????????????
     * @return
     */
    public boolean handleChatFileWatermarkFeature() {
        if (!onChatFileSettingsValid()  || CustomerHelper.isHighSecurity(BaseApplicationLike.baseContext)) {
            return false;
        }

        if(PersonalShareInfo.getInstance().isChatFileInWhitelist(BaseApplicationLike.baseContext)) {
            return true;
        }

        return BaseApplicationLike.sDomainSettings.getChatFileSettings().getShowWatermark();
    }

    /**
     * ??????????????????????????????
     * @return
     */
    public boolean handleChatFileExpiredFeature() {
        if (!onChatFileSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getChatFileSettings().getEnableExpired();
    }

    /**
     * ???????????????????????????
     * @return
     */
    public int getChatFileExpiredDay() {
        if (!onChatFileSettingsValid()) {
            return 7;
        }
        return BaseApplicationLike.sDomainSettings.getChatFileSettings().getRetentionDays();
    }

    /**
     * ??????????????????????????????, ???????????????, ?????????-1
     * */
    public long getChatFileExpiredTime() {
        if(handleChatFileExpiredFeature()) {
            return TimeUtil.getTimeInMillisDaysAfter(getChatFileExpiredDay());
        }

        return -1;
    }

    /**
     * ???????????????????????????
     * @return
     */
    public long getOrgTotalLimit() {
        if (!onPanSettingsValid()) {
            return Long.MAX_VALUE;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getOrgTotalLimit();
    }

    /**
     * ??????????????????????????????
     * @return
     */
    public long getUserTotalLimit() {
        if (!onPanSettingsValid()) {
            return Long.MAX_VALUE;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getUserTotalLimit();
    }

    /**
     * ???????????????????????????
     * @return
     */
    public long getInternalDiscussionTotalLimit() {
        if (!onPanSettingsValid()) {
            return Long.MAX_VALUE;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getInternalDiscussionTotalLimit();
    }

    /**
     * ?????????????????????
     * @return
     */
    public long getUserDiscussionTotalLimit() {
        if (!onPanSettingsValid()) {
            return Long.MAX_VALUE;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getUserDiscussionTotalLimit();
    }

    /**
     * ?????????????????????????????????
     * @return
     */
    public long getOrgItemLimit() {
        if (!onPanSettingsValid()) {
            return Long.MAX_VALUE;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getOrgItemLimit();
    }

    /**
     * ??????????????????????????????
     * @return
     */
    public long getUserItemLimit() {
        if (!onPanSettingsValid()) {
            return Long.MAX_VALUE;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getUserItemLimit();
    }

    /**
     * ?????????????????????????????????
     * @return
     */
    public long getInternalDiscussionItemLimit() {
        if (!onPanSettingsValid()) {
            return Long.MAX_VALUE;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getInternalDiscussionItemLimit();
    }

    /**
     * ?????????????????????????????????
     * @return
     */
    public long getUserDiscussionItemLimit() {
        if (!onPanSettingsValid()) {
            return Long.MAX_VALUE;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getUserDiscussionItemLimit();
    }

    /**
     * ????????????????????????????????????
     * @return
     */
    public boolean handleUserDiscussionEnabledFeature() {
        if (!onPanSettingsValid()) {
            return true;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getUserDiscussionEnabled();
    }


    /**
     * ????????????????????????
     * */
    public boolean handleAssetEnableFeature() {
        if(BaseApplicationLike.sIsDebug) {
            return true;
        }

        if (!onAssetSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getAssetSettings().getAssetsEnabled();
    }

    /**
     * ??????????????????????????????
     * */
    public String getAssetCoinCnName() {
        if (!onAssetSettingsValid()) {
            return StringUtils.EMPTY;
        }

        return BaseApplicationLike.sDomainSettings.getAssetSettings().getCnName();
    }

    /**
     * ??????????????????????????????
     * */
    public String getAssetCoinTwName() {
        if (!onAssetSettingsValid()) {
            return StringUtils.EMPTY;
        }

        return BaseApplicationLike.sDomainSettings.getAssetSettings().getTwName();
    }


    /**
     * ????????????????????????
     * */
    public String getAssetCoinEnName() {
        if (!onAssetSettingsValid()) {
            return StringUtils.EMPTY;
        }

        return BaseApplicationLike.sDomainSettings.getAssetSettings().getEnName();
    }

    /**
     * ????????????????????????1???
     * */
    @Deprecated
    public boolean isUpdateRemindOneTime() {
        if(!onAppSettingsValid()) {
            return false;
        }

        return !BaseApplicationLike.sDomainSettings.getAppSettings().getUpgradeRemind();

    }


    public UpgradeRemindMode getUpgradeRemindMode() {
        if(!onAppSettingsValid()) {
            return UpgradeRemindMode.ONCE;
        }

        String upgradeRemindMode = BaseApplicationLike.sDomainSettings.getAppSettings().getUpgradeRemindMode();
        if(StringUtils.isEmpty(upgradeRemindMode)) {

            if(BaseApplicationLike.sDomainSettings.getAppSettings().getUpgradeRemind()) {
                return UpgradeRemindMode.REPEATED;

            } else {
                return UpgradeRemindMode.ONCE;
            }

        }

        return UpgradeRemindMode.Companion.parse(upgradeRemindMode);

    }

    /**
     * ??????????????????
     * */
    public long getUpdateRemindInterval() {
        if(!onAppSettingsValid()) {
            return AppSettingsKt.DEFAULT_INTERVAL;
        }
//        return 1000;
        return BaseApplicationLike.sDomainSettings.getAppSettings().getUpgradeRemindTime();
    }


    /**
     * ????????????????????????
     * */
    public boolean getLoginDeviceAuthEnable() {
        if(!onAppSettingsValid()) {
            return false;
        }

        return BaseApplicationLike.sDomainSettings.getAppSettings().getUserDeviceAuthEnable();

    }


    /**
     * ???????????????????????????????????????
     * */
    public int getLoginDeviceMaxUnAuthCount() {
        if(!onAppSettingsValid()) {
            return 0;
        }

        return BaseApplicationLike.sDomainSettings.getAppSettings().getUserDeviceMaxUnAuthCount();

    }

    /**
     * ??????????????????????????? 1
     * */
    public String getLoginDeviceUnAuthPrompt() {
        if(!onAppSettingsValid()) {
            return StringUtils.EMPTY;
        }

        switch (LanguageUtil.getLanguageSupport(BaseApplicationLike.baseContext)) {
            case LanguageSupport.TRADITIONAL_CHINESE:
                return BaseApplicationLike.sDomainSettings.getAppSettings().getUserDeviceUnAuthPromptTw();

            case LanguageSupport.ENGLISH:
                return BaseApplicationLike.sDomainSettings.getAppSettings().getUserDeviceUnAuthPromptEn();
        }

        return BaseApplicationLike.sDomainSettings.getAppSettings().getUserDeviceUnAuthPrompt();
    }


    /**
     * ??????????????????????????? 2
     * */
    public String getLoginDeviceRefuseAuthPrompt() {
        if(!onAppSettingsValid()) {
            return StringUtils.EMPTY;
        }

        switch (LanguageUtil.getLanguageSupport(BaseApplicationLike.baseContext)) {
            case LanguageSupport.TRADITIONAL_CHINESE:
                return BaseApplicationLike.sDomainSettings.getAppSettings().getUserDeviceRefuseAuthPromptTw();

            case LanguageSupport.ENGLISH:
                return BaseApplicationLike.sDomainSettings.getAppSettings().getUserDeviceRefuseAuthPromptEn();
        }

        return BaseApplicationLike.sDomainSettings.getAppSettings().getUserDeviceRefuseAuthPrompt();
    }

    public String getEnvValue(String envKey) {
        if (ListUtil.isEmpty(BaseApplicationLike.sDomainSettings.getEnvsSettings())) {
            return StringUtils.EMPTY;
        }
        for(EnvSettings envSetting: BaseApplicationLike.sDomainSettings.getEnvsSettings()) {
            if(envKey.equalsIgnoreCase(envSetting.mKey)) {
                return envSetting.mValue;
            }
        }

        return StringUtils.EMPTY;

    }


    public boolean showJobTitleOnPhoneFloating() {
        return BaseApplicationLike.sDomainSettings.getPhoneAssistant().getShowJobTitle();
    }

    public boolean showDirectlyOrgOnPhoneFloating() {
        return BaseApplicationLike.sDomainSettings.getPhoneAssistant().getShowDirectlyOrg();
    }

    public boolean showDirectlyCorpOnPhoneFloating() {
        return BaseApplicationLike.sDomainSettings.getPhoneAssistant().getShowDirectlyCorp();
    }

    /**
     * ???????????? url
     * */
    public String getInformUrl() {
        if(!onEnvSettinsValid()) {
            return StringUtils.EMPTY;
        }

        for(EnvSettings envSetting: BaseApplicationLike.sDomainSettings.getEnvsSettings()) {
            if("w6s_inform_url".equalsIgnoreCase(envSetting.mKey)) {
                return envSetting.mValue;
            }
        }

        return StringUtils.EMPTY;

    }


    /**
     * ??????zoom h5 basic url
     * */
    public String getZoomBasicUrl() {
        if(!onEnvSettinsValid()) {
            return StringUtils.EMPTY;
        }

        for(EnvSettings envSetting: BaseApplicationLike.sDomainSettings.getEnvsSettings()) {
            if("ZOOM_BASIC_URL".equalsIgnoreCase(envSetting.mKey)) {
                return envSetting.mValue;
            }
        }

        return StringUtils.EMPTY;

    }

    /**
     * ???????????? basic url
     * */
    public String getCheckinBasicApiUrl() {
        if(!onEnvSettinsValid()) {
            return StringUtils.EMPTY;
        }

        for(EnvSettings envSetting: BaseApplicationLike.sDomainSettings.getEnvsSettings()) {
            if("CHECKIN_BASIC_URL".equalsIgnoreCase(envSetting.mKey)) {
                return envSetting.mValue;
            }
        }

        return StringUtils.EMPTY;

    }

    /**
     * ???????????? basic url
     * */
    public String getVoteBasicApiUrl() {
        if(BeeWorks.getInstance().config.isLite()) {
            return StringUtils.EMPTY;
        }


        if(!onEnvSettinsValid()) {
            return StringUtils.EMPTY;
        }

        for(EnvSettings envSetting: BaseApplicationLike.sDomainSettings.getEnvsSettings()) {
            if("VOTE_BASIC_URL".equalsIgnoreCase(envSetting.mKey)) {
                return envSetting.mValue;
            }
        }

        return StringUtils.EMPTY;

    }

    /**
     * ??????????????? basic url
     * */
    public String getColleagueCircleBasicUrl() {
        if(BeeWorks.getInstance().config.isLite()) {
            return StringUtils.EMPTY;
        }


        if(!onEnvSettinsValid()) {
            return StringUtils.EMPTY;
        }

        for(EnvSettings envSetting: BaseApplicationLike.sDomainSettings.getEnvsSettings()) {
            if("COLLEAGUE_CIRCLE_BASIC_URL".equalsIgnoreCase(envSetting.mKey)) {
                return envSetting.mValue;
            }
        }

        return StringUtils.EMPTY;

    }


    public long getMaxMobileChatFileUploadSize() {
        if(!onEnvSettinsValid()) {
            return DEFAULT_MAX_MOBILE_CHAT_FILE_UPLOAD_SIZE;
        }

        for(EnvSettings envSetting: BaseApplicationLike.sDomainSettings.getEnvsSettings()) {
            if("max_mobile_chat_file_upload_size".equalsIgnoreCase(envSetting.mKey)) {
                return Long.parseLong(envSetting.mValue) * 1024 * 1024L;
            }
        }

        return DEFAULT_MAX_MOBILE_CHAT_FILE_UPLOAD_SIZE;
    }

    public int getDiscussionReadFeatureThreshold() {
        if(!onEnvSettinsValid()) {
            return DEFAULT_DISCUSSION_READ_FEATURE_THRESHOLD;
        }

        try {
            for(EnvSettings envSetting: BaseApplicationLike.sDomainSettings.getEnvsSettings()) {
                if("DISCUSSION_READ_FEATURE_THRESHOLD".equalsIgnoreCase(envSetting.mKey)) {
                    return Integer.parseInt(envSetting.mValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return DEFAULT_DISCUSSION_READ_FEATURE_THRESHOLD;
    }


    /**
     * ????????????onlyOffice??????????????????
     * */
    public String getFileOnlinePreviewFeature() {
        if(!onEnvSettinsValid()) {
            return StringUtils.EMPTY;
        }

        for(EnvSettings envSetting: BaseApplicationLike.sDomainSettings.getEnvsSettings()) {
            if("PREVIEW_FILE_SERVICE".equalsIgnoreCase(envSetting.mKey)) {
                return envSetting.mValue;
            }
        }

        return StringUtils.EMPTY;

    }


    public boolean onChatSettingsValid() {
        return (!onDomainSettingsInValid() && BaseApplicationLike.sDomainSettings.getChatSettings() != null);
    }

    public boolean onEmailSettingsValid() {
        return (!onDomainSettingsInValid() && BaseApplicationLike.sDomainSettings.getEmailSettings() != null);
    }

    public boolean onUserSettingsValid() {
        return (!onDomainSettingsInValid() && BaseApplicationLike.sDomainSettings.getUserSettings() != null);
    }

    public boolean onOrgSettingsValid() {
        return (!onDomainSettingsInValid() && BaseApplicationLike.sDomainSettings.getOrgSettings() != null);
    }

    public boolean onWatermarkSettingsValid() {
        return (!onDomainSettingsInValid() && BaseApplicationLike.sDomainSettings.getWatermarkSettings() != null);
    }

    public boolean onEphemeronSettingsValid() {
        return (!onDomainSettingsInValid() && BaseApplicationLike.sDomainSettings.getEphemeronSettings() != null);
    }

    public boolean onChatFileSettingsValid() {
        return (!onDomainSettingsInValid() && BaseApplicationLike.sDomainSettings.getChatFileSettings() != null);
    }

    public boolean onPanSettingsValid() {
        return (!onDomainSettingsInValid() && BaseApplicationLike.sDomainSettings.getPanSettings() != null);
    }

    public boolean onAssetSettingsValid() {
        return (!onDomainSettingsInValid() && BaseApplicationLike.sDomainSettings.getAssetSettings() != null);
    }

    public boolean onAppSettingsValid() {
        return (!onDomainSettingsInValid() && BaseApplicationLike.sDomainSettings.getAppSettings() != null);
    }

    public boolean onEnvSettinsValid() {
        return (!onDomainSettingsInValid() && BaseApplicationLike.sDomainSettings.getEnvsSettings() != null);
    }

    /**
     * ??????domainSettings??????????????????
     * @return
     */
    private boolean onDomainSettingsInValid() {
        DomainSettings domainSettings = BaseApplicationLike.getDomainSetting();
        return null == domainSettings;
    }

}
