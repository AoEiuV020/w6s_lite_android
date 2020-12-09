package com.foreveross.atwork.support;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreverht.workplus.ui.component.statusbar.WorkplusStatusBarHelper;
import com.foreveross.atwork.api.sdk.setting.DynamicPropertiesAsyncNetService;
import com.foreveross.atwork.broadcast.HomeWatcherReceiver;
import com.foreveross.atwork.broadcast.ScreenReceiver;
import com.foreveross.atwork.component.IRootViewListener;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.interfaces.IBindWbShareCallbackProxy;
import com.foreveross.atwork.infrastructure.interfaces.OnWbShareCallbackProxy;
import com.foreveross.atwork.infrastructure.lifecycle.IBindActivityLifecycleListener;
import com.foreveross.atwork.infrastructure.lifecycle.OnLifecycleListener;
import com.foreveross.atwork.infrastructure.manager.FileAlbumService;
import com.foreveross.atwork.infrastructure.model.domain.AppVersions;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.AtworkConstants;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.foreveross.atwork.listener.EnterLeaveAppListener;
import com.foreveross.atwork.listener.HomeActionListener;
import com.foreveross.atwork.listener.ScreenActionListener;
import com.foreveross.atwork.manager.listener.DomainSettingUpdater;
import com.foreveross.atwork.modules.login.listener.ILoginFlowListener;
import com.foreveross.atwork.modules.main.service.HandleLoginService;
import com.foreveross.atwork.modules.secure.manager.ApkVerifyManager;
import com.foreveross.atwork.modules.secure.manager.RootVerifyManager;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper;
import com.foreveross.atwork.utils.statusbar.OnStatusBarChangeListener;
import com.foreveross.theme.interfaces.ISkinUpdate;
import com.foreveross.theme.manager.SkinMaster;
import com.foreveross.theme.model.Theme;
import com.sina.weibo.sdk.share.WbShareCallback;


