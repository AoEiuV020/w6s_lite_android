package com.foreveross.atwork.modules.setting.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseNetWorkListener;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.api.sdk.users.responseJson.ModifyPasswordResponse;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.domain.CommonUsingSetting;
import com.foreveross.atwork.infrastructure.model.domain.PasswordStrength;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.listener.TextWatcherAdapter;
import com.foreveross.atwork.modules.login.activity.AccountLoginActivity;
import com.foreveross.atwork.modules.login.activity.LoginWithAccountActivity;
import com.foreveross.atwork.modules.login.util.LoginHelper;
import com.foreveross.atwork.modules.route.action.MainRouteAction;
import com.foreveross.atwork.modules.setting.activity.ChangePasswordActivity;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.support.BaseActivity;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ErrorHandleUtil;


public class ChangePasswordFragment extends BackHandledFragment implements View.OnClickListener {

    public static final String TAG = ChangePasswordFragment.class.getSimpleName();

    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvRightest;
    private TextView mTvInitChangePwdTip;
    private TextView mTvCommitPwd;

    private RelativeLayout mRlOldPwdView;
    private EditText mEtOldPassword;
    private EditText mEtNewPassword;
    private EditText mEtConfirmPassword;

    private TextView mTvPwdStrengthTip;

    private ProgressDialogHelper mLoadingDialog;

    private ChangePasswordActivity.Mode mMode = ChangePasswordActivity.Mode.DEFAULT;
    private String mOld, mNew, mNewRepeated;
    private boolean mIsUserLoginBefore = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();

