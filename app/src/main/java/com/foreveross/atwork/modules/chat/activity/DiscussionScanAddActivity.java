package com.foreveross.atwork.modules.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.api.sdk.qrcode.responseModel.WorkplusQrCodeInfo;
import com.foreveross.atwork.modules.chat.fragment.DiscussionScanAddFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 16/2/3.
 */
public class DiscussionScanAddActivity extends SingleFragmentActivity{
    public static final String DATA_DISCUSSION_SCAN_INFO = "data_group_scan_info";
    private DiscussionScanAddFragment mGroupScanFragment;

    private WorkplusQrCodeInfo mWorkplusQrCodeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mWorkplusQrCodeInfo = getIntent().getParcelableExtra(DATA_DISCUSSION_SCAN_INFO);

        super.onCreate(savedInstanceState);
    }

    public static Intent getIntent(Context context, WorkplusQrCodeInfo workplusQrCodeInfo) {
        Intent intent = new Intent();
        intent.setClass(context, DiscussionScanAddActivity.class);
        intent.putExtra(DATA_DISCUSSION_SCAN_INFO, workplusQrCodeInfo);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mGroupScanFragment = new DiscussionScanAddFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DATA_DISCUSSION_SCAN_INFO, mWorkplusQrCodeInfo);
        mGroupScanFragment.setArguments(bundle);
        return mGroupScanFragment;
    }

}
