package com.foreveross.atwork.modules.aboutatwork.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.beeworks.BeeWorksNetService;
import com.foreveross.atwork.api.sdk.setting.DynamicPropertiesAsyncNetService;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.component.WorkplusSwitchCompat;
import com.foreveross.atwork.db.daoService.OrganizationDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksHelper;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.domain.AppVersions;
import com.foreveross.atwork.infrastructure.plugin.UCCalendarPlugin;
import com.foreveross.atwork.infrastructure.shared.BeeWorksPreviewData;
import com.foreveross.atwork.infrastructure.shared.BeeWorksPublicData;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.shared.SettingInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.AtworkConstants;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.DomainSettingsHelper;
import com.foreveross.atwork.manager.UCCalendarManager;
import com.foreveross.atwork.modules.aboutatwork.activity.FeedbackActivity;
import com.foreveross.atwork.modules.aboutatwork.activity.IntroFriendsActivity;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.tab.helper.BeeworksTabHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;

/**
 * 关于AtWork页面
 * Created by ReyZhang on 2015/5/6.
 */
public class AboutAtWorkFragment extends BackHandledFragment implements View.OnClickListener,
        DeveloperModeDialog.OnPreviewCodeListener {

    private static final String TAG = AboutAtWorkFragment.class.getSimpleName();

    public static final String ACTION_MAIN_FINISH = "action_main_finish";


    //----------顶部栏-------
    private View mVFakeStatusBar;
    private ImageView mBackBtn;
    private TextView mTitle;
    private TextView mMoreBtn;

    //---------中间组件-----
    private ImageView mAppIcon;
    private TextView mAppVersion;
    private TextView mTvCopyrightCommon;
    private TextView mTvCopyrightBeeWorks;

    private View mTransparentView;

    private long mLastClickTime = 0;

    private int mStep2DevMode = 0;
    private DeveloperModeDialog mDevDialog;

    private ProgressDialogHelper mProgressDialog;

    private boolean mIsNewUpdate = false;

    private RelativeLayout mLayout;

    private RelativeLayout mCheckVersionUpgradeLayout;
    private ImageView mVersionArrow;
    private TextView mLatestVersionText;
    private LinearLayout mNewVersionLayou;
    private TextView mVersionCode;
    private ImageView mUpdateTip;

    private RelativeLayout mInviteShareLayout;

    private RelativeLayout mIntroduceToFriendLayout;

    private RelativeLayout mFeedbackLayout;

    private RelativeLayout mDevModeLayout;
    private WorkplusSwitchCompat mDevSwitchBtn;

    private RelativeLayout mUpdateDesignLayout;

    private RelativeLayout mRlUploadLogs;

    private RelativeLayout mRlService;
    private RelativeLayout mRlPrivacy;

    private BroadcastReceiver mAppUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AppVersions appVersions = intent.getParcelableExtra(DynamicPropertiesAsyncNetService.ACTION_INTENT_UPDATE_EXTRA);
            if (appVersions == null) {
                return;
            }
            new Handler().postDelayed(() -> mActivity.runOnUiThread(AboutAtWorkFragment.this::refreshRedView), 20);

        }
    };

    private BroadcastReceiver mReceiveMainFinishListen = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            new Handler().postDelayed(() -> {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                if (mIsNewUpdate) {
                    AtworkToast.showLongToast(getString(R.string.update_success));
                }
                Intent newIntent = MainActivity.getMainActivityIntent(mActivity, true);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newIntent);
                mActivity.finish();
            }, 2000);

        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mAppUpdateReceiver, new IntentFilter(AtworkConstants.ACTION_RECEIVE_APP_UPDATE));
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mReceiveMainFinishListen, new IntentFilter(ACTION_MAIN_FINISH));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_atwork, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setup();
        iniData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiveMainFinishListen != null) {
            LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mReceiveMainFinishListen);
        }

        if (mAppUpdateReceiver != null) {
            LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mAppUpdateReceiver);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    protected void findViews(View view) {
        mVFakeStatusBar = view.findViewById(R.id.v_fake_statusbar);
        mAppIcon = view.findViewById(R.id.app_icon);
        mBackBtn = view.findViewById(R.id.title_bar_common_back);
        mTitle = view.findViewById(R.id.title_bar_common_title);
        mMoreBtn = view.findViewById(R.id.title_bar_common_right_text);
        mAppVersion = view.findViewById(R.id.app_version);
        mTvCopyrightCommon = view.findViewById(R.id.tv_about_copyright_common);
        mTvCopyrightBeeWorks = view.findViewById(R.id.tv_about_copyright_beeworks);
        mLayout = view.findViewById(R.id.layout);
        mTransparentView = new View(mActivity);
        mTransparentView.setBackgroundColor(Color.BLACK);
        mTransparentView.setAlpha(0.5f);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mActivity.addContentView(mTransparentView, lp);
        mTransparentView.setVisibility(View.GONE);

        mCheckVersionUpgradeLayout = view.findViewById(R.id.check_version_update_layout);
        mVersionArrow = mCheckVersionUpgradeLayout.findViewById(R.id.version_arrow_right);
        mLatestVersionText = mCheckVersionUpgradeLayout.findViewById(R.id.tv_rightest);
        mNewVersionLayou = mCheckVersionUpgradeLayout.findViewById(R.id.version_update_layout);
        mVersionCode = mNewVersionLayou.findViewById(R.id.version_code_tv);
        mUpdateTip = mCheckVersionUpgradeLayout.findViewById(R.id.new_version_tip);

        mInviteShareLayout = view.findViewById(R.id.invite_share_layout);

        mIntroduceToFriendLayout = view.findViewById(R.id.intro_to_friend_layou);

        mFeedbackLayout = view.findViewById(R.id.feedback_layout);

        mDevModeLayout = view.findViewById(R.id.dev_mode_layout);
        mDevSwitchBtn = view.findViewById(R.id.dev_switch_btn);

        mUpdateDesignLayout = view.findViewById(R.id.update_design_layout);

        mRlUploadLogs= view.findViewById(R.id.rl_upload_log_layout);

        mRlService= view.findViewById(R.id.rl_workplus_service);
        mRlPrivacy= view.findViewById(R.id.rl_workplus_policy);
        if(AtworkConfig.PROTOCOL){
            mRlService.setVisibility(View.VISIBLE);
            mRlPrivacy.setVisibility(View.VISIBLE);
        }else{
            mRlService.setVisibility(View.GONE);
            mRlPrivacy.setVisibility(View.GONE);
        }

        if(AtworkConfig.INTEGRATE_UCCALENDAR) {
            mRlUploadLogs.setVisibility(View.VISIBLE);
        }  else {
            mRlUploadLogs.setVisibility(View.GONE);
        }


//        mAboutList.setDivider(null);
//        mDevModeList.setDivider(null);
    }


    @Override
    protected View getFakeStatusBar() {
        return null;
    }

    private void setup() {
        mBackBtn.setOnClickListener(this);
        mAppIcon.setOnClickListener(this);
        mMoreBtn.setOnClickListener(this);
        mAppVersion.setOnClickListener(this);

        mCheckVersionUpgradeLayout.setOnClickListener(this);
        mInviteShareLayout.setOnClickListener(this);
        mIntroduceToFriendLayout.setOnClickListener(this);
        mFeedbackLayout.setOnClickListener(this);
        mUpdateDesignLayout.setOnClickListener(this);
        mRlUploadLogs.setOnClickListener(this);
        mRlService.setOnClickListener(this);
        mRlPrivacy.setOnClickListener(this);

        mDevSwitchBtn.setOnClickNotPerformToggle(()-> {
            mProgressDialog = null;
            mProgressDialog = new ProgressDialogHelper(mActivity);
            mProgressDialog.show(false);
            closeDevMode();
        });

    }

    private void iniData() {
        mAppIcon.setImageResource(R.mipmap.about_workplus_logo);
        mAppVersion.setText(AppUtil.getVersionName(mActivity));
        try {
            String appIcon = BeeWorks.getInstance().config.copyright.companyAboutIcon;
            if (!TextUtils.isEmpty(appIcon)) {
                BeeworksTabHelper.getInstance().setBeeImage(mAppIcon, appIcon, 0);
            }

        } catch (NullPointerException e) {

            e.printStackTrace();
        }

        try {
            String androidBuildNo = BeeWorks.getInstance().beeWorksAppBase.mCustomerBuildNo.mAndroidBuildNo;
            if (!TextUtils.isEmpty(androidBuildNo)) {
                mAppVersion.setText(androidBuildNo);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            String showInfo = BeeWorksHelper.getCopyright(BaseApplicationLike.baseContext, true);
            String contactInfo = BeeWorksHelper.getString(BaseApplicationLike.baseContext, BeeWorks.getInstance().config.copyright.contactInfo);
            if(!StringUtils.isEmpty(contactInfo)) {
                showInfo += "\n" + contactInfo;
            }

            mTvCopyrightCommon.setText(showInfo);
            mTvCopyrightBeeWorks.setText(showInfo);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        mTitle.setText(mActivity.getIntent().getStringExtra("aboutName"));


        mMoreBtn.setVisibility(View.GONE);

        String loginUserId = LoginUserInfo.getInstance().getLoginUserId(getActivity());

        //如果当前没有组织, 则隐藏"邀请分享"
        OrganizationDaoService.getInstance().queryOrgCount(loginUserId, this::handleItemListData);

    }


    private void handleItemListData(int count) {
        showUpdateTip(AtworkUtil.isFoundNewVersion(mActivity));
        if (count < 1 || !DomainSettingsManager.getInstance().handleOrgApplyFeature()) {
            mInviteShareLayout.setVisibility(View.GONE);
        }


        if(AtworkConfig.ABOUT_WORKPLUS_CONFIG.isNeedIntroFriends()) {
            mIntroduceToFriendLayout.setVisibility(View.VISIBLE);
        }

//        if(AtworkConfig.ABOUT_WORKPLUS_CONFIG.isNeedFeedback()) {
//            mFeedbackLayout.setVisibility(View.VISIBLE);
//        }

        if (SettingInfo.getInstance().getUserSettings(mActivity).mIsDevMode) {
            mDevModeLayout.setVisibility(View.VISIBLE);
            mDevSwitchBtn.setChecked(true);

            mTvCopyrightCommon.setVisibility(View.GONE);
        }
        refreshRedView();
    }

    @Override
    public void onClick(View v) {
        if(CommonUtil.isFastClick(500)) {
            return;
        }

        int id = v.getId();
        switch (id) {
            case R.id.title_bar_common_back:
//                AtworkUtil.hideInput(mActivity);
                finish();
                break;

            case R.id.app_icon:
                //beeworks 开发者模式下，不检查beeworks更新
                try {
                    if (SettingInfo.getInstance().getUserSettings(mActivity).mIsDevMode || TextUtils.isEmpty(BeeWorks.getInstance().config.beeWorksUrl)) {
                        return;
                    }
                    toDevMode();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.app_version:
//                String url = UrlConstantManager.getInstance().getColleagueCircleUrl(PersonalShareInfo.getInstance().getCurrentOrg(mActivity));
                if (AtworkConfig.TEST_MODE_CONFIG.isTestMode()) {
                    String url = "file:///android_asset/www/index_2.1.html?theme=red_envelope&watermark_enable=1&ticket={{ticket}}";
                    WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url);
                    startActivity(WebViewActivity.getIntent(mActivity, webViewControlAction));
                }
                showBuildNo();
                break;

            case R.id.check_version_update_layout:
                DomainSettingsHelper.getInstance().getDomainSettingsFromRemote(mActivity, false, null);
                break;

            case R.id.invite_share_layout:

                String currentOrg = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext);
                String url = String.format(UrlConstantManager.getInstance().getOrgQrcodeUrl(), currentOrg, Uri.encode(getString(R.string.app_name)), true);
                WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url).setNeedShare(false);
                Intent intent = WebViewActivity.getIntent(getActivity(), webViewControlAction);
                startActivity(intent);

//                Intent inviteShareIntent = OrgInviteShareActivity.getIntent(mActivity);
//                startActivity(inviteShareIntent);
                break;

            case R.id.intro_to_friend_layou:
                Intent introToFriendIntent = IntroFriendsActivity.getIntent(mActivity);
                startActivity(introToFriendIntent);
                break;

            case R.id.feedback_layout:
                Intent feedbackIntent = FeedbackActivity.getIntent(mActivity);
                startActivity(feedbackIntent);
                break;

            case R.id.update_design_layout:
                if (TextUtils.isEmpty(SettingInfo.getInstance().getUserSettings(mActivity).mDevCode)) {
                    popUpPwdDialog();
                } else {
                    getPreviewDataByCode();
                }
                break;

            case R.id.rl_upload_log_layout:

                AtworkAlertDialog atworkAlertDialog = new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE)
                        .setContent(R.string.upload_qsy_log_ask)
                        .setClickBrightColorListener(dialog -> uploadLogs());

                atworkAlertDialog.show();
                break;

            case R.id.rl_workplus_service:
                String urlService = UrlConstantManager.getInstance().getProtocolPrivacyUrl();
                WebViewControlAction webViewControlActionService = WebViewControlAction.newAction().setTitle(BaseApplicationLike.baseContext.getString(R.string.policy_service)).setUrl(urlService).setNeedShare(false);
                Intent intentJumpService = WebViewActivity.getIntent(getActivity(), webViewControlActionService);
                startActivity(intentJumpService);
                break;

            case R.id.rl_workplus_policy:
                String urlPolicy = UrlConstantManager.getInstance().getProtocolProtocolUrl();
                WebViewControlAction webViewControlActionPolicy = WebViewControlAction.newAction().setTitle(BaseApplicationLike.baseContext.getString(R.string.policy_protocol)).setUrl(urlPolicy).setNeedShare(false);
                Intent intentJumpPolicy = WebViewActivity.getIntent(getActivity(), webViewControlActionPolicy);
                startActivity(intentJumpPolicy);
                break;
        }
    }

    private void uploadLogs() {
        ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(getActivity());
        progressDialogHelper.show(false, 30 * 1000);
        UCCalendarManager.getInstance().onUploadLogsWithDescription("iwork_qsy", new UCCalendarPlugin.OnUCCalendarCallBack() {
            @Override
            public void onSuccess() {
                LogUtil.e("qsy", "onSuccess -> ");
                progressDialogHelper.dismiss();
                AtworkToast.showToast("上传成功");

            }

            @Override
            public void onFail(int errCode) {
                LogUtil.e("qsy", "onFail -> " + errCode);

                progressDialogHelper.dismiss();

                AtworkToast.showResToast(R.string.network_not_avaluable);

            }

            @Override
            public void onProcess() {
                LogUtil.e("qsy", "onProcess -> ");

            }
        });
    }


    private void toDevMode() {
        if (mDevDialog != null && mDevDialog.isShowing()) {
            return;
        }
        long curTime = System.currentTimeMillis();
        if (Math.abs(curTime - mLastClickTime) > 2000) {
            mStep2DevMode = 4;
            mLastClickTime = curTime;
        }
        if (mStep2DevMode == -1) {
            return;
        }
        if (TextUtils.isEmpty(SettingInfo.getInstance().getUserSettings(mActivity).mDevCode)) {
            popUpPwdDialog();
        } else {
            getPreviewDataByCode();
        }

        mStep2DevMode--;
    }

    int clickTime = 0;
    private void showBuildNo() {
        clickTime++;
        if (clickTime == 2) {
            Toast.makeText(mActivity, "BuildNo. = " + AppUtil.getVersionCode(mActivity), Toast.LENGTH_SHORT).show();
            clickTime = 0;
            return;
        }
    }

    private void popUpPwdDialog() {
        if (mStep2DevMode == 0) {
            showPwdDialog();
            mStep2DevMode--;
            return;
        }
    }

    private void getPreviewDataByCode() {
        if (mStep2DevMode == 0) {
            onBeeWorksPreview(SettingInfo.getInstance().getUserSettings(mActivity).mDevCode, false);
            mStep2DevMode--;
            return;
        }

    }

    /**
     * 请求beeworks 预览模式
     *
     * @param code
     * @param isNewCode 判断是否是新输入的还是已存在的预览码
     */
    private void onBeeWorksPreview(final String code, boolean isNewCode) {
        mProgressDialog = null;
        mProgressDialog = new ProgressDialogHelper(getContext());
        mProgressDialog.show(false);
        BeeWorksNetService service = BeeWorksNetService.getInstance();
        service.queryPreviewByCode(code, new BeeWorksNetService.BeeWorksPreviewListener() {
            @Override
            public void success(final String tabDatas) {
                //保存开发者模式数据
                mIsNewUpdate = true;
                SettingInfo.getInstance().setDevMode(getContext(), true);
                SettingInfo.getInstance().setDevCode(getContext(), code);
                //发送广播通知页面销毁
                LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(MainActivity.ACTION_FINISH_MAIN));
                BeeWorksPreviewData.getInstance().setBeeWorksPreviewData(getContext(), tabDatas);
                BeeWorks.getInstance().initBeeWorks(tabDatas);
                Logger.d("dialog", tabDatas);


            }

            @Override
            public void fail() {
                if (isAdded()) {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    if (isNewCode) {
                        AtworkToast.showToast(getString(R.string.preview_code_incorrect));
                        showPwdDialog();
                        return;
                    }
                    AtworkToast.showToast(getString(R.string.preview_code_overdue));
                    SettingInfo.getInstance().setDevCode(mActivity, "");
                    closeDevMode();
                }
            }
        });
    }

    private void showPwdDialog() {
        //关闭并释放上一个view
        if (mDevDialog != null) {
            mDevDialog.dismiss();
        }
        mDevDialog = null;
        mDevDialog = new DeveloperModeDialog(mActivity);
        mDevDialog.setPreviewCodeListener(this);
        mDevDialog.showAtLocation(mLayout, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 250);
        mTransparentView.setVisibility(View.VISIBLE);
        mTransparentView.setOnClickListener(view -> {
            //  todo nothing...
            return;
        });
        mDevDialog.setOnDismissListener(() -> mTransparentView.setVisibility(View.GONE));
//        mDevDialog.show(getChildFragmentManager(), "DEVELOPER_MODE_DIALOG");
        new Handler().postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }, 200);


    }


    private void refreshRedView() {
        mVersionArrow.setVisibility(View.GONE);
        mUpdateTip.setVisibility(View.GONE);
        if (AtworkUtil.isFoundNewVersion(mActivity)) {
            mNewVersionLayou.setVisibility(View.VISIBLE);
            mVersionCode.setText(AtworkUtil.getNewVersionName(mActivity));
            mLatestVersionText.setVisibility(View.GONE);
        } else {
            mLatestVersionText.setVisibility(View.VISIBLE);
            mNewVersionLayou.setVisibility(View.GONE);
        }
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;
    }



    @Override
    public void onPreviewCodeInput(String code) {
        onBeeWorksPreview(code, true);
    }

    public void showUpdateTip(boolean show) {
        if (show) {
            mUpdateTip.setVisibility(View.VISIBLE);
            return;
        }
        mUpdateTip.setVisibility(View.GONE);
    }


    private void closeDevMode() {
        mTvCopyrightCommon.setVisibility(View.VISIBLE);
        mDevModeLayout.setVisibility(View.GONE);
        SettingInfo.getInstance().setDevMode(mActivity, false);
        if (!TextUtils.isEmpty(BeeWorksPublicData.getInstance().getBeeWorksPublicData(mActivity))) {
            BeeWorks.getInstance().initBeeWorks(BeeWorksPublicData.getInstance().getBeeWorksPublicData(mActivity));
        } else {
            BeeWorks.getInstance().initBeeWorks(BeeWorks.getBeeWorksJson());
        }
        LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(MainActivity.ACTION_FINISH_MAIN));
    }


}
