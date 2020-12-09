package com.foreveross.atwork.modules.aboutme.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreverht.workplus.ui.component.popUpView.W6sPopUpView;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.Employee.EmployeeAsyncNetService;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.api.sdk.util.LightNoticeHelper;
import com.foreveross.atwork.broadcast.NetworkBroadcastReceiver;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.db.daoService.OrganizationDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.Shortcut;
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppKind;
import com.foreveross.atwork.infrastructure.model.biometricAuthentication.BiometricAuthenticationProtectItemType;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.MapUtil;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.OrganizationSettingsHelper;
import com.foreveross.atwork.modules.aboutatwork.activity.AboutAtWorkActivity;
import com.foreveross.atwork.modules.aboutme.activity.MyAccountActivity;
import com.foreveross.atwork.modules.aboutme.adapter.MeFunctionsAdapter;
import com.foreveross.atwork.modules.aboutme.model.ListItemType;
import com.foreveross.atwork.modules.aboutme.model.MeFunctionItem;
import com.foreveross.atwork.modules.aboutme.model.ShortcutItem;
import com.foreveross.atwork.modules.app.util.AppRefreshHelper;
import com.foreveross.atwork.modules.common.fragment.ImageGuidePageDialogFragment;
import com.foreveross.atwork.modules.common.lightapp.LightNoticeMapping;
import com.foreveross.atwork.modules.common.lightapp.SimpleLightNoticeMapping;
import com.foreveross.atwork.modules.contact.activity.ContactActivity;
import com.foreveross.atwork.modules.downLoad.activity.MyDownLoadActivity;
import com.foreveross.atwork.modules.dropbox.activity.DropboxActivity;
import com.foreveross.atwork.modules.main.data.TabNoticeManager;
import com.foreveross.atwork.modules.main.helper.NetworkErrorViewManager;
import com.foreveross.atwork.modules.main.helper.OrgSwitcherHelper;
import com.foreveross.atwork.modules.main.helper.TabHelper;
import com.foreveross.atwork.modules.route.model.ActivityInfo;
import com.foreveross.atwork.modules.search.util.SearchHelper;
import com.foreveross.atwork.modules.setting.activity.EmailSettingActivity;
import com.foreveross.atwork.modules.setting.activity.W6sTopSettingActivity;
import com.foreveross.atwork.support.NoticeTabAndBackHandledFragment;
import com.foreveross.atwork.tab.helper.BeeworksTabHelper;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.IntentUtil;
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper;
import com.foreveross.theme.model.Theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.foreveross.atwork.cordova.plugin.HTMemberPlugin.getGetSignature;
import static com.foreveross.atwork.infrastructure.BaseApplicationLike.baseContext;

/**
 * Created by lingen on 15/3/20.
 * Description: 关于我的Fragment
 */
public class AboutMeFragment extends NoticeTabAndBackHandledFragment implements NetworkBroadcastReceiver.NetworkChangedListener {

    public static final String TAB_ID = "me";

    private static final String TAG = AboutMeFragment.class.getSimpleName();

    public static final String ACTION_DATA_REFRESH = "action_data_refresh";

    public static final String ACTION_EMAIL_REFRESH = "action_email_refresh";

    public static String ACTION_CHECK_UPDATE_NOTICE = "action_check_update_notice";

    public static String ACTION_VPN_STATUS_REFRESH = "action_vpn_status_refresh";

    public static String ACTION_REFRESH_ABSOLUTELY = "ACTION_REFRESH_ABSOLUTELY";

    public static String DATA_IS_LOADING = "data_is_loading";

    private static final int REQUEST_CODE_MY_ACCOUNT = 10001;

    private ListView mFunctionListView;
    private MeFunctionsAdapter mMeFunctionsAdapter;
    private LinearLayout mHeaderLayout;
    private ImageView mImageViewAvatar;
    private TextView mOrgNameView;
    private TextView mJobTitleView;
    private TextView mNameView;
    private TextView mTvIpOpenTip;
    private RelativeLayout mlLJobArea;
    private ImageView mIvMeTabGuide;
    private ImageView mIvOrgSwitcher;

    private TreeMap<Integer, ArrayList<Shortcut>> mShortcutGroup = new TreeMap<>();

