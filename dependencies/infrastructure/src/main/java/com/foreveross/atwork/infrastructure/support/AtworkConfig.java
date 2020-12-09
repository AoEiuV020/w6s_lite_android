package com.foreveross.atwork.infrastructure.support;

import android.Manifest;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksEncryption;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksTextTranslate;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksXunfei;
import com.foreveross.atwork.infrastructure.beeworks.BeeworksServiceAppHistoricalMessage;
import com.foreveross.atwork.infrastructure.beeworks.voice.BeeworksVoice;
import com.foreveross.atwork.infrastructure.model.translate.TextTranslateSdk;
import com.foreveross.atwork.infrastructure.model.translate.TextTranslateSdkType;
import com.foreveross.atwork.infrastructure.model.translate.VoiceTranslateSdk;
import com.foreveross.atwork.infrastructure.model.translate.VoiceTranslateSdkType;
import com.foreveross.atwork.infrastructure.model.umeeting.UmeetingConfig;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.support.cdn.CdnConfig;
import com.foreveross.atwork.infrastructure.support.setting.SettingPageConfig;
import com.foreveross.atwork.infrastructure.theme.SystemThemeType;
import com.foreveross.atwork.infrastructure.utils.DeviceUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.w6s.config.OctConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by lingen on 15/3/19.
 * Description:
 */
public class AtworkConfig {
    private static final String TAG = AtworkConfig.class.getSimpleName();

    /**
     * SOCKET请求超时时间
     */
    public static final int SOCKET_TIME_OUT = 10 * 1000;
    /**
     * HTTP连接超时时间
     */
    public static final int READ_TIME_OUT = 30 * 1000;

    public static final int CONNECT_TIME_OUT = 10 * 1000;

    public static final String ANDROID_PLATFORM = "Android";

    public static final int INTERVAL_SERVICE_GUARD = 5 * 60 * 1000;

    /**
     * 应后台要求，心跳改成90秒一次，因为现在部分机器支持推送，心跳过长会导致无法判定设备离线，期间就无法收到离线推送
     */
    public static int INTERVAL_HEART_BEAT = 1000 * 70;

    /**
     * 数据库版本
     */
    public static int DB_VERSION = 451;

    /**
     *  引导页版本
     */
    public static int INTRODUCE_VERSION = 5;
    public static boolean SHOW_INTRODUCE = false;

    /**
     * 行为日志数据库版本
     * */
    public static int DB_VERSION_BEHAVIOR_LOG = 1;

    /**
     * 企业通讯录数据库版本
     */
    public static int DB_VERSION_EMP_INCOMING_CALL = 1;

    /**
     * API请求地址
     */
    public static String API_URL;

    /**
     * 媒体请求的 API 请求地址, 部分客户需要配置单独的 cdn 地址(如 kwg)
     * */
    public static String API_MEDIA_URL;

    /**
     * 管理后台请求地址
     * */
    public static String ADMIN_URL;

    /**
     * 是否将组织架构的冗余的节点折叠
     * */
    public static boolean  DUPLICATE_REMOVAL = false;

    /**
     * 是否需要展示隐私协议
     * */
    public static boolean  PROTOCOL = false;

    /**
     * 媒体请求的 管理后台请求地址, 部分客户需要配置单独的 cdn 地址(如 kwg)
     * */
    public static String ADMIN_MEDIA_URL;


    public static String LIGHT_APP_ADMIN_URL;

    public static String ARTICLE_URL;

    public static String COLLEAGUE_URL;

    public static String CORDOVA_SCHEDULE_URL;

//    public static String MAIN_ACTIVITY_SHAKE_URL;

    //-------------------------- IP 配置块 ----------------------------

    public static boolean IP_DOMAIN_SWITCHER = false;

    public static String APP_URL_IP;

    /**
     * 宇视科技：二维码扫描阿里云接口appcode
     * */
    public static String YSKJ_APP_CODE_URL;

    //-------------------------------------------------------

    /**
     * 连接SOCKET服务器的授权ID
     */
    public static String DOMAIN_ID;


    /**
     * 客户端标记
     */
    public static String PROFILE = StringUtils.EMPTY;

    /**
     * 当前手机设备ID
     */
    public static String DEVICE_ID = "";



    /**
     * 友盟appkey
     */
    public static String UMENG_APPKEY;

    public static boolean UMENG_ANALITICS;

    public static String CHANNEL_ID = StringUtils.EMPTY;

    public static String APP_FOLDER;

    public static int ROMAING_DAYS = 7;

    /**
     * 上次更新域设值的时间
     * */
    public static long sLastUpdateDomainSettingTime = 0;

    public static long sLastUpdateEmpIncomingCallTime = 0;

    public static long lastBackTime = 0;

    /**
     * 促发锁屏的间隔时间段
     */
    public static long LOCK_DURATION = 120000;


    public static String APP_ID;

    public static boolean H3C_CONFIG;

    public static boolean HIDE_TEL;

    public static boolean VPN_CHANNEL;

