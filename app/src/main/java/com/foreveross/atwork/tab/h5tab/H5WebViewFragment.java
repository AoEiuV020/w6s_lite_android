package com.foreveross.atwork.tab.h5tab;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.cordova.CordovaAsyncNetService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksHelper;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksTab;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;
import com.foreveross.atwork.infrastructure.webview.OnSetWebUiChangeListener;
import com.foreveross.atwork.infrastructure.webview.OnWebActivityActionListener;
import com.foreveross.atwork.inter.BeeWorksInfo;
import com.foreveross.atwork.modules.chat.util.SchemaUrlJumpHelper;
import com.foreveross.atwork.modules.setting.util.W6sTextSizeHelper;
import com.foreveross.atwork.modules.web.fragment.WebviewFragment;
import com.foreveross.atwork.tab.helper.BeeworksTabHelper;
import com.foreveross.atwork.utils.AtworkToast;


public class H5WebViewFragment extends WebviewFragment implements BeeWorksInfo {

    public static final String ID = "ID";
    public static final String DATA_TAB_TITLE = "DATA_TAB_TITLE";
    public static final String DATA_IS_SYSTEM = "DATA_IS_SYSTEM";

    private Activity mActivity;

    public String mId;

    public String mTabId;

    public String mTabTitle;

    public boolean mIsSystemFragment;

    public boolean mLoadTicketFail = false;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initData();

        View view = super.onCreateView(inflater, container, savedInstanceState);

        handleCustomTitleView();
        handleFakeStatusBar();

        registerListener();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();


