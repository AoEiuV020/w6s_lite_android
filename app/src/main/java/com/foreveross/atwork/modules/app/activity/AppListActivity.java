package com.foreveross.atwork.modules.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.app.fragment.AppListFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;


public class AppListActivity extends SingleFragmentActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, AppListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment createFragment() {
        return new AppListFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

}
