package com.foreveross.atwork.modules.login.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;
import com.foreveross.atwork.modules.login.fragment.LoginFragment;
import com.foreveross.atwork.utils.AtworkUtil;


public class LoginActivity extends BasicLoginActivity {

    public static final String ACCOUNT_SWITCH = "ACCOUNT_SWITCH";

    private Bundle mBundle;

    public static final String INTENT_LOGIN_USER_NAME = "INTENT_LOGIN_USER_NAME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();

        AtworkUtil.checkBasePermissions(this);
    }

    @Override
    protected Fragment createFragment() {
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(mBundle);
        return fragment;
    }

    @Override
    public boolean checkDomainSettingUpdate() {
        AtworkUtil.checkUpdate(this, true);
        CommonShareInfo.setHomeKeyStatusForDomainSetting(this, false);
        return true;
    }

    public static Intent getLoginIntent(Context context, boolean switchAccount) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ACCOUNT_SWITCH, switchAccount);
        intent.putExtras(bundle);
        intent.setClass(context, LoginActivity.class);
        intent.putExtra(ACCOUNT_SWITCH, switchAccount);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    public static Intent getLoginIntent(Context context, String username) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_LOGIN_USER_NAME, username);
        intent.putExtras(bundle);
        intent.setClass(context, LoginActivity.class);
        return intent;
    }

    @Override
    public void changeStatusBar() {
        StatusBarUtil.setTransparentForImageView(this, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
        LogUtil.e("get~ per result");
    }

}
