package com.foreveross.atwork.modules.setting.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.setting.fragment.EmailSettingFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by reyzhang22 on 2018/4/26.
 */

public class EmailSettingActivity extends SingleFragmentActivity {

    public static void startEmailSettingActivity(Context context) {
        Intent intent = new Intent(context, EmailSettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected Fragment createFragment() {
        return new EmailSettingFragment();
    }
}
