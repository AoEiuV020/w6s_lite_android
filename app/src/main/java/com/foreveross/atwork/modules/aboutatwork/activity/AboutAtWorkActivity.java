package com.foreveross.atwork.modules.aboutatwork.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.aboutatwork.fragment.AboutAtWorkFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by ReyZhang on 2015/5/6.
 */
public class AboutAtWorkActivity extends SingleFragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AboutAtWorkActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new AboutAtWorkFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }


}
