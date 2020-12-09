package com.foreveross.atwork.infrastructure.shared;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.FloatPos;
import com.foreveross.atwork.infrastructure.model.aliyun.AccessTokenInfo;
import com.foreveross.atwork.infrastructure.model.location.LocationDataInfo;
import com.foreveross.atwork.infrastructure.model.app.ServiceApp;
import com.foreveross.atwork.infrastructure.model.domain.MultiDomainsItem;
import com.foreveross.atwork.infrastructure.model.zoom.ZoomJoinMeetingHistoryData;
import com.foreveross.atwork.infrastructure.model.zoom.ZoomJoinMeetingHistoryDataItem;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.PreferencesUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.language.LanguageSetting;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Comparator;
import java.util.List;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;

/**
 * 该类用于管理非登录状态的 SharedPreferences
 * <p>
 * Created by dasunsy on 15/12/11.
 */
public class CommonShareInfo {
    public static final String SP_COMMON = "sp_common" + AtworkConfig.SP_SUFFIX_COMMON_AND_PERSONAL_FILE;

    /**
     * 存储手势密码的 home 键行为
     */
    private final static String KEY_HOME_BTN_STATUS_FOR_COMMON = "KEY_HOME_BTN_STATUS_FOR_COMMON";

    private final static String KEY_HOME_BTN_STATUS_FOR_GESTURE = "KEY_HOME_BTN_STATUS_FOR_GESTURE";

    private final static String KEY_HOME_BTN_STATUS_FOR_DOMAIN_SETTING = "KEY_HOME_BTN_STATUS_FOR_DOMAIN_SETTING";

    private static final String KEY_LATEST_APP_VERSION = "LatestAppVersion";

    private static final String KEY_LATEST_APP_VERSION_NAME = "LatestAppVersionName";

    private static final String KEY_LATEST_ALERT_APP_VERSION = "key_latest_alert_app_version";

    private static final String KEY_LATEST_ALERT_UPDATE_TIME = "key_latest_alert_update_time";

    private static final String KEY_FORCE_APP_UPDATE_STATE = "app_force_update_state";

    private static final String KEY_BOARD_INPUT_HEIGHT = "key_board_input_height";

    private static final String KEY_VPN_PERMISSION_HAS_SHOWN = "vpn_permission_has_shown";

    private static final String KEY_ENCRYPT_MODE = "KEY_ENCRYPT_MODE";

    private static final String KEY_LOGIN_USERNAME_SP_LIST = "KEY_LOGIN_USERNAME_SP_LIST";

    private static final String KEY_EMAIL_AUTH_ERROR_TIME = "KEY_EMAIL_AUTH_ERROR_TIME";

    private static final String SETTING_LANGUAGE = "SETTING_LANGUAGE";

    public static final String XIAOMI_PUSH_TOKEN = "XIAOMI_PUSH_TOKEN";

    public static final String HUAWEI_PUSH_TOKEN = "HUAWEI_PUSH_TOKEN";
    public static final String MEIZU_PUSH_TOKEN = "MEIZU_PUSH_TOKEN";
    public static final String VIVO_PUSH_TOKEN = "VIVO_PUSH_TOKEN";
    public static final String OPPO_PUSH_TOKEN = "OPPO_PUSH_TOKEN";

    public static final String KEY_SELECT_FETCH_MESSAGES_IN_DAYS = "KEY_SELECT_FETCH_MESSAGES_IN_DAYS";

    public static final String KEY_SELECT_FETCH_MESSAGE_MODE = "KEY_SELECT_FETCH_MESSAGE_MODE";

    /**
     * 全时云视频指引是否显示过
     * */
    public static final String KEY_QSY_VIDEO_IS_GUIDE_SHOWED = "key_qsy_video_is_guide_showed";

    /**
     * 在主界面弹出过悬浮窗权限请求框次数
     * */
    public static final String KEY_FLOAT_PERMISSION_ASKED_ALERT_IN_MAIN_COUNT = "KEY_FLOAT_PERMISSION_ASKED_ALERT_IN_MAIN_COUNT";

    /**
     * 默认需要的在主界面弹出过悬浮窗权限请求框次数, 暂时只需要1次
     * */
    public static final int DEFAULT_NEED_FLOAT_PERMISSION_ASKED_ALERT_IN_MAIN_COUNT = 1;


    /**
     * 是否为新创建的 notification channel
     * suffix with NotificationConfig#keyNewDefaultNotificationChannelSuffix
     * */
    public static String KEY_NEW_DEFAULT_NOTIFICATION_CHANNEL = "KEY_NEW_DEFAULT_NOTIFICATION_CHANNEL" + AtworkConfig.NOTIFICATION_CONFIG.getKeyNewDefaultNotificationChannelSuffix();



    /**
     * 是否请求成功过安装量日志
     * */
    public static String KEY_REQUEST_SUCCESSFULLY_INSTALL_LOG = "KEY_REQUEST_INSTALL_LOG";

    public static String KEY_REQUEST_LOCATION_DATA = "KEY_REQUEST_LOCATION_DATA";

    public static String KEY_FLOATING_X_POS = "KEY_FLOATING_WIDTH_POS";

    public static String KEY_FLOATING_Y_POS = "KEY_FLOATING_HEIGHT_POS";


    public static String KEY_ALIYUN_AT = "KEY_ALIYUN_AT";

    public static String KEY_INTRODUCE_VERSION = "KEY_PRODUCE_VERSION_";

    public static String KEY_DEVICE_ID = "KEY_DEVICE_ID";

    public static String KEY_DOMAIN_ID = "KEY_DOMAIN_ID";

    public static String KEY_DOMAINS_DATA_REMOTE = "KEY_DOMAINS_DATA_REMOTE";

    public static String KEY_RESET_APP_DATA = "KEY_RESET_APP_DATA";


    //是否需要弹出隐私协议的dialog
    private static final String SETTING_PROTOCOL= "SETTING_PROTOCOL";

    /**
     * zoom 会议加入会议历史记录
     * */
    public static final String KEY_DATA_ZOOM_JOIN_MEETING_HISTORY = "KEY_DATA_ZOOM_JOIN_MEETING_HISTORY";


    public static boolean isAppDataReset(Context context) {
        return PreferencesUtils.getBoolean(context, SP_COMMON, KEY_RESET_APP_DATA,false);
    }

    public static void appDataReset(Context context) {
        PreferencesUtils.putBoolean(context, SP_COMMON, KEY_RESET_APP_DATA, true);
    }

    /**
     * 是否需要弹出隐私协议
     */
    public static boolean getProtocol(Context context) {
        return PreferencesUtils.getBoolean(context, SP_COMMON, SETTING_PROTOCOL,true);
    }

    public static void setProtocol(Context context,Boolean isOpen) {
        PreferencesUtils.putBoolean(context, SP_COMMON, SETTING_PROTOCOL, isOpen);
    }


    public static void setDeviceId(Context context, String deviceId) {
        PreferencesUtils.putString(context, SP_COMMON, KEY_DEVICE_ID, deviceId);
    }


    public static String getDeviceId(Context context) {
        return PreferencesUtils.getString(context, SP_COMMON, KEY_DEVICE_ID, StringUtils.EMPTY);
    }


    public static void setDomainId(Context context, String domainId) {
        PreferencesUtils.putString(context, SP_COMMON, KEY_DOMAIN_ID, domainId);
    }


    public static String getDomainId(Context context) {
        return PreferencesUtils.getString(context, SP_COMMON, KEY_DOMAIN_ID, StringUtils.EMPTY);
    }


    public static void setDomainsDataRemote(Context context, @Nullable List<MultiDomainsItem> multiDomainsItems) {
        if (null != multiDomainsItems) {
            PreferencesUtils.putString(context, SP_COMMON, KEY_DOMAINS_DATA_REMOTE, JsonUtil.toJsonList(multiDomainsItems));
        }
    }

    @Nullable
    public static List<MultiDomainsItem> getDomainsDataRemote(Context context) {
        String domainsDataRemoteStr = PreferencesUtils.getString(context, SP_COMMON, KEY_DOMAINS_DATA_REMOTE, StringUtils.EMPTY);
        List<MultiDomainsItem> multiDomainsItems = new Gson().fromJson(domainsDataRemoteStr, new TypeToken<List<MultiDomainsItem>>() {}.getType());
        return multiDomainsItems;
    }

    public static void catchKeyHomeBtnAndScreenLock(Context context) {

        setHomeKeyStatusForDomainSetting(context, true);

        setKeyHomeBtnStatusForGesture(context, true);
    }

    public static void setHomeKeyStatusForCommon(Context context, boolean hadClickHomeKey) {
        BaseApplicationLike.sIsHomeStatus = hadClickHomeKey;
        PreferencesUtils.putBoolean(context, SP_COMMON, KEY_HOME_BTN_STATUS_FOR_COMMON, hadClickHomeKey);
    }

    public static boolean getHomeKeyStatusForCommon(Context context) {
        return PreferencesUtils.getBoolean(context, SP_COMMON, KEY_HOME_BTN_STATUS_FOR_COMMON, false);
    }

    public static void setIntroduceVersionShowed(Context context) {
        PreferencesUtils.putBoolean(context, SP_COMMON, KEY_INTRODUCE_VERSION + AtworkConfig.INTRODUCE_VERSION, true);
    }

    public static boolean isIntroduceShowedByVersion(Context context) {
        return PreferencesUtils.getBoolean(context, SP_COMMON, KEY_INTRODUCE_VERSION + AtworkConfig.INTRODUCE_VERSION, false);
    }

    public static void setHomeKeyStatusForDomainSetting(Context context, boolean hadClickHomeKey) {
        PreferencesUtils.putBoolean(context, SP_COMMON, KEY_HOME_BTN_STATUS_FOR_DOMAIN_SETTING, hadClickHomeKey);
    }

    public static boolean getHomeKeyStatusForDomainSetting(Context context) {
        return PreferencesUtils.getBoolean(context, SP_COMMON, KEY_HOME_BTN_STATUS_FOR_DOMAIN_SETTING, false);
    }

    public static void setKeyHomeBtnStatusForGesture(Context context, boolean hadClickHome) {
        boolean lastHomeStatus = getKeyHomeBtnStatusForGesture(context);

        PreferencesUtils.putBoolean(context, SP_COMMON, KEY_HOME_BTN_STATUS_FOR_GESTURE, hadClickHome);

        //从 false -> true 这样的状态转变时才记录下时间值
        if (!lastHomeStatus && hadClickHome) {

            //设置锁屏后马上需要促发锁屏时间
            if (!LoginUserInfo.getInstance().mIsInitOpenCodeLock) {
                LoginUserInfo.getInstance().mLastCodeLockTime = System.currentTimeMillis();

            } else {

                LoginUserInfo.getInstance().mIsInitOpenCodeLock = false;
            }

        }
    }

    public static boolean getKeyHomeBtnStatusForGesture(Context context) {
        return PreferencesUtils.getBoolean(context, SP_COMMON, KEY_HOME_BTN_STATUS_FOR_GESTURE, false);
    }

    public static boolean isForcedUpdatedState(Context context) {
        return PreferencesUtils.getBoolean(context, SP_COMMON, KEY_FORCE_APP_UPDATE_STATE, false);

    }

    public static void setForcedUpdatedState(Context context, boolean isForce) {
        PreferencesUtils.putBoolean(context, SP_COMMON, KEY_FORCE_APP_UPDATE_STATE, isForce);
    }

    public static void setLatestVersionCodeAlerted(Context context, int versionCodeAlerted) {
        PreferencesUtils.putInt(context, SP_COMMON, KEY_LATEST_ALERT_APP_VERSION, versionCodeAlerted);
    }



    public static boolean isVersionCodeNotAlerted(Context context, int versionCodeRemote) {
        return versionCodeRemote > PreferencesUtils.getInt(context, SP_COMMON, KEY_LATEST_ALERT_APP_VERSION, -1);
    }

    public static void setLatestAlertUpdateTime(Context context, long alertTime) {
        PreferencesUtils.putLong(context, SP_COMMON, KEY_LATEST_ALERT_UPDATE_TIME, alertTime);
    }

    public static boolean shouldAlertUpdate(Context context, long interval) {
        long lastAlertTime = PreferencesUtils.getLong(context, SP_COMMON, KEY_LATEST_ALERT_UPDATE_TIME, -1);

        if(interval < System.currentTimeMillis() - lastAlertTime) {
            return true;
        }

        return false;
    }


    public static void setNewVersionCode(Context context, int newVersionCode) {
        PreferencesUtils.putInt(context, SP_COMMON, KEY_LATEST_APP_VERSION, newVersionCode);
    }

    public static int getNewVersionCode(Context context) {
        return PreferencesUtils.getInt(context, SP_COMMON, KEY_LATEST_APP_VERSION, -1);
    }

    //发现更新的版本
    public static boolean isFoundNewVersionCode(Context context, int currentVersion) {
        int newVersion = getNewVersionCode(context);
        return newVersion > currentVersion;
    }



