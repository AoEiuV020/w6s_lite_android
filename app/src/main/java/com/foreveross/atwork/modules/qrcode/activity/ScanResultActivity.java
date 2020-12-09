package com.foreveross.atwork.modules.qrcode.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.qrcode.fragment.ScanResultFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 15/12/10.
 */
public class ScanResultActivity extends SingleFragmentActivity {
    public static final String DATA_RESULT_CONTENT = "data_result_content";

    private String mContentData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(null != getIntent()) {
            mContentData = getIntent().getStringExtra(DATA_RESULT_CONTENT);
        }

        super.onCreate(savedInstanceState);
    }


    @Override
    protected Fragment createFragment() {
        ScanResultFragment scanResultFragment = new ScanResultFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DATA_RESULT_CONTENT, mContentData);
        scanResultFragment.setArguments(bundle);
        return scanResultFragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }


    public static Intent getIntent(Activity activity, String content) {
        Intent intent = new Intent(activity, ScanResultActivity.class);
        intent.putExtra(DATA_RESULT_CONTENT, content);
        return intent;
    }
}
