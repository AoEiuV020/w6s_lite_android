package com.foreveross.atwork.modules.voip.fragment.agora;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.CallParams;
import com.foreveross.atwork.infrastructure.model.voip.CallState;
import com.foreveross.atwork.infrastructure.model.voip.PhoneState;
import com.foreveross.atwork.infrastructure.model.voip.UserType;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.rom.FloatWindowPermissionUtil;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.manager.VoipMeetingController;
import com.foreveross.atwork.modules.voip.activity.CallActivity;
import com.foreveross.atwork.modules.voip.activity.agora.AgoraCallActivity;
import com.foreveross.atwork.modules.voip.adapter.RecyclerViewNoIndexOutOfBoundsExceptionGridLayoutManager;
import com.foreveross.atwork.modules.voip.adapter.agora.VoipGroupMembersAdapter;
import com.foreveross.atwork.modules.voip.component.agora.MeetingVideoModeItemView;
import com.foreveross.atwork.modules.voip.component.agora.MeetingVideoModeMainBigView;
import com.foreveross.atwork.modules.voip.fragment.VoipSelectModeFragment;
import com.foreveross.atwork.modules.voip.model.ViewMode;
import com.foreveross.atwork.modules.voip.support.agora.interfaces.OnControllerVoipListener;
import com.foreveross.atwork.modules.voip.support.agora.interfaces.OnVoipStatusListener;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.AvatarHelper;
import com.foreveross.atwork.utils.FloatWinHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by dasunsy on 16/9/13.
 */
public class AgoraCallFragment extends BackHandledFragment implements View.OnClickListener, OnVoipStatusListener {

    public static final String TAG = AgoraCallFragment.class.getName();

    /**
     * 点击最小化按钮时, 申请 system_windows_overlay 权限
     */
    public final static int REQUEST_CODE_PERMISSION_WINDOWS_OVERLAY_ON_BTN = 1;

    /**
     * 按返回时, 申请 system_windows_overlay 权限
     */
    public final static int REQUEST_CODE_PERMISSION_WINDOWS_OVERLAY_ON_BACK = 2;

    /**
     * voip 重连最大次数, 若达到最大次数, 需要中断语音视频
     */
    public final static int RE_CONNECTED_COUNT_MAX = 10;

    private LinearLayout mLlCallInitArea;
    private RecyclerView mRvGroupMembers;
    private ImageView mIvAvatar;
    private TextView mTvUsername;
    private TextView mTvPreCallTip;
    private LinearLayout mLlAvatarArea;
    private TextView mTvCallingTip;
    private TextView mTvWarnTipVoiceMode;
    private android.widget.LinearLayout mLlStatusTipArea;
    private ImageView mIvSilence;
    private RelativeLayout mRlSilence;
    private ImageView mIvHangfree;
    private RelativeLayout mRlHangfree;
    private ImageView mIvVideo;
    private RelativeLayout mRlVideo;
    private ImageView mIvInvite;
    private TextView mTvInvite;
    private RelativeLayout mRlInvite;
    private ImageView mIvCancel;
    private RelativeLayout mRlCancel;
    private ImageView mIvMinmize;
    private RelativeLayout mRlMinmize;
    private GridLayout mGlCallingControlArea;
    private ImageView mIvHangupCall;
    private TextView mTvHangupCall;
    private RelativeLayout mRlHangupCall;
    private ImageView mIvPickupCall;
    private RelativeLayout mRlPickupCall;
    private LinearLayout mRlCallInitControlArea;
    private FrameLayout mFlControlArea;
    private RelativeLayout mRlNoAnswer;
    private MeetingVideoModeMainBigView mViewVideoBig;
    private HorizontalScrollView mHsVideoSmall;
    private LinearLayout mLlVideoSmall;
    private RelativeLayout mRlVideoStatusBar;
    private TextView mTvTimeVideoStaus;
    private ImageView mIvSwitchCamera;
    private TextView mTvWarnTipVideoMode;
    private TextView mTvVideo;
    private TextView mTvSilence;
    private TextView mTvHangFree;


    private VoipGroupMembersAdapter mVoipGroupMemberAdapter;

    private PowerManager.WakeLock mWakelock;

    // 当前View模式
    private ViewMode mViewMode = ViewMode.RECEIVE_CALL;

    private VoipType mVoipType = VoipType.VOICE;

    private boolean mIsFromOutSide = false;

    private static boolean mFirstInitCall = false; //delay show the bottom buttons for the first time

    private Handler mHandler = new Handler();

    private ScheduledExecutorService mWaitAnswerService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture mWaitAnswerFuture;

    private int mNoAnswerLayStayDuration = 30; // 无人答复提示逗留时间
    private int mWaitAnswerSecondsLeft = AtworkConfig.VOIP_MAX_WAIT_ANSWER_DURATION - mNoAnswerLayStayDuration; // 等待对方接听的倒计时剩余时间

    private UserHandleInfo mInviter = null;

    private int mReconnectedTimes = 0;     //重连次数

    private boolean mIsReconnecting = false;     //是否正在重连

    private long mCallSecDuration = 0;


