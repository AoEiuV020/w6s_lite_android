package com.foreveross.atwork.modules.voip.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.voip.fragment.VoipHistoryFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 16/7/13.
 */
public class VoipHistoryActivity extends SingleFragmentActivity {

    private VoipHistoryFragment mFragment;

    public static Intent getIntent(final Context context) {
        Intent intent = new Intent();
        intent.setClass(context, VoipHistoryActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mFragment = new VoipHistoryFragment();
        return mFragment;
    }
}
