package com.foreveross.atwork.modules.setting.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.setting.fragment.EmailSyncSettingFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by reyzhang22 on 2018/4/26.
 */

public class EmailSyncSettingActivity extends SingleFragmentActivity {

    public static void startEmailSyncSettingActivity(Context context) {
        Intent intent = new Intent(context, EmailSyncSettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected Fragment createFragment() {
        return new EmailSyncSettingFragment();
    }
}
