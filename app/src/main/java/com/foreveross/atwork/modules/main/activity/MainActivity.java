package com.foreveross.atwork.modules.main.activity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreverht.workplus.ui.component.statusbar.WorkplusStatusBarHelper;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.BuildConfig;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;
import com.foreveross.atwork.api.sdk.beeworks.BeeWorksNetService;
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.api.sdk.voip.responseJson.CreateOrQueryMeetingResponseJson;
import com.foreveross.atwork.broadcast.NetworkBroadcastReceiver;
import com.foreveross.atwork.component.ItemHomeTabView;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.component.floatView.service.WorkplusFloatService;
import com.foreveross.atwork.component.viewPager.CustomViewPager;
import com.foreveross.atwork.cordova.plugin.ContactPlugin_New;
import com.foreveross.atwork.cordova.plugin.WebViewPlugin;
import com.foreveross.atwork.db.daoService.OrganizationDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksTab;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.manager.zoom.ZoomManager;
import com.foreveross.atwork.infrastructure.model.IES;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.domain.UpgradeRemindMode;
import com.foreveross.atwork.infrastructure.model.orgization.OrgRelationship;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.voip.CallState;
import com.foreveross.atwork.infrastructure.model.voip.CurrentVoipMeeting;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.model.workbench.Workbench;
import com.foreveross.atwork.infrastructure.model.zoom.HandleMeetingInfo;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.BeeWorksPreviewData;
import com.foreveross.atwork.infrastructure.shared.BeeWorksPublicData;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.shared.SettingInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.ServiceCompat;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.manager.DataPackageManager;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.IESInflaterManager;
import com.foreveross.atwork.manager.OrgApplyManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.OrganizationSettingsHelper;
import com.foreveross.atwork.manager.ReceivingTitleQueueManager;
import com.foreveross.atwork.manager.ShakeDetector;
import com.foreveross.atwork.manager.SkinManger;
import com.foreveross.atwork.manager.SyncManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.manager.WorkplusUpdateManager;
import com.foreveross.atwork.manager.im.OfflineMessageReplayStrategyManager;
import com.foreveross.atwork.modules.aboutatwork.activity.AppUpgradeActivity;
import com.foreveross.atwork.modules.aboutatwork.fragment.AboutAtWorkFragment;
import com.foreveross.atwork.modules.aboutme.fragment.AboutMeFragment;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.app.dao.AppDaoService;
import com.foreveross.atwork.modules.app.fragment.AppFragment;
import com.foreveross.atwork.modules.app.route.UrlRouteHelper;
import com.foreveross.atwork.modules.app.util.AppRefreshHelper;
import com.foreveross.atwork.modules.bugFix.manager.W6sBugFixCoreManager;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.adapter.MainTabAdapter;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatListFragment;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.modules.chat.service.ChatService;
import com.foreveross.atwork.modules.configSettings.manager.ConfigSettingsManager;
import com.foreveross.atwork.modules.contact.fragment.ContactFragment;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.group.module.UserSelectControlAction;
import com.foreveross.atwork.modules.location.W6sLocationManager;
import com.foreveross.atwork.modules.login.activity.AccountLoginActivity;
import com.foreveross.atwork.modules.login.activity.LoginActivity;
import com.foreveross.atwork.modules.login.service.LoginService;
import com.foreveross.atwork.modules.login.util.LoginHelper;
import com.foreveross.atwork.modules.main.adapter.MainFabBottomPopAdapter;
import com.foreveross.atwork.modules.main.data.TabNoticeManager;
import com.foreveross.atwork.modules.main.helper.MainFabBottomPopupHelper;
import com.foreveross.atwork.modules.main.helper.TabHelper;
import com.foreveross.atwork.modules.main.service.HandleLoginService;
import com.foreveross.atwork.modules.meeting.service.UmeetingReflectService;
import com.foreveross.atwork.modules.monitor.tingyun.TingyunManager;
import com.foreveross.atwork.modules.route.manager.ActivityStack;
import com.foreveross.atwork.modules.route.manager.RouteActionConsumer;
import com.foreveross.atwork.modules.route.model.ActivityInfo;
import com.foreveross.atwork.modules.route.model.RouteParams;
import com.foreveross.atwork.modules.search.activity.NewSearchActivity;
import com.foreveross.atwork.modules.search.fragment.SearchFragment;
import com.foreveross.atwork.modules.search.model.NewSearchControlAction;
import com.foreveross.atwork.modules.search.model.SearchContent;
import com.foreveross.atwork.modules.secure.manager.ApkVerifyManager;
import com.foreveross.atwork.modules.voip.activity.VoipHistoryActivity;
import com.foreveross.atwork.modules.voip.activity.VoipSelectModeActivity;
import com.foreveross.atwork.modules.voip.manager.ZoomVoipManager;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.modules.workbench.fragment.WorkbenchFragment;
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager;
import com.foreveross.atwork.services.ImSocketService;
import com.foreveross.atwork.support.AtworkBaseActivity;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.RecyclerViewUtil;
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper;
import com.foreveross.theme.model.Theme;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tencent.bugly.crashreport.CrashReport;
import com.w6s.emoji.StickerManager;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import kotlin.collections.CollectionsKt;

/**
 * Created by lingen on 15/3/21.
 * Description:
 */
public class MainActivity extends AtworkBaseActivity {

    public static final String ACTION_TYPE = "ACTION_TYPE";
    public static final String ACTION_EXIT_ALL = "action_exit_all";
    public static final String ACTION_EXIT_ALL_KILL_PROCESS = "ACTION_EXIT_ALL_KILL_PROCESS";
    public static final String ACTION_CLEAR_DIALOG = "action_clear_dialog";
    public static final String ACTION_JUMP_FIRST_TAB = "action_jump_first_tab";
    public static final String ACTION_ON_CREATED = "ACTION_ON_CREATED";
    public static final String ACTION_TO_FRAGMENT = "action_to_fragment";
    public static final String ACTION_FINISH_MAIN = "action_finish_main";
    public static final String ACTION_SELECT_TAB = "action_select_tab";
    public static final String ACTION_RECREATE_MAIN_PAGE = "action_recreate_main_page";
    public static final String DATA_LOAD_OFFLINE_DATA_APP = "action_load_offline_data";
    public static final String DATA_TAB = "data_tab";

    /**
     * 申请 system_windows_overlay 权限
     */
    public final static int REQUEST_CODE_PERMISSION_WINDOWS_OVERLAY = 7777;

    //创建群聊code
    public static final int CREATE_DICUSSION_CHAT = 1;
    //创建语音会议 code
    public static final int CREATE_VOIP_MEETING = 2;
    //创建预约会畅会议 code
    public static final int CREATE_BIZCONF_MEETING_RESERVATION = 3;
    //创建即时会畅会议 code
    public static final int CREATE_ZOOM_MEETING_INSTANT = 4;

    public static final String TAG = MainActivity.class.getSimpleName();
    public static boolean mIsSyncingApp = false;

    private View mVFabMainBottomPop;
    private RecyclerView mRvFabBottomContent;
    private FloatingActionButton mFabMain;
    private RelativeLayout mRlFabSlideNotice;
    private ImageView mIvSlideFinger;
    private CustomViewPager mMainViewPaper;
    private RelativeLayout mRlUpdateNotice;
    private ImageView mIvUpdateCancel;
    private TextView mTvUpdateNotice;

    private List<Fragment> mFragmentList = new ArrayList<>();
    public List<ItemHomeTabView> mTabs = new ArrayList<>();
    public Map<String, ItemHomeTabView> mTabMap = new HashMap<>();

    private MainFabBottomPopAdapter mMainFabBottomPopAdapter;

    // 进度对话框
    private ProgressDialogHelper mProgressDialogHelper;
    public Fragment mSelectedFragment;
    private ShakeDetector mShakeDetector;
    private LinearLayout mHomeTabLayout;
    private boolean mCanShake = false; //避免多次打开 webview

    private int mCurrentIndex = -1;

    private View mRootView;
    private boolean mConsumedIntent = true;

    private boolean mOnCreated = false; //主界面是否已经创建过了, 当内存回收, 闪退其他情况出现时,该值认为 true

    private AtworkAlertDialog mEmailAuthDialog;

    private boolean mFinishSetOverallWatermark = false;

    private BroadcastReceiver mFunctionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (ACTION_FINISH_MAIN.equalsIgnoreCase(action)) {
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent(AboutAtWorkFragment.ACTION_MAIN_FINISH));
                    finish();
                    return;
                }
                if(ACTION_RECREATE_MAIN_PAGE.equals(action)) {
                    recreate();
                    return;
                }

                if(WorkplusUpdateManager.ACTION_REFRESH_FLOAT_UPDATE_TIP_VIEW.equals(action)) {

                    doRefreshFloatUpdateTipView();

                    return;
                }

                if(WorkbenchManager.ACTION_REFRESH_WORKBENCH.equalsIgnoreCase(action)) {
                    doRefreshWorkbenchTab();
                    return;
                }

                if(OfflineMessageReplayStrategyManager.ACTION_END_PULL_OFFLINE_MESSAGES.equalsIgnoreCase(intent.getAction())) {
                    //清除过期的 session
                    ChatService.calibrateExpiredSessions();

                    return;
                }

                if(ACTION_SELECT_TAB.equalsIgnoreCase(action)) {
                    String tabSelected = intent.getStringExtra(DATA_TAB);
                    int positionSelected = BeeWorks.getInstance().getTabIndexByTabId(tabSelected);

                    if (-1 != positionSelected) {
                        tabSelected(positionSelected, false);
                        fragmentSelected(positionSelected);
                    }

                }
            }
        }
    };


    private BroadcastReceiver mTabRefreshBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (TabNoticeManager.TAB_REFRESH_NOTICE.equals(intent.getAction())) {
                String tabId = intent.getStringExtra(TabNoticeManager.TAB_REFRESH_TAB_ID);

                refreshTabNoticeView(tabId);

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null != savedInstanceState) {
            mOnCreated = savedInstanceState.getBoolean(ACTION_ON_CREATED, false);
        }
        findView();
        initMainFabBottom();
        initFragmentAndTab();
        registerListener();
        initJumpTab();

        registerReceiver();

        if (isInLoginFlow()) {
            LogUtil.d("RRR", "syncData");
            syncData();


            getIntent().removeExtra(HandleLoginService.DATA_FROM_LOGIN);
//            mIsFromLogin = false;

        } else {
            startImService();
        }

        /**
         * 初始化 WebUrlFloatService
         * */
        WorkplusFloatService.Companion.init();

/*        if (!"meizu".equalsIgnoreCase(android.os.Build.BRAND)) {
            VPNInflaterManager.getInstance().initSXF(this);
        }*/
        clearHomeKeyStatus();

        OrgApplyManager.getInstance().loadOrg(this);
        //初次安装登录, 漫游7天内的必应消息
//        BingOfflineCompatibleSyncService.getInstance().checkRoamingSync();

        WorkbenchManager.INSTANCE.checkWorkbenchRemote(true, null);

        //更新文件下载白名单状态
        UserAsyncNetService.getInstance().getCustomizationInfo(this, null);

        ChatService.calibrateExpiredSessions();

        W6sBugFixCoreManager.getInstance().fixedForcedCheckAppRefresh();

        TingyunManager.INSTANCE.setUserId(LoginUserInfo.getInstance().getLoginUserRealUserName(getApplication()));

        String username = LoginUserInfo.getInstance().getLoginUserRealUserName(this);
        if(!StringUtils.isEmpty(username)) {
            TingyunManager.INSTANCE.setUserId(username);
            CrashReport.setUserId(username);
        }

        //检查更新自己的信息
        LoginHelper.refreshSelfUserFromRemote();
        LoginHelper.refreshSelfEmpsFromRemote();

        AppRefreshHelper.refreshAppAbsolutely(PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext), true);


//        tryRequestLocationPermission();

        mOnCreated = true;

        LogUtil.e("is umeeting zoom init -> " + UmeetingReflectService.isInitialized());
        LogUtil.e("is bizconf zoom init -> " + ZoomManager.INSTANCE.isInitialized(AtworkApplicationLike.baseContext));

        LogUtil.e("ActivityLifecycleListener", "Main oncreate  ~~~~~~~~~~~");
        DataPackageManager.checkWebCachePreDownload(AtworkApplicationLike.baseContext);
    }

    private void tryRequestLocationPermission() {
//        if(CustomerHelper.isRfchina(AtworkApplicationLike.baseContext)) {
//            return;
//        }

        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, new PermissionsResultAction() {

                    @Override
                    public void onGranted() {
                        W6sLocationManager.requestLocationData(AtworkApplicationLike.baseContext);
                    }

                    @Override
                    public void onDenied(String permission) {
                        AtworkUtil.popAuthSettingAlert(MainActivity.this, permission);
                    }
                });

            }

            @Override
            public void onDenied(String permission) {
                AtworkUtil.popAuthSettingAlert(MainActivity.this, permission);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        handleJumpRegisterUrl();
        AtworkApplicationLike.refreshSystemInstalledApps();
        startShakeListening();

        WorkplusUpdateManager.INSTANCE.refreshFloatUpdateTipView();
//        checkActionIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseApplicationLike.sMainActivity = this;
        StickerManager.Companion.getInstance().checkRemoteStickerAlbums(this, false);
        refreshAllTabNoticeView();

        handleLoadOfflineData();

        if (AtworkConfig.H3C_CONFIG) {
            handleH3CEvent();
        }
        checkBeeWorksNewVersion();
        if (RomUtil.isXiaomi()) {
            MiPushClient.clearNotification(BaseApplicationLike.sApp);
        }

        ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();

        Logger.cleanLogs();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopShakeListening();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterReceivers();
    }

    @Override
    public void networkChanged(NetworkBroadcastReceiver.NetWorkType networkType) {
        super.networkChanged(networkType);

        for(Fragment fragment: mFragmentList) {
            if(fragment instanceof NetworkBroadcastReceiver.NetworkChangedListener) {
                NetworkBroadcastReceiver.NetworkChangedListener networkChangedListener = (NetworkBroadcastReceiver.NetworkChangedListener) fragment;
                networkChangedListener.networkChanged(networkType);
            }
        }
    }

    public boolean isFinishSetOverallWatermark() {
        return mFinishSetOverallWatermark;
    }

    public void setOverallWatermarkHandleStatus(boolean finishSetOverallWatermark) {
        this.mFinishSetOverallWatermark = finishSetOverallWatermark;
    }

    private boolean shouldPopGestureCodeLock(boolean isFromK9) {
        if(isFromK9) {
            return false;
        }

        if(!AtworkUtil.hasBasePermissions(this)) {
            return false;
        }

        if(false == PersonalShareInfo.getInstance().getLockCodeSetting(this)) {
            return false;
        }

        if(mOnCreated) {
           return false;
        }

        return true;

    }

    private void initMainFabBottom() {
        mMainFabBottomPopAdapter = new MainFabBottomPopAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mRvFabBottomContent.setAdapter(mMainFabBottomPopAdapter);
        mRvFabBottomContent.setLayoutManager(linearLayoutManager);
        mRvFabBottomContent.setAdapter(mMainFabBottomPopAdapter);

    }

    public FloatingActionButton getFab() {
        return mFabMain;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onOrgSettingChange() {
        //更新皮肤
        SkinManger.getInstance().load(PersonalShareInfo.getInstance().getCurrentOrg(this), null);
    }

    private void checkBeeWorksNewVersion() {
        //beeworks 开发者模式下或者url为空，不检查beeworks更新
        try {
            if (SettingInfo.getInstance().getUserSettings(this).mIsDevMode || TextUtils.isEmpty(BeeWorks.getInstance().config.beeWorksUrl)) {
                return;
            }
            BeeWorksNetService.getInstance().checkBeeWorksNewVersion(new BeeWorksNetService.BeeWorksPreviewListener() {
                @Override
                public void success(String tabDatas) {
                    if (TextUtils.isEmpty(tabDatas)) {
                        return;
                    }
                    SettingInfo.getInstance().setDevMode(MainActivity.this, false);
                    BeeWorksPreviewData.getInstance().clearBeeWorksPreviewData(MainActivity.this);
                    //保存新发布版本模式
                    BeeWorksPublicData.getInstance().setBeeWorksPublicData(MainActivity.this, tabDatas);
                    BeeWorks.getInstance().initBeeWorks(tabDatas);
                    //跳转webiview页面，更新
                    String url = "file:///android_asset/www/loading.html";

                    WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                                                                                    .setUrl(url)
                                                                                    .setTitle("beeworks更新")
                                                                                    .setNeedShare(false);

                    Intent intent = WebViewActivity.getIntent(MainActivity.this, webViewControlAction);
                    intent.putExtra("BeeWorks_Update", true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }

                @Override
                public void fail() {

                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void registerReceiver() {
        registerNetworkReceiver(this);
        registerFunctionReceiver();
        registerTabNoticeReceiver();
    }

    public void clearHomeKeyStatus() {
        CommonShareInfo.setHomeKeyStatusForCommon(this, false);
        CommonShareInfo.setHomeKeyStatusForDomainSetting(this, false);
    }

    public void handleLoadOfflineData() {
        AppBundles lightApp = getIntent().getParcelableExtra(DATA_LOAD_OFFLINE_DATA_APP);
        if (null != lightApp) {
            getIntent().removeExtra(DATA_LOAD_OFFLINE_DATA_APP);
            Session session = ChatSessionDataWrap.getInstance().getSession(lightApp.getId(), null);
            loadOfflineData(session, lightApp);
        }
    }

    public void loadOfflineData(final Session session, @NonNull final AppBundles appBundle) {
        ProgressDialogHelper dialogHelper = new ProgressDialogHelper(this);

        DataPackageManager.loadData(this, appBundle, new DataPackageManager.OnLoadDataListener() {
            @Override
            public void onStart() {
                dialogHelper.show(false);
            }

            @Override
            public void onSuccess() {
                dialogHelper.dismiss();

                WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                                                                                .setUrl(session.entryValue)
                                                                                .setTitle(session.name)
                                                                                .setSessionId(session.identifier)
                                                                                .setLightApp(appBundle);
                startActivity(WebViewActivity.getIntent(MainActivity.this, webViewControlAction));

            }

            @Override
            public void onError() {
                dialogHelper.dismiss();

                new AtworkAlertDialog(MainActivity.this, AtworkAlertDialog.Type.SIMPLE)
                        .setContent(R.string.offline_failed)
                        .setBrightBtnText(R.string.retry)
                        .setClickBrightColorListener(dialog -> {
                            loadOfflineData(session, appBundle);
                        })
                        .show();
            }

            @Override
            public void downloadProgress(double progress, double size) {

            }
        });
    }

    public void handleJumpRegisterUrl() {

        String nextUrl = getIntent().getStringExtra(WebViewPlugin.INTENT_KEY_NEXT_URL);

        if(!StringUtils.isEmpty(nextUrl)) {
            Uri uri = Uri.parse(nextUrl);

           RouteActionConsumer.INSTANCE.route(MainActivity.this, RouteParams.Companion.newRouteParams().uri(uri).build());
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            getIntent().removeExtra(WebViewPlugin.INTENT_KEY_NEXT_URL);
        }


        if (!TextUtils.isEmpty(nextUrl)) {
            StringBuilder builder = new StringBuilder();
            builder.append("file:///android_asset/www/register/index.html?/").append(nextUrl);

            WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(builder.toString()).setNeedShare(false);
            Intent intent = WebViewActivity.getIntent(this, webViewControlAction);
            startActivityForResult(intent, 3001);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);


            getIntent().removeExtra(WebViewPlugin.INTENT_KEY_NEXT_URL);
        }
    }

    public static Intent getMainActivityIntent(Context context, boolean backToFirstTab) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.putExtra(ACTION_CLEAR_DIALOG, true);
        intent.putExtra(ACTION_JUMP_FIRST_TAB, backToFirstTab);
        return intent;
    }

    public static Intent getExitAllIntent(Context context, boolean kill) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ACTION_EXIT_ALL, true);
        intent.putExtra(ACTION_EXIT_ALL_KILL_PROCESS, kill);
        return intent;
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(ACTION_ON_CREATED, true);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (CREATE_DICUSSION_CHAT == requestCode && RESULT_OK == resultCode) {
            mProgressDialogHelper = new ProgressDialogHelper(this);
            mProgressDialogHelper.show(getResources().getString(R.string.create_group_ing));
            List<ShowListItem> contactList = SelectedContactList.getContactList();
            if (contactList.size() == 1) {
                ShowListItem contact = contactList.get(0);

                EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(SessionType.User, contact);

                ChatSessionDataWrap.getInstance().entrySession(entrySessionRequest);
                startActivity(ChatDetailActivity.getIntent(this, contact.getId()));
                mProgressDialogHelper.dismiss();
                return;
            }
            DiscussionManager.getInstance().createDiscussion(this, contactList, null, null,null, null, true, new DiscussionAsyncNetService.OnCreateDiscussionListener() {
                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    mProgressDialogHelper.dismiss();

                    ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Group, errorCode, errorMsg);

                }

                @Override
                public void onDiscussionSuccess(Discussion discussion) {
                    mProgressDialogHelper.dismiss();
                    if (null != discussion) {
                        Intent intent = ChatDetailActivity.getIntent(MainActivity.this, discussion.mDiscussionId);
                        startActivity(intent);
                    }
                }


            });

            return;
        }

        if (CREATE_VOIP_MEETING == requestCode && RESULT_OK == resultCode) {
            AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
                @Override
                public void onSuccess(@NonNull User user) {
                    List<ShowListItem> contacts = SelectedContactList.getContactList();
                    contacts.add(0, user);
                    Intent intent = VoipSelectModeActivity.getIntent(MainActivity.this, (ArrayList<? extends ShowListItem>) contacts);
                    startActivity(intent);
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                }
            });


            return;
        }


        if(CREATE_BIZCONF_MEETING_RESERVATION == requestCode && RESULT_OK == resultCode) {
            List<ShowListItem> contacts = SelectedContactList.getContactList();
            ContactPlugin_New.setContactSelectedCache(contacts);

            contacts.clear();

            WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                    .setUrl(UrlHandleHelper.addParam(AtworkConfig.ZOOM_CONFIG.getUrl(), "w6sContactCache", "1"))
                    .setNeedShare(false)
                    .setHideTitle(false);

            UrlRouteHelper.routeUrl(MainActivity.this, webViewControlAction);
//            startActivity(WebViewActivity.getIntent(MainActivity.this, webViewControlAction));

            return;
        }



        if(CREATE_ZOOM_MEETING_INSTANT == requestCode && RESULT_OK == resultCode) {

            createZoomMeetingInstant();

            return;
        }



        if (RESULT_OK == resultCode && data != null) {
            String action = data.getStringExtra(ACTION_TO_FRAGMENT);
            if ("orgFragment".equalsIgnoreCase(action)) {
                fragmentSelected(1);
                tabSelected(1, false);
            }
        }
    }

    private void createZoomMeetingInstant() {


        AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void onSuccess(@NonNull User loginUser) {


                doCreateZoomMeetingInstant(loginUser);

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
            }
        });
    }

    private void doCreateZoomMeetingInstant(@NonNull User loginUser) {
        List<ShowListItem> contactsSelected = new ArrayList<>(SelectedContactList.getContactList());



        if(1 == contactsSelected.size()) {
            doCreateP2pZoomMeetingInstant(loginUser, contactsSelected);

            return;
        }


        doCreateP2NZoomMeetingInstant(loginUser, contactsSelected);
    }

    private void doCreateP2NZoomMeetingInstant(@NonNull User loginUser, List<ShowListItem> contactsSelected) {
        contactsSelected.add(loginUser);
        SelectedContactList.clear();


        MeetingInfo meetingInfo = new MeetingInfo();
        meetingInfo.mType = MeetingInfo.Type.MULTI;
        meetingInfo.mOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext);

        ZoomVoipManager.INSTANCE.setCurrentVoipMeeting(AtworkApplicationLike.baseContext, true, true, contactsSelected, null, meetingInfo, VoipType.VIDEO, null, null);

        CurrentVoipMeeting currentVoipMeeting = ZoomVoipManager.INSTANCE.getCurrentVoipMeeting();
        if(null == currentVoipMeeting) {
            return;
        }

        ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(MainActivity.this);
        progressDialogHelper.show(false);

        ZoomVoipManager.INSTANCE.changeCallState(CallState.CallState_StartCall);

        //创建会畅会议, 加入, 并调用sdk
        ZoomVoipManager.INSTANCE.createMeeting(AtworkApplicationLike.baseContext, meetingInfo, VoipType.VIDEO, currentVoipMeeting.mCallParams.getCallMemberList(), new VoipManager.OnCreateAndQueryVoipMeetingListener() {
            @Override
            public void onSuccess(CreateOrQueryMeetingResponseJson responseJson) {
                String workplusVoipMeetingId = responseJson.mResult.mMeetingId;

                ZoomVoipManager.INSTANCE.joinMeeting(AtworkApplicationLike.baseContext, meetingInfo, workplusVoipMeetingId, VoipType.VIDEO, new VoipManager.OnGetJoinTokenListener() {

                    @Override
                    public void onSuccess(String token) {

                        ZoomVoipManager.INSTANCE.changeCallState(CallState.CallState_Calling);


                        progressDialogHelper.dismiss();
                        LogUtil.e("bizconf token -> " + token);

                        HandleMeetingInfo handleMeetingInfo = new HandleMeetingInfo();
                        handleMeetingInfo.setMeetingUrl(token);
                        ZoomManager.INSTANCE.startMeeting(MainActivity.this, handleMeetingInfo);
                    }


                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        progressDialogHelper.dismiss();

                        ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);
                        ZoomVoipManager.INSTANCE.stopCall();
                    }


                });


            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                progressDialogHelper.dismiss();

                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);
                ZoomVoipManager.INSTANCE.stopCall();

            }
        });
    }

    private void doCreateP2pZoomMeetingInstant(@NonNull User loginUser, List<ShowListItem> contactsSelected) {
        MeetingInfo meetingInfo = new MeetingInfo();
        meetingInfo.mType = MeetingInfo.Type.USER;
        meetingInfo.mId = contactsSelected.get(0).getId();
        meetingInfo.mOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext);

        contactsSelected.add(loginUser);
        SelectedContactList.clear();

        ZoomVoipManager.INSTANCE.goToCallActivity(MainActivity.this, null, meetingInfo, VoipType.VIDEO, false, true, contactsSelected, null, null);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long current = TimeUtil.getCurrentTimeInMillis();

            //非华三的包不用该特性
            if (!CustomerHelper.isH3c(this) || current - AtworkConfig.lastBackTime <= 2000) {
                moveTaskToBack(true);

                CommonShareInfo.setHomeKeyStatusForCommon(this, true);

                AtworkApplicationLike.appInvisibleHandleFloatView();


            } else {
                AtworkToast.showToast(getResources().getString(R.string.exit_app_tip));
            }

            AtworkConfig.lastBackTime = TimeUtil.getCurrentTimeInMillis();

            return false;
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (null != intent) {

            if (intent.getBooleanExtra(ACTION_JUMP_FIRST_TAB, false)) {
                fragmentSelected(0);
                tabSelected(0, false);
            }

            if (intent.getBooleanExtra(ACTION_CLEAR_DIALOG, false)) {
                clearDialogFragment();

            } else if (intent.getBooleanExtra(ACTION_EXIT_ALL, false)) {
                clearDialogFragment();
                finish();

                if(intent.getBooleanExtra(ACTION_EXIT_ALL_KILL_PROCESS, false)) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }
            }

        }

    }


    private void registerTabNoticeReceiver() {
        IntentFilter filter = new IntentFilter(TabNoticeManager.TAB_REFRESH_NOTICE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mTabRefreshBroadcastReceiver, filter);

    }

    private void unregisterTabNoticeReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTabRefreshBroadcastReceiver);

    }

    private void registerFunctionReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_FINISH_MAIN);
        intentFilter.addAction(ACTION_RECREATE_MAIN_PAGE);
        intentFilter.addAction(WorkplusUpdateManager.ACTION_REFRESH_FLOAT_UPDATE_TIP_VIEW);
        intentFilter.addAction(WorkbenchManager.ACTION_REFRESH_WORKBENCH);
        intentFilter.addAction(OfflineMessageReplayStrategyManager.ACTION_END_PULL_OFFLINE_MESSAGES);
        intentFilter.addAction(ACTION_SELECT_TAB);
        LocalBroadcastManager.getInstance(this).registerReceiver(mFunctionReceiver, intentFilter);
    }

    private void unRegisterFinishMainReceiver() {
        if (mFunctionReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mFunctionReceiver);
        }
    }


    private void unRegisterReceivers() {
        unRegisterNetworkReceiver();
        unRegisterFinishMainReceiver();
        unregisterTabNoticeReceiver();
    }



    private void syncData() {

        ConfigSettingsManager.INSTANCE.syncConfigSettings();

        syncBasicData();
    }

    /**
     * 同步联系人，群组以及APP等
     */
    private void syncBasicData() {
        SyncManager.SYNCING = true;
        mIsSyncingApp = true;
        ReceivingTitleQueueManager.getInstance().push(this, ReceivingTitleQueueManager.TAG_SYNC);

        LoginService loginNetService = new LoginService(BaseApplicationLike.baseContext);

        loginNetService.sync(new LoginService.SyncListener() {

            @Override
            public void syncSuccess(List<User> userFriendList, List<User> userFlagList, List<Discussion> discussionList, List<Organization> organizationList, List<App> appList) {
                //同步完成后，全量更新一下组织设置列表
                OrganizationSettingsHelper.getInstance().checkOrgSettingsUpdate(MainActivity.this, -1);
                if (null != userFriendList) {
                    syncBatchInsertRelations(userFriendList, true);
                }
                if (null != userFlagList) {
                    syncBatchInsertRelations(userFlagList, false);
                }
                if (null != discussionList) {
                    batchInsertDiscussions(discussionList);
                }
                if (null != organizationList) {
                    batchInsertOrganizations(organizationList);
                    batchInsertOrgRelations(organizationList);
                }
                if (null != appList) {
                    batchInsertAppsCheckDb(appList);
                }
                ReceivingTitleQueueManager.getInstance().pull(MainActivity.this, ReceivingTitleQueueManager.TAG_SYNC);
                SyncManager.SYNCING = false;
            }

            @Override
            public void syncFail(int errorCode, String errorMsg) {
                if (BuildConfig.DEBUG) {
                    AtworkToast.showToast(getResources().getString(R.string.sync_contact_fail) + ":" + errorCode);
                }

                ReceivingTitleQueueManager.getInstance().pull(MainActivity.this, ReceivingTitleQueueManager.TAG_SYNC);
                mIsSyncingApp = false;
                SyncManager.SYNCING = false;
            }
        });
    }

    /**
     * 插入应用
     *
     * @param appList
     */
    private void batchInsertAppsCheckDb(List<App> appList) {
        AppDaoService.getInstance().batchInsertAppsCheckDb(appList, new AppDaoService.BatchInsertAppListener() {
            @Override
            public void batchInsertSuccess() {

                HashSet<String> batchInsertSuccessOrgs = new HashSet<>(CollectionsKt.map(appList, app -> app.mOrgId));

                for(String orgCode: batchInsertSuccessOrgs) {
                    AppManager.getInstance().updateAppSyncStatusSuccess(orgCode);
                }


                mIsSyncingApp = false;
            }

            @Override
            public void batchInsertFail() {
                AtworkToast.showToast(MainActivity.this.getString(R.string.sync_app_db_fail));
                mIsSyncingApp = false;
            }
        });

    }

    /**
     * 插入讨论组
     *
     * @param discussionList
     */
    private void batchInsertDiscussions(List<Discussion> discussionList) {
        DiscussionManager.getInstance().syncInsertDiscussions(this, discussionList);
    }

    /**
     * 插入组织架构信息
     *
     * @param organizationList
     */
    private void batchInsertOrganizations(List<Organization> organizationList) {
        OrganizationManager.getInstance().batchInsertOrganizationsToLocal(organizationList);
    }

    /**
     * 插入组织架构信息
     *
     * @param organizationList
     */
    private void batchInsertOrgRelations(List<Organization> organizationList) {
        if (!ListUtil.isEmpty(organizationList)) {
            String meUserId = LoginUserInfo.getInstance().getLoginUserId(this);

            List<OrgRelationship> orgRelationshipList = OrgRelationship.produceOrgRelationshipListByOrgList(meUserId, OrgRelationship.Type.EMPLOYEE, organizationList);
            OrganizationDaoService.getInstance().batchInsertRelationAndClean(meUserId, OrgRelationship.Type.EMPLOYEE, orgRelationshipList);
            OrganizationManager.getInstance().updateAndCleanOrgCodeListCache(OrgRelationship.getOrgCodeList(orgRelationshipList));

        }
    }

    /**
     * 插入关系人员，包括星标和好友
     *
     * @param userList
     * @param isFriend true表示为好友关系，false表示为星标关系
     */
    private void syncBatchInsertRelations(List<User> userList, boolean isFriend) {
        UserManager.getInstance().asyncAddUserRelationShipToLocal(MainActivity.this, userList, isFriend);
    }


    private void findView() {
        mRootView = getLayoutInflater().inflate(R.layout.activity_main, null);
        mMainViewPaper = mRootView.findViewById(R.id.main_viewpager);
        mHomeTabLayout = mRootView.findViewById(R.id.home_tab_layout);
        mFabMain = mRootView.findViewById(R.id.fab_main);
        mRlFabSlideNotice = mRootView.findViewById(R.id.rl_fab_slide_notice);
        mIvSlideFinger = mRootView.findViewById(R.id.iv_slide_finger);
        mVFabMainBottomPop = mRootView.findViewById(R.id.v_fab_main_bottom_pop);
        mRvFabBottomContent = mRootView.findViewById(R.id.rl_fab_content);
        mRlUpdateNotice = mRootView.findViewById(R.id.rl_update_notice);
        mTvUpdateNotice = mRootView.findViewById(R.id.tv_update_notice);
        mIvUpdateCancel = mRootView.findViewById(R.id.iv_update_cancel);

        WorkplusTextSizeChangeHelper.handleHeightEnlargedTextSizeStatus(mHomeTabLayout, 1.2f, 3);

        setContentView(mRootView);
    }


    private void initFragmentAndTab() {

        if (mTabs != null && mTabs.size() == 0) {


            initAssembleWorkbenchToBeeworksTab();

            TabHelper.clearTabIdMap();
            for (BeeWorksTab beeWorksTab : BeeWorks.getInstance().tabs) {
                initFragmentAndTab(beeWorksTab);
            }

            doRefreshWorkbenchTab();

            refreshItemHomeTabViewIndex();
        }
    }

    private boolean initAssembleWorkbenchToBeeworksTab() {
        Workbench workbench = WorkbenchManager.INSTANCE.getCurrentOrgWorkbenchWithoutContent();
        if(null != workbench && workbench.isLegal()) {

            if(!BeeWorks.getInstance().containsTab(WorkbenchFragment.TAB_ID)) {
                BeeWorksTab beeWorksTab = getWorkbenchBeeWorksTab(workbench);
                int insertPosition = BeeWorks.getInstance().tabs.size() / 2;
                if (CustomerHelper.moveWorkBenchToNext(MainActivity.this)) {
                    insertPosition += 1;
                }

                BeeWorks.getInstance().tabs.add(insertPosition, beeWorksTab);

                return true;
            }

        }

        return false;
    }

    private void refreshOffscreenPageLimit() {
        //增加页面缓存数量, 避免不必要的页面重现绘制
        mMainViewPaper.setOffscreenPageLimit(mTabs.size() - 1);
    }

    private void refreshItemHomeTabViewIndex() {
        for (int i = 0; i < mTabs.size(); i++) {
            ItemHomeTabView itemHomeTab = mTabs.get(i);
            itemHomeTab.setIndex(i);
        }
    }


    private void initFragmentAndTab(BeeWorksTab beeWorksTab) {
        initFragmentAndTab(beeWorksTab, -1);
    }

    private void initFragmentAndTab(BeeWorksTab beeWorksTab, int insertPosition) {
        Fragment fragment = TabHelper.getTabFragment(this, beeWorksTab);


        if (-1 < insertPosition) {
            mFragmentList.add(insertPosition, fragment);
        } else {
            mFragmentList.add(fragment);

        }


        ItemHomeTabView tab = mTabMap.get(beeWorksTab.id);
        if (-1 < insertPosition) {
            mTabs.add(insertPosition, tab);
        } else {
            mTabs.add(tab);

        }

        tab.refreshSelected();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        mHomeTabLayout.addView(tab, insertPosition, layoutParams);
    }


    public void search(SearchContent... searchContents) {

        NewSearchControlAction newSearchControlAction = new NewSearchControlAction();
        newSearchControlAction.setSearchContentList(searchContents);

        Intent intent = NewSearchActivity.getIntent(MainActivity.this, newSearchControlAction);
        startActivity(intent);
    }


    public static void recreateMainPage() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ACTION_RECREATE_MAIN_PAGE));
    }


    private void registerListener() {
        if (mMainViewPaper.getAdapter() == null) {
            mMainViewPaper.setAdapter(new MainTabAdapter(getSupportFragmentManager(), mFragmentList));
            refreshOffscreenPageLimit();
            //设置页面是否能够滑动
            mMainViewPaper.setCanScroll(AtworkConfig.MAIN_VIEW_PAGER_CAN_SCROLL);
        }
        //监控滑动Fragment事件
        //切换fragment时，进行TAB选择
        mMainViewPaper.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabSelected(position, true);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if (positionOffset == 0) {
//                    tabSelected(position);
//                }
            }
        });

        //监控切换TAB事件
        registerTabsListener();

        mFabMain.setOnClickListener(v -> {
            mFabMain.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onHidden(FloatingActionButton fab) {
                    super.onHidden(fab);
                    String currentTabId = mTabs.get(mCurrentIndex).getTabId();
                    MainFabBottomPopupHelper.createMainFabBottomPopUp(currentTabId, mMainFabBottomPopAdapter);
                    mRvFabBottomContent.scrollToPosition(0);
                    mVFabMainBottomPop.setVisibility(View.VISIBLE);

                    checkFabSlideFingerShow();


                }
            });
        });


        mVFabMainBottomPop.setOnClickListener(v -> {
            if(mRlFabSlideNotice.isShown()) {
                mRlFabSlideNotice.setVisibility(View.GONE);
                mIvSlideFinger.clearAnimation();
                return;
            }

            mVFabMainBottomPop.setVisibility(View.GONE);
            mFabMain.show();
        });


        mMainFabBottomPopAdapter.setOnClickItemListener(action -> {

            MainFabBottomPopupHelper.handleItemClick(MainActivity.this, action);
            mVFabMainBottomPop.setVisibility(View.GONE);
            mFabMain.show();
        });

        mRlFabSlideNotice.setOnClickListener((v) -> {
            if(mRlFabSlideNotice.isShown()) {
                mRlFabSlideNotice.setVisibility(View.GONE);
                mIvSlideFinger.clearAnimation();
            }

        });

        mRvFabBottomContent.setOnTouchListener((v, event) -> {
            if(MotionEvent.ACTION_DOWN == event.getAction()
                || MotionEvent.ACTION_MOVE == event.getAction()) {
                if(mRlFabSlideNotice.isShown()) {
                    mRlFabSlideNotice.setVisibility(View.GONE);
                    mIvSlideFinger.clearAnimation();
                }
            }
            return false;
        });


        mIvUpdateCancel.setOnClickListener(v -> {
            WorkplusUpdateManager.INSTANCE.setTipFloatStatusAndRefresh(false);
        });


        mRlUpdateNotice.setOnClickListener(v -> {
            Intent intent = AppUpgradeActivity.getIntent(MainActivity.this, CommonShareInfo.getNewVersionCode(MainActivity.this));
            intent.putExtra(AppUpgradeActivity.INTENT_FORCE_UPDATED, CommonShareInfo.isForcedUpdatedState(MainActivity.this));
            startActivity(intent);
        });

    }

    private void registerTabsListener() {
        for (final ItemHomeTabView tab : mTabs) {
            tab.setOnClickListener(v -> {
                handleItemClick(tab);
            });
        }
    }


    private void handleItemClick(ItemHomeTabView tab) {
        if(CommonUtil.isDoubleClick(tab.getTabId())) {

            if("im".equalsIgnoreCase(tab.getTabId())) {

                for(Fragment fragment : mFragmentList) {
                    if(fragment instanceof ChatListFragment) {
                        ((ChatListFragment)fragment).scrollToUnreadUnSaw();
                        break;
                    }
                }


            } else {
                handleItemSelected(tab);

            }

            return;
        }

        handleItemSelected(tab);
    }

    private void handleItemSelected(ItemHomeTabView tab) {
        int i = tab.getIndex();

        if (mCurrentIndex != i) {
            fragmentSelected(i);
            tabSelected(i, true);
        }
    }

    private void checkFabSlideFingerShow() {
        int shownCount = PersonalShareInfo.getInstance().getMainFabSlideFingerShownCount(MainActivity.this);
        if(shownCount >= PersonalShareInfo.DEFAULT_SHOULD_MAIN_FAB_BOTTOM_SLIDE_NOTICE_FINGER_SHOWN_COUNT) {
            return;
        }

        mRvFabBottomContent.postDelayed(()->{
            if(RecyclerViewUtil.isRecyclerScrollable(mRvFabBottomContent)) {
                mRlFabSlideNotice.setVisibility(View.VISIBLE);
                MainFabBottomPopupHelper.doFingerAnimation(mIvSlideFinger);
                int updatedShownCount = shownCount + 1;

                PersonalShareInfo.getInstance().putMainFabSlideFingerShownCount(MainActivity.this, updatedShownCount);

            }

        }, 200);
    }


    /**
     * 点击"更多"里的发起会议的事件操作
     */
    private void handleMorePopClickStartVoip() {
        boolean hasVoipHistory = true;

        if (hasVoipHistory) {
            Intent intent = VoipHistoryActivity.getIntent(this);
            startActivity(intent);

        } else {
            if (AtworkUtil.isSystemCalling()) {
                AtworkToast.showResToast(R.string.alert_is_handling_system_call);
                return;
            }

            if (VoipHelper.isHandlingVoipCall()) {
                AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
            } else {
                List<ShowListItem> notAllowContactList = new ArrayList<>();
                AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
                    @Override
                    public void onSuccess(@NonNull User user) {
                        notAllowContactList.add(user);
                        UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
                        userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
                        userSelectControlAction.setSelectAction(UserSelectActivity.SelectAction.VOIP);
                        userSelectControlAction.setSelectedContacts(notAllowContactList);
                        userSelectControlAction.setFromTag(TAG);

                        Intent intent = UserSelectActivity.getIntent(MainActivity.this, userSelectControlAction);

                        startActivityForResult(intent, MainActivity.CREATE_VOIP_MEETING);
                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                    }
                });

            }
        }
    }


    private void initJumpTab() {

        int initJumpTab = AtworkConfig.INIT_JUMP_TAB;

        if (AtworkConfig.WORKBENCH_CONFIG.isNeedInitJump()) {
            int workbenchPosition = BeeWorks.getInstance().getTabIndexById(WorkbenchFragment.TAB_ID);

            if(-1 != workbenchPosition) {
                initJumpTab = workbenchPosition;

            }
        }

        if (0 != initJumpTab) {
            fragmentSelected(initJumpTab);
        }
        tabSelected(initJumpTab, false);
    }

    /**
     * TAB选择
     *
     * @param selectedIndex   当前选中的项
     * @param userHandle 是否是用户手动操作
     */
    private void  tabSelected(int selectedIndex, boolean userHandle) {
        if(mCurrentIndex == selectedIndex) {
            return;
        }

        mCurrentIndex = selectedIndex;

        for (int i = 0; i < mTabs.size(); i++) {
            ItemHomeTabView itemHomeTab = mTabs.get(i);
            if (i == selectedIndex) {
                ActivityStack.INSTANCE.updateActivityInfo(new ActivityInfo(MainActivity.this, itemHomeTab.getTabId()));

                itemHomeTab.setSelected(true);
                if("im".equalsIgnoreCase(itemHomeTab.getTabId()) || "contact".equalsIgnoreCase(itemHomeTab.getTabId())) {
//                    MainFabBottomPopupHelper.revertTranslation(mFabMain, () -> {
//                    });

                    mFabMain.show();


                } else {
                    mFabMain.hide();
                }



                if (userHandle) {
                    WorkplusUpdateManager.INSTANCE.refreshFloatUpdateTipView();

                    SyncManager.getInstance().checkSyncStatus(this);

                    ApkVerifyManager.INSTANCE.checkLegal(this);
                }
            } else {
                itemHomeTab.setSelected(false);
            }
        }

        mSelectedFragment = mFragmentList.get(selectedIndex);

    }

    private String getCurrentHomeTabId() {
        ItemHomeTabView currentHomeTabView = getCurrentHomeTabView();
        if(null != currentHomeTabView) {
            return currentHomeTabView.getTabId();
        }

        return StringUtils.EMPTY;
    }

    @Nullable
    private ItemHomeTabView getCurrentHomeTabView() {
        return mTabs.get(mCurrentIndex);
    }


    /**
     * 启动SOCKET连接服务
     */
    private void startImService() {

        ServiceCompat.startServiceCompat(this, ImSocketService.class);

        ImSocketService.checkConnection(this);
    }


    /**
     * 选中某一个Fragment
     *
     * @param selected 当前选中的项
     */
    private void fragmentSelected(int selected) {
        mMainViewPaper.setCurrentItem(selected, false);
    }

    public void handleH3CEvent() {
        boolean result = IESInflaterManager.getInstance().initIES(this);
        int requestLoginResult = IESInflaterManager.getInstance().requestInodeLogin(this);
        if (requestLoginResult != 0) {
            return;
        }
        IES ies = IESInflaterManager.getInstance().getIESAccount(this);
        if (ies == null) {
            return;
        }
        boolean isVpnLogin = IESInflaterManager.getInstance().checkVpnOnline(this);
        //说明华三Inode帐号不存在
        if (!isVpnLogin) {
            logoutByInode();
            return;
        }

        String username = LoginUserInfo.getInstance().getLoginUserUserName(this);
        //如果当前登陆帐号和Inode帐号不一致
        if (ies.iesAccountName == null) {
            return;
        }
        if (!username.equalsIgnoreCase(ies.iesAccountName)) {
            logoutByInode();
        }
    }

    private void logoutByInode() {
        AtworkApplicationLike.clearData();
        ImSocketService.closeConnection();
        BeeWorks beeWorks = BeeWorks.getInstance();
        if (beeWorks.isBeeWorksFaceBioSettingEnable() && beeWorks.config.beeWorksSetting.getFaceBioSetting().getFaceBioAuth()) {
             startActivity(AccountLoginActivity.getLoginIntent(this));
             finish();
             return;
        }
        Intent intent = LoginActivity.getLoginIntent(this, false);
        startActivity(intent);
        finish();
    }

    private void startShakeListening() {

        mCanShake = true;


        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (null == mShakeDetector) {
            mShakeDetector = new ShakeDetector(() -> {
                try {
                    if (mCanShake) {
                        String shareOrgShareUrl = OrganizationSettingsManager.getInstance().getShareOrgShareUrl(PersonalShareInfo.getInstance().getCurrentOrg(this));
                        if(StringUtils.isEmpty(shareOrgShareUrl)) {
                            return;
                        }

                        mCanShake = false;

//                        boolean hideTitle = !CustomerHelper.isCimc(MainActivity.this);

                        WebViewControlAction webViewControlAction = WebViewControlAction
                                .newAction()
                                .setUrl(shareOrgShareUrl)
                                .setFrom(WebViewControlAction.FROM_SHAKE);

                        startActivity(WebViewActivity.getIntent(MainActivity.this, webViewControlAction));

                        MainActivity.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }

        mShakeDetector.start(sensorManager);
    }

    public void stopShakeListening() {
        if (null != mShakeDetector) {
            mShakeDetector.stop();
        }
    }


    private void clearDialogFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("SearchDialog");
        if (null != fragment && fragment instanceof SearchFragment) {
            ((SearchFragment) fragment).dismissAllowingStateLoss();
        }
    }


    @Override
    public void onThemeUpdate(Theme theme) {
        super.onThemeUpdate(theme);

        for (ItemHomeTabView tab : mTabs) {

            if (tab.isSelected()) {
                tab.updateSelectedTitleImageUI();
            } else {
                tab.updateTitleImageUI();
            }

        }
    }

    @Override
    public void changeStatusBar() {
        WorkplusStatusBarHelper.setCommonFullScreenStatusBar(this, true);

    }

    @Override
    public boolean shouldInterceptToAgreement() {
        if (PersonalShareInfo.getInstance().needCheckSignedAgreement(this)) {
            return true;
        }

        return isInLoginFlow();
    }

    @Override
    public void onHome() {
        super.onHome();

        CommonShareInfo.setHomeKeyStatusForCommon(BaseApplicationLike.baseContext, true);

        AtworkApplicationLike.appInvisibleHandleFloatView();


    }

    @Override
    public void onBackFromHome() {
        super.onBackFromHome();

        ChatService.calibrateUserSessions(true);

        //清除过期的 session
        ChatService.calibrateExpiredSessions();
    }

    @Override
    public void onEnterApp() {

        //检查 im 服务
        ServiceCompat.startServiceCompat(this, ImSocketService.class);

        //更新文件下载白名单状态
        UserAsyncNetService.getInstance().getCustomizationInfo(this, null);


        WorkbenchManager.INSTANCE.checkWorkbenchRemote(false,null);

        W6sBugFixCoreManager.getInstance().fixedForcedCheckAppRefresh();

        W6sLocationManager.requestLocationData(AtworkApplicationLike.baseContext);

        DataPackageManager.checkWebCachePreDownload(AtworkApplicationLike.baseContext);
    }

    @Override
    public void onLeaveApp() {
        AtworkApplicationLike.appInvisibleHandle();
    }



    private void doRefreshWorkbenchTab() {
//        if(true) {
//            return;
//        }

        Workbench workbench = WorkbenchManager.INSTANCE.getCurrentOrgWorkbenchWithoutContent();
        if(null != workbench && workbench.isLegal()) {

            if (BeeWorks.getInstance().containsTab(WorkbenchFragment.TAB_ID)) {
                //do updating
                ItemHomeTabView itemHomeTabView = getItemHomeTab(WorkbenchFragment.TAB_ID);
                if(null != itemHomeTabView) {
                    itemHomeTabView.setTitle(workbench.getNameI18n(AtworkApplicationLike.baseContext));
                }
            } else {
                BeeWorksTab beeWorksTab = getWorkbenchBeeWorksTab(workbench);

                int insertPosition = BeeWorks.getInstance().tabs.size() / 2;
                if (CustomerHelper.moveWorkBenchToNext(MainActivity.this)) {
                    insertPosition += 1;
                }
                BeeWorks.getInstance().tabs.add(insertPosition, beeWorksTab);

                initFragmentAndTab(beeWorksTab, insertPosition);
                notifyViewPaperRefresh();

                refreshItemHomeTabViewIndex();
                refreshOffscreenPageLimit();

                registerTabsListener();

            }


            return;
        }

        //do removing
        if(BeeWorks.getInstance().containsTab(WorkbenchFragment.TAB_ID)) {

            BeeWorks.getInstance().removeTab(WorkbenchFragment.TAB_ID);
            TabHelper.removeTabId(WorkbenchFragment.TAB_ID);

            removeWorkbenchFragment();
            removeWorkbenchItemHomeTabView();

            notifyViewPaperRefresh();

            refreshItemHomeTabViewIndex();
            refreshOffscreenPageLimit();

        }





    }

    @NonNull
    private BeeWorksTab getWorkbenchBeeWorksTab(Workbench workbench) {
        BeeWorksTab beeWorksTab = new BeeWorksTab();
        beeWorksTab.id = WorkbenchFragment.TAB_ID;
        beeWorksTab.tabId = WorkbenchFragment.TAB_ID;
        beeWorksTab.type = "workbench";
        beeWorksTab.iconSelected = "icon_workbench_selected";
        beeWorksTab.iconUnSelected = "icon_workbench_unselected";
        beeWorksTab.name = workbench.getNameI18n(AtworkApplicationLike.baseContext);
        return beeWorksTab;
    }

    private void notifyViewPaperRefresh() {
        if (null != mMainViewPaper.getAdapter()) {
            mMainViewPaper.getAdapter().notifyDataSetChanged();
        }
    }

    private void removeWorkbenchFragment() {
        List<Fragment> fragmentRemovedList = new ArrayList<>();
        for(Fragment fragment : mFragmentList) {
            if(fragment instanceof WorkbenchFragment) {
                WorkbenchFragment workbenchFragment = (WorkbenchFragment) fragment;
                workbenchFragment.clear();

                fragmentRemovedList.add(fragment);
            }
        }

        mFragmentList.removeAll(fragmentRemovedList);
    }

    private void removeWorkbenchItemHomeTabView() {
        ItemHomeTabView itemHomeTabView = mTabMap.get(WorkbenchFragment.TAB_ID);
        if(null != itemHomeTabView) {
            mTabs.remove(itemHomeTabView);
            mTabMap.remove(itemHomeTabView);
            mHomeTabLayout.removeView(itemHomeTabView);
        }
    }


    private void doRefreshFloatUpdateTipView() {

        if (shouldRefreshFloatUpdateTipView()) {
            mRlUpdateNotice.setVisibility(View.VISIBLE);
            mTvUpdateNotice.setText(getString(R.string.tip_new_version, AtworkUtil.getNewVersionName(BaseApplicationLike.baseContext)));

        } else {
            mRlUpdateNotice.setVisibility(View.GONE);

        }

    }

    private boolean shouldRefreshFloatUpdateTipView() {

        if(UpgradeRemindMode.NEVER == DomainSettingsManager.getInstance().getUpgradeRemindMode()) {
            return false;
        }

        if(0 < WorkplusUpdateManager.INSTANCE.getShowTipCount()) {
            return false;
        }

        if(!WorkplusUpdateManager.INSTANCE.isNeedShowUpdateTipFloat()) {
            return false;
        }

        if(!AtworkUtil.isFoundNewVersion(BaseApplicationLike.baseContext)) {
            return false;
        }

        if(!"im".equalsIgnoreCase(getCurrentHomeTabId())) {
            return false;
        }

        return true;


    }

    private void refreshAllTabsText() {
        for(ItemHomeTabView tab : mTabs) {
            String tabId = tab.getTabId();
            String id = TabHelper.getId(tabId);
            BeeWorksTab beeWorksTab = BeeWorks.getInstance().getBeeWorksTabById(MainActivity.this, id);
            String tabTitle = StringUtils.EMPTY;
            if(null != beeWorksTab) {
                tabTitle = beeWorksTab.name;

            }

            if(StringUtils.isEmpty(tabTitle)) {
                if(ChatListFragment.TAB_ID.equalsIgnoreCase(tabId)) {
                    tabTitle = getString(R.string.item_chat);

                } else if(ContactFragment.TAB_ID.equalsIgnoreCase(tabId)) {
                    tabTitle = getString(R.string.item_contact);

                } else if(AppFragment.TAB_ID.equalsIgnoreCase(tabId)) {
                    tabTitle = getString(R.string.item_app);

                } else if(AboutMeFragment.TAB_ID.equalsIgnoreCase(tabId)) {
                    tabTitle = getString(R.string.item_about_me);

                }

            }

            if (!StringUtils.isEmpty(tabTitle)) {
                tab.setTitle(tabTitle);
            }
        }
    }


    public void refreshAllTabNoticeView() {
        List<String> tabIdIdList = new ArrayList<>(mTabMap.keySet());
        for(String tabIdId : tabIdIdList) {
            refreshTabNoticeView(tabIdId);
        }
    }

    public void refreshTabNoticeView(String tabIdId) {

        LightNoticeData lightNoticeJson = TabNoticeManager.getInstance().getTabNotice(tabIdId);

        if (lightNoticeJson == null) {

            Logger.e("tab_test_size", "tabs null NoticeTabAndBackHandledFragment 81");


            showNothing(tabIdId);

            return;
        }
        if (lightNoticeJson.isDigit()) {

            showNumNotice(tabIdId, lightNoticeJson.tip.num);

            return;
        }

        if (lightNoticeJson.isDot()) {
            showDotNotice(tabIdId);
            return;

        }
        if (lightNoticeJson.isNothing()) {
            showNothing(tabIdId);
            return;

        }

        if (lightNoticeJson.isIcon()) {
            showIconNotice(tabIdId, lightNoticeJson.tip.iconUrl);
            return;
        }
    }


    @Nullable
    public ItemHomeTabView getItemHomeTab(String tabIdId) {

        return mTabMap.get(tabIdId);
    }

    public void showNumNotice(String tabIdId, String num) {
        ItemHomeTabView tab = getItemHomeTab(tabIdId);
        if (null == tab) {
            logTraceTab();

            return;
        }
        tab.setNum(Integer.parseInt(num));

    }

    public void logTraceTab() {
        try {
            Logger.e("tab_test_size", "tabs map size ->" + mTabMap.size() + "");


            Logger.e("tab_test",  "tabs -> " + mTabs.toString());
            Logger.e("tab_test_size", "tabs size ->" + mTabs.size() + "");
        } catch (Exception e) {
            e.printStackTrace();

            Logger.e("tab_test_size", "tab exception" + e.toString());

        }
    }

    public void showDotNotice(String tabIdId) {
        ItemHomeTabView tab = getItemHomeTab(tabIdId);
        if (null == tab) {
            logTraceTab();

            return;
        }

        tab.showNewMessage();


    }

    public void showIconNotice(String tabIdId, String imageUrl) {
        //TODO FIXME 支持图片的显示
        ItemHomeTabView tab = getItemHomeTab(tabIdId);
        if (null != tab) {
            tab.showNewMessage();

        }
    }

    public void showNothing(String tabIdId) {
        ItemHomeTabView tab = getItemHomeTab(tabIdId);
        if (null != tab) {
            tab.showNothing();

        }
    }

    private void checkFloatPermission() {
        Context context = BaseApplicationLike.baseContext;
        int askedAlertCount = CommonShareInfo.getFloatPermissionAskedAlertInMainCount(context);
        if(CommonShareInfo.DEFAULT_NEED_FLOAT_PERMISSION_ASKED_ALERT_IN_MAIN_COUNT <= askedAlertCount) {
            return;
        }



        AtworkAlertDialog atworkAlertDialog = new AtworkAlertDialog(this, AtworkAlertDialog.Type.CLASSIC)
                .setTitleText(R.string.float_windows_no_permission_alert_title)
                .setContent(getString(R.string.float_windows_no_permission_common_alert_content, getString(R.string.app_name), getString(R.string.app_name)))
                .setForbiddenBack()
                .setAlertCanceledOnTouchOutside(false);

//        if((RomUtil.isMeizu() || RomUtil.isXiaomiNeedFloatPermissionCheck())) {
//            if(!FloatWindowPermissionUtil.isFloatWindowOpAllowed(this)) {
//                atworkAlertDialog.hideDeadBtn()
////                        .setBrightBtnText(R.string.ok)
//                        .show();
//
//                CommonShareInfo.setFloatPermissionAskedAlertInMainCount(context, ++askedAlertCount);
//
//                return;
//            }
//        }



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            atworkAlertDialog.setClickBrightColorListener(dialog -> {
                /** if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + MainActivity.this.getPackageName()));
                /** request permission via start activity for result */
                startActivityForResult(intent, REQUEST_CODE_PERMISSION_WINDOWS_OVERLAY);
            })
            .show();

            CommonShareInfo.setFloatPermissionAskedAlertInMainCount(context, ++askedAlertCount);


            return;
        }



    }

}