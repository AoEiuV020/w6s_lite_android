package com.foreveross.atwork.modules.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.modules.contact.fragment.SyncContactFailedFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

import java.util.ArrayList;

/**
 * Created by dasunsy on 2015/6/29 0029.
 */
public class SyncContactFailedActivity extends SingleFragmentActivity {
    public static String BUNDLE_USER_LIST = "intent_user_list";

    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment createFragment() {
        Fragment fragment = new SyncContactFailedFragment();
        fragment.setArguments(mBundle);
        return fragment;
    }

    public static Intent getIntent(Context context, ArrayList<ShowListItem> contactList) {
        Intent intent = new Intent();
        intent.setClass(context, SyncContactFailedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_USER_LIST, contactList);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

}