    public static void clearNewVersionCode(Context context) {
        PreferencesUtils.remove(context, SP_COMMON, KEY_LATEST_APP_VERSION);
    }

    public static void setNewVersionName(Context context, String newVersionName) {
        PreferencesUtils.putString(context, SP_COMMON, KEY_LATEST_APP_VERSION_NAME, newVersionName);
    }

    public static void setVpnPermissionHasShown(Context context, boolean hasPermitted) {
        PreferencesUtils.putBoolean(context, SP_COMMON, KEY_VPN_PERMISSION_HAS_SHOWN, hasPermitted);
    }


    public static boolean getVpnPermissionHasShown(Context context) {
        return PreferencesUtils.getBoolean(context, SP_COMMON, KEY_VPN_PERMISSION_HAS_SHOWN, false);
    }

    //获取更新的版本名称
    public static String getNewVersionName(Context context, String currentVersionName) {
        return PreferencesUtils.getString(context, SP_COMMON, KEY_LATEST_APP_VERSION_NAME, currentVersionName);
    }

    public static void setKeyBoardHeight(Context context, int height) {
        PreferencesUtils.putInt(context, SP_COMMON, KEY_BOARD_INPUT_HEIGHT, height);

    }

    public static int getKeyBoardHeight(Context context) {
        return PreferencesUtils.getInt(context, SP_COMMON, KEY_BOARD_INPUT_HEIGHT, -1);
    }

    public static void setQsyVideoGuideShowed(Context context, boolean isShowed) {
        PreferencesUtils.putBoolean(context, SP_COMMON, KEY_QSY_VIDEO_IS_GUIDE_SHOWED, isShowed);
    }

    public static boolean isQsyVideoGuideShowed(Context context) {
        return PreferencesUtils.getBoolean(context, SP_COMMON, KEY_QSY_VIDEO_IS_GUIDE_SHOWED, false);
    }

    public static void resetEncryptMode(Context context) {
        setEncryptMode(context, AtworkConfig.getEncryptModeConfig());
    }

    public static void setEncryptMode(Context context, int encryptMode) {
        PreferencesUtils.putInt(context, SP_COMMON, KEY_ENCRYPT_MODE, encryptMode);
    }

    public static int getEncryptMode(Context context) {
        int defaultEncryptMode;
        if(LoginUserInfo.getInstance().hasLoginBefore(context)) {
            defaultEncryptMode = AtworkConfig.FLAG_NO_ENCRYPTION;

        } else {
            defaultEncryptMode = AtworkConfig.getEncryptModeConfig();
        }
        return PreferencesUtils.getInt(context, SP_COMMON, KEY_ENCRYPT_MODE, defaultEncryptMode);
    }

    /**
     * 维护保存用户登录新建的{@link PersonalShareInfo}名字
     * */
    public static void saveLoginUserNameRecord(Context context, String loginUserName) {
        String usernamesSps = getLoginUsernameRecordStr(context);
        if(!usernamesSps.contains(loginUserName)) {
            PreferencesUtils.putString(context, SP_COMMON, KEY_LOGIN_USERNAME_SP_LIST, usernamesSps + loginUserName + ",");
        }
    }

    @NonNull
    public static String getLoginUsernameRecordStr(Context context) {
        return PreferencesUtils.getString(context, SP_COMMON, KEY_LOGIN_USERNAME_SP_LIST, StringUtils.EMPTY);
    }

    public static String[] getLoginUsernameRecordList(Context context) {
        String usernamesSps = getLoginUsernameRecordStr(context);
        return usernamesSps.split(",");
    }

    /**
     * 用户是否之前登录过
     * @param context
     * @param loginUserName 登录用户名
     * */
    public static boolean isUserLoginedBefore(Context context, String loginUserName) {
        return getLoginUsernameRecordStr(context).contains(loginUserName);
    }

    public static void setLanguageSetting(Context context, int setting) {
        PreferencesUtils.putInt(context, SP_COMMON, SETTING_LANGUAGE, setting);
    }

    public static int getLanguageSetting(Context context) {
        return PreferencesUtils.getInt(context, SP_COMMON, SETTING_LANGUAGE, LanguageSetting.AUTO);
    }

    public static void setKeyEmailAuthErrorTime(Context context, String userId , String email) {
        PreferencesUtils.putLong(context, SP_COMMON, KEY_EMAIL_AUTH_ERROR_TIME + userId + "_" + email, System.currentTimeMillis());
    }