public abstract class BaseActivity extends FragmentActivity implements ISkinUpdate, OnStatusBarChangeListener, DomainSettingUpdater, HomeActionListener, ScreenActionListener, EnterLeaveAppListener
        , IBindActivityLifecycleListener
        , IBindWbShareCallbackProxy
        , ILoginFlowListener
        , WbShareCallback
        , IRootViewListener {

    public static final String ACTION_FINISH_CHAIN = "ACTION_FINISH_CHAIN";

    public static final String DATA_CHAIN_TAG = "DATA_CHAIN_TAG";
    public static final String DATA_RESULT_CODE = "DATA_RESULT_CODE";

    protected boolean mOnStarted = false;
    protected boolean mOnResumed = false;

    private OnLifecycleListener mOnLifecycleListener;
    private OnWbShareCallbackProxy mOnWbShareCallbackProxy;


    private static boolean sCheckAppUpdated = false;


    private BroadcastReceiver mAppUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AtworkConstants.ACTION_RECEIVE_APP_UPDATE.equalsIgnoreCase(intent.getAction())) {
                AppVersions appVersions = intent.getParcelableExtra(DynamicPropertiesAsyncNetService.ACTION_INTENT_UPDATE_EXTRA);
                boolean silentMode = intent.getBooleanExtra(DynamicPropertiesAsyncNetService.DATA_INTENT_SILENT_UPDATE, true);

                AtworkUtil.updateApp(BaseActivity.this, appVersions, silentMode);

                sCheckAppUpdated = true;

                return;
            }


        }
    };

    private HomeWatcherReceiver mHomeWatcherReceiver = new HomeWatcherReceiver(this);

    private ScreenReceiver mScreenReceiver = new ScreenReceiver(this);

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(EnterLeaveAppListener.ACTION_ENTER_LEAVE_APP.equals(action)) {
                int handle = intent.getIntExtra(EnterLeaveAppListener.DATA_ENTER_LEAVE, -1);
                switch (handle) {
                    case EnterLeaveAppListener.LEAVE:
                        onLeaveApp();
                        break;

                    case EnterLeaveAppListener.ENTER:
                        onEnterApp();
                        break;
                }

                return;
            }


            if(ACTION_FINISH_CHAIN.equalsIgnoreCase(intent.getAction())) {
                String tagFinishAction = intent.getStringExtra(DATA_CHAIN_TAG);
                Integer resultCode = null;
                if(intent.hasExtra(DATA_RESULT_CODE)) {
                    resultCode = intent.getIntExtra(DATA_RESULT_CODE, Activity.RESULT_OK);

                }
                if(null == tagFinishAction) {
                    tagFinishAction = StringUtils.EMPTY;
                }

                if(tagFinishAction.equals(getFinishChainTag())) {
                    if (null != resultCode) {
                        BaseActivity.this.setResult(resultCode);
                    }
                    BaseActivity.this.finish();
                }
                return;

            }
        }
    };


    private AtworkAlertDialog mUpdateAlertDialog = null;

    private AtworkAlertDialog mVerifyLegalAlertDialog = null;

    private String mFinishChainTag;

    protected Intent mActionIntent = null;
    protected boolean mIsFromLogin = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(null != intent) {
            mIsFromLogin = intent.getBooleanExtra(HandleLoginService.DATA_FROM_LOGIN, false);
            mActionIntent = intent.getParcelableExtra(HandleLoginService.DATA_ACTION_INTENT);

        }

        LanguageUtil.checkLanguageSetting(this);

        if (needTheme()) {
            SkinMaster.getInstance().attach(this);
        }

        //5.0以下机器(华为, 三星)在 onCreate 时立马设置 statusBar时(AppCompat 主题的情况下), 会出现页面全屏的问题, 该逻辑
        // 判断在 Workplus 沉浸式全部使用全屏 fake view实现时去掉
        if (changeStatusBarOnCreate()) {
            changeStatusBar();
        }

        WorkplusTextSizeChangeHelper.setTextSizeTheme(this);

        registerBroadcast();


        LogUtil.e("u r in login flow now -> " + isInLoginFlow());

    }

    @Override
    protected void onStart() {
        //在操作 fragment 之前就要控制语言, 所以需要在 super.onStart()前调用
        LanguageUtil.checkLanguageSetting(this);

        super.onStart();

        mOnStarted = true;


        if (needTheme()) {
            changeTheme();
            changeStatusBar();
        }

        if (!changeStatusBarOnCreate()) {
            changeStatusBar();
        }

        handleHomeBackEvent();


    }

    @Override
    protected void onResume() {
        super.onResume();
        mOnResumed = true;

        registerUpdateReceiver();

        checkDomainSettingUpdate();

        FileAlbumService.getInstance().checkUpdate();

        if (needCheckAppLegal()) {
            ApkVerifyManager.INSTANCE.checkLegal(this);

            RootVerifyManager.INSTANCE.checkRoot(this);
        }



    }

    @Override
    protected void onPause() {
        super.onPause();

        mOnResumed = false;

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAppUpdateReceiver);
    }


    @Override
    protected void onStop() {
        super.onStop();

        mOnStarted = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (needTheme()) {
            SkinMaster.getInstance().detach(this);
        }

        unregisterBroadcast();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(null != mOnLifecycleListener) {
            mOnLifecycleListener.onActivityResult(data);
        }
    }

    protected boolean isVisable() {
        return  mOnStarted;
    }

    private boolean changeStatusBarOnCreate() {
        return Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT;
    }


    @Override
    public boolean checkDomainSettingUpdate() {


        AtworkUtil.checkUpdate(this, isImmediately());
        CommonShareInfo.setHomeKeyStatusForDomainSetting(this, false);
        return true;
    }

    private boolean isImmediately() {
//        if(!sCheckAppUpdated) {
//             return true;
//        }

        if(CommonShareInfo.getHomeKeyStatusForDomainSetting(this)) {
            return true;
        }

        return false;
    }

    @Override
    public void registerUpdateReceiver() {
        //splash 与 广告页 时不处理更新通知
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AtworkConstants.ACTION_RECEIVE_APP_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mAppUpdateReceiver, intentFilter);

    }

    @Override
    public void changeTheme() {
        SkinMaster.getInstance().changeTheme((ViewGroup) getWindow().getDecorView().getRootView());
    }

    @Override
    public void onThemeUpdate(Theme theme) {
//        recreate();

        changeTheme();

        changeStatusBar();
    }

    protected boolean needTheme() {
        return AtworkConfig.SKIN;
    }

    @Override
    public void onBackFromHome() {
        LogUtil.e("home stuff -> onBackFromHome");
    }

    @Override
    public void onHome() {
        LogUtil.e("home stuff -> onHome");

    }

    @Override
    public void onRecentApps() {
        LogUtil.e("home stuff -> onRecentApps");

    }

    @Override
    public void onScreenOn() {
        LogUtil.e("screen stuff -> onScreenOn");

    }

    @Override
    public void onScreenOff() {
        LogUtil.e("screen stuff -> onScreenOff");

    }

    @Override
    public void onScreenUserPresent() {
        LogUtil.e("screen stuff -> onScreenUserPresent");

    }

    @Override
    public void onEnterApp() {

    }

    @Override
    public void onLeaveApp() {

    }

    @Override
    public void changeStatusBar() {
        WorkplusStatusBarHelper.setCommonStatusBar(this);
    }




    private void handleHomeBackEvent() {
        if(true == CommonShareInfo.getHomeKeyStatusForCommon(this)) {
            this.onBackFromHome();
        }

        CommonShareInfo.setHomeKeyStatusForCommon(this, false);

    }

    private void registerBroadcast() {
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeWatcherReceiver, homeFilter);


        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenFilter.addAction(Intent.ACTION_USER_PRESENT);
        screenFilter.addAction(Intent.ACTION_INPUT_METHOD_CHANGED);