        if (mLoadTicketFail) {
            loadData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            handleBeeWorksTitle();

            handleViewWillAppear();

        }
    }

    @Override
    protected void handleOverallWatermark() {
        //do nothing
    }


    @Override
    protected void init() {
        super.init();

        setCmdFinishCheckNoGoBack(false);
    }

    private void registerListener() {
        ImageView ivReload = getReloadImageView();
        if(null != ivReload) {
            ivReload.setOnClickListener((v)-> {
                if(mLoadTicketFail) {
                    loadData();
                } else {
                    reload();
                }
            });
        }

        setOnSetWebTitleListener(new OnSetWebUiChangeListener() {
            @Override
            public void onSetWebTitle(String title, String url) {

            }

            @Override
            public void onUrlStart() {

            }

            @Override
            public void onUrlFinish(String url) {

            }

            @Override
            public void onUrlWrong() {

            }

            @Override
            public void onStatusBarChange(String color) {

            }

            @Override
            public void onSetTextZoom() {
                changeTextSize(W6sTextSizeHelper.getWebviewTextSizeSetNative());

            }
        });
    }


    private void handleCustomTitleView() {
        if(isCustomTitleView()) {

            View titleBarView = getTitleBarView();

            if(null != titleBarView) {
                getTitleTextView().setText(getFragmentName());
                titleBarView.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isCustomTitleView() {
        BeeWorksTab beeWorksTab = BeeWorks.getInstance().getBeeWorksTabById(mActivity, mId);

        return null != beeWorksTab && beeWorksTab.customTitleView && !StringUtils.isEmpty(mTabTitle);
    }

    private void handleFakeStatusBar() {
        BeeWorksTab beeWorksTab = BeeWorks.getInstance().getBeeWorksTabById(mActivity, mId);
        //默认沉浸式处理, 非沉浸式的需要强制显示"伪造的 statusbar"
        if(null == beeWorksTab || !beeWorksTab.immersion) {

            if (StatusBarUtil.supportStatusBarMode()) {
                initVFakeStatusBar(true);
                getVFakeStatusBar().setBackgroundColor(ContextCompat.getColor(BaseApplicationLike.baseContext, R.color.white));
            }
        }


    }

    private void initData() {
        if(null != getArguments()) {
            Bundle bundle = getArguments();
            mId = bundle.getString(ID);
            mTabTitle = bundle.getString(DATA_TAB_TITLE);
            mIsSystemFragment = bundle.getBoolean(DATA_IS_SYSTEM);
        }
    }

    protected void loadData() {

        BeeWorksTab beeWorksTab = BeeWorks.getInstance().getBeeWorksTabById(mActivity, mId);
        if (customerConfigLegal(beeWorksTab)) {
            if(CustomerHelper.isYueHai(mActivity)) {
                appendTicket(beeWorksTab.url);
                return;
            }


            if(needToApplyTicket(beeWorksTab.url)) {
                handleApplyTicket(beeWorksTab.url);

            } else {
                loadUrl(beeWorksTab.url);
            }

        } else {
            loadBeeWork(beeWorksTab);

        }
    }

    private boolean needToApplyTicket(String url) {
        return !TextUtils.isEmpty(url) && url.contains("{{ticket}}");
    }

    private void handleApplyTicket(final String loadUrl) {
        LogUtil.e("LoadUrl", "ticket:" + loadUrl);

        CordovaAsyncNetService.getUserTicket(BaseApplicationLike.baseContext, new CordovaAsyncNetService.GetUserTicketListener() {
            @Override
            public void getUserTicketSuccess(String userTicket) {
                if(StringUtils.isEmpty(userTicket)) {
                    mLoadTicketFail = true;
                    AtworkToast.showToast("请求应用ticket失败");
                }

                mLoadTicketFail = false;

                String loadUrlReplaced = loadUrl.replace("{{ticket}}", userTicket);

                loadUrl(loadUrlReplaced);

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                mLoadTicketFail = true;
                AtworkToast.showToast("请求应用ticket失败:" + errorCode);
            }
        });
    }

    private void appendTicket(String url) {
        CordovaAsyncNetService.getUserTicket(AtworkApplicationLike.baseContext, new CordovaAsyncNetService.GetUserTicketListener() {
            @Override
            public void getUserTicketSuccess(String userTicket) {
                if (TextUtils.isEmpty(userTicket)) {
                    mLoadTicketFail = true;
                    AtworkToast.showToast("请求应用ticket失败");
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder(url);
                stringBuilder.append("?ticket=").append(userTicket);
                loadUrl(stringBuilder.toString());
                mLoadTicketFail = false;
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                mLoadTicketFail = true;
                AtworkToast.showToast("请求应用ticket失败:" + errorCode);
            }
        });
    }

    private void loadBeeWork(BeeWorksTab beeWorksTab) {
        BeeworksTabHelper.getInstance().getH5DefineView(mActivity, appView, this, beeWorksTab);
    }

    private boolean customerConfigLegal(BeeWorksTab beeWorksTab) {
        if(null == beeWorksTab) {
            return false;
        }

        if(StringUtils.isEmpty(beeWorksTab.url)) {
            return false;
        }

        return !BeeWorksHelper.isBeeWorksDefinitionPackage();
    }

    public String getFragmentName() {
        return mTabTitle;
    }


    @Override
    public void setBeeWorksInfo(String id, String tabId, String tabTitle, boolean isSystemFragment) {
        Bundle bundle = new Bundle();
        bundle.putString(ID, id);
        bundle.putString(DATA_TAB_TITLE, tabTitle);
        bundle.putBoolean(DATA_IS_SYSTEM, isSystemFragment);
        bundle.putSerializable(SHOW_HIDDEN_CLOSE, new H5WebFragmentActionListener());
        setArguments(bundle);


    }

    private void handleBeeWorksTitle() {
        if (mActivity == null) {
            return;
        }
        BeeWorksTab beeWorksTab = BeeWorks.getInstance().getBeeWorksTabById(mActivity, mId);
        if (beeWorksTab == null) {
            return;
        }

//        ((MainActivity)mActivity).onNavigationUpdate(beeWorksTab, mIsSystemFragment);
    }

    @Override
    public TextView getTitleTextView() {
        return super.getTitleTextView();
    }


    @Override
    public View getTitleBarView() {
        return super.getTitleBarView();
    }


    static class H5WebFragmentActionListener implements OnWebActivityActionListener {

        @Override
        public void showCloseView() {

        }

        @Override
        public void hiddenCloseView() {

        }

        @Override
        public boolean handleSchemaUrlJump(Context context, String url) {
            return SchemaUrlJumpHelper.handleUrl(context, url);
        }

        @Override
        public void registerShake() {

        }

        @Override
        public void unregisterShake() {

        }
    }
}
