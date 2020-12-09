package com.foreveross.atwork.modules.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.modules.contact.fragment.PersonalInfoFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by lingen on 15/4/13.
 * Description:
 */
public class PersonalInfoActivity extends SingleFragmentActivity {

    public static final String DATA_USER = "DATA_USER";


    public static final String USER_REGISTERED = "USER_REGISTERED";

    private User mUser;//当前员工对象

    private boolean mUserRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        mUser = intent.getParcelableExtra(DATA_USER);

        mUserRegistered = intent.getBooleanExtra(USER_REGISTERED, true);


        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment createFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(DATA_USER, mUser);
        bundle.putBoolean(USER_REGISTERED, mUserRegistered);
        PersonalInfoFragment personalInfoFragment = new PersonalInfoFragment();
        personalInfoFragment.setArguments(bundle);
        return personalInfoFragment;
    }

    public static Intent getIntent(Context context, User user) {
        Intent intent = new Intent();
        intent.setClass(context, PersonalInfoActivity.class);
        intent.putExtra(DATA_USER, user);
        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //界面回退动画
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

}
