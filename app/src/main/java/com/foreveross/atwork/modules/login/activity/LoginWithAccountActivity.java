package com.foreveross.atwork.modules.login.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;
import com.foreveross.atwork.modules.login.fragment.LoginWithAccountFragment;
import com.foreveross.atwork.utils.AtworkUtil;

/**
 * 已有用户登录页面
 * Created by ReyZhang on 2015/5/21.
 */
public class LoginWithAccountActivity extends BasicLoginActivity {

    private LoginWithAccountFragment loginWithAccountFragment;

    public static final String INTENT_FACE_BIO_LOGIN_AUTH = "INTENT_FACE_BIO_LOGIN_AUTH";

    public static final String INTENT_FACE_BIO_LOGIN_USERNAME = "INTENT_FACE_BIO_LOGIN_USERNAME";

    public Bundle mBundle;

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

    public static Intent getIntent(Context context, boolean faceBioLoginAuth) {
        return getIntent(context, null, faceBioLoginAuth);
    }

    public static Intent getIntent(Context context, String loginUsername, boolean faceBioLoginAuth) {
        Intent intent = new Intent(context, LoginWithAccountActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putBoolean(INTENT_FACE_BIO_LOGIN_AUTH, faceBioLoginAuth);
        if (!TextUtils.isEmpty(loginUsername)) {
            bundle.putString(INTENT_FACE_BIO_LOGIN_USERNAME, loginUsername);
        }
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent getClearTaskIntent(Context context){
        Intent intent = new Intent(context, LoginWithAccountActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        loginWithAccountFragment = new LoginWithAccountFragment();
        loginWithAccountFragment.setArguments(mBundle);
        return loginWithAccountFragment;
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
        loginWithAccountFragment.onActivityResult(requestCode, resultCode, data);
    }


}