    public static int CHAT_THUMB_SIZE = 15 << 10; //15kb
    public static int CHAT_ORIGINAL_COMPRESS_SIZE = 300 << 10; //300kb
    public static int CHAT_IMG_SHOW_LIMIT = 2 << 20; //2mb
    public static int CHAT_MICRO_VIDEO_PLAY_NOT_CHECK_THRESHOLD_SIZE = 5 << 20; //5mb
//    public static int CHAT_MICRO_VIDEO_PLAY_NOT_CHECK_THRESHOLD_SIZE = 15 << 10; //15kb (test)
    public static int GIF_AUTO_DOWNLOAD_THRESHOLD_SIZE = 300 << 10; //300kb
    public static int COLLEAGUE_CIRCLE_COMPRESS_SIZE = 300 << 10; //300kb
    public static int CHAT_FULL_IMG_SELECT_LIMIT_SIZE = 20 << 20; //20mb

    /**
     * 小视频相关
     */
    public static int MICRO_MAX_TIME = 10000;
    public static int MICRO_TARGET_WIDTH = 480;
    public static int MICRO_TARGET_HEIGHT = 360;

    /**
     * 消息同步相关
     * */
    public static int COUNT_SYNC_MESSAGE_BATCH = 100; //漫游跟离线每一批数据的数量

    /**
     * 漫游和下拉所匹配的消息类型
     * */
    public static String ROAMING_AND_PULL_SYNC_INCLUDE_TYPE = "IMAGE,TEXT,VOICE,BURN_IMAGE,BURN_TEXT,BURN_VOICE,VIDEO,FILE,SHARE,ARTICLE,MULTIPART,RED_ENVELOP,STICKER,FACE";


    /**
     * 域设置更新接口更新频率
     * */
    public static final int INTERVAL_REFRESH_DOMAIN_SETTINGS = 1000 * 60 * 5;

    /**
     * 组织设置更新接口更新频率
     * */
    public static final int INTERVAL_REFRESH_ORG_SETTINGS = 1000 * 60 * 2;


    public static String WX_APP_ID;

    //workplus测试appId:   1105511346
    //workplus的appId:     1105486764
    public static String QQ_APP_ID;


    /**
     * voip member 判断存活时间阈值(秒)
     * */
    public static int VOIP_KEEP_ALIVE_THRESHOLD = 30;

    /**
     * 语音视频聊天最大人数, 默认最大9人
     * */
    public static int VOIP_MEMBER_COUNT_MAX = 9;

    /**
     * 群聊最大人数
     * */
    public static final int DISCUSSION_MEMBER_COUNT_MAX = 500;

    /**
     * 消息转发最大数量
     * */
    public static final int TRANSFER_MESSAGE_COUNT_MAX = 100;

    /**
     * 同步号码最大数量
     * */
    public static final int SYNC_NUMS_COUNT_MAX = 500;

    public static final long VIDEO_IN_CHAT_MAX_SELECT = 5 * 60 * 1000;

    /**
     * 全时云 id
     * */
    public static final String VOIP_QSY_APP_APP_ID = "zhonghaiji";

    /**
     * 全时云 password
     * */
    public static final String VOIP_QSY_APP_APP_PASSWORD = "ADU1FVVN";

    /**
     * 语音通话最长等待时间
     * */
    public static final int VOIP_MAX_WAIT_ANSWER_DURATION = 60;

    /**
     * voip 悬浮窗是否在桌面显示(//todo  true 的场景暂未满足)
     * */
    public static final boolean VOIP_NEED_FLOATVIEW_DESKTOP_SHOW = false;


    /**
     * 语音会议的种类(全时云, 声网等), null 则关闭语音会议
     * */
    public static VoipSdkType VOIP_SDK_TYPE = VoipSdkType.AGORA;

    /**
     * app本地存储 是否开启加密
     * */
    public static boolean OPEN_DISK_ENCRYPTION = false;

    /**
     * app数据库 是否开启加密
     * */
    public static boolean OPEN_DB_ENCRYPTION = false;

    /**
     * 语音聊天(声网) 是否开启加密
     * */
    public static boolean OPEN_VOIP_ENCRYPTION = false;


    public static int TK_TYPE = BeeWorksEncryption.TK_TYPE_DEFAULT;

    /**
     * IM 聊天内容是否加密
     * */
    public static boolean OPEN_IM_CONTENT_ENCRYPTION = false;

    public static final int FLAG_NO_ENCRYPTION = 0x00000000;

    public static final int FLAG_OPEN_DB_ENCRYPTION = 0x00000001;

    public static final int FLAG_OPEN_DISK_ENCRYPTION = 0x00000002;


    /**
     * 是否打开voip功能
     * */
    public static boolean OPEN_VOIP = false;

    /**
     * 是否打开网盘功能
     */
    public  static boolean OPEN_DROPBOX = false;

    /**
     * OnlyOffice
     */
    public static boolean ONLY_OFFICE_ENABLE = true;

    /**
     * 关闭网盘的时候需要特殊显示群文件
     * */
    public static boolean SPECIAL_ENABLE_DISCUSSION_FILE_WHEN_CLOSE_DROPBOX = false;

    /**
     * 服务号特殊入口
     * */
    public static boolean SERVICE_APP_LIST_ENTRY = false;


    /**
     * "关于 xxx"的只显示"关于", 不需要 xxx
     * */
    public static boolean ABOUT_APP_LABEL_PURE = false;

    /**
     * 是否使用2.0同事圈, 2.0同事圈在分享 url 上需要特殊处理
     * */
    public static boolean USE_V2_COLLEAGUE_CIRCLE = false;

    /**
     * 默认皮肤主题
     * */
    public static SystemThemeType DEFAULT_THEME = SystemThemeType.SKY_BLUE;

    public static String DB_SUFFIX = StringUtils.EMPTY;

    public static String SP_SUFFIX_COMMON_AND_PERSONAL_FILE = StringUtils.EMPTY;

    public static String SP_SUFFIX_USER_LOGIN_FILE = StringUtils.EMPTY;

    public static TextTranslateSdk TEXT_TRANSLATE_SDK;

    public static VoiceTranslateSdk VOICE_TRANSLATE_SDK;

    public static String ALI_APP_ID = "xxx";

    public static boolean SCREEN_ORIENTATION_USER_SENSOR = true;

    public static boolean LIGHT_APP_INIT_HIDE_MORE_BTN = false;

    /**
     * 首页的 viewpager 是否能够滑动
     * */
    public static boolean MAIN_VIEW_PAGER_CAN_SCROLL = false;

    /**
     * 用户头像能否点击修改
     * */
    public static boolean LOGIN_AVATAR_CAN_MODIFY = true;

    /**
     * 自定义修改密码的 api url
     * */
    public static String CUSTOM_MODIFY_API_URL;


    /**
     * 必应消息是否在聊天界面显示(无法在聊天界面漫游回来)
     * */
    public static boolean BING_SHOW_IN_CHAT_VIEW = false;

    /**
     * 换肤开关
     * */
    public static boolean SKIN = false;

    /**
     * beeworks 检查更新
     * */
    public static boolean BEEWORKS_CHECK = true;

    /**
     * 消息声音开关默认值
     * */
    public static boolean DEFAULT_VOICE_SETTING = true;


    /**
     * 消息震动开关默认值
     * */
    public static boolean DEFAULT_VIBRATOR_SETTING = true;

    /**
     * 默认初始化打开的 tab 页面
     * */
    public static int INIT_JUMP_TAB = 0;

    /**
     * umeeting 配置
     * */
    public static UmeetingConfig UMEETING_CONFIG = null;

    /**
     * 是否集成全时云日历
     * */
    public static boolean INTEGRATE_UCCALENDAR = false;

    public static boolean PERSISTENCE_PWD = true;

    /**
     * webview 销毁时, 是否需要告诉上个页面, 并执行 "goBack()", 目前科达需要
     * */
    public static boolean COMMAND_DO_GOBACK_FROM_WEBVIEW_FINISH = false;

    /**
     * 是否需要强制调用 goback() js 方法, 暂时华三依据此来监听页面的返回
     */
    public static boolean COMMAND_RECEIVE_WEBVIEW_GOBACK = false;

    /**
     * 个人帐号信息界面控制配置
     * */
    public static UserInfoViewConfig USER_INFO_VIEW_CONFIG = new UserInfoViewConfig();

    public static EmployeeConfig EMPLOYEE_CONFIG = new EmployeeConfig();

    public static AssetConfig ASSET_CONFIG = new AssetConfig();

    public static SearchConfig SEARCH_CONFIG = new SearchConfig();

    /**
     * 登录页面控制配置
     * */
    public static LoginViewConfig LOGIN_VIEW_CONFIG = new LoginViewConfig();

    /**
     * webview 控制配置
     * */
    public static WebviewConfig WEBVIEW_CONFIG = new WebviewConfig();



    public static BehaviorLogConfig BEHAVIOR_LOG_CONFIG = new BehaviorLogConfig();

    /**
     * 主界面悬浮 "+" 的控制配置
     * */
    public static MainFabBottomPopConfig MAIN_FAB_BOTTOM_POP_CONFIG = new MainFabBottomPopConfig();

    /**
     * 关于 workplus 的页面的控制配置
     * */
    public static AboutWorkplusConfig ABOUT_WORKPLUS_CONFIG = new AboutWorkplusConfig();

    /**
     * 基础权限的控制配置
     * */
    public static BasePermissionConfig BASE_PERMISSION_CONFIG = new BasePermissionConfig();


    /**
     * 关于我的页面的控制配置
     * */
    public static AboutMeConfig ABOUT_ME_CONFIG = new AboutMeConfig();


    /**
     * 阅后即焚配置
     * */
    public static BurnMessageConfig BURN_MESSAGE_CONFIG = new BurnMessageConfig();


    /**
     * 撤回消息配置
     * */
    public static UndoMessageConfig UNDO_MESSAGE_CONFIG = new UndoMessageConfig();


    /**
     * bugly 配置
     * */
    public static BuglyConfig BUGLY_CONFIG = new BuglyConfig();

    /**
     * 聊天记录检查配置
     * */
    public static ChatRecordCheckConfig CHAT_RECORD_CHECK_CONFIG = new ChatRecordCheckConfig();

    /**
     * 聊天相关配置
     * */
    public static ChatConfig CHAT_CONFIG = new ChatConfig();

    /**
     * 群组配置
     * */
    public static DiscussionConfig DISSCUSSION_CONFIG = new DiscussionConfig();

    /**
     * 旺旺集团 auth api url
     * */
    public static WangWangAuthUrlConfig WANGWANG_AUTH_URL_CONFIG = new WangWangAuthUrlConfig();

    /**
     * 圈子配置
     * */
    public static CircleConfig CIRCLE_CONFIG = new CircleConfig();

    /**
     * 组织相关配置
     * */
    public static OrgConfig ORG_CONFIG = new OrgConfig();