    public static long getKeyEmailAuthErrorTime(Context context, String userId , String email) {
        return PreferencesUtils.getLong(context, SP_COMMON, KEY_EMAIL_AUTH_ERROR_TIME + userId + "_" + email, -1);
    }

    public static void removeKeyEmailAuthErrorTime(Context context, String userId , String email) {
        PreferencesUtils.remove(context, SP_COMMON, KEY_EMAIL_AUTH_ERROR_TIME + userId + "_" + email);
    }

    public static void setXiaomiPushToken(Context context, String pushToken) {
        PreferencesUtils.putString(context, SP_COMMON, XIAOMI_PUSH_TOKEN, pushToken);
    }

    public static String getXiaomiPushToken(Context context) {
        return PreferencesUtils.getString(context, SP_COMMON, XIAOMI_PUSH_TOKEN, "");
    }

    public static void setHuaweiPushToken(Context context, String pushToken) {
        PreferencesUtils.putString(context, SP_COMMON, HUAWEI_PUSH_TOKEN, pushToken);
    }

    public static String getHuaweiPushToken(Context context) {
        return PreferencesUtils.getString(context, SP_COMMON, HUAWEI_PUSH_TOKEN, "");
    }

    public static void setMeiZuPushToken(Context context, String pushToken) {
        PreferencesUtils.putString(context, SP_COMMON, MEIZU_PUSH_TOKEN, pushToken);
    }

    public static String getMeiZuPushToken(Context context) {
        return PreferencesUtils.getString(context, SP_COMMON, MEIZU_PUSH_TOKEN, "");
    }

    public static void setOPPOPushToken(Context context, String pushToken) {
        PreferencesUtils.putString(context, SP_COMMON, OPPO_PUSH_TOKEN, pushToken);
    }

    public static String getOPPOPushToken(Context context) {
        return PreferencesUtils.getString(context, SP_COMMON, OPPO_PUSH_TOKEN, "");
    }

    public static void setVIVOPushToken(Context context, String pushToken) {
        PreferencesUtils.putString(context, SP_COMMON, OPPO_PUSH_TOKEN, pushToken);
    }

    public static String getVIVOPushToken(Context context) {
        return PreferencesUtils.getString(context, SP_COMMON, OPPO_PUSH_TOKEN, "");
    }



    public static void setSelectFetchMessagesDays(Context context, int day) {
        PreferencesUtils.putInt(context, SP_COMMON, KEY_SELECT_FETCH_MESSAGES_IN_DAYS, day);
    }

    public static int getSelectFetchMessagesDays(Context context) {
        return PreferencesUtils.getInt(context, SP_COMMON, KEY_SELECT_FETCH_MESSAGES_IN_DAYS, AtworkConfig.CHAT_CONFIG.getDefaultOnlyFetchRecentMessagesDays());
    }


    public static void setSelectFetchMessageMode(Context context, int mode) {
        PreferencesUtils.putInt(context, SP_COMMON, KEY_SELECT_FETCH_MESSAGE_MODE, mode);
    }

    public static int getSelectFetchMessageMode(Context context) {
        return PreferencesUtils.getInt(context, SP_COMMON, KEY_SELECT_FETCH_MESSAGE_MODE, AtworkConfig.CHAT_CONFIG.getDefaultFetchMessagesMode());
    }


    /**
     * 设置在主界面弹出过悬浮窗权限的次数
     * */
    public static void setFloatPermissionAskedAlertInMainCount(Context context, int count) {
        PreferencesUtils.putInt(context, SP_COMMON, KEY_FLOAT_PERMISSION_ASKED_ALERT_IN_MAIN_COUNT, count);
    }


    /**
     * 返回主界面弹出过悬浮窗权限请求框的次数
     * */
    public static int getFloatPermissionAskedAlertInMainCount(Context context) {
        return PreferencesUtils.getInt(context, SP_COMMON, KEY_FLOAT_PERMISSION_ASKED_ALERT_IN_MAIN_COUNT, 0);
    }


    /**
     * 获取 notification channel的新建状态
     *
     * -1为默认值, 表示尚未设置过任何值, 处于未知状态, 0表示不是新创建的 1表示新创建的
     * 不是新创建的 channel, 声音、震动、呼吸灯等修改会无效
     *
     * */
    public static int getNewDefaultNotificationChannelStatus(Context context) {
        return PreferencesUtils.getInt(context, SP_COMMON, KEY_NEW_DEFAULT_NOTIFICATION_CHANNEL, -1);

    }

