package com.foreveross.atwork.infrastructure.shared;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.organizationSetting.OrganizationSettings;
import com.foreveross.atwork.infrastructure.model.contact.ContactViewMode;
import com.foreveross.atwork.infrastructure.model.employee.DataSchema;
import com.foreveross.atwork.infrastructure.model.wallet.WalletAccount;
import com.foreveross.atwork.infrastructure.newmessage.post.ack.AckPostMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dasunsy on 15/12/25.
 * <p>
 * 每个帐号区分开的 sp, 不会因为登出而清除数据
 */
public class PersonalShareInfo {
    public static final Object sLock = new Object();

    private static final String SP_IN_PRIVATE_USER = "_sp_in_private_user" + AtworkConfig.SP_SUFFIX_COMMON_AND_PERSONAL_FILE;
    /**
     * 最新一条消息时间
     */
    private static final String LATEST_MESSAGE_TIME = "LATEST_MESSAGE_TIME";
    private static final String LATEST_MESSAGE_ID = "LATEST_MESSAGE_ID";
    private static final String DATA_SCHEMA = "DATA_SCHEMA";


    private static final String FIRST_CLICK_UNDO_MESSAGE = "first_click_undo_message";

    private static final String GESTURE_CODE_LOCK = "GESTURE_CODE_LOCK";
    private static final String SETTING_GESTURE_CODE_LOCK = "SETTING_GESTURE_CODE_LOCK";

    /**
     * 手势密码连续错误次数
     * */
    private static final String GESTURE_CODE_LOCK_FAIL_CONTINUOUS_COUNT = "GESTURE_CODE_LOCK_FAIL_CONTINUOUS_COUNT";

    /**
     * 是否开启指纹
     * */
    private static final String SETTING_FINGER_PRINT = "SETTING_FINGER_PRINT";

    /**
     * 是否app 内部功能需要二次保护
     * */
    private static final String SETTING_BIOMETRIC_AUTHENTICATION_PROTECT = "SETTING_BIOMETRIC_AUTHENTICATION_PROTECT";

    /**
     * 保护时间
     * */
    private static final String SETTING_BIOMETRIC_AUTHENTICATION_DURATION = "SETTING_BIOMETRIC_AUTHENTICATION_DURATION";

    /**
     * 二次保护的内容
     */
    private static final String CONTENT_BIOMETRIC_AUTHENTICATION_PROTECT = "CONTENT_BIOMETRIC_AUTHENTICATION_PROTECT";

    private static final String LOGIN_USER_CUR_ORG = "LOGIN_USER_CUR_ORG";

    private static final String AUDIO_PLAY_MODE = "AUDIO_PLAY_MODE";

    private static final String LAST_ENCRYPT_MODE = "LAST_ENCRYPT_MODE";

    private static final String ORG_OUT_FIELD_PUNCH_REQUEST_CODE = "_OUT_FIELD_PUNCH_REQUEST_CODE_";

    private static final String ORG_OUT_FIELD_PUNCH_INTERVAL_TIME = "_ORG_OUT_FIELD_PUNCH_INTERVAL_TIME_";

    /**
     * 重设模式, 登录后需要强制进入的界面(-1 表示不需要)
     */
    private static final String RESET_MODE = "RESET_MODE";

    private static final String REMOVE_ACK_NEED_CHECK = "REMOVE_ACK_NEED_CHECK";

    /**
     * 是否提醒过设置生物认证
     * */
    private static final String NOTICED_TO_SET_BIOMETRIC_AUTHENTICATION = "NOTICED_TO_SET_BIOMETRIC_AUTHENTICATION";


    /**
     * 是否签署过协议
     */
    private static final String LOGIN_SIGN_AGREEMENT = "LOGIN_SIGN_AGREEMENT";

    /**
     * 保密协议页面强制打开
     */
    private static final String AGREEMENT_INTERCEPT_FORCED = "AGREEMENT_INTERCEPT_FORCED";


    /**
     * 是否需要检查签署协议
     */
    private static final String NEED_CHECK_SIGN_AGREEMENT = "NEED_CHECK_SIGN_AGREEMENT";


    /**
     * 必应消息是否做过同步处理, 针对低版本升级高版本后, 仍然能完整显示出必应消息出来
     */
    private static final String BING_SYNC = "BING_SYNC";

    /**
     * 上次必应消息同步的时间点
     */
    private static final String LAST_BING_SYNC_TIME = "LAST_BING_SYNC_TIME";


    /**
     * 主界面底部弹出操作框的引导滑动的"手指"提醒次数
     */
    private static final String MAIN_FAB_BOTTOM_SLIDE_NOTICE_FINGER_SHOWN_COUNT = "MAIN_FAB_BOTTOM_SLIDE_NOTICE_FINGER_SHOWN_COUNT";

    /**
     * 必应消息初次使用时, 引导使用必应消息的透明页面
     */
    private static final String MAIN_CHAT_VIEW_BING_FIRST_GUIDE_PAGE_SHOWN_COUNT = "MAIN_CHAT_VIEW_GUIDE_PAGE_SHOWN_COUNT";


    /**
     * 通讯录模式切换初次使用时, 引导使用通讯录模式切换的透明页面
     */
    private static final String MAIN_COTACT_VIEW_MODE_SWITCH_FIRST_GUIDE_PAGE_SHOWN_COUNT = "MAIN_COTACT_VIEW_MODE_SWITCH_FIRST_GUIDE_PAGE_SHOWN_COUNT";