    /**
     * 网盘相关配置
     * */
    public static DropboxConfig DROPBOX_CONFIG = new DropboxConfig();

    /**
     * 启动页配置
     */
    public static SplashConfig SPLASH_CONFIG = new SplashConfig();

    /**
     * 加密相关配置
     * */
    public static EncryptConfig ENCRYPT_CONFIG = new EncryptConfig();

    /**
     * 生物认证配置
     * */
    public static BiometricAuthenticationConfig BIOMETRICAUTHENTICATION_CONFIG = new BiometricAuthenticationConfig();

    /**
     * 文件配置
     * */
    public static FileConfig FILE_CONFIG = new FileConfig();

    /**
     * 应用配置
     * */
    public static AppConfig APP_CONFIG = new AppConfig();

    /**
     * 通知配置
     * */
    public static NotificationConfig NOTIFICATION_CONFIG = new NotificationConfig();

    /**
     * 修复模式配置
     * */
    public static W6sBugFixConfig W6SBUGFIX_CONFIG = new W6sBugFixConfig();


    /**
     * 通讯录相关配置
     * */
    public static ContactConfig CONTACT_CONFIG = new ContactConfig();

    /**
     * 工作台配置
     * */
    public static WorkbenchConfig WORKBENCH_CONFIG = new  WorkbenchConfig();

    public static KPPAVerifyConfig KPPA_VERIFY_CONFIG = new KPPAVerifyConfig();

    public static OctConfig OCT_CONFIG = new OctConfig();


    /**
     * 调试模式配置
     * */
    public static TestModeConfig TEST_MODE_CONFIG = new TestModeConfig();


    /**
     * 权限相关跳转地址配置(修改密码等)
     * */
    public static AuthRouteConfig AUTH_ROUTE_CONFIG = new AuthRouteConfig();



    /**
     * 表情配置
     * */
    public static StickerConfig STICKER_CONFIG = new StickerConfig();

    /**
     * '语音'配置(语音搜索)
     * */
    public static VoiceConfig VOICE_CONFIG = new VoiceConfig();

    /**
     * 设置页面配置
     * */
    public static SettingPageConfig SETTING_PAGE_CONFIG = new SettingPageConfig();

    /**
     * 服务号历史消息配置
     * */
    public static ServiceAppHistoricalMessageConfig SERVICE_APP_HISTORICAL_MESSAGE_CONFIG = new ServiceAppHistoricalMessageConfig();

    /**
     * cdn 配置
     * */
    public static CdnConfig CDN_CONFIG = new CdnConfig();

    /**
     * zoom 配置
     * */
    public static ZoomConfig ZOOM_CONFIG = new ZoomConfig();

    /**
     * 消息配置
     * */
    public static MessageConfig MESSAGE_CONFIG = new MessageConfig();


    /**
     * 默认字体大小
     * */
    public static int DEFAULT_TEXT_SIZE = 1;

    /**
     * 华为推送id
     */
    public static String HUAWEI_PUSH_APP_ID = "";

    /**
     * 小米推送id
     */
    public static String XIAOMI_PUSH_APP_ID = "";
    public static String XIAOMI_PUSH_APP_KEY = "";

    /**
     * 魅族推送id
     * */
    public static String MEIZU_PUSH_APP_ID = "";
    public static String MEIZU_PUSH_APP_KEY = "";


    /**
     * oppo 推送id
     */
    public static String OPPO_PUSH_APP_ID = "";
    public static String OPPO_PUSH_APP_KEY = "";

    /**
     * VIVO 推送id
     */
    public static String VIVO_PUSH_APP_ID = "";
    public static String VIVO_PUSH_APP_KEY = "";

    public static Boolean HTTP_DNS_ENABLE = false;

    /**
     * 智能机器人默认开关
     */
    public static boolean DEFAULT_ROBOT_SETTING = true;
    public static boolean DEFAULT_SERVER_ROBOT_SETTING = false;
    public static boolean DEFAULT_ROBOT_SHARK_SETTING = false;
    public static long DEFAULT_ROBOT_ORDER_UPDATE_TIME = -1;

    /**
     * Gitee拍配置
     */
    public static GiteeConfig GITEE_CONFIG = new GiteeConfig();


