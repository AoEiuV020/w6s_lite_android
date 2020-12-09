package com.foreveross.atwork.modules.aboutatwork.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.aboutatwork.fragment.IntroFriendsFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 15/12/15.
 */
public class IntroFriendsActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new IntroFriendsFragment();
    }

    public static Intent getIntent(Context ctx) {
        Intent intent = new Intent(ctx, IntroFriendsActivity.class);
        return intent;
    }
}
