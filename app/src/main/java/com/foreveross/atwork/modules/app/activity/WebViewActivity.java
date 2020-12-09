package com.foreveross.atwork.modules.app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.SensorManager;
import android.net.Uri;
import android.org.apache.commons.lang3.StringEscapeUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.threadGear.HighPriorityCachedTreadPoolExecutor;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.cordova.CordovaAsyncNetService;
import com.foreveross.atwork.component.popview.PopUpView;
import com.foreveross.atwork.cordova.plugin.ContactPlugin_New;
import com.foreveross.atwork.cordova.plugin.WebViewPlugin;
import com.foreveross.atwork.cordova.plugin.WxOrQQPlugin;
import com.foreveross.atwork.cordova.plugin.voice.VoiceCordovaPlugin;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.LightApp;
import com.foreveross.atwork.infrastructure.model.app.appEnum.BannerType;
import com.foreveross.atwork.infrastructure.model.app.appEnum.DisplayMode;
import com.foreveross.atwork.infrastructure.model.clickStatistics.Type;
import com.foreveross.atwork.infrastructure.model.setting.WebviewFloatActionSetting;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.infrastructure.utils.PatternUtils;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper;
import com.foreveross.atwork.infrastructure.utils.ViewCompat;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;
import com.foreveross.atwork.infrastructure.webview.AtworkWebView;
import com.foreveross.atwork.infrastructure.webview.OnSetWebUiChangeListener;
import com.foreveross.atwork.infrastructure.webview.OnWebActivityActionListener;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.FengMapManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.ShakeDetector;
import com.foreveross.atwork.manager.cas.CasLoginNetService;
import com.foreveross.atwork.manager.listener.BaseQueryListener;
import com.foreveross.atwork.modules.aboutatwork.fragment.AboutAtWorkFragment;
import com.foreveross.atwork.modules.app.component.WebTitleBarRightButtonView;
import com.foreveross.atwork.modules.app.model.WebRightButton;
import com.foreveross.atwork.modules.app.util.WebShareHandler;
import com.foreveross.atwork.modules.chat.activity.ChatInfoActivity;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.service.ChatService;
import com.foreveross.atwork.modules.chat.util.SchemaUrlJumpHelper;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.modules.login.activity.LoginWithAccountActivity;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.main.service.HandleLoginService;
import com.foreveross.atwork.modules.setting.util.W6sTextSizeHelper;
import com.foreveross.atwork.modules.web.auth.CordovaInjectType;
import com.foreveross.atwork.modules.web.component.Status;
import com.foreveross.atwork.modules.web.component.WebActionFloatView;
import com.foreveross.atwork.modules.web.component.WebSharePopupWindow;
import com.foreveross.atwork.modules.web.fragment.WebviewFragment;
import com.foreveross.atwork.modules.web.model.WebShareBuilder;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.theme.manager.SkinHelper;
import com.foreveross.watermark.core.WaterMarkUtil;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.tauth.Tencent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

//import com.tencent.tauth.Tencent;


/**
 * Created by lingen on 15/4/30.
 * Description:
 */
public class WebViewActivity extends WebViewBaseActivity implements OnWebActivityActionListener, AtworkWebView.OnWebViewFragmentCreate, WebTitleBarRightButtonView.OnActionListener {

    public static final String DATA_WEBVIEW_CONTROL_ACTION = "DATA_WEBVIEW_CONTROL_ACTION";
    public static final String AUTO_AUTH_CORDOVA = "AUTO_AUTH_CORDOVA";

    public static final String ACTION_FINISH = "ACTION_FINISH";
    /**
     * 从上个 webview 返回来
     */
    public static final String ACTION_GO_BACK_FROM_WEBVIEW = "ACTION_GO_BACK_FROM_WEBVIEW";

    public static final String ACTION_HOOKING_MODE = "ACTION_HOOKING_MODE";

    public static final String DATA_HOOKING_MODE = "ACTION_HOOKING_MODE";

    public static final String DATA_TARGET_WEBVIEW = "DATA_TARGET_WEBVIEW";
    public static final String LOAD_URL = "WEB_VIEW_LOAD_URL";
    public static final String VIEW_TITLE = "VIEW_TITLE";
    public static final String HIDDEN_SHARE = "hidden_share=1";

    private String mInitLoadUrl;

    private String mNowLoadUrl;

    private AppBundles mAppBundle;
    private String mTitle;
    private boolean mNeedAuth;
    private boolean mNeedWaterMark;

    //root view
    private RelativeLayout mRlRoot;
    //返回
    private ImageView mBackView;

    private View mViewRightest;
    //标题
    private TextView mTitleView;

    private TextView mBackText;
    //关闭
    private TextView mTvCloseView;
    private ImageView mIvCloseView;
    //标题栏，用于隐藏
    private RelativeLayout mTitleBarLayout;
    //url 无法加载时显示的提示view
    private View mViewUrlWrong;

    private View mViewWebShow;

    private WebActionFloatView mWebActionFloatView;

    public AtworkWebView mAtworkWebView;

    private WebviewFragment mWebViewFragment;

    private WebViewControlAction mWebViewControlAction;

    private boolean mFromNotice;

    private boolean mBackHome = false;

    private boolean mHasKeyboard = false;

    private String mSessionID;

    private boolean mHideTitle;

    private ArticleItem mArticleItem;

    private int mFrom;//来自哪个页面进来的。从这里判断是否是从收藏中进来
    /**
     * 确定的封面 url, 若存在该值, 则优先使用该 封面url
     */
    private String mDetermineCoverUrl;

    private boolean mIsPreJumpUrl;

    private boolean mNeedClose;

    private boolean mUseSystem;

    private View mTransparentView;

    private WebSharePopupWindow webSharePopupWindow;

    private boolean mWebCanBack = false;

    private boolean mFistOpenWeb = false;

    private boolean mFixedTitle = false;

    private Handler mHandler = new Handler();

    private boolean mCanCallBackKeyEvent = true;

    //返回时间是否有 down事件作为初始, 反之突然接收到前一个结束的 activity 的返回 up事件
    private boolean mBackKeyEventHasDown = false;

    private int mLeftAreaWidth;

    private WebTitleBarRightButtonView mRightButtons;

    private WebTitleBarRightButtonView mLeftButtons;

    private boolean mNeedShowShare = true;

    private boolean mNeedChangeStatusBar = true;

    private boolean mErrorPage = false;

    private boolean mLoadSuccess = false;

    /**
     * 左侧按钮自定义动作，默认空，不拦截返回
     */
    private String mNewBackAction;

    private Tencent mTencent;

    private boolean mIsNewUpdate = false;

    private boolean mHasChangedStatusBar;

    private String mForwardMode = "ALL";

    public static List<String> sWebActivityQueue = new ArrayList<>();

    private String mQueueTag = UUID.randomUUID().toString();

    private String mBehaviorLogKeyTag;

    private boolean mIsAppLogRecording = false;

    private ShakeDetector mShakeDetector;
    private boolean mCanShake = false; //避免多次回调
    private boolean mRegisterShake = false;
    private String mMessageId;
    private Integer mOrientation;

    private CordovaInjectType injectType = CordovaInjectType.InjectNeedAsked;