    private Map<String, LightNoticeMapping> mAppLightNoticeMap = new HashMap<>();
    private Map<String, LightNoticeMapping> mColleagueLightNoticeMap = new HashMap<>();

    private View mTitleBar;
    private View mVFakeStatusBar;
    private TextView mTitleView;
    private ImageView mMoreView;
    private ImageView mOrgSwitcher;

    private ProgressDialogHelper mProgressDialogHelper;

    private NetworkErrorViewManager mNetworkErrorViewManager;

    public BroadcastReceiver mRefreshDataBdcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (ACTION_DATA_REFRESH.equals(action)) {
                //refresh data
                refreshData();

            } else if (AppRefreshHelper.ACTION_REFRESH_APP.equals(action)) {

                refreshShortcut();
            } else if (AppRefreshHelper.ACTION_REFRESH_APP_LIGHTLY.equals(action)) {

                refreshAdapterLightly();

            } else if (ACTION_REFRESH_ABSOLUTELY.equals(action)) {
                refreshAdapterAbsolutely();

            }

        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        registerBroadcast();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_me, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();

        refreshUserMsg();

        refreshShortcut();

        mNetworkErrorViewManager.registerImReceiver(mActivity);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!MapUtil.isEmpty(mShortcutGroup)) {
            loadLightAppNotices(mAppLightNoticeMap, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        OrgSwitcherHelper.checkShowOrgSwitcher(mOrgSwitcher);
        checkShortcuts();
        refreshUserMsg();
        refreshColleagueLightNoticeModel();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected View getFakeStatusBar() {
        return mVFakeStatusBar;
    }

    public void refreshShortcut() {
        if (!StringUtils.isEmpty(PersonalShareInfo.getInstance().getCurrentOrg(getActivity()))) {
            mShortcutGroup.clear();
            mShortcutGroup.putAll(AppManager.getInstance().getShortcutGroup());

        } else {
            mShortcutGroup.clear();

        }

        refreshAdapterAbsolutely();

        refreshAppsLightNoticeModel();
    }


    private BroadcastReceiver mVpnResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(ACTION_VPN_STATUS_REFRESH)) {
                boolean result = intent.getBooleanExtra(DATA_IS_LOADING, false);

                if (!result) {
                    mMeFunctionsAdapter.dismissProgressDialog();
                }

                mMeFunctionsAdapter.setShowing(result);

                refreshAdapterLightly();
            }
        }
    };

    private BroadcastReceiver mRegisterCheckUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(ACTION_CHECK_UPDATE_NOTICE)) {
                checkUpdateAndUpdateNotice();

            }
        }
    };


    private AdapterView.OnItemClickListener mOnItemClickListener = (parent, view, position, id) -> {

        if(CommonUtil.isFastClick(800)) {
            return;
        }

        MeFunctionItem item = (MeFunctionItem) parent.getItemAtPosition(position);

        String userId = LoginUserInfo.getInstance().getLoginUserId(mActivity);

        final FragmentActivity activity = getActivity();

        //跳转到我的下载页面
        if(ListItemType.DOWNLOAD == item.mListItemType) {
            Intent intent = MyDownLoadActivity.getIntent(mActivity);
            startActivity(intent);
            return;
        }

        //跳转到关于atwork页面
        if (ListItemType.ABOUT == item.mListItemType) {
            Intent intent = AboutAtWorkActivity.getIntent(mActivity);
            intent.putExtra("aboutName", item.getTitle(baseContext));
            startActivity(intent);
            return;
        }

        //跳转到设置页
        if (ListItemType.SETTING == item.mListItemType) {
            Intent intent = W6sTopSettingActivity.getIntent(mActivity);
            startActivity(intent);
            return;
        }

        //跳转到同事圈
        if (ListItemType.CIRCLE == item.mListItemType) {

            String url = UrlConstantManager.getInstance().getColleagueCircleUrl(PersonalShareInfo.getInstance().getCurrentOrg(mActivity));
//            String url = "file:///android_asset/www/index_2.1.html";
            WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                    .setUrl(url)
                    .setNeedClose(false)
                    .setNeedShare(false)
                    .setFrom(WebViewControlAction.FROM_MOMENTS);

            if(AtworkConfig.USE_V2_COLLEAGUE_CIRCLE) {
                webViewControlAction.setHideTitle(true);
            }

            ActivityInfo nextActivityInfo = new ActivityInfo(null, DropboxActivity.class.getName(), ListUtil.makeSingleList(BiometricAuthenticationProtectItemType.CIRCLE.transferToActivityTag()));

            return;
        }

        if (ListItemType.CALENDAR == item.mListItemType) {
            IntentUtil.startSystemCalendar(mActivity);
            return;
        }

        if (ListItemType.CONTACT == item.mListItemType) {
            Intent intent = ContactActivity.getIntent(mActivity);
            startActivity(intent);
            return;
        }

        if (ListItemType.MAIL_SETTING == item.mListItemType) {
            EmailSettingActivity.startEmailSettingActivity(mActivity);
            return;
        }

        if (ListItemType.DROPBOX == item.mListItemType) {
            //String userId = LoginUserInfo.getInstance().getLoginUserId(mActivity);
            Intent intent = DropboxActivity.getIntent(mActivity, Dropbox.SourceType.User, userId, AtworkConfig.DOMAIN_ID);

            ActivityInfo nextActivityInfo = new ActivityInfo(null, DropboxActivity.class.getName(), ListUtil.makeSingleList(BiometricAuthenticationProtectItemType.DROPBOX.transferToActivityTag()));

            return;
        }

        if (ListItemType.SHORTCUT == item.mListItemType) {
            ShortcutItem shortcutItem = (ShortcutItem) item;


            AppManager.getInstance().queryApp(activity, shortcutItem.mShortcut.mAppId, PersonalShareInfo.getInstance().getCurrentOrg(activity), new AppManager.GetAppFromMultiListener() {
                @Override
                public void onSuccess(@NonNull App app) {

                    SearchHelper.handleSearchResultCommonClick(activity, StringUtils.EMPTY, app, null, null);

                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
                }
            });
        }

    };

    /**
     * Description:根据返回的数据判断是否进入进入我的会员
     * @param httpResult
     * @return
     */
    private Boolean isOpenMyIntegral(HttpResult httpResult){
        if(httpResult.isNetSuccess()){
            String result = httpResult.result;
            String status = result.substring(10, result.indexOf(","));
            if(status.equalsIgnoreCase("0")){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }

    }

    private View.OnClickListener mOnClickListener = v -> {
        Intent intent = new Intent(mActivity, MyAccountActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MY_ACCOUNT);
        //界面切换效果
        mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    };


    public void registerBroadcast() {
        LocalBroadcastManager.getInstance(baseContext).registerReceiver(mRegisterCheckUpdateReceiver, new IntentFilter(ACTION_CHECK_UPDATE_NOTICE));

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DATA_REFRESH);
        filter.addAction(ACTION_EMAIL_REFRESH);
        filter.addAction(AppRefreshHelper.ACTION_REFRESH_APP);
        filter.addAction(AppRefreshHelper.ACTION_REFRESH_APP_LIGHTLY);
        filter.addAction(ACTION_REFRESH_ABSOLUTELY);

        LocalBroadcastManager.getInstance(baseContext).registerReceiver(mRefreshDataBdcast, filter);

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mVpnResultReceiver, new IntentFilter(ACTION_VPN_STATUS_REFRESH));
    }

    public void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mVpnResultReceiver);
    }

    public static void refreshVpnLoadingStatus(boolean isShowing) {
        Intent intent = new Intent(ACTION_VPN_STATUS_REFRESH);
        intent.putExtra(DATA_IS_LOADING, isShowing);
        LocalBroadcastManager.getInstance(baseContext).sendBroadcast(intent);

    }

    public static void refreshUserMsg() {
        LocalBroadcastManager.getInstance(baseContext).sendBroadcast(new Intent(ACTION_DATA_REFRESH));
    }

    public static void refreshAbsolutely() {
        LocalBroadcastManager.getInstance(baseContext).sendBroadcast(new Intent(ACTION_REFRESH_ABSOLUTELY));

    }

    public void checkShortcuts() {
        if (StringUtils.isEmpty(PersonalShareInfo.getInstance().getCurrentOrg(getActivity()))) {
            //clear
            mShortcutGroup.clear();
            refreshAdapterAbsolutely();
            refreshAppsLightNoticeModel();

        } else {
            checkShortcutsOnNoAppFragment();
        }

    }

    private void checkShortcutsOnNoAppFragment() {
        if (!TabHelper.hasAppFragment()) {
            if (MapUtil.isEmpty(mShortcutGroup)) {
                AppRefreshHelper.refreshAppAbsolutely();
            }
        }
    }

    private void refreshData() {
        if(AtworkConfig.ABOUT_ME_CONFIG.isHideMoreBtn()) {
            mMoreView.setVisibility(View.GONE);

        } else {
            mMoreView.setVisibility(View.VISIBLE);

        }

        refreshJobAreaView();

        AtworkApplicationLike.getLoginUser(new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
            }

            @Override
            public void onSuccess(@NonNull User loginUser) {
                refreshView(loginUser);
            }


        });


        checkUpdateAndUpdateNotice();
    }

    private void refreshJobAreaView() {
        if(!AtworkConfig.EMPLOYEE_CONFIG.getShowSelfJobTitle()) {
            mlLJobArea.setVisibility(View.GONE);
            return;
        }

        OrganizationDaoService.getInstance().queryOrgCount(LoginUserInfo.getInstance().getLoginUserId(mActivity), count -> {
            mlLJobArea.setVisibility(count < 1 ? View.GONE : View.VISIBLE);
        });
    }

    private void checkUpdateAndUpdateNotice() {
        if (null != mActivity) {
            LightNoticeMapping lightNoticeModel = SimpleLightNoticeMapping.createInstance(mId, TabNoticeManager.APP_ID_ABOUT_ME_CHECK_UPDATE);
            LightNoticeData lightNoticeData;

            if (AtworkUtil.isFoundNewVersion(mActivity)) {
                lightNoticeData = LightNoticeData.createDotLightNotice();

            } else {
                lightNoticeData = LightNoticeData.createNothing();
            }
            TabNoticeManager.getInstance().update(lightNoticeModel, lightNoticeData);

            refreshAdapterLightly();
        }

    }


    private void refreshView(User user) {
        if (user == null) {
            Logger.d(TAG, "user is null..");
            return;
        }
        mNameView.setText(user.getShowName());
        ImageCacheHelper.displayImageByMediaId(user.mAvatar, mImageViewAvatar, ImageCacheHelper.getMyAccountAvatarOptions());

        refreshShowJobTitle(user);

    }

    private void refreshShowJobTitle(User user) {
        if (jobTitleViewShowNothing()) {
            mOrgNameView.setText(StringUtils.EMPTY);
            mJobTitleView.setText(StringUtils.EMPTY);//当前没有组织 code, 显示空职位

        } else {
            String orgCode = PersonalShareInfo.getInstance().getCurrentOrg(mActivity);
            EmployeeManager.getInstance().queryEmp(mActivity, user.mUserId, orgCode, new EmployeeAsyncNetService.QueryEmployeeInfoListener() {
                @Override
                public void onSuccess(@NonNull Employee employee) {
                    mJobTitleView.setText(employee.getPositionThreeShowStr());
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleTokenError(errorCode, errorMsg);

                }
            });


            OrganizationManager.getInstance().getLocalCurrentOrg(mActivity, org -> {
                mOrgNameView.setText(org.getNameI18n(baseContext));
            });

        }

    }

    private boolean jobTitleViewShowNothing() {

        String orgCode = PersonalShareInfo.getInstance().getCurrentOrg(mActivity);
        return TextUtils.isEmpty(orgCode);
    }


    @Override
    protected void findViews(View view) {
        mTitleBar = view.findViewById(R.id.about_me_title_bar);
        mIvMeTabGuide = view.findViewById(R.id.iv_me_tab_guide);
        mTitleView = mTitleBar.findViewById(R.id.title_bar_main_title);
        mVFakeStatusBar = mTitleBar.findViewById(R.id.v_fake_statusbar);
        mMoreView = mTitleBar.findViewById(R.id.titlebar_main_more_btn);
        mOrgSwitcher = mTitleBar.findViewById(R.id.org_switcher);
        mOrgSwitcher.setVisibility(View.GONE);
        mNetworkErrorViewManager = new NetworkErrorViewManager(mTitleBar);

        mFunctionListView = view.findViewById(R.id.me_function_items);
        mTvIpOpenTip = view.findViewById(R.id.tv_h3_ip_tip);
        addHeaderView();

        mMeFunctionsAdapter = new MeFunctionsAdapter(mActivity);
        mMeFunctionsAdapter.setShortcutGroup(mShortcutGroup);
        mFunctionListView.setAdapter(mMeFunctionsAdapter);

        mProgressDialogHelper = new ProgressDialogHelper(getActivity());



//                mHeaderLayout.setOnClickListener(mOnClickListener);
        //取消默认的分割线
        mFunctionListView.setDivider(null);
    }

    private void registerListener() {

        mIvMeTabGuide.setOnClickListener(v -> mIvMeTabGuide.setVisibility(View.GONE));

        mFunctionListView.setOnItemClickListener(mOnItemClickListener);

        mMoreView.setOnClickListener(view -> {
//            MoreViewPopupHelper.onQRClick(mActivity);
            popWinView();
//            CrashReport.testJavaCrash();
//            DiscussionMemberRoleSettingActivity.Companion.startActivity(mActivity);
        });

        mOrgSwitcher.setOnClickListener(view -> {

        });

        OrgSwitcherHelper.checkShowOrgSwitcher(mIvOrgSwitcher);
        mIvOrgSwitcher.setOnClickListener(view -> {
            //防抖动
            if (CommonUtil.isFastClick(2000)) {
                return;
            }
            OrgSwitcherHelper.setOrgPopupView(mActivity, this);
        });
    }

    private void popWinView() {
        OrganizationManager.getInstance().queryLoginOrgCodeListTryCache(result -> {

            W6sPopUpView w6sPopUpView = new W6sPopUpView(getActivity());
            w6sPopUpView.addPopItem(-1, R.string.modify_personal_info, 0);
            if(null != result && 1 < result.size()) {
                w6sPopUpView.addPopItem(-1, R.string.switch_current_org, 1);

            }

            w6sPopUpView.setPopItemOnClickListener((title, pos) -> {
                if (title.equals(getStrings(R.string.modify_personal_info))) {
                    Intent intent = new Intent(mActivity, MyAccountActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_MY_ACCOUNT);
                    //界面切换效果
                    mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                    w6sPopUpView.dismiss();
                    return;
                }

                if (title.equals(getStrings(R.string.switch_current_org))) {
                    OrgSwitcherHelper.setOrgPopupView(mActivity, this);
                    w6sPopUpView.dismiss();

                    return;
                }
            });

            w6sPopUpView.pop(mMoreView);

        });




    }

    /**
     * 需放在listView.setAdapter()前
     */
    private void addHeaderView() {
        View headerView = LayoutInflater.from(mActivity).inflate(R.layout.header_about_me_new, null);
        mHeaderLayout = headerView.findViewById(R.id.me_header_layout);
        mlLJobArea = headerView.findViewById(R.id.ll_job_area);
        mOrgNameView = headerView.findViewById(R.id.tv_org_name);
        mJobTitleView = headerView.findViewById(R.id.me_org_job);
        mNameView = headerView.findViewById(R.id.me_name);
        mImageViewAvatar = headerView.findViewById(R.id.me_header_avatar);
        mIvOrgSwitcher = headerView.findViewById(R.id.iv_org_switcher);
        headerView.setOnClickListener(mOnClickListener);
        mFunctionListView.addHeaderView(headerView);

        WorkplusTextSizeChangeHelper.handleViewEnlargedTextSizeStatus(mImageViewAvatar, 1.1f);
    }

    public String getFragmentName() {
        try {
            String beeworksName = BeeWorks.getInstance().getBeeWorksTabById(baseContext, mId).name;
            return TextUtils.isEmpty(beeworksName) ? getStrings(R.string.item_about_me) : beeworksName;
        } catch (NullPointerException e) {
            e.printStackTrace();

        }
        return getStrings(R.string.item_about_me);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        WorkplusStatusBarHelper.setCommonStatusBar(getActivity());


        AtworkUtil.checkUpdate(mActivity, false);
        if (isVisibleToUser) {

            checkShortcuts();
            refreshData();

            checkUpdateApps();

            refreshColleagueLightNoticeModel();

            OrganizationSettingsHelper.getInstance().checkOrgSettingsUpdate(mActivity, 0);

            getSystemNavigation();

            handleMeTabFirstGuidePageShown();

        } else {
            if(null != mIvMeTabGuide) {
                mIvMeTabGuide.setVisibility(View.GONE);
            }
        }

    }

    @Override
    protected void refreshNetworkStatusUI() {
        //如果fragment显示可见
        if (mNetworkErrorViewManager != null) {
            mNetworkErrorViewManager.refreshNetworkStatusUI(NetworkStatusUtil.isNetworkAvailable(BaseApplicationLike.baseContext));
        }
    }

    public void checkUpdateApps() {

        if(!AppManager.getInstance().checkLegalRequestIdCheckAppUpdatesRemote()) {
            return;
        }

        AppManager.getInstance().addInterceptRequestIdCheckAppUpdatesRemote();

        AppManager.getInstance().getAppCheckUpdateController().checkAppsUpdate(PersonalShareInfo.getInstance().getCurrentOrg(mActivity), mAppLightNoticeMap, new AppManager.CheckAppListUpdateListener() {
            @Override
            public void refresh(boolean needUpdate) {



                if (needUpdate) {
                    AppRefreshHelper.refreshApp();

                } else {
                    //没有更新则直接检查红点
                    loadLightAppNotices(mAppLightNoticeMap, false);
                }
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                AppManager.getInstance().removeInterceptRequestCheckAppUpdatesRemote();

                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);


            }
        });
    }


    private void getSystemNavigation() {
        BeeworksTabHelper.getInstance().getSystemNavigation(mActivity, BeeWorks.getInstance().getBeeWorksTabById(mActivity, mId));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MY_ACCOUNT) {
            if (resultCode == Activity.RESULT_OK) {
                mActivity.finish();
            }
        }

    }


    @Override
    protected boolean onBackPressed() {
        return false;
    }


    public void refreshAppsLightNoticeModel() {

        resetAppLightNoticeModel();

        loadLightAppNotices(mAppLightNoticeMap, true);
    }

    public void refreshColleagueLightNoticeModel() {

        resetColleagueLightNoticeModel();

        loadLightAppNotices(mColleagueLightNoticeMap, true);
    }

    private void resetColleagueLightNoticeModel() {
        mColleagueLightNoticeMap.clear();
        TabNoticeManager.getInstance().unregisterLightNotice(mId, TabNoticeManager.getInstance().getCircleAppId(getActivity()));

        checkColleagueLightNoticeModel();
    }


    private void resetAppLightNoticeModel() {
        mAppLightNoticeMap.clear();

        checkAppsLightModel();
    }

    private void checkAppsLightModel() {
        List<App> appList = AppManager.getInstance().getAppList();
        if (!ListUtil.isEmpty(appList)) {
            for (App app : appList) {

                if (app.mAppKind.equals(AppKind.LightApp)) {
                    if (ListUtil.isEmpty(app.mBundles)) {
                        continue;
                    }
                    AppBundles bundles = app.mBundles.get(0);
                    if (bundles == null) {
                        continue;
                    }

                    if (null != app.mShortcut) {
                        if (!StringUtils.isEmpty(bundles.mNoticeUrl)) {
                            List<String> tabIds = new ArrayList<>();
                            tabIds.add(mId);
                            if (app.isShowInMarket()) {
                                tabIds.add(TabHelper.getAppFragmentId());
                            }

                            LightNoticeMapping lightNoticeModel = SimpleLightNoticeMapping.createInstance(bundles.mNoticeUrl, tabIds, app.mAppId);
                            mAppLightNoticeMap.put(app.mAppId, lightNoticeModel);

                        } else {

                            TabNoticeManager.getInstance().unregisterLightNotice(mId, app.mAppId);

                            if (app.isShowInMarket()) {
                                TabNoticeManager.getInstance().unregisterLightNotice(TabHelper.getAppFragmentId(), app.mAppId);
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkColleagueLightNoticeModel() {
        String circleAppId = TabNoticeManager.getInstance().getCircleAppId(getActivity());

        if (OrganizationSettingsManager.getInstance().handleMyCircleFeature(PersonalShareInfo.getInstance().getCurrentOrg(mActivity))) {

            if (!TextUtils.isEmpty(AtworkConfig.COLLEAGUE_URL)) {
                String tabIdId = StringUtils.EMPTY;
                if (AtworkConfig.CIRCLE_CONFIG.isNotifyShowInTab()) {
                    tabIdId = mId;
                }


                LightNoticeMapping lightNoticeModel = SimpleLightNoticeMapping.createInstance(AtworkConfig.COLLEAGUE_URL, tabIdId, circleAppId);
                mColleagueLightNoticeMap.put(lightNoticeModel.getAppId(), lightNoticeModel);
            }

        }

    }

    /**
     * 检查红点
     *
     * @param lightNoticeMap
     */
    private void loadLightAppNotices(Map<String, LightNoticeMapping> lightNoticeMap, boolean isCheckTotally) {

        if (0 != lightNoticeMap.size()) {

            //全量检查更新的时候, 先根据本地数据刷新 tab
            if (isCheckTotally) {
                TabNoticeManager.getInstance().update(mId);
            }

            for (Map.Entry<String, LightNoticeMapping> entry : lightNoticeMap.entrySet()) {
                final LightNoticeMapping lightNoticeMapping = entry.getValue();

                TabNoticeManager.getInstance().registerLightNoticeMapping(lightNoticeMapping);

                LightNoticeHelper.loadLightNotice(lightNoticeMapping.getNoticeUrl(), mActivity, new LightNoticeHelper.LightNoticeListener() {
                    @Override
                    public void success(LightNoticeData lightNoticeJson) {

                        if (TabNoticeManager.getInstance().checkLightNoticeUpdate(lightNoticeMapping.getAppId(), lightNoticeJson)) {

                            TabNoticeManager.getInstance().update(lightNoticeMapping, lightNoticeJson);
                            AppRefreshHelper.refreshAppLightly();
                        }
                    }

                    @Override
                    public void fail() {
                        TabNoticeManager.getInstance().update(mId);

                    }
                });
            }
        } else {
            TabNoticeManager.getInstance().update(mId);

        }
    }

    private void refreshAdapterAbsolutely() {
        if (mMeFunctionsAdapter != null) {
            mMeFunctionsAdapter.refreshItemList();
        }
    }

    private void refreshAdapterLightly() {
        if (mMeFunctionsAdapter != null) {
            mMeFunctionsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onThemeUpdate(Theme theme) {
        super.onThemeUpdate(theme);
        refreshAdapterLightly();
    }

    @Override
    public void onDomainSettingChange() {
        refreshAdapterAbsolutely();
    }

    @Override
    public void onOrgSettingChange() {
        refreshAdapterAbsolutely();

        refreshColleagueLightNoticeModel();
    }

    private void setTitle() {
        mTitleView.setText(getFragmentName());
    }

    @Override
    public void networkChanged(NetworkBroadcastReceiver.NetWorkType networkType) {
        if (mNetworkErrorViewManager != null) {

            LogUtil.e("refreshNetworkStatusUI  AboutMeFragment");

            mNetworkErrorViewManager.refreshNetworkStatusUI(networkType.hasNetwork());
        }
    }


    private void handleMeTabFirstGuidePageShown() {
        if(!AtworkConfig.ABOUT_ME_CONFIG.getNeedGuideByImage()) {
            return;
        }

        if(null == mIvMeTabGuide) {
            return;
        }

        if(!isAdded()) {
            return;
        }


        int shownCount = PersonalShareInfo.getInstance().getMeTabFirstGuidePageShownCount(baseContext);
        if (shownCount >= PersonalShareInfo.DEFAULT_SHOULD_MAIN_FAB_BOTTOM_SLIDE_NOTICE_FINGER_SHOWN_COUNT) {
            return;
        }

//        mIvMeTabGuide.setVisibility(View.VISIBLE);

        ImageGuidePageDialogFragment dialogFragment = new ImageGuidePageDialogFragment();
        dialogFragment.setGuideImage(R.mipmap.me_tab_guide);
        dialogFragment.show(getFragmentManager(), "MeTabGuidePage");

        PersonalShareInfo.getInstance().putMeTabFirstGuidePageShownCount(baseContext, ++shownCount);
    }
}
