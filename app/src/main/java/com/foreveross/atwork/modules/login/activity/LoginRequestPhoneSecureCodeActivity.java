package com.foreveross.atwork.modules.login.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.login.fragment.LoginRequestPhoneSecureCodeFragment;
import com.foreveross.atwork.support.NoFilterSingleFragmentActivity;

/**
 * Created by dasunsy on 2017/10/11.
 */

public class LoginRequestPhoneSecureCodeActivity extends NoFilterSingleFragmentActivity {

    public static final String DATA_MODE = "DATA_MODE";

    public static Intent getIntent(Context context, int mode) {
        Intent intent = new Intent(context, LoginRequestPhoneSecureCodeActivity.class);
        intent.putExtra(DATA_MODE, mode);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new LoginRequestPhoneSecureCodeFragment();
    }

    public class Mode {

        public static final int REGISTER = 0;

        public static final int FORGOT_PWD = 1;
    }
}