    public CordovaInjectType getInjectType(){
        return injectType;
    }

    private BroadcastReceiver mReceiveMainFinishListen = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            new Handler().postDelayed(() -> {
                if (mIsNewUpdate) {
                    AtworkToast.showLongToast(getString(R.string.update_success));
                }
                Intent newIntent = MainActivity.getMainActivityIntent(WebViewActivity.this, true);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newIntent);
                finish();
            }, 2000);

        }
    };

    private BroadcastReceiver mSideBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_FINISH.equals(action)) {
                WebViewActivity.this.finish();

                return;
            }

            if (ACTION_GO_BACK_FROM_WEBVIEW.equals(action)) {
                String targetTag = intent.getStringExtra(DATA_TARGET_WEBVIEW);
                if (mQueueTag.equals(targetTag)) {
                    if (AtworkConfig.COMMAND_DO_GOBACK_FROM_WEBVIEW_FINISH) {
                        onScriptAction("goBack()");
                    }
                }

                return;
            }


            if(ACTION_HOOKING_MODE.equals(action)) {
                boolean hookingFloatMode = intent.getBooleanExtra(DATA_HOOKING_MODE, false);

                if(null != mWebViewControlAction) {
                    mWebViewControlAction.setHookingFloatMode(hookingFloatMode);
                }

                return;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        initData();

        super.onCreate(savedInstanceState);
        sWebActivityQueue.add(mQueueTag);

        initView();
        initFragment();
        registerListener();

        initTitleMax();


        boolean result = ScreenUtils.handleOrientation(WebViewActivity.this, mOrientation);
        if(!result) {
            if (AtworkConfig.SCREEN_ORIENTATION_USER_SENSOR) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }
        }



        registerBroadcast();

        VoiceCordovaPlugin.init();

    }



    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mRegisterShake) {
            startShakeListening();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mRegisterShake) {
            stopShakeListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissWebSharePopupWindow();

    }



    @Override
    protected void onDestroy() {

        ContactPlugin_New.clearContactSelectedCache();
        FengMapManager.getInstance().stopLocation();
        WebViewActivity.goBackFromWebview();

        sWebActivityQueue.remove(mQueueTag);

        if (mReceiveMainFinishListen != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiveMainFinishListen);
        }
        System.gc();
        System.runFinalization();
        System.gc();
        super.onDestroy();

        WxOrQQPlugin.Companion.release();
        VoiceCordovaPlugin.release();

        unregisterBroadcast();

    }

    @Override
    protected void checkWebUrlHookingFloat() {
        if(isHookFloatMode()) {
            return;
        }


        super.checkWebUrlHookingFloat();
    }

    private boolean isHookFloatMode() {
        return null != mWebViewControlAction && true == mWebViewControlAction.mHookingFloatMode;
    }

    public static Intent getIntent(Context context, WebViewControlAction webViewControlAction) {
        return getIntent(context,webViewControlAction,false);
    }

    public static Intent getIntent(Context context, WebViewControlAction webViewControlAction,Boolean autoAuthCordova) {
        Intent intent = new Intent();
        intent.setClass(context, WebViewActivity.class);
        intent.putExtra(DATA_WEBVIEW_CONTROL_ACTION, webViewControlAction);
        intent.putExtra(AUTO_AUTH_CORDOVA, autoAuthCordova);
        return intent;
    }

    /**
     * before login, we do not need any locking action
     */
    public static Intent getIntentBeforeLogin(Context context, String url, boolean isPreJumpUrl) {
        WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                .setUrl(url)
                .setNeedShare(true)
                .setNeedCodeLock(false)
                .setIsPreJumpUrl(isPreJumpUrl);
        Intent intent = WebViewActivity.getIntent(context, webViewControlAction);
        return intent;
    }


    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.webview_fragment);
        if (fragment == null) {
            fragment = createFragment();
            fragmentManager.beginTransaction().add(R.id.webview_fragment, fragment).commit();
        }

        mAtworkWebView.setFragmentCreateListener(this);


    }


    private void handleInitData() {
        if (mHideTitle) {
            StatusBarUtil.setTransparentForImageView(this, null);
        }

        setViewTitle(mTitle);

        if(mNeedWaterMark) {
            Uri uri = Uri.parse(mInitLoadUrl);

            WaterMarkUtil.setLoginUserWatermark(this, mAtworkWebView.getWatermarkView(), uri.getQueryParameter("watermark_add_value"));
        }

        if (mArticleItem != null) {
            mArticleItem.url = mInitLoadUrl;
        }
        if (mFromNotice && !TextUtils.isEmpty(mSessionID)) {
            Session session = ChatSessionDataWrap.getInstance().getSession(mSessionID, null);
            if (session != null) {
                ChatService.sendSessionReceipts(this, session);

                ChatSessionDataWrap.getInstance().readSpecialSession(this, session);

                SessionRefreshHelper.notifyRefreshSessionAndCount();


            }
        }
        handleTitleViewByURL();

        if (!mHasChangedStatusBar && mAppBundle != null) {

            refreshUIFromApp();
        }


        if (!StringUtils.isEmpty(mInitLoadUrl)) {
            if (mAppBundle != null) {
                loadWebViewFromApp();
            } else {
                loadWebViewFromURL(mInitLoadUrl);
            }

            return;
        }


        AtworkToast.showToast(getResources().getString(R.string.not_valid_url));
        finish();
    }

    private void initData() {
        handleWebViewControlAction();

    }


    @SuppressLint("StaticFieldLeak")
    private void getApp(BaseQueryListener<App> listener) {

        new AsyncTask<Void, Void, App>(){

            @Override
            protected App doInBackground(Void... voids) {
                return getLightAppSync();
            }

            @Override
            protected void onPostExecute(App app) {
                listener.onSuccess(app);
            }
        }.executeOnExecutor(HighPriorityCachedTreadPoolExecutor.getInstance());
    }

    @Nullable
    private App getLightAppSync() {
        if(StringUtils.isEmpty(mSessionID)) {
           return null;
        }

        Session session = ChatSessionDataWrap.getInstance().getSession(mSessionID, null);
        if(null != session && session.isAppType()) {
            App app = AppManager.getInstance().queryAppSync(BaseApplicationLike.baseContext, mSessionID, session.orgId);
            return app;
        }

        return null;
    }

    private void handleWebViewControlAction() {
        WebViewControlAction webViewControlAction = getIntent().getParcelableExtra(DATA_WEBVIEW_CONTROL_ACTION);

        Boolean autoAuthCordova = getIntent().getBooleanExtra(AUTO_AUTH_CORDOVA,false);
        if(autoAuthCordova){
            injectType = CordovaInjectType.InjectAllow;
        }

        mWebViewControlAction = webViewControlAction;

        mMessageId = webViewControlAction.mMessageId;
        mInitLoadUrl = webViewControlAction.getInitLoadUrl();
        mAppBundle = webViewControlAction.mAppBundle;
        mTitle = webViewControlAction.mTitle;
        mFromNotice = webViewControlAction.mFromNotice;
        mBackHome = webViewControlAction.mBackToHome;
        mSessionID = webViewControlAction.mSessionId;
        mHideTitle = webViewControlAction.mHideTitle;
        mOrientation = webViewControlAction.mOrientation;
        mIsPreJumpUrl = webViewControlAction.mIsPreJumpUrl;
        mDetermineCoverUrl = webViewControlAction.mCoverUrl;
        mNeedIntercept = webViewControlAction.mNeedCodeLock;
        mNeedShowShare = webViewControlAction.mNeedShare;
        mNeedClose = webViewControlAction.mNeedClose;
        mNeedChangeStatusBar = webViewControlAction.mNeedChangeStatusBar;
        mArticleItem = webViewControlAction.mArticleItem;
        mNeedAuth = webViewControlAction.mNeedAuth;
        mFrom = webViewControlAction.mFrom;

        if (null != mAppBundle) {
            mHideTitle = DisplayMode.FULL_SCREEN.equals(mAppBundle.mSettings.mMobileBehaviour.mScreenMode);
            injectType = CordovaInjectType.InjectAllow;

//            String lightAppUrl = mAppBundle.mAccessEndPoints.get(LightApp.MOBILE_ENDPOINT);
//            if (TextUtils.isEmpty(mInitLoadUrl) && !StringUtils.isEmpty(lightAppUrl)) {
//                mInitLoadUrl = lightAppUrl;
//            }
        }

        if ((null != mAppBundle && AtworkConfig.LIGHT_APP_INIT_HIDE_MORE_BTN) || (!TextUtils.isEmpty(mInitLoadUrl) && mInitLoadUrl.contains(HIDDEN_SHARE))) {
            mNeedShowShare = false;
        }

        if(null != mAppBundle) {

            if(StringUtils.isEmpty(mTitle)) {
                mTitle = mAppBundle.getTitleI18n(this);

            }
        }




        mUseSystem = webViewControlAction.mUseSystem;
        mNeedWaterMark = null != webViewControlAction.getWatermark() && webViewControlAction.getWatermark();



    }

    private boolean isNeedHideShare() {
        return (null != mAppBundle && AtworkConfig.LIGHT_APP_INIT_HIDE_MORE_BTN)
                || UrlHandleHelper.isHiddenShare(mInitLoadUrl)
                || mAppBundle != null && UrlHandleHelper.isHiddenShare(mAppBundle.mAccessEndPoints.get(LightApp.MOBILE_ENDPOINT));
    }

    private void loadWebViewFromApp() {
        setViewTitle(mAppBundle.getTitleI18n(BaseApplicationLike.baseContext));
//        mInitLoadUrl = mAppBundle.mAccessEndPoints.get(LightApp.MOBILE_ENDPOINT);

        //if need cas
        if (mAppBundle.needCasLogin()) {
            LogUtil.e("--->    start login cas");
            CasLoginNetService.login(mWebViewFragment.getAppView().getView(), mAppBundle.getTicketUrl(), success -> {
                loadWebViewFromURL(mInitLoadUrl);
            });

        } else {

            loadWebViewFromURL(mInitLoadUrl);

        }


    }


    /**
     * 根据轻应用控制头部 ui
     *
     * @return 是否做了处理
     */
    private boolean refreshUIFromApp() {

        if (!mHideTitle) {

            if (BannerType.CUSTOM_COLOR.equals(mAppBundle.mSettings.mMobileBehaviour.mBannerType)) {
                SkinHelper.clearSkinTag(mTitleBarLayout);

                mTitleBarLayout.setBackgroundColor(Color.parseColor(mAppBundle.mSettings.mMobileBehaviour.mBannerProp));

                StatusBarUtil.setColorNoTranslucent(this, Color.parseColor(mAppBundle.mSettings.mMobileBehaviour.mBannerProp));

                mHasChangedStatusBar = true;
                return true;


            } else if (BannerType.CUSTOM_PIC.equals(mAppBundle.mSettings.mMobileBehaviour.mBannerType)) {
                SkinHelper.clearSkinTag(mTitleBarLayout);

                ImageCacheHelper.loadImageByMediaId(mAppBundle.mSettings.mMobileBehaviour.mBannerProp, ImageCacheHelper.getDefaultImageOptions(false, true, true), new ImageCacheHelper.ImageLoadedListener() {
                    @Override
                    public void onImageLoadedComplete(Bitmap bitmap) {
                        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                        ViewCompat.setBackground(mTitleBarLayout, drawable);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            ViewGroup.LayoutParams layoutParams = mTitleBarLayout.getLayoutParams();
                            layoutParams.height += StatusBarUtil.getStatusBarHeight(WebViewActivity.this);
                            mTitleBarLayout.setLayoutParams(layoutParams);

                            StatusBarUtil.setTransparentForImageView(WebViewActivity.this, null);
                        }

                        mHasChangedStatusBar = true;

                    }

                    @Override
                    public void onImageLoadedFail() {

                    }
                });

                return true;

            }
        }


        if (LightApp.HORIZONTAL.equalsIgnoreCase(mAppBundle.mSettings.mMobileBehaviour.mShowMode)) {
            ScreenUtils.landscapeMode(this, true, true);
        }

        return false;

    }


    private boolean needToApplyTicket(String url) {
        return !TextUtils.isEmpty(url) && url.contains("{{ticket}}");
    }

    private void loadWebViewFromURL(String url) {
        //用于比较协议头等处理
//        LogUtil.e("LoadUrl", url + "");

        if(null != mAppBundle && !StringUtils.isEmpty(mAppBundle.mRouteUrl)) {
            doLoadWebViewFromURL(mAppBundle.mRouteUrl);
            return;
        }

        doLoadWebViewFromURL(url);
    }

    private void doLoadWebViewFromURL(String url) {
        String lowerUrl = url.intern().toLowerCase();
        if (lowerUrl.startsWith("local://")) {
            url = UrlHandleHelper.handle(this, url, mAppBundle);

        } else {
            url = UrlHandleHelper.fixProtocolHead(url);
        }

        if(lowerUrl.startsWith("file://") || lowerUrl.startsWith("local://")){
            injectType = CordovaInjectType.InjectAllow;
        }

        url = UrlHandleHelper.replaceOrgKeyParam(this, url, mAppBundle);
        url = UrlHandleHelper.replaceBasicKeyParams(this, url);

        mInitLoadUrl = url;
        mNowLoadUrl = url;

        if (!needToApplyTicket(mInitLoadUrl)) {
            mAtworkWebView.loadUrl(url);
            return;
        }
        handleApplyTicket(mInitLoadUrl);
    }


    private void handleApplyTicket(String loadUrl) {
        LogUtil.e("LoadUrl", "ticket:" + loadUrl);
        CordovaAsyncNetService.getUserTicket(BaseApplicationLike.baseContext, new CordovaAsyncNetService.GetUserTicketListener() {
            @Override
            public void getUserTicketSuccess(String userTicket) {

                replaceUrlTicket(userTicket);


                if (null != mAtworkWebView) {
                    mAtworkWebView.loadUrl(mInitLoadUrl);
                }
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {

                AtworkToast.showToast("请求应用ticket失败:" + errorCode);
                showErrorPage();
            }
        });
    }

    private void replaceUrlTicket(String userTicket) {
        StringBuffer sb = new StringBuffer();

        if (CustomerHelper.isH3c(BaseApplicationLike.baseContext) || !mInitLoadUrl.contains("?")) {
            sb.append(userTicket);
            sb.append("?access_token=");
            sb.append(LoginUserInfo.getInstance().getLoginUserAccessToken(BaseApplicationLike.baseContext));

        } else {
            sb.append(userTicket);
            sb.append("&access_token=");
            sb.append(LoginUserInfo.getInstance().getLoginUserAccessToken(BaseApplicationLike.baseContext));
        }

        mInitLoadUrl = mInitLoadUrl.replace("{{ticket}}", sb.toString());


        CrashReport.postCatchedException(new Throwable("{{ticket}} -> " + mInitLoadUrl));


    }


    private void showErrorPage() {
        mErrorPage = true;
        mViewUrlWrong.setVisibility(View.VISIBLE);
        mViewWebShow.setVisibility(View.GONE);
//        mMoreView.setVisibility(View.GONE);
        mTitleView.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(mTitle)) {
            mTitleView.setVisibility(View.VISIBLE);
            setViewTitle(mTitle);
        }

    }

    private void handleTitleViewByURL() {
        if (mHideTitle) {
            mTitleBarLayout.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(mInitLoadUrl) && mAppBundle == null) {
            return;
        }
        //说明使用的是本地的url，比如同事圈,应用市场
        if (mHideTitle && (mInitLoadUrl != null && (mInitLoadUrl.startsWith("local://")))) {
            mTitleBarLayout.setVisibility(View.GONE);
        }
    }


    private void registerListener() {

        mWebActionFloatView.setOnClickEventListener(status -> {
            if(Status.BACK == status) {


                if (!mAtworkWebView.backHistory()) {
                    if (mIsPreJumpUrl) {
                        HandleLoginService.getInstance().toStart(WebViewActivity.this, mHandler, 0);
                        return;
                    }
                    finish();
                }
                return;
            }

            if(Status.CLOSE == status) {
                finish();

                return;
            }
        });

        getCloseView().setOnClickListener(v -> {
            if (mIsPreJumpUrl) {
                HandleLoginService.getInstance().toStart(WebViewActivity.this, mHandler, 0);
                return;
            }
            finish();
        });

        mBackView.setOnClickListener(v -> handleBackAction());

        mBackText.setOnClickListener(v -> handleBackAction());


        //监听键盘的状态
        mRlRoot.getViewTreeObserver().addOnGlobalLayoutListener(() -> {

            Rect r = new Rect();
            mRlRoot.getWindowVisibleDisplayFrame(r);


            int screenHeight;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                screenHeight = ScreenUtils.getScreenHeight(this);
            } else {
                screenHeight = mRlRoot.getHeight();
            }

            mHasKeyboard = (screenHeight != r.bottom);

        });

        mViewUrlWrong.setOnClickListener((view) -> {
            if(CommonUtil.isFastClick(2000) || !NetworkStatusUtil.isNetworkAvailable(this)) return;
            mViewUrlWrong.setVisibility(View.GONE);
            mViewWebShow.setVisibility(View.VISIBLE);
            mTitleView.setVisibility(View.VISIBLE);
            mAtworkWebView.reload();
            mErrorPage = false;
        });


    }

    private void handleBackAction() {
        if (mBackHome) {
            String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(this);
            if (TextUtils.isEmpty(accessToken)) {
                startActivity(LoginWithAccountActivity.getClearTaskIntent(this));
            } else {
                startActivity(MainActivity.getMainActivityIntent(this, true));
            }
            finish();
            return;
        }
        if (!TextUtils.isEmpty(mNewBackAction)) {
            mAtworkWebView.loadJS(mNewBackAction);
            return;
        }
        if (!mAtworkWebView.backHistory()) {
            if (mIsPreJumpUrl) {
                HandleLoginService.getInstance().toStart(WebViewActivity.this, mHandler, 0);
                return;
            }
            finish();
        }
    }
    private void tryRevertTitleFixedStatus() {
        if(!mFistOpenWeb && !mAtworkWebView.canGoBack()) {
            revertTitleFixedStatus();
        }
    }

    private void revertTitleFixedStatus() {
        mFixedTitle = false;
    }


    private void popDefaultAction() {
        //强制关闭键盘
        AtworkUtil.hideInput(WebViewActivity.this);

        if (null != mAppBundle) {
            popLightAppWin();

        } else {
            popWebShareWin();

        }
    }

    private void popLightAppWin() {
        if (mAppBundle != null) { //轻应用的情况
            final PopUpView popUpView = new PopUpView(WebViewActivity.this);
            popUpView.addPopItem(R.mipmap.icon_phone_single, R.string.refresh, 0);
            popUpView.addPopItem(R.mipmap.icon_info, R.string.app_info, 1);
            popUpView.setPopItemOnClickListener((title, pos) -> {
                if (title.equals(getResources().getString(R.string.refresh))) {
                    tryRevertTitleFixedStatus();
                    mAtworkWebView.reload();
                    popUpView.dismiss();
                    return;
                }

                if (title.equals(getResources().getString(R.string.app_info))) {
                    Fragment fragment = (Fragment) mAtworkWebView;
                    Intent intent = ChatInfoActivity.getIntent(SessionType.LightApp, mAppBundle.mBundleId, mAppBundle.appDomainId, mAppBundle.mOrgId);
                    fragment.startActivity(intent);
                    popUpView.dismiss();
                    return;
                }
            });

            popUpView.pop(mRightButtons);

        } else { //普通网页的情况


        }
    }

    private void popWebShareWin() {
        OrganizationManager.getInstance().queryOrgLocalHavingCircle(this, orgList -> {

            ArticleItem articleItem = getArticleItemCompatible();
            articleItem.mForwardMode = mForwardMode;

            webSharePopupWindow = WebSharePopupWindow.newBuilder()
                    .setContext(WebViewActivity.this)
                    .setFragment(mWebViewFragment)
                    .setOrgListHavingCircle(orgList)
                    .setArticleItem(articleItem)
                    .setShareType(ShareChatMessage.ShareType.Link)
                    .setNeedFetchInfoFromRemote(AtworkConfig.WEBVIEW_CONFIG.isNeedFetchInfoFromRemote())
                    .setMessageId(mMessageId)
                    .setSessionId(mSessionID)
                    .buildWebSharePopupWindow();

            webSharePopupWindow.setHookingFloatMode(isHookFloatMode());


            if (mLoadSuccess) {
                webSharePopupWindow.setCommonModeList(mFrom);

            } else {
                webSharePopupWindow.setPreLoadMode();

            }


            webSharePopupWindow.show(getSupportFragmentManager(), "webSharePopupWindow");

//            webSharePopupWindow.showAtLocation(mViewWebShow, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//            mTransparentView.setVisibility(View.VISIBLE);
//            webSharePopupWindow.setOnDismissListener(() -> mTransparentView.setVisibility(View.GONE));



        });
    }



    @NonNull
    private ArticleItem getArticleItemCompatible() {
        ArticleItem articleItem;
        if (null != mArticleItem) {
            articleItem = mArticleItem;
//            if (StringUtils.isEmpty(articleItem.mCoverUrl)) {
//                articleItem.mCoverUrl = getCoverUrl();
//            }
        } else {
            articleItem = new ArticleItem();
            articleItem.url = mNowLoadUrl;
//            articleItem.title = mTitleView.getText().toString();
//
//            articleItem.mCoverUrl = getCoverUrl();

        }

        if(StringUtils.isEmpty(articleItem.mCoverUrl) && !StringUtils.isEmpty(mDetermineCoverUrl)) {
            articleItem.mCoverUrl = mDetermineCoverUrl;
        }

        return articleItem;
    }

    private String getCoverUrl() {
        //优先使用 mDetermineCoverUrl, 若不存在, 则使用 webview 爬下来的封面 url
        String coverUrl;
        if (!StringUtils.isEmpty(mDetermineCoverUrl)) {
            coverUrl = mDetermineCoverUrl;

        } else {
            coverUrl = mAtworkWebView.getCrawlerCoverUrl();

        }
        return coverUrl;
    }

    /**
     * 弹出分享的菜单栏
     *
     * @param articleItem
     * @param shareScope  分享的范围, 0是内部分享, 1是全部, 不做任何限制
     * @param shareDirectlyType 直接分享的类型
     * @param shareSessionId 分享
     * @param callbackContext
     */
    public void showSharePopupFromCordova(ArticleItem articleItem, int shareScope, String shareDirectlyType, String shareSessionId, CallbackContext callbackContext) {
        runOnUiThread(() -> {

            ShareChatMessage.ShareType shareType;
            if (!StringUtils.isEmpty(articleItem.mOrgCode) && !StringUtils.isEmpty(articleItem.mOrgDomainId)) {
                shareType = ShareChatMessage.ShareType.OrgInviteBody;
            } else {
                shareType = ShareChatMessage.ShareType.Link;
            }

            if(!StringUtils.isEmpty(shareDirectlyType)) {
                WebShareBuilder webShareBuilder = new WebShareBuilder()
                        .setContext(WebViewActivity.this)
                        .setFragment(mWebViewFragment)
                        .setArticleItem(articleItem)
                        .setShareType(shareType)
                        .setNeedFetchInfoFromRemote(AtworkConfig.WEBVIEW_CONFIG.isNeedFetchInfoFromRemote())
                        .setCallbackContext(callbackContext);

                WebShareHandler webShareHandler = new WebShareHandler(webShareBuilder);
                webShareHandler.share(shareDirectlyType, shareSessionId);

                return;
            }

            OrganizationManager.getInstance().queryOrgLocalHavingCircle(this, orgList -> {



                webSharePopupWindow = WebSharePopupWindow.newBuilder()
                        .setContext(WebViewActivity.this)
                        .setFragment(mWebViewFragment)
                        .setOrgListHavingCircle(orgList)
                        .setArticleItem(articleItem)
                        .setShareType(shareType)
                        .setNeedFetchInfoFromRemote(AtworkConfig.WEBVIEW_CONFIG.isNeedFetchInfoFromRemote())
                        .setMessageId(mMessageId)
                        .setSessionId(mSessionID)
                        .buildWebSharePopupWindow();

                webSharePopupWindow.setHookingFloatMode(isHookFloatMode());

                if (0 == shareScope) {
                    webSharePopupWindow.setInnerShareModeList();

                } else {
                    if (ShareChatMessage.ShareType.OrgInviteBody == shareType) {
                        webSharePopupWindow.setCommonModeExcludeRefresh();

                    } else {
                        webSharePopupWindow.setCommonModeList();

                    }

                }

                webSharePopupWindow.show(getSupportFragmentManager(), "webSharePopupWindow");

//                webSharePopupWindow.showAtLocation(mViewWebShow, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//                mTransparentView.setVisibility(View.VISIBLE);
//                webSharePopupWindow.setOnDismissListener(() -> mTransparentView.setVisibility(View.GONE));
            });


        });

    }

    private void initView() {
        setContentView(R.layout.activity_webview);

        mRlRoot = findViewById(R.id.rl_root);
        mTitleBarLayout = findViewById(R.id.webview_title_bar);
        mViewWebShow = findViewById(R.id.webview_fragment);
        mViewUrlWrong = findViewById(R.id.view_url_wrong);
        mTitleView = findViewById(R.id.webview_title);
        mBackView = findViewById(R.id.webview_back);
        mViewRightest = findViewById(R.id.rl_rightest);
        mRightButtons = findViewById(R.id.web_title_right_button);
        mLeftButtons = findViewById(R.id.web_title_left_button);
        mBackText = findViewById(R.id.webview_back_text);
        mTvCloseView = findViewById(R.id.tv_webview_close);
        mIvCloseView = findViewById(R.id.iv_webview_close);
        mWebActionFloatView = findViewById(R.id.v_workplus_float_action_btn);


        handleWebviewFloatActionBtn();
        if (CustomerHelper.isRuYuan(this)) {
            mBackText.setVisibility(View.VISIBLE);
            mBackView.setVisibility(View.GONE);
            mTvCloseView.setText(getString(R.string.item_app));
        } else {
            mBackText.setVisibility(View.GONE);
            mBackView.setVisibility(View.VISIBLE);
        }
        if (isAlwaysShowClose()) {
            getCloseView().setVisibility(View.VISIBLE);

        } else {
            getCloseView().setVisibility(View.GONE);

        }

        mTransparentView = new View(this);
        mTransparentView.setBackgroundColor(Color.BLACK);
        mTransparentView.setAlpha(0.5f);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(mTransparentView, lp);
        mTransparentView.setVisibility(View.GONE);


        String theme = UrlHandleHelper.getTheme(mWebViewControlAction.getInitLoadUrl());
        if (mNeedShowShare) {
            mRightButtons.initDefaultRightBtn(this, "red_envelope".equals(theme));

        }

        refreshRedEnvelopeThemeTitleBarView(theme);

        boolean updateBeeWorks = getIntent().getBooleanExtra("BeeWorks_Update", false);
        if (updateBeeWorks) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mReceiveMainFinishListen, new IntentFilter(AboutAtWorkFragment.ACTION_MAIN_FINISH));
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MainActivity.ACTION_FINISH_MAIN));
        }


    }

    private View getCloseView() {
        if(AtworkConfig.WEBVIEW_CONFIG.isCloseImageBtn()) {
            return mIvCloseView;

        } else {
            return mTvCloseView;

        }
    }

    private void handleWebviewFloatActionBtn() {
        int webviewFloatActionSetting = PersonalShareInfo.getInstance().getWebviewFloatActionSetting(BaseApplicationLike.baseContext);
        switch (webviewFloatActionSetting) {
            case WebviewFloatActionSetting.CLOSE:
                mWebActionFloatView.setVisibility(View.GONE);

                break;

            case WebviewFloatActionSetting.BOTTOM_LEFT:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mWebActionFloatView.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                mWebActionFloatView.setLayoutParams(layoutParams);

                mWebActionFloatView.setVisibility(View.VISIBLE);
                break;

            case WebviewFloatActionSetting.BOTTOM_RIGHT:

                RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) mWebActionFloatView.getLayoutParams();
                layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                mWebActionFloatView.setLayoutParams(layoutParams2);

                mWebActionFloatView.setVisibility(View.VISIBLE);

                break;
        }
    }

    private boolean isAlwaysShowClose() {
        if(!mNeedClose) {
            return false;
        }

        return AtworkConfig.WEBVIEW_CONFIG.isAlwaysShowClose();
    }

    private void refreshRedEnvelopeThemeTitleBarView(String theme) {
        if ("red_envelope".equals(theme)) {
            mBackView.setImageResource(R.mipmap.icon_back_white);
            int redEnvelopeRed = ContextCompat.getColor(WebViewActivity.this, R.color.wallet_light_red);
            mTitleBarLayout.setBackgroundColor(redEnvelopeRed);

            StatusBarUtil.setStatusBarMode(WebViewActivity.this, false);
            StatusBarUtil.setColorNoTranslucent(WebViewActivity.this, redEnvelopeRed);

            int textColor = ContextCompat.getColor(WebViewActivity.this, R.color.white);
            mTitleView.setTextColor(textColor);
            mTvCloseView.setTextColor(textColor);

            mHasChangedStatusBar = true;

        }
    }



    protected Fragment createFragment() {
        mWebViewFragment = new WebviewFragment();
        mWebViewFragment.initBundle(this, mAppBundle, mInitLoadUrl, mHideTitle, mNeedAuth, mUseSystem, UrlHandleHelper.supportViewport(mInitLoadUrl));
        mFistOpenWeb = true;

        mAtworkWebView = mWebViewFragment;

        setOnSetWebTitleListener();

        return mWebViewFragment;
    }

    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_FINISH);
        intentFilter.addAction(ACTION_GO_BACK_FROM_WEBVIEW);
        intentFilter.addAction(ACTION_HOOKING_MODE);

        LocalBroadcastManager.getInstance(this).registerReceiver(mSideBroadcastReceiver, intentFilter);
    }

    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSideBroadcastReceiver);

    }

    private void setOnSetWebTitleListener() {
        mAtworkWebView.setOnSetWebTitleListener(new OnSetWebUiChangeListener() {
            @Override
            public void onSetWebTitle(String title, String url) {
                tryRevertTitleFixedStatus();
                setWebTitle(url, title);

            }

            @Override
            public void onUrlStart() {
                if (mErrorPage) return;


                mViewUrlWrong.setVisibility(View.GONE);
                mViewWebShow.setVisibility(View.VISIBLE);
                mTitleView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onUrlFinish(String url) {

                mNowLoadUrl = url;

                mFistOpenWeb = false;
//                mMoreView.setVisibility(View.VISIBLE);

                if (!mErrorPage) {
                    mLoadSuccess = true;
                }
            }

            @Override
            public void onUrlWrong() {
                showErrorPage();
            }

            @Override
            public void onStatusBarChange(String color) {
                StatusBarUtil.setStatusBarMode(WebViewActivity.this, true);
            }

            @Override
            public void onSetTextZoom() {
                mAtworkWebView.changeTextSize(W6sTextSizeHelper.getWebviewTextSizeSetNative());
            }

            public void setWebTitle(String url, String title) {

                if(mFistOpenWeb) {
                    if(!StringUtils.isEmpty(mTitle)) {
                        setViewTitle(mTitle);
                        //同步更新数据
                        if (mArticleItem != null && !StringUtils.isEmpty(title) && !StringUtils.isEmpty(url)) {
                            mArticleItem.title = title;
                            mArticleItem.url = url;
                        }

                        mFixedTitle = true;

                        return;
                    }

                    if (StringUtils.isEmpty(mTitle)
                            && !StringUtils.isEmpty(mSessionID)) {

                        getApp(app -> {
                            if (null != app) {
                                mTitle = app.getTitleI18n(AtworkApplicationLike.baseContext);

                                setViewTitle(mTitle);
                                //同步更新数据
                                if (mArticleItem != null && !StringUtils.isEmpty(title) && !StringUtils.isEmpty(url)) {
                                    mArticleItem.title = title;
                                    mArticleItem.url = url;
                                }

                                mFixedTitle = true;

                            }
                        });

                        return;
                    }


                    if (null != mArticleItem
                            && !StringUtils.isEmpty(mArticleItem.title)) {

                        setViewTitle(mArticleItem.title);

                        return;
                    }

                    //普通URL打开web页面 普通URL的ArticleItem是没有title的
                    if (!StringUtils.isEmpty(title)) {
                        setViewTitle(title);
                    }
                    //同步数据
                    if (null != mArticleItem) {
                        mArticleItem.title = title;
                    }

                }


                if(mFixedTitle) {
                    return;
                }

                //处理二级以上web页面
                if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(url)) {
                    setViewTitle(title);
                    //同步更新数据
                    if (null != mArticleItem) {
                        mArticleItem.title = title;
                        mArticleItem.url = url;
                    }
                    return;
                }



            }
        });
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {

            //有 down 事件进来, 才处理
            if (KeyEvent.ACTION_DOWN == event.getAction()) {
                mBackKeyEventHasDown = true;

            } else if (KeyEvent.ACTION_UP == event.getAction()) {

                //拦截下来
                if (!mBackKeyEventHasDown) {
                    return true;
                }

                //revert
                mBackKeyEventHasDown = false;
            }

            if (mCanCallBackKeyEvent) {
                mCanCallBackKeyEvent = false;
                //1秒钟之内只能处理1次Key_Back 的时间, 该处除了人为快速按返回外, 也防止系统回调2次的机制
                mBackView.postDelayed(() -> mCanCallBackKeyEvent = true, 1000);

                if (mHasKeyboard) {
                    AtworkUtil.hideInput(this);
                    return true;

                } else {
                    if (!mAtworkWebView.canGoBack() && mIsPreJumpUrl) {
                        HandleLoginService.getInstance().toStart(WebViewActivity.this, mHandler, 0);
                        return true;

                    }

                }

            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (webSharePopupWindow != null) {
            webSharePopupWindow.dismiss();
        }
        //界面回退动画
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    public void showCloseView() {
        if (mNeedClose) {
            getCloseView().setVisibility(View.VISIBLE);
        }
        mWebCanBack = true;

        handleTitleFloat();
    }

    @Override
    public void hiddenCloseView() {


        if (!isAlwaysShowClose()) {
            getCloseView().setVisibility(View.GONE);
        }
        mWebCanBack = false;

        handleTitleFloat();
    }

    @Override
    public boolean handleSchemaUrlJump(Context context, String url) {
        return SchemaUrlJumpHelper.handleUrl(context, url);

    }

    @Override
    public void registerShake() {
        mRegisterShake = true;
        startShakeListening();
    }

    @Override
    public void unregisterShake() {
        mRegisterShake = false;
        stopShakeListening();
    }



    @Override
    public void onFragmentCreate() {
        handleInitData();

    }

    @Override
    public void finish() {
        AtworkUtil.hideInput(this);

        super.finish(true);


    }

    private void setTitleMax() {

        View closeView = getCloseView();
        if (closeView instanceof TextView) {
            mLeftAreaWidth = mBackView.getMeasuredWidth() + ViewUtil.getTextLength((TextView) closeView);

        } else if(closeView instanceof ImageView){
            //72 为icon_close_webivew 的宽度
            mLeftAreaWidth = mBackView.getMeasuredWidth() + 72;

        }

        int titleMax = ScreenUtils.getScreenWidth(WebViewActivity.this)
                - mLeftAreaWidth
                - DensityUtil.dip2px(35 + 20);
        mTitleView.setWidth(titleMax);
    }

    private void setViewTitle(String title) {
        if (!StringUtils.isEmpty(title)) {
            if (PatternUtils.isUrlLink(title)) {
                return;
            }
        }
        mTitleView.setText(title);

        if (null != title) {
            handleTitleFloat();
        }
    }

    private void handleTitleFloat() {
        //加多30伪造长度, 以免与"关闭"挨得太劲
        int titleTotalLength = ViewUtil.getTextLength(mTitleView) + DensityUtil.dip2px(30);
        //计算当"关闭"出现时, 文字居中会出现重叠的情况
        if (getCloseView().isShown() && (titleTotalLength / 2 + mLeftAreaWidth) > ScreenUtils.getScreenWidth(WebViewActivity.this) / 2) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTitleView.getLayoutParams();
            layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.button_group_left);
            layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.rl_rightest);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
            mTitleView.setLayoutParams(layoutParams);
            return;

        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTitleView.getLayoutParams();
        layoutParams.addRule(RelativeLayout.RIGHT_OF, -1);
        layoutParams.addRule(RelativeLayout.LEFT_OF, -1);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        mTitleView.setLayoutParams(layoutParams);

    }


    private void initTitleMax() {
        getCloseView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                setTitleMax();
                getCloseView().getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    public void setTitleBarStatus(boolean lock) {
        mBackView.setEnabled(lock);
    }

    @Override
    public void onTitleChange(final String title) {
        new Handler().postDelayed(() -> runOnUiThread(() -> setViewTitle(title)), 100);
    }

    @Override
    public void onLeftButtonChange(String leftButtonAction) {
        mNewBackAction = leftButtonAction;
    }

    @Override
    public void onRightButtonChange(final JSONArray jsonArray) {

        new Handler().postDelayed(() -> {
            runOnUiThread(() -> {
                mRightButtons.setVisibility(View.VISIBLE);
                try {
                    if(jsonArray.get(0) instanceof JSONArray){
                        final JSONArray iconGroups = jsonArray.getJSONArray(0);
                        final JSONObject iconObject = iconGroups.getJSONObject(0);
                        int flag = 0;
                        if(iconObject.has("is_keep_original_menus")){
                            if (iconObject.get("is_keep_original_menus") instanceof Integer){
                                flag = iconObject.getInt("is_keep_original_menus");
                            }
                        }
                        List<List<WebRightButton>> rightButtons = mRightButtons.setupWebRightButtonsFromJson(jsonArray, WebViewActivity.this);
                        if (rightButtons.isEmpty()) {
                            return;
                        }
                        mRightButtons.isLightApp(isLightApp());
                        //mRightButtons.isLightApp(true);
                        mRightButtons.setFlag(flag);
                        mRightButtons.setWebRightButton(rightButtons);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }, 20);
    }

    @Override
    public void onRightButtonChange1(final JSONArray jsonArray) {

        new Handler().postDelayed(() -> {
            runOnUiThread(() -> {
                mRightButtons.setVisibility(View.VISIBLE);
                try {
                    if(jsonArray.get(0) instanceof JSONObject){
                        final JSONObject iconGroups = jsonArray.getJSONObject(0);
                        JSONArray jsonArray1 = iconGroups.getJSONArray("data");
                        int flag = 0;
                        if(iconGroups .has("flag")){
                            if (iconGroups.get("flag") instanceof Integer){
                                flag = iconGroups.getInt("flag");
                            }
                        }
                        List<List<WebRightButton>> rightButtons = mRightButtons.setupWebRightButtonsFromJson(jsonArray1, WebViewActivity.this);
                        if (rightButtons.isEmpty()) {
                            return;
                        }
                        mRightButtons.isLightApp(isLightApp());
                        //mRightButtons.isLightApp(true);
                        mRightButtons.setFlag(flag);
                        mRightButtons.setWebRightButton(rightButtons);
                    }
                    else if(jsonArray.get(0) instanceof JSONArray){
                        List<List<WebRightButton>> rightButtons = mRightButtons.setupWebRightButtonsFromJson(jsonArray, WebViewActivity.this);
                        if (rightButtons.isEmpty()) {
                            return;
                        }
                        mRightButtons.setWebRightButton(rightButtons);
                    }
                    else{
                        AtworkToast.showToast("参数错误！");
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }

            });

        }, 20);
    }

    private boolean isLightApp() {
        return null != mAppBundle && mAppBundle.isLightAppBundle();
    }

    @Override
    public void onRightButtonReset(Activity activity, WebViewPlugin.ActionCallbackListener listener) {
        new Handler().postDelayed(() -> {
            runOnUiThread(() -> {
                mRightButtons.resetShowUp();
                listener.onActionSuccess();
            });

        }, 100);
    }

    @Override
    public void onLeftButtonReset(Activity activity, WebViewPlugin.ActionCallbackListener listener) {
        new Handler().postDelayed(() -> {
            runOnUiThread(() -> {
                mNewBackAction = "";
                if (!CustomerHelper.isRuYuan(activity)) {
                    mBackView.setVisibility(View.VISIBLE);
                }
                mLeftButtons.resetShowUp();
                mLeftButtons.setVisibility(View.GONE);
                listener.onActionSuccess();
            });

        }, 100);

    }

    @Override
    public void onChangeLeftButton(JSONArray jsonArray) {
        new Handler().postDelayed(() -> {
            runOnUiThread(() -> {
                mBackView.setVisibility(View.GONE);
                mLeftButtons.setVisibility(View.VISIBLE);
                List<List<WebRightButton>> rightButtons = mLeftButtons.setupWebRightButtonsFromJson(jsonArray, WebViewActivity.this);
                if (rightButtons.isEmpty()) {
                    return;
                }

                mLeftButtons.setWebRightButton(rightButtons);
            });

        }, 20);


    }

    @Override
    public void onTitleBarLock(final boolean isLock) {
        new Handler().postDelayed(() -> runOnUiThread(() -> {
            if (isLock) {
                ViewCompat.setAlpha(mBackView, 0.5f);
                ViewCompat.setAlpha(mRightButtons, 0.5f);
                ViewCompat.setAlpha(getCloseView(), 0.5f);
                ViewCompat.setAlpha(mLeftButtons, 0.5f);

            } else {
                ViewCompat.setAlpha(mBackView, 1);
                ViewCompat.setAlpha(mRightButtons, 1);
                ViewCompat.setAlpha(getCloseView(), 1);
                ViewCompat.setAlpha(mLeftButtons, 1);
            }
            mBackView.setClickable(!isLock);
            mRightButtons.setClickable(!isLock);
            getCloseView().setClickable(!isLock);

            mRightButtons.lockButton(!isLock);
            mLeftButtons.lockButton(!isLock);
        }), 100);

    }

    @Override
    public void onLeftButtonVisible(final boolean showBack, final boolean showClose) {
        runOnUiThread(() -> {
            mNeedClose = showClose;
            if (!CustomerHelper.isRuYuan(this)) {
                mBackView.setVisibility(showBack ? View.VISIBLE : View.GONE);
            }

            getCloseView().setVisibility(showClose ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void setWatermark(boolean show, String orgId, String textColor, int textSize, int paddingTop, double alpha, String addValue) {
        runOnUiThread(() -> {
            if (!show) {
                mAtworkWebView.showWatermark(false);
                return;
            }
            LoginUserBasic meUser = LoginUserInfo.getInstance().getLoginUserBasic(this);
            String name = meUser.mName;
            String num = meUser.mUsername;
            if (!TextUtils.isEmpty(orgId)) {
                Employee employee = EmployeeManager.getInstance().queryEmpInSync(this, meUser.mUserId, orgId);
                //如果无法找到雇员名称，则使用当前登录的用户名
                if (employee != null) {
                    name = employee.name;
                }
            }
            mAtworkWebView.setWatermark(show, name, num, textColor, textSize, paddingTop, alpha, addValue);
        });

    }

    @Override
    public void setForwardMode(String forwardMode) {
        mForwardMode = forwardMode;
    }


    @Override
    public void onScriptAction(String action) {
        mAtworkWebView.loadJS(action);
    }

    @Override
    public void onSystemAction(String action) {
        if ("share".equalsIgnoreCase(action)) {
            popWebShareWin();

        } else if ("refresh".equalsIgnoreCase(action)) {
            tryRevertTitleFixedStatus();
            mAtworkWebView.reload();

        } else if ("close".equalsIgnoreCase(action)) {
            finish();

        } else if ("pop_default".equalsIgnoreCase(action)) {
            popDefaultAction();

        } else if("reload".equalsIgnoreCase(action)){
            mAtworkWebView.reload();

        } else if ("app_info".equalsIgnoreCase(action)) {
            if(!isLightApp()){
                AtworkToast.showToast("该网页非轻应用！");
            }else{
                Fragment fragment = (Fragment) mAtworkWebView;
                Intent intent = ChatInfoActivity.getIntent(SessionType.LightApp, mAppBundle.mBundleId, mAppBundle.appDomainId, mAppBundle.mOrgId);
                fragment.startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (isBeeworksShareAndroidEnable()) {
            if (mTencent == null) {
                mTencent = Tencent.createInstance(AtworkConfig.QQ_APP_ID, BaseApplicationLike.baseContext);
            }
            mTencent.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean isBeeworksShareAndroidEnable() {
        boolean enable = false;
        try {
            enable = BeeWorks.getInstance().config.beeWorksShare.mShareQQ.mShareAndroid.enable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enable;
    }

    @Override
    public void changeStatusBar() {
        boolean isCustomTitle = null != mAppBundle && BannerType.CUSTOM_PIC.equals(mAppBundle.mSettings.mMobileBehaviour.mBannerType);

        if (!mHideTitle && !isCustomTitle && mNeedChangeStatusBar && !mHasChangedStatusBar) {
            super.changeStatusBar();

        }
    }

    @Override
    public boolean shouldInterceptOnStart() {
        if (mNeedIntercept) {
            return super.shouldInterceptOnStart();
        }

        return false;
    }

    public static void destroy() {
        Intent intent = new Intent(WebViewActivity.ACTION_FINISH);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
    }

    public static void goBackFromWebview() {
        if (1 < sWebActivityQueue.size()) {
            Intent intent = new Intent(ACTION_GO_BACK_FROM_WEBVIEW);
            String lastTarget = sWebActivityQueue.get(sWebActivityQueue.size() - 2);
            intent.putExtra(DATA_TARGET_WEBVIEW, lastTarget);
            LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
        }

    }


    public static void setHookingFloatMode(boolean hookingFloatMode) {
        Intent intent = new Intent(WebViewActivity.DATA_HOOKING_MODE);
        intent.putExtra(DATA_HOOKING_MODE, hookingFloatMode);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //FIXME 多种WEBVIEW模式，会引发BUG，后续需要对onSaveInstanceState进行详细的了解以修正BUG
//        super.onSaveInstanceState(outState);
    }


    public String getAppId() {
        if(null != mAppBundle) {
            return mAppBundle.mBundleId;
        }

        return mSessionID;
    }

    @Override
    public boolean commandProtected(@NotNull Function2<? super String, ? super Boolean, Unit> getResult) {
        getApp(app -> {
            if(null != app) {
                getResult.invoke(app.getId(), app.isNeedBioAuthProtect());
            } else {
                getResult.invoke(StringUtils.EMPTY, false);

            }
        });

        return true;
    }

    @Override
    public void onUndoMsgReceive(UndoEventMessage undoEventMessage) {
        if(null == mArticleItem) {
            return;
        }

        if(undoEventMessage.isMsgUndo(mArticleItem.msgId)) {
            showUndoDialog(this, undoEventMessage);

        }

    }




    private void startShakeListening() {

        mCanShake = true;


        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (null == mShakeDetector) {

            mShakeDetector = new ShakeDetector(() -> {
                try {

                    if (mCanShake) {

                        /*if(PersonalShareInfo.getInstance().getOpenRobotShark(BaseApplicationLike.baseContext)){
                            mCanShake = false;
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, RobotActivity.class);
                            startActivity(intent);
                            return;
                        }*/

                        mCanShake = false;

                        LogUtil.e("onWorkplusShake()");
                        mAtworkWebView.loadJS("onWorkplusShake()");

                        Runnable runnable = () -> mCanShake = true;
                        mHandler.postDelayed(runnable, 500);
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

    public void filePreviewOnline(CallbackContext callbackContext){
        if(mWebViewControlAction != null && mWebViewControlAction.filePreviewOnlineData != null) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("file_name", mWebViewControlAction.filePreviewOnlineData.getFileName());
                jsonObject.put("file_id", mWebViewControlAction.filePreviewOnlineData.getFileId());
                jsonObject.put("file_type", mWebViewControlAction.filePreviewOnlineData.getFileType());
                jsonObject.put("file_download_url", mWebViewControlAction.filePreviewOnlineData.getFileDownloadUrl());
                String jsonData = StringEscapeUtils.unescapeJson(jsonObject.toString());

                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonData);
                callbackContext.sendPluginResult(pluginResult);
                callbackContext.success();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void dismissWebSharePopupWindow() {
        try {
            if (webSharePopupWindow != null) {
                webSharePopupWindow.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void requestCordovaAuth(){
        final AtworkAlertDialog alertDialog = new AtworkAlertDialog(this, AtworkAlertDialog.Type.CLASSIC);
        alertDialog.setTitleText(R.string.cordova_auth_title);
        alertDialog.setContent(R.string.cordova_auth_request);
        alertDialog.setBrightBtnText(R.string.cordova_auth_allow);
        alertDialog.setDeadBtnText(R.string.cordova_auth_disallow);
        alertDialog.setCancelable(true);
        alertDialog.setClickDeadColorListener(dialog -> {
            injectType = CordovaInjectType.InjectDisallow;
            alertDialog.dismiss();
            AtworkToast.showResToast(R.string.cordova_auth_not_allow_tip);
        });
        alertDialog.setClickBrightColorListener(dialog -> {
            injectType = CordovaInjectType.InjectAllow;
            mAtworkWebView.reload();
        });
        alertDialog.show();
    }
}
