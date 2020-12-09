package com.foreveross.atwork.modules.login.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.auth.model.LoginDeviceNeedAuthResult;
import com.foreveross.atwork.api.sdk.auth.model.LoginWithFaceBioRequest;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.layout.KeyboardRelativeLayout;
import com.foreveross.atwork.listener.TextWatcherAdapter;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.login.activity.LoginActivity;
import com.foreveross.atwork.modules.login.listener.LoginNetListener;
import com.foreveross.atwork.modules.login.service.LoginService;
import com.foreveross.atwork.modules.login.util.LoginHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import static android.view.View.VISIBLE;

public class AccountLoginFragment extends BaseLoginFragment {

    private ImageView mAvatar;
    private TextView mUsername;
    private View mAccountNameView;
    private EditText mAccountName;
    private ImageView mClearBtn;
    private TextView mNextBtn;
    private ImageView mSetting;
    private TextView mRegisterNewAccount;
    private TextView mUserSmsLogin;
    private ProgressDialogHelper mDialog;
    private ScrollView mLoginScrollView;
    private TextView mSwitchAccount;


    private boolean mKeyboardShow = false;
    private boolean mLastInputHadFocus = false;
    private int mContinueScrollViewToSeeBtnCount = 1;

    private KeyboardRelativeLayout mLoginResizeRelativeLayout;

    public static final int REQUEST_CAMERA_CODE = 0X11;

