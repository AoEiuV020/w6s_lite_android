package com.foreveross.atwork.modules.setting.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData;
import com.foreverht.workplus.ui.component.dialogFragment.W6sSelectDialogFragment;
import com.foreveross.atwork.R;
import com.foreveross.atwork.component.WorkplusSwitchCompat;
import com.foreveross.atwork.infrastructure.shared.EmailSettingInfo;
import com.foreveross.atwork.support.BackHandledFragment;

import java.util.ArrayList;

/**
 * Created by reyzhang22 on 2018/4/26.
 */

public class EmailSyncSettingFragment extends BackHandledFragment {

    private static final String IN_ONE_DAY = "1天内";
    private static final String IN_TWO_DAY = "2天内";
    private static final String IN_ONE_WEEK = "1周内";
    private static final String IN_TWO_WEEK = "2周内";
    private static final String IN_ONE_MONTH = "1月内";
    private static final String IN_THREE_MONTH = "3月内";

    private Activity mActivity;

    private TextView mTitle;
    private ImageView mBack;
    private TextView mRight;
    private WorkplusSwitchCompat mSyncEmailBtn;
    private View mSyncTimePicker;
    private ArrayList<String> itemList = new ArrayList<>();
    private TextView mDayTv;
    private W6sSelectDialogFragment mW6sSelectDialogFragment;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_email_background_sync_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        registerListener();
        initData();
    }

    @Override
    protected void findViews(View view) {

    }

    private void initViews(View view) {
        mBack = view.findViewById(R.id.title_bar_common_back);
        mTitle = view.findViewById(R.id.title_bar_common_title);
        mRight = view.findViewById(R.id.title_bar_common_right_text);
        mTitle.setText(getString(R.string.email_setting));
        mRight.setVisibility(View.GONE);
        mSyncEmailBtn = view.findViewById(R.id.email_sync_setting_switch_btn);
        mSyncTimePicker = view.findViewById(R.id.sync_time_picker);
        mDayTv = view.findViewById(R.id.day_tv);
        itemList.add(IN_ONE_DAY);
        itemList.add(IN_TWO_DAY);
        itemList.add(IN_ONE_WEEK);
        itemList.add(IN_TWO_WEEK);
        itemList.add(IN_ONE_MONTH);
        itemList.add(IN_THREE_MONTH);
    }

    private void registerListener() {
        mBack.setOnClickListener(view -> {
            onBackPressed();
        });

        mSyncEmailBtn.setOnClickNotPerformToggle(() -> {
            boolean checked = mSyncEmailBtn.isChecked();
            mSyncEmailBtn.setChecked(!checked);
            EmailSettingInfo.getInstance().setEmailSyncBg(mActivity, !checked);
        });

        mSyncTimePicker.setOnClickListener(v -> {

            mW6sSelectDialogFragment = new W6sSelectDialogFragment();
            mW6sSelectDialogFragment.setData(new CommonPopSelectData(itemList, null))
                    .setOnClickItemListener((position, value) ->  {
                        if (TextUtils.isEmpty(value)) {
                            return;
                        }
                        mDayTv.setText(value);
                        EmailSettingInfo.getInstance().setEmailSyncDay(mActivity, value);
                    })
                    .show(getChildFragmentManager(), "TEXT_POP_DIALOG");

        });

    }

    private void initData() {
        String dayIndex = EmailSettingInfo.getInstance().getEmailSyncDay(mActivity);
        mDayTv.setText(dayIndex);
        mSyncEmailBtn.setChecked(EmailSettingInfo.getInstance().getEmailSyncBg(mActivity));
    }


    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        return false;
    }
}
