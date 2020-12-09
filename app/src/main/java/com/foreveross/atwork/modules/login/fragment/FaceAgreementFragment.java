package com.foreveross.atwork.modules.login.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener;
import com.foreveross.atwork.api.sdk.agreement.UserLoginAgreementAsyncService;
import com.foreveross.atwork.api.sdk.agreement.UserLoginAgreementSyncService;
import com.foreveross.atwork.infrastructure.model.face.FaceBizInfo;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import static android.app.Activity.RESULT_CANCELED;
import static com.foreveross.atwork.modules.login.activity.LoginSignAgreementActivity.INTENT_FACE_AGREEMENT_INFO;

public class FaceAgreementFragment extends BaseAgreementFragment {

    private Activity mActivity;

    private FaceBizInfo bizInfo;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bizInfo = getArguments().getParcelable(INTENT_FACE_AGREEMENT_INFO);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIvBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    void afterBackPressed() {
    }

    @Override
    public boolean onBackPressed() {
        mActivity.setResult(RESULT_CANCELED);
        mActivity.finish();
        return true;
    }

    @Override
    void onNextBtnClicked() {
        UserLoginAgreementAsyncService.signFaceProtocolAgreement(getActivity(), new BaseCallBackNetWorkListener() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent();
                intent.putExtra(INTENT_FACE_AGREEMENT_INFO, bizInfo);
                mActivity.setResult(Activity.RESULT_OK, intent);
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
        return getString(R.string.biz_protocol_agreement);
    }

    @Override
    String getLoadUrl() {
        return UserLoginAgreementSyncService.getFaceProtocolAgreementUrl(getActivity());
    }
}