        initData();
        refreshUI();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if(null != bundle) {
            if (bundle.containsKey(ChangePasswordActivity.DATA_MODE)) {
                mMode = (ChangePasswordActivity.Mode) bundle.getSerializable(ChangePasswordActivity.DATA_MODE);
            }

            mOld = bundle.getString(ChangePasswordActivity.DATA_OLD_PWD);
            mIsUserLoginBefore = bundle.getBoolean(ChangePasswordActivity.DATA_USER_LOGIN_BEFORE);
        }
    }

    @Override
    protected void findViews(View view) {
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mTvRightest = view.findViewById(R.id.title_bar_common_right_text);
        mTvInitChangePwdTip = view.findViewById(R.id.tv_init_change_pwd_tip);
        mTvCommitPwd = view.findViewById(R.id.tv_commit_pwd);

        mRlOldPwdView = view.findViewById(R.id.rl_setting_old_password_view);
        mEtOldPassword = view.findViewById(R.id.change_password_old_password);
        mEtNewPassword = view.findViewById(R.id.change_password_new_password);
        mEtConfirmPassword = view.findViewById(R.id.change_password_confirm_new_password);

        mTvPwdStrengthTip = view.findViewById(R.id.tv_pwd_strength_tip);
    }


    private void refreshUI() {
        mTvTitle.setText(getString(R.string.change_password));

        if (ChangePasswordActivity.Mode.DEFAULT == mMode) {
            mTvInitChangePwdTip.setVisibility(View.GONE);
            mRlOldPwdView.setVisibility(View.VISIBLE);

            mTvRightest.setVisibility(View.VISIBLE);
            mTvRightest.setText(getString(R.string.save));
            mTvRightest.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));

        } else {
            mTvInitChangePwdTip.setVisibility(View.VISIBLE);

            if (!StringUtils.isEmpty(mOld)) {
                mRlOldPwdView.setVisibility(View.GONE);
            }

            handleDomainSetting();

        }

        //根据密码强度设定文字提示
        mTvPwdStrengthTip.setText(getPwdWithStrengthTip());

    }

    private void handleDomainSetting() {
        if(CommonUsingSetting.ENABLED == DomainSettingsManager.getInstance().handleFirstLoginPasswordSetting()) {
            mTvRightest.setVisibility(View.VISIBLE);
            mTvRightest.setText(getString(R.string.over_jump));
            mTvRightest.setTextColor(ContextCompat.getColor(getActivity(), R.color.common_item_black));

            mTvCommitPwd.setVisibility(View.VISIBLE);

        } else {
            mTvRightest.setVisibility(View.VISIBLE);
            mTvRightest.setText(getString(R.string.save));
            mTvRightest.setTextColor(ContextCompat.getColor(getActivity(), R.color.common_item_black));

            mTvCommitPwd.setVisibility(View.GONE);

        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.title_bar_common_back:
                onBackPressed();
                break;

            case R.id.title_bar_common_right_text:
                if(ChangePasswordActivity.Mode.INIT_CHANGE == mMode
                        && CommonUsingSetting.ENABLED == DomainSettingsManager.getInstance().handleFirstLoginPasswordSetting()) {
                    resetPersonalModeNone();

                    new MainRouteAction().action((BaseActivity) getActivity());
                    finish(false);
                } else {
                    handleChangePassword();
                }
                break;

            case R.id.tv_commit_pwd:
                handleChangePassword();
                break;
        }
    }

    /**
     * 处理修改密码流程
     */
    private void handleChangePassword() {
        String oldPassword = mOld;
        String newPassword = mEtNewPassword.getText().toString();
        String confirmPassword = mEtConfirmPassword.getText().toString();

        if (CommonUsingSetting.DISABLED == DomainSettingsManager.getInstance().handleFirstLoginPasswordSetting()
                && TextUtils.isEmpty(oldPassword)) {
            AtworkToast.showToast(getString(R.string.please_input_old_password));
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            AtworkToast.showToast(getString(R.string.please_input_new_password));
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            AtworkToast.showToast(getString(R.string.please_input_confirm_password));
            return;
        }

        if (!confirmPassword.equals(newPassword)) {
            AtworkToast.showToast(getString(R.string.new_not_equal_confirm));
            return;
        }

        if (!checkRegex(confirmPassword)) {
            AtworkToast.showToast(getString(getPwdWithStrengthTip()));
            return;
        }

        if(confirmPassword.equals(oldPassword)) {
            AtworkToast.showToast(getString(R.string.new_pwd_and_old_pwd_are_not_the_same));
            return;
        }

        mLoadingDialog = new ProgressDialogHelper(mActivity);
        mLoadingDialog.show(R.string.changing_password);

        modifyPwd(oldPassword, newPassword);

    }

    private boolean checkRegex(String confirmPassword) {
        String[] regexArray = DomainSettingsManager.getInstance().handlePasswordRegex();
        for(String regex : regexArray) {
            if(confirmPassword.matches(regex)){
                return true;
            }
        }

        return false;
    }

    private void modifyPwd(String oldPassword, String newPassword) {
        UserAsyncNetService.getInstance().modifyPassword(getActivity(),
                LoginUserInfo.getInstance().getLoginUserId(getActivity()), oldPassword, newPassword, new BaseNetWorkListener<ModifyPasswordResponse>() {
                    @Override
                    public void networkFail(int errorCode, String errorMsg) {

                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                            mLoadingDialog = null;
                        }

                        ErrorHandleUtil.handleError(ErrorHandleUtil.Module.ChangePwd, errorCode, errorMsg);

                    }

                    @Override
                    public void onSuccess(ModifyPasswordResponse response) {
                        mLoadingDialog.dismiss();

                        if(!StringUtils.isEmpty(AtworkConfig.CUSTOM_MODIFY_API_URL)) {
                            toastOver(R.string.chang_password_success);
                            logout();
                            return;
                        }

                        if(null == response.mLoginToken) {
                            logout();

                        } else {
                            refreshTokenInfo(response, newPassword);

                            if(ChangePasswordActivity.Mode.DEFAULT == mMode) {
                                finish();
                            } else {
                                resetPersonalModeNone();
                                finish(false);
                            }

                        }


                        toastOver(R.string.chang_password_success);

                    }


                });
    }

    private void resetPersonalModeNone() {
        PersonalShareInfo.getInstance().setResetMode(mActivity, PersonalShareInfo.ResetMode.NONE);
    }

    public void refreshTokenInfo(ModifyPasswordResponse response, String newPassword) {
        response.saveToShared(BaseApplicationLike.baseContext);

        if (AtworkConfig.PERSISTENCE_PWD) {
            LoginUserInfo.getInstance().setLoginUserPw(BaseApplicationLike.baseContext, newPassword);
        }
    }

    private int getPwdWithStrengthTip() {
        if(PasswordStrength.WEAK == DomainSettingsManager.getInstance().handlePasswordStrength()) {
            return (R.string.pwd_weak_level_tip);

        } else if(PasswordStrength.STRONG == DomainSettingsManager.getInstance().handlePasswordStrength()) {
            return (R.string.pwd_strong_level_tip);

        } else {
            //default MIDDLE
            return (R.string.pwd_middle_level_tip);
        }

    }


    private void logout() {
        AtworkApplicationLike.clearData();
        BeeWorks beeWorks = BeeWorks.getInstance();
        if (beeWorks.isBeeWorksFaceBioSettingEnable() && beeWorks.config.beeWorksSetting.getFaceBioSetting().getFaceBioAuth()) {
            startActivity(AccountLoginActivity.getLoginIntent(mActivity));
        } else {
            startActivity(LoginWithAccountActivity.getClearTaskIntent(mActivity));
        }
        mActivity.finish();
    }


    private void registerListener() {
        mIvBack.setOnClickListener(this);
        mTvRightest.setOnClickListener(this);
        mTvCommitPwd.setOnClickListener(this);

        mEtOldPassword.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mOld = s.toString();
                if (StringUtils.isEmpty(s.toString())) {
                    mTvRightest.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));
                }
                isSaveBtnShow();
            }
        });

        mEtNewPassword.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNew = s.toString();
                if (StringUtils.isEmpty(s.toString())) {
                    mTvRightest.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));
                }
                isSaveBtnShow();
            }
        });
        mEtConfirmPassword.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNewRepeated = s.toString();
                if (StringUtils.isEmpty(s.toString())) {
                    mTvRightest.setTextColor(getResources().getColor(R.color.title_bar_rightest_text_gray));
                }
                isSaveBtnShow();
            }
        });

    }

    /**
     * 保存按钮的显示
     */
    private void isSaveBtnShow() {
        if (!StringUtils.isEmpty(mOld)) {
            if (!StringUtils.isEmpty(mNew)) {
                if (!StringUtils.isEmpty(mNewRepeated)) {
                    mTvRightest.setTextColor(getResources().getColor(R.color.common_item_black));
                }
            }
        }
    }

    @Override
    protected boolean onBackPressed() {
        if(ChangePasswordActivity.Mode.DEFAULT == mMode) {
            finish();


        } else {
            AtworkAlertDialog alertDialog =
                    new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE)
                    .setContent(R.string.ask_sure_to_log_out)
                    .setClickBrightColorListener(dialog -> logout());

            alertDialog.show();

        }

        return false;
    }

    @Override
    public void onDomainSettingChange() {
        refreshUI();
    }
}