    public static void initConfig() {
        final Properties pro = new Properties();
        InputStream inputStream = AtworkConfig.class.getResourceAsStream("/assets/atwork.properties");

        try {
            pro.load(inputStream);
            APP_FOLDER = pro.getProperty("APP_FOLDER");
            API_URL = BeeWorks.getInstance().config.apiUrl;
            API_MEDIA_URL = BeeWorks.getInstance().config.apiMediaUrl;


            if(!StringUtils.isEmpty(CommonShareInfo.getDomainId(BaseApplicationLike.baseContext))) {
                DOMAIN_ID = CommonShareInfo.getDomainId(BaseApplicationLike.baseContext);

            } else {
                DOMAIN_ID = BeeWorks.getInstance().config.domainId;

            }


            if (BaseApplicationLike.sDomainSettings != null && !TextUtils.isEmpty(BaseApplicationLike.sDomainSettings.getDashBaseUrl())) {
                ADMIN_URL = BaseApplicationLike.sDomainSettings.getDashBaseUrl();
            } else {
                ADMIN_URL = BeeWorks.getInstance().config.adminUrl;
            }

            DUPLICATE_REMOVAL = BeeWorks.getInstance().config.duplicateRemoval;
            PROTOCOL = BeeWorks.getInstance().config.protocol;
            ADMIN_MEDIA_URL = BeeWorks.getInstance().config.adminMediaUrl;
            YSKJ_APP_CODE_URL = BeeWorks.getInstance().config.beeWorksScanner.appCode;

            CORDOVA_SCHEDULE_URL = BeeWorks.getInstance().config.getScheduleUrl();
            COLLEAGUE_URL = BeeWorks.getInstance().config.getColleagueCircleUrl();
            ARTICLE_URL = BeeWorks.getInstance().config.articleContentURL;

//            MAIN_ACTIVITY_SHAKE_URL = BeeWorks.getInstance().config.getShakeUrl();

            UMENG_APPKEY = BeeWorks.getInstance().config.beeWorksUMeng.appId;
            CHANNEL_ID = BeeWorks.getInstance().config.beeWorksUMeng.channelId;
            UMENG_ANALITICS = BeeWorks.getInstance().config.beeWorksUMeng.enabled;

            PROFILE = BeeWorks.getInstance().config.profile;
            APP_ID = BeeWorks.getInstance().beeWorksAppBase.mBundleId;
            try {
                WX_APP_ID = BeeWorks.getInstance().config.beeWorksShare.mShareWX.appId;
                QQ_APP_ID = BeeWorks.getInstance().config.beeWorksShare.mShareQQ.mShareAndroid.appId;
            } catch (Exception e) {
                e.printStackTrace();
            }
            OPEN_DROPBOX = false;


            OPEN_VOIP = null != VOIP_SDK_TYPE;

            if(null != BeeWorks.getInstance().config.beeWorkEncryption) {
                OPEN_DB_ENCRYPTION = BeeWorks.getInstance().config.beeWorkEncryption.mDb;
                OPEN_DISK_ENCRYPTION = BeeWorks.getInstance().config.beeWorkEncryption.mDisk;
                OPEN_VOIP_ENCRYPTION = BeeWorks.getInstance().config.beeWorkEncryption.mVoip;
                OPEN_IM_CONTENT_ENCRYPTION = BeeWorks.getInstance().config.beeWorkEncryption.mIm;
                TK_TYPE = BeeWorks.getInstance().config.beeWorkEncryption.mTkType;

            }

            if (null != BeeWorks.getInstance().beeworksApn) {
                HUAWEI_PUSH_APP_ID = BeeWorks.getInstance().beeworksApn.getHuaweiInfo().getAppId();

                XIAOMI_PUSH_APP_ID = BeeWorks.getInstance().beeworksApn.getXiaomiInfo().getAppId();
                XIAOMI_PUSH_APP_KEY = BeeWorks.getInstance().beeworksApn.getXiaomiInfo().getAppKey();

                MEIZU_PUSH_APP_ID = BeeWorks.getInstance().beeworksApn.getMeizuInfo().getAppId();
                MEIZU_PUSH_APP_KEY = BeeWorks.getInstance().beeworksApn.getMeizuInfo().getAppKey();

                OPPO_PUSH_APP_ID = BeeWorks.getInstance().beeworksApn.getOppoInfo().getAppId();
                OPPO_PUSH_APP_KEY = BeeWorks.getInstance().beeworksApn.getOppoInfo().getAppKey();

                VIVO_PUSH_APP_ID = BeeWorks.getInstance().beeworksApn.getVivoInfo().getAppId();
                VIVO_PUSH_APP_KEY = BeeWorks.getInstance().beeworksApn.getVivoInfo().getAppKey();
            }

            if (null != BeeWorks.getInstance().config.beeWorksSetting) {
                HTTP_DNS_ENABLE = BeeWorks.getInstance().config.beeWorksSetting.getHttpDnsSetting().getEnable();
            }

            //一些特殊配置，暂时未纳入BeeWorks中
            APP_URL_IP = pro.getProperty("APP_URL_IP");
//            DB_VERSION = Integer.parseInt(pro.getProperty("versionNum"));


            if (pro.containsKey("H3C_CONFIG")) {
                H3C_CONFIG = Boolean.valueOf(pro.getProperty("H3C_CONFIG"));
            }

            if (pro.containsKey("OCT_QR_EPASS_URL")) {
                OCT_CONFIG.setOctQrEpassUrl(pro.getProperty("OCT_QR_EPASS_URL"));
            }

            if (pro.containsKey("OCT_MODIFY_PASSWORK_URL")) {
                OCT_CONFIG.setOctModifyPwdUrl(pro.getProperty("OCT_MODIFY_PASSWORK_URL"));
            }


            if (pro.containsKey("VPN_CHANNEL")) {
                VPN_CHANNEL = Boolean.valueOf(pro.getProperty("VPN_CHANNEL"));
            }


            if (pro.containsKey("USE_V2_COLLEAGUE_CIRCLE")) {
                USE_V2_COLLEAGUE_CIRCLE = Boolean.valueOf(pro.getProperty("USE_V2_COLLEAGUE_CIRCLE"));
            }

            HIDE_TEL = Boolean.valueOf(pro.getProperty("HIDE_TEL"));

            if(pro.containsKey("DB_SUFFIX")) {
                DB_SUFFIX = pro.getProperty("DB_SUFFIX");
            }

            if(pro.containsKey("SP_SUFFIX_COMMON_AND_PERSONAL_FILE")) {
                SP_SUFFIX_COMMON_AND_PERSONAL_FILE = pro.getProperty("SP_SUFFIX_COMMON_AND_PERSONAL_FILE");
            }

            if(pro.containsKey("SP_SUFFIX_USER_LOGIN_FILE")) {
                SP_SUFFIX_USER_LOGIN_FILE = pro.getProperty("SP_SUFFIX_USER_LOGIN_FILE");
            }

            if(pro.containsKey("VOIP_MEMBER_COUNT_MAX")) {
                VOIP_MEMBER_COUNT_MAX = Integer.parseInt(pro.getProperty("VOIP_MEMBER_COUNT_MAX"));
            }


            if(pro.containsKey("SCREEN_ORIENTATION_USER_SENSOR")) {
                SCREEN_ORIENTATION_USER_SENSOR = Boolean.valueOf(pro.getProperty("SCREEN_ORIENTATION_USER_SENSOR"));
            }

            GITEE_CONFIG.parse(pro);

            BeeWorksTextTranslate beeWorkTextTranslate = BeeWorks.getInstance().config.beeWorkTextTranslate;
            if(null != beeWorkTextTranslate && beeWorkTextTranslate.isLegal()) {
                TextTranslateSdkType sdkType = TextTranslateSdkType.valueOf(beeWorkTextTranslate.mType.toUpperCase());
                String key = beeWorkTextTranslate.getKey();

                TEXT_TRANSLATE_SDK = new TextTranslateSdk(sdkType, key);
            }

            BeeWorksXunfei beeWorkXunfei = BeeWorks.getInstance().config.beeWorkXunfei;
            if(null != beeWorkXunfei && beeWorkXunfei.isLegal()) {
                VoiceTranslateSdkType sdkType = VoiceTranslateSdkType.XUNFEI;
                String key = beeWorkXunfei.getKey();

                VOICE_TRANSLATE_SDK = new VoiceTranslateSdk(sdkType, key);
            }


            if(pro.containsKey("LIGHT_APP_INIT_HIDE_MORE_BTN")) {
                LIGHT_APP_INIT_HIDE_MORE_BTN = Boolean.valueOf(pro.getProperty("LIGHT_APP_INIT_HIDE_MORE_BTN"));
            }

            if(pro.containsKey("SPECIAL_ENABLE_DISCUSSION_FILE_WHEN_CLOSE_DROPBOX")) {
                SPECIAL_ENABLE_DISCUSSION_FILE_WHEN_CLOSE_DROPBOX = Boolean.valueOf(pro.getProperty("SPECIAL_ENABLE_DISCUSSION_FILE_WHEN_CLOSE_DROPBOX"));
            }

            if(pro.containsKey("SERVICE_APP_LIST_ENTRY")) {
                SERVICE_APP_LIST_ENTRY = Boolean.valueOf(pro.getProperty("SERVICE_APP_LIST_ENTRY"));
            }

            if(pro.containsKey("MAIN_VIEW_PAGER_CAN_SCROLL")) {
                MAIN_VIEW_PAGER_CAN_SCROLL = Boolean.valueOf(pro.getProperty("MAIN_VIEW_PAGER_CAN_SCROLL"));
            }

            if(pro.containsKey("ABOUT_APP_LABEL_PURE")) {
                ABOUT_APP_LABEL_PURE = Boolean.valueOf(pro.getProperty("ABOUT_APP_LABEL_PURE"));
            }

            if(pro.containsKey("LOGIN_AVATAR_CAN_MODIFY")) {
                LOGIN_AVATAR_CAN_MODIFY = Boolean.valueOf(pro.getProperty("LOGIN_AVATAR_CAN_MODIFY"));
            }

            if(pro.containsKey("CUSTOM_MODIFY_API_URL")) {
                CUSTOM_MODIFY_API_URL = pro.getProperty("CUSTOM_MODIFY_API_URL");
            }

            if (pro.containsKey("SHOW_INTRODUCE")) {
                SHOW_INTRODUCE = Boolean.valueOf(pro.getProperty("SHOW_INTRODUCE"));
            }

            if(pro.containsKey("BEEWORKS_CHECK")) {
                BEEWORKS_CHECK = Boolean.valueOf(pro.getProperty("BEEWORKS_CHECK"));
            }

            if(pro.containsKey("DEFAULT_VOICE_SETTING")) {
                DEFAULT_VOICE_SETTING = Boolean.valueOf(pro.getProperty("DEFAULT_VOICE_SETTING"));
            }

            if(pro.containsKey("DEFAULT_VIBRATOR_SETTING")) {
                DEFAULT_VIBRATOR_SETTING = Boolean.valueOf(pro.getProperty("DEFAULT_VIBRATOR_SETTING"));
            }

            if (pro.containsKey("INIT_JUMP_TAB")) {
                INIT_JUMP_TAB = Integer.parseInt(pro.getProperty("INIT_JUMP_TAB"));
            }

            if(pro.containsKey("INTEGRATE_UCCALENDAR")) {
                INTEGRATE_UCCALENDAR = Boolean.valueOf(pro.getProperty("INTEGRATE_UCCALENDAR"));
            }

            if(pro.containsKey("COMMAND_DO_GOBACK_FROM_WEBVIEW_FINISH")) {
                COMMAND_DO_GOBACK_FROM_WEBVIEW_FINISH = Boolean.valueOf(pro.getProperty("COMMAND_DO_GOBACK_FROM_WEBVIEW_FINISH"));
            }

            if(isUmeetingValid(pro)) {

                UMEETING_CONFIG = UmeetingConfig.newInstance()
                        .setAppKey(pro.getProperty("UMEETING_APP_KEY"))
                        .setAppSecret(pro.getProperty("UMEETING_APP_SECRET"))
                        .setWebDomain(pro.getProperty("UMEETING_WEB_DOMAIN"))
                        .setUrl(pro.getProperty("UMEETING_URL"))
                        .setInviteUrl(pro.getProperty("UMEETING_INVITE_URL"))
                        .setInvokeUrl(pro.getProperty("UMEETING_INVOKE_URL"));

            }



            if(pro.containsKey("PERSISTENCE_PWD")) {
                PERSISTENCE_PWD = Boolean.valueOf(pro.getProperty("PERSISTENCE_PWD"));
            }

            if(pro.containsKey("COMMAND_RECEIVE_WEBVIEW_GOBACK")) {
                COMMAND_RECEIVE_WEBVIEW_GOBACK = Boolean.valueOf(pro.getProperty("COMMAND_RECEIVE_WEBVIEW_GOBACK"));
            }


            if(pro.containsKey("USER_INFO_SHOW_GENDER")) {
                USER_INFO_VIEW_CONFIG.setShowGender(Boolean.valueOf(pro.getProperty("USER_INFO_SHOW_GENDER")));
            }

            if(pro.containsKey("USER_INFO_SHOW_BIRTHDAY")) {
                USER_INFO_VIEW_CONFIG.setShowBirthday(Boolean.valueOf(pro.getProperty("USER_INFO_SHOW_BIRTHDAY")));
            }

            if(pro.containsKey("USER_INFO_COMMAND_INITIALIZED")) {
                USER_INFO_VIEW_CONFIG.setCommandInitialized(Boolean.valueOf(pro.getProperty("USER_INFO_COMMAND_INITIALIZED")));
            }



            if(pro.containsKey("LOGIN_AVATAR_SIZE")) {
                LOGIN_VIEW_CONFIG.setAvatarSize(Integer.valueOf(pro.getProperty("LOGIN_AVATAR_SIZE")));
            }


            if(pro.containsKey("MAIN_FAB_BOTTOM_POP_CONTACT_ITEM_IN_CHAT")) {
                MAIN_FAB_BOTTOM_POP_CONFIG.setContactItemInChat(Boolean.valueOf(pro.getProperty("MAIN_FAB_BOTTOM_POP_CONTACT_ITEM_IN_CHAT")));
            }

            if(pro.containsKey("MAIN_FAB_BOTTOM_POP_FRIEND_ITEM_IN_CHAT")) {
                MAIN_FAB_BOTTOM_POP_CONFIG.setFriendItemInChat(Boolean.valueOf(pro.getProperty("MAIN_FAB_BOTTOM_POP_FRIEND_ITEM_IN_CHAT")));
            }


            if(pro.containsKey("BURN_COMMAND_HIDE_WATERMARK")) {
                BURN_MESSAGE_CONFIG.setCommandHideWatermark(Boolean.valueOf(pro.getProperty("BURN_COMMAND_HIDE_WATERMARK")));
            }

//            if(pro.containsKey("UNDO_EXPIRY_PERIOD")) {
//                UNDO_MESSAGE_CONFIG.setUndoExpiryPeriod(Long.valueOf(pro.getProperty("UNDO_EXPIRY_PERIOD")));
//            }


            if(pro.containsKey("CHAT_RECORD_CHECK_NEED_REMOTE")) {
                CHAT_RECORD_CHECK_CONFIG.setNeedRemote(Boolean.valueOf(pro.getProperty("CHAT_RECORD_CHECK_NEED_REMOTE")));
            }


            if(pro.containsKey("CHAT_RECORD_CALIBRATE_USER_SESSION")) {
                CHAT_RECORD_CHECK_CONFIG.setNeedCalibrateUserSession(Boolean.valueOf(pro.getProperty("CHAT_RECORD_CALIBRATE_USER_SESSION")));
            }


            if(pro.containsKey("BEHAVIOR_LOG_ENABLE")) {
                BEHAVIOR_LOG_CONFIG.setEnable(Boolean.valueOf(pro.getProperty("BEHAVIOR_LOG_ENABLE")));
            }

            if(pro.containsKey("WANGWANG_AUTH_API_URL")) {
                WANGWANG_AUTH_URL_CONFIG.setApiUrl(pro.getProperty("WANGWANG_AUTH_API_URL"));
            }


            if(pro.containsKey("DROPBOX_FORCE_ALL_REFRESH_TAG")) {
                DROPBOX_CONFIG.setForceAllRefreshTag(pro.getProperty("DROPBOX_FORCE_ALL_REFRESH_TAG"));
            }


            if(pro.containsKey("DROPBOX_FORCED_SHOW_WATERMARK")) {
                DROPBOX_CONFIG.setForcedShowWaterMark(Boolean.valueOf(pro.getProperty("DROPBOX_FORCED_SHOW_WATERMARK")));
            }

            if(pro.containsKey("DEFAULT_TEXT_SIZE")) {
                DEFAULT_TEXT_SIZE = Integer.parseInt(pro.getProperty("DEFAULT_TEXT_SIZE"));
            }



            CDN_CONFIG.parse();
            ZOOM_CONFIG.parse();
            SERVICE_APP_HISTORICAL_MESSAGE_CONFIG.parse();
            VOICE_CONFIG.parse();
            SETTING_PAGE_CONFIG.parse();
            KPPA_VERIFY_CONFIG.parse();
            BUGLY_CONFIG.parse();

            LOGIN_VIEW_CONFIG.parse(pro);
            ABOUT_ME_CONFIG.parse(pro);
            SPLASH_CONFIG.parse(pro);
            WEBVIEW_CONFIG.parse(pro);
            SEARCH_CONFIG.parse(pro);
            ASSET_CONFIG.parse(pro);
            FILE_CONFIG.parse(pro);
            ENCRYPT_CONFIG.parse(pro);
            CHAT_CONFIG.parse(pro);
            APP_CONFIG.parse(pro);
            CONTACT_CONFIG.parse(pro);
            TEST_MODE_CONFIG.parse(pro);
            CIRCLE_CONFIG.parse(pro);
            BIOMETRICAUTHENTICATION_CONFIG.parse(pro);
            W6SBUGFIX_CONFIG.parse(pro);
            WORKBENCH_CONFIG.parse(pro);
            NOTIFICATION_CONFIG.parse(pro);

            APP_CONFIG.parse(pro);
            DISSCUSSION_CONFIG.parse(pro);
            EMPLOYEE_CONFIG.parse(pro);
            ABOUT_WORKPLUS_CONFIG.parse(pro);
            STICKER_CONFIG.parse(pro);
            AUTH_ROUTE_CONFIG.parse(pro);
            ORG_CONFIG.parse(pro);
            MESSAGE_CONFIG.parse(pro);
            BASE_PERMISSION_CONFIG.parse(pro);


        } catch (Exception e) {
            String message = e.getLocalizedMessage();
            if(null == message) {
                message = StringUtils.EMPTY;
            }

            LogUtil.e(TAG, message);

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean isUmeetingValid(Properties pro) {
        return pro.containsKey("UMEETING_APP_KEY")
                && pro.containsKey("UMEETING_APP_SECRET")
                && pro.containsKey("UMEETING_WEB_DOMAIN");
    }


    public static int getEncryptModeConfig() {
        int config = getDbEncryptionConfig();

        if(OPEN_DISK_ENCRYPTION) {
            config |= FLAG_OPEN_DISK_ENCRYPTION;
        }

        return config;
    }

    public static int getDbEncryptionConfig() {
        int config = FLAG_NO_ENCRYPTION;
        if(OPEN_DB_ENCRYPTION) {
            config |= FLAG_OPEN_DB_ENCRYPTION;
        }
        return config;
    }


    public static int getDiskEncryptionConfig() {
        int config = FLAG_NO_ENCRYPTION;
        if(OPEN_DISK_ENCRYPTION) {
            config |= FLAG_OPEN_DISK_ENCRYPTION;
        }
        return config;
    }

    public static boolean openTextTranslate() {
        return null != AtworkConfig.TEXT_TRANSLATE_SDK;
    }

    public static boolean openVoiceTranslate() {
        return null != AtworkConfig.VOICE_TRANSLATE_SDK;
    }


    public static boolean hasCustomForgetPwdJumpUrl() {
        return !StringUtils.isEmpty(AUTH_ROUTE_CONFIG.getCustomForgetPwdJumpUrl());
    }

    public static boolean hasCustomModifyPwdJumpUrl() {
        return !StringUtils.isEmpty(AUTH_ROUTE_CONFIG.getCustomModifyPwdJumpUrl());
    }


    public static boolean hasCustomVerificationCodeUrl() {
        return !StringUtils.isEmpty(AUTH_ROUTE_CONFIG.getCustomVerificationCodeLogin());
    }

    public static String getDeviceId() {

        if(StringUtils.isEmpty(DEVICE_ID)) {

            String deviceId = CommonShareInfo.getDeviceId(BaseApplicationLike.baseContext);
            if(!StringUtils.isEmpty(deviceId)) {
                setDeviceId(deviceId);
            }

        }


        if(StringUtils.isEmpty(DEVICE_ID)) {
            if((PermissionsManager.getInstance().hasPermission(BaseApplicationLike.baseContext, Manifest.permission.READ_PHONE_STATE))) {
                DeviceUtil.initDeviceId(BaseApplicationLike.baseContext);
            }
        }


//        CrashReport.postCatchedException(new Throwable("{{device_id}} -> " + DEVICE_ID));

//        LogUtil.e("DEVICE_ID  -------> " + DEVICE_ID);
        return DEVICE_ID;
    }

    public static void setDeviceId(String deviceId) {
        DEVICE_ID = deviceId;
    }

    public static String getApiMediaUrl() {
        if(!StringUtils.isEmpty(API_MEDIA_URL)) {
            return API_MEDIA_URL;
        }

        return API_URL;
    }

    public static String getAdminMediaUrl() {
        if(!StringUtils.isEmpty(ADMIN_MEDIA_URL)) {
            return ADMIN_MEDIA_URL;
        }

        return ADMIN_URL;
    }


}