    private User mPreLoginUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initObserver();
        registerListener();
    }

    @Override
    ImageView getAvatarIv() {
        return mAvatar;
    }



    @Override
    protected void findViews(View view) {
        mAvatar = view.findViewById(R.id.iv_login_user_avatar);
        mAccountNameView = view.findViewById(R.id.login_username_view);
        mUsername = view.findViewById(R.id.tv_user_name);
        mAccountName = view.findViewById(R.id.et_login_username);
        mClearBtn = view.findViewById(R.id.iv_login_username_cancel_btn);
        mNextBtn = view.findViewById(R.id.bt_next);
        mSetting = view.findViewById(R.id.iv_sync_messages_setting);
        mRegisterNewAccount = view.findViewById(R.id.tv_login_register);
        mUserSmsLogin = view.findViewById(R.id.tv_login_with_sms_code);
        mDialog = new ProgressDialogHelper(mActivity);
        mLoginResizeRelativeLayout = view.findViewById(R.id.login_layout);
        mLoginScrollView = view.findViewById(R.id.login_scroll);
        mSwitchAccount = view.findViewById(R.id.tv_switch_account);
    }

    private void initData() {

        if(StringUtils.isEmpty(LoginUserInfo.getInstance().getLoginUserId(getActivity()))) {
            handleMemberActivated(DomainSettingsManager.getInstance().handleUserActivated());
            return;
        }

        mPreLoginUser = UserManager.getInstance().queryLocalUser(LoginUserInfo.getInstance().getLoginUserId(getActivity()));
        if (mPreLoginUser == null) {
            handleMemberActivated(DomainSettingsManager.getInstance().handleUserActivated());
            return;
        }

        setAvatar(mPreLoginUser);

        mUsername.setText(mPreLoginUser.getShowName());
        mAccountNameView.setVisibility(View.GONE);
        onChangeLoginBtnStatus();
        mSwitchAccount.setVisibility(VISIBLE);
    }

    private void setAvatar(User preLoginUser) {
        setAvatarSize(AtworkConfig.LOGIN_VIEW_CONFIG.getAvatarSize());

        ImageCacheHelper.displayImageByMediaId(preLoginUser.mAvatar, mAvatar, getLoginAvatarImageOptions(), new ImageCacheHelper.ImageLoadedListener() {
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
        ViewGroup.LayoutParams layoutParams = mAvatar.getLayoutParams();
        layoutParams.height = DensityUtil.dip2px( size);
        layoutParams.width = DensityUtil.dip2px( size);

        mAvatar.setLayoutParams(layoutParams);
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


    private void registerListener() {
        mClearBtn.setOnClickListener(view -> {
            mAccountName.setText("");
        });

        mNextBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        mSetting.setOnClickListener(view -> {
            openSyncMessageSetting();
        });

        mRegisterNewAccount.setOnClickListener(view -> {
            String url = String.format(UrlConstantManager.getInstance().getRegisterUrl(), AtworkConfig.H3C_CONFIG ? "h3c" : 0);
            WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                    .setUrl(url)
                    .setTitle(getString(R.string.new_register))
                    .setNeedShare(false);

            startActivity(WebViewActivity.getIntent(mActivity, webViewControlAction));
        });

        mUserSmsLogin.setOnClickListener(view -> {
            if(BaseApplicationLike.sIsDebug) {

                String url = "file:///android_asset/www/index_2.1.html?theme=red_envelope&watermark_enable=1";
                WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url);
                startActivity(WebViewActivity.getIntent(mActivity, webViewControlAction));

                return;
            }


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

        mNextBtn.setOnClickListener(view -> {
            startActivity(LoginActivity.getLoginIntent(mActivity, mAccountName.getText().toString()));
        });

        mAccountName.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable text) {
                onChangeLoginBtnStatus();
                onCancelViewFocus(mClearBtn, text);
            }
        });

        mAccountName.setOnClickListener(v -> {
            handleScrollWhenFocusInput();
            mLastInputHadFocus = true;
        });

        mAccountName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                handleScrollWhenFocusInput();
                mLastInputHadFocus = true;
            }
        });

        mSwitchAccount.setOnClickListener(v -> {
            handleMemberActivated(DomainSettingsManager.getInstance().handleUserActivated());
            mPreLoginUser = null;
            mUsername.setText("");
            mAccountNameView.setVisibility(VISIBLE);
            onChangeLoginBtnStatus();
            mSwitchAccount.setVisibility(View.GONE);
            setAvatarSize(100);
            mAvatar.setImageResource(R.mipmap.default_login_avatar);
        });
    }

    private void login(String faceData, String faceToken) {
        mDialog.show();

        final LoginWithFaceBioRequest loginWithFaceBioRequest = new LoginWithFaceBioRequest();
        String username = mAccountName.getText().toString();
        if (mPreLoginUser != null) {
            username = mPreLoginUser.mUsername;
        }
        loginWithFaceBioRequest.clientId = username;
        loginWithFaceBioRequest.clientSecret = faceData;
        loginWithFaceBioRequest.getAdditionalFaceId().setBizToken(faceToken);

        new LoginService(mActivity).loginWithFaceBio(loginWithFaceBioRequest, new LoginNetListener() {
            @Override
            public void loginDeviceNeedAuth(LoginDeviceNeedAuthResult result) {

            }

            @Override
            public void loginSuccess(String clientId, boolean needInitPwd) {
                mDialog.dismiss();
                LoginHelper.handleFinishLogin(mActivity, needInitPwd, loginWithFaceBioRequest.clientId, StringUtils.EMPTY);
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                mDialog.dismiss();
                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.FaceBio, errorCode, errorMsg);
            }
        });
    }


    private void onCancelViewFocus(ImageView cancelView, Editable text) {
        if (null != text && 0 < text.length()) {
            cancelView.setVisibility(VISIBLE);
        } else {
            cancelView.setVisibility(View.INVISIBLE);

        }
    }

    private void onChangeLoginBtnStatus() {

        if (0 >= mAccountName.getText().length() && mPreLoginUser == null) {
            mNextBtn.setBackgroundResource(R.drawable.shape_login_rect_input_nothing);
            return;
        }
        mNextBtn.setBackgroundResource(R.drawable.shape_login_rect_input_something);
    }

    private void handleScrollWhenFocusInput() {
        if(0 < mContinueScrollViewToSeeBtnCount && mKeyboardShow && !mLastInputHadFocus) {
            mContinueScrollViewToSeeBtnCount--;
            scrollRootView();
        }
    }

    private void initObserver() {
        mLoginResizeRelativeLayout.setOnKeyboardChangeListener(state -> {
            if (state == KeyboardRelativeLayout.KEYBOARD_STATE_SHOW) {

                LogUtil.e("KeyboardRelativeLayout.KEYBOARD_STATE_SHOW");

                if (!mKeyboardShow) {
                    mKeyboardShow = true;
                    scrollRootView();

                }



            } else if (state == KeyboardRelativeLayout.KEYBOARD_STATE_HIDE) {
                if (mKeyboardShow) {
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
    private int getScrollBy() {

        int scrollBy = DensityUtil.dip2px(39) + mNextBtn.getHeight();
        return scrollBy;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            String photoPath = data.getStringExtra("BIO_FACE_PHOTO_PATH");
            login(photoPath, "");
        }

    }

    @Override
    public void onDomainSettingChange() {

    }

    private void handleMemberActivated(boolean backEndActivate) {
        new Handler().postDelayed(() -> mActivity.runOnUiThread(() -> {
            ViewUtil.setVisible(mRegisterNewAccount, !backEndActivate);


            if (AtworkConfig.hasCustomVerificationCodeUrl()) {
                ViewUtil.setVisible(mUserSmsLogin, true);


            } else {
                ViewUtil.setVisible(mUserSmsLogin, !backEndActivate);

            }

        }), 20);
    }

    @Override
    protected boolean onBackPressed() {
        mActivity.moveTaskToBack(true);
        return true;
    }
}
