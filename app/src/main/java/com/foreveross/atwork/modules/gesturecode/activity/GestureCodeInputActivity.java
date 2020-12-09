package com.foreveross.atwork.modules.gesturecode.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.gesturecode.fragment.GestureCodeInputFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 16/1/13.
 */
public class GestureCodeInputActivity extends SingleFragmentActivity {
    public static final String DATA_MODE = "DATA_MODE";

    public static Intent getIntent(Context context, int mode ) {
        Intent intent = new Intent(context, GestureCodeInputActivity.class);
        intent.putExtra(DATA_MODE, mode);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment createFragment() {
        GestureCodeInputFragment fragment = new GestureCodeInputFragment();
        return fragment;
    }

    @Override
    public boolean shouldInterceptToAgreement() {
        return isInLoginFlow();
    }


    @Override
    protected boolean shouldCheckBioAuthSetting() {
        return false;
    }
}
