package com.foreveross.atwork.modules.gesturecode.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.auth.LoginAsyncNetService;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.InterceptHelper;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.biometricAuthentication.fragment.BasicFingerPrintLockFragment;
import com.foreveross.atwork.modules.gesturecode.activity.GestureCodeInputActivity;
import com.foreveross.atwork.modules.gesturecode.activity.GestureCodeLockActivity;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.takwolf.android.lock9.Lock9View;

/**
 * Created by dasunsy on 16/1/14.
 */
public class GestureCodeLockFragment extends BasicFingerPrintLockFragment {
    private Animation mAnimation;
    private RelativeLayout mRlRoot;
    private TextView mTvTitle;
    private ImageView mIvBack;
    private Lock9View mLockView;
    private TextView mTvSwitch;
    private TextView mTvResultTip;
    private RelativeLayout mRlLoginWrapper;
    private EditText mEtLoginInput;
    private TextView mTvLoginClick;
    private TextView mTvInputTip;
    private RelativeLayout mRlTitleBar;
    private ProgressDialogHelper mProgressDialogHelper;

    private Class mRouteClass;
    private int mMode = Mode.LOCK_GESTURE;
    private int mActionFromSwitch = -1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gesture_code_lock, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();
        initData();

        changeUI(mMode);

        if (canBack()) {
            mRlTitleBar.setVisibility(View.VISIBLE);
            mTvTitle.setText(R.string.gesture_code);
            if (ActionFromSwitch.CLOSE != mActionFromSwitch) {
                mTvInputTip.setText(R.string.please_input_old_gesture_code);

            }
        } else {
            mRlTitleBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        CommonShareInfo.setKeyHomeBtnStatusForGesture(mActivity, false);
    }

    @Override
    public void onResume() {
        super.onResume();
//        setLockViewRightColor(ColorHelper.getMainColor());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //避免锁屏的广播比较慢, 造成状态值的错误
        CommonShareInfo.setKeyHomeBtnStatusForGesture(mActivity, false);
    }

