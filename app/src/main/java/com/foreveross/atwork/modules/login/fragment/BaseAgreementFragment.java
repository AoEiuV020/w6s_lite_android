package com.foreveross.atwork.modules.login.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.R;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ViewCompat;
import com.foreveross.atwork.modules.login.activity.LoginSignAgreementActivity;
import com.foreveross.atwork.support.BackHandledFragment;

public abstract class BaseAgreementFragment extends BackHandledFragment {

    abstract void afterBackPressed();

    abstract void onNextBtnClicked();

    abstract String getTitle();

    abstract String getLoadUrl();

    protected View mVLayoutTitle;
    protected ImageView mIvBack;
    protected TextView mTvTitle;
    protected TextView mTvRightest;
    protected TextView mTvAgreementTitle;
    protected WebView mWbAgreementContent;
    protected RelativeLayout mRlSignAgreement;
    protected TextView mTvAgreementTip;
    protected CheckBox mCbSelect;

    protected ProgressDialogHelper mProgressDialogHelper;
    protected boolean mIsSignAgreement = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_sign_agreement, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        registerListener();
        LoginSignAgreementActivity.clearShadow();
        loadRemoteAgreement();
    }



    @Override
    protected void findViews(View view) {
        mVLayoutTitle = view.findViewById(R.id.layout_title_bar);
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mTvRightest = view.findViewById(R.id.title_bar_common_right_text);
        mTvAgreementTitle = view.findViewById(R.id.tv_agreement_title);
        mWbAgreementContent = view.findViewById(R.id.wv_agreement_content);
        mCbSelect = view.findViewById(R.id.cb_select);
        mRlSignAgreement= view.findViewById(R.id.rl_sign_agreement);
        mTvAgreementTip = view.findViewById(R.id.tv_agreement_tip);
    }

    private void initViews() {
        mTvTitle.setText(getTitle());
        mTvRightest.setText(R.string.next_step);
        mTvRightest.setVisibility(View.GONE);
        ViewCompat.setElevation(mRlSignAgreement, DensityUtil.dip2px(16));
//        refreshNextStepBtn();
        initWebview();
    }


    private void initWebview() {
        mWbAgreementContent.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                mTvTitle.setText(view.getTitle());

                mWbAgreementContent.setVisibility(View.VISIBLE);
                mRlSignAgreement.setVisibility(View.VISIBLE);

                mProgressDialogHelper.dismiss();
            }
        });
    }

    private void loadRemoteAgreement() {
        if (null == mProgressDialogHelper) {
            mProgressDialogHelper = new ProgressDialogHelper(getActivity());
        }

        mProgressDialogHelper.show();

        mWbAgreementContent.loadUrl(getLoadUrl());
    }

    private void registerListener() {

        mIvBack.setOnClickListener(v -> onBackPressed());


        mRlSignAgreement.setOnClickListener(v -> {
            mProgressDialogHelper.show();
            onNextBtnClicked();
        });
    }


    @Override
    public boolean onBackPressed() {
        AtworkAlertDialog alertDialog =
                new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE)
                        .setContent(R.string.ask_sure_to_log_out)
                        .setClickBrightColorListener(dialog -> afterBackPressed());

        alertDialog.show();
        return false;
    }


}
