package com.foreveross.atwork.modules.aboutme.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.modules.aboutme.fragment.PersonalQrcodeFragment;
import com.foreveross.atwork.modules.aboutme.fragment.PersonalQrcodeFragmentV2;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by shadow on 2016/5/17.
 */
public class PersonalQrcodeActivity extends SingleFragmentActivity {

    public static final String INTENT_DATA = "intent_data";

    public Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);

    }

    public static Intent getIntent(Context context, User user) {
        Intent intent = new Intent(context,PersonalQrcodeActivity.class);
        intent.putExtra(INTENT_DATA,user);
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        if (CustomerHelper.isNewland(AtworkApplicationLike.baseContext)) {
            PersonalQrcodeFragment fragment = new PersonalQrcodeFragment();
            fragment.setArguments(mBundle);
            return fragment;
        }


        PersonalQrcodeFragmentV2 fragment = new PersonalQrcodeFragmentV2();
        fragment.setArguments(mBundle);
        return fragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //界面回退动画
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

}
