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
import com.foreveross.atwork.component.WorkplusSwitchCompat;
import com.foreveross.atwork.infrastructure.shared.EmailSettingInfo;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.manager.MessageNoticeManager;
import com.foreveross.atwork.support.BackHandledFragment;

/**
 * Created by reyzhang22 on 2018/4/26.
 */

public class EmailRemindFragment extends BackHandledFragment {

    private Activity mActivity;

    private TextView mTitle;
    private ImageView mBack;
    private TextView mRight;

    private WorkplusSwitchCompat mReceivedRemindBtn;
    private WorkplusSwitchCompat mVoiceBtn;
    private WorkplusSwitchCompat mVibBtn;

    private View mVoiceRemindView;
    private View mVibRemindView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_email_remind, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
        registerListener();
        initData();
    }

    @Override
    protected void findViews(View view) {
        mBack = view.findViewById(R.id.title_bar_common_back);
        mTitle = view.findViewById(R.id.title_bar_common_title);
        mRight = view.findViewById(R.id.title_bar_common_right_text);
        mTitle.setText(getString(R.string.email_setting));
        mRight.setVisibility(View.GONE);

        mVoiceRemindView = view.findViewById(R.id.activity_push_setting_voice_container);
        mVibRemindView = view.findViewById(R.id.activity_push_setting_shake_container);

        mReceivedRemindBtn = view.findViewById(R.id.email_push_setting_notice_switch_btn);
        mVoiceBtn = view.findViewById(R.id.email_push_setting_voice_switch_btn);
        mVibBtn = view.findViewById(R.id.email_push_setting_shake_switch_btn);
    }


    private void initUI() {
        tryMakeVoiceLayoutVisible();
    }

    private void tryMakeVoiceLayoutVisible() {
        ViewUtil.setVisible(mVoiceRemindView, MessageNoticeManager.shouldSetSoundOnNotificationModel());
    }

    private void registerListener() {
        mBack.setOnClickListener(view -> {
            onBackPressed();
        });
        mReceivedRemindBtn.setOnClickNotPerformToggle(() ->{
            setButtonToggle(mReceivedRemindBtn);
            EmailSettingInfo.getInstance().setEmailRemindSetting(mActivity, mReceivedRemindBtn.isChecked());
            setButtonStatus(mReceivedRemindBtn.isChecked());
        });
        mVoiceBtn.setOnClickNotPerformToggle(() ->{
            setButtonToggle(mVoiceBtn);
            EmailSettingInfo.getInstance().setEmailVoiceNotice(mActivity,mVoiceBtn.isChecked());
        });
        mVibBtn.setOnClickNotPerformToggle(() ->{
            setButtonToggle(mVibBtn);
            EmailSettingInfo.getInstance().setEmailVibrateNotice(mActivity, mVibBtn.isChecked());
        });
    }

    private void initData() {
        boolean isRemind = EmailSettingInfo.getInstance().getEmailRemindSetting(mActivity);
        setButtonStatus(isRemind);
        mVoiceBtn.setChecked(EmailSettingInfo.getInstance().getEmailVoiceNotice(mActivity));
        mVibBtn.setChecked(EmailSettingInfo.getInstance().getEmailVibrateNotice(mActivity));
    }

    private void setButtonToggle(WorkplusSwitchCompat button) {
        boolean isChecked = button.isChecked();
        button.setChecked(!isChecked);
    }

    private void setButtonStatus(boolean isRemind) {
        mReceivedRemindBtn.setChecked(isRemind);

        if(isRemind) {
            tryMakeVoiceLayoutVisible();
        } else {
            mVoiceRemindView.setVisibility(View.GONE);
        }

        mVibRemindView.setVisibility(isRemind ? View.VISIBLE : View.GONE);
    }


    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        return false;
    }
}
