package com.foreveross.atwork.modules.setting.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.setting.activity.CalendarSettingsActivity;
import com.foreveross.atwork.modules.setting.activity.EmailRemindActivity;
import com.foreveross.atwork.modules.setting.activity.EmailSyncSettingActivity;
import com.foreveross.atwork.support.BackHandledFragment;

/**
 * Created by reyzhang22 on 2018/4/26.
 */

public class EmailSettingFragment extends BackHandledFragment {

    private View mEmailRemind;
    private View mEmailSync;
    private View mCalSyncSetting;
    private TextView mTitle;
    private ImageView mBack;
    private TextView mRight;

    private Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_email_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        registerListener();
    }

    @Override
    protected void findViews(View view) {

    }

    private void initViews(View view) {
        mEmailRemind = view.findViewById(R.id.email_remind_layout);
        mEmailSync = view.findViewById(R.id.email_sync_background_layout);
        mCalSyncSetting = view.findViewById(R.id.calendar_sync_setting_layout);
        mBack = view.findViewById(R.id.title_bar_common_back);
        mTitle = view.findViewById(R.id.title_bar_common_title);
        mRight = view.findViewById(R.id.title_bar_common_right_text);
        mTitle.setText(getString(R.string.email_setting));
        mRight.setVisibility(View.GONE);
    }

    private void registerListener() {
        mBack.setOnClickListener(view -> {
            onBackPressed();
        });

        mEmailSync.setOnClickListener(view -> {
            EmailSyncSettingActivity.startEmailSyncSettingActivity(mActivity);
        });

        mEmailRemind.setOnClickListener(view -> {
            EmailRemindActivity.startEmailRemindActivity(mActivity);
        });

        mCalSyncSetting.setOnClickListener(view -> {
            CalendarSettingsActivity.startCalendarSettings(mActivity);
        });
    }

    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        return false;
    }
}
