package com.foreveross.atwork;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.foreverht.cache.MessageCache;
import com.foreverht.cache.OrgCache;
import com.foreverht.cache.UserCache;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.webview.WebkitSdkUtil;
import com.foreverht.webview.X5EngineUtil;
import com.foreverht.workplus.amap.AmapManager;
import com.foreverht.workplus.amap.WorkPlusLocationManager;
import com.foreverht.workplus.notification.UpsManager;
import com.foreveross.atwork.api.sdk.Employee.EmployeeAsyncNetService;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.RequestRemoteInterceptor;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.broadcast.NetworkBroadcastReceiver;
import com.foreveross.atwork.component.floatView.service.WorkplusFloatService;
import com.foreveross.atwork.crash.CrashExceptionHandler;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.BuildConfig;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.manager.speedUp.SpeedUpManager;
import com.foreveross.atwork.infrastructure.manager.zoom.ZoomManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.organizationSetting.OrganizationSettings;
import com.foreveross.atwork.infrastructure.model.organizationSetting.VasSettings;
import com.foreveross.atwork.infrastructure.model.speedUp.SpeedUpSdk;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.shared.ApiSettingInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.shared.dev.DevCommonShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AsyncTaskSentry;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.foreveross.atwork.listener.AppActivityLifecycleListener;
import com.foreveross.atwork.manager.AgreementManager;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.manager.BasicNotificationManager;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.FriendManager;
import com.foreveross.atwork.manager.IESInflaterManager;
import com.foreveross.atwork.manager.MessageNoticeManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.OrganizationSettingsHelper;
import com.foreveross.atwork.manager.SkinManger;
import com.foreveross.atwork.manager.UCCalendarManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.manager.VoipNoticeManager;
import com.foreveross.atwork.manager.WorkplusUpdateManager;
import com.foreveross.atwork.manager.im.OfflineMessageSessionStrategyManager;
import com.foreveross.atwork.manager.model.MultiResult;
import com.foreveross.atwork.modules.advertisement.manager.BootAdvertisementManager;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.clickStatistics.ClickStatisticsManager;
import com.foreveross.atwork.modules.contact.data.StarUserListDataWrap;
import com.foreveross.atwork.modules.lite.manager.LiteManager;
import com.foreveross.atwork.modules.login.activity.BasicLoginActivity;
import com.foreveross.atwork.modules.login.service.LoginService;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.main.data.TabNoticeManager;
import com.foreveross.atwork.modules.monitor.tingyun.TingyunManager;
import com.foreveross.atwork.modules.voip.activity.CallActivity;
import com.foreveross.atwork.modules.voip.service.CallService;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager;
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager;
import com.foreveross.atwork.services.ImSocketService;
import com.foreveross.atwork.services.support.AlarmMangerHelper;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.OutFieldPunchHelper;
import com.foreveross.db.SQLiteDatabase;
import com.foreveross.translate.Translator;
import com.iflytek.cloud.SpeechUtility;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.tencent.bugly.crashreport.CrashReport;
import com.w6s.handyfloat.HandyFloat;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AtworkApplicationLike extends BaseApplicationLike {

    public static final String TAG = AtworkApplication.class.getName();

    public static NetworkBroadcastReceiver.NetWorkType sNetWorkType;
    private static List<String> sInstalledApps;

    public static long sStartTime = -1;



    public static void clearData() {
        //停止所有按会话拉取消息任务
        OfflineMessageSessionStrategyManager.INSTANCE.cancelAll();
        ChatDaoService.getInstance().clear();

        OrgCache.getInstance().clear();
        OutFieldPunchHelper.stopAllOutFieldIntervalPunch(baseContext);
        ImSocketService.closeConnection();
        new LoginService(baseContext).deleteDeviceForRemote();
        AppManager.getInstance().clear();
        OrganizationManager.getInstance().clear();
        TabNoticeManager.getInstance().clear();
        MediaCenterHttpURLConnectionUtil.getInstance().breakAll();
        StarUserListDataWrap.getInstance().clear();
        MessageCache.getInstance().clear();
        ChatSessionDataWrap.getInstance().clear();
        MessageNoticeManager.getInstance().clear();
        LoginUserInfo.getInstance().clear(baseContext);
        PersonalShareInfo.getInstance().clear();
        MediaCenterNetManager.breakAll(false);

        ImageCacheHelper.refreshLoader(baseContext);
        if (VoipHelper.isHandlingVoipCall()) {
            VoipManager.getInstance().getVoipMeetingController().stopCall();
        }

        ClickStatisticsManager.INSTANCE.clear();

        UCCalendarManager.getInstance().onCalendarLogout();

        WorkplusFloatService.Companion.sendAllRemoveFloatingWindow();

        if(AtworkConfig.WEBVIEW_CONFIG.isLogoutClearCookies()) {
            WebkitSdkUtil.clearCookies();
        }

        LoginUserInfo.getInstance().setUserNeedClearWebview(baseContext, true);
        FriendManager.getInstance().clear();
        DiscussionManager.getInstance().clear();

        WorkbenchManager.INSTANCE.clear();
        WorkbenchAdminManager.INSTANCE.clear();

        AsyncTaskSentry.INSTANCE.clearTask();

        AgreementManager.SHOULD_CHECK_AGREEMENT =  true;

        testAgreement();


        Logger.e("ACCESSTOEKN", "call clearData");


    }

    /**
     * 调试用户保密协议打开的开关
     */
    private static void testAgreement() {
        if (BuildConfig.DEBUG && true) {
            PersonalShareInfo.getInstance().setLoginSignedAgreementConfirmed(baseContext, false);
        }
    }

    /**
     * 同步方法, 根据 org_code查询当前用户对应的雇员信息
     */
    @Nullable
    public static Employee getLoginUserEmpSync(String orgCode) {
        return EmployeeManager.getInstance().queryEmpInSync(baseContext, LoginUserInfo.getInstance().getLoginUserId(baseContext), orgCode);
    }

    /**
     * 根据 org_code查询当前用户对应的雇员信息
     */
    public static void getLoginUserEmp(String orgCode, EmployeeAsyncNetService.QueryEmployeeInfoListener listener) {
        EmployeeManager.getInstance().queryEmp(baseContext, LoginUserInfo.getInstance().getLoginUserId(baseContext), orgCode, listener);
    }

    /**
     * 同步方法, 获取当前登录的用户
     */
    @Nullable
    public static User getLoginUserSync() {
        User user = UserManager.getInstance().queryUserInSyncByUserId(baseContext, LoginUserInfo.getInstance().getLoginUserId(baseContext), AtworkConfig.DOMAIN_ID);
        return user;
    }

    /**
     * 同步方法, 获取当前登录用户的混合结果
     */
    public static MultiResult<User> getLoginUserResultSync() {
        return UserManager.getInstance().queryUserResultInSyncByUserId(baseContext, LoginUserInfo.getInstance().getLoginUserId(baseContext), AtworkConfig.DOMAIN_ID);
    }

    /**
     * 异步方法, 获取当前登录的用户
     */
    public static void getLoginUser(UserAsyncNetService.OnQueryUserListener listener) {


        //先从缓存中找
        User userCache = UserCache.getInstance().getUserCache(LoginUserInfo.getInstance().getLoginUserId(baseContext));
        if (null != userCache) {
            listener.onSuccess(userCache);
            return;
        }

        new AsyncTask<Void, Void, MultiResult>() {
            @Override
            protected MultiResult doInBackground(Void... params) {
                return getLoginUserResultSync();
            }

            @Override
            protected void onPostExecute(MultiResult multiResult) {
                if (listener == null) {
                    return;
                }
                User user = UserManager.getInstance().getUserFromMultiResult(multiResult);
                if (null != user) {
                    listener.onSuccess(user);
                    return;
                }
                if (multiResult == null) {
                    listener.networkFail(-3, null);
                    return;
                }
                NetworkHttpResultErrorHandler.handleHttpError(multiResult.httpResult, listener);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 获取当前登录用户的基本信息(从 sp 读取)
     */
    public static UserHandleInfo getLoginUserHandleInfo(Context context) {
        return LoginUserInfo.getInstance().getLoginUserBasic(context).toUserHandleInfo();
    }


    /**
     * 获取系统已安装应用
     */
    public static void refreshSystemInstalledApps() {
        try {
            List<PackageInfo> packages = baseContext.getPackageManager().getInstalledPackages(0);
            sInstalledApps = new ArrayList<>();
            for (PackageInfo packageInfo : packages) {
                sInstalledApps.add(packageInfo.packageName);
            }
        } catch (Exception e) {
            LogUtil.e("refreshSystemInstalledApps", e.getMessage());
        }

    }

    public static List<String> getInstalledApps() {
        if (sInstalledApps == null) {
            refreshSystemInstalledApps();
        }
        return sInstalledApps;
    }

    public static boolean isBackground() {
        ActivityManager activityManager = (ActivityManager) baseContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(baseContext.getPackageName())) {
                return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
            }
        }
        return false;
    }

    public static String getResourceString(int resId, Object... formatArgs) {
        try {
            return baseContext.getResources().getString(resId, formatArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    public Application getApplication() {
        return sApp;
    }

    @Override
    public void onCreate() {
        sStartTime = System.currentTimeMillis();
        super.onCreate();
        sApp = this;
        sIsDebug = BuildConfig.DEBUG;

        LogUtil.e(TAG, "Application start");


        if(!AtworkUtil.isMainProcessEnsured(sApp)) {
            return;
        }


        WebkitSdkUtil.init(sApp);
        if (!CustomerHelper.isRfchina(sApp)) {
            X5EngineUtil.init(sApp);
        }


        if (BuildConfig.DEBUG) {
            //使用严格的StrictMode,以防代码中的有阻塞UI的行为
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll()
//                    .penaltyDialog()
//                    .penaltyLog()
//                    .build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
//                    .penaltyLog()
//                    .build());
        }

        LogUtil.init(sIsDebug);

        LogUtil.e("service", "AtworkApplication  restart");

        baseContext = getApplication();

        SQLiteDatabase.loadLibs(sApp);

        BeeWorks.getInstance().reInitBeeWorks(sApp, beeWorks -> LiteManager.INSTANCE.processBeeWork(beeWorks, null));

        changeApi();

        configCrashLog();

        //10, 8DP所占用的PX数，计算出来
        DensityUtil.DP_1O_TO_PX = DensityUtil.dip2px(10);
        DensityUtil.DP_8_TO_PX = DensityUtil.dip2px(8);

        Logger.e(ImSocketService.TAG, "AtworkApplication -> reSetAlarm");
//        setAlarm(this);
        AlarmMangerHelper.initServiceGuardAlarm(sApp);

        refreshSystemInstalledApps();
        AtWorkDirUtils.getInstance().initialAtworkDirPath(sApp);

        //初始化imageloader
        ImageCacheHelper.refreshLoader(sApp);

        //日志
        Logger.setLogFilePath(AtWorkDirUtils.getInstance().getLOG());

        IESInflaterManager.getInstance().initIES(sApp);


        //初始定位
        AmapManager.getInstance().init(sApp);
        WorkPlusLocationManager.getInstance().init(sApp);

        initChromeStetho();

        SkinManger.getInstance().init(sApp);

        //初始化讯飞
        if (AtworkConfig.openVoiceTranslate()) {
            SpeechUtility.createUtility(sApp, "appid=" + AtworkConfig.VOICE_TRANSLATE_SDK.getKey());
        }

        //初始化翻译 sdk
        if (AtworkConfig.openTextTranslate()) {
            Translator.getInstance().setSdk(AtworkConfig.TEXT_TRANSLATE_SDK.getSdkType());
            Translator.getInstance().getTranslator().init(sApp, AtworkConfig.TEXT_TRANSLATE_SDK.getKey());
        }


        String orgCode = PersonalShareInfo.getInstance().getCurrentOrg(sApp);
        if (!TextUtils.isEmpty(orgCode)) {
            BootAdvertisementManager.getInstance().clearLastViewAdTime(sApp, orgCode);

        }

        OrganizationSettings organizationSetting = OrganizationSettingsManager.getInstance().getCurrentUserOrgSetting(orgCode);
        if(organizationSetting!=null){
            VasSettings zoomVasSetting = organizationSetting.getZoomSetting();

            //初始化umeeting
            if (zoomVasSetting != null) {
                String appKey = zoomVasSetting.getValue(VasSettings.ZOOM_APP_KEY);
                String appSecret = zoomVasSetting.getValue(VasSettings.ZOOM_APP_SECRET);
                String webDomain = zoomVasSetting.getValue(VasSettings.ZOOM_WEB_DOMAIN);
                ZoomManager.INSTANCE.init(sApp,appKey, appSecret, webDomain);
            }
        }


        BasicNotificationManager.createNotificationChannels(sApp);

        sApp.registerActivityLifecycleCallbacks(new AppActivityLifecycleListener());

        UpsManager.UpsInstance.getInstance().startUpsPush(sApp);

        handleInitTingyun();

        HandyFloat.init(sApp);

        WorkplusUpdateManager.INSTANCE.initTipFloatStatus();

        EncryptCacheDisk.getInstance().setRevertNamePure(AtworkConfig.ENCRYPT_CONFIG.isRevertNamePure());

        modifyDeviceSettings();

        if (DevCommonShareInfo.INSTANCE.isOpenWangsuSce(sApp)) {
            SpeedUpManager.INSTANCE.setSdk(SpeedUpSdk.WANGSU);
            SpeedUpManager.INSTANCE.init(sApp);
        }



        // 初始化日历
        AndroidThreeTen.init(sApp);
    }

    private void configCrashLog() {
        //设定自己的异常捕获
        Thread.setDefaultUncaughtExceptionHandler(new CrashExceptionHandler());

        if (AtworkConfig.BUGLY_CONFIG.getEnabled()) {

            CrashReport.initCrashReport(sApp, AtworkConfig.BUGLY_CONFIG.getAppId(), com.foreveross.atwork.BuildConfig.DEBUG);
            CrashReport.setIsDevelopmentDevice(sApp, com.foreveross.atwork.BuildConfig.DEBUG);
//            onTinkerConfig(sApp);
        }
    }


    private void handleInitTingyun() {
        if(null == BeeWorks.getInstance().config.beeWorksTinYun) {
            return;
        }

        if(!BeeWorks.getInstance().config.beeWorksTinYun.enalbed) {
            return;
        }

        if(StringUtils.isEmpty(BeeWorks.getInstance().config.beeWorksTinYun.appKey)) {
            return;
        }


        LogUtil.e("tingyun key -> " + BeeWorks.getInstance().config.beeWorksTinYun.appKey + "  enable -> " + BeeWorks.getInstance().config.beeWorksTinYun.enalbed);

        TingyunManager.INSTANCE.setLicenseKey(BeeWorks.getInstance().config.beeWorksTinYun.appKey).withLocationServiceEnabled(true).start(getApplication());

    }


    @Deprecated
    private void changeApi() {
        String apiUrl = ApiSettingInfo.getInstance().getApiUrl(getApplication());
        String adminUrl = ApiSettingInfo.getInstance().getAdminUrl(getApplication());
        String domainId = ApiSettingInfo.getInstance().getDomainId(getApplication());
        String profile = ApiSettingInfo.getInstance().getProfile(getApplication());
        int loginEncryptInt = ApiSettingInfo.getInstance().getLoginEncrypt(getApplication());



        if (!StringUtils.isEmpty(apiUrl)) {
            AtworkConfig.API_URL = apiUrl;
            BeeWorks.getInstance().config.apiUrl = apiUrl;
        }

        if(!StringUtils.isEmpty(adminUrl)) {
            AtworkConfig.ADMIN_URL = adminUrl;
            BeeWorks.getInstance().config.adminUrl = adminUrl;
        }


        if (!StringUtils.isEmpty(domainId)) {
            AtworkConfig.DOMAIN_ID = domainId;
            BeeWorks.getInstance().config.domainId = domainId;
        }


        if (!StringUtils.isEmpty(profile)) {
            AtworkConfig.PROFILE = profile;
            BeeWorks.getInstance().config.profile = profile;
        }



    }


    public static void appInvisibleHandle() {

        RequestRemoteInterceptor.INSTANCE.clear();

        WorkplusFloatService.Companion.sendAllRemoveFloatingWindow();

    }

    public static void appInvisibleHandleFloatView() {
        if (AtworkConfig.OPEN_VOIP && !AtworkConfig.VOIP_NEED_FLOATVIEW_DESKTOP_SHOW) {
            if (!CallActivity.sIsOpening) {
                VoipManager.getInstance().getVoipMeetingController().saveShowingVideo(-1);
            }

            CallService.sendRemoveFloatingWindow();

            if (VoipHelper.isHandlingVoipCallAndInit()) {
                VoipNoticeManager.getInstance().callingNotificationShow(baseContext, -1);
            }
        }

        WorkplusFloatService.Companion.sendAllRemoveFloatingWindow();
    }

    @Override
    public void exitApp(boolean killApp) {
        exitAll(this.getApplication().getApplicationContext(), killApp);
    }

    public static void exitAll(Context context, boolean kill) {
        if (context instanceof BasicLoginActivity) {
            ((BasicLoginActivity)context).finish();
            return;
        }
        context.startActivity(MainActivity.getExitAllIntent(context, kill));
    }

    public static void initCurrentOrgSettings() {
        String orgCode = PersonalShareInfo.getInstance().getCurrentOrg(baseContext);
        if (TextUtils.isEmpty(orgCode)) {
            return;
        }
        OrganizationSettingsHelper.getInstance().setCurrentOrgCodeAndRefreshSetting(baseContext, orgCode, true, false);
    }

    public static void initDomainAndOrgSettings() {
        //获取域设置
        getDomainSetting();
        //初始化当前组织的组织设置
        initCurrentOrgSettings();

    }

    /**
     * 测试环境下才启用 chrome 调试
     */
    public void initChromeStetho() {
        if (AtworkConfig.TEST_MODE_CONFIG.isTestMode()) {
            try {
                Class<?> classStetho = Class.forName("com.facebook.stetho.Stetho");

                Method initMethod = classStetho.getDeclaredMethod("initializeWithDefaults", Context.class);
                initMethod.invoke(null, baseContext);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private void initFreeline() {
        if (BuildConfig.DEBUG) {
            try {
                Class<?> classFreeline = Class.forName("com.antfortune.freeline.FreelineCore");

                Method initMethod = classFreeline.getDeclaredMethod("init", Context.class);
                initMethod.invoke(null, baseContext);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        modifyDeviceSettings();

    }

    public static void modifyDeviceSettings() {
        LoginUserInfo userInfo = LoginUserInfo.getInstance();
        String userId = userInfo.getLoginUserId(sApp);
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        String url = String.format(UrlConstantManager.getInstance().getModifyDeviceSettingsUrl(), userId, userInfo.getLoginUserAccessToken(sApp));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("locale", LanguageUtil.getCurrentSettingLocale(sApp).getLanguage());
            jsonObject.put("push_token", RomUtil.getPushTokenByRom(sApp));
            jsonObject.put("push_sound", "default");
            jsonObject.put("push_details", PersonalShareInfo.getInstance().getSettingShowDetails(sApp));
            jsonObject.put("push_enabled", PersonalShareInfo.getInstance().getSettingNotice(sApp));
            jsonObject.put("voip_token", RomUtil.getPushTokenByRom(sApp));
            jsonObject.put("voip_enabled", PersonalShareInfo.getInstance().getSettingNotice(sApp));
            //0表示不开启，1表示开启aes128
            jsonObject.put("encrypt_type", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Executors.newCachedThreadPool().submit((Runnable) () -> HttpURLConnectionComponent.getInstance().postHttp(url, jsonObject.toString()));
    }



}
