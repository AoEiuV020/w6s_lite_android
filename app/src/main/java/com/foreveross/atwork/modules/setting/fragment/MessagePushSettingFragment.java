package com.foreveross.atwork.modules.setting.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.component.WorkplusSwitchCompat;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.manager.MessageNoticeManager;
import com.foreveross.atwork.support.BackHandledFragment;

/**
 * 消息推送设置页面
 * Created by ReyZhang on 2015/5/6.
 */
public class MessagePushSettingFragment extends BackHandledFragment implements View.OnClickListener {

    private static final String TAG = MessagePushSettingFragment.class.getSimpleName();

    //----------顶部栏-------
    private ImageView mIvBack;
    private TextView mTvTitle;

    private WorkplusSwitchCompat mSwitchBtnNotice;
    private WorkplusSwitchCompat mSwitchBtnVoice;
    private WorkplusSwitchCompat mSwitchBtnVibrate;
    private WorkplusSwitchCompat mSwitchShowDetails;

    private RelativeLayout mShakeLayout;

    private RelativeLayout mVoiceLayout;

    private RelativeLayout mShowDetailLayout;

    private View mShakeLine;

    private View mVoiceLine;

    private View mShowDetailLine;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_push_setting, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
        setup();
        initData();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected void findViews(View view) {
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);

        mSwitchBtnNotice = view.findViewById(R.id.push_setting_notice_switch_btn);
        mSwitchBtnVoice = view.findViewById(R.id.push_setting_voice_switch_btn);
        mSwitchBtnVibrate = view.findViewById(R.id.push_setting_shake_switch_btn);
        mSwitchShowDetails = view.findViewById(R.id.push_setting_details_switch_btn);
        mShakeLayout = view.findViewById(R.id.activity_push_setting_shake_container);
        mVoiceLayout = view.findViewById(R.id.activity_push_setting_voice_container);
        mShowDetailLayout = view.findViewById(R.id.activity_push_setting_details_container);
        mShakeLine = view.findViewById(R.id.line_shake);
        mVoiceLine = view.findViewById(R.id.line_voice);
        mShowDetailLine = view.findViewById(R.id.line_show_details);
    }

    private void initUI() {
        tryMakeVoiceLayoutVisible();
    }

    private void tryMakeVoiceLayoutVisible() {
        ViewUtil.setVisible(mVoiceLayout, MessageNoticeManager.shouldSetSoundOnNotificationModel());
    }

    private void setup() {
        mIvBack.setOnClickListener(this);
        mSwitchBtnNotice.setOnClickNotPerformToggle(() -> {
            mSwitchBtnNotice.toggle();
            showVoiceAndShakeAndShowDetail(mSwitchBtnNotice.isChecked());
            PersonalShareInfo.getInstance().setSettingNotice(getActivity(), mSwitchBtnNotice.isChecked());
            AtworkApplicationLike.modifyDeviceSettings();
        });
        mSwitchBtnVoice.setOnClickNotPerformToggle(() -> {
            mSwitchBtnVoice.toggle();
            PersonalShareInfo.getInstance().setSettingVoice(getActivity(), mSwitchBtnVoice.isChecked());
            AtworkApplicationLike.modifyDeviceSettings();
        });

        mSwitchBtnVibrate.setOnClickNotPerformToggle(() -> {
            mSwitchBtnVibrate.toggle();
            PersonalShareInfo.getInstance().setSettingVibrate(getActivity(), mSwitchBtnVibrate.isChecked());
            AtworkApplicationLike.modifyDeviceSettings();
        });

        mSwitchShowDetails.setOnClickNotPerformToggle(() -> {
            mSwitchShowDetails.toggle();
            PersonalShareInfo.getInstance().setSettingShowDetails(getActivity(), mSwitchShowDetails.isChecked());
            AtworkApplicationLike.modifyDeviceSettings();
        });
    }

    private void initData() {
        mTvTitle.setText(getString(R.string.push_message_setting));
        boolean isNotice = PersonalShareInfo.getInstance().getSettingNotice(getActivity());
        boolean isVoice = PersonalShareInfo.getInstance().getSettingVoice(getActivity());
        boolean isVibrate = PersonalShareInfo.getInstance().getSettingVibrate(getActivity());
        boolean isShowDetail = PersonalShareInfo.getInstance().getSettingShowDetails(getActivity());

        if (isNotice) {
            mSwitchBtnNotice.setChecked(true);
        }
        if (isVibrate) {
            mSwitchBtnVibrate.setChecked(true);
        }
        if (isVoice) {
            mSwitchBtnVoice.setChecked(true);
        }
        if (isShowDetail) {
            mSwitchShowDetails.setChecked(true);
        }

        showVoiceAndShakeAndShowDetail(isNotice);
    }

    /**
     * 设置通知状态，如果通知状态为关闭，则不显示"振动, 声音, 消息详情"等区域, 反之则显示
     *
     * @param isNotice
     */
    private void showVoiceAndShakeAndShowDetail(boolean isNotice) {
        if (isNotice) {
            mShakeLayout.setVisibility(View.VISIBLE);
            mVoiceLayout.setVisibility(View.VISIBLE);
            tryMakeVoiceLayoutVisible();

            mShakeLine.setVisibility(View.VISIBLE);

            return;
        }

        mShakeLayout.setVisibility(View.GONE);
        mVoiceLayout.setVisibility(View.GONE);
        mShowDetailLayout.setVisibility(View.GONE);

        mShakeLine.setVisibility(View.GONE);


    }

    private void tryShowDetailView() {
        if(AtworkConfig.NOTIFICATION_CONFIG.isCommandHideDetail()) {
            return;
        }

        mShowDetailLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.title_bar_common_back:
                finish();

                break;

        }
    }

    @Override
    protected boolean onBackPressed() {
        finish();

        return false;
    }


}
