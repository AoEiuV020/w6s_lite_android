package com.foreveross.atwork.modules.setting.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.setting.fragment.TextSizeSettingFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by reyzhang22 on 2017/12/5.
 */

public class TextSizeSettingActivity extends SingleFragmentActivity {

    public static final Intent getIntent(Context context) {
        Intent intent = new Intent(context, TextSizeSettingActivity.class);
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        return new TextSizeSettingFragment();
    }

}