    @Override
    protected void findViews(View view) {
        mRlRoot = view.findViewById(R.id.rl_root);
        mAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.text_shake);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mLockView = view.findViewById(R.id.lock_9_view);
        mTvSwitch = view.findViewById(R.id.tv_login);
        mTvResultTip = view.findViewById(R.id.tv_result_tip);
        mRlLoginWrapper = view.findViewById(R.id.rl_login);
        mEtLoginInput = view.findViewById(R.id.et_login_input);
        mTvLoginClick = view.findViewById(R.id.tv_login_click);
        mTvInputTip = view.findViewById(R.id.tv_tip_input_sth);
        mRlTitleBar = view.findViewById(R.id.title_bar_common);
        mProgressDialogHelper = new ProgressDialogHelper(mActivity);
        initLockView(mLockView);
    }





    private void registerListener() {
        mIvBack.setOnClickListener(v -> onBackPressed());

        mTvLoginClick.setOnClickListener(v -> {
            String inputContent = mEtLoginInput.getText().toString();
            LoginAsyncNetService loginAsyncNetService = new LoginAsyncNetService(mActivity);
            mProgressDialogHelper.show();
            loginAsyncNetService.auth(mActivity, inputContent, StringUtils.EMPTY, new LoginAsyncNetService.OnAuthListener() {
                @Override
                public void onAuth(boolean result) {
                    mProgressDialogHelper.dismiss();


                    if (result) {
                        AtworkUtil.hideInput(mActivity, mEtLoginInput);
                        onUnLockSuccessfully();

                    } else {
                        mTvResultTip.setText(R.string.input_login_pw_wrong);
                        shakeErrorTxt();

                    }

                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    mProgressDialogHelper.dismiss();
                    ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Login, errorCode, errorMsg);

                }
            });

        });

        mEtLoginInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = s.toString();

                if (StringUtils.isEmpty(content)) {
                    mTvLoginClick.setVisibility(View.GONE);
                } else {
                    mTvLoginClick.setVisibility(View.VISIBLE);

                    mTvResultTip.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mLockView.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {
                if (password.equals(PersonalShareInfo.getInstance().getLockCode(mActivity))) {
                    mLockView.showRightResult();
                    onUnLockSuccessfully();

                } else {

                    handleGestureCodeContinuousFail();

                }
            }

            @Override
            public void onTooShort(String password) {



                handleGestureCodeContinuousFail();

            }

            @Override
            public void onLowStrength(String password) {

                //验证页面无需关注强度低, 因为密码已经设置好了
                handleGestureCodeContinuousFail();
            }
        });


        mRlRoot.setOnClickListener(v -> AtworkUtil.hideInput(mActivity, mEtLoginInput));
    }

    private void handleGestureCodeContinuousFail() {
        mTvResultTip.setText(R.string.input_gesture_coed_wrong);
        shakeErrorTxt();
        mLockView.showWrongResult();

        PersonalShareInfo.getInstance().increaseGestureCodeLockFailContinuousCount(AtworkApplicationLike.baseContext);
    }

    protected boolean shouldDoAuth() {
        if(canBack()) {
            return false;

        } else {
            return true;
        }
    }

    private boolean isRouteMode() {
        return null != mRouteClass;
    }

    private void onUnLockSuccessfully() {
        InterceptHelper.sIsLocking = false;
        CommonShareInfo.setKeyHomeBtnStatusForGesture(mActivity, false);

        mTvResultTip.setText("");
        if (isRouteMode()) {
            Intent intent = new Intent(mActivity, mRouteClass);
            if(GestureCodeInputActivity.class == mRouteClass) {
                intent.putExtra(GestureCodeInputActivity.DATA_MODE, GestureCodeInputFragment.Mode.ADD);
            }
            startActivity(intent);
            //界面切换效果
            getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            mActivity.finish();

        } else {
            mActivity.setResult(Activity.RESULT_OK);
            mActivity.finish();
        }

    }

    private void changeUI(int mode) {
        if (Mode.LOCK_GESTURE == mode) {
            mTvInputTip.setText(R.string.please_input_gesture_code);
            mLockView.setVisibility(View.VISIBLE);
            mRlLoginWrapper.setVisibility(View.INVISIBLE);
            mTvSwitch.setText(R.string.login_with_id_and_pw);
            mTvTitle.setText(R.string.gesture_code);

        } else if (Mode.LOGIN == mode) {
            mTvTitle.setText(R.string.valid_password);
            mTvInputTip.setText(R.string.please_input_id_and_pw);
            mLockView.setVisibility(View.INVISIBLE);
            mRlLoginWrapper.setVisibility(View.VISIBLE);
            mTvSwitch.setText(R.string.login_with_gesture_lock);

            AtworkUtil.showInput(mActivity, mEtLoginInput);

        }

        mTvResultTip.setText("");

    }

    private void shakeErrorTxt() {
        mTvResultTip.startAnimation(mAnimation);
    }

    private void initData() {
        mRouteClass = (Class) getArguments().getSerializable(GestureCodeLockActivity.ACTION_ROUTE_CLASS);
        mActionFromSwitch = getArguments().getInt(GestureCodeLockActivity.ACTION_SWITCH);
    }

    private boolean canBack() {
        return (isRouteMode()) || ActionFromSwitch.CLOSE == mActionFromSwitch;
    }


    @Override
    public boolean onBackPressed() {

        if (Mode.LOGIN == mMode) {
            changeUI(Mode.LOCK_GESTURE);
            mMode = Mode.LOCK_GESTURE;
            return true;
        } else {

            if (canBack()) {
                mActivity.finish();
                return true;
            }

        }

        return false;
    }


    interface Mode {
        int LOCK_GESTURE = 0;

        int LOGIN = 1;
    }

    public interface ActionFromSwitch {
        int OPEN = 0;
        int CLOSE = 1;
        int EDIT = 2;
    }
}
