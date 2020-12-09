package com.foreveross.atwork.modules.login.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.support.BackHandledFragment;

/**
 * Created by dasunsy on 2017/10/11.
 */

public class LoginRequestPhoneSecureCodeFragment extends BackHandledFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_request_phone_secure_code, container, false);
    }

    @Override
    protected void findViews(View view) {

    }

    @Override
    protected boolean onBackPressed() {
        return false;
    }


}
