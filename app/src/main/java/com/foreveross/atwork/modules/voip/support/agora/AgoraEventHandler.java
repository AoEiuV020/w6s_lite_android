package com.foreveross.atwork.modules.voip.support.agora;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.voip.CallState;
import com.foreveross.atwork.infrastructure.model.voip.UserStatus;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.utils.ActivityManagerHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.manager.VoipMeetingController;
import com.foreveross.atwork.modules.voip.service.VoipEventService;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;

/**
 * Created by dasunsy on 2016/9/22.
 */

public class AgoraEventHandler extends IRtcEngineEventHandler {

    private int mLastRxBytes = 0;
    private int mLastTxBytes = 0;
    private int mLastDuration = 0;

    @Override
    public void onUserJoined(int uid, int elapsed) {
        LogUtil.e("agora", "uid : " + uid + " join");

        VoipMeetingMember member = VoipMeetingController.getInstance().findMember(uid);
        if(null != member) {
            VoipManager.getInstance().getTimeController().cancel(member.mUserId);
            VoipManager.getInstance().getOfflineController().cancel(uid);

            VoipManager.getInstance().getVoipMeetingController().setParticipantStatusAndRefreshUI(member, UserStatus.UserStatus_Joined);
            if(null != VoipMeetingController.getInstance().getVoipStatusListener()) {
                VoipMeetingController.getInstance().getVoipStatusListener().onUsersProfileRefresh();
            }
        }



    }

    /**
     * 标记着自己进入频道
     * */
    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        LogUtil.e("agora", "uid : " + uid + " self join success");

        VoipMeetingMember mySelf = VoipMeetingController.getInstance().getMySelf();
        mySelf.setUserStatus(UserStatus.UserStatus_Joined);

        VoipMeetingController.getInstance().setOnceConnected(true);
        VoipMeetingController.getInstance().changeCallState(CallState.CallState_Calling);

