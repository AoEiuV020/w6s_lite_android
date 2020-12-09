package com.foreveross.atwork.modules.setting.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.setting.fragment.SettingFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * 设置页面
 * Created by ReyZhang on 2015/5/6.
 */
@Deprecated
public class SettingActivity extends SingleFragmentActivity{

    private static final String TAG = SettingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, SettingActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new SettingFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //界面回退动画
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

}