    /**
     * 工作台初次使用时, 引导使用的页面
     * */
    private static final String WORKBENCH_FIRST_GUIDE_PAGE_SHOWN_COUNT = "WORKBENCH_FIRST_GUIDE_PAGE_SHOWN_COUNT";


    /**
     * "应用"初次使用时, 引导使用的页面
     * */
    private static final String APP_TAB_FIRST_GUIDE_PAGE_SHOWN_COUNT = "APP_TAB_FIRST_GUIDE_PAGE_SHOWN_COUNT";


    /**
     * "我"初次使用时, 引导使用的页面
     * */
    private static final String ME_TAB_FIRST_GUIDE_PAGE_SHOWN_COUNT = "ME_TAB_FIRST_GUIDE_PAGE_SHOWN_COUNT";


    /**
     * 默认需要的提醒次数, 暂时只需要1次
     */
    public static final int DEFAULT_SHOULD_MAIN_FAB_BOTTOM_SLIDE_NOTICE_FINGER_SHOWN_COUNT = 1;


    /**
     * url 悬浮窗持有
     */
    public static final String URL_HOOKING_FOR_FLOAT = "URL_HOOKING_FOR_FLOAT";

    public static final String WALLET_ACCOUNT = "WALLET_ACCOUNT";

    /**
     * 网盘是否需要全部重新拉取
     */
    public static final String DROPBOX_FORCE_ALL_REFRESH = "DROPBOX_FORCE_ALL_REFRESH";

    /**
     * 应用界面顶部 banner 展示统计
     */
    public static final String DATA_APP_TOP_BANNER_DISPLAY_RECORD = "DATA_APP_TOP_BANNER_DISPLAY_RECORD";

    /**
     * webview 悬浮按钮控制
     * -1: 关闭
     * 0: 左下方悬浮
     * 1: 右下方悬浮
     */
    public static final String WEBVIEW_FLOAT_ACTION_SETTING = "WEBVIEW_FLOAT_ACTION_SETTING";


    /**
     * 通讯录视图模式
     * 0: 经典版
     * 1: 简洁版
     */
    public static final String CONTACT_VIEW_MODE = "CONTACT_VIEW_MODE";


    /**
     * 是否在文件下载白名单
     */
    public static final String CHAT_FILE_IN_WHITELIST = "CHAT_FILE_IN_WHITELIST";


    /**
     * 新的好友申请对应的 userId 列表
     */
    public static final String NEW_APPLY_FRIEND_USER_IDS = "NEW_APPLY_FRIEND_USER_IDS";

    /**
     * 是否长按删除了群聊助手
     * */
    public static final String HIDE_DISCUSSION_HELPER = "HIDE_DISCUSSION_HELPER";

    /**
     * 是否开启了pc登录手机静音模式
     */
    public static final String DEVICE_ONLINE_MUTE_MODE = "DEVICE_ONLINE_MUTE_MODE";
    /**
     * 是否pc在线
     */
    public static final String IS_PC_ONLINE = "IS_PC_ONLINE";

    /**
     * PC端在线的系统（win/mac）
     */
    public static final String INTENT_ONLINE_DEVICE_SYSTEM = "INTENT_ONLINE_DEVICE_SYSTEM";

    public static final String PC_DEVICE_ID = "PC_DEVICE_ID";


    /**
     * 内宣号上一次点击打开进入的时间
     * */
    public static final String LAST_TIME_ENTER_ANNOUNCE_APP = "LAST_TIME_ENTER_ANNOUNCE_APP";

    public static final String KEY_STOP_VERIFY = "KEY_STOP_VERIFY";

    private static final String DOC_TRANSFER_NOTICE = "DOC_TRANSFER_NOTICE";
    private static final String DOC_CUSTOMER_VOLUME_ENABLE = "DOC_CUSTOMER_VOLUME_ENABLE";

    //----------------------SETTING ----------------------------
    private static final String SETTING_NOTICE = "SETTING_NOTICE";
    private static final String SETTING_VOICE = "SETTING_VOICE";
    private static final String SETTING_VIBRATE = "SETTING_VIBRATE";
    private static final String SETTING_SHOW_DETAIL = "SETTING_SHOW_DETAIL";
    private static final String SETTING_SXF_VPN = "SETTING_SXF_VPN";
    private static final String SETTING_IP_DOMAIN = "SETTING_IP_DOMAIN";
    private static final String SETTING_TEXT_SIZE = "SETTING_TEXT_SIZE";
    private static final String SETTING_WEBVIEW_TEXT_SIZE = "SETTING_WEBVIEW_TEXT_SIZE";
    private static final String SETTING_COMMON_TEXT_SIZE_SYNC_WEBVIEW = "SETTING_COMMON_TEXT_SIZE_SYNC_WEBVIEW";
    private static final String SETTING_DISCUSSION_HELPER = "SETTING_DISCUSSION_HELPER";
    //智能机器人设置
    private static final String SETTING_SERVER_ROBOT_SWITCH = "SETTING_SERVER_ROBOT_SWITCH";
    private static final String SETTING_ROBOT_SWITCH = "SETTING_ROBOT_SWITCH";
    private static final String SETTING_ROBOT_SHARKE_OPEN = "SETTING_ROBOT_SHARKE_OPEN";
    private static final String SETTING_ROBOT_CLICK_OPEN = "SETTING_ROBOT_CLICK_OPEN";
    private static final String SETTING_ROBOT_ORDER_UPDATE_TIME = "SETTING_ROBOT_ORDER_UPDATE_TIME";

