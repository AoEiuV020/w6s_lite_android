package com.foreveross.atwork.modules.setting.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.setting.fragment.ChangePasswordFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

import java.io.Serializable;

/**
 * Created by ReyZhang on 2015/5/7.
 */
public class ChangePasswordActivity extends SingleFragmentActivity {

    private static final String TAG = ChangePasswordActivity.class.getSimpleName();

    public static final String DATA_MODE = "DATA_MODE";
    public static final String DATA_OLD_PWD = "DATA_OLD_PWD";
    public static final String DATA_USER_LOGIN_BEFORE = "DATA_USER_LOGIN_BEFORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        return intent;
    }

    public static Intent getIntent(Context context, Mode mode, String oldPwd, boolean isUserLoginBefore) {
        Intent intent = getIntent(context);
        intent.putExtra(DATA_MODE, mode);
        intent.putExtra(DATA_OLD_PWD, oldPwd);
        intent.putExtra(DATA_USER_LOGIN_BEFORE, isUserLoginBefore);
//        intent.putExtra(HandleLoginService.DATA_FROM_LOGIN, true);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new ChangePasswordFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    public boolean shouldInterceptToAgreement() {
        return isInLoginFlow();
    }

    public enum Mode implements Serializable {

        INIT_CHANGE,

        DEFAULT
    }

}
