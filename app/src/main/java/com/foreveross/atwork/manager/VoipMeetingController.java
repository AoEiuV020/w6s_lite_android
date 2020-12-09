package com.foreveross.atwork.manager;

/**
 * Created by dasunsy on 2016/9/30.
 */

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.voip.responseJson.CreateOrQueryMeetingResponseJson;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.VoipControllerStrategy;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.organizationSetting.OrganizationSettings;
import com.foreveross.atwork.infrastructure.model.organizationSetting.VasSettings;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.voip.CallParams;
import com.foreveross.atwork.infrastructure.model.voip.CallState;
import com.foreveross.atwork.infrastructure.model.voip.CurrentVoipMeeting;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.UserStatus;
import com.foreveross.atwork.infrastructure.model.voip.UserType;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ActivityManagerHelper;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.AudioUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.voip.component.agora.MeetingVideoModeItemView;
import com.foreveross.atwork.modules.voip.component.agora.MeetingVideoModeMainBigView;
import com.foreveross.atwork.modules.voip.service.SpeakingMonitor;
import com.foreveross.atwork.modules.voip.support.agora.AgoraEventHandler;
import com.foreveross.atwork.modules.voip.support.agora.interfaces.OnControllerVoipListener;
import com.foreveross.atwork.modules.voip.support.agora.interfaces.OnVoipStatusListener;
import com.foreveross.atwork.modules.voip.support.qsy.utils.VibratorUtil;
import com.foreveross.atwork.modules.voip.utils.SoundHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.agora.rtc.RtcEngine;
import io.agora.rtc.internal.RtcEngineImpl;
import io.agora.rtc.video.VideoCanvas;
import kotlin.collections.CollectionsKt;

/**
 * 语音会议控制类, 维护会议中的人是否发音等状态
 */
public class VoipMeetingController implements VoipControllerStrategy {

    private static Object sLock = new Object();

    public static VoipMeetingController sInstance = null;

    private OnControllerVoipListener mOnControllerVoipListener = null;
    private OnVoipStatusListener mOnVoipStatusListener = null;

    private CurrentVoipMeeting mCurrentVoipMeeting = null;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private RtcEngine mRtcEngine;

    private AgoraEventHandler mEventHandler = new AgoraEventHandler();

    private ScheduledExecutorService mCallingDurationService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture mCallingDurationFuture;

    private SpeakingMonitor mSpeakingMonitor = new SpeakingMonitor();

    private String appId;

    /**
     * 通话时长
     */
    private long mElapsedCallingTime = 0;

    /**
     * 开始通话的时间
     */
    private long mStartTimeMillis = 0;

    /**
     * 是否曾经重连成功
     * */
    private boolean mOnceConnected = false;


    public static VoipMeetingController getInstance() {
        /**
         * double check
         * */
        if (null == sInstance) {
            synchronized (sLock) {
                if (null == sInstance) {
                    sInstance = new VoipMeetingController();
                }

            }
        }

        return sInstance;

    }

