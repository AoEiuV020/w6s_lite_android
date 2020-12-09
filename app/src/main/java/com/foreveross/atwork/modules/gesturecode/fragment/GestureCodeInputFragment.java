package com.foreveross.atwork.modules.gesturecode.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.domain.CommonUsingSetting;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.gesturecode.activity.GestureCodeInputActivity;
import com.foreveross.atwork.modules.login.activity.AccountLoginActivity;
import com.foreveross.atwork.modules.login.activity.LoginWithAccountActivity;
import com.foreveross.atwork.modules.login.util.LoginHelper;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.support.BaseActivity;
import com.foreveross.atwork.utils.AtworkToast;
import com.takwolf.android.lock9.Lock9View;

/**
 * Created by dasunsy on 16/1/13.
 */
public class GestureCodeInputFragment extends BackHandledFragment {
    private Animation mAnimation;
    private TextView mTvTitle;
    private ImageView mIvBack;
    private TextView mTvRightest;
    private Lock9View mLockView;
    private TextView mTvTip;
    private TextView mTvResult;
    private TextView mTvWatchDemo;

    private boolean mAddModeHasInit = false;
    private String mLastLockCode = StringUtils.EMPTY;
    private int mMode = Mode.ADD;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gesture_code_input, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        registerListener();

        refreshUI();
    }

    private void refreshUI() {
        mTvTitle.setText(R.string.gesture_code);

        if(Mode.INIT_ADD == mMode && CommonUsingSetting.ENABLED == DomainSettingsManager.getInstance().handleFirstLoginGestureLockSetting()) {
            mTvRightest.setVisibility(View.VISIBLE);
            mTvRightest.setText(getString(R.string.over_jump));
            mTvRightest.setTextColor(ContextCompat.getColor(getActivity(), R.color.common_item_black));

        } else {
            mTvRightest.setVisibility(View.GONE);
        }
    }

    @Override
    protected void findViews(View view) {
        mAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.text_shake);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mTvRightest = view.findViewById(R.id.title_bar_common_right_text);
        mLockView = view.findViewById(R.id.lock_9_view);
        mTvTip = view.findViewById(R.id.tv_tip_input_sth);
        mTvResult = view.findViewById(R.id.tv_result_tip);
        mTvWatchDemo = view.findViewById(R.id.tv_watch_demo);

        String tipPrefix = getStrings(R.string.low_strength_demo_prefix);
        String tipSuffix = getStrings(R.string.low_strength_demo_suffix);
        String tip = tipPrefix + tipSuffix;
        SpannableString ss = new SpannableString(tip);
        int color = AtworkApplicationLike.baseContext.getResources().getColor(R.color.link);
        ss.setSpan(new ForegroundColorSpan(color), tip.lastIndexOf(tipSuffix), tip.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(), tip.lastIndexOf(tipSuffix), tip.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTvWatchDemo.setText(ss);
        initLockView(mLockView);

        mLockView.setGestureLockLowStrength(AtworkConfig.BIOMETRICAUTHENTICATION_CONFIG.getGestureLockLowStrength());
        mTvWatchDemo.setVisibility(View.GONE);
    }


    private void registerListener() {
        mIvBack.setOnClickListener(v -> onBackPressed());

        mTvRightest.setOnClickListener(v -> {
            if(CommonUsingSetting.ENABLED == DomainSettingsManager.getInstance().handleFirstLoginGestureLockSetting()) {
                resetPersonalModeNone();
                LoginHelper.goMainActivity((BaseActivity) getActivity());
                finish(false);
            }
        });

        mLockView.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {
                if (Mode.ADD == mMode || Mode.INIT_ADD == mMode) {
                    if (!mAddModeHasInit) {
                        mAddModeHasInit = true;
                        mLastLockCode = password;

                        mLockView.clear();
                        mTvResult.setText(R.string.please_input_gesture_code_again);

                    } else {
                        if (mLastLockCode.equals(password)) {
                            PersonalShareInfo.getInstance().setLockCode(mActivity, password);
                            LoginUserInfo.getInstance().mIsInitOpenCodeLock = true;  //do the refresh
                            LoginUserInfo.getInstance().mLastCodeLockTime = -1;

                            mTvResult.setText("");
                            AtworkToast.showResToast(R.string.input_gesture_code_success);
                            if (Mode.ADD == mMode ) {
                                mActivity.setResult(Activity.RESULT_OK);
                                mActivity.finish();

                            } else if(Mode.INIT_ADD == mMode) {
                                resetPersonalModeNone();
                                PersonalShareInfo.getInstance().setLockCodeSetting(mActivity, true);
                                LoginUserInfo.getInstance().mIsInitOpenCodeLock = true;

                                LoginHelper.goMainActivity((BaseActivity) mActivity);
                                mActivity.finish();


                            }

                        } else {

                            clearLastLockCode();

                            mTvResult.setText(R.string.please_input_gesture_code_right);
                            shakeErrorTxt();
                            mLockView.showWrongResult();
                        }

                    }
                }
            }

            @Override
            public void onTooShort(String password) {
                mTvResult.setText(R.string.at_least_4_points_wrong);
                shakeErrorTxt();
                mLockView.showWrongResult();

            }

            @Override
            public void onLowStrength(String password) {
                mTvResult.setText(R.string.gesture_lock_pwd_low_strength);
                shakeErrorTxt();
                mLockView.showWrongResult();
            }
        });
    }

    private void clearLastLockCode() {
        mLastLockCode = StringUtils.EMPTY;
        mAddModeHasInit = false;
    }

    private void resetPersonalModeNone() {
        PersonalShareInfo.getInstance().setResetMode(mActivity, PersonalShareInfo.ResetMode.NONE);
    }

    private void initData() {
        mMode = getArguments().getInt(GestureCodeInputActivity.DATA_MODE);
    }

    private void shakeErrorTxt() {
        mTvResult.startAnimation(mAnimation);
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

    @Override
    protected boolean onBackPressed() {
        if(Mode.ADD == mMode) {
            mActivity.finish();

        } else if(Mode.INIT_ADD == mMode) {
            AtworkAlertDialog alertDialog =
                    new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE)
                            .setContent(R.string.ask_sure_to_log_out)
                            .setClickBrightColorListener(dialog -> logout());

            alertDialog.show();
        }

        return false;
    }


    public interface Mode {

        /**
         * 新增手势密码, 登录后在设置界面内点进
         * */
        int ADD = 0;

        /**
         * 修改手势密码
         * */
        int EDIT = 1;

        /**
         * 新增手势密码, 登录后根据域设置, 且之前是否登录过来判断是否进行此操作
         * */
        int INIT_ADD = 2;
    }
}
