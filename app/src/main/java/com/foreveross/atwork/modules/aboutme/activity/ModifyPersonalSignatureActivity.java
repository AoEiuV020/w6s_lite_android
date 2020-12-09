package com.foreveross.atwork.modules.aboutme.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.aboutme.fragment.ModifyPersonalSignatureFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by wuzejie on 2019/10/14.
 * description: 修改个性签名
 */
public class ModifyPersonalSignatureActivity extends SingleFragmentActivity {

    private static final String TAG = ModifyPersonalSignatureActivity.class.getSimpleName();

    public static final String INTENT_DATA_VALUE = "intent_data_value";

    public Bundle mBundle;


    public static Intent getIntent(Context context, String value) {
        Intent intent = new Intent(context, ModifyPersonalSignatureActivity.class);
        intent.putExtra(INTENT_DATA_VALUE, value);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment createFragment() {
        ModifyPersonalSignatureFragment fragment = new ModifyPersonalSignatureFragment();
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
