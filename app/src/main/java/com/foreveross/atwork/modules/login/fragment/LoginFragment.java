package com.foreveross.atwork.modules.login.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData;
import com.foreverht.workplus.ui.component.dialogFragment.W6sSelectDialogFragment;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseNetWorkListener;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.IES;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.domain.MultiDomainsItem;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.layout.KeyboardRelativeLayout;
import com.foreveross.atwork.listener.TextWatcherAdapter;
import com.foreveross.atwork.manager.IESInflaterManager;
import com.foreveross.atwork.manager.MessageNoticeManager;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.domain.DomainNetManager;
import com.foreveross.atwork.modules.lite.manager.LiteManager;
import com.foreveross.atwork.modules.lite.module.LiteBindConfig;
import com.foreveross.atwork.modules.login.activity.AccountLoginActivity;
import com.foreveross.atwork.modules.login.activity.LoginWithAccountActivity;
import com.foreveross.atwork.modules.login.model.LoginControlViewBundle;
import com.foreveross.atwork.modules.login.model.LoginHandleBundle;
import com.foreveross.atwork.modules.login.util.BasePermissionHelperV2;
import com.foreveross.atwork.modules.login.util.LoginHelper;
import com.foreveross.atwork.modules.meeting.activity.ZoomJoinVoipMeetingActivity;
import com.foreveross.atwork.modules.qrcode.activity.QrcodeScanActivity;
import com.foreveross.atwork.services.ImSocketService;
import com.foreveross.atwork.support.BaseActivity;
import com.foreveross.atwork.tab.helper.BeeworksTabHelper;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.ImageViewUtil;
import com.foreveross.atwork.utils.UserRemoveHelper;
import com.foreveross.atwork.utils.ViewHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import kotlin.collections.CollectionsKt;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.foreveross.atwork.modules.login.activity.LoginActivity.INTENT_LOGIN_USER_NAME;


public class LoginFragment extends BaseLoginFragment {


    private static final String TAG = LoginFragment.class.getSimpleName();
    private KeyboardRelativeLayout mLoginResizeRelativeLayout;

    public static final String ACCOUNT_SWITCH = "ACCOUNT_SWITCH";

    private ScrollView mLoginScrollView;

    private EditText mEtUsername;

    private ImageView mIvUsernameCancel;

    private EditText mEtPassword;

    private ImageView mIvPasswordCancel;
    private ImageView mIvPasswordShowOrHide;

    private Button mBtnLogin;
    private ImageView mIvFakeHeader;

    private TextView mTvLoginWithSmsCode;
    private TextView mTvForgetPwd;

    private RelativeLayout mRlBottomArea;

    private TextView mTvRegisterButton;

    private ImageView mIvUserAvatar;

    private ImageView mIvBack;

    private View mVFakeStatusbar;

    private ImageView mIvSyncMessagesSetting;

    private boolean mIsSwitch;

    private ProgressDialogHelper mProgressDialogHelper;

    private View mSecureCodeLayout;
    private ImageView mIvSecureCode;
    private ImageView mIvSecureCodeRefresh;
    private EditText mEtSecureCode;

    private View mVLineUsername;
    private View mVLinePassword;
    private View mVLineSecureCode;

    private View mCopyRightView;

    private boolean mKeyboardShow = false;
    private boolean mLastInputHadFocus = false;
    private int mContinueScrollViewToSeeBtnCount = 1;

    private ImageView mIvQrcodeScan;
    private TextView mTvSyncMessagesSetting;
    private TextView mTvFaceLogin;

    private TextView mTvSelectDomain;

    private TextView mTvJoinZoomMeeting;

    private RelativeLayout mRlLoginCompanyView;
    private TextView mTvLoginCompany;
    private ImageView mIvCompanyArrow;

    private RelativeLayout mRlProtocol;
    private TextView mTvServiceButton;
    private TextView mTvPrivacyButton;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO：关闭清空权限
        mShouldClearPermission = false;
        setRetainInstance(true);
        ImSocketService.closeConnection();

