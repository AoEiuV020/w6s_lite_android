package com.foreveross.atwork.modules.friend.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.friend.fragment.MyFriendsFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 16/5/19.
 */
public class MyFriendsActivity extends SingleFragmentActivity{

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MyFriendsActivity.class);
        return intent;
    }
    @Override
    protected Fragment createFragment() {
        return new MyFriendsFragment();
    }
}