    Runnable mNoAnswerShowAfterRunnable = new Runnable() {
        public void run() {
            mRlNoAnswer.setVisibility(View.GONE);
            toast(R.string.voip_calling_expired);

            autoFinishCall();
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_call_agora, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();

        startCall();

        refreshMyControlButton();

        handleInitCallVIew();

        keepScreenWake();
    }

    @Override
    public void onResume() {
        super.onResume();

        keepScreenWake();
    }

    @Override
    public void onPause() {
        super.onPause();

        releaseWakeLock();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        clearData();

        releaseWakeLock();
    }


    @Override
    protected void findViews(View view) {

        mLlCallInitArea = view.findViewById(R.id.rl_call_init_control_area);
        mFlControlArea = view.findViewById(R.id.fl_control_area);

        mRlCallInitControlArea = view.findViewById(R.id.rl_call_init_control_area);
        mRlPickupCall = view.findViewById(R.id.rl_pickup_call);
        mIvPickupCall = view.findViewById(R.id.iv_pickup_call);
        mRlHangupCall = view.findViewById(R.id.rl_hangup_call);
        mIvHangupCall = view.findViewById(R.id.iv_hangup_call);
        mTvHangupCall = view.findViewById(R.id.tv_hangup_call);

        mGlCallingControlArea = view.findViewById(R.id.gl_calling_control_area);
        mRlMinmize = view.findViewById(R.id.rl_minmize);
        mIvMinmize = view.findViewById(R.id.iv_minmize);
        mRlCancel = view.findViewById(R.id.rl_cancel);
        mIvCancel = view.findViewById(R.id.iv_cancel);
        mRlInvite = view.findViewById(R.id.rl_invite);
        mIvInvite = view.findViewById(R.id.iv_invite);
        mTvInvite = view.findViewById(R.id.tv_invite);
        mRlVideo = view.findViewById(R.id.rl_video);
        mIvVideo = view.findViewById(R.id.iv_video);
        mRlHangfree = view.findViewById(R.id.rl_hang_free);
        mIvHangfree = view.findViewById(R.id.iv_hang_free);
        mRlSilence = view.findViewById(R.id.rl_silence);
        mIvSilence = view.findViewById(R.id.iv_silence);
        mTvVideo = view.findViewById(R.id.tv_video);
        mTvHangFree = view.findViewById(R.id.tv_hang_free);
        mTvSilence = view.findViewById(R.id.tv_silence);

        mLlStatusTipArea = view.findViewById(R.id.ll_status_calling_tip_area);
        mTvCallingTip = view.findViewById(R.id.tv_calling_status_tip);
        mTvWarnTipVoiceMode = view.findViewById(R.id.tv_warn_tip_voice);

        mLlAvatarArea = view.findViewById(R.id.ll_avatar_area);
        mTvPreCallTip = view.findViewById(R.id.tv_pre_call_tip);
        mTvUsername = view.findViewById(R.id.tv_user_name);
        mIvAvatar = view.findViewById(R.id.iv_avatar);
        mRvGroupMembers = view.findViewById(R.id.rv_group_members);

        mRlNoAnswer = view.findViewById(R.id.rl_no_answer);

        mViewVideoBig = view.findViewById(R.id.view_video_big);
        mHsVideoSmall = view.findViewById(R.id.hs_video_small);
        mLlVideoSmall = view.findViewById(R.id.ll_video_small);

        mRlVideoStatusBar = view.findViewById(R.id.rl_vedio_status_bar);
        mTvTimeVideoStaus = view.findViewById(R.id.tv_time_video_status);
        mIvSwitchCamera = view.findViewById(R.id.iv_switch_camera);
        mTvWarnTipVideoMode = view.findViewById(R.id.tv_warn_tip_video);

    }

    private void registerListener() {
        mRlPickupCall.setOnClickListener(this);
        mRlHangupCall.setOnClickListener(this);
        mRlHangfree.setOnClickListener(this);
        mRlCancel.setOnClickListener(this);
        mRlMinmize.setOnClickListener(this);
        mRlSilence.setOnClickListener(this);
        mRlInvite.setOnClickListener(this);
        mRlVideo.setOnClickListener(this);

        mIvPickupCall.setOnClickListener(this);
        mIvHangupCall.setOnClickListener(this);
        mIvHangfree.setOnClickListener(this);
        mIvCancel.setOnClickListener(this);
        mIvMinmize.setOnClickListener(this);
        mIvSilence.setOnClickListener(this);
        mIvInvite.setOnClickListener(this);
        mIvVideo.setOnClickListener(this);

        mIvSwitchCamera.setOnClickListener(this);
        mViewVideoBig.setOnClickListener(this);
    }

    private void initData() {
        Bundle bundle = getArguments();
        mVoipType = (VoipType) bundle.getSerializable(AgoraCallActivity.EXTRA_VOIP_TYPE);
        mIsFromOutSide = bundle.getBoolean(AgoraCallActivity.EXTRA_START_FROM_OUTSIDE);
        mInviter = bundle.getParcelable(AgoraCallActivity.EXTRA_INVITER);

    }


    private void handleInitLoudSpeak() {
        if (VoipType.VOICE.equals(mVoipType)) {
            VoipMeetingController.getInstance().enableLoudSpeaker(false);
        }
    }

    private void handleInitCallVIew() {
        VoipMeetingMember mySelf = VoipMeetingController.getInstance().getMySelf();
        if (mySelf == null) {
            return;
        }

        if (mySelf.getUserType() == UserType.Originator) { // 我是发送方
            switchViewMode(getInitViewMode());

        } else if (mySelf.getUserType() == UserType.Recipient) { // 我是接收方
            // 判断呼状态
            if (VoipManager.getInstance().getCallState() == CallState.CallState_Init) {
                switchViewMode(ViewMode.RECEIVE_CALL);

            } else {
                switchViewMode(getInitViewMode());
            }
        }

        restoreNeedShowVideo();

        onUsersProfileRefresh();

    }

    private void startCall() {

        if (!mIsFromOutSide) {

            //init the call type
            VoipMeetingController.getInstance().enableVideo(VoipType.VIDEO == mVoipType);

            CallParams callParams = VoipMeetingController.getInstance().getCurrentVoipMeeting().mCallParams;

            if (callParams.isGroupChat()) {
                VoipMeetingController.getInstance().initGroupCall();

            } else {
                VoipMeetingController.getInstance().initPeerCall();
            }

            mFirstInitCall = true;

        } else {
            VoipMeetingController.getInstance().enableVideo(VoipMeetingController.getInstance().haveVideoNeedRestore());

        }
    }

    public ViewMode getInitViewMode() {
        if (mFirstInitCall) {
            if (VoipMeetingController.getInstance().isGroupChat()) {
                return ViewMode.AUDIO_GROUP;

            } else {
                return ViewMode.AUDIO_P2P;

            }

        } else {
            if (VoipMeetingController.getInstance().haveVideoNeedRestore()) {
                if (VoipMeetingController.getInstance().isGroupChat()) {
                    return ViewMode.VIDEO_GROUP;

                } else {
                    return ViewMode.VIDEO_P2P;

                }

            } else {
                if (VoipMeetingController.getInstance().isGroupChat()) {
                    return ViewMode.AUDIO_GROUP;

                } else {
                    return ViewMode.AUDIO_P2P;

                }
            }
        }
    }

    private void restoreNeedShowVideo() {
        if (VoipMeetingController.getInstance().haveVideoNeedRestore()) {
            checkPermissionAndOpenVideo(true, false);
        }
    }

    /**
     * @brief 等待接听倒计时启动
     */
    private void startWaitAnswerCountDown() {
        mWaitAnswerFuture = mWaitAnswerService.schedule(() -> {
            if (UserType.Originator == getMySelf().getUserType()) {
                if (isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        mRlNoAnswer.setVisibility(View.VISIBLE);
                        startWaitNoAnswerHandler();
                    });

                }


            } else {
                autoFinishCall();

            }

        }, mWaitAnswerSecondsLeft, TimeUnit.SECONDS);
    }


    /**
     * @brief 停止等待接听计时并重置
     */
    public void stopWaitAnswerCountDown() {
        if (mWaitAnswerFuture != null) {
            mWaitAnswerFuture.cancel(true);
            mWaitAnswerFuture = null;
        }
    }

    private void startWaitNoAnswerHandler() {
        //#mNoAnswerLayStayDuration 时间后, 自动消失
        mRlNoAnswer.postDelayed(mNoAnswerShowAfterRunnable, mNoAnswerLayStayDuration * 1000);
    }

    public void stopWaitNoAnswerHandler() {
        if (null != mRlNoAnswer) {
            mRlNoAnswer.removeCallbacks(mNoAnswerShowAfterRunnable);
            mRlNoAnswer.setVisibility(View.GONE);
        }
    }


    @Override
    protected boolean onBackPressed() {
        if (CallState.CallState_Init != getCallState() && CallState.CallState_Idle != getCallState()) {
            checkPopPermissionAndDoMinimize();
        }

        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_pickup_call: {
                if (CommonUtil.isFastClick(500)) {
                    return;
                }
                acceptCallAndCheckPermission();
                break;
            }

            case R.id.iv_hangup_call: {
                if (CommonUtil.isFastClick(500)) {
                    return;
                }

                if(UserType.Originator == getMySelf().getUserType()) {
                    toast(R.string.voip_meeting_has_canceled_self);

                    stopWaitNoAnswerHandler();

                } else {
                    toast(R.string.voip_meeting_has_rejected_self);

                }

                finishCall();
                break;
            }


            case R.id.iv_cancel: {
                if (CommonUtil.isFastClick(500)) {
                    return;
                }

                toast(R.string.voip_meeting_end_tip_self);

                finishCall();
                break;
            }

            case R.id.iv_minmize: {
                if (CommonUtil.isFastClick(500)) {
                    return;
                }
                checkPopPermissionAndDoMinimize();
                break;
            }

            case R.id.iv_hang_free: {

                boolean success = enableLoudSpeaker(!mIvHangfree.isSelected());
                if (!success) {
                    AtworkToast.showResToast(R.string.switch_loud_speak_failed);
                }
                break;
            }

            case R.id.iv_silence: {
                boolean success = VoipMeetingController.getInstance().muteSelf(!mIvSilence.isSelected());
                if (success) {
                    VoipManager.refreshMeeting(AtworkApplicationLike.baseContext, getMeetingId(), null);
                    onUsersProfileRefresh();
                }

                break;
            }

            case R.id.iv_invite: {
                if (CommonUtil.isFastClick(500)) {
                    return;
                }

                boolean isMax = (AtworkConfig.VOIP_MEMBER_COUNT_MAX == VoipMeetingController.getInstance().getVoipMemInMeetingList().size());
                if (null != getControllerVoipListener() && !isMax) {
                    getControllerVoipListener().onInviteMember();
                }
                break;
            }

            case R.id.iv_video: {
                if (CommonUtil.isFastClick(500)) {
                    return;
                }

                clickVideoHandleActionAndCheckPermission();

                break;
            }

            case R.id.iv_switch_camera: {
                if (CommonUtil.isFastClick(500)) {
                    return;
                }

                VoipMeetingController.getInstance().switchCamera();
                break;
            }

            case R.id.view_video_big: {
                if (mGlCallingControlArea.isShown()) {
                    mGlCallingControlArea.setVisibility(View.GONE);

                } else {
                    mGlCallingControlArea.setVisibility(View.VISIBLE);

                }
                break;
            }
        }
    }

    public boolean enableLoudSpeaker(boolean isEnable) {
        boolean success = VoipMeetingController.getInstance().enableLoudSpeaker(isEnable);

        if (success) {
            handleLoudSpeakerSelected(isEnable);

        }

        return success;
    }


    public void clickVideoHandleActionAndCheckPermission() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(), new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                clickVideoHandleAction();

            }

            @Override
            public void onDenied(String permission) {
                AtworkUtil.popAuthSettingAlert(getContext(), permission);
            }
        });
    }

    public void clickVideoHandleAction() {
        boolean isOpenVideo = !mIvVideo.isSelected();
        boolean success = false;

        if (VoipMeetingController.getInstance().isGroupChat()) {
            if (isOpenVideo) {
                switchViewMode(ViewMode.VIDEO_GROUP);

            } else {
                switchViewMode(ViewMode.AUDIO_GROUP);
            }
        } else {

            if (isOpenVideo) {
                switchViewMode(ViewMode.VIDEO_P2P);

            } else {
                switchViewMode(ViewMode.AUDIO_P2P);

            }
        }


        if (isOpenVideo) {
            success = openVideo(true);

            if (!success) {
                AtworkToast.showResToast(R.string.open_video_failed);
            }


        } else {

            VoipMeetingController.getInstance().saveShowingVideo(-1);

            clearAllVideoViews();

            success = VoipMeetingController.getInstance().enableVideo(false);

        }


        if (success) {
            onUsersProfileRefresh();
        }
    }

    public void checkPermissionAndOpenVideo(boolean isOpenVideo, boolean handleSwitchViewNow) {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(), new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                boolean success = openVideo(isOpenVideo);
                if (success) {
//                    onUsersProfileRefresh();

                }

                if (handleSwitchViewNow) {
                    if (VoipMeetingController.getInstance().isGroupChat()) {
                        switchViewMode(ViewMode.VIDEO_GROUP);

                    } else {
                        switchViewMode(ViewMode.VIDEO_P2P);

                    }
                }
            }

            @Override
            public void onDenied(String permission) {
                AtworkUtil.popAuthSettingAlert(getContext(), permission);
            }
        });

    }

    public boolean openVideo(boolean isOpenVideo) {
        boolean success;
        success = VoipMeetingController.getInstance().enableVideo(isOpenVideo);

        if (success) {
            //若该会议从没有开启过视频, 则默认开启扬声器, 打开前置摄像头
            if (!VoipManager.getInstance().isMeetingHasOpenVideo(getMeetingId())) {

                enableLoudSpeaker(true);

                VoipMeetingController.getInstance().switchFrontCamera();

                VoipManager.getInstance().setMeetingHasOpenVideo(getMeetingId());
            }
        }

        int currentBigViewUid = VoipMeetingController.getInstance().getCurrentBigVideoUid();

        mViewVideoBig.bindVideoView(VoipMeetingController.getInstance().findMember(currentBigViewUid));
        mViewVideoBig.setTag(currentBigViewUid);

        VoipMeetingController.getInstance().saveShowingVideo(currentBigViewUid);

        mViewVideoBig.refresh();

        createVideoSmallView();
        return success;
    }

    public void createVideoSmallView() {
        List<VoipMeetingMember> meetingMemberList = VoipMeetingController.getInstance().getVoipMemInCallList();
        int currentBigViewUid = VoipMeetingController.getInstance().getCurrentBigVideoUid();

        boolean isHaveNewView = false;

        for (VoipMeetingMember member : meetingMemberList) {
            if (currentBigViewUid == member.getUid()) {
                continue;
            }

            MeetingVideoModeItemView itemView = mLlVideoSmall.findViewWithTag(member.getUid());
            if (null == itemView) {
                itemView = new MeetingVideoModeItemView(getActivity());
                itemView.setTag(member.getUid());
                itemView.bindVideoView(member);

                MeetingVideoModeItemView finalItemView = itemView;

                itemView.setOnClickListener((v) -> {
                    VoipMeetingController.getInstance().switchVideoView(mViewVideoBig, finalItemView);

                });

                mLlVideoSmall.addView(itemView);

                itemView.refresh();

                isHaveNewView = true;
            }

        }

        //if having new view, then need show to the user. So, just do the full scroll.
        if (isHaveNewView) {
            mHsVideoSmall.postDelayed(() -> {
                mHsVideoSmall.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }, 100);

        }
    }

    public void clearAllVideoViews() {
        List<VoipMeetingMember> meetingMemberList = VoipMeetingController.getInstance().getVoipMemInMeetingList();
        for (VoipMeetingMember meetingMember : meetingMemberList) {
            VoipMeetingController.getInstance().removeVideoView(meetingMember.getUid());
        }


        mLlVideoSmall.removeAllViews();
    }

    private void handleInviteBtn(boolean isMax) {
        if (isMax) {
            mIvInvite.setImageResource(R.mipmap.voip_invite_max);
            mTvInvite.setText(R.string.had_to_max);
            mTvInvite.setTextColor(Color.parseColor("#4B5F71"));


        } else {
            mIvInvite.setImageResource(R.mipmap.voip_invite);
            mTvInvite.setText(R.string.label_invite);
            mTvInvite.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

        }

        mIvInvite.setSelected(isMax);
    }

    private void handleVideoSelected(boolean enableVideo) {
        if (enableVideo) {

            mIvVideo.setImageResource(R.mipmap.voip_video_open);
            mTvVideo.setTextColor(ContextCompat.getColor(getActivity(), R.color.voip_agora_selected_blue));

        } else {

            if (VoipMeetingController.getInstance().isOtherEnableVideo()) {
                mIvVideo.setImageResource(R.mipmap.voip_video_notice);

            } else {
                mIvVideo.setImageResource(R.mipmap.voip_video_close);

            }

            mTvVideo.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));


        }

        mIvVideo.setSelected(enableVideo);
    }

    private void handleLoudSpeakerSelected(boolean enableLoud) {

        if (enableLoud) {

            mIvHangfree.setImageResource(R.mipmap.voip_hangup_open);
            mTvHangFree.setTextColor(ContextCompat.getColor(getActivity(), R.color.voip_agora_selected_blue));

        } else {
            mIvHangfree.setImageResource(R.mipmap.voip_hangup_close);
            mTvHangFree.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

        }

        mIvHangfree.setSelected(enableLoud);
    }

    private void handleSilenceSelected(boolean enableMuted) {
        if (enableMuted) {

            mIvSilence.setImageResource(R.mipmap.voip_silence_open);
            mTvSilence.setTextColor(ContextCompat.getColor(getActivity(), R.color.voip_agora_selected_blue));


        } else {
            mIvSilence.setImageResource(R.mipmap.voip_silence_close);
            mTvSilence.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

        }

        mIvSilence.setSelected(enableMuted);
    }

    private void handleHangupTextView() {
        if (CallState.CallState_Init == getCallState()) {

            if (UserType.Originator == getMySelf().getUserType()) {
                mTvHangupCall.setText(R.string.cancel);

            } else {
                mTvHangupCall.setText(R.string.reject);

            }

        }

    }

    public void finishCall() {
        VoipMeetingController.getInstance().finishCall();
    }

    /**
     * 处理超时 call 的时候的挂断处理(拨打方才发送"reject"通知)
     */
    public void autoFinishCall() {
        VoipMeetingMember myself = getMySelf();
        if (null == myself) {
            return;
        }
        if (UserType.Originator == myself.getUserType()) {

            if (null != getControllerVoipListener()) {
                getControllerVoipListener().onCancelCall();
            }

            VoipMeetingController.getInstance().stopCall();


        } else if (UserType.Recipient == myself.getUserType()) {
            if (null != getControllerVoipListener() && VoipMeetingController.getInstance().isGroupChat()) {
//                getDelegate().onRejectCall();
            }

            VoipMeetingController.getInstance().stopCall();

        }

    }

    private void acceptCallAndCheckPermission() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                VoipMeetingController.getInstance().init(getActivity());
                acceptCall();
            }

            @Override
            public void onDenied(String permission) {

                final AtworkAlertDialog alertDialog = AtworkUtil.getAuthSettingAlert(getActivity(), permission);
                alertDialog.show();
            }
        });
    }


    /**
     * 接通电话
     */
    private void acceptCall() {
        if (null != getControllerVoipListener()) {
            getControllerVoipListener().onStartVoipMeeting();
            getControllerVoipListener().onAcceptCall();

            if (VoipMeetingController.getInstance().isGroupChat()) {
                switchViewMode(ViewMode.AUDIO_GROUP);
            } else {
                switchViewMode(ViewMode.AUDIO_P2P);
            }

            onUsersProfileRefresh();
        }
    }

    private void switchViewMode(ViewMode viewMode) {
        this.mViewMode = viewMode;
        switch (viewMode) {
            case AUDIO_P2P:
                switchToAudioP2pCallView();
                break;

            case AUDIO_GROUP:
                switchToAudioGroupCallView();
                break;

            case VIDEO_P2P:
                switchToVideoP2pCallView();
                break;

            case VIDEO_GROUP:
                switchToVideoGroupCallView();

                break;

            case RECEIVE_CALL:
                switchToReceiveCallView();

                break;
            default:
                break;
        }
    }

    private void switchToVideoGroupCallView() {
        mLlAvatarArea.setVisibility(View.GONE);
        mRvGroupMembers.setVisibility(View.GONE);
        mViewVideoBig.setVisibility(View.VISIBLE);
        mHsVideoSmall.setVisibility(View.VISIBLE);
        mRlVideoStatusBar.setVisibility(View.VISIBLE);

        mGlCallingControlArea.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent_70));

        refreshBottomControlArea();
        updateDurationText(VoipMeetingController.getInstance().getCallingTime());
        refreshWarnView();

    }

    private void switchToVideoP2pCallView() {
        mLlAvatarArea.setVisibility(View.GONE);
        mRvGroupMembers.setVisibility(View.GONE);
        mViewVideoBig.setVisibility(View.VISIBLE);
        mHsVideoSmall.setVisibility(View.VISIBLE);
        mRlVideoStatusBar.setVisibility(View.VISIBLE);

        mGlCallingControlArea.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent_70));

        refreshBottomControlArea();
        updateDurationText(VoipMeetingController.getInstance().getCallingTime());
        refreshWarnView();

    }


    private void switchToAudioP2pCallView() {
        mLlAvatarArea.setVisibility(View.VISIBLE);
        mViewVideoBig.setVisibility(View.GONE);
        mHsVideoSmall.setVisibility(View.GONE);
        mRlVideoStatusBar.setVisibility(View.GONE);

        mGlCallingControlArea.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
        handleHangupTextView();
        refreshBottomControlArea();
        updateDurationText(VoipMeetingController.getInstance().getCallingTime());
        refreshWarnView();
    }

    private void switchToAudioGroupCallView() {
        mLlAvatarArea.setVisibility(View.GONE);
        mRvGroupMembers.setVisibility(View.VISIBLE);
        mViewVideoBig.setVisibility(View.GONE);
        mHsVideoSmall.setVisibility(View.GONE);
        mRlVideoStatusBar.setVisibility(View.GONE);

        mGlCallingControlArea.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));

        handleHangupTextView();
        refreshBottomControlArea();
        updateDurationText(VoipMeetingController.getInstance().getCallingTime());
        refreshWarnView();

    }

    private void refreshWarnView() {
        if (CallState.CallState_ReConnecting == getCallState()
                || CallState.CallState_Disconnected == getCallState()) {
            if (VoipMeetingController.getInstance().isVideoCallOpened()) {
                mTvWarnTipVideoMode.setVisibility(View.VISIBLE);

            } else {
                mTvWarnTipVoiceMode.setVisibility(View.VISIBLE);

            }
        } else {
            mTvWarnTipVoiceMode.setVisibility(View.GONE);
            mTvWarnTipVideoMode.setVisibility(View.GONE);

        }

    }


    /**
     * 根据状态控制底部UI显示
     */
    private void refreshBottomControlArea() {
        if (CallState.CallState_Init == getCallState()) {
            mGlCallingControlArea.setVisibility(View.GONE);
            mRlCallInitControlArea.setVisibility(View.VISIBLE);
            mRlPickupCall.setVisibility(View.GONE);
            mRlHangupCall.setVisibility(View.VISIBLE);


        } else if (VoipMeetingController.getInstance().isOnceConnected()) {
            if (ViewMode.VIDEO_P2P == mViewMode || ViewMode.VIDEO_GROUP == mViewMode) {
                mGlCallingControlArea.setVisibility(View.VISIBLE);// 视频接通后需要先显示控制 area

            } else {
                mGlCallingControlArea.setVisibility(View.VISIBLE);

            }
            mRlCallInitControlArea.setVisibility(View.GONE);

            mTvPreCallTip.setText(StringUtils.EMPTY);
        }
    }


    private void switchToReceiveCallView() {
        mRlCallInitControlArea.setVisibility(View.VISIBLE);
        mRlHangupCall.setVisibility(View.VISIBLE);
        mRlPickupCall.setVisibility(View.VISIBLE);

        mLlAvatarArea.setVisibility(View.VISIBLE);
        mRlVideoStatusBar.setVisibility(View.GONE);


        mGlCallingControlArea.setVisibility(View.GONE);

        handleHangupTextView();
    }


    /**
     * 保持屏幕唤醒
     */
    private void keepScreenWake() {
        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
        mWakelock.acquire();
    }

    /**
     * @brief 释放屏幕唤醒
     */
    private void releaseWakeLock() {
        if (mWakelock != null) {
            mWakelock.release();
            mWakelock = null;
        }
    }

    @Override
    public void onUsersProfileRefresh() {
        if (isAdded() && isCurrentVoipMeetingValid()) {
            FragmentActivity activity = getActivity();

            activity.runOnUiThread(() -> {
                refreshMyControlButton();

                if (VoipMeetingController.getInstance().isGroupChat()) {
                    if (ViewMode.RECEIVE_CALL != mViewMode) {

                        if (ViewMode.AUDIO_P2P == mViewMode) {
                            switchViewMode(ViewMode.AUDIO_GROUP);

                        } else if (ViewMode.VIDEO_P2P == mViewMode) {
                            switchViewMode(ViewMode.VIDEO_GROUP);

                        }


                        if (null != mVoipGroupMemberAdapter) {
                            mVoipGroupMemberAdapter.refresh();

                        } else {

                            // Grid布局
                            RecyclerViewNoIndexOutOfBoundsExceptionGridLayoutManager gridLayoutMgr = new RecyclerViewNoIndexOutOfBoundsExceptionGridLayoutManager(getActivity(), 4);
                            mVoipGroupMemberAdapter = new VoipGroupMembersAdapter(activity, VoipMeetingController.getInstance().getGroup().mParticipantList);

                            mRvGroupMembers.setLayoutManager(gridLayoutMgr);
                            mRvGroupMembers.setAdapter(mVoipGroupMemberAdapter);
                        }
                    } else {
                        if (null != mInviter) {
                            this.mTvUsername.setText(mInviter.mShowName);
                            AvatarHelper.setUserInfoAvatar(mInviter, this.mIvAvatar);

                        }
                    }


                } else {
                    VoipMeetingMember peer = VoipMeetingController.getInstance().getPeer();

                    if (null != peer) {
                        this.mTvUsername.setText(peer.mShowName);
                        AvatarHelper.setUserInfoAvatar(peer, this.mIvAvatar);
                    }
                }


                //refresh video status
                refreshVideoViews();


            });
        }
    }


    public void refreshVideoViews() {
        if (VoipMeetingController.getInstance().isVideoCallOpened()) {

            //refresh big
            mViewVideoBig.refresh();

            //refresh small views
            int childCount = mLlVideoSmall.getChildCount();
            List<VoipMeetingMember> memberInMeetingList = VoipMeetingController.getInstance().getVoipMemInCallList();
            List<MeetingVideoModeItemView> itemViewRemovedList = new ArrayList<>();


            boolean isMeSwitchToBig = false;
            if (null != mViewVideoBig.getTag()) {
                int uidBigNow = (int) mViewVideoBig.getTag();
                isMeSwitchToBig = false;

                if (!VoipMeetingController.getInstance().isExistInCallList(uidBigNow)) {
                    mViewVideoBig.setTag(getMySelf().getUid());
                    mViewVideoBig.bindVideoView(getMySelf());

                    VoipMeetingController.getInstance().saveShowingVideo(getMySelf().getUid());

                    mViewVideoBig.refresh();

                    isMeSwitchToBig = true;
                }
            }


            for (int i = 0; i < childCount; i++) {
                View view = mLlVideoSmall.getChildAt(i);
                if (view instanceof MeetingVideoModeItemView) {
                    MeetingVideoModeItemView itemView = (MeetingVideoModeItemView) view;
                    int uid = (int) itemView.getTag();

                    if (!VoipMeetingController.getInstance().isExistInCallList(uid) || (isMeSwitchToBig && uid == getMySelf().getUid())) {
                        itemViewRemovedList.add(itemView);
                    } else {
                        itemView.refresh();

                    }

                }
            }

            //remove action
            for (MeetingVideoModeItemView removedView : itemViewRemovedList) {
                mLlVideoSmall.removeView(removedView);
            }

            //add action
            createVideoSmallView();
        }
    }

    public void checkPopPermissionAndDoMinimize() {
        if ((RomUtil.isMeizu() || FloatWinHelper.isXiaomiNeedFloatPermissionCheck())) {
            if (FloatWindowPermissionUtil.isFloatWindowOpAllowed(getActivity())) {
                doMinimize();

            } else {
                new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.CLASSIC)
                        .setTitleText(R.string.float_windows_no_permission_alert_title)
                        .setContent(getString(R.string.float_windows_no_permission_voip_alert_content, getString(R.string.app_name), getString(R.string.app_name)))
                        .hideDeadBtn()
                        .show();

            }

        }
        /** check if we already  have permission to draw over other apps */
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity())) {
            /** if not construct intent to request permission */
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getActivity().getPackageName()));
            /** request permission via start activity for result */
            startActivityForResult(intent, REQUEST_CODE_PERMISSION_WINDOWS_OVERLAY_ON_BACK);

        } else {

            doMinimize();
        }
    }

    /**
     * 最小化操作
     */
    public void doMinimize() {
        CallActivity.sIsOpening = false;

        getControllerVoipListener().onHideView();
    }


    /**
     * 根据状态更新, 处理交互, 包括文字提示的刷新以及 fragment 的消亡等
     */
    private void handleCallStateChanged(CallState callState) {
        if (null == mTvCallingTip || !isCurrentVoipMeetingValid())
            return;

        UserType userType = getMySelf().getUserType();

        boolean isGroupCall = VoipMeetingController.getInstance().isGroupChat();

        Log.i(TAG, "callState->" + callState.name());

        switch (callState) {
            case CallState_Idle:
                break;
            case CallState_Init:
                setControlAreaAvailable(false);

                if (UserType.Originator == getMySelf().getUserType()) {
                    mWaitAnswerSecondsLeft = AtworkConfig.VOIP_MAX_WAIT_ANSWER_DURATION - mNoAnswerLayStayDuration;

                } else {
                    mWaitAnswerSecondsLeft = AtworkConfig.VOIP_MAX_WAIT_ANSWER_DURATION;

                }

                stopWaitAnswerCountDown();

                startWaitAnswerCountDown(); // 开启等待接听倒计时

                if (isGroupCall) {
                    if (UserType.Originator == userType) {
                        setVoipCallingStatusTip(R.string.tangsdk_connecting_msg);

                    } else {
                        if (VoipType.VIDEO.equals(mVoipType)) {
                            setVoipPreCallStatusTip(R.string.voip_tip_invite_join_video_meeting);

                        } else {
                            setVoipPreCallStatusTip(R.string.voip_tip_invite_join_audio_meeting);

                        }
                    }


                } else {
                    if (UserType.Originator == userType) {
                        setVoipPreCallStatusTip(R.string.voip_tip_wait_peer_accept);

                    } else {
                        if (VoipType.VIDEO.equals(mVoipType)) {
                            setVoipPreCallStatusTip(R.string.voip_tip_invite_join_video_meeting);

                        } else {
                            setVoipPreCallStatusTip(R.string.voip_tip_invite_join_audio_meeting);

                        }
                    }
                }
                break;
            case CallState_StartCall:
//                setVoipCallingStatusTip(R.string.tangsdk_connecting_msg);

                break;
            case CallState_Waiting:
                break;
            case CallState_Calling:
                stopWaitNoAnswerHandler();   //结束"无人接听"提示的handler 定时任务
                stopWaitAnswerCountDown(); // 结束等待接听倒计时

                if (mFirstInitCall) {
                    handleInitLoudSpeak();
                    //sdk 存在缓存上一次静音状态的 bug, 所以这里需要在语音会议开始默认不静音的操作
                    VoipMeetingController.getInstance().muteSelf(false);
                }

                refreshBottomControlArea();
                refreshMyControlButton();
                refreshWarnView();

                if (mFirstInitCall) {
                    // 延迟1s点亮按钮
                    mHandler.postDelayed(() -> {
                        setControlAreaAvailable(true);

                        if (VoipType.VIDEO.equals(mVoipType)) {
                            checkPermissionAndOpenVideo(true, true);
                        }

                    }, 1000);

                } else {
                    setControlAreaAvailable(true);
                }

                mFirstInitCall = false;
                mReconnectedTimes = 0;
                mIsReconnecting = false;
//                stopWaitingAnim();
//                mIsReconnecting = false;
//                mRetriesTimes = 0;

                if (UserType.Originator == userType) {
                    VoipSelectModeFragment.finishActivity();

                }
                //开始定时器, 发送 refresh 告诉服务器后台当前成员是存活的
                VoipManager.getInstance().voipStartHeartBeat(getActivity(), getMeetingId());

                break;

            case CallState_Disconnected:
                setControlAreaAvailable(false);

                refreshWarnView();
                //声网断开链接, sdk 内部会开始重连
                mIsReconnecting = true;

                break;
            case CallState_ReConnecting:
                setControlAreaAvailable(false);

                if (mIsReconnecting) {
                    mReconnectedTimes++;
                }

                if (RE_CONNECTED_COUNT_MAX <= mReconnectedTimes) {
                    onReconnectFailed();
                }

                break;
            case CallState_Ended:
                setControlAreaAvailable(false);
                setCallInitAreaAvailable(false);
//                mLLCover.setVisibility(View.VISIBLE);
//                setVoipCallingStatusTip(R.string.tangsdk_call_will_soon_end);

//                this.clearData();

                mHandler.postDelayed(()->{

                    if (isAdded()) {
                        finish();
                    }

                }, 1000);

                break;
            default:
                break;
        }
    }


    private void setControlAreaAvailable(boolean isAvailable) {
        mRlSilence.setEnabled(isAvailable);
        mRlHangfree.setEnabled(isAvailable);
        mRlVideo.setEnabled(isAvailable);
        mRlInvite.setEnabled(isAvailable);
        mRlMinmize.setEnabled(isAvailable);
    }

    private void setCallInitAreaAvailable(boolean isAvailable) {
        mIvHangupCall.setClickable(isAvailable);
        mIvPickupCall.setClickable(isAvailable);
    }

    private void setVoipPreCallStatusTip(int contentResId) {
        mTvPreCallTip.setText(contentResId);
        mTvCallingTip.setText(StringUtils.EMPTY);
    }

    private void setVoipCallingStatusTip(int contentResId) {
        mTvCallingTip.setText(contentResId);
        mTvPreCallTip.setText(StringUtils.EMPTY);
    }

    private void setVoipCallingStatusTip(String content) {
        if (CallState.CallState_Calling == getCallState()
                || CallState.CallState_ReConnecting == getCallState()
                || CallState.CallState_Disconnected == getCallState()
                ) {

            switch (mViewMode) {
                case VIDEO_GROUP:
                case VIDEO_P2P:
                    mTvCallingTip.setText(StringUtils.EMPTY);
                    mTvTimeVideoStaus.setText(content);
                    break;

                case AUDIO_GROUP:
                case AUDIO_P2P:
                    mTvCallingTip.setText(content);
                    mTvTimeVideoStaus.setText(StringUtils.EMPTY);

                    break;
            }
            mTvPreCallTip.setText(StringUtils.EMPTY);
        }
    }

    private String getMeetingId() {
        return VoipManager.getInstance().getVoipMeetingController().getWorkplusVoipMeetingId();
    }


    public CallState getCallState() {
        return VoipManager.getInstance().getCallState();
    }

    private boolean isCurrentVoipMeetingValid() {
        return VoipMeetingController.getInstance().isCurrentVoipMeetingValid();
    }

    public VoipMeetingMember getMySelf() {
        return VoipMeetingController.getInstance().getMySelf();
    }

    public VoipMeetingMember getPeer() {
        return VoipMeetingController.getInstance().getPeer();
    }

    public VoipMeetingGroup getGroup() {
        return VoipMeetingController.getInstance().getGroup();
    }

    public OnControllerVoipListener getControllerVoipListener() {
        return VoipMeetingController.getInstance().getControllerVoipListener();
    }

    public void setControllerVoipListener(OnControllerVoipListener onControllerVoipListener) {
        VoipMeetingController.getInstance().setOnControllerVoipListener(onControllerVoipListener);
    }

    @Override
    public void onTipToast(String tip) {
        if (isAdded()) {
            getActivity().runOnUiThread(() -> {
                AtworkToast.showToast(tip);

            });
        }

    }

    @Override
    public void onLoudSpeakerStatusChanged(boolean bLoudSpeaker) {

    }

    @Override
    public void onIsSpeakingChanged(String strSpeakingNames) {

    }

    @Override
    public void onVideoItemAdded(String userId) {

    }

    @Override
    public void onVideoItemDeleted(String userId) {

    }

    @Override
    public Object onVideoItemAttachSurface(String userId) {
        return null;
    }

    @Override
    public void onVideoItemDetachSurface(String userId, Object videoSurface) {

    }

    @Override
    public void onVideoItemShowed(String userId, String domainId) {

    }

    @Override
    public void onVideoCallClosed() {

    }

    @Override
    public void onCallStateChanged(CallState callState) {
        if (isAdded()) {
            getActivity().runOnUiThread(() -> handleCallStateChanged(callState));
        }

    }

    @Override
    public void onCallingTimeElpased(long nSeconds) {
        if (isAdded()) {
            mCallSecDuration = nSeconds;

            getActivity().runOnUiThread(() -> updateDurationText(nSeconds));
        }
    }

    @Override
    public void onVOIPQualityIsBad() {

    }

    @Override
    public void onPhoneCallResult(boolean bSucceeded) {

    }

    @Override
    public void onPhoneStateChanged(PhoneState phoneState) {

    }

    /**
     * 尝试重连后失败
     */
    private void onReconnectFailed() {
        AtworkToast.showResToast(R.string.voip_reconnect_failed);

        //此时不需要其他接口返回错误的提示了
        if (getActivity() instanceof AgoraCallActivity) {
            ((AgoraCallActivity) getActivity()).setNeedCommonRequestTip(false);
        }

        finishCall();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /** check if received result code
             is equal our requested code for draw permission  */
            if (REQUEST_CODE_PERMISSION_WINDOWS_OVERLAY_ON_BTN == requestCode) {
                //if so check once again if we have permission
                if (Settings.canDrawOverlays(getActivity())) {
                    // continue here - permission was granted
                    doMinimize();
                }
            } else if (REQUEST_CODE_PERMISSION_WINDOWS_OVERLAY_ON_BACK == requestCode) {
                //if so check once again if we have permission
                if (Settings.canDrawOverlays(getActivity())) {
                    // continue here - permission was granted
                    doMinimize();


                }
            }
        }
    }

    private void refreshMyControlButton() {
        CallState callState = getCallState();
        if (CallState.CallState_Calling == callState) {

            boolean isLoudSpeaker = VoipMeetingController.getInstance().isLoudSpeakerStatus(mActivity);
            handleLoudSpeakerSelected(isLoudSpeaker);

            boolean isMute = getMySelf().mIsMute;
            handleSilenceSelected(isMute);

            boolean isOpenVideo = getMySelf().mIsVideoShared;
            handleVideoSelected(isOpenVideo);

            boolean isInviteMax = (AtworkConfig.VOIP_MEMBER_COUNT_MAX <= VoipMeetingController.getInstance().getVoipMemInMeetingList().size());
            handleInviteBtn(isInviteMax);

        } else if (CallState.CallState_Idle == callState
                || CallState.CallState_Init == callState
                || CallState.CallState_StartCall == callState
                || CallState.CallState_Waiting == callState) {
            // 控制区域设置不可见
            mGlCallingControlArea.setVisibility(View.GONE);
        } else {
            setControlAreaAvailable(false);
        }

    }


    /**
     * @brief 刷新显示时长的文本框
     */
    private void updateDurationText(long nTotalSeconds) {
        setVoipCallingStatusTip(VoipHelper.toCallDurationShow(nTotalSeconds));
    }

    public void clearData() {
        stopWaitNoAnswerHandler();
        stopWaitAnswerCountDown();
    }
}
