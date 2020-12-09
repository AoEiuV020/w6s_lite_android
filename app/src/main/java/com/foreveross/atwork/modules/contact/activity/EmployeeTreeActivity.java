package com.foreveross.atwork.modules.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.modules.contact.fragment.EmployeeTreeFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by ReyZhang on 2015/3/25.
 */
public class EmployeeTreeActivity extends SingleFragmentActivity {

    public static String DATA_ORG = "data_org";

    private Organization mOrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mOrg = getIntent().getParcelableExtra(DATA_ORG);

        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment createFragment() {
        EmployeeTreeFragment fragment = new EmployeeTreeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DATA_ORG, mOrg);
        fragment.setArguments(bundle);
        return fragment;
    }


    public static Intent getEmployeeTreeIntent(Context context, Organization org) {
        Intent intent = new Intent();
        intent.setClass(context, EmployeeTreeActivity.class);
        intent.putExtra(DATA_ORG, org);
        return intent;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }


}
