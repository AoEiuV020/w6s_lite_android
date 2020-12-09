package com.foreveross.atwork.modules.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.db.service.repository.OrganizationRepository;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreverht.workplus.ui.component.textview.MediumBoldTextView;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.app.AppAsyncNetService;
import com.foreveross.atwork.api.sdk.app.model.InstallOrDeleteAppJSON;
import com.foreveross.atwork.api.sdk.app.model.InstallOrRemoveAppResponseJson;
import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;
import com.foreveross.atwork.api.sdk.util.LightNoticeHelper;
import com.foreveross.atwork.broadcast.NetworkBroadcastReceiver;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementKind;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.manager.NativeAppDownloadManager;
import com.foreveross.atwork.manager.OrganizationSettingsHelper;
import com.foreveross.atwork.manager.model.GetAppListRequest;
import com.foreveross.atwork.modules.advertisement.manager.AdvertisementManager;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.app.adapter.AppsAdapter;
import com.foreveross.atwork.modules.app.component.AdvertisementBannerCardView;
import com.foreveross.atwork.modules.app.inter.AppRemoveListener;
import com.foreveross.atwork.modules.app.model.GroupAppItem;
import com.foreveross.atwork.modules.app.util.AppRefreshHelper;
import com.foreveross.atwork.modules.common.fragment.ImageGuidePageDialogFragment;
import com.foreveross.atwork.modules.common.lightapp.LightNoticeMapping;
import com.foreveross.atwork.modules.common.lightapp.SimpleLightNoticeMapping;
import com.foreveross.atwork.modules.main.data.TabNoticeManager;
import com.foreveross.atwork.modules.main.helper.NetworkErrorViewManager;
import com.foreveross.atwork.modules.main.helper.OrgSwitcherHelper;
import com.foreveross.atwork.modules.main.helper.TabHelper;
import com.foreveross.atwork.modules.search.activity.NewSearchActivity;
import com.foreveross.atwork.modules.search.model.NewSearchControlAction;
import com.foreveross.atwork.modules.search.model.SearchContent;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.support.NoticeTabAndBackHandledFragment;
import com.foreveross.atwork.tab.helper.BeeworksTabHelper;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.theme.model.Theme;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import static android.view.View.GONE;

/**
 * Created by lingen on 15/3/20.
 * Description: 应用中心的Fragment
 */
public class AppFragment extends NoticeTabAndBackHandledFragment implements AppRemoveListener, BackHandledFragment.OnK9MailClickListener, NetworkBroadcastReceiver.NetworkChangedListener {

    public static final String TAB_ID = "app";

    private String TAG = AppFragment.class.getSimpleName();

    public static final String ACTION_REMOVE_APP = "android.intent.action.PACKAGE_REMOVED";

    public static final String ACTION_INSTALL_APP = "android.intent.action.PACKAGE_ADDED";

    public static final String KEY_CHECK_ORG_ID = "KEY_CHECK_ORG_ID";

    public static String REFRESH_APP_NOTICE = "REFRESH_APP_NOTICE";
    public static String REFRESH_APP = "REFRESH_APP";

    private static final int MSG_REFRESH_LISTVIEW_AFTER_REMOVE = 0x0011;
    private static final int MSG_REFRESH = 0x0012;
    private static final int MSG_UPDATE_NATIVE_APP_PROGRESS = 0x0013;
    private static final int MSG_NO_APPS = 0X0014;

    private RelativeLayout mRlMainArea;
    private AdvertisementBannerCardView mAdvertisementBannerCardView;
    private ListView mAppsListView;
    private RelativeLayout mRlNoApps;
    private ImageView mIvArrow;
    private ImageView mIvAppTabGuide;

    public boolean mRemoveAble;

    private AppsAdapter mAdapter;

    private List<GroupAppItem> mGroupAppItems = new ArrayList<>();
    private ConcurrentHashMap<String, LightNoticeMapping> mLightNoticeMap = new ConcurrentHashMap<>();

    private Map<String, AppBundles> mNativeRemoveList = new Hashtable<>();
    private NativeAppInstallRemovedReceiver mNativeAppInstallRemovedReceiver;

    private View mVTitleBarFixed;
    private View mVFakeStatusBarFixed;
    private MediumBoldTextView mTitleViewFixed;
    private ImageView mMoreViewFixed;
    private RelativeLayout mRlBackFloat;
    private ImageView mBackView;
    private ImageView mIvOrgSwitcherFixed;
    private ImageView mIvSearch;

    private View mVTitleBarInAdvertView;
    private MediumBoldTextView mTitleViewInAdvertView;
    private ImageView mIvOrgSwitcherInAdvertView;
    private ImageView mMoreViewInAdvertView;
    private ImageView mIvSearchInAdvertView;


    private View mVMainInOrgHeader;
    private TextView mTvOrgNameInOrgHeader;
    private ImageView mIvOrgSwitcherInOrgHeader;

    private View mVHeaderOrgInNoApps;

    private TextView mTvOrgNameInNoAppsView;
    private ImageView mIvOrgSwitcherInNoAppsView;

    private NetworkErrorViewManager mNetworkErrorViewManager;

    //用于插件调用，弹出app列表单独页面
    private String mCheckOrgId;

    private Boolean mCanOrgHeaderSee;

    //监听fragment是否进入挂起的状态(主要用于离线下载成功之后是否需要跳转)
    public static boolean isPutUp = true;
//    public static ArrayList<String> mRefreshAppIdList = new ArrayList<>();

    private BroadcastReceiver mRefreshDataBdReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isAdded()) {
                String action = intent.getAction();
                if(AppRefreshHelper.ACTION_REFRESH_APP.equals(action)) {
                    refreshAppUI();
                    setTitle();

                    return;
                }

                if(AdvertisementManager.ACTION_REFRESH_ADVERTISEMENTS.equals(action)) {

                    AdvertisementKind kind = (AdvertisementKind) intent.getSerializableExtra(AdvertisementManager.DATA_KIND);

                    if(AdvertisementKind.APP_BANNER != kind) {
                        return;
                    }

                    //refresh advertisements
                    String actionOrgCode = intent.getStringExtra(AdvertisementManager.DATA_ORG_CODE);
                    if(actionOrgCode.equals(PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext))) {
                        refreshAdvertisements();
                        handleOrgSwitcher();

                    }

                    return;
                }
            }
        }
    };

    private BroadcastReceiver mRefreshLightlyBdReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            refreshCustomAppGroupItem();

            refreshAdapter();
        }
    };


    //----------------------------handler处理消息---------------------------
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {

                case MSG_REFRESH_LISTVIEW_AFTER_REMOVE:
                    onAppListUpdate();
                    mAdapter.setRemoveAble(mRemoveAble);
                    break;

                case MSG_REFRESH:
                    refreshAdapter();

                    break;

                case MSG_UPDATE_NATIVE_APP_PROGRESS:

                    break;


            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        registerBroadcast();
        mCheckOrgId = getArguments().getString(KEY_CHECK_ORG_ID, "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        registerListener();


//        mNetworkErrorViewManager.registerImReceiver(mActivity);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handleIvNoAppArrow();
    }

    @Override
    public void onStart() {
        super.onStart();
        setTitle();
        String orgId = TextUtils.isEmpty(mCheckOrgId) ? PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext) : mCheckOrgId;
        AppRefreshHelper.refreshAppAbsolutely(orgId, false);
        if (checkAppsEmpty()) {

        }
        //检查红点
        loadLightAppNotices(mLightNoticeMap, false);

        refreshAdvertisements();
        handleOrgSwitcher();


        AdvertisementManager.INSTANCE.requestCurrentOrgBannerAdvertisementsSilently(AdvertisementKind.APP_BANNER);
        AdvertisementManager.INSTANCE.checkCurrentOrgBannerMediaDownloadSilently(AdvertisementKind.APP_BANNER);

        cancelRemove();

        mAdvertisementBannerCardView.startAutoScrollAppBanner();

    }

    @Override
    public void onPause() {
        super.onPause();
        isPutUp = false;
    }

    @Override
    public void onStop() {
        super.onStop();

        mAdvertisementBannerCardView.stopAutoScroll();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected View getFakeStatusBar() {
        return mVFakeStatusBarFixed;
    }

    /**
     * 需放在listView.setAdapter()前
     */
    private void addHeaderView() {


        mAdvertisementBannerCardView = new AdvertisementBannerCardView(mActivity);
        mVTitleBarInAdvertView = mAdvertisementBannerCardView.findViewById(R.id.app_title_bar);
        mIvOrgSwitcherInAdvertView = mAdvertisementBannerCardView.findViewById(R.id.org_switcher);
        mMoreViewInAdvertView = mAdvertisementBannerCardView.findViewById(R.id.titlebar_main_more_btn);
        mMoreViewInAdvertView.setImageResource(R.mipmap.icon_app_store);
        mTitleViewInAdvertView = mAdvertisementBannerCardView.findViewById(R.id.title_bar_main_title);
        mIvSearchInAdvertView = mAdvertisementBannerCardView.findViewById(R.id.iv_search);

        if(AtworkConfig.APP_CONFIG.isNeedAppInSearch()) {
            ViewUtil.setWidth(mIvSearchInAdvertView, DensityUtil.dip2px(40));
        } else {
            ViewUtil.setWidth(mIvSearchInAdvertView, 0);
        }


        mAppsListView.addHeaderView(mAdvertisementBannerCardView);



        View headerView = LayoutInflater.from(mActivity).inflate(R.layout.header_app, null);
        mVMainInOrgHeader = headerView.findViewById(R.id.rl_main);
        mTvOrgNameInOrgHeader = headerView.findViewById(R.id.tv_org_name);
        mIvOrgSwitcherInOrgHeader = headerView.findViewById(R.id.iv_switch_org);

        mAppsListView.addHeaderView(headerView);


    }




    /**
     * 控制"无应用"界面箭头动态对准"app store"的图标
     */
    public void handleIvNoAppArrow() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mIvArrow.getLayoutParams();
        int rightDp = DensityUtil.px2dip(33) + 10;
        layoutParams.setMargins(0, DensityUtil.dip2px(8), DensityUtil.dip2px(rightDp) - 8, 0);

        mIvArrow.setLayoutParams(layoutParams);
    }

    private void handleAppListVisible(boolean visible) {
        if (visible) {
            mRlNoApps.setVisibility(GONE);
            mAppsListView.setVisibility(View.VISIBLE);


        } else {
            mRlNoApps.setVisibility(View.VISIBLE);
            mAppsListView.setVisibility(GONE);

            showTitleBarFixedJoin();


        }


//        OrgSwitcherHelper.checkShowOrgSwitcher(mIvOrgSwitcherFixed);


    }



    private void registerListener() {

        mIvSearchInAdvertView.setOnClickListener(view -> routeAppSearch());
        mIvSearch.setOnClickListener(view -> routeAppSearch());

        mIvAppTabGuide.setOnClickListener(v -> mIvAppTabGuide.setVisibility(GONE));

        mBackView.setOnClickListener(view -> {
            finish();
        });


        mIvOrgSwitcherFixed.setOnClickListener(view -> {
            //防抖动
            if (CommonUtil.isFastClick(2000)) {
                return;
            }
            OrgSwitcherHelper.setOrgPopupView(mActivity, this);
        });


        mIvOrgSwitcherInAdvertView.setOnClickListener(view -> {
            //防抖动
            if (CommonUtil.isFastClick(2000)) {
                return;
            }
            OrgSwitcherHelper.setOrgPopupView(mActivity, this);
        });


        if (null != mIvOrgSwitcherInOrgHeader) {
            mIvOrgSwitcherInOrgHeader.setOnClickListener(v -> {
                //防抖动
                if (CommonUtil.isFastClick(2000)) {
                    return;
                }
                OrgSwitcherHelper.setOrgPopupView(mActivity, this);
            });
        }


        if (null != mIvOrgSwitcherInNoAppsView) {
            mIvOrgSwitcherInNoAppsView.setOnClickListener(v -> {
                //防抖动
                if (CommonUtil.isFastClick(2000)) {
                    return;
                }
                OrgSwitcherHelper.setOrgPopupView(mActivity, this);
            });
        }

        mMoreViewFixed.setOnClickListener(view -> {
            onMarketClick();
        });

        mMoreViewInAdvertView.setOnClickListener(view -> {
            onMarketClick();
        });

        mAppsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                LogUtil.e("onScroll  -> firstVisibleItem -> " + firstVisibleItem);

                if(!mAdvertisementBannerCardView.isPlaying()) {

                    handleSwitchIconFixedOnScroll(view, firstVisibleItem);
                    return;
                }


                handleTitleBarFloatOnScroll(view, firstVisibleItem);

            }
        });
    }

    private void routeAppSearch() {
        List<SearchContent> searchContentList = new ArrayList<>();

        NewSearchControlAction newSearchControlAction = new NewSearchControlAction();

        if (AtworkConfig.APP_CONFIG.isNeedAppInSearch()) {
            searchContentList.add(SearchContent.SEARCH_APP);
        }
        newSearchControlAction.setSearchContentList(searchContentList.toArray(new SearchContent[0]));
        newSearchControlAction.setSearchAppForTargetOrgCode(PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext));

        Intent intent = NewSearchActivity.getIntent(getActivity(), newSearchControlAction);
        startActivity(intent);
    }

    private void handleTitleBarFloatOnScroll(AbsListView view, int firstVisibleItem) {
        View childAt = view.getChildAt(firstVisibleItem);

        if (0 == firstVisibleItem && null != childAt) {

            LogUtil.e(" child top -> " + childAt.getTop() + " bottom ->  " + childAt.getBottom() + " height -> " + mVTitleBarFixed.getHeight());


            if (childAt.getBottom() >= DensityUtil.dip2px(50) + StatusBarUtil.getStatusBarHeight(AtworkApplicationLike.baseContext)) {

                hideTitleBarFixed();
                return;

            }

        }

        showTitleBarFixedCover();

    }

    private void handleSwitchIconFixedOnScroll(AbsListView view, int firstVisibleItem) {
        if(checkAppsEmpty()) {
            return;
        }

        int realFirstVisibleItem = firstVisibleItem - 1;
        if(0 > realFirstVisibleItem) {
            mIvOrgSwitcherFixed.setVisibility(GONE);
            mCanOrgHeaderSee = true;
            return;
        }

        View childAt = view.getChildAt(realFirstVisibleItem);
        if (0 == realFirstVisibleItem && null != childAt && null != mIvOrgSwitcherInOrgHeader) {

            int hideTop = mVMainInOrgHeader.getHeight() - ((mVMainInOrgHeader.getHeight() - mIvOrgSwitcherInOrgHeader.getHeight()) / 2) - DensityUtil.dip2px(5);
            LogUtil.e("childAt.getTop() -> " + childAt.getTop() + "    hideTop -> " + hideTop);

            if(Math.abs(childAt.getTop()) < hideTop) {
                mIvOrgSwitcherFixed.setVisibility(GONE);
                mCanOrgHeaderSee = true;

                return;
            }
        }


        //避免调用#checkShowOrgSwitcher里的线程太频繁, 已经显示的就不用反复调用, 因为此处 scroll 会调用多次
        if(!mIvOrgSwitcherFixed.isShown()) {
            OrgSwitcherHelper.checkShowOrgSwitcher(mIvOrgSwitcherFixed);
        }
        mCanOrgHeaderSee = false;

        return;
    }

    private void hideTitleBarFixed() {


        mVTitleBarFixed.setVisibility(GONE);
        if(!shouldNotShowBackBtnInTitleBar()) {
            mRlBackFloat.setVisibility(View.VISIBLE);
        }

    }

    private void showTitleBarFixedCover() {


        mVTitleBarFixed.setVisibility(View.VISIBLE);
        mRlBackFloat.setVisibility(View.GONE);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRlMainArea.getLayoutParams();


        if (-1 != layoutParams.getRules()[RelativeLayout.BELOW]) {
            layoutParams.addRule(RelativeLayout.BELOW, -1);
            mVTitleBarFixed.requestLayout();
        }
    }

    private void showViewInOrgHeader() {
        if(!AtworkConfig.ORG_CONFIG.isShowHeaderOneOrgInAppFragment()) {
            OrgSwitcherHelper.checkShowOrgSwitcher(mVMainInOrgHeader);
            return;
        }

        if(!AtworkConfig.ORG_CONFIG.isShowHeaderInAppFragment()) {
            return;
        }


        mVMainInOrgHeader.setVisibility(View.VISIBLE);
    }

    private void hideViewInOrgHeader() {
        mVMainInOrgHeader.setVisibility(View.GONE);

    }


    private void showTitleBarFixedJoin() {


        mVTitleBarFixed.setVisibility(View.VISIBLE);
        mRlBackFloat.setVisibility(View.GONE);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRlMainArea.getLayoutParams();

        if (R.id.app_title_bar != layoutParams.getRules()[RelativeLayout.BELOW]) {
            layoutParams.addRule(RelativeLayout.BELOW, R.id.app_title_bar);
            mVTitleBarFixed.requestLayout();
        }
    }

    private void registerBroadcast() {
        if (null == mNativeAppInstallRemovedReceiver) {
            mNativeAppInstallRemovedReceiver = new NativeAppInstallRemovedReceiver(this);
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(ACTION_REMOVE_APP);
            iFilter.addAction(ACTION_INSTALL_APP);

            iFilter.addDataScheme("package");
            //因为要接收系统广播, 所以该出不能使用 LocalBroadcastManager
            mActivity.registerReceiver(mNativeAppInstallRemovedReceiver, iFilter);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(AppRefreshHelper.ACTION_REFRESH_APP);
        filter.addAction(AdvertisementManager.ACTION_REFRESH_ADVERTISEMENTS);

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mRefreshDataBdReceiver, filter);

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mRefreshLightlyBdReceiver, new IntentFilter(AppRefreshHelper.ACTION_REFRESH_APP_LIGHTLY));

        //离线下载广播监听
        registerTabrouteReceiver();
    }

    private void unRegisterBroadcast() {
        try {
            mActivity.unregisterReceiver(mNativeAppInstallRemovedReceiver);
            mActivity.unregisterReceiver(mRefreshDataBdReceiver);
            mActivity.unregisterReceiver(mRefreshLightlyBdReceiver);
            mActivity.unregisterReceiver(mTabRouteBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void cancelRemove() {
        if (mRemoveAble) {
            removeMode(false);
        }
    }


    private void initData() {
        mAdapter = new AppsAdapter(getActivity());
        mAdapter.setAppRemoveListener(this);
        mAdapter.setMailClickListener(this);

        handleHeaderOgInNoApps();

        addHeaderView();
        mAppsListView.setAdapter(mAdapter);


    }

    private void handleHeaderOgInNoApps() {

        if(!AtworkConfig.ORG_CONFIG.isShowHeaderOneOrgInAppFragment()) {
            OrgSwitcherHelper.checkShowOrgSwitcher(mVMainInOrgHeader);
            return;
        }

        if (AtworkConfig.ORG_CONFIG.isShowHeaderInAppFragment()) {


            mVHeaderOrgInNoApps.setVisibility(View.VISIBLE);

        } else {
            mVHeaderOrgInNoApps.setVisibility(GONE);
        }


        if(AtworkConfig.APP_CONFIG.isNeedAppInSearch()) {
            ViewUtil.setWidth(mIvSearch, DensityUtil.dip2px(40));
        } else {
            ViewUtil.setWidth(mIvSearch, 0);
        }
    }

    @Override
    protected void findViews(View view) {
        mRlMainArea = view.findViewById(R.id.rl_main_area);
        mAppsListView = view.findViewById(R.id.apps_list);
        mRlNoApps = view.findViewById(R.id.rl_no_app);
        mIvArrow = view.findViewById(R.id.iv_arrow);
        mIvAppTabGuide = view.findViewById(R.id.iv_app_tab_guide);

        View vHeaderOrgInNoApps = view.findViewById(R.id.v_header_org_in_no_apps);

        mVHeaderOrgInNoApps = vHeaderOrgInNoApps.findViewById(R.id.rl_main);
        mIvOrgSwitcherInNoAppsView = vHeaderOrgInNoApps.findViewById(R.id.iv_switch_org);
        mTvOrgNameInNoAppsView = vHeaderOrgInNoApps.findViewById(R.id.tv_org_name);

        mVTitleBarFixed = view.findViewById(R.id.app_title_bar);
        mTitleViewFixed = mVTitleBarFixed.findViewById(R.id.title_bar_main_title);
        mVFakeStatusBarFixed = mVTitleBarFixed.findViewById(R.id.v_fake_statusbar);
        mMoreViewFixed = mVTitleBarFixed.findViewById(R.id.titlebar_main_more_btn);
        mMoreViewFixed.setImageResource(R.mipmap.icon_app_store);

        mRlBackFloat = view.findViewById(R.id.rl_back_float);

        mBackView = mVTitleBarFixed.findViewById(R.id.back_btn);
        mBackView.setVisibility(shouldNotShowBackBtnInTitleBar() ? GONE : View.VISIBLE);
        mIvOrgSwitcherFixed = mVTitleBarFixed.findViewById(R.id.org_switcher);
        mIvSearch = mVTitleBarFixed.findViewById(R.id.iv_search);
//        mNetworkErrorViewManager = new NetworkErrorViewManager(mVTitleBarFixed);

        if(StatusBarUtil.supportStatusBarMode()) {
            ViewUtil.setTopMargin(mRlBackFloat, StatusBarUtil.getStatusBarHeight(BaseApplicationLike.baseContext));
        }
    }

    private boolean shouldNotShowBackBtnInTitleBar() {
        return TextUtils.isEmpty(mCheckOrgId);
    }


    /**
     * 加载app list列表后刷新页面
     *
     * @param data
     */
    private void onAppListRefresh(final List<App> data) {

        Executors.newSingleThreadExecutor().execute(() -> {
            AtworkApplicationLike.refreshSystemInstalledApps();


            List<GroupAppItem> groupAppItemsSorted = AppRefreshHelper.sortApp(AtworkApplicationLike.baseContext, data);
            if(null != groupAppItemsSorted) {
                mGroupAppItems = groupAppItemsSorted;
            }

            //添加常用应用列表
            refreshCustomAppGroupItem();

            refreshAppLightNoticeModel();

            mHandler.obtainMessage(MSG_REFRESH).sendToTarget();
        });

    }



    private void onAppListUpdate() {

        Executors.newSingleThreadExecutor().execute(() -> {
            AtworkApplicationLike.refreshSystemInstalledApps();
            mHandler.obtainMessage(MSG_REFRESH).sendToTarget();
        });

    }



    private void refreshCustomAppGroupItem() {
        if(!OrganizationSettingsManager.getInstance().onAppCustomizationEnabled(PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext))) {
            return;
        }


        if(ListUtil.isEmpty(mGroupAppItems)) {
            return;
        }


        List<GroupAppItem> groupAppItems = new ArrayList<>();
        groupAppItems.addAll(mGroupAppItems);



        List<AppBundles> appBundleList = new ArrayList<>();
        for(GroupAppItem groupAppItem : groupAppItems) {

            if (!groupAppItem.custom) {
                appBundleList.addAll(groupAppItem.mAppBundleList);
            }
        }

        GetAppListRequest getAppListRequest = new GetAppListRequest();
        getAppListRequest.setStrategy(GetAppListRequest.STRATEGY_CUSTOM);
        getAppListRequest.setLimit(8);

        List<AppBundles> appListInCustomStrategy = AppManager.getInstance().getAppListInCustomStrategy(AtworkApplicationLike.baseContext, getAppListRequest, appBundleList);

        if(!groupAppItems.get(0).custom) {
            String key = getStrings(R.string.common_apps);
            GroupAppItem titleAppItemAdded = new GroupAppItem(key, GroupAppItem.TYPE_TITLE);
            titleAppItemAdded.custom = true;

            GroupAppItem groupAppItemAdded = new GroupAppItem(key, appListInCustomStrategy, GroupAppItem.TYPE_APP);
            groupAppItemAdded.custom = true;

            groupAppItems.add(0, titleAppItemAdded);
            groupAppItems.add(1, groupAppItemAdded);

        } else {
            GroupAppItem groupAppItemAdded = groupAppItems.get(1);
            groupAppItemAdded.mAppBundleList = appListInCustomStrategy;
        }

        mGroupAppItems = groupAppItems;

    }



    private void refreshAdapter() {

        if (null != mAdapter) {

            mAdapter.refreshGroupAppList(mGroupAppItems);

            handleAppListVisible(!checkAppsEmpty());
            mCanOrgHeaderSee = null;

            refreshAdvertisements();

            handleOrgSwitcher();
        }


    }


    @Override
    public void removeMode(boolean removeAble) {
        if (this.mRemoveAble != removeAble) {
            this.mRemoveAble = removeAble;
            mAdapter.setRemoveAble(removeAble);
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void removeComplete(final GroupAppItem groupAppItem, final AppBundles appBundle) {
        removeMode(false);
        Executors.newSingleThreadExecutor().execute(() -> {
            removeGroupAppItem(groupAppItem, appBundle);
            refreshCustomAppGroupItem();

            mLightNoticeMap.remove(appBundle.mBundleId);

            mHandler.obtainMessage(MSG_REFRESH_LISTVIEW_AFTER_REMOVE).sendToTarget();
        });
    }

    @Override
    public Map<String, AppBundles> getNativeAppRemoveFlagHashTable() {
        return mNativeRemoveList;
    }


    private GroupAppItem findGroupAppItemNormal(AppBundles appBundle) {
        GroupAppItem result = null;
        for (GroupAppItem groupAppItem : mGroupAppItems) {
            if (groupAppItem.title.equals(appBundle.getCategoryNameI18n(AtworkApplicationLike.baseContext))
                    && GroupAppItem.TYPE_APP == groupAppItem.type
                    && !groupAppItem.custom) {
                result = groupAppItem;
                break;
            }
        }

        return result;
    }

    private void removeGroupAppItem(GroupAppItem groupAppItem, AppBundles appBundle) {
        GroupAppItem handedGroupAppItem = groupAppItem;
        if (null == groupAppItem || groupAppItem.custom) {
            handedGroupAppItem = findGroupAppItemNormal(appBundle);
        }
        handedGroupAppItem.mAppBundleList.remove(appBundle);

        if (ListUtil.isEmpty(handedGroupAppItem.mAppBundleList)) { //若该分组已经清空了 app, 则整体去除
            int titleIndex = mGroupAppItems.indexOf(handedGroupAppItem) - 1;
            mGroupAppItems.remove(handedGroupAppItem);
            //remove title
            mGroupAppItems.remove(titleIndex);
        }

        AppManager.getInstance().removeBundle(appBundle);
    }

    @Override
    public LoaderManager getLoaderManager() {
        return super.getLoaderManager();
    }


    private void refreshAdvertisements() {
        LogUtil.e("refreshAdvertisements   ~~~~~");

        if(null == mAdvertisementBannerCardView) {
            return;
        }



        if(checkAppsEmpty()) {
            return;
        }

        mAdvertisementBannerCardView.refreshAppBanner();

        if (AdvertisementManager.INSTANCE.isCurrentOrgAdvertisementsLegal(AdvertisementKind.APP_BANNER)) {
            OrgSwitcherHelper.checkShowOrgSwitcher(mIvOrgSwitcherInAdvertView);
            hideTitleBarFixed();

        } else {
            showTitleBarFixedJoin();

        }


        if(mAdvertisementBannerCardView.isPlaying()) {
            hideViewInOrgHeader();
        } else {
            showViewInOrgHeader();
        }
    }

    private void refreshAppUI() {
        if (!StringUtils.isEmpty(PersonalShareInfo.getInstance().getCurrentOrg(getActivity()))) {
            onAppListRefresh(AppManager.getInstance().getAppList());

        } else {
            clearData();
            refreshAppLightNoticeModel();
            mHandler.obtainMessage(MSG_REFRESH).sendToTarget();
        }
    }

    private void clearData() {
        AppManager.getInstance().clearAppData();

        clearLightNotices();

        mGroupAppItems.clear();
    }

    private void clearLightNotices() {
        mLightNoticeMap.clear();
        TabNoticeManager.getInstance().unregisterTab(mId);
    }

    public void refreshAppLightNoticeModel() {

        resetAppLightNoticeModel();

        loadLightAppNotices(mLightNoticeMap, true);
    }

    private void resetAppLightNoticeModel() {
        mLightNoticeMap.clear();

        List<App> appList = AppManager.getInstance().getAppList();
        if (!ListUtil.isEmpty(appList)) {
            for (App app : appList) {

                if (app.supportLightNotice()) {
                    if (ListUtil.isEmpty(app.mBundles)) {
                        continue;
                    }
                    for (AppBundles bundle : app.mBundles) {
                        if (!TextUtils.isEmpty(bundle.mNoticeUrl)) {
                            List<String> tabIds = new ArrayList<>();
                            tabIds.add(mId);
                            if (null != app.mShortcut) {
                                tabIds.add(TabHelper.getAboutMeFragmentId());
                            }

                            LightNoticeMapping lightNoticeModel = SimpleLightNoticeMapping.createInstance(bundle.mNoticeUrl, tabIds, bundle.mBundleId);
                            mLightNoticeMap.put(bundle.mBundleId, lightNoticeModel);

                        } else {
                            TabNoticeManager.getInstance().unregisterLightNotice(mId, bundle.mBundleId);

                            if (null != app.mShortcut) {
                                TabNoticeManager.getInstance().unregisterLightNotice(TabHelper.getAboutMeFragmentId(), bundle.mBundleId);

                            }
                        }
                    }

                }
            }
        }
    }

    /**
     * @param lightNoticeMap
     * @param isCheckTotally 是否全量检查更新, 若是的话, 则无论如何都要刷新 tab 红点
     */
    private void loadLightAppNotices(Map<String, LightNoticeMapping> lightNoticeMap, boolean isCheckTotally) {
        if (0 != lightNoticeMap.size()) {

            //全量检查更新的时候, 先根据本地数据刷新 tab
            if (isCheckTotally) {
                TabNoticeManager.getInstance().update(mId);
            }

            for (Map.Entry<String, LightNoticeMapping> entry : lightNoticeMap.entrySet()) {
                final LightNoticeMapping lightNoticeModel = entry.getValue();

                //注册此轻应用
                TabNoticeManager.getInstance().registerLightNoticeMapping(lightNoticeModel);

                LightNoticeHelper.loadLightNotice(lightNoticeModel.getNoticeUrl(), AtworkApplicationLike.baseContext, new LightNoticeHelper.LightNoticeListener() {
                            @Override
                            public void success(LightNoticeData lightNoticeJson) {
                                if (TabNoticeManager.getInstance().checkLightNoticeUpdate(lightNoticeModel.getAppId(), lightNoticeJson)) {

                                    TabNoticeManager.getInstance().update(lightNoticeModel, lightNoticeJson);
                                    AppRefreshHelper.refreshAppLightly();
                                }
                            }

                            @Override
                            public void fail() {
                                TabNoticeManager.getInstance().update(mId);
                            }
                        }


                );
            }

        } else {
            TabNoticeManager.getInstance().update(mId);

        }
    }

    /**
     * 公开的方法，用于关闭离线应用下载完成之后的自动跳转
     */
    public void setLightAppDownloadNotJump(){
        isPutUp = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            AtworkUtil.checkUpdate(mActivity, false);
            AdvertisementManager.INSTANCE.requestCurrentOrgBannerAdvertisementsSilently(AdvertisementKind.APP_BANNER);
            AdvertisementManager.INSTANCE.checkCurrentOrgBannerMediaDownloadSilently(AdvertisementKind.APP_BANNER);


            if (StringUtils.isEmpty(PersonalShareInfo.getInstance().getCurrentOrg(getActivity()))) {
                clearData();
                refreshAppLightNoticeModel();

                mHandler.obtainMessage(MSG_REFRESH).sendToTarget();


            } else {
                if (checkAppsEmpty()) {
                    AppRefreshHelper.refreshAppAbsolutely();
                }
            }
            OrganizationSettingsHelper.getInstance().checkOrgSettingsUpdate(mActivity, 0);


            checkUpdateApps();

            getSystemNavigation();

            handleAppTabFirstGuidePageShown();

        } else {
            isPutUp = false;
            cancelRemove();

            if (null != mIvAppTabGuide) {
                mIvAppTabGuide.setVisibility(GONE);
            }
        }


        if (null != mAdvertisementBannerCardView) {
            if(isVisibleToUser) {
                mAdvertisementBannerCardView.startAutoScrollAppBanner();
            } else {
                mAdvertisementBannerCardView.stopAutoScroll();

            }
        }

    }

    @Override
    protected void refreshNetworkStatusUI() {
        if (mNetworkErrorViewManager != null) {
            mNetworkErrorViewManager.refreshNetworkStatusUI(NetworkStatusUtil.isNetworkAvailable(AtworkApplicationLike.baseContext));
        }
    }


    private void handleOrgSwitcher() {
        if (checkAppsEmpty()) {


            if (AtworkConfig.ORG_CONFIG.isShowHeaderInAppFragment()) {
                mIvOrgSwitcherFixed.setVisibility(GONE);
                OrgSwitcherHelper.checkShowOrgSwitcher(mIvOrgSwitcherInNoAppsView);


            } else {
                OrgSwitcherHelper.checkShowOrgSwitcher(mIvOrgSwitcherFixed);

            }

        } else {

            if(mAdvertisementBannerCardView.isPlaying()) {
                OrgSwitcherHelper.checkShowOrgSwitcher(mIvOrgSwitcherFixed);
                OrgSwitcherHelper.checkShowOrgSwitcher(mIvOrgSwitcherInAdvertView);


            } else {

                OrgSwitcherHelper.checkShowOrgSwitcher(mIvOrgSwitcherInOrgHeader);

                if(AtworkConfig.ORG_CONFIG.isShowHeaderInAppFragment()) {
                    if (null == mCanOrgHeaderSee || mCanOrgHeaderSee) {
                        mIvOrgSwitcherFixed.setVisibility(GONE);

                    } else {
                        OrgSwitcherHelper.checkShowOrgSwitcher(mIvOrgSwitcherFixed);
                    }


                } else {
                    OrgSwitcherHelper.checkShowOrgSwitcher(mIvOrgSwitcherFixed);


                }

            }


        }
    }

    private boolean checkAppsEmpty() {
        return ListUtil.isEmpty(mGroupAppItems) || (null != mAdapter && 0 >= mAdapter.getCount());
    }

    public void checkUpdateApps() {

        if(!AppManager.getInstance().checkLegalRequestIdCheckAppUpdatesRemote()) {
            return;
        }

        AppManager.getInstance().addInterceptRequestIdCheckAppUpdatesRemote();

        AppManager.getInstance().getAppCheckUpdateController().checkAppsUpdate(PersonalShareInfo.getInstance().getCurrentOrg(mActivity), mLightNoticeMap, new AppManager.CheckAppListUpdateListener() {
            @Override
            public void refresh(boolean needUpdate) {

                if (needUpdate) {
                    AppRefreshHelper.refreshApp();
                } else {
                    //没有更新则直接检查红点
                    loadLightAppNotices(mLightNoticeMap, false);

                }

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                AppManager.getInstance().removeInterceptRequestCheckAppUpdatesRemote();

                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
            }
        });
    }

    @Override
    public void onChangeLanguage() {
        clearData();
    }

    private void getSystemNavigation() {
        BeeworksTabHelper.getInstance().getSystemNavigation(mActivity, BeeWorks.getInstance().getBeeWorksTabById(mActivity, mId));
    }

    @Override
    protected boolean onBackPressed() {
        return false;
    }


    @Override
    public void onK9MailClick(String appId) {
    }

    @Override
    public void networkChanged(NetworkBroadcastReceiver.NetWorkType networkType) {
        if (mNetworkErrorViewManager != null) {
            LogUtil.e("refreshNetworkStatusUI  AppFragment");

            mNetworkErrorViewManager.refreshNetworkStatusUI(networkType.hasNetwork());
        }

    }

    public static class NativeAppInstallRemovedReceiver extends BroadcastReceiver {
        private AppRemoveListener mAppRemoveListener;
        private Map<String, AppBundles> mNativeRemoveList;

        public NativeAppInstallRemovedReceiver(AppRemoveListener appRemoveListener) {
            this.mAppRemoveListener = appRemoveListener;
            this.mNativeRemoveList = mAppRemoveListener.getNativeAppRemoveFlagHashTable();
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            //接收卸载广播
            if (ACTION_REMOVE_APP.equals(intent.getAction())) {
                String packageName = intent.getDataString();
                if (packageName.startsWith("package:")) {
                    packageName = packageName.replace("package:", "");
                }

                AtworkApplicationLike.getInstalledApps().remove(packageName);
                NativeAppDownloadManager.getInstance().updateDownloadAppInfo(packageName, false);


                final AppBundles appBundle = mNativeRemoveList.get(packageName);
                //用户不是在 workplus 里卸装, 而是在桌面卸装
                if (appBundle == null) {
                    AppRefreshHelper.refreshAppLightly();
                    return;
                }

                AppAsyncNetService appAsyncNetService = new AppAsyncNetService(AtworkApplicationLike.baseContext);
                List<String> appList = new ArrayList<>();
                appList.add(appBundle.mBundleId);
                List<InstallOrDeleteAppJSON.AppEntrances> entrances = new ArrayList<>();
                appAsyncNetService.asyncInstallOrRemoveAppFromRemote(context, PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext), entrances, false, true,
                        new AppAsyncNetService.AddOrRemoveAppListener() {
                            @Override
                            public void addOrRemoveSuccess(InstallOrRemoveAppResponseJson json) {
                                mNativeRemoveList.remove(appBundle);
                                removeDB(appBundle);
                            }

                            @Override
                            public void networkFail(int errorCode, String errorMsg) {
                                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.App, errorCode, errorMsg);
                            }
                        });
//
            } else if (ACTION_INSTALL_APP.equals(intent.getAction())) {
                String packageName = intent.getDataString();
                if (packageName.startsWith("package:")) {
                    packageName = packageName.replace("package:", "");
                }

                AtworkApplicationLike.getInstalledApps().add(packageName);
                NativeAppDownloadManager.getInstance().updateDownloadAppInfo(packageName, true);
                AppRefreshHelper.refreshAppLightly();

            }

        }

        public void removeDB(final AppBundles app) {
            //TODO: NOTICE!!
//            AppDaoService.getInstance().removeApp(app.mAppId, new AppDaoService.AddOrRemoveAppListener() {
//                @Override
//                public void addOrRemoveSuccess() {
////                    mAppRemoveListener.removeComplete(null, app);
//                    //删除成功后应该把session的纪录以及相对应的message表删除
//
////                    ChatSessionDataWrap.getInstance().removeSessionSafely(app.mAppId);
//                }
//
//                @Override
//                public void addOrRemoveFail() {
////                    AtworkToast.showToast(getResources().getString(R.string.remove_app_fail));
//                }
//            });
        }
    }


    @Override
    public void onThemeUpdate(Theme theme) {
        super.onThemeUpdate(theme);
        if (mAdapter == null) {
            return;
        }
        mAdapter.notifyDataSetChanged();
    }

    public void setTitle() {
        String mainTitle = getMainTitle();
        mainTitle = AtworkUtil.tempMakeI18n(mainTitle);
        mTitleViewFixed.setText(mainTitle);
        if(null != mTitleViewInAdvertView) {
            mTitleViewInAdvertView.setText(mainTitle);
        }

        String subTitle = getSubTitle();
        if (null != subTitle && null != mTvOrgNameInOrgHeader) {
            mTvOrgNameInOrgHeader.setText(subTitle);
            mTvOrgNameInNoAppsView.setText(subTitle);
        }
    }

    /**
     * 获取头部名称，如果有组织，优先返回组织名称
     *
     * @return
     */
    private String getSubTitle() {
        String orgCode = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext);
        if (!TextUtils.isEmpty(orgCode)) {
            Organization organization = OrganizationRepository.getInstance().queryOrganization(orgCode);
            if (null == organization) {
                return StringUtils.EMPTY;
            }
            return organization.getNameI18n(AtworkApplicationLike.baseContext);
        }
        return StringUtils.EMPTY;
    }

    private String getMainTitle() {
        try {
            String beeworksName = BeeWorks.getInstance().getBeeWorksTabById(mActivity, mId).name;
            return TextUtils.isEmpty(beeworksName) ? getStrings(R.string.item_app) : beeworksName;
        } catch (NullPointerException e) {
            e.printStackTrace();

        }
        return getStrings(R.string.item_app);
    }


    private void onMarketClick() {
        String orgId = PersonalShareInfo.getInstance().getCurrentOrg(mActivity);
        // 可以认为它没有任何组织
        if (TextUtils.isEmpty(orgId)) {
            AtworkAlertDialog alert = new AtworkAlertDialog(mActivity);
            alert.setContent(getString(R.string.please_create_org))
                    .hideDeadBtn()
                    .setBrightBtnText(getString(R.string.ok))
                    .setTitleText(getString(R.string.tip))
                    .setClickBrightColorListener(dialog -> alert.dismiss())
                    .setCanceledOnTouchOutside(false);
            alert.show();
            return;
        }
//      String url = "file:///android_asset/www/index_2.1.html";
        String url = String.format(UrlConstantManager.getInstance().getAppStoreUrl(), PersonalShareInfo.getInstance().getCurrentOrg(mActivity), LoginUserInfo.getInstance().getLoginUserId(mActivity), false);

        WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url);
        Intent intent = WebViewActivity.getIntent(mActivity, webViewControlAction);
        startActivity(intent);
    }


    private void handleAppTabFirstGuidePageShown() {
        if(!AtworkConfig.APP_CONFIG.getNeedGuideByImage()) {
            return;
        }

        if(null == mIvAppTabGuide) {
            return;
        }

        if(!isAdded()) {
            return;
        }


        int shownCount = PersonalShareInfo.getInstance().getAppTabFirstGuidePageShownCount(BaseApplicationLike.baseContext);
        if (shownCount >= PersonalShareInfo.DEFAULT_SHOULD_MAIN_FAB_BOTTOM_SLIDE_NOTICE_FINGER_SHOWN_COUNT) {
            return;
        }

//        mIvAppTabGuide.setVisibility(View.VISIBLE);

        ImageGuidePageDialogFragment dialogFragment = new ImageGuidePageDialogFragment();
        dialogFragment.setGuideImage(R.mipmap.app_tab_guide);
        dialogFragment.show(getFragmentManager(), "AppTabGuidePage");


        PersonalShareInfo.getInstance().putAppTabFirstGuidePageShownCount(BaseApplicationLike.baseContext, ++shownCount);
    }

    private void registerTabrouteReceiver() {
        IntentFilter filter = new IntentFilter(REFRESH_APP_NOTICE);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mTabRouteBroadcastReceiver, filter);

    }

    private BroadcastReceiver mTabRouteBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (REFRESH_APP_NOTICE.equals(intent.getAction())) {
                mAdapter.notifyDataSetChanged();
            }
        }
    };

}