//        screenFilter.addAction(Intent.ACTION_TIME_TICK);
        screenFilter.addAction(Intent.ACTION_DATE_CHANGED);
        screenFilter.addAction(Intent.ACTION_TIME_CHANGED);
        screenFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

        registerReceiver(mScreenReceiver, screenFilter);

        IntentFilter enterLeaveFilter = new IntentFilter();
        enterLeaveFilter.addAction(EnterLeaveAppListener.ACTION_ENTER_LEAVE_APP);
        enterLeaveFilter.addAction(ACTION_FINISH_CHAIN);

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, enterLeaveFilter);

    }

    private void unregisterBroadcast() {
        if (mHomeWatcherReceiver != null) {
            unregisterReceiver(mHomeWatcherReceiver);
        }

        if(null != mScreenReceiver) {
            unregisterReceiver(mScreenReceiver);
        }

        if(null != mReceiver) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);

        }

    }

    public String getFinishChainTag() {
        return mFinishChainTag;
    }

    public void setFinishChainTag(String finishChainTag) {
        mFinishChainTag = finishChainTag;
    }

    public static void triggerFinishChain(String tag) {
        triggerFinishChain(tag, null);
    }

    public static void triggerFinishChain(String tag, @Nullable Integer resultCode) {
        if(!StringUtils.isEmpty(tag)) {
            Intent intent = new Intent(ACTION_FINISH_CHAIN);
            intent.putExtra(DATA_CHAIN_TAG, tag);
            if(null != resultCode) {
                intent.putExtra(DATA_RESULT_CODE, resultCode);
            }

            LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
        }
    }

    @Override
    public void bindOnLifecycleListener(OnLifecycleListener onLifecycleListener) {
        mOnLifecycleListener = onLifecycleListener;
    }

    @Override
    public void bindWbShareCallbackProxy(OnWbShareCallbackProxy onWbShareCallbackProxy) {
        mOnWbShareCallbackProxy = onWbShareCallbackProxy;
    }

    @Override
    public void onWbShareSuccess() {
        if(null != mOnWbShareCallbackProxy) {
            mOnWbShareCallbackProxy.onWbShareSuccess();
        }
    }

    @Override
    public void onWbShareCancel() {
        if(null != mOnWbShareCallbackProxy) {
            mOnWbShareCallbackProxy.onWbShareCancel();
        }
    }


    @Override
    public void onWbShareFail() {
        if(null != mOnWbShareCallbackProxy) {
            mOnWbShareCallbackProxy.onWbShareFail();
        }
    }

    /**
     * 用于从全屏->非全屏, 体验上更加舒服, 没有突然下坠的感觉
     */
    public void smoothSwitchScreen() {
        ViewGroup rootView = ((ViewGroup) this.findViewById(android.R.id.content));
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        rootView.setPadding(0, statusBarHeight, 0, 0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public void startActivity(Intent intent) {
        if(isInLoginFlow()) {
            intent.putExtra(HandleLoginService.DATA_FROM_LOGIN, isInLoginFlow());

        }

        if(null != mActionIntent && !mActionIntent.filterEquals(intent)) {
            intent.putExtra(HandleLoginService.DATA_ACTION_INTENT, mActionIntent);
        }

        super.startActivity(intent);
    }

    public Intent getActionIntent() {
        return mActionIntent;
    }



    public void clearActionIntent() {
        Intent intent = getIntent();
        if(null != intent) {
            intent.removeExtra(HandleLoginService.DATA_ACTION_INTENT);
            mActionIntent = null;
        }
    }

    public void unfullScreen() {
        WindowManager.LayoutParams lAttrs = getWindow().getAttributes();
        lAttrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(lAttrs);

    }

    public void fullScreen() {
        WindowManager.LayoutParams lAttrs = getWindow().getAttributes();
        lAttrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(lAttrs);
    }


    /**
     * 是否处于登录流程
     * */
    @Override
    public boolean isInLoginFlow() {
        return mIsFromLogin;
    }

    public boolean needCheckAppLegal() {
        return true;
    }

    @Override
    public int getRootHeight() {
        return -1;
    }

    public AtworkAlertDialog getUpdateAlertDialog() {

        if(null == mUpdateAlertDialog) {

            //double check
            synchronized (AtworkAlertDialog.class) {
                if(null == mUpdateAlertDialog) {
                    mUpdateAlertDialog = new AtworkAlertDialog(this);
                }
            }


        }

        return mUpdateAlertDialog;
    }

    public AtworkAlertDialog getVerifyLegalAlertDialog() {
        return mVerifyLegalAlertDialog;
    }



}
