package com.foreveross.atwork.modules.voip.component;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.voip.CallState;
import com.foreveross.atwork.infrastructure.model.voip.PhoneState;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.manager.VoipMeetingController;
import com.foreveross.atwork.modules.voip.support.agora.interfaces.OnVoipStatusListener;

/**
 * @author zhen.liu
 * @brief 呼中的悬浮状态层, 系统顶层窗口，用来显示通话状态及视频画面
 * @date 2015/12/28
 */

public class WorkplusFloatCallView extends BaseVoipFloatCallView implements OnVoipStatusListener {

    private ImageView mIvFloat;
    private ImageView mIvSmallAudioStatus;
    private FrameLayout mFlSurfaceHome;
    private SurfaceView mSurfaceView;

    public WorkplusFloatCallView(Context context) {
        super(context);


        initView();

        VoipMeetingController.getInstance().setOnVoipStatusListener(this);

    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_workplus_float_call_video, this);
        mIvFloat = view.findViewById(R.id.iv_float);
        mIvSmallAudioStatus = view.findViewById(R.id.iv_small_audio_status);
        mFlSurfaceHome = view.findViewById(R.id.fl_surface_home);

        mSurfaceView =  VoipMeetingController.getInstance().createRendererView(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        mFlSurfaceHome.addView(mSurfaceView, params);

        refresh();

    }

    private void refresh() {
        boolean isVideoShow = false;
        VoipMeetingMember member = null;
        //自己打开视频的情况下才判断是否大屏的逻辑
        if (VoipMeetingController.getInstance().getMySelf().mIsVideoShared) {
            int currentBigVideoUid = VoipMeetingController.getInstance().getCurrentBigVideoUid();

            if(!VoipMeetingController.getInstance().isExistInCallList(currentBigVideoUid)) {
                currentBigVideoUid = VoipMeetingController.getInstance().getMySelf().getUid();
                VoipMeetingController.getInstance().saveShowingVideo(currentBigVideoUid);
            }


            if(-1 != currentBigVideoUid) {

                member = VoipMeetingController.getInstance().findMember(currentBigVideoUid);

                if(null != member && member.mIsVideoShared) {
                    isVideoShow = true;
                }
            }
        }


        if(isVideoShow) {

            if(member.mIsMute) {
                mIvSmallAudioStatus.setVisibility(VISIBLE);

            } else {
                mIvSmallAudioStatus.setVisibility(GONE);

            }
            mFlSurfaceHome.setVisibility(VISIBLE);
            mSurfaceView.setVisibility(VISIBLE);
            mIvFloat.setVisibility(GONE);


            VoipMeetingController.getInstance().setupVideoView(mSurfaceView, member.getUid());

        } else {
            mFlSurfaceHome.setVisibility(GONE);
            mIvSmallAudioStatus.setVisibility(GONE);
            mSurfaceView.setVisibility(GONE);

            mIvFloat.setVisibility(VISIBLE);

            if(VoipMeetingController.getInstance().isOtherMute()) {
                mIvFloat.setImageResource(R.mipmap.float_slience);

            } else {
                mIvFloat.setImageResource(R.mipmap.float_voice);

            }

        }


    }

    @Override
    public void onUsersProfileRefresh() {

        mHandler.post(()->{
            refresh();

        });
    }

    @Override
    public void onTipToast(String tip) {

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
        mHandler.post(() -> handleCallStateChanged(callState));

    }

    private void handleCallStateChanged(CallState callState) {
        switch (callState) {
            case CallState_Calling: {
            }
            break;
            case CallState_Waiting: {
            }
            break;
            case CallState_Disconnected: {
            }
            break;
            case CallState_ReConnecting: {
            }
            break;
            case CallState_Ended: {
                // 移除当前View
                mHandler.postDelayed(() -> {
                    try {
                        mWindowManager.removeView(WorkplusFloatCallView.this);
                    } catch (IllegalArgumentException e) {
                        LogUtil.e("agora", "IllegalArgumentException: view not attached to window manager");
                    }
                }, 1500);

            }
            break;

        }
    }

    @Override
    public void onCallingTimeElpased(long nSeconds) {

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
}