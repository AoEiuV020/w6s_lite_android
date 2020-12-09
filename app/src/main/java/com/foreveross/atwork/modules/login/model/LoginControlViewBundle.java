package com.foreveross.atwork.modules.login.model;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreveross.atwork.component.ProgressDialogHelper;

/**
 * Created by dasunsy on 2018/1/23.
 */

public class LoginControlViewBundle {

    public ProgressDialogHelper mDialogHelper;

    public View mSecureCodeLayout;

    public ImageView mSecureCodeView;

    public ImageView mIvFakeHeader;

    public TextView mTvForgetPwd;

    public EditText mEtPassword;

    private LoginControlViewBundle(Builder builder) {
        mDialogHelper = builder.mDialogHelper;
        mSecureCodeLayout = builder.mSecureCodeLayout;
        mSecureCodeView = builder.mSecureCodeView;
        mIvFakeHeader = builder.mIvFakeHeader;
        mTvForgetPwd = builder.mTvForgetPwd;
        mEtPassword = builder.mEtPassword;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private ProgressDialogHelper mDialogHelper;
        private View mSecureCodeLayout;
        private ImageView mSecureCodeView;
        private ImageView mIvFakeHeader;
        private TextView mTvForgetPwd;
        private EditText mEtPassword;

        private Builder() {
        }

        public Builder setDialogHelper(ProgressDialogHelper dialogHelper) {
            this.mDialogHelper = dialogHelper;
            return this;
        }

        public Builder setEtPassword(EditText passwordView) {
            this.mEtPassword = passwordView;
            return this;
        }

        public Builder setSecureCodeLayout(View secureCodeLayout) {
            this.mSecureCodeLayout = secureCodeLayout;
            return this;
        }

        public Builder setSecureCodeView(ImageView secureCodeView) {
            this.mSecureCodeView = secureCodeView;
            return this;
        }

        public Builder setIvFakeHeader(ImageView ivFakeHeader) {
            this.mIvFakeHeader = ivFakeHeader;
            return this;
        }

        public Builder setTvForgetPwd(TextView tvForgetPwd) {
            this.mTvForgetPwd = tvForgetPwd;
            return this;
        }

        public LoginControlViewBundle build() {
            return new LoginControlViewBundle(this);
        }
    }
}
