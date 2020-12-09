package com.foreveross.atwork.modules.organization.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.organization.fragment.OrgApplyingFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 16/6/14.
 */
public class OrgApplyingActivity extends SingleFragmentActivity {

    public Fragment mOrgApplyingFragment;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, OrgApplyingActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mOrgApplyingFragment = new OrgApplyingFragment();
        return mOrgApplyingFragment;
    }
}
