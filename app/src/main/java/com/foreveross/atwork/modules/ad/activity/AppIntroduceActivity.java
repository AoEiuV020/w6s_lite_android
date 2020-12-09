package com.foreveross.atwork.modules.ad.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.modules.ad.fragment.AppIntroduceFragment;
import com.foreveross.atwork.support.NoFilterSingleFragmentActivity;

/**
 * 应用介绍页面
 */
public class AppIntroduceActivity extends NoFilterSingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new AppIntroduceFragment();
    }

    public static Intent getIntent(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, AppIntroduceActivity.class);
        return intent;
    }


    @Override
    public void changeStatusBar() {
        //do nothing
    }

    @Override
    public boolean checkDomainSettingUpdate() {
        //do nothing
        return false;
    }

    @Override
    public void registerUpdateReceiver() {
        //do nothing
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
        LogUtil.e("get~ per result");
    }
}
