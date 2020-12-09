package com.foreveross.atwork.modules.login.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;
import com.foreveross.atwork.modules.login.fragment.AccountLoginFragment;
import com.foreveross.atwork.utils.AtworkUtil;

public class AccountLoginActivity extends BasicLoginActivity {

    private AccountLoginFragment fragment;


    public static Intent getLoginIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AccountLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        fragment = new AccountLoginFragment();
        fragment.setArguments(getIntent().getExtras());
        return fragment;
    }

    @Override
    public boolean checkDomainSettingUpdate() {
        AtworkUtil.checkUpdate(this, true);
        CommonShareInfo.setHomeKeyStatusForDomainSetting(this, false);
        return true;
    }


    @Override
    public void changeStatusBar() {
        StatusBarUtil.setTransparentForImageView(this, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