        checkEncryptMode(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (havingBottomArea()) {
//            setFakeMaxHeightWhenHavingBottomArea();

        }

        if(null != mTvJoinZoomMeeting) {
            ViewUtil.setVisible(mTvJoinZoomMeeting, AtworkConfig.ZOOM_CONFIG.getEnabled());
        }

        messageSyncSettingStatus();
        initData();
        initObserver();
        registerListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置UI显示为true
        setUserVisibleHint(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        handleMemberActivated(DomainSettingsManager.getInstance().handleUserActivated());
        if (AtworkConfig.H3C_CONFIG) {
            handleIESAccount();
        }

        DomainNetManager.INSTANCE.getMultiDomainsRemote(new BaseNetWorkListener<List<MultiDomainsItem>>() {
            @Override
            public void onSuccess(List<MultiDomainsItem> multiDomainsItems) {
                refreshTvSelectDomainUI();
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {

            }
        });

        refreshCompanyUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        //PermissionsManager.getInstance().clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private boolean havingBottomArea() {
        return AtworkConfig.hasCustomForgetPwdJumpUrl()
                || CustomerHelper.isMinJie(BaseApplicationLike.baseContext);
    }


    @Override
    protected View getFakeStatusBar() {
        return mVFakeStatusbar;
    }

    @Override
    public void onDomainSettingChange() {
        handleMemberActivated(DomainSettingsManager.getInstance().handleUserActivated());
    }

    private void handleMemberActivated(boolean backEndActivate) {
        new Handler().postDelayed(() -> mActivity.runOnUiThread(() -> {

            if(AtworkConfig.LOGIN_VIEW_CONFIG.getForcedHideRegisterBtn()) {
                ViewUtil.setVisible(mTvRegisterButton, false);

            } else {
                ViewUtil.setVisible(mTvRegisterButton, !backEndActivate);

            }


            if (AtworkConfig.hasCustomVerificationCodeUrl()) {
                ViewUtil.setVisible(mTvLoginWithSmsCode, true);


            } else {
                ViewUtil.setVisible(mTvLoginWithSmsCode, !backEndActivate);

            }


            if (AtworkConfig.hasCustomForgetPwdJumpUrl()) {
//                setFakeMaxHeightWhenHavingBottomArea();

                mTvForgetPwd.setText(R.string.forget_pwd);
                mTvForgetPwd.setVisibility(VISIBLE);

            }

        }), 20);
    }


    private void initData() {
        try {
            String loginAvatar = BeeWorks.getInstance().config.copyright.companyLoginIcon;
            if (!TextUtils.isEmpty(loginAvatar)) {
                setAvatarSize(100);
                BeeworksTabHelper.getInstance().setBeeImage(mIvUserAvatar, loginAvatar, 1);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        mProgressDialogHelper = new ProgressDialogHelper(mActivity);
        Bundle bundle = getArguments();
        if (bundle == null) {
            mIsSwitch = false;
        } else {
            mIsSwitch = bundle.getBoolean(ACCOUNT_SWITCH, false);
        }

        if (mIsSwitch) {
            mIvBack.setVisibility(VISIBLE);

        } else {
            mIvBack.setVisibility(GONE);

        }


        setAvatarSize(AtworkConfig.LOGIN_VIEW_CONFIG.getAvatarSize());

        if (getArguments() != null) {
            String username = getArguments().getString(INTENT_LOGIN_USER_NAME);
            mEtUsername.setText(username);
        }


        refreshTvSelectDomainUI();
    }

    private void refreshTvSelectDomainUI() {
        if (!shouldMultiDomain()) {
            mTvSelectDomain.setVisibility(GONE);
            return;
        }


        String domainNameSelected =  DomainNetManager.INSTANCE.getDomainNameSelected();

        if(!StringUtils.isEmpty(domainNameSelected)) {
            mTvSelectDomain.setText(domainNameSelected);
            mTvSelectDomain.setVisibility(VISIBLE);
        }



    }

    private boolean shouldMultiDomain() {
        return null != BeeWorks.getInstance().mLoginPage &&  BeeWorks.getInstance().mLoginPage.mMultiDomain;
    }

    private int mHackApiClickTimes = 0;

    private void registerListener() {

        registerAvatarClickListener();

        mLoginResizeRelativeLayout.setOnClickListener(v -> {
            if (0 == mHackApiClickTimes) {
                mLoginResizeRelativeLayout.postDelayed(()-> mHackApiClickTimes = 0, 2000);
            }

            mHackApiClickTimes++;

            AtworkUtil.hideInput(mActivity);
        });



        mEtUsername.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable text) {
                onChangeLoginBtnStatus();

                onCancelViewFocus(mIvUsernameCancel, text);
            }
        });


        if (shouldMultiDomain()) {
            InputFilter[] filters = new InputFilter[1];
            filters[0] = (source, start, end, dest, dstart, dend) -> {
                if (!StringUtils.isEmptyNoTrim(source.toString())) {
                    StringBuilder newText = new StringBuilder(source.subSequence(start, end).toString());
                    if("#".equals(newText.toString())) {

                        tryPopMultiDomainsSelectDialog();

                        return source.subSequence(0, start);
                    }
                }

                return null; //null to accept the original replacement

            };

            mEtUsername.setFilters(filters);
        }


        mEtPassword.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable text) {
                onChangeLoginBtnStatus();

                onCancelViewFocus(mIvPasswordCancel, text);
                onCancelViewFocus(mIvPasswordShowOrHide, text);


            }
        });

        mEtSecureCode.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable text) {
                onChangeLoginBtnStatus();
            }
        });

        mIvUsernameCancel.setOnClickListener(v -> mEtUsername.setText(StringUtils.EMPTY));

        mIvPasswordCancel.setOnClickListener(v -> mEtPassword.setText(StringUtils.EMPTY));

        mIvPasswordShowOrHide.setOnClickListener(v -> {
            if ((InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT) == mEtPassword.getInputType()) {
                mIvPasswordShowOrHide.setImageResource(R.mipmap.icon_open_eye);
                mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                mEtPassword.setSelection(mEtPassword.getText().length());

            } else {
                mIvPasswordShowOrHide.setImageResource(R.mipmap.icon_close_eye);
                mEtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                mEtPassword.setSelection(mEtPassword.getText().length());

            }

        });

        mBtnLogin.setOnClickListener(v -> {
            if(CommonUtil.isFastClick(2000)) {
                return;
            }
            if(!BasePermissionHelperV2.getNeedAskBasePermission()){
                doLogin();
                return;
            }

            if (canLogin()) {
                AtworkUtil.hideInput(mActivity);

                BasePermissionHelperV2.requestPermissions(mActivity, () -> {
                    doLogin();
                    BasePermissionHelperV2.setNeedAskBasePermission(false);
                    return null;
                });
            }
        });

        mTvLoginWithSmsCode.setOnClickListener(v -> {

//            if(BaseApplicationLike.sIsDebug) {
//
//                String url = "file:///android_asset/www/index_2.1.html?theme=red_envelope&watermark_enable=1";
//                WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url);
//                startActivity(WebViewActivity.getIntent(mActivity, webViewControlAction));
//
//                return;
//            }


            String url;
            if (AtworkConfig.hasCustomVerificationCodeUrl()) {
                url = AtworkConfig.AUTH_ROUTE_CONFIG.getCustomVerificationCodeLogin();
            } else {
                url = String.format(UrlConstantManager.getInstance().getRegisterUrl(), 1);

            }


            WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                    .setUrl(url)
                    .setTitle(getString(R.string.valid_info))
                    .setNeedShare(false);


            startActivity(WebViewActivity.getIntent(mActivity, webViewControlAction));
        });

        mTvRegisterButton.setOnClickListener(v -> {
            String url = String.format(UrlConstantManager.getInstance().getRegisterUrl(), AtworkConfig.H3C_CONFIG ? "h3c" : 0);
            WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                    .setUrl(url)
                    .setTitle(getString(R.string.new_register))
                    .setNeedShare(false);

            startActivity(WebViewActivity.getIntent(mActivity, webViewControlAction));
        });

        mTvForgetPwd.setOnClickListener(v -> {
            if(AtworkConfig.hasCustomForgetPwdJumpUrl()) {
                WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                        .setUrl(AtworkConfig.AUTH_ROUTE_CONFIG.getCustomForgetPwdJumpUrl())
                        .setNeedShare(false);

                startActivity(WebViewActivity.getIntent(mActivity, webViewControlAction));

//                handleCustomForgetAction();

            }
        });

        mIvBack.setOnClickListener(v -> onBackPressed());

        mIvSyncMessagesSetting.setOnClickListener(v -> {
            openSyncMessageSetting();

        });

        if (null != mTvSyncMessagesSetting) {
            mTvSyncMessagesSetting.setOnClickListener(view -> {
                openSyncMessageSetting();
            });
        }

        mIvSecureCodeRefresh.setOnClickListener(view -> {
            LoginHelper.getSecureCodeRemote(mIvSecureCode);
        });


        //处理分割线获取焦点时的特效
        mEtUsername.setOnFocusChangeListener((v, hasFocus) -> {
            ViewHelper.focusOnLine(mVLineUsername, hasFocus);

            if (hasFocus) {
                onCancelViewFocus(mIvUsernameCancel, mEtUsername.getText());
            } else {
                mIvUsernameCancel.setVisibility(View.INVISIBLE);
            }

        });

        mEtPassword.setOnFocusChangeListener((v, hasFocus) -> {
            ViewHelper.focusOnLine(mVLinePassword, hasFocus);

            if (hasFocus) {
                onCancelViewFocus(mIvPasswordCancel, mEtPassword.getText());
                onCancelViewFocus(mIvPasswordShowOrHide, mEtPassword.getText());

                if(!mSecureCodeLayout.isShown()) {
                    handleScrollWhenFocusInput();
                    mLastInputHadFocus = true;
                }

            } else {
                mIvPasswordCancel.setVisibility(View.INVISIBLE);
                mIvPasswordShowOrHide.setVisibility(View.INVISIBLE);
            }
        });

        mEtPassword.setOnClickListener(v -> {

            if(!mSecureCodeLayout.isShown()) {
                handleScrollWhenFocusInput();
                mLastInputHadFocus = true;
            }
        });

        mEtSecureCode.setOnFocusChangeListener((v, hasFocus) -> {
            ViewHelper.focusOnLine(mVLineSecureCode, hasFocus);

            if (hasFocus) {
                handleScrollWhenFocusInput();
                mLastInputHadFocus = true;
            }

        });


        mEtSecureCode.setOnClickListener(v -> {
            handleScrollWhenFocusInput();
            mLastInputHadFocus = true;
        });


        mIvQrcodeScan.setOnClickListener(v -> {
            FragmentActivity activity = getActivity();
            if(null == activity) {
                return;
            }

            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(activity, new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    Intent intent = QrcodeScanActivity.getIntent(activity);
                    activity.startActivity(intent);
                }

                @Override
                public void onDenied(String permission) {
                    AtworkUtil.popAuthSettingAlert(activity, permission);
                }
            });

        });


        if(null != mTvFaceLogin) {
            mTvFaceLogin.setOnClickListener(v -> {
                WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                        .setUrl(AtworkConfig.AUTH_ROUTE_CONFIG.getCustomFaceLoginJumpUrl())
                        .setNeedShare(false);

                startActivity(WebViewActivity.getIntent(mActivity, webViewControlAction));
            });
        }

        mTvServiceButton.setOnClickListener(v -> {
            String urlService = UrlConstantManager.getInstance().getProtocolProtocolUrl();
            WebViewControlAction webViewControlActionService = WebViewControlAction.newAction().setTitle(BaseApplicationLike.baseContext.getString(R.string.policy_service)).setUrl(urlService).setNeedShare(false);
            Intent intentJumpService = WebViewActivity.getIntent(getActivity(), webViewControlActionService);
            startActivity(intentJumpService);
        });
        mTvPrivacyButton.setOnClickListener(v -> {
            String urlService = UrlConstantManager.getInstance().getProtocolPrivacyUrl();
            WebViewControlAction webViewControlActionService = WebViewControlAction.newAction().setTitle(BaseApplicationLike.baseContext.getString(R.string.policy_protocol)).setUrl(urlService).setNeedShare(false);
            Intent intentJumpService = WebViewActivity.getIntent(getActivity(), webViewControlActionService);
            startActivity(intentJumpService);
        });



        mTvSelectDomain.setOnClickListener(view -> {
            tryPopMultiDomainsSelectDialog();
        });

        if (null != mTvJoinZoomMeeting) {
            mTvJoinZoomMeeting.setOnClickListener(view -> {
                Intent intent = ZoomJoinVoipMeetingActivity.getIntent(mActivity);
                startActivity(intent);
            });
        }


        if(null != mRlLoginCompanyView) {
            mRlLoginCompanyView.setOnClickListener(v -> handleClickLoginCompanyView());
        }

    }

    private void handleClickLoginCompanyView() {
        String qrScanBindingText = getStrings(R.string.qr_scan_bind_new_company);
        if(qrScanBindingText.equals(mTvLoginCompany.getText().toString())) {
            routeQrScan();

            return;
        }


        mIvCompanyArrow.animate().rotation(180).setDuration(200);

        //pop select dialog
        List<LiteBindConfig> bindConfigs = new ArrayList<>(LiteManager.INSTANCE.getBindConfigs());
        List<String> itemList = bindConfigs.stream().map(LiteBindConfig::getDomainName).collect(Collectors.toList());
        itemList.add(qrScanBindingText);


        W6sSelectDialogFragment commonPopSelectListDialogFragment = new W6sSelectDialogFragment();
        commonPopSelectListDialogFragment
                .setData(new CommonPopSelectData(itemList, null))
                .setTextColor(ListUtil.makeSingleList(qrScanBindingText), "#1A98FF");

        commonPopSelectListDialogFragment.setOnClickItemListener((position, value) -> {
            if(qrScanBindingText.equals(value)) {
                routeQrScan();
                return;
            }

            LiteManager.INSTANCE.updateBindConfigCurrent(bindConfigs.get(position));
            refreshCompanyUI();

        });

        commonPopSelectListDialogFragment.setOnDismissListener(dialogInterface -> {
            mIvCompanyArrow.animate().rotation(0).setDuration(200);
            return null;
        });



        commonPopSelectListDialogFragment.show(getChildFragmentManager(), "commonPopSelectListDialog");
    }

    private void routeQrScan() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                Intent intent = QrcodeScanActivity.getIntent(mActivity);
                startActivity(intent);
            }

            @Override
            public void onDenied(String permission) {
                AtworkUtil.popAuthSettingAlert(mActivity, permission);
            }
        });
    }

    private void tryPopMultiDomainsSelectDialog() {
        List<MultiDomainsItem> multiDomainsData = DomainNetManager.INSTANCE.getMultiDomainsData();
        if(!ListUtil.isEmpty(multiDomainsData)) {

            popMultiDomainsSelectDialog(multiDomainsData);


        } else {

            ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(getActivity());
            progressDialogHelper.show();

            DomainNetManager.INSTANCE.getMultiDomainsRemote(new BaseNetWorkListener<List<MultiDomainsItem>>() {
                @Override
                public void onSuccess(List<MultiDomainsItem> multiDomainsItems) {
                    progressDialogHelper.dismiss();

                    popMultiDomainsSelectDialog(multiDomainsItems);

                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    progressDialogHelper.dismiss();
                    ErrorHandleUtil.handleError(errorCode, errorMsg);
                }
            });

        }
    }

    private void popMultiDomainsSelectDialog(List<MultiDomainsItem> multiDomainsData) {

        if(ListUtil.isEmpty(multiDomainsData)) {
            toastOver("没有更多的域");
            return;
        }

        W6sSelectDialogFragment commonPopSelectListDialogFragment = new W6sSelectDialogFragment();
        List<String> domainNames = CollectionsKt.map(multiDomainsData, MultiDomainsItem::getDomainName);

        String domainNameSelected =  DomainNetManager.INSTANCE.getDomainNameSelected();

        commonPopSelectListDialogFragment.setData(new CommonPopSelectData(domainNames, domainNameSelected));
        commonPopSelectListDialogFragment.setOnClickItemListener((position, value) -> {
            MultiDomainsItem domainItemSelected = multiDomainsData.get(position);
            if(null != domainItemSelected) {
                mTvSelectDomain.setText(domainItemSelected.getDomainName());
                CommonShareInfo.setDomainId(AtworkApplicationLike.baseContext, domainItemSelected.getDomainId());
                AtworkConfig.DOMAIN_ID = domainItemSelected.getDomainId();

                mTvSelectDomain.setVisibility(VISIBLE);

            }
        });

        commonPopSelectListDialogFragment.show(getChildFragmentManager(), "commonPopSelectListDialog");
    }


    private void handleScrollWhenFocusInput() {
        if(0 < mContinueScrollViewToSeeBtnCount && mKeyboardShow && !mLastInputHadFocus) {
            mContinueScrollViewToSeeBtnCount--;
            scrollRootView();
        }
    }

    private void onCancelViewFocus(ImageView cancelView, Editable text) {
        if (null != text && 0 < text.length()) {
            cancelView.setVisibility(VISIBLE);
        } else {
            cancelView.setVisibility(View.INVISIBLE);

        }
    }


    private void onChangeLoginBtnStatus() {
//        if(mEtSecureCode.isShown() && 0 >= mEtSecureCode.getText().length()) {
//            mBtnLogin.setBackgroundResource(R.drawable.shape_login_rect_input_nothing);
//            return;
//        }

        if (0 >= mEtUsername.getText().length()) {
            mBtnLogin.setBackgroundResource(R.drawable.shape_login_rect_input_nothing);
            return;
        }

        if (0 >= mEtPassword.getText().length()) {
            mBtnLogin.setBackgroundResource(R.drawable.shape_login_rect_input_nothing);
            return;
        }

        mBtnLogin.setBackgroundResource(R.drawable.shape_login_rect_input_something);

    }

    private void doLogin() {
        if(mTvSelectDomain.isShown() && StringUtils.isEmpty(CommonShareInfo.getDomainId(AtworkApplicationLike.baseContext))) {
            toastOver(R.string.please_select_domain);
            return;
        }


        mProgressDialogHelper.show(getResources().getString(R.string.login_message));
        String userName = mEtUsername.getText().toString().trim().toLowerCase();

        String password = mEtPassword.getText().toString();
        String secureCode = mEtSecureCode.getText().toString();

        int selectionStart = userName.length();
        int selectionEnd = selectionStart;
        mEtUsername.setText(userName);
        mEtUsername.setSelection(selectionStart, selectionEnd);
        setOnCancelViewStatus(mIvPasswordCancel, View.INVISIBLE);
        setOnCancelViewStatus(mIvPasswordShowOrHide, View.INVISIBLE);
        setOnCancelViewStatus(mIvUsernameCancel, View.INVISIBLE);

        MessageNoticeManager.getInstance().clear();

        LoginControlViewBundle loginControlViewBundle = LoginControlViewBundle.newBuilder()
                .setDialogHelper(mProgressDialogHelper)
                .setSecureCodeLayout(mSecureCodeLayout)
                .setSecureCodeView(mIvSecureCode)
                .setIvFakeHeader(mIvFakeHeader)
                .setTvForgetPwd(mTvForgetPwd)
                .setEtPassword(mEtPassword)
                .build();

        LoginHandleBundle loginHandleBundle = new LoginHandleBundle();
        loginHandleBundle.setUsername(userName);
        loginHandleBundle.setPsw(password);
        loginHandleBundle.setSecureCode(secureCode);
        loginHandleBundle.setLoginControlViewBundle(loginControlViewBundle);
        loginHandleBundle.setRefreshCode(mSecureCodeLayout.isShown());

        //登录认证
        LoginHelper.doLogin((BaseActivity) mActivity, loginHandleBundle);
    }


    private boolean canLogin() {
        return !(StringUtils.isEmpty(mEtUsername.getText().toString()) || StringUtils.isEmpty(mEtPassword.getText().toString())
                || (mSecureCodeLayout.isShown() && TextUtils.isEmpty(mEtSecureCode.getText().toString())));
    }

    private void initObserver() {
        mLoginResizeRelativeLayout.setOnKeyboardChangeListener(state -> {
            if (state == KeyboardRelativeLayout.KEYBOARD_STATE_SHOW) {

                LogUtil.e("KeyboardRelativeLayout.KEYBOARD_STATE_SHOW");

                if (!mKeyboardShow) {
//                    setForgetPwdBtnBelowAnchor();

                    mKeyboardShow = true;

                    scrollRootView();

                }



            } else if (state == KeyboardRelativeLayout.KEYBOARD_STATE_HIDE) {
                LogUtil.e("KeyboardRelativeLayout.KEYBOARD_STATE_HIDE");

                if (mKeyboardShow) {
//                    clearForgetPwdBtnBelowAnchor();

                    mKeyboardShow = false;
                    mContinueScrollViewToSeeBtnCount = 1;
                    mLastInputHadFocus = false;
                }

            }
        });


        mLoginResizeRelativeLayout.setOnKeyBoardHeightListener(height -> CommonShareInfo.setKeyBoardHeight(mActivity, height));
    }

    private void scrollRootView() {
        mLoginScrollView.postDelayed(() -> {
            int scrollBy = getScrollBy();
            mLoginScrollView.smoothScrollBy(0, scrollBy);
        }, 0);
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    protected void findViews(View view) {

        mVFakeStatusbar = view.findViewById(R.id.v_fake_statusbar);
        mLoginResizeRelativeLayout = view.findViewById(R.id.login_layout);
        mLoginScrollView = view.findViewById(R.id.login_scroll);
        mEtUsername = view.findViewById(R.id.et_login_username_EditText);
        mIvUsernameCancel = view.findViewById(R.id.iv_login_username_cancel_btn);
        mEtPassword = view.findViewById(R.id.et_login_password);
        mIvPasswordCancel = view.findViewById(R.id.iv_login_password_cancel_btn);
        mIvPasswordShowOrHide = view.findViewById(R.id.iv_pwd_input_show_or_hide);
        mBtnLogin = view.findViewById(R.id.login_login_button);
        mTvRegisterButton = view.findViewById(R.id.tv_login_register_button);
        mTvLoginWithSmsCode = view.findViewById(R.id.tv_login_with_sms_code);

        mRlBottomArea = view.findViewById(R.id.rl_bottom_area);
        mTvForgetPwd = view.findViewById(R.id.tv_forget_pwd);
        mIvFakeHeader = view.findViewById(R.id.iv_fake_header_bg);
        mIvUserAvatar = view.findViewById(R.id.iv_login_user_avatar);

        mIvQrcodeScan = view.findViewById(R.id.iv_scan_qrcode);

        mRlProtocol = view.findViewById(R.id.rl_protocol);
        mTvServiceButton = view.findViewById(R.id.tv_login_service_button);
        mTvPrivacyButton = view.findViewById(R.id.tv_login_privacy_button);

        if(AtworkConfig.PROTOCOL){
            mRlProtocol.setVisibility(VISIBLE);
            mTvServiceButton.setVisibility(VISIBLE);
            mTvPrivacyButton.setVisibility(VISIBLE);

        }else{
            mRlProtocol.setVisibility(GONE);
            mTvServiceButton.setVisibility(GONE);
            mTvPrivacyButton.setVisibility(GONE);
        }

        mIvBack = view.findViewById(R.id.iv_back);
        mIvSyncMessagesSetting = view.findViewById(R.id.iv_sync_messages_setting);
        mTvSyncMessagesSetting = view.findViewById(R.id.tv_sync_messages_setting);

//        mTvFaceLogin = view.findViewById(R.id.tv_face_login);
        mTvFaceLogin = view.findViewById(getResources().getIdentifier("tv_face_login", "id", AtworkApplicationLike.baseContext.getPackageName()));

        mSecureCodeLayout = view.findViewById(R.id.secure_code_layout);
        mIvSecureCode = mSecureCodeLayout.findViewById(R.id.iv_login_secure_code);
        mIvSecureCodeRefresh = mSecureCodeLayout.findViewById(R.id.iv_login_secure_code_refresh);
        mEtSecureCode = mSecureCodeLayout.findViewById(R.id.et_login_secure_code);
        mCopyRightView = view.findViewById(R.id.copyright_layout);
        mCopyRightView.setVisibility(CustomerHelper.isAlog(mActivity) ? VISIBLE : GONE);

        mVLineUsername = view.findViewById(R.id.v_username_input_line);
        mVLinePassword = view.findViewById(R.id.v_password_input_line);
        mVLineSecureCode = view.findViewById(R.id.v_line_secure_code_input);

        mTvSelectDomain = view.findViewById(R.id.tv_select_domain);
        mTvJoinZoomMeeting = view.findViewById(R.id.tv_join_zoom_meeting);

        mRlLoginCompanyView = view.findViewById(R.id.login_company_view);
        mTvLoginCompany = view.findViewById(R.id.tv_login_company);
        mIvCompanyArrow = view.findViewById(R.id.iv_login_company_arrow);

        ImageViewUtil.setMaxHeight(mIvFakeHeader, (int) (ScreenUtils.getScreenHeight(BaseApplicationLike.baseContext) * 0.45));

    }

    private void refreshCompanyUI() {
        LiteBindConfig liteBindConfig = LiteManager.INSTANCE.getBindConfigCurrent();
        if(BeeWorks.getInstance().config.isLite()) {
            mRlLoginCompanyView.setVisibility(VISIBLE);

            if(null == liteBindConfig) {
                mTvLoginCompany.setText(R.string.qr_scan_bind_new_company);
                mIvCompanyArrow.setVisibility(View.INVISIBLE);

            } else {
                mTvLoginCompany.setText(liteBindConfig.getDomainName());
                mIvCompanyArrow.setVisibility(VISIBLE);
            }

        } else {
            mRlLoginCompanyView.setVisibility(GONE);

        }
    }


    private void messageSyncSettingStatus() {
        if(null == mTvSyncMessagesSetting) {
            return;
        }

        if (CustomerHelper.isMinJie(mActivity)) {
            mIvSyncMessagesSetting.setVisibility(GONE);
            mTvSyncMessagesSetting.setVisibility(VISIBLE);
            return;
        }
        mIvSyncMessagesSetting.setVisibility(VISIBLE);
        mTvSyncMessagesSetting.setVisibility(View.GONE);
    }


    private int getScrollBy() {

        int scrollBy = DensityUtil.dip2px(39) + mBtnLogin.getHeight();
        return scrollBy;

    }


    @Override
    protected boolean onBackPressed() {
        BeeWorks beeWorks = BeeWorks.getInstance();
        if (beeWorks.isBeeWorksFaceBioSettingEnable() && beeWorks.config.beeWorksSetting.getFaceBioSetting().getFaceBioAuth()) {
            Intent intent = AccountLoginActivity.getLoginIntent(mActivity);
            startActivity(intent);
            mActivity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            mActivity.finish();
            return true;
        }
        if (mIsSwitch) {
            Intent intent = LoginWithAccountActivity.getIntent(mActivity, false);
            startActivity(intent);
            mActivity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            mActivity.finish();
        } else {
            mActivity.moveTaskToBack(true);
        }
        return true;
    }


    private void setOnCancelViewStatus(ImageView view, int visible) {
        view.setVisibility(visible);
    }


    private void handleIESAccount() {
        boolean result = IESInflaterManager.getInstance().initIES(mActivity);
        if (!result) {
            Logger.e("IES", "init ies result = " + result);
        }
        int requestLoginResult = IESInflaterManager.getInstance().requestInodeLogin(mActivity);
        if (requestLoginResult != 0) {
            Logger.e("IES", "requestIesLogin result = " + requestLoginResult);
            return;
        }
        final IES ies = IESInflaterManager.getInstance().getIESAccount(mActivity);
        if (ies == null) {
            return;
        }
        //如果华三Inode登录了，模拟登录
        new Handler().postDelayed(() -> {
            if (TextUtils.isEmpty(ies.iesAccountName) || TextUtils.isEmpty(ies.iesPassword)) {
                return;
            }
            mEtUsername.setText(ies.iesAccountName);
            mEtPassword.setText(ies.iesPassword);
            doLogin();
        }, 100);
    }

    private void checkEncryptMode(Context context) {
        if (!UserRemoveHelper.isEncryptModeMatch(context)) {
            startClean(context);

        } else {
            CommonShareInfo.resetEncryptMode(context);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void startClean(Context context) {
        ProgressDialogHelper dialogHelper = new ProgressDialogHelper(context);
        dialogHelper.show(false, -1);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                UserRemoveHelper.cleanAllUsers(context);

                CommonShareInfo.resetEncryptMode(context);

                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {

                dialogHelper.dismiss();
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    private void setFakeMaxHeightWhenHavingBottomArea() {
        mRlBottomArea.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                try {
                    int flBottomAreaHeight = mRlBottomArea.getHeight();
                    int secureLayoutHeight = mSecureCodeLayout.getHeight();

                    LogUtil.e("flBottomAreaHeight -> " + flBottomAreaHeight);
                    ImageViewUtil.setMaxHeight(mIvFakeHeader, (int) (ScreenUtils.getScreenHeight(BaseApplicationLike.baseContext) * 0.45) - flBottomAreaHeight - secureLayoutHeight);

                    mRlBottomArea.getViewTreeObserver().removeOnPreDrawListener(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                return false;
            }
        });
    }

    private void setForgetPwdBtnBelowAnchor() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRlBottomArea.getLayoutParams();

        layoutParams.addRule(RelativeLayout.BELOW, R.id.login_login_button);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        mRlBottomArea.setLayoutParams(layoutParams);


    }

    private void clearForgetPwdBtnBelowAnchor() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRlBottomArea.getLayoutParams();
        layoutParams.addRule(RelativeLayout.BELOW, -1);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
        mRlBottomArea.setLayoutParams(layoutParams);
    }

    private void setAvatarSize(int size) {
        ViewGroup.LayoutParams layoutParams = mIvUserAvatar.getLayoutParams();
        layoutParams.height = DensityUtil.dip2px(size);
        layoutParams.width = DensityUtil.dip2px(size);

        mIvUserAvatar.setLayoutParams(layoutParams);
    }

    @Override
    ImageView getAvatarIv() {
        return mIvUserAvatar;
    }
}

