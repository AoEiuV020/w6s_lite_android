package com.foreveross.atwork.modules.setting.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.component.WorkplusSwitchCompat;
import com.foreveross.atwork.infrastructure.shared.EmailSettingInfo;
import com.foreveross.atwork.manager.CalendarHelper;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;

public class CalendarSettingFragment extends BackHandledFragment {

    private Activity mActivity;

    private TextView mTitle;
    private ImageView mBack;
    private TextView mRight;
    private WorkplusSwitchCompat mSyncCalBtn;
    private View mClearCal;
    private ProgressDialogHelper mProgress;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initData();
        registerListener();
    }

    @Override
    protected void findViews(View view) {

    }

    private void initViews(View view) {
        mBack = view.findViewById(R.id.title_bar_common_back);
        mTitle = view.findViewById(R.id.title_bar_common_title);
        mRight = view.findViewById(R.id.title_bar_common_right_text);
        mTitle.setText(getString(R.string.cal_sync_setting));
        mRight.setVisibility(View.GONE);
        mSyncCalBtn = view.findViewById(R.id.calendar_sync_setting_switch_btn);
        mClearCal = view.findViewById(R.id.clear_local_calendar_layout);
        mProgress = new ProgressDialogHelper(mActivity);
    }

    private void initData() {
        mSyncCalBtn.setChecked(EmailSettingInfo.getInstance().getSyncCalendar(mActivity));
    }

    private void registerListener() {
        mBack.setOnClickListener(view -> {
            onBackPressed();
        });

        mSyncCalBtn.setOnClickNotPerformToggle(() -> {
            boolean checked = mSyncCalBtn.isChecked();
            mSyncCalBtn.setChecked(!checked);
            EmailSettingInfo.getInstance().setSyncCalendar(getContext(), !checked);
        });

    }

    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        return true;
    }
}
