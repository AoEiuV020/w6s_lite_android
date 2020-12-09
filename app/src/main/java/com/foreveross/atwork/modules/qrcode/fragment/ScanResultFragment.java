package com.foreveross.atwork.modules.qrcode.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.chat.util.AutoLinkHelper;
import com.foreveross.atwork.modules.qrcode.activity.ScanResultActivity;
import com.foreveross.atwork.support.BackHandledFragment;

/**
 * Created by dasunsy on 15/12/10.
 */
public class ScanResultFragment extends BackHandledFragment {

    private static final String TAG = ScanResultFragment.class.getSimpleName();

    private TextView mTvTitle;
    private ImageView mIvBack;
    private TextView mTvResultTip;
    private TextView mTvResult;

    private String mContentData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan_result, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();
        initData();

        mTvTitle.setText(R.string.qrcode_result);
        mTvResultTip.setText(getString(R.string.qrcode_result_tip, getString(R.string.app_name)));
        mTvResult.setText(AutoLinkHelper.getAtInstance().getSpannableString(mActivity, "", null, mTvResult, mContentData));

    }

    @Override
    protected void findViews(View view) {
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mTvResultTip = view.findViewById(R.id.tv_result_tip);
        mTvResult = view.findViewById(R.id.tv_result);

    }

    private void registerListener() {
        mIvBack.setOnClickListener(v -> onBackPressed());
    }

    private void initData() {
        mContentData = getArguments().getString(ScanResultActivity.DATA_RESULT_CONTENT, "");
    }

    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        return false;
    }

}