    private static final String DROPBOX_REFRESH_ID = "DROPBOX_REFRESH_ID_";

    private static final String LAST_SHORTCUT_REFRESH_TIME = "LAST_SHORTCUT_REFRESH_TIME";

    private static final String STOP_POLLING_EMAIL_ACCOUNT = "STOP_POLLING_EMAIL_ACCOUNT";


    private static PersonalShareInfo sInstance;

    private long mLatestMessageReceivedTime = -1;

    private List<DataSchema> mDsList = new ArrayList<>();


    public static PersonalShareInfo getInstance() {
        synchronized (sLock) {
            if (null == sInstance) {
                sInstance = new PersonalShareInfo();
            }
            return sInstance;
        }
    }

    public void clear() {
        mLatestMessageReceivedTime = -1;
        mDsList.clear();
    }

    public void clearAbsolutely(Context context, String username) {
        clear();
        PreferencesUtils.clear(context, getPersonalSpName(username));
    }


    public void setDataSchema(Context context, List<DataSchema> dataSchemaList) {
        mDsList.clear();
        if (!ListUtil.isEmpty(dataSchemaList)) {
            mDsList.addAll(dataSchemaList);

        }
        String dsJson = new Gson().toJson(dataSchemaList, new TypeToken<ArrayList<DataSchema>>() {
        }.getType());
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putString(context, getPersonalSpName(username), DATA_SCHEMA, dsJson);
    }

    public List<DataSchema> getDataSchema(Context context) {
        if (ListUtil.isEmpty(mDsList)) {
            String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
            String dsJson = PreferencesUtils.getString(context, getPersonalSpName(username), DATA_SCHEMA);
            List<DataSchema> dataSchemaList = new Gson().fromJson(dsJson, new TypeToken<ArrayList<DataSchema>>() {
            }.getType());

            if (!ListUtil.isEmpty(dataSchemaList)) {
                mDsList.addAll(dataSchemaList);

            }
        }

        return mDsList;
    }

    public void resetEncryptMode(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        resetEncryptMode(context, username);
    }

    public void resetEncryptMode(Context context, String username) {
        setEncryptMode(context, username, AtworkConfig.getEncryptModeConfig());
    }

    public void setEncryptMode(Context context, String username, int encryptMode) {

        PreferencesUtils.putInt(context, getPersonalSpName(username), LAST_ENCRYPT_MODE, encryptMode);
    }

    public int getEncryptMode(Context context, String username) {
        int defaultEncryptMode;
        if (LoginUserInfo.getInstance().hasLoginBefore(context)) {
            defaultEncryptMode = AtworkConfig.FLAG_NO_ENCRYPTION;

        } else {
            defaultEncryptMode = AtworkConfig.getEncryptModeConfig();
        }

        return PreferencesUtils.getInt(context, getPersonalSpName(username), LAST_ENCRYPT_MODE, defaultEncryptMode);
    }

    public void setLatestMessageTime(Context context, long latestMessageTime, @Nullable String messageId) {
        this.mLatestMessageReceivedTime = latestMessageTime;
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putLong(context, getPersonalSpName(username), LATEST_MESSAGE_TIME, latestMessageTime);

        if (null != messageId) {
            PreferencesUtils.putString(context, getPersonalSpName(username), LATEST_MESSAGE_ID, messageId);
        }
    }

    public long getLatestMessageTime(Context context) {
        if (mLatestMessageReceivedTime == -1) {
            String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
            mLatestMessageReceivedTime = PreferencesUtils.getLong(context, getPersonalSpName(username), LATEST_MESSAGE_TIME);
        }

        /**因未读, 但过期的消息需要拉取回来, 所以此处暂时不做比较, 让消息都拉取回来, 再本地做判断*/
//        long fetchInDayTimeDomainSetting = DomainSettingsManager.getInstance().getMessagePullLatestTime();
//
//        if(fetchInDayTimeDomainSetting > mLatestMessageReceivedTime) {
//            mLatestMessageReceivedTime = fetchInDayTimeDomainSetting;
//        }


        return mLatestMessageReceivedTime;
    }

    public String getLatestMessageId(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getString(context, getPersonalSpName(username), LATEST_MESSAGE_ID);
    }

