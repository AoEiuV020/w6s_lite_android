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
     * 默认的密码校验规则(中级—密码包含数字、大写字母、小写字母或其它特殊符号中的两种，长度在6-20位之间)
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
     * 处理开启创建组织功能  默认开启
     * @return
     */
    public boolean handleOrgCreatePermission() {
        if (!onOrgSettingsValid()) {
            return true;
        }
        return "anonymous".equalsIgnoreCase(BaseApplicationLike.sDomainSettings.getOrgSettings().getPermission());
    }

    /**
     * 处理开启申请"加入组织", "退出组织"功能,  默认开启
     * @return
     */
    public boolean handleOrgApplyFeature() {
        if (!onOrgSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getOrgSettings().getRequestEnable();
    }



    /**
     * 处理用户激活状态显示，默认显示
     * @return
     */
    public boolean handleUserActivated() {
        if (!onUserSettingsValid()) {
            return true;
        }
        return BaseApplicationLike.sDomainSettings.getUserSettings().getActivated();
    }



    /**
     * 获取初次登录是否修改登录密码的设置
     * */
    public CommonUsingSetting handleFirstLoginPasswordSetting() {
        if (!onUserSettingsValid()) {
            return CommonUsingSetting.DISABLED;
        }

        return CommonUsingSetting.valueOfStr(BaseApplicationLike.sDomainSettings.getUserSettings().getResetPassword());
    }

    /**
     * 获取初次登录是否修改手势密码的设置
     * */
    public CommonUsingSetting handleFirstLoginGestureLockSetting() {
        if (!onUserSettingsValid()) {
            return CommonUsingSetting.DISABLED;
        }

        return CommonUsingSetting.valueOfStr(BaseApplicationLike.sDomainSettings.getUserSettings().getScreenLock());
    }

    /**
     * 获取生物认证设置
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
     * 发送消息权限
     * */
    public ChatConnectionMode getChatCheckPermission() {
        if (!onChatSettingsValid()) {
            return ChatConnectionMode.UN_LIMIT;
        }

        return BaseApplicationLike.sDomainSettings.getChatSettings().getConnectionMode();
    }

    /**
     * 发送消息权限相关警告语
     * */
    public String getConnectionNonsupportPrompt() {
        if (!onChatSettingsValid()) {
            return "已开启发消息权限，请通过其他方式联系。";
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
     * 消息拉取时间限制
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
     * 撤回消息最大间隔
     * */
    public long getMaxUndoTime() {
        if (!onChatSettingsValid()) {
            return 120000;
        }

        long maxUndoTime = BaseApplicationLike.sDomainSettings.getChatSettings().getMaxUndoTime();

        return maxUndoTime;

    }

    /**
     * 是否启用登录签协议的功能
     * */
    public boolean handleLoginAgreementEnable() {
        if (!onUserSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getUserSettings().getUserAgreementEnable();
    }

    /**
     * 是否启用个人签名的功能
     * */
    public boolean handlePersonalSignatureEnable() {

        if (!onUserSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getUserSettings().getPersonalSignatureEnabled();
    }

    /**
     * 是否开启文件传输助手
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
     * 返回{@link #handlePasswordStrength()}密码等级对应的正则表达式
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
     * 同步第三方数据接口
     * @return
     */
    public boolean syncWeChatMessageFeature() {
        if (onDomainSettingsInValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getSyncWeChat();
    }

    /**
     * 处理同步通讯录邀请功能  默认开启
     * @return
     */
    public boolean handleMobileContactInviteFeature() {
        if (!onUserSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getUserSettings().getMobileContactsEnable();
    }

    /**
     * 同步通讯录到手机功能，默认开启
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
     * 用户水印功能
     * @return
     */
    public String handleUserWatermarkFeature() {
        if (!handleWatermarkSettingsFeature() || CustomerHelper.isHighSecurity(BaseApplicationLike.baseContext)) {
            return "none";
        }
        return BaseApplicationLike.sDomainSettings.getWatermarkSettings().getUser();
    }

    /**
     * 群组水印功能
     * @return
     */
    public String handleDiscussionWatermarkFeature() {
        if (!handleWatermarkSettingsFeature() || CustomerHelper.isHighSecurity(BaseApplicationLike.baseContext)) {
            return "none";
        }
        return BaseApplicationLike.sDomainSettings.getWatermarkSettings().getDiscussion();
    }

    /**
     * 必应消息水印功能
     * @return
     * */
    public String handleBingWatermarkFeature() {
        if (!handleWatermarkSettingsFeature() || CustomerHelper.isHighSecurity(BaseApplicationLike.baseContext)) {
            return "none";
        }
        return BaseApplicationLike.sDomainSettings.getWatermarkSettings().getBing();

    }

    /**
     * 邮箱水印功能
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
     * 开启好友功能 默认开启
     * @return
     */
    public boolean handleFriendsRelationshipsFeature() {
        if (!onUserSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getUserSettings().getRelationshipsEnable();
    }

    /**
     * 是否显示在线离线状态 默认不开启
     * @return
     */
    public boolean handleUserOnlineFeature() {
        if (!onUserSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getUserSettings().getOnlineEnabled();
    }

    /**
     * 处理开启修改用户名称功能 默认开启
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
     * 处理开启邮箱功能  默认开启
     * @return
     */
    public boolean handleEmailSettingsFeature() {
        if (!onEmailSettingsValid()) {
            return true;
        }
        return BaseApplicationLike.sDomainSettings.getEmailSettings().getEnable();
    }

    /**
     * 是否启用阅后即焚
     * ps: debug 下, 为方便调试, 默认打开开关
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
     * 通过条件获取读取焚毁倒计时
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
     * 获取阅后即焚保留总时间
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
     * 是否允许聊天文件在线预览
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
     * 是否允许聊天文件转发
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
     * 是否允许聊天文件下载
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
     * 是否允许聊天文件使用第三方打开
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
     * 是否开启聊天文件水印功能
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
     * 是否开启文件过期策略
     * @return
     */
    public boolean handleChatFileExpiredFeature() {
        if (!onChatFileSettingsValid()) {
            return false;
        }
        return BaseApplicationLike.sDomainSettings.getChatFileSettings().getEnableExpired();
    }

    /**
     * 获取文件过期时间段
     * @return
     */
    public int getChatFileExpiredDay() {
        if (!onChatFileSettingsValid()) {
            return 7;
        }
        return BaseApplicationLike.sDomainSettings.getChatFileSettings().getRetentionDays();
    }

    /**
     * 获取文件具体过期时间, 若开关关闭, 则返回-1
     * */
    public long getChatFileExpiredTime() {
        if(handleChatFileExpiredFeature()) {
            return TimeUtil.getTimeInMillisDaysAfter(getChatFileExpiredDay());
        }

        return -1;
    }

    /**
     * 获取部门群网盘总量
     * @return
     */
    public long getOrgTotalLimit() {
        if (!onPanSettingsValid()) {
            return Long.MAX_VALUE;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getOrgTotalLimit();
    }

    /**
     * 获取用户个人网盘总量
     * @return
     */
    public long getUserTotalLimit() {
        if (!onPanSettingsValid()) {
            return Long.MAX_VALUE;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getUserTotalLimit();
    }

    /**
     * 获取内部群网盘总量
     * @return
     */
    public long getInternalDiscussionTotalLimit() {
        if (!onPanSettingsValid()) {
            return Long.MAX_VALUE;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getInternalDiscussionTotalLimit();
    }

    /**
     * 获取普通群总量
     * @return
     */
    public long getUserDiscussionTotalLimit() {
        if (!onPanSettingsValid()) {
            return Long.MAX_VALUE;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getUserDiscussionTotalLimit();
    }

    /**
     * 获取部门群单个文件限制
     * @return
     */
    public long getOrgItemLimit() {
        if (!onPanSettingsValid()) {
            return Long.MAX_VALUE;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getOrgItemLimit();
    }

    /**
     * 获取个人单个文件限制
     * @return
     */
    public long getUserItemLimit() {
        if (!onPanSettingsValid()) {
            return Long.MAX_VALUE;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getUserItemLimit();
    }

    /**
     * 获取内部群单个文件限制
     * @return
     */
    public long getInternalDiscussionItemLimit() {
        if (!onPanSettingsValid()) {
            return Long.MAX_VALUE;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getInternalDiscussionItemLimit();
    }

    /**
     * 获取普通群单个文件限制
     * @return
     */
    public long getUserDiscussionItemLimit() {
        if (!onPanSettingsValid()) {
            return Long.MAX_VALUE;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getUserDiscussionItemLimit();
    }

    /**
     * 获取普通群是否开启群文件
     * @return
     */
    public boolean handleUserDiscussionEnabledFeature() {
        if (!onPanSettingsValid()) {
            return true;
        }
        return BaseApplicationLike.sDomainSettings.getPanSettings().getUserDiscussionEnabled();
    }


    /**
     * 红包功能是否开启
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
     * 获取金币简体中文名字
     * */
    public String getAssetCoinCnName() {
        if (!onAssetSettingsValid()) {
            return StringUtils.EMPTY;
        }

        return BaseApplicationLike.sDomainSettings.getAssetSettings().getCnName();
    }

    /**
     * 获取金币繁体中文名字
     * */
    public String getAssetCoinTwName() {
        if (!onAssetSettingsValid()) {
            return StringUtils.EMPTY;
        }

        return BaseApplicationLike.sDomainSettings.getAssetSettings().getTwName();
    }


    /**
     * 获取金币英文名字
     * */
    public String getAssetCoinEnName() {
        if (!onAssetSettingsValid()) {
            return StringUtils.EMPTY;
        }

        return BaseApplicationLike.sDomainSettings.getAssetSettings().getEnName();
    }

    /**
     * 是否更新仅仅提醒1次
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
     * 更新提醒间隔
     * */
    public long getUpdateRemindInterval() {
        if(!onAppSettingsValid()) {
            return AppSettingsKt.DEFAULT_INTERVAL;
        }
//        return 1000;
        return BaseApplicationLike.sDomainSettings.getAppSettings().getUpgradeRemindTime();
    }


    /**
     * 登录设备认证开关
     * */
    public boolean getLoginDeviceAuthEnable() {
        if(!onAppSettingsValid()) {
            return false;
        }

        return BaseApplicationLike.sDomainSettings.getAppSettings().getUserDeviceAuthEnable();

    }


    /**
     * 登录设备免认证最大设备数量
     * */
    public int getLoginDeviceMaxUnAuthCount() {
        if(!onAppSettingsValid()) {
            return 0;
        }

        return BaseApplicationLike.sDomainSettings.getAppSettings().getUserDeviceMaxUnAuthCount();

    }

    /**
     * 登录设备管理提示语 1
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
     * 登录设备管理提示语 2
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
     * 获取投诉 url
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
     * 获取zoom h5 basic url
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
     * 获取考勤 basic url
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
     * 获取投票 basic url
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
     * 获取同事圈 basic url
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
     * 是否使用onlyOffice进行在线预览
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
     * 验证domainSettings是否为空处理
     * @return
     */
    private boolean onDomainSettingsInValid() {
        DomainSettings domainSettings = BaseApplicationLike.getDomainSetting();
        return null == domainSettings;
    }

}
