package com.foreveross.atwork.modules.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.fragment.app.Fragment;

import com.foreverht.workplus.ui.component.statusbar.WorkplusStatusBarHelper;
import com.foreveross.atwork.modules.app.fragment.AppFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by reyzhang22 on 17/9/14.
 */

public class AppActivity extends SingleFragmentActivity {

    public static final String KEY_APP_ORG_ID = "KEY_APP_ORG_ID";
    private String mOrgId;

    public static Intent getIntent(Context context, String orgId) {
        Intent intent = new Intent(context, AppActivity.class);
        intent.putExtra(KEY_APP_ORG_ID, orgId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            mOrgId = intent.getStringExtra(KEY_APP_ORG_ID);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        AppFragment appFragment = new AppFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppFragment.KEY_CHECK_ORG_ID, mOrgId);
        appFragment.setArguments(bundle);
        return appFragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void changeStatusBar() {
        WorkplusStatusBarHelper.setCommonFullScreenStatusBar(this, true);
    }
}
