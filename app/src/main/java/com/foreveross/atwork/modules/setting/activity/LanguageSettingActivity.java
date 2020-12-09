package com.foreveross.atwork.modules.setting.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.setting.fragment.LanguageSettingFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 2017/4/20.
 */

public class LanguageSettingActivity extends SingleFragmentActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, LanguageSettingActivity.class);
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        return new LanguageSettingFragment();
    }
}