        if(null != VoipMeetingController.getInstance().getVoipStatusListener()) {
            VoipMeetingController.getInstance().getVoipStatusListener().onUsersProfileRefresh();
        }

    }

    @Override
    public void onUserOffline(int uid, int reason) {
        LogUtil.e("agora", "uid : " + uid + " leave   reason->" + reason);

        //对方用户主动退出时, 则判断是否需要挂断自己的通话
        if (VoipHelper.isHandlingVoipCall()) {

            if(Constants.USER_OFFLINE_QUIT == reason) {

                VoipMeetingMember meetingMember = VoipMeetingController.getInstance().findMember(uid);


                if (!VoipMeetingController.getInstance().isGroupChat()) {

                    if(null != meetingMember) {
                        if(!User.isYou(BaseApplicationLike.baseContext, meetingMember.getId())) {
                            VoipMeetingController.getInstance().stopCall();

                            VoipEventService.getInstance().enqueueLeavingMeeting(VoipMeetingController.getInstance().getWorkplusVoipMeetingId());

                        }
                    }
                } else {
                    if(null != meetingMember) {
                        VoipManager.getInstance().getVoipMeetingController().setParticipantStatusAndRefreshUI(meetingMember, UserStatus.UserStatus_Left);

                    }

                }

            } else if(Constants.USER_OFFLINE_DROPPED == reason) { //SDK 15s内没有任何数据会判断用户为离线
                if (!VoipMeetingController.getInstance().isGroupChat()) {
                    VoipManager.getInstance().getOfflineController().checkOfflineStatus(uid);
                }
            }
        }

    }

    /**
     * 标记自己的会议结束
     * */
    @Override
    public void onLeaveChannel(RtcStats stats) {
        LogUtil.e("agora", "self leave");

        ActivityManagerHelper.finishAll();
        //退会议成功, 则清除数据
        VoipMeetingController.getInstance().clearData();

    }


    @Override
    public void onUserMuteVideo(int uid, boolean muted) {
        LogUtil.e("agora", "uid : " + uid + " mute video");


    }

    /**
     * 其他用户开启/关闭视频功能
     * */
    @Override
    public void onUserEnableVideo(int uid, boolean enabled) {
        LogUtil.e("agora", "uid : " + uid + " enable video");

        VoipMeetingMember member = VoipMeetingController.getInstance().findMember(uid);
        if(null != member) {
            member.mIsVideoShared = enabled;

            if(null != VoipMeetingController.getInstance().getVoipStatusListener()) {
                VoipMeetingController.getInstance().getVoipStatusListener().onUsersProfileRefresh();
            }
        }


    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {
        LogUtil.e("agora", "uid : " + uid + " mute audio");

        VoipMeetingMember member = VoipMeetingController.getInstance().findMember(uid);
        if(null != member) {
            member.mIsMute = muted;

            if(null != VoipMeetingController.getInstance().getVoipStatusListener()) {
                VoipMeetingController.getInstance().getVoipStatusListener().onUsersProfileRefresh();
            }

            setVoipMemberJoined(uid,member);
        }

    }

    /**
     * （经测试，该回调不准：txQuality和rxQuality绝大部分都是1）
     * 通话中每个用户的网络上下行 last mile 质量报告回调。
     *
     * 该回调描述每个用户/主播在通话中的 last mile 网络状态，其中 last mile 是指设备到 Agora 边缘服务器的网络状态。该回调每 2 秒触发一次。如果远端有多个用户/主播，该回调每 2 秒会被触发多次。
     * @param uid 用户 ID。表示该回调报告的是持有该 ID 的用户的网络质量。当 uid 为 0 时，返回的是本地用户的网络质量。
     * @param txQuality
     * 该用户的上行网络质量，基于上行视频的发送码率、上行丢包率、平均往返时延和网络抖动计算。该值代表当前的上行网络质量，帮助判断是否可以支持当前设置的视频编码属性。假设直播模式下上行码率是 1000 Kbps，那么支持 640 × 480 的分辨率、30 fps 的帧率没有问题，但是支持 1280 x 720 的分辨率就会有困难。
     * QUALITY_UNKNOWN(0)：质量未知
     * QUALITY_EXCELLENT(1)：质量极好
     * QUALITY_GOOD(2)：用户主观感觉和极好差不多，但码率可能略低于极好
     * QUALITY_POOR(3)：用户主观感受有瑕疵但不影响沟通
     * QUALITY_BAD(4)：勉强能沟通但不顺畅
     * QUALITY_VBAD(5)：网络质量非常差，基本不能沟通
     * QUALITY_DOWN(6)：网络连接断开，完全无法沟通
     *
     * @param rxQuality
     * 该用户的下行网络质量，基于下行网络的丢包率、平均往返延时和网络抖动计算。
     * QUALITY_UNKNOWN(0)：质量未知
     * QUALITY_EXCELLENT(1)：质量极好
     * QUALITY_GOOD(2)：用户主观感觉和极好差不多，但码率可能略低于极好
     * QUALITY_POOR(3)：用户主观感受有瑕疵但不影响沟通
     * QUALITY_BAD(4)：勉强能沟通但不顺畅
     * QUALITY_VBAD(5)：网络质量非常差，基本不能沟通
     * QUALITY_DOWN(6)：网络连接断开，完全无法沟通
     */
    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        super.onNetworkQuality(uid, txQuality, rxQuality);
//        LogUtil.e(TAG,String.format("onNetworkQuality:uid:%d:txQuality:%d:rxQuality:%d",uid,txQuality,rxQuality));


        VoipMeetingMember meetingMember = null;
        //声网在该回调里uid 为0时代表自己
        if(0 == uid) {
            meetingMember = VoipMeetingController.getInstance().getMySelf();
        } else {
            meetingMember = VoipMeetingController.getInstance().findMember(uid);
        }

        if(null != meetingMember) {
            //boolean isGoodNet = (txQuality == 1 || txQuality == 2 || txQuality == 3) || (rxQuality == 1 || rxQuality == 2 || rxQuality == 3);
            boolean isWeakNet = (txQuality == 0 && rxQuality == 0) ||
                    (txQuality == 4 || txQuality == 5 || txQuality == 6) ||
                    (rxQuality == 4 || rxQuality == 5 || rxQuality == 6);

//            notifyMemberWeakNetStatus(meetingMember, isWeakNet);

            setVoipMemberJoined(uid,meetingMember);
        }
    }

    /**
     * 提示第一帧本地视频画面已经显示在屏幕上
     * */
    @Override
    public void onFirstLocalVideoFrame(int width, int height, int elapsed) {
        LogUtil.e("agora", " on first local video frame");

    }

    /**
     * 第一帧远程视频显示在视图上时，触发此调用
     * */
    @Override
    public void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed) {
        LogUtil.e("agora", "uid : " + uid + " on first remote video frame");

    }

    /**
     * 监听正在说话的人
     * */
    @Override
    public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
        List<VoipMeetingMember> speakingMembersNeedRefreshUI = new ArrayList<>();
        for(int i = 0; i < speakers.length; i++) {
            VoipMeetingMember member;
            //声网在该回调里uid 为0时代表自己
            if(0 == speakers[i].uid) {
                member = VoipMeetingController.getInstance().getMySelf();
            } else {
                member = VoipMeetingController.getInstance().findMember(speakers[i].uid);

            }

            if(null != member && speakers[i].volume > 0) {
                LogUtil.e("agora", member.mShowName +  " is speaking  volumn : " + speakers[i].volume);

                VoipMeetingController.getInstance().getSpeakingMonitor().refreshTimer(member.mUserId);

                //从没说话->说话的状态变化, 需要马上刷新
                if(!member.mIsSpeaking) {
                    speakingMembersNeedRefreshUI.add(member);
                    member.mIsSpeaking = true;
                }

                setVoipMemberJoined(speakers[i].uid, member);


            }
        }

        if(!ListUtil.isEmpty(speakingMembersNeedRefreshUI)) {
            if(null != VoipMeetingController.getInstance().getVoipStatusListener()) {
                VoipMeetingController.getInstance().getVoipStatusListener().onUsersProfileRefresh();
            }
        }



    }

    /**
     * 声网统计数据回调, 每两秒触发一次
     * */
    @Override
    public void onRtcStats(RtcStats stats) {
        String rateFlow = ((stats.txBytes + stats.rxBytes - mLastTxBytes - mLastRxBytes) / 1024 / (stats.totalDuration - mLastDuration + 1)) + "KB/s";

        LogUtil.e("agora", "rateFlow -> " + rateFlow);

        // remember data from this call back
        mLastRxBytes = stats.rxBytes;
        mLastTxBytes = stats.txBytes;
        mLastDuration = stats.totalDuration;


    }

    @Override
    public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
        LogUtil.e("agora", "onRejoinChannelSuccess");

        VoipMeetingController.getInstance().changeCallState(CallState.CallState_Calling);

    }

    /**
     *
     * 该回调方法表示 SDK 和服务器失去了网络连接，并且尝试自动重连一段时间(默认 10 秒)
     * 后仍未连上。该回调触发后，SDK 仍然会尝试重连，重连成功后会触发 onRejoinChannelSuccess
     * 回调。
     * */
    @Override
    public void onConnectionLost() {
        LogUtil.e("agora", "onConnectionLost");

    }

    /**
     * 该回调方法表示 SDK 和服务器失去了网络连接。与 onConnectionLost 回调的区别是:
     * onConnectionInterrupted 回调在 SDK 刚失去和服务器连接时触发，onConnectionLost
     * 在失 去连接且尝试自动重连失败后才触发。失去连接后，除非 App 主动调用 leaveChannel，
     * SDK 会一直自动重连。
     * */
    @Override
    public void onConnectionInterrupted() {
        LogUtil.e("agora", "onConnectionInterrupted");

        VoipMeetingController.getInstance().changeCallState(CallState.CallState_Disconnected);


    }

    @Override
    public void onError(int err) {
        LogUtil.e("agora", "error " + err);
    }

    @Override
    public void onWarning(int warn) {
        LogUtil.e("agora", "warn " + warn);

        if(WarnCode.WARN_LOOKUP_CHANNEL_TIMEOUT == warn || WarnCode.WARN_OPEN_CHANNEL_TIMEOUT == warn) {
            VoipMeetingController.getInstance().changeCallState(CallState.CallState_ReConnecting);
        }

    }


    private void setVoipMemberJoined(int uid, VoipMeetingMember meetingMember) {
        if (0 != uid && meetingMember.getUserStatus() != UserStatus.UserStatus_Joined) {
            VoipManager.getInstance().getOfflineController().cancel(uid);
            meetingMember.setUserStatus(UserStatus.UserStatus_Joined);
            if (null != VoipMeetingController.getInstance().getVoipStatusListener()) {
                //解决有时候人员离线回调不准确引起的在线人数显示不正确的问题
                //增加mLeaveFromIM状态是为了防止当收到IM离开会议的推送比声网的该回调要快的情况下，界面频繁刷新的问题。
                VoipManager.getInstance().getVoipMeetingController().setParticipantStatusAndRefreshUI(meetingMember, UserStatus.UserStatus_Joined);
            }
        }
    }
}