    public void setLockCode(Context context, String lockCode) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putString(context, getPersonalSpName(username), GESTURE_CODE_LOCK, lockCode);

    }

    public String getLockCode(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getString(context, getPersonalSpName(username), GESTURE_CODE_LOCK);
    }

    public void setLockCodeSetting(Context context, boolean lock) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), SETTING_GESTURE_CODE_LOCK, lock);
    }

    public boolean getLockCodeSetting(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), SETTING_GESTURE_CODE_LOCK, false);
    }

    public int getGestureCodeLockFailContinuousCount(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getInt(context, getPersonalSpName(username), GESTURE_CODE_LOCK_FAIL_CONTINUOUS_COUNT, 0);
    }

    public void increaseGestureCodeLockFailContinuousCount(Context context) {
        setGestureCodeLockFailContinuousCount(context, getGestureCodeLockFailContinuousCount(context) + 1);
    }

    public void setGestureCodeLockFailContinuousCount(Context context, int count) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putInt(context, getPersonalSpName(username), GESTURE_CODE_LOCK_FAIL_CONTINUOUS_COUNT, count);
    }

    public void setFingerPrintSetting(Context context, boolean lock) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), SETTING_FINGER_PRINT, lock);
    }

    public boolean getFingerPrintSetting(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), SETTING_FINGER_PRINT, false);
    }


    public void setBiometricAuthenticationProtectSetting(Context context, int type) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putInt(context, getPersonalSpName(username), SETTING_BIOMETRIC_AUTHENTICATION_PROTECT, type);
    }

    public int getBiometricAuthenticationProtectSetting(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getInt(context, getPersonalSpName(username), SETTING_BIOMETRIC_AUTHENTICATION_PROTECT, -1);
    }

    public void setBiometricAuthenticationDuration(Context context, long duration) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putLong( context, getPersonalSpName(username), SETTING_BIOMETRIC_AUTHENTICATION_DURATION, duration);

    }

    public long getBiometricAuthenticationDuration(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getLong( context, getPersonalSpName(username), SETTING_BIOMETRIC_AUTHENTICATION_DURATION, 0L);

    }

    public void setBiometricAuthenticationProtectContent(Context context, int value) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putInt(context, getPersonalSpName(username), CONTENT_BIOMETRIC_AUTHENTICATION_PROTECT, value);
    }

    public int getBiometricAuthenticationProtectContent(Context context, int defaultValue) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getInt(context, getPersonalSpName(username), CONTENT_BIOMETRIC_AUTHENTICATION_PROTECT, defaultValue);
    }

    public void setFirstClickUndoMessage(Context context, boolean isFirst) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), FIRST_CLICK_UNDO_MESSAGE, isFirst);
    }

    public boolean getFirstClickUndoMessage(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), FIRST_CLICK_UNDO_MESSAGE, true);
    }

    /**
     * 保存语音播放的模式
     *
     * @param context
     * @param isSpeakerMode
     */
    public void setAudioPlayMode(Context context, boolean isSpeakerMode) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), AUDIO_PLAY_MODE, isSpeakerMode);

    }

    /**
     * 获取当前语音播放的模式, 默认为扬声器模式
     *
     * @param context
     */
    public boolean isAudioPlaySpeakerMode(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), AUDIO_PLAY_MODE, true);
    }


    /**
     * 设置当前的组织架构信息
     *
     * @param context
     * @param currentOrg
     */
    public void setCurrentOrg(Context context, String currentOrg) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putString(context, getPersonalSpName(username), LOGIN_USER_CUR_ORG, currentOrg);

    }

    /**
     * 获取当前用户的组织架构
     */
    public String getCurrentOrg(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getString(context, getPersonalSpName(username), LOGIN_USER_CUR_ORG, StringUtils.EMPTY);
    }


    public Map<String, OrganizationSettings> getCurrentUserOrganizationsSettings(Context context) {
        String userId = LoginUserInfo.getInstance().getLoginUserId(context);

        String data = OrgPersonalShareInfo.getInstance().getOrganizationsSettingsData(context, userId);
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        return new Gson().fromJson(data, new TypeToken<Map<String, OrganizationSettings>>() {
        }.getType());
    }

    public void setCurrentUserOrganizationsSettings(Context context, String data) {
        String userId = LoginUserInfo.getInstance().getLoginUserId(context);

        OrgPersonalShareInfo.getInstance().setOrganizationsSettingsData(context, userId, data);
    }


    /**
     * 保存通知设置
     *
     * @param context
     * @param isNotice
     */
    public void setSettingNotice(Context context, boolean isNotice) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), SETTING_NOTICE, isNotice);
    }

    public boolean getSettingNotice(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), SETTING_NOTICE, true);
    }

    /**
     * 保存震动设置
     *
     * @param context
     * @param isVibrate
     */
    public void setSettingVibrate(Context context, boolean isVibrate) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), SETTING_VIBRATE, isVibrate);

    }

    public boolean getSettingVibrate(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), SETTING_VIBRATE, AtworkConfig.DEFAULT_VIBRATOR_SETTING);
    }

    public void setSettingShowDetails(Context context, boolean showDetail) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), SETTING_SHOW_DETAIL, showDetail);
    }

    public boolean getSettingShowDetails(Context context) {
        if(AtworkConfig.NOTIFICATION_CONFIG.isCommandHideDetail()) {
            return false;
        }

        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), SETTING_SHOW_DETAIL, true);
    }

    /**
     * 保存声音设置
     *
     * @param context
     * @param isVoice
     */
    public void setSettingVoice(Context context, boolean isVoice) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), SETTING_VOICE, isVoice);
    }

    public boolean getSettingVoice(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), SETTING_VOICE, AtworkConfig.DEFAULT_VOICE_SETTING);

    }

    /**
     * 保存深信服vpn
     *
     * @param context
     * @param vpn
     */
    public void settingSXFVpn(Context context, boolean vpn) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), SETTING_SXF_VPN, vpn);
    }

    public boolean getSettingSXFVpn(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), SETTING_SXF_VPN, false);
    }


    /**
     * 保存域 ip 切换
     *
     * @param context
     * @param switcher
     */
    public void settingIPDomainSwitch(Context context, boolean switcher) {

        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), SETTING_IP_DOMAIN, switcher);
    }


    public boolean getSettingIPDomainSwitch(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), SETTING_IP_DOMAIN, false);
    }

    public void setTextSizeLevel(Context context, int level) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putInt(context, getPersonalSpName(username), SETTING_TEXT_SIZE, level);
    }

    public int getTextSizeLevel(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getInt(context, getPersonalSpName(username), SETTING_TEXT_SIZE, AtworkConfig.DEFAULT_TEXT_SIZE);
    }


    public void setWebviewTextSizeLevel(Context context, int level) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putInt(context, getPersonalSpName(username), SETTING_WEBVIEW_TEXT_SIZE, level);
    }

    public int getWebviewTextSizeLevel(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getInt(context, getPersonalSpName(username), SETTING_WEBVIEW_TEXT_SIZE, 1);
    }

    public void setCommonTextSizeSyncWebview(Context context, boolean open) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), SETTING_COMMON_TEXT_SIZE_SYNC_WEBVIEW, open);
    }

    public boolean getCommonTextSizeSyncWebview(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), SETTING_COMMON_TEXT_SIZE_SYNC_WEBVIEW, AtworkConfig.WEBVIEW_CONFIG.isTextZoomOnCommonTextSizeSetting());
    }

    public boolean isTextSizedOverStandard(Context context) {
        return getTextSizeLevel(context) > 1;
    }

    public boolean isDeviceOnlineMuteMode(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), DEVICE_ONLINE_MUTE_MODE, true);
    }

    public void setDeviceOnlineMuteMode(Context context, boolean isMute) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), DEVICE_ONLINE_MUTE_MODE, isMute);
    }


    public boolean isPCOnline(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), IS_PC_ONLINE, false);
    }

    public void setIsPCOnline(Context context, boolean isOnline) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), IS_PC_ONLINE, isOnline);
    }

    public void setIsPCOnline(Context context, boolean isOnline, String deviceSystem, String pcDeviceId) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), IS_PC_ONLINE, isOnline);
        PreferencesUtils.putString(context, getPersonalSpName(username), INTENT_ONLINE_DEVICE_SYSTEM, deviceSystem);
        PreferencesUtils.putString(context, getPersonalSpName(username), PC_DEVICE_ID, pcDeviceId);
    }

    public String getDeviceSystem(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getString(context, getPersonalSpName(username), INTENT_ONLINE_DEVICE_SYSTEM,"");
    }

    public String getPCDeviceId(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getString(context, getPersonalSpName(username), PC_DEVICE_ID,"");
    }

    public void setSettingDiscussionHelper(Context context, boolean switcher) {

        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), SETTING_DISCUSSION_HELPER, switcher);
    }


    /**
     * 是否需要群聊助手
     * */
    public boolean getSettingDiscussionHelper(Context context) {
        if(!AtworkConfig.DISSCUSSION_CONFIG.isNeedDiscussionHelper()) {
            return false;
        }

        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), SETTING_DISCUSSION_HELPER, true);
    }

    public String getDropboxRefreshId(Context context, String sourceId) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getString(context, getPersonalSpName(username), DROPBOX_REFRESH_ID + sourceId, "");
    }

    public void setDropboxRefreshId(Context context, String sourceId) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putString(context, getPersonalSpName(username), DROPBOX_REFRESH_ID + sourceId, sourceId);
    }



    /**
     * 是否需要强制跳转设置密码, 手势密码
     */
    public boolean needReset(Context context) {
        return ResetMode.NONE != getResetMode(context);
    }

    public void setResetMode(Context context, int resetMode) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putInt(context, getPersonalSpName(username), RESET_MODE, resetMode);
    }

    public int getResetMode(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getInt(context, getPersonalSpName(username), RESET_MODE, ResetMode.NONE);
    }


    public List<AckPostMessage> getAcksNeedCheck(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        String acksJson = PreferencesUtils.getString(context, getPersonalSpName(username), REMOVE_ACK_NEED_CHECK);
        List<AckPostMessage> ackPostMessageList = new Gson().fromJson(acksJson, new TypeToken<List<AckPostMessage>>() {
        }.getType());

        if (null == ackPostMessageList) {
            ackPostMessageList = new ArrayList<>();
        }
        return ackPostMessageList;
    }

    /**
     * @see #setAcksNeedCheck(Context, List)
     */
    public void setAckNeedCheck(Context context, AckPostMessage ackPostMessage) {
        List<AckPostMessage> ackPostMessageList = getAcksNeedCheck(context);
        ackPostMessageList.add(ackPostMessage);

        setAcksNeedCheck(context, ackPostMessageList);
    }


    /**
     * 保存需要检查的 remove ack
     */
    public void setAcksNeedCheck(Context context, List<AckPostMessage> ackPostMessageList) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        String acksJson = JsonUtil.toJsonList(ackPostMessageList);
        PreferencesUtils.putString(context, getPersonalSpName(username), REMOVE_ACK_NEED_CHECK, acksJson);
    }

    /**
     * 检查完成后,收到服务器发送回来的 write 回执,  删除对应的 ack
     */
    public void removeAcksNeedCheck(Context context, List<String> ackIdRemovedList) {
        List<AckPostMessage> ackPostMessageList = getAcksNeedCheck(context);

        List<AckPostMessage> ackRemovedList = new ArrayList<>();

        for (AckPostMessage ackPostMessage : ackPostMessageList) {
            if (ackIdRemovedList.contains(ackPostMessage.deliveryId)) {
                ackRemovedList.add(ackPostMessage);
            }
        }

        ackPostMessageList.removeAll(ackRemovedList);
        setAcksNeedCheck(context, ackPostMessageList);
    }


    /**
     * 是否提醒过设置生物认证
     * */
    public boolean isNoticedToSetBiometricAuthentication(Context context) {
        if(BaseApplicationLike.sIsDebug) {
            return false;
        }

        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), NOTICED_TO_SET_BIOMETRIC_AUTHENTICATION, false);
    }


    /**
     * 更新提醒过设置生物认证的状态
     * */
    public void setNoticedToSetBiometricAuthentication(Context context, boolean noticed) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), NOTICED_TO_SET_BIOMETRIC_AUTHENTICATION, noticed);
    }

    /**
     * 用户是否签署过协议
     */
    public boolean isLoginSignedAgreementConfirmed(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), LOGIN_SIGN_AGREEMENT, false);
    }

    /**
     * 更新用户签署协议状态
     */
    public void setLoginSignedAgreementConfirmed(Context context, boolean isSigned) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), LOGIN_SIGN_AGREEMENT, isSigned);
    }


    /**
     * 用户是否需要检查协议
     */
    public boolean needCheckSignedAgreement(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), NEED_CHECK_SIGN_AGREEMENT, false);
    }

    /**
     * 更新"用户是否需要检查协议"状态
     */
    public void setNeedCheckSignedAgreement(Context context, boolean yes) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), NEED_CHECK_SIGN_AGREEMENT, yes);
    }

    /**
     * 保密协议页面是否强制打开
     */
    public boolean isLoginSignedAgreementForced(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), AGREEMENT_INTERCEPT_FORCED, false);
    }


    /**
     * 设置保密协议页面强制打开
     */
    public void setLoginSignedAgreementForced(Context context, boolean forced) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), AGREEMENT_INTERCEPT_FORCED, forced);
    }

    /**
     * 是否做过必应消息同步处理
     */
    public boolean hasBingSync(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), BING_SYNC, false);
    }

    /**
     * 更新必应消息同步的状态
     */
    public void updateBingSyncFinish(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), BING_SYNC, true);
    }

    /**
     * 上次同步必应消息的时间
     */
    public long getLastBingSyncTime(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getLong(context, getPersonalSpName(username), LAST_BING_SYNC_TIME, -1);
    }

    /**
     * 更新同步必应消息的时间
     */
    public void updateLastBingSyncTime(Context context, long time) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putLong(context, getPersonalSpName(username), LAST_BING_SYNC_TIME, time);

    }

    public void setOrgOutFieldPunchRequestCode(Context context, String orgId) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        int requestCode = (int) (Math.random() * 90000 + 10000);
        PreferencesUtils.putInt(context, getPersonalSpName(username), orgId + ORG_OUT_FIELD_PUNCH_REQUEST_CODE + username, requestCode);
    }

    public int getOrgOutFieldPunchRequestCode(Context context, String orgId) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getInt(context, getPersonalSpName(username), orgId + ORG_OUT_FIELD_PUNCH_REQUEST_CODE + username, -1);
    }

    public void removeOrgOutFieldPunchRequestCode(Context context, String orgId) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.remove(context, getPersonalSpName(username), orgId + ORG_OUT_FIELD_PUNCH_REQUEST_CODE + username);
    }

    public void setOrgOutFieldPunchIntervalTime(Context context, String orgId, int interval) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putInt(context, getPersonalSpName(username), orgId + ORG_OUT_FIELD_PUNCH_INTERVAL_TIME + username, interval);
    }

    public int getOrgOutFieldPunchIntervalTime(Context context, String orgId) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getInt(context, getPersonalSpName(username), orgId + ORG_OUT_FIELD_PUNCH_INTERVAL_TIME + username, -1);
    }

    public void removeOrgOutFieldPunchIntervalTime(Context context, String orgId) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.remove(context, getPersonalSpName(username), orgId + ORG_OUT_FIELD_PUNCH_INTERVAL_TIME + username);
    }

    public int getMainFabSlideFingerShownCount(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getInt(context, username, MAIN_FAB_BOTTOM_SLIDE_NOTICE_FINGER_SHOWN_COUNT, 0);
    }

    public void putMainFabSlideFingerShownCount(Context context, int count) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putInt(context, username, MAIN_FAB_BOTTOM_SLIDE_NOTICE_FINGER_SHOWN_COUNT, count);
    }

    public int getMainContactViewModeSwitchFirstGuidePageShownCount(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getInt(context, username, MAIN_COTACT_VIEW_MODE_SWITCH_FIRST_GUIDE_PAGE_SHOWN_COUNT, 0);
    }

    public void putMainContactViewModeSwitchFirstGuidePageShownCount(Context context, int count) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putInt(context, username, MAIN_COTACT_VIEW_MODE_SWITCH_FIRST_GUIDE_PAGE_SHOWN_COUNT, count);
    }


    public int getWorkbenchFirstGuidePageShownCount(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getInt(context, username, WORKBENCH_FIRST_GUIDE_PAGE_SHOWN_COUNT, 0);
    }

    public void putWorkbenchFirstGuidePageShownCount(Context context, int count) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putInt(context, username, WORKBENCH_FIRST_GUIDE_PAGE_SHOWN_COUNT, count);
    }


    public int getAppTabFirstGuidePageShownCount(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getInt(context, username, APP_TAB_FIRST_GUIDE_PAGE_SHOWN_COUNT, 0);
    }

    public void putAppTabFirstGuidePageShownCount(Context context, int count) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putInt(context, username, APP_TAB_FIRST_GUIDE_PAGE_SHOWN_COUNT, count);
    }


    public int getMeTabFirstGuidePageShownCount(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getInt(context, username, ME_TAB_FIRST_GUIDE_PAGE_SHOWN_COUNT, 0);
    }

    public void putMeTabFirstGuidePageShownCount(Context context, int count) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putInt(context, username, ME_TAB_FIRST_GUIDE_PAGE_SHOWN_COUNT, count);
    }


    public void putDropboxForceAllRefresh(Context context, boolean allRefresh, String sourceId) {
        if (StringUtils.isEmpty(AtworkConfig.DROPBOX_CONFIG.getForceAllRefreshTag())) {
            return;
        }

        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        String dropboxForceAllRefreshTag = DROPBOX_FORCE_ALL_REFRESH + AtworkConfig.DROPBOX_CONFIG.getForceAllRefreshTag() + sourceId;
        PreferencesUtils.putBoolean(context, username, dropboxForceAllRefreshTag, allRefresh);
    }

    public boolean isDropboxForceAllRefresh(Context context, String sourceId) {
        if (StringUtils.isEmpty(AtworkConfig.DROPBOX_CONFIG.getForceAllRefreshTag())) {
            return false;
        }

        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        String dropboxForceAllRefreshTag = DROPBOX_FORCE_ALL_REFRESH + AtworkConfig.DROPBOX_CONFIG.getForceAllRefreshTag() + sourceId;
        return PreferencesUtils.getBoolean(context, username, dropboxForceAllRefreshTag, true);

    }


    public void updateWalletAccount(Context context, WalletAccount walletAccount) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        String walletStr = JsonUtil.toJson(walletAccount);
        PreferencesUtils.putString(context, username, WALLET_ACCOUNT, walletStr);
    }

    @Nullable
    public WalletAccount getWalletAccount(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        String walletStr = PreferencesUtils.getString(context, username, WALLET_ACCOUNT);
        if (!StringUtils.isEmpty(walletStr)) {
            return JsonUtil.fromJson(walletStr, WalletAccount.class);
        }

        return null;
    }


    public String getUrlHookingForFloat(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getString(context, getPersonalSpName(username), URL_HOOKING_FOR_FLOAT);
    }

    public void setUrlHookingForFloat(Context context, String url) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putString(context, getPersonalSpName(username), URL_HOOKING_FOR_FLOAT, url);
    }

    public void setDataAppTopBannerDisplayRecord(Context context, String advertisementId) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        Set<String> recordSet = getDataAppTopBannerDisplayRecord(context);
        recordSet.add(advertisementId);

        StringBuilder sb = new StringBuilder();
        for (String item : recordSet) {
            sb.append(item).append(",");
        }
        PreferencesUtils.putString(context, username, DATA_APP_TOP_BANNER_DISPLAY_RECORD, sb.toString());
    }

    public Set<String> getDataAppTopBannerDisplayRecord(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        String dataAppTopBannerDisplayRecordStr = PreferencesUtils.getString(context, username, DATA_APP_TOP_BANNER_DISPLAY_RECORD, StringUtils.EMPTY);
        String[] splitRecordStr = dataAppTopBannerDisplayRecordStr.split(",");
        return new HashSet<>(Arrays.asList(splitRecordStr));
    }

    public boolean containsAppTopBannerDisplayRecord(Context context, String advertisementId) {
        return getDataAppTopBannerDisplayRecord(context).contains(advertisementId);
    }

    public int getWebviewFloatActionSetting(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getInt(context, getPersonalSpName(username), WEBVIEW_FLOAT_ACTION_SETTING, -1);
    }

    public void setWebviewFloatActionSetting(Context context, int setting) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putInt(context, getPersonalSpName(username), WEBVIEW_FLOAT_ACTION_SETTING, setting);
    }



    public int getContactViewMode(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getInt(context, getPersonalSpName(username), CONTACT_VIEW_MODE, ContactViewMode.SIMPLE_VERSION);
    }

    public void setContactViewMode(Context context, int mode) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putInt(context, getPersonalSpName(username), CONTACT_VIEW_MODE, mode);
    }

    public boolean isChatFileInWhitelist(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), CHAT_FILE_IN_WHITELIST, false);
    }

    public void setChatFileInWhitelist(Context context, boolean isInWhitelist) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), CHAT_FILE_IN_WHITELIST, isInWhitelist);
    }

    public void addNewFriendsApplyUserId(Context context, String userId) {
        HashSet<String> newApplyFriendUserIds = getNewApplyFriendUserIds(context);
        newApplyFriendUserIds.add(userId);

        refreshNewFriendApplyIUserIds(context, newApplyFriendUserIds);

    }

    public void setStopPollingEmailAccount(Context context, String emailAccount, boolean stopPolling) {
        PreferencesUtils.putBoolean(context, getPersonalSpName(emailAccount), STOP_POLLING_EMAIL_ACCOUNT, stopPolling);
    }

    public boolean getStopPollingEmailAccount(Context context, String emailAccount) {
        return PreferencesUtils.getBoolean(context, getPersonalSpName(emailAccount), STOP_POLLING_EMAIL_ACCOUNT, false);
    }


    public void removeNewFriendApplyUserId(Context context, String userId) {
        HashSet<String> newApplyFriendUserIds = getNewApplyFriendUserIds(context);
        newApplyFriendUserIds.remove(userId);

        refreshNewFriendApplyIUserIds(context, newApplyFriendUserIds);
    }

    private void refreshNewFriendApplyIUserIds(Context context, HashSet<String> newApplyFriendUserIds) {
        StringBuilder sb = new StringBuilder();
        for(String applyUserId : newApplyFriendUserIds) {
            if(!StringUtils.isEmpty(applyUserId)) {
                sb.append(applyUserId).append(",");
            }
        }

        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putString(context, getPersonalSpName(username), NEW_APPLY_FRIEND_USER_IDS, sb.toString());
    }


    public HashSet<String> getNewApplyFriendUserIds(Context context) {
        String[] friendUserIdArray = getNewApplyFriendUserIdsStr(context).split(",");

        HashSet<String> userIdList = new HashSet<>(Arrays.asList(friendUserIdArray));
        HashSet<String> userIdListFilter = new HashSet<>();
        for(String userId : userIdList) {
            if(!StringUtils.isEmpty(userId)) {
                userIdListFilter.add(userId);
            }
        }
        return userIdListFilter;
    }

    public String getNewApplyFriendUserIdsStr(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getString(context, getPersonalSpName(username), NEW_APPLY_FRIEND_USER_IDS, StringUtils.EMPTY);

    }

    public boolean isHideDiscussionHelper(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), HIDE_DISCUSSION_HELPER, false);
    }

    public void setHideDiscussionHelper(Context context, boolean hide) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), HIDE_DISCUSSION_HELPER, hide);
    }

    public long getLastTimeEnterAnnounceApp(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getLong(context, getPersonalSpName(username), LAST_TIME_ENTER_ANNOUNCE_APP, -1);

    }

    public void setLastTimeEnterAnnounceApp(Context context, long timeEnter) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putLong(context, getPersonalSpName(username), LAST_TIME_ENTER_ANNOUNCE_APP, timeEnter);

    }

    public String getPersonalSpName(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);

        return getPersonalSpName(username);
    }

    /**
     * 为了防止onResume循环验证，一次验证不通过后回调onResume时不做验证，迭代下一次的onResume
     * @param context
     * @param stop
     */
    public void setStopVerify(Context context, boolean stop) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), KEY_STOP_VERIFY, stop);
    }

    public boolean getStopVerify(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), KEY_STOP_VERIFY, false);
    }

    /**
     * 智能机器人开关设置
     *
     * @param context
     * @param isOpen 是否为打开状态
     */
    public void setOpenServerRobot(Context context, boolean isOpen) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), SETTING_SERVER_ROBOT_SWITCH, isOpen);
    }

    public boolean getOpenServerRobot(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), SETTING_SERVER_ROBOT_SWITCH, AtworkConfig.DEFAULT_SERVER_ROBOT_SETTING);
    }
    public void setOpenRobot(Context context, boolean isOpen) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), SETTING_ROBOT_SWITCH, isOpen);
        if(isOpen){
            PersonalShareInfo.getInstance().setOpenRobotShark(context, true);
            PersonalShareInfo.getInstance().setOpenRobotClick(context, true);
        }
    }

    //摇一摇开启
    public void setOpenRobotShark(Context context, boolean isSharkOpen) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), SETTING_ROBOT_SHARKE_OPEN, isSharkOpen);
    }

    public boolean getOpenRobotShark(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), SETTING_ROBOT_SHARKE_OPEN, AtworkConfig.DEFAULT_ROBOT_SHARK_SETTING);
    }
    //点击开启
    public void setOpenRobotClick(Context context, boolean isClickOpen) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), SETTING_ROBOT_CLICK_OPEN, isClickOpen);
    }

    public boolean getOpenRobotClick(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), SETTING_ROBOT_CLICK_OPEN, AtworkConfig.DEFAULT_ROBOT_SETTING);
    }

    //智能机器人指令最近更新时间
    public void setRobotOrderUpdateTime(Context context, long updateTime) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        PreferencesUtils.putLong(context, getPersonalSpName(username), SETTING_ROBOT_ORDER_UPDATE_TIME, updateTime);
    }

    public long getRobotOrderUpdateTime(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserUserName(context);
        return PreferencesUtils.getLong(context, getPersonalSpName(username), SETTING_ROBOT_ORDER_UPDATE_TIME, AtworkConfig.DEFAULT_ROBOT_ORDER_UPDATE_TIME);
    }

    public void setDocTransferNotice(Context context, boolean notice) {
        String username = LoginUserInfo.getInstance().getLoginUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), DOC_TRANSFER_NOTICE, notice);
    }

    public boolean getDocTransferNotice(Context context) {
        String username = LoginUserInfo.getInstance().getLoginUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), DOC_TRANSFER_NOTICE, false);
    }

    public void setDocCustomerVolumeEnable(Context context, String orgId, boolean enable) {
        String username =  LoginUserInfo.getInstance().getLoginUserName(context);
        PreferencesUtils.putBoolean(context, getPersonalSpName(username), DOC_CUSTOMER_VOLUME_ENABLE + orgId, enable);
    }

    public boolean getDocCustomerVolumeEnable(Context context, String orgId) {
        String username =  LoginUserInfo.getInstance().getLoginUserName(context);
        return PreferencesUtils.getBoolean(context, getPersonalSpName(username), DOC_CUSTOMER_VOLUME_ENABLE + orgId, true);
    }


    @NonNull
    public String getPersonalSpName(String userId) {
        return userId + SP_IN_PRIVATE_USER;
    }

    public static final class ResetMode {

        public static final int NONE = -1;

        public static final int MODIFY_PWD = 0;

        public static final int INIT_GESTURE_LOCK = 1;

        public static final int INIT_BIOMETRIC_AUTHENTICATION = 2;
    }


}
