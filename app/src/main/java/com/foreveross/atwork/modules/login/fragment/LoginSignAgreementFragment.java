package com.foreveross.atwork.modules.login.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener;
import com.foreveross.atwork.api.sdk.agreement.UserLoginAgreementAsyncService;
import com.foreveross.atwork.api.sdk.agreement.UserLoginAgreementSyncService;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.modules.login.activity.AccountLoginActivity;
import com.foreveross.atwork.modules.login.activity.LoginWithAccountActivity;
import com.foreveross.atwork.utils.ErrorHandleUtil;

/**
 * Created by dasunsy on 2017/7/20.
 */

public class LoginSignAgreementFragment extends BaseAgreementFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_sign_agreement, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PersonalShareInfo.getInstance().setLoginSignedAgreementForced(getActivity(), true);
    }


    @Override
    void afterBackPressed() {
        PersonalShareInfo.getInstance().setLoginSignedAgreementForced(getActivity(), false);

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
    void onNextBtnClicked() {
        UserLoginAgreementAsyncService.signUserLoginAgreement(getActivity(), new BaseCallBackNetWorkListener() {
            @Override
            public void onSuccess() {
                PersonalShareInfo.getInstance().setLoginSignedAgreementConfirmed(AtworkApplicationLike.baseContext, true);
                PersonalShareInfo.getInstance().setLoginSignedAgreementForced(AtworkApplicationLike.baseContext, false);

                mProgressDialogHelper.dismiss();
                finish();

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(errorCode, errorMsg);

                mProgressDialogHelper.dismiss();
            }
        });
    }

    @Override
    String getTitle() {
        return getString(R.string.user_agreement);
    }

    @Override
    String getLoadUrl() {
        return UserLoginAgreementSyncService.getUserLoginAgreementUrl(getActivity());
    }
}
