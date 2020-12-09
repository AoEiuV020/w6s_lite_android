package com.foreveross.atwork.modules.login.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import com.foreverht.workplus.ui.component.dialogFragment.W6sSelectDialogFragment;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseNetWorkListener;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.auth.model.LoginDeviceNeedAuthResult;
import com.foreveross.atwork.api.sdk.auth.model.LoginWithFaceBioRequest;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.model.IES;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.domain.MultiDomainsItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
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
import com.foreveross.atwork.manager.IESInflaterManager;
import com.foreveross.atwork.manager.MessageNoticeManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.chat.activity.SyncMessagesSettingActivity;
import com.foreveross.atwork.modules.domain.DomainNetManager;
import com.foreveross.atwork.modules.lite.manager.LiteManager;
import com.foreveross.atwork.modules.lite.module.LiteBindConfig;
import com.foreveross.atwork.modules.login.activity.LoginActivity;
import com.foreveross.atwork.modules.login.listener.LoginNetListener;
import com.foreveross.atwork.modules.login.model.LoginControlViewBundle;
import com.foreveross.atwork.modules.login.model.LoginHandleBundle;
import com.foreveross.atwork.modules.login.service.LoginService;
import com.foreveross.atwork.modules.login.util.BasePermissionHelperV2;
import com.foreveross.atwork.modules.login.util.LoginHelper;
import com.foreveross.atwork.modules.meeting.activity.ZoomJoinVoipMeetingActivity;
import com.foreveross.atwork.modules.qrcode.activity.QrcodeScanActivity;
import com.foreveross.atwork.support.BaseActivity;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.ImageViewUtil;
import com.foreveross.atwork.utils.ViewHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import kotlin.collections.CollectionsKt;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.foreveross.atwork.modules.login.activity.LoginWithAccountActivity.INTENT_FACE_BIO_LOGIN_AUTH;
import static com.foreveross.atwork.modules.login.activity.LoginWithAccountActivity.INTENT_FACE_BIO_LOGIN_USERNAME;
import static com.foreveross.atwork.modules.login.fragment.AccountLoginFragment.REQUEST_CAMERA_CODE;


public class LoginWithAccountFragment extends BaseLoginFragment implements View.OnClickListener {

    private static final String TAG = LoginWithAccountFragment.class.getSimpleName();

    private Activity mActivity;

    private View mVFakeStatusbar;

    private ScrollView mSvRoot;
    private KeyboardRelativeLayout mKeyboardRelativeLayout;

    private TextView mSwitchAccount;
    private ImageView mIvAvatar;
    private ImageView mIvCancelInput;
    private ImageView mIvShowOrHidePwd;
    private TextView mUsername;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private ImageView mIvFakeHeader;
    private View mVPasswordInputLayout;

    private TextView mTvRegisterButton;
    private TextView mTvLoginWithSmsCode;

    private ProgressDialogHelper mProgressDialogHelper;
    private View mCopyRightView;

    private View mSecureCodeLayout;
    private ImageView mIvSecureCode;
    private ImageView mIvSecureCodeRefresh;
    private EditText mEtSecureCode;

    private View mVLinePwdInput;
    private View mVLineSecureCodeInput;

    private TextView mTvForgetPwd;
    private TextView mTvFaceBioLogin;

    private RelativeLayout mRlBottomArea;

    private ImageView mIvSyncMessagesSetting;
    private TextView mTvSyncMessagesSetting;

    private TextView mTvFaceLogin;

    private TextView mTvSelectDomain;

    private TextView mTvJoinZoomMeeting;

    private RelativeLayout mRlProtocol;
    private TextView mTvServiceButton;
    private TextView mTvPrivacyButton;

    private RelativeLayout mRlLoginCompanyView;
    private TextView mTvLoginCompany;
    private ImageView mIvCompanyArrow;

    private boolean mKeyboardShow = false;
    private boolean mLastInputHadFocus = false;
    private int mContinueScrollViewToSeeBtnCount = 1;