    public void init(Context context) {

        try {
            String orgCode = PersonalShareInfo.getInstance().getCurrentOrg(BaseApplicationLike.baseContext);
            OrganizationSettings organizationSetting = OrganizationSettingsManager.getInstance().getCurrentUserOrgSetting(orgCode);
            VasSettings agoraVasSetting = organizationSetting.getAgoraSetting();
            if(agoraVasSetting != null){
                String appId = agoraVasSetting.getValue(VasSettings.AGORA_APP_ID);

                LogUtil.e("key", "key -> " +appId + "  version -> " + RtcEngine.getSdkVersion());
                mRtcEngine = RtcEngine.create(context, appId, mEventHandler);
                mRtcEngine.setLogFile(AtWorkDirUtils.getInstance().getAgoraVoipLOG());

                //监听说话者信息, 500ms 监听一次
                mRtcEngine.enableAudioVolumeIndication(500, 3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SpeakingMonitor getSpeakingMonitor() {
        return mSpeakingMonitor;
    }

    public long getCallingTime() {
        return mElapsedCallingTime;
    }

    private void startCountDuration() {
        stopCountDuration();

        mStartTimeMillis = System.currentTimeMillis();
        mElapsedCallingTime = 0;

        mCallingDurationFuture = mCallingDurationService.scheduleAtFixedRate(() -> {
            if (null != getVoipStatusListener()) {

                LogUtil.e("agora", "time counting ->" + mElapsedCallingTime);


                VoipNoticeManager.getInstance().clear(VoipNoticeManager.ID_VOIP_CALLING);
                if(BaseApplicationLike.sIsHomeStatus) {
                    VoipNoticeManager.getInstance().callingNotificationShow(BaseApplicationLike.baseContext, mElapsedCallingTime);
                }

                CurrentVoipMeeting currentVoipMeeting = VoipMeetingController.getInstance().getCurrentVoipMeeting();

                if(null != currentVoipMeeting) {
                    currentVoipMeeting.mCallDuration = mElapsedCallingTime;
                }


                getVoipStatusListener().onCallingTimeElpased(mElapsedCallingTime);

                mElapsedCallingTime += 1;

            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

    }

    private void stopCountDuration() {
        if (null != mCallingDurationFuture) {
            mCallingDurationFuture.cancel(true);
            mCallingDurationFuture = null;
        }
    }

    public boolean haveVideoNeedRestore() {
        return isCurrentVoipMeetingValid() && -1 != mCurrentVoipMeeting.mCurrentBigVideoUid;
    }



    /**
     * 加入频道
     */
    @Override
    public void startCallByJoinKey(String workplusVoipMeetingId, String token) {
        if (null != mRtcEngine) {
            int uid = getMySelf().getUid();

            //只能CallState_StartCall -> CallState_Calling, 不能逆转, 不能CallState_Calling -> 只能CallState_StartCall
            if (CallState.CallState_Calling != VoipManager.getInstance().getCallState()) {
                VoipMeetingController.getInstance().changeCallState(CallState.CallState_StartCall);
            }

            if (AtworkConfig.OPEN_VOIP_ENCRYPTION) {
                int result = mRtcEngine.setEncryptionSecret(workplusVoipMeetingId);
                LogUtil.e("key", "voip setEncryptionSecret result -> " + (0 == result));
            }

            mRtcEngine.joinChannel(null, workplusVoipMeetingId, "", uid);

            LogUtil.e("key", "meeting id -> " + workplusVoipMeetingId);
            LogUtil.e("key", "uid -> " + uid);


        }
    }

    @Override
    public boolean isVideoCallOpened() {

        return isCurrentVoipMeetingValid() && getMySelf().mIsVideoShared;
    }

    @Override
    public void saveShowingVideo(int currentBigViewUid) {
        if(isCurrentVoipMeetingValid()) {
            mCurrentVoipMeeting.mCurrentBigVideoUid = currentBigViewUid;
        }

    }


    public int getCurrentBigVideoUid() {
        if(isCurrentVoipMeetingValid()) {

            if(-1 == getCurrentVoipMeeting().mCurrentBigVideoUid) {
                getCurrentVoipMeeting().mCurrentBigVideoUid = getMySelf().getUid();

            }

            return getCurrentVoipMeeting().mCurrentBigVideoUid;
        }

        return -1;
    }


    public void releaseAgora() {
        mRtcEngine = null;
    }

    public void startTest() {
        if (null != mRtcEngine) {
            mRtcEngine.startEchoTest();
        }
    }

    public void stopTest() {
        if (null != mRtcEngine) {
            mRtcEngine.stopEchoTest();
        }
    }

    @Override
    public boolean isCurrentVoipMeetingValid() {
        return null != mCurrentVoipMeeting;
    }

    public CurrentVoipMeeting getCurrentVoipMeeting() {
        return mCurrentVoipMeeting;
    }

    public void setCurrentVoipMeeting(String workplusVoipMeetingId, MeetingInfo meetingInfo, VoipType voipType, CallParams callParams) {

        clearData();

        mCurrentVoipMeeting = new CurrentVoipMeeting();

        mCurrentVoipMeeting.mWorkplusVoipMeetingId = workplusVoipMeetingId;
        mCurrentVoipMeeting.mMeetingInfo = meetingInfo;
        mCurrentVoipMeeting.mVoipType = voipType;
        mCurrentVoipMeeting.mCallParams = callParams;

        if (callParams.isGroupChat()) {
            this.mCurrentVoipMeeting.mCallSelf = callParams.mMySelf;
            this.mCurrentVoipMeeting.mMeetingGroup = callParams.mGroup;

        } else {
            this.mCurrentVoipMeeting.mCallSelf = callParams.mMySelf;
            this.mCurrentVoipMeeting.mCallPeer = callParams.mPeer;

        }

    }

    public void setCurrentVoipMeetingId(String workplusVoipMeetingId) {
        if (isCurrentVoipMeetingValid()) {
            mCurrentVoipMeeting.mWorkplusVoipMeetingId = workplusVoipMeetingId;
        }
    }


    public void setCurrentVoipMeetingDomainId(String workplusVoipMeetingDomainId) {
        if (isCurrentVoipMeetingValid()) {
            mCurrentVoipMeeting.mWorkplusVoipMeetingDomainId = workplusVoipMeetingDomainId;
        }
    }


    public void setOnControllerVoipListener(OnControllerVoipListener onControllerVoipListener) {
        this.mOnControllerVoipListener = onControllerVoipListener;
    }

    public void setOnVoipStatusListener(OnVoipStatusListener onVoipStatusListener) {
        this.mOnVoipStatusListener = onVoipStatusListener;
    }

    public OnControllerVoipListener getControllerVoipListener() {
        return this.mOnControllerVoipListener;
    }

    public OnVoipStatusListener getVoipStatusListener() {
        return this.mOnVoipStatusListener;
    }


    @Override
    public String getWorkplusVoipMeetingId() {
        if(isCurrentVoipMeetingValid()) {
            return mCurrentVoipMeeting.mWorkplusVoipMeetingId;

        }

        return StringUtils.EMPTY;
    }

    @Override
    public void addParticipants(ArrayList<VoipMeetingMember> memberArray) {
        if (isCurrentVoipMeetingValid()) {
            for (VoipMeetingMember meetingMember : memberArray) {
                meetingMember.setUserStatus(UserStatus.UserStatus_NotJoined);

                mCurrentVoipMeeting.mMeetingGroup.mParticipantList.remove(meetingMember);
            }

            mCurrentVoipMeeting.mMeetingGroup.mParticipantList.addAll(memberArray);

            if (null != getVoipStatusListener()) {
                getVoipStatusListener().onUsersProfileRefresh();
            }

        }
    }

    @Override
    public void switchToGroup(VoipMeetingGroup group) {
        if (isCurrentVoipMeetingValid()) {
            mCurrentVoipMeeting.mMeetingGroup = group;

            if (null != getVoipStatusListener()) {
                getVoipStatusListener().onUsersProfileRefresh();
            }
        }
    }

    /**
     * 单聊模型转群语音
     */
    @Override
    public VoipMeetingGroup transfer2Group() {
        VoipMeetingGroup voipMeetingGroup = new VoipMeetingGroup();
        voipMeetingGroup.mParticipantList = new CopyOnWriteArrayList<>();
        voipMeetingGroup.mParticipantList.add(getMySelf());
        voipMeetingGroup.mParticipantList.add(getPeer());

        mCurrentVoipMeeting.mMeetingGroup = voipMeetingGroup;

        return mCurrentVoipMeeting.mMeetingGroup;
    }

    @Override
    public void stopCall() {
        stopCountDuration();

        changeCallState(CallState.CallState_Ending);
        changeCallState(CallState.CallState_Ended);
        if (null != mRtcEngine) {
            mRtcEngine.leaveChannel();
        }

        if (isGroupChat()) {
            VoipManager.getInstance().getTimeController().cancelAll();
            getSpeakingMonitor().cancelAll();
        }



        VoipManager.getInstance().voipStopHeartBeat();
        VoipManager.getInstance().getOfflineController().cancelAll();

        VoipNoticeManager.getInstance().callingNotificationCancel(BaseApplicationLike.baseContext);

    }

    /**
     * 挂断电话
     */
    @Override
    public void finishCall() {
        if(!isCurrentVoipMeetingValid()) {
            return;
        }

        if (UserType.Originator == getMySelf().getUserType()) {
            // 发送方点击挂断按钮，结束呼叫
            CallState callState = VoipManager.getInstance().getCallState();
            VoipMeetingController.getInstance().stopCall();

            //notify APP cancel call, reject call, finish call
            if (CallState.CallState_Idle == callState ||
                    CallState.CallState_Init == callState) {
                if (UserType.Originator == getMySelf().getUserType()) {

                    if (null != getControllerVoipListener()) {
                        getControllerVoipListener().onCancelCall();
                    }

                }
            } else {
                notifyFinishCall(-1);
            }

        } else if (UserType.Recipient == getMySelf().getUserType()) {
            // 接收方点击挂断按钮
            CallState callState = VoipManager.getInstance().getCallState();
            VoipMeetingController.getInstance().stopCall();


            if (CallState.CallState_Idle == callState ||
                    CallState.CallState_Init == callState) {

                // 若会还没有启动，则拒绝呼叫
                if (null != getControllerVoipListener()) {
                    getControllerVoipListener().onRejectCall();
                }
            } else {
                // 会已启动，则结束呼叫
                notifyFinishCall(-1);
            }
        } else {
            VoipMeetingController.getInstance().stopCall();

        }


    }

    /**
     * 声网接口不提供当前频道人数
     */
    private void notifyFinishCall(int meetingMemberCount) {
        if (getControllerVoipListener() != null) {
            ActivityManagerHelper.finishAll(); // finish all of call relate activity

            getControllerVoipListener().onFinishCall(meetingMemberCount);
            setOnControllerVoipListener(null); // avoid duplicate call onFinishCall
        }
    }




    public boolean enableLoudSpeaker(boolean isEnable) {
        if (null != mRtcEngine) {
            return 0 == mRtcEngine.setEnableSpeakerphone(isEnable);
        }


        return false;
    }

    /**
     * 是否切换成功
     */
    public boolean isLoudSpeakerStatus(Context context) {
        if (null != mRtcEngine) {
            return mRtcEngine.isSpeakerphoneEnabled();

        }

        return AudioUtil.isSpeakModel(context);
    }


    public void muteAll(boolean isMute) {
        muteSelf(isMute);
        muteRemoteAll(isMute);
    }

    public boolean muteSelf(boolean isMute) {
        if (null != mRtcEngine) {
            if (0 == mRtcEngine.muteLocalAudioStream(isMute)) {
                getMySelf().mIsMute = isMute;
                return true;
            }

        }
        return false;
    }

    public boolean muteRemoteAll(boolean isMute) {
        if (null != mRtcEngine) {
            if (0 == mRtcEngine.muteAllRemoteAudioStreams(isMute)) {
                return true;
            }

        }

        return false;
    }

    /**
     * 开启视频, 关闭视频
     */
    public boolean enableVideo(boolean enable) {
        boolean result = false;
        if (null != mRtcEngine) {
            if (enable) {
                result = (0 == mRtcEngine.enableVideo());

            } else {
                result = (0 == mRtcEngine.disableVideo());
            }
        }

        if (result && null != getMySelf()) {
            getMySelf().mIsVideoShared = enable;
        }

        return result;
    }

    @Nullable
    public SurfaceView createRendererView(Context context) {
        SurfaceView surfaceView = null;
        if(null != mRtcEngine) {
            surfaceView = RtcEngine.CreateRendererView(context);
        }

        return surfaceView;
    }

    public boolean setupVideoView(SurfaceView surfaceView, int uid) {
        if(getMySelf().getUid() == uid) {
            return setupLocalVideo(surfaceView);
        } else {
            return setupRemoteVideo(surfaceView, uid);
        }
    }

    private boolean setupLocalVideo(SurfaceView surfaceView) {
        boolean result = false;
        if (null != mRtcEngine) {
            result = (0 == mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView)));
        }

        return result;
    }

    private boolean setupRemoteVideo(SurfaceView surfaceView, int uid) {
        boolean result = false;
        if (null != mRtcEngine) {
            result = (0 == mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid)));
        }

        return result;
    }

    public boolean removeVideoView(int uid) {
        if(getMySelf().getUid() == uid) {
            return removeLocalVideo();
        } else {
            return removeRemoteVideo(uid);
        }

    }

    private boolean removeRemoteVideo(int uid) {
        boolean result = false;
        if (null != mRtcEngine) {
            result = (0 == mRtcEngine.setupRemoteVideo(new VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid)));
        }

        return result;
    }

    private boolean removeLocalVideo() {
        boolean result = false;
        if (null != mRtcEngine) {
            result = (0 == mRtcEngine.setupLocalVideo(new VideoCanvas(null)));
        }

        return result;
    }

    public void switchVideoView(MeetingVideoModeMainBigView meetingBigView, MeetingVideoModeItemView meetingSmallView) {

        int currentBigVideoUid = getCurrentBigVideoUid();
        int smallViewUid = meetingSmallView.mBindingMember.getUid();

        //do the switch work
        removeVideoView(currentBigVideoUid);
        removeVideoView(smallViewUid);

        saveShowingVideo(smallViewUid);

        meetingBigView.setTag(smallViewUid);
        meetingBigView.bindVideoView(meetingSmallView.mBindingMember);
        meetingSmallView.setTag(currentBigVideoUid);
        meetingSmallView.bindVideoView(findMember(currentBigVideoUid));


        meetingBigView.refresh();
        meetingSmallView.refresh();

    }

    public boolean switchFrontCamera() {
        boolean result = false;
        if (null != mRtcEngine && mRtcEngine instanceof RtcEngineImpl) {
            result = (0 == ((RtcEngineImpl)mRtcEngine).setVideoCamera(1));
        }

        return result;
    }

    public boolean switchCamera() {
        boolean result = false;
        if (null != mRtcEngine) {
            result = (0 == mRtcEngine.switchCamera());
        }

        return result;
    }

    public void switchVideoView(int uid, int otherUid) {
        if(null != mRtcEngine) {
            mRtcEngine.switchView(uid, otherUid);
        }

    }


    public void changeCallState(CallState callState) {
        CallState oldCallState = VoipManager.getInstance().getCallState();

        if (CallState.CallState_ReConnecting == callState || callState != oldCallState) {
            VoipManager.getInstance().setCallState(callState);

            if (null != getVoipStatusListener()) {
                getVoipStatusListener().onCallStateChanged(callState);
            }

            switch (callState) {
                case CallState_Idle:
                    break;
                case CallState_Init:
                    if (isGroupChat() && UserType.Originator == getMySelf().getUserType()) {

                    } else {
                        SoundHelper.getInstance().play(BaseApplicationLike.baseContext);
                    }
                    break;
                case CallState_StartCall:
                    break;
                case CallState_Waiting:
                    break;
                case CallState_Calling:
                    //CallState_ReConnecting->CallState_Calling 或者 CallState_Disconnected->CallState_Calling不需要重置时间
                    if(CallState.CallState_ReConnecting != oldCallState && CallState.CallState_Disconnected != oldCallState) {
                        startCountDuration();
                    }

                    if (UserType.Originator == getMySelf().getUserType()) {
                        VibratorUtil.Vibrate(BaseApplicationLike.baseContext, 100);

                    }

                    SoundHelper.getInstance().stop();

                    break;
                case CallState_Disconnected:
                    break;
                case CallState_ReConnecting:
                    break;
                case CallState_Ended:
                    SoundHelper.getInstance().release();

                    break;
                default:
                    break;
            }
        }

    }


    public void initPeerCall() {

        refreshData();


//        this.mCurrentVoipMeeting.mCallSelf = mySelfEntity;
//        this.mCurrentVoipMeeting.mCallPeer = peerEntity;

        changeCallState(CallState.CallState_Init);

        if (null != getControllerVoipListener()) {
            if (UserType.Originator == getMySelf().getUserType()) {
                getControllerVoipListener().onStartVoipMeeting();
            }
        }

    }

    public void initGroupCall() {

        refreshData();

//        this.mCurrentVoipMeeting.mCallSelf = mySelfEntity;
//        this.mCurrentVoipMeeting.mMeetingGroup = group;

        changeCallState(CallState.CallState_Init);

        if (null != getControllerVoipListener()) {

            if (UserType.Originator == getMySelf().getUserType()) {
                getControllerVoipListener().onStartVoipMeeting();
            }
        }
    }


    /**
     * 是否用户存在会议成员列表中
     * */
    public boolean isExist(int uid) {
        return null != findMember(uid);
    }

    /**
     * 是否用户存在会议中(已经进入会议)
     * */
    public boolean isExistInCallList(int uid) {
        VoipMeetingMember meetingMember = findMember(uid);
        if(null != meetingMember) {
            return UserStatus.UserStatus_Joined == meetingMember.getUserStatus();
        }

        return false;
    }



    /**
     * 通过 uid 找到对应的用户
     */
    @Nullable
    public VoipMeetingMember findMember(int uid) {
        if (isCurrentVoipMeetingValid()) {
            if (isGroupChat()) {
                for (VoipMeetingMember member : mCurrentVoipMeeting.mMeetingGroup.mParticipantList) {
                    if (uid == member.getUid()) {
                        return member;
                    }
                }

            } else {
                if (uid == getMySelf().getUid()) {
                    return getMySelf();
                }

                if (uid == getPeer().getUid()) {
                    return getPeer();
                }
            }

        }

        return null;
    }


    /**
     * 通过 userId 找到对应的用户
     */
    @Nullable
    public VoipMeetingMember findMember(String userId) {
        if (isCurrentVoipMeetingValid()) {
            if (isGroupChat()) {
                for (VoipMeetingMember member : mCurrentVoipMeeting.mMeetingGroup.mParticipantList) {
                    if (userId.equals(member.getId())) {
                        return member;
                    }
                }

            } else {
                if (userId.equals(getMySelf().getId())) {
                    return getMySelf();
                }

                if (userId.equals(getPeer().getId())) {
                    return getPeer();
                }
            }

        }

        return null;

    }

    /**
     * 是否其他人开启了视频
     */
    public boolean isOtherEnableVideo() {
        boolean isOtherEnableVideo = false;
        List<VoipMeetingMember> memberList = getVoipMemInMeetingList();
        for (VoipMeetingMember member : memberList) {
            if (!User.isYou(BaseApplicationLike.baseContext, member.getId()) && member.mIsVideoShared) {
                isOtherEnableVideo = true;
                break;
            }
        }

        return isOtherEnableVideo;

    }

    /**
     * 会议里其他所有人(除了自己)是否静音状态
     * */
    public boolean isOtherMute() {
        boolean isOtherMute = true;
        List<VoipMeetingMember> memberList = getVoipMemInCallList();
        for (VoipMeetingMember member : memberList) {
            if (!User.isYou(BaseApplicationLike.baseContext, member.getId()) && !member.mIsMute) {
                isOtherMute = false;
                break;
            }
        }

        return isOtherMute;
    }

    /**
     * 返回正在会议中的人, 不包括离开, 或者拒绝的人
     */
    @NonNull
    @Override
    public List<VoipMeetingMember> getVoipMemInMeetingList() {
        List<VoipMeetingMember> memberInMeetingList = new ArrayList<>();
        List<VoipMeetingMember> totalMemberList = getMemberList();
        for (VoipMeetingMember member : totalMemberList) {
            if (UserStatus.UserStatus_Joined.equals(member.getUserStatus()) || UserStatus.UserStatus_NotJoined.equals(member.getUserStatus())) {
                memberInMeetingList.add(member);

            }
        }

        return memberInMeetingList;
    }

    /**
     * 返回正在通话中的人, 不包括离开, 拒绝, 待加入等
     * */
    @NonNull
    public List<VoipMeetingMember> getVoipMemInCallList() {
        List<VoipMeetingMember> memberInMeetingList = new ArrayList<>();
        List<VoipMeetingMember> totalMemberList = getMemberList();
        for (VoipMeetingMember member : totalMemberList) {
            if (UserStatus.UserStatus_Joined.equals(member.getUserStatus())) {
                memberInMeetingList.add(member);

            }
        }

        return memberInMeetingList;
    }

    @NonNull
    public List<VoipMeetingMember> getMemberList() {
        List<VoipMeetingMember> memberList = new ArrayList<>();

        if (isGroupChat()) {
            memberList.addAll(getCurrentVoipMeeting().mMeetingGroup.mParticipantList);
        } else {
            memberList.add(getMySelf());
            memberList.add(getPeer());
        }

        return memberList;
    }

    public VoipMeetingMember getMySelf() {
        if(isCurrentVoipMeetingValid()) {
            return mCurrentVoipMeeting.mCallSelf;
        }

        return null;
    }

    public VoipMeetingMember getPeer() {
        if(isCurrentVoipMeetingValid()) {
            return mCurrentVoipMeeting.mCallPeer;
        }

        return null;
    }

    /**
     * 刷新{@link #mCurrentVoipMeeting}里 member 相关的 uid
     */
    public void refreshCurrentMeetingMembersUid(List<VoipMeetingMember> memberListHavingUid) {
        if (isCurrentVoipMeetingValid()) {
            CallParams callParams = mCurrentVoipMeeting.mCallParams;

            if (null != callParams.mPeer) {
                refreshMemberUid(memberListHavingUid, callParams.mPeer);

            }

            if (null != callParams.mMySelf) {
                refreshMemberUid(memberListHavingUid, callParams.mMySelf);
            }


            if (null != callParams.mGroup) {
                for (VoipMeetingMember meetingMember : callParams.mGroup.mParticipantList) {
                    refreshMemberUid(memberListHavingUid, meetingMember);
                }
            }


        }
    }

    private void refreshMemberUid(List<VoipMeetingMember> memberListHavingUid, VoipMeetingMember meetingMember) {
        int index = memberListHavingUid.indexOf(meetingMember);

        if (-1 != index) {
            meetingMember.mGateWay = memberListHavingUid.get(index).mGateWay;
        }
    }


    public VoipMeetingGroup getGroup() {
        return mCurrentVoipMeeting.mMeetingGroup;
    }

    @Override
    public boolean isGroupChat() {
        return isCurrentVoipMeetingValid() && null != mCurrentVoipMeeting.mMeetingGroup;
    }

    private boolean mIsFirstCallHeartBeat = true;

    public void calibrateStatusAndRefreshUI(CreateOrQueryMeetingResponseJson responseJson) {

        if (isCurrentVoipMeetingValid()) {


            //检查是否自己处于异常情况
            if (!mIsFirstCallHeartBeat) {

                List<VoipMeetingMember> memberSnapshotList = responseJson.toParticipantList();

                for(VoipMeetingMember memberSnapshot: memberSnapshotList) {
                    if(User.isYou(AtworkApplicationLike.baseContext, memberSnapshot.mUserId) && UserStatus.UserStatus_Joined != memberSnapshot.getUserStatus()) {
                        tipToast(AtworkApplicationLike.getResourceString(R.string.error_happened));
                        stopCall();
                        return;
                    }
                }
            }

            mIsFirstCallHeartBeat = false;


            List<VoipMeetingMember> memberInMeetingList = calibrateMemberList(responseJson);


            calibrateMemberStatusAndRefreshUI(responseJson, memberInMeetingList);


            if (null != VoipMeetingController.getInstance().getVoipStatusListener()) {
                VoipMeetingController.getInstance().getVoipStatusListener().onUsersProfileRefresh();
            }
        }
    }

    @NonNull
    private List<VoipMeetingMember> calibrateMemberList(CreateOrQueryMeetingResponseJson responseJson) {
        List<VoipMeetingMember> memberInMeetingList = new ArrayList<>();
        if(isGroupChat()) {
            memberInMeetingList.addAll(mCurrentVoipMeeting.mMeetingGroup.mParticipantList);

        } else {
            memberInMeetingList.add(getMySelf());
            memberInMeetingList.add(getPeer());
        }

        List<VoipMeetingMember> memberSnapshotList = responseJson.toParticipantList();

        //修正参会人员数据
        ArrayList<VoipMeetingMember> memberInsertList = new ArrayList<>(CollectionsKt.filter(memberSnapshotList, voipMeetingMemberSnapShot -> UserStatus.UserStatus_Joined == voipMeetingMemberSnapShot.getUserStatus() && !memberInMeetingList.contains(voipMeetingMemberSnapShot)));
        if(!ListUtil.isEmpty(memberInsertList)) {
            addParticipants(memberInsertList);
            memberInMeetingList.addAll(memberInsertList);
        }
        return memberInMeetingList;
    }

    private void calibrateMemberStatusAndRefreshUI(CreateOrQueryMeetingResponseJson responseJson, List<VoipMeetingMember> memberInMeetingList) {



        for (VoipMeetingMember memberInMeeting : memberInMeetingList) {

            List<VoipMeetingMember> memberSnapshotList = responseJson.toParticipantList();

            if (!ListUtil.isEmpty(memberSnapshotList)) {


                for(VoipMeetingMember memberSnapshot: memberSnapshotList) {
                    if(memberSnapshot.mUserId.equalsIgnoreCase(memberInMeeting.mUserId)) {

                        if (!User.isYou(AtworkApplicationLike.baseContext, memberSnapshot.mUserId)) {
                            if(memberSnapshot.isAlive()) {
                                memberInMeeting.setUserStatus(UserStatus.UserStatus_Joined);

                            } else {
                                if (UserStatus.UserStatus_NotJoined != memberInMeeting.getUserStatus()) {
                                    memberInMeeting.setUserStatus(UserStatus.UserStatus_Left);
                                }

                            }
                        }


                        if(null != memberSnapshot.isMutedRemote() ) {
                            boolean changed = (memberInMeeting.mIsMute!= memberSnapshot.isMutedRemote());
                            memberInMeeting.mIsMute = memberSnapshot.isMutedRemote();

                            if(User.isYou(AtworkApplicationLike.baseContext, memberInMeeting.mUserId) && changed) {
                                muteSelf(memberSnapshot.isMutedRemote());
                            }
                        }



                        break ;
                    }
                }
            }


        }
    }


    @Override
    public void setParticipantStatusAndRefreshUI(@NonNull VoipMeetingMember voipMeetingMember, UserStatus userStatus) {
        if (isCurrentVoipMeetingValid()) {

            VoipMeetingMember memberFound = findMember(voipMeetingMember.getId());
            if (null != memberFound) {
                memberFound.setUserStatus(userStatus);

                if (null != VoipMeetingController.getInstance().getVoipStatusListener()) {
                    VoipMeetingController.getInstance().getVoipStatusListener().onUsersProfileRefresh();
                }
            }

        }

    }

    @Override
    public void removeParticipantAndRefreshUI(String userId) {
        List<String> singleList = new ArrayList<>();
        singleList.add(userId);

        removeParticipantsAndRefreshUI(singleList);
    }

    @Override
    public void removeParticipantsAndRefreshUI(List<String> userIdList) {
        if (isGroupChat()) {
            removeParticipants(userIdList);

            if (null != VoipMeetingController.getInstance().getVoipStatusListener()) {
                VoipMeetingController.getInstance().getVoipStatusListener().onUsersProfileRefresh();
            }
        }
    }

    public void removeParticipants(List<String> userIdList) {
        for (String userId : userIdList) {
            removeParticipant(userId);
        }
    }

    public void removeParticipant(String userId) {
        VoipMeetingMember removedMember = null;
        for (VoipMeetingMember member : getGroup().mParticipantList) {
            if (userId.equals(member.mUserId)) {
                removedMember = member;
                break;
            }
        }

        if (null != removedMember) {
            getGroup().mParticipantList.remove(removedMember);
        }
    }




    @Override
    public void tipToast(String tip) {
        if (null != getVoipStatusListener()) {
            getVoipStatusListener().onTipToast(tip);
        }
    }

    public void refreshData() {
        VoipManager.getInstance().setCallState(CallState.CallState_Idle);

    }

    public void setOnceConnected(boolean isOnceConnected) {
        this.mOnceConnected = isOnceConnected;
    }

    public boolean isOnceConnected() {
        return this.mOnceConnected;
    }

    public void clearData() {
        releaseAgora();
        mCurrentVoipMeeting = null;

        VoipManager.getInstance().setCallState(CallState.CallState_Idle);

        setOnControllerVoipListener(null);
        setOnVoipStatusListener(null);

        mElapsedCallingTime = 0;
        mStartTimeMillis = 0;
        setOnceConnected(false);

        mIsFirstCallHeartBeat = true;

    }
}

