package com.foreveross.atwork.modules.setting.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.setting.fragment.StorageSpaceSettingFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by wuzejie on 2019/9/17.
 */

public class StorageSpaceSettingActivity extends SingleFragmentActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, StorageSpaceSettingActivity.class);
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        return new StorageSpaceSettingFragment();
    }
}