    User preLoginUser;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO:关闭清空权限
        mShouldClearPermission = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_with_account, container, false);
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
        registerListener();


    }



    @Override
    public void onResume() {
        super.onResume();
        initData();
        handleMemberActivated();

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDomainSettingChange() {
        handleMemberActivated();
    }

    private void showSyncMessageSetting() {
        FragmentActivity activity = getActivity();
        if (null != activity) {
            Intent intent = SyncMessagesSettingActivity.Companion.getIntent(activity);
            startActivity(intent);
        }
    }

    private void registerListener() {
        registerAvatarClickListener();

        mIvSyncMessagesSetting.setOnClickListener(v -> {

            showSyncMessageSetting();
        });

        if (null != mTvSyncMessagesSetting) {
            mTvSyncMessagesSetting.setOnClickListener(v ->{
                showSyncMessageSetting();
            });
        }


        if (null != mTvLoginWithSmsCode) {
            mTvLoginWithSmsCode.setOnClickListener(v -> {
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
        }


        mTvForgetPwd.setOnClickListener(v -> {
            if(AtworkConfig.hasCustomForgetPwdJumpUrl()) {
                WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                        .setUrl(AtworkConfig.AUTH_ROUTE_CONFIG.getCustomForgetPwdJumpUrl())
                        .setNeedShare(false);

                startActivity(WebViewActivity.getIntent(mActivity, webViewControlAction));

//                handleCustomForgetAction();

            }
        });


        //使登录按钮不被键盘挡住, 优化体验
        mKeyboardRelativeLayout.setOnKeyboardChangeListener(state -> {
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

        mKeyboardRelativeLayout.setOnClickListener(v -> hideKeyBoard());

        mKeyboardRelativeLayout.setOnKeyBoardHeightListener(height -> CommonShareInfo.setKeyBoardHeight(mActivity, height));


        mSwitchAccount.setOnClickListener(this);
        mIvCancelInput.setOnClickListener(this);
        mIvShowOrHidePwd.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);

        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setPasswordStatus(!TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEtPassword.setOnFocusChangeListener((v, hasFocus) -> {
            ViewHelper.focusOnLine(mVLinePwdInput, hasFocus);


            if(hasFocus) {
                if(!mSecureCodeLayout.isShown()) {
                    handleScrollWhenFocusInput();
                    mLastInputHadFocus = true;
                }
            }
        });


        mEtPassword.setOnClickListener(v -> {

            if(!mSecureCodeLayout.isShown()) {
                handleScrollWhenFocusInput();
                mLastInputHadFocus = true;
            }
        });


        mEtSecureCode.setOnFocusChangeListener((v, hasFocus) -> {
            ViewHelper.focusOnLine(mVLineSecureCodeInput, hasFocus);

            if (hasFocus) {
                handleScrollWhenFocusInput();
                mLastInputHadFocus = true;
            }

        });

        mIvSecureCodeRefresh.setOnClickListener(view -> {
            LoginHelper.getSecureCodeRemote(mIvSecureCode);
        });


        mEtSecureCode.setOnClickListener(v -> {
            handleScrollWhenFocusInput();
            mLastInputHadFocus = true;
        });


        if(null != mTvFaceLogin) {
            mTvFaceLogin.setOnClickListener(v -> {
                String faceLoginJumpUrl = AtworkConfig.AUTH_ROUTE_CONFIG.getCustomFaceLoginJumpUrl();

                faceLoginJumpUrl += "?username=" + LoginUserInfo.getInstance().getLoginUserRealUserName(AtworkApplicationLike.baseContext) + "&name=" + LoginUserInfo.getInstance().getLoginUserName(AtworkApplicationLike.baseContext);

                WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                        .setUrl(faceLoginJumpUrl)
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
                .setData(new com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData(itemList, null))
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


    private void popMultiDomainsSelectDialog(List<MultiDomainsItem> multiDomainsData) {
        if(ListUtil.isEmpty(multiDomainsData)) {
            toastOver("没有更多的域");
            return;
        }

        W6sSelectDialogFragment commonPopSelectListDialogFragment = new W6sSelectDialogFragment();
        List<String> domainNames = CollectionsKt.map(multiDomainsData, MultiDomainsItem::getDomainName);

        String domainNameSelected =  DomainNetManager.INSTANCE.getDomainNameSelected();

        commonPopSelectListDialogFragment.setData(new com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData(domainNames, domainNameSelected));
        commonPopSelectListDialogFragment.setOnClickItemListener((position, value) -> {
            MultiDomainsItem domainItemSelected = multiDomainsData.get(position);
            if(null != domainItemSelected) {
                mTvSelectDomain.setText(domainItemSelected.getDomainName());
                CommonShareInfo.setDomainId(AtworkApplicationLike.baseContext, domainItemSelected.getDomainId());
                AtworkConfig.DOMAIN_ID = domainItemSelected.getDomainId();

            }
        });

        commonPopSelectListDialogFragment.show(getFragmentManager(), "commonPopSelectListDialog");
    }

    private void handleScrollWhenFocusInput() {
        if(0 < mContinueScrollViewToSeeBtnCount && mKeyboardShow && !mLastInputHadFocus) {
            mContinueScrollViewToSeeBtnCount--;
            scrollRootView();
        }
    }

    private void scrollRootView() {
        mSvRoot.postDelayed(() -> {
            int scrollBy = getScrollBy();
            mSvRoot.smoothScrollBy(0, scrollBy);
        }, 0);
    }

    private void setForgetPwdBtnBelowAnchor() {

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRlBottomArea.getLayoutParams();

        layoutParams.addRule(RelativeLayout.BELOW, R.id.switch_account);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        mRlBottomArea.setLayoutParams(layoutParams);


    }

    private void clearForgetPwdBtnBelowAnchor() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRlBottomArea.getLayoutParams();
        layoutParams.addRule(RelativeLayout.BELOW, -1);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
        mRlBottomArea.setLayoutParams(layoutParams);
    }

    private int getScrollBy() {

        int scrollBy = DensityUtil.dip2px(29) + mBtnLogin.getHeight();
        return scrollBy;

    }

    @Override
    protected void findViews(View view) {
        mVFakeStatusbar = view.findViewById(R.id.v_fake_statusbar);
        mSvRoot = view.findViewById(R.id.sv_root);
        mKeyboardRelativeLayout = view.findViewById(R.id.login_with_account_layout);

        mSwitchAccount = view.findViewById(R.id.switch_account);
        mIvAvatar = view.findViewById(R.id.iv_user_avatar);
        mIvCancelInput = view.findViewById(R.id.iv_cancel_pwd_input);
        mIvShowOrHidePwd = view.findViewById(R.id.iv_pwd_input_show_or_hide);
        mUsername = view.findViewById(R.id.tv_user_name);
        mEtPassword = view.findViewById(R.id.et_password_input);
        mBtnLogin = view.findViewById(R.id.login_button);
        mIvFakeHeader = view.findViewById(R.id.iv_fake_header_bg);
        mVPasswordInputLayout = view.findViewById(R.id.password_input_layout);
        mVLinePwdInput = view.findViewById(R.id.v_line_pwd_input);
        mVLineSecureCodeInput = view.findViewById(R.id.v_line_secure_code_input);

        mTvRegisterButton = view.findViewById(R.id.tv_login_register_button);
        mTvLoginWithSmsCode = view.findViewById(R.id.tv_login_with_sms_code);

        mSecureCodeLayout = view.findViewById(R.id.secure_code_layout);
        mIvSecureCode = mSecureCodeLayout.findViewById(R.id.iv_login_secure_code);
        mIvSecureCodeRefresh = mSecureCodeLayout.findViewById(R.id.iv_login_secure_code_refresh);
        mEtSecureCode = mSecureCodeLayout.findViewById(R.id.et_login_secure_code);
        mCopyRightView = getView().findViewById(R.id.copyright_layout);
        mTvFaceBioLogin = view.findViewById(R.id.tv_face_login);

        mCopyRightView.setVisibility(CustomerHelper.isAlog(mActivity) ? View.VISIBLE : View.GONE);

        mRlBottomArea = view.findViewById(R.id.rl_bottom_area);

        mTvForgetPwd = view.findViewById(R.id.tv_login_forget_password);

        mIvSyncMessagesSetting = view.findViewById(R.id.iv_sync_messages_setting);
        mTvSyncMessagesSetting = view.findViewById(R.id.tv_sync_messages_setting);

//        mTvFaceLogin = view.findViewById(R.id.tv_face_login);
        mTvFaceLogin = view.findViewById(getResources().getIdentifier("tv_face_login", "id", AtworkApplicationLike.baseContext.getPackageName()));

        mTvSelectDomain = view.findViewById(R.id.tv_select_domain);
        mTvJoinZoomMeeting = view.findViewById(R.id.tv_join_zoom_meeting);


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

        mRlLoginCompanyView = view.findViewById(R.id.login_company_view);
        mTvLoginCompany = view.findViewById(R.id.tv_login_company);
        mIvCompanyArrow = view.findViewById(R.id.iv_login_company_arrow);

        ImageViewUtil.setMaxHeight(mIvFakeHeader, (int) (ScreenUtils.getScreenHeight(BaseApplicationLike.baseContext) * 0.45));
    }

    private void messageSyncSettingStatus() {
        if(null == mIvSyncMessagesSetting) {
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

    @Override
    protected View getFakeStatusBar() {
        return mVFakeStatusbar;
    }

    private void initData() {
        preLoginUser = UserManager.getInstance().queryLocalUser(LoginUserInfo.getInstance().getLoginUserId(getActivity()));
        if (preLoginUser == null) {
            return;
        }
        setAvatar(preLoginUser);

        mUsername.setText(preLoginUser.getShowName());

        mProgressDialogHelper = new ProgressDialogHelper(mActivity);


        refreshTvSelectDomainUI();

    }

    private void refreshTvSelectDomainUI() {
        if (null == BeeWorks.getInstance().mLoginPage || !BeeWorks.getInstance().mLoginPage.mMultiDomain) {
            mTvSelectDomain.setVisibility(GONE);
            return;
        }


        String domainNameSelected =  DomainNetManager.INSTANCE.getDomainNameSelected();

        if(!StringUtils.isEmpty(domainNameSelected)) {
            mTvSelectDomain.setText(domainNameSelected);
        }

//        mTvSelectDomain.setVisibility(VISIBLE);

    }

    private void setAvatar(User preLoginUser) {
        //默认头像(带阴影)跟正常头像的比例为 ->  396 : 300
        //对应的换算则算出 imageView 大小为  396 / 300 * 100 = 132
        setAvatarSize(AtworkConfig.LOGIN_VIEW_CONFIG.getAvatarSize());

        ImageCacheHelper.displayImageByMediaId(preLoginUser.mAvatar, mIvAvatar, getLoginAvatarImageOptions(), new ImageCacheHelper.ImageLoadedListener() {
            @Override
            public void onImageLoadedComplete(Bitmap bitmap) {
                setAvatarSize(100);
            }

            @Override
            public void onImageLoadedFail() {
                setAvatarSize(AtworkConfig.LOGIN_VIEW_CONFIG.getAvatarSize());
            }
        });
    }

    private void setAvatarSize(int size) {
        ViewGroup.LayoutParams layoutParams = mIvAvatar.getLayoutParams();
        layoutParams.height = DensityUtil.dip2px(size);
        layoutParams.width = DensityUtil.dip2px(size);

        mIvAvatar.setLayoutParams(layoutParams);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.switch_account:
                onAccountSwitch();
                break;

            case R.id.iv_cancel_pwd_input:
                mEtPassword.setText("");
                break;

            case R.id.iv_pwd_input_show_or_hide:
                if ((InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT) == mEtPassword.getInputType()) {
                    mIvShowOrHidePwd.setImageResource(R.mipmap.icon_open_eye);
                    mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    mEtPassword.setSelection(mEtPassword.getText().length());

                } else {
                    mIvShowOrHidePwd.setImageResource(R.mipmap.icon_close_eye);
                    mEtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    mEtPassword.setSelection(mEtPassword.getText().length());

                }
                break;

            case R.id.login_button:
                hideKeyBoard();
                if(CommonUtil.isFastClick(2000)) {
                    return;
                }
                if(!BasePermissionHelperV2.getNeedAskBasePermission()){
                    doLogin();
                    return;
                }
                BasePermissionHelperV2.requestPermissions(mActivity, () -> {
                    doLogin();
                    BasePermissionHelperV2.setNeedAskBasePermission(false);

                    return null;
                });

                break;
        }
    }

    private void setPasswordStatus(boolean hadInputText) {
        if (hadInputText) {
            mIvCancelInput.setVisibility(View.VISIBLE);
            mIvShowOrHidePwd.setVisibility(View.VISIBLE);
            mBtnLogin.setBackgroundResource(R.drawable.shape_login_rect_input_something);

            return;
        }
        mIvCancelInput.setVisibility(View.INVISIBLE);
        mIvShowOrHidePwd.setVisibility(View.INVISIBLE);
        mBtnLogin.setBackgroundResource(R.drawable.shape_login_rect_input_nothing);

    }


    private void doLogin() {

        String password = mEtPassword.getText().toString();
        String secureCode = mEtSecureCode.getText().toString();
        if (TextUtils.isEmpty(password) || (mSecureCodeLayout.isShown() && TextUtils.isEmpty(secureCode))) {
            return;
        }


        if(mTvSelectDomain.isShown() && StringUtils.isEmpty(CommonShareInfo.getDomainId(AtworkApplicationLike.baseContext))) {
            toastOver(R.string.please_select_domain);
            return;
        }

        String loginUserName;
        if(getArguments() != null && !TextUtils.isEmpty(getArguments().getString(INTENT_FACE_BIO_LOGIN_USERNAME)) ){
            loginUserName = getArguments().getString(INTENT_FACE_BIO_LOGIN_USERNAME);
        } else {
            loginUserName = LoginUserInfo.getInstance().getLoginUserUserName(mActivity);
        }
        if (mProgressDialogHelper == null) {
            mProgressDialogHelper = new ProgressDialogHelper(mActivity);
        }
        mProgressDialogHelper.show(getResources().getString(R.string.login_message));


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
        loginHandleBundle.setUsername(loginUserName);
        loginHandleBundle.setPsw(password);
        loginHandleBundle.setSecureCode(secureCode);
        loginHandleBundle.setLoginControlViewBundle(loginControlViewBundle);
        loginHandleBundle.setRefreshCode(mSecureCodeLayout.isShown());


        LoginHelper.doLogin((BaseActivity) mActivity, loginHandleBundle);
    }

    public void onAccountSwitch() {
        Intent intent = LoginActivity.getLoginIntent(mActivity, true);
        getActivity().startActivity(intent);
        //界面切换效果
        mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        mActivity.finish();
    }

    private void hideKeyBoard() {
        WeakReference<Activity> weakReference = new WeakReference<>(mActivity);
        AtworkUtil.hideInput(weakReference);
    }

    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        return false;
    }

    public DisplayImageOptions getLoginAvatarImageOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheOnDisk(true);
        builder.cacheInMemory(true);
        builder.showImageOnLoading(R.mipmap.default_login_avatar);
        builder.showImageForEmptyUri(R.mipmap.default_login_avatar);
        builder.showImageOnFail(R.mipmap.default_login_avatar);
        builder.displayer(new RoundedBitmapDisplayer(180));
        return builder.build();

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
            if (TextUtils.isEmpty(ies.iesPassword)) {
                return;
            }
            mEtPassword.setText(ies.iesPassword);
            doLogin();
        }, 100);
    }

    private void handleMemberActivated() {
        new Handler().postDelayed(() -> mActivity.runOnUiThread(() -> {

            if (AtworkConfig.hasCustomForgetPwdJumpUrl()) {
//                setFakeMaxHeightWhenHavingBottomArea();


                mTvForgetPwd.setText(R.string.forget_pwd);
                mTvForgetPwd.setVisibility(View.VISIBLE);
            }


            if (null != mTvLoginWithSmsCode) {
                ViewUtil.setVisible(mTvLoginWithSmsCode, false);
            }


        }), 20);
    }

    private boolean havingBottomArea() {
        return AtworkConfig.hasCustomForgetPwdJumpUrl()
                || CustomerHelper.isMinJie(BaseApplicationLike.baseContext);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            String photoPath = data.getStringExtra("BIO_FACE_PHOTO_PATH");
            login(photoPath);
        }
    }

    private void login(String secret) {
        mProgressDialogHelper.show();

        final LoginWithFaceBioRequest loginWithFaceBioRequest = new LoginWithFaceBioRequest();
        User preLoginUser = UserManager.getInstance().queryLocalUser(LoginUserInfo.getInstance().getLoginUserId(getActivity()));
        if (preLoginUser != null) {
            loginWithFaceBioRequest.clientId = preLoginUser.mUsername;
        }

        loginWithFaceBioRequest.clientSecret = secret;

        new LoginService(mActivity).loginWithFaceBio(loginWithFaceBioRequest, new LoginNetListener() {
            @Override
            public void loginDeviceNeedAuth(LoginDeviceNeedAuthResult result) {

            }

            @Override
            public void loginSuccess(String clientId, boolean needInitPwd) {
                mProgressDialogHelper.dismiss();
                LoginHelper.handleFinishLogin(mActivity, needInitPwd, loginWithFaceBioRequest.clientId, StringUtils.EMPTY);
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                mProgressDialogHelper.dismiss();
                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.FaceBio, errorCode, errorMsg);
            }
        });
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


    @Override
    ImageView getAvatarIv() {
        return mIvAvatar;
    }
}