    /**
     * 设置"notification channel的新建状态"
     *
     * -1为默认值, 表示尚未设置过任何值, 处于未知状态, 0表示不是新创建的 1表示新创建的
     * */
    public static void setNewNotificationChannelStatus(Context context, int value) {
        PreferencesUtils.putInt(context, SP_COMMON, KEY_NEW_DEFAULT_NOTIFICATION_CHANNEL, value);
    }


    public static boolean isRequestSuccessfullyInstallLog(Context context) {
        return PreferencesUtils.getBoolean(context, SP_COMMON, KEY_REQUEST_SUCCESSFULLY_INSTALL_LOG, false);
    }


    public static void setRequestSuccessfullyInstallLog(Context context) {
        PreferencesUtils.putBoolean(context, SP_COMMON, KEY_REQUEST_SUCCESSFULLY_INSTALL_LOG, true);
    }

    public static void setFloatingPos(Context context, FloatPos pos) {
        PreferencesUtils.putInt(context, SP_COMMON, KEY_FLOATING_X_POS, pos.getPosX());
        PreferencesUtils.putInt(context, SP_COMMON, KEY_FLOATING_Y_POS, pos.getPosY());
    }

    public static FloatPos getFloatingPos(Context context) {
        FloatPos pos = new FloatPos();
        pos.setPosX(PreferencesUtils.getInt(context, SP_COMMON, KEY_FLOATING_X_POS, 0));
        pos.setPosY(PreferencesUtils.getInt(context, SP_COMMON, KEY_FLOATING_Y_POS, 0));
        return pos;
    }


    @Nullable
    public static AccessTokenInfo getAliyunAccessToken(Context context) {
        String accessTokenStr = PreferencesUtils.getString(context, SP_COMMON, KEY_ALIYUN_AT, StringUtils.EMPTY);
        return JsonUtil.fromJson(accessTokenStr, AccessTokenInfo.class);
    }

    public static void setAliyunAccessToken(Context context, AccessTokenInfo info) {
        PreferencesUtils.putString(context, SP_COMMON, KEY_ALIYUN_AT, JsonUtil.toJson(info));
    }

    @Nullable
    public static ZoomJoinMeetingHistoryData getZoomJoinMeetingHistoryData(Context context) {
        return PreferencesUtils.getObject(context, SP_COMMON, KEY_DATA_ZOOM_JOIN_MEETING_HISTORY,  ZoomJoinMeetingHistoryData.class);
    }

    public static void updateZoomJoinMeetingHistoryData(Context context, ZoomJoinMeetingHistoryDataItem dataItem) {
        ZoomJoinMeetingHistoryData zoomJoinMeetingHistoryData = getZoomJoinMeetingHistoryData(context);
        if(null == zoomJoinMeetingHistoryData) {
            zoomJoinMeetingHistoryData = new ZoomJoinMeetingHistoryData();
        }

        zoomJoinMeetingHistoryData.getDataList().add(dataItem);

        CollectionsKt.sortWith(zoomJoinMeetingHistoryData.getDataList(), new Comparator<ZoomJoinMeetingHistoryDataItem>() {
            @Override
            public int compare(ZoomJoinMeetingHistoryDataItem o1, ZoomJoinMeetingHistoryDataItem o2) {
                return Long.compare(o2.getJoinTime(), o1.getJoinTime());

            }
        });

        try {
            zoomJoinMeetingHistoryData.getDataList().subList(0, 5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PreferencesUtils.updateObject(context, SP_COMMON, KEY_DATA_ZOOM_JOIN_MEETING_HISTORY, zoomJoinMeetingHistoryData);
    }

    @Nullable
    public static LocationDataInfo getLocationDataInfo(Context context) {
        String locationDataInfoStr = PreferencesUtils.getString(context, SP_COMMON, KEY_REQUEST_LOCATION_DATA, StringUtils.EMPTY);
        return JsonUtil.fromJson(locationDataInfoStr, LocationDataInfo.class);
    }

    public static void updateLocationDataInfo(Context context, LocationDataInfo locationDataInfo) {
        PreferencesUtils.putString(context, SP_COMMON, KEY_REQUEST_LOCATION_DATA, JsonUtil.toJson(locationDataInfo));
    }


    static class InputInfo {
        public InputInfo(String name, int height) {
            this.name = name;
            this.height = height;
        }

        public String name;
        public int height;
    }
}
