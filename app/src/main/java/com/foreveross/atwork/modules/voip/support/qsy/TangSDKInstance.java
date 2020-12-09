package com.foreveross.atwork.modules.voip.support.qsy;


import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.VoipControllerStrategy;
import com.foreveross.atwork.infrastructure.model.voip.CallState;
import com.foreveross.atwork.infrastructure.model.voip.CallType;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.PhoneState;
import com.foreveross.atwork.infrastructure.model.voip.UserStatus;
import com.foreveross.atwork.infrastructure.model.voip.UserType;
import com.foreveross.atwork.infrastructure.model.voip.VoiceType;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMemberWrapData;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.ActivityManagerHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.manager.VoipNoticeManager;
import com.foreveross.atwork.modules.voip.support.qsy.interfaces.ICallDelegate;
import com.foreveross.atwork.modules.voip.support.qsy.interfaces.ICallDelegatePrivate;
import com.foreveross.atwork.modules.voip.support.qsy.tangsdkwapper.AudioSessionController;
import com.foreveross.atwork.modules.voip.support.qsy.tangsdkwapper.DesktopShareSessionController;
import com.foreveross.atwork.modules.voip.support.qsy.tangsdkwapper.VideoSessionController;
import com.foreveross.atwork.modules.voip.support.qsy.tangsdkwapper.VoipMeetingController;
import com.foreveross.atwork.modules.voip.support.qsy.utils.CameraUtil;
import com.foreveross.atwork.modules.voip.support.qsy.utils.IGnetTangUserHelper;
import com.foreveross.atwork.modules.voip.support.qsy.utils.SoundUtil;
import com.foreveross.atwork.modules.voip.support.qsy.utils.VibratorUtil;
import com.tang.gnettangsdk.CGNetTangSessionErrorInfo;
import com.tang.gnettangsdk.CGNetTangVariant;
import com.tang.gnettangsdk.GNetTangSessionType;
import com.tang.gnettangsdk.IGNetTangUser;
import com.tang.gnettangsdk.PhoneCallNum;
import com.tang.gnettangsdk.TANG_JOINCONF_STATUS;
import com.tang.gnettangsdk.TANG_LEFTCONF_REASON;
import com.tang.gnettangsdk.intArray;
import com.tang.gnettangsdk.phoneCallNumArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;


public class TangSDKInstance implements VoipControllerStrategy{

    private VoipMeetingController mMeetingController;

    private List<VoipMeetingMemberWrapData> mUserItemDataList = null;
    private List<VoipMeetingMemberWrapData> mSpeakingUserItemDataList = null;
    private List<VoipMeetingMemberWrapData> mVideoUserItemDataList = null;

    private VoipMeetingMember mCallSelf = null; ///< 当前用户实体
    private VoipMeetingMember mCallPeer = null; ///< 一对一成员实体
    private VoipMeetingGroup mMeetingGroup = null; ///< 群呼实体

    private String mWorkplusVoipMeetingId;
    private MeetingInfo mMeetingInfo;
    private VoipType mVoipType;

    private int callType = CallType.CallType_Audio.value() | CallType.CallType_Video.value(); // 会议类型
    private CallState mCallState = CallState.CallState_Idle; ///< 呼状态

    private Handler mHandler = new Handler(Looper.getMainLooper());//// TODO: 16/8/9 待优化


    private String joinKey = ""; /// 会议吊起串
    private ICallDelegate callBack = null;
    private ICallDelegatePrivate callBackPrivate = null;

    private boolean m_videoCallOpened = false;
    private String currentShowingVideoUserId = "";
    private boolean currentShowingVideoMySelf = false;
    private boolean m_bNeedSwitchVideo = false; //Activity View: P2P Video- need switch, Group Video-needn't switch; floatview: both need swtich
    private int videoDeviceIndex = 1;
    private int nDefaultVideoShareWidth = 360;
    private int nDefaultVideoShareHeight = 640;

    private int nDefaultVideoViewWidth = 360;
    private int nDefaultVideoViewHeight = 640;

    private String m_strPhoneCallNumber = "";
    private PhoneState m_phoneState = PhoneState.PhoneState_Idle;

    private long m_elapsedCallingTime = 0;// 通话时长
    private long m_startTimeMillis = 0;
    private Timer m_timer = null; ///< 计时器
    private TimerTask m_timerTask = null; ///< 执行的即时任务
    private Timer m_timerWaitPeer = null; ///< 计时器
    private TimerTask m_timerTaskWaitPeer = null; ///< 执行的即时任务
    private Timer m_timerWaitMeetingEnd = null; ///< 计时器
    private TimerTask m_timerTaskWaitMeetingEnd = null; ///< 执行的即时任务
    private static final long ONE_SECOND = 1000;
    private final int MSG_COUNT_DURATION = 0x01; /// 计时消息

    private static Context sAppContext = null;

    private static TangSDKInstance sInst;

    // 上一次检测相机权限的结果
    private boolean mLastCheckCameraIsOpen = false;
    private boolean isBusyTonePlayed; // 是否已播放过忙音，如果已播放，呼结束时就不再播放提示音
    private boolean m_bCallSucceeded = false;
    private long m_nDesktopShareConfUserId = 0;

    private TangSDKInstance() {
        //初始化状态
        mCallState = CallState.CallState_Idle; //暂时尽量避免修改全时云的代码, 所以不引用 workplus 自己维护的 mCallState
//        VoipManager.getInstance().setCallState(CallState.CallState_Idle);
    }

    public static TangSDKInstance getInstance() {
        if (sInst == null) {
            sInst = new TangSDKInstance();
        }
        return sInst;
    }

    public void init(Context context, String strLogPath) {
        try {
            sAppContext = context;
            VoipMeetingController.init(context, strLogPath);

            if(null == mMeetingController) {
                mMeetingController = new VoipMeetingController();
            }

            // 加载音频文件到内存
            SoundUtil.getInstance().init(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param callType     会议类型, CallType类型的组合
     * @param mySelfEntity 当前用户信息
     * @param peerEntity   一对一对方的信息
     * @brief 初始化1对1呼
     */
    public void initPeerCall(int callType, VoipMeetingMember mySelfEntity, VoipMeetingMember peerEntity) {

        clearData();

        this.m_bCallSucceeded = false;
        this.callType = callType;
        this.mCallSelf = mySelfEntity;
        this.mCallPeer = peerEntity;
        m_bNeedSwitchVideo = true;

        changeCallState(CallState.CallState_Init);
        if (mySelfEntity.getUserType() == UserType.Originator) {
            startTimerWaitPeer();
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getDelegate() != null) {

                    //通知应用开始异步去取user, group图像
                    getDelegate().onStartGetUserProfile(getMySelf().mUserId, getMySelf().mDomainId);
                    getDelegate().onStartGetUserProfile(getPeer().mUserId, getPeer().mDomainId);

                    if (getMySelf().getUserType() == UserType.Originator) {
                        getDelegate().onStartVoipMeeting();
                    }
                }
            }
        });

    }

    /**
     * @param callType 会议类型, CallType类型的组合
     * @param group    组信息
     * @brief 初始化群呼
     */
    public void initGroupCall(int callType, VoipMeetingMember mySelfEntity, VoipMeetingGroup group) {
        clearData();

        this.m_bCallSucceeded = false;
        this.callType = callType;
        this.mCallSelf = mySelfEntity;
        this.mMeetingGroup = group;

        m_bNeedSwitchVideo = false;

        changeCallState(CallState.CallState_Init);

        mHandler.post(() -> {
            if (getDelegate() != null) {
                //通知应用开始异步去取user, group图像
                getDelegate().onStartGetUserProfile(getMySelf().mUserId, getMySelf().mDomainId);
                getDelegate().onStartGetGroupProfile(getGroup());
                //通知应用去获取吊起串
                if (getMySelf().getUserType() == UserType.Originator) {
                    getDelegate().onStartVoipMeeting();
                }
            }
        });

    }

    /**
     * @param strPeerPhoneNumber 对方的电话号码
     * @brief 切换为1对1纯电话呼叫
     */
    public void switchToPurePhonePeerCall(String strPeerPhoneNumber) {
        if (mCallPeer == null || !TextUtils.isEmpty(mCallPeer.getBindPhoneNumber()) || TextUtils.isEmpty(strPeerPhoneNumber)) {
            return;
        }

        mCallPeer.setBindPhoneNumber(strPeerPhoneNumber);

        CallState callState = getCallState();
        if (callState == CallState.CallState_Init) {
            startTimerWaitPeer();
            if (!TextUtils.isEmpty(joinKey)) {
                changeCallState(CallState.CallState_StartCall);
                mMeetingController.joinConferenceWithJoinKey(joinKey);
            }
        } else if (callState == CallState.CallState_Waiting ||
                callState == CallState.CallState_Calling) {
            startTimerWaitPeer();
            startPurePhone(strPeerPhoneNumber);
        }
    }


    /**
     * @param callBack
     * @brief 注册回调
     */
    public void setDelegate(ICallDelegate callBack) {
        this.callBack = callBack;
    }


    /**
     * @param callBack
     * @brief 注册内部回调
     */
    public void setDelegatePrivate(ICallDelegatePrivate callBack) {
        this.callBackPrivate = callBack;
    }

    public boolean isGroupChat() {
        return mMeetingGroup != null;
    }

    public boolean isCallSucceeded() {

        return m_bCallSucceeded;
    }

    public CallState getCallState() {
        return mCallState;
    }

    public VoipMeetingMember getMySelf() {
        return mCallSelf;
    }

    public VoipMeetingMember getPeer() {
        return mCallPeer;
    }

    /**
     * 单聊模型转群语音
     */
    public VoipMeetingGroup transfer2Group() {
        VoipMeetingGroup voipMeetingGroup = new VoipMeetingGroup();
        voipMeetingGroup.mParticipantList = new CopyOnWriteArrayList<>();
        voipMeetingGroup.mParticipantList.add(getMySelf());
        voipMeetingGroup.mParticipantList.add(getPeer());

        mMeetingGroup = voipMeetingGroup;

        return mMeetingGroup;
    }

    public VoipMeetingGroup getGroup() {
        return mMeetingGroup;
    }

    public ICallDelegate getDelegate() {

        return callBack;
    }

    public ICallDelegatePrivate getDelegatePrivate() {

        return callBackPrivate;
    }


    /**
     * 挂断电话, 注意需要在 UI 显示操作
     */
    public void finishCall() {
        VoipMeetingMember myself = getMySelf();
        if (myself == null) {
            return;
        }
        if (myself.getUserType() == UserType.Originator) {
            // 发送方点击挂断按钮，结束呼叫
            CallState oldCallState = getCallState();
            int meetingUserCount = getVoipMeetingMemCount();
            stopCall();
            //notify APP cancel call, reject call, finish call
            if (oldCallState == CallState.CallState_Idle ||
                    oldCallState == CallState.CallState_Init) {
                if (myself.getUserType() == UserType.Originator) {
                    //fix bug B160115-010, delay send cancel to avoid peer recieve cancel before invitation
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getDelegate() != null) {
                                getDelegate().onCancelCall();
                            }
                        }
                    }, 1000);

                }
            } else {
                notifyFinishCall(meetingUserCount);
            }

        } else if (myself.getUserType() == UserType.Recipient) {
            // 接收方点击挂断按钮
            CallState oldCallState = getCallState();
            int meetingUserCount = getVoipMeetingMemCount();
            stopCall();

            if (oldCallState == CallState.CallState_Idle ||
                    oldCallState == CallState.CallState_Init) {

                // 若会还没有启动，则拒绝呼叫
                if (getDelegate() != null) {
                    getDelegate().onRejectCall();
                }
            } else {
                // 会已启动，则结束呼叫
                notifyFinishCall(meetingUserCount);
            }
        }

        Logger.e("qsy", "qsy click end call time -> " + System.currentTimeMillis());

    }

    /**
     * 处理超时 call 的时候的挂断处理
     */
    public void autoFinishCall() {
        VoipMeetingMember myself = getMySelf();
        if (null == myself) {
            return;
        }
        if (UserType.Originator == myself.getUserType()) {

            if (null != getDelegate()) {
                getDelegate().onCancelCall();
            }


            // 发送方点击挂断按钮，结束呼叫
            CallState oldCallState = getCallState();
            int meetingUserCount = getVoipMeetingMemCount();
            stopCall();
            //notify APP cancel call, reject call, finish call
            if (CallState.CallState_Idle != oldCallState &&
                    CallState.CallState_Init != oldCallState) {
                notifyFinishCall(meetingUserCount);
            }

        } else if (UserType.Recipient == myself.getUserType()) {
            if (null != getDelegate() && isGroupChat()) {
//                getDelegate().onRejectCall();
            }

            // 接收方点击挂断按钮
            CallState oldCallState = getCallState();
            int meetingUserCount = getVoipMeetingMemCount();
            stopCall();

            if (CallState.CallState_Idle != oldCallState &&
                    CallState.CallState_Init != oldCallState) {
                // 会已启动，则结束呼叫
                notifyFinishCall(meetingUserCount);
            }
        }

    }

    /**
     * @brief 接听按钮点击响应
     */
    public void acceptCall() {
        if (getDelegate() != null) {
            Logger.e("qsy", "qsy click accpet call time -> " + System.currentTimeMillis());

            getDelegate().onStartVoipMeeting();
            getDelegate().onAcceptCall();
        }
    }

    public void startVOIP() {
        if (!mMeetingController.isValid()) {
            return;
        }
        AudioSessionController audioSessionController = mMeetingController.getAudioSession();
        if (audioSessionController != null) {
            if (m_phoneState == PhoneState.PhoneState_Connecting || m_phoneState == PhoneState.PhoneState_Connected) {
                stopPhone();// stop previous phone call first
            }
            m_strPhoneCallNumber = "";
            audioSessionController.startVoip();
        }
    }

    public void stopVOIP() {
        if (!mMeetingController.isValid()) {
            return;
        }
        AudioSessionController audioSessionController = mMeetingController.getAudioSession();
        if (audioSessionController != null) {
            audioSessionController.stopVoip();
        }
    }


    //enum NetworkQualityType
    //{
    //    NetworkQuality_VeryGood = 0,
    //    NetworkQuality_Good,
    //    NetworkQuality_Normal,
    //    NetworkQuality_Bad,
    //    NetworkQuality_VeryBad,
    //};
    public void onVOIPQualityChanged(int statusCode) {
        if (statusCode >= 3) { //voip quality is bad
            //this callback is not in main thread, switch it to main UI thread;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (getDelegatePrivate() != null) {
                        getDelegatePrivate().onVOIPQualityIsBad();
                    }
                }
            });
        }
    }

    //fix bug B160308-001: 蜜蜂SDK 开启呼会议后，3s后才能听到对方说话声

    public void onAudioChannelReady(boolean bReady) {
        if (!bReady) {
            return;
        }

        checkCallSucceed();
    }

    public boolean isAudioChannelReady() {
        AudioSessionController audioSessionController = mMeetingController.getAudioSession();
        if (audioSessionController == null) {
            return false;
        }
        CGNetTangVariant varAudioDataReady = new CGNetTangVariant();
        audioSessionController.getPropertyValue("audioDataReady", varAudioDataReady);
        return varAudioDataReady.getBoolVal();
    }

    private boolean checkCallSucceed() {
        if (getCallState() == CallState.CallState_Calling) {
            return true;
        }

        boolean bRet = false;
        boolean isAudioChannelReady = isAudioChannelReady();
        if (getCallState() == CallState.CallState_Init || getCallState() == CallState.CallState_StartCall || getCallState() == CallState.CallState_Waiting) {
            if (!isGroupChat()) {
                boolean isPeerPurePhoneCallSucceed = checkPeerPurePhoneCallSucceed();
                boolean isPeerCallSucceed = checkPeerCallSucceed();

                if (isAudioChannelReady && (isPeerCallSucceed || isPeerPurePhoneCallSucceed)) {
                    changeCallState(CallState.CallState_Calling);
                    startCountDuration();
                    bRet = true;
                }
            } else {
                if (isAudioChannelReady) {
                    changeCallState(CallState.CallState_Calling);
                    startCountDuration();
                    bRet = true;
                }
            }
        }
        return bRet;
    }

    private boolean checkPeerPurePhoneCallSucceed() {
        if (mCallPeer == null || TextUtils.isEmpty(mCallPeer.getBindPhoneNumber())) {
            return false;
        }

        if (!mMeetingController.isValid()) {
            return false;
        }

        IGNetTangUser tangUser = mMeetingController.getUserByName(mCallPeer.getBindPhoneNumber());
        if (tangUser == null) {
            return false;
        }
        long nAudioStatus = tangUser.getAudioStatus();
        return nAudioStatus == 4;
    }

    private boolean checkPeerCallSucceed() {
        if (mCallPeer == null) {
            return false;
        }

        if (!mMeetingController.isValid()) {
            return false;
        }

        IGNetTangUser tangUser = mMeetingController.getUserByName(mCallPeer.mUserId);
        return tangUser != null;
    }

    public void startPhone(String strPhoneNumber) {
        if (!mMeetingController.isValid()) {
            return;
        }
        long confUserId = getMyselfConfUserId();
        if (confUserId == 0) {
            return;
        }

        AudioSessionController audioSessionController = mMeetingController.getAudioSession();
        if (audioSessionController != null) {

            boolean bNeedDelayCall = false;
            if (m_phoneState == PhoneState.PhoneState_Connecting || m_phoneState == PhoneState.PhoneState_Connected) {
                stopPhone();// stop previous phone call first
                bNeedDelayCall = true;
            } else {
                stopVOIP(); //stop VOIP first
            }

            m_strPhoneCallNumber = strPhoneNumber;

            if (bNeedDelayCall) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PhoneCallNum phoneCallNum = new PhoneCallNum();
                        long confUserId1 = getMyselfConfUserId();
                        phoneCallNum.setNUserID(confUserId1);
                        phoneCallNum.setSPhoneNum(m_strPhoneCallNumber);
                        phoneCallNumArray phoneCalls = new phoneCallNumArray(1);
                        phoneCalls.setitem(0, phoneCallNum);
                        AudioSessionController audioSessionController1 = mMeetingController.getAudioSession();
                        if (audioSessionController1 != null) {
                            audioSessionController1.call(phoneCalls, 1);
                        }
                        m_phoneState = PhoneState.PhoneState_Connecting;
                        if (getDelegate() != null) {
                            getDelegate().onNewPhoneNumberCalled(m_strPhoneCallNumber);
                        }
                    }
                }, 1000);

            } else {
                PhoneCallNum phoneCallNum = new PhoneCallNum();
                phoneCallNum.setNUserID(confUserId);
                phoneCallNum.setSPhoneNum(m_strPhoneCallNumber);
                phoneCallNumArray phoneCalls = new phoneCallNumArray(1);
                phoneCalls.setitem(0, phoneCallNum);
                AudioSessionController audioSessionController1 = mMeetingController.getAudioSession();
                audioSessionController.call(phoneCalls, 1);
                m_phoneState = PhoneState.PhoneState_Connecting;
                if (getDelegate() != null) {
                    getDelegate().onNewPhoneNumberCalled(m_strPhoneCallNumber);
                }
            }
        }
    }

    public String getPhoneCallNumber() {
        return m_strPhoneCallNumber;
    }

    public void stopPhone() {
        if (!mMeetingController.isValid()) {
            return;
        }

        if (m_phoneState != PhoneState.PhoneState_Idle && m_phoneState != PhoneState.PhoneState_Disconnect) {
            m_phoneState = PhoneState.PhoneState_Idle;//fix bug, avoid switch phone to voip exit call
            AudioSessionController audioSessionController = mMeetingController.getAudioSession();
            if (audioSessionController != null) {
                long confUserId = getMyselfConfUserId();
                intArray userIds = new intArray(1);
                userIds.setitem(0, confUserId);
                audioSessionController.hangUp(userIds, 1);
            }
        }
        m_strPhoneCallNumber = "";
    }

    public void onAudioStatusChanged(IGNetTangUser tangUser) {
        if (tangUser == null) {
            return;
        }

        if (getMyselfConfUserId() == tangUser.getUserID()) {
            if (m_phoneState == PhoneState.PhoneState_Connecting || m_phoneState == PhoneState.PhoneState_Connected) {
                //fix bug, the audiostatus property change is too frequent, delay to get the audio status
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updatePhoneState();
                    }
                }, 1000);
            }
        }

        if (tangUser.getUserType() == 1 && mCallPeer != null && !TextUtils.isEmpty(mCallPeer.getBindPhoneNumber()) && tangUser.getUserName().equals(mCallPeer.getBindPhoneNumber())) {
            checkCallSucceed();
        }

        this.onUserUpdate(IGnetTangUserHelper.getUserId(tangUser));
    }

    private void updatePhoneState() {
        IGNetTangUser tangUser = null;
        if (mMeetingController.isValid()) {
            tangUser = mMeetingController.getUserByID(getMyselfConfUserId());
        }
        if (tangUser == null) {
            return;
        }

        long nAudioStatus = tangUser.getAudioStatus();
        if (m_phoneState == PhoneState.PhoneState_Connecting) {
            if (nAudioStatus == 4) {
                m_phoneState = PhoneState.PhoneState_Connected;
                if (getDelegatePrivate() != null) {
                    getDelegatePrivate().onPhoneStateChanged(m_phoneState);
                }
                this.onPhoneCallResult(true);
            }
        } else if (m_phoneState == PhoneState.PhoneState_Connected) {
            if (nAudioStatus < 4) {
                m_phoneState = PhoneState.PhoneState_Disconnect;
                if (getDelegatePrivate() != null) {
                    getDelegatePrivate().onPhoneStateChanged(m_phoneState);
                }
            }
        }
    }

    public void onPhoneCallResult(boolean bSucceeded) {

        if (getDelegatePrivate() != null) {
            getDelegatePrivate().onPhoneCallResult(bSucceeded);
        }
    }

    public void muteMySelf(boolean bMute) {
        if (!mMeetingController.isValid()) {
            return;
        }
        long confUserId = getMyselfConfUserId();
        if (confUserId == 0) {
            return;
        }

        AudioSessionController audioSessionController = mMeetingController.getAudioSession();
        if (audioSessionController != null) {
            intArray userIds = new intArray(1);
            userIds.setitem(0, confUserId);
            if (bMute) {
                audioSessionController.muteUser(userIds, 1);
            } else {
                audioSessionController.unMuteUser(userIds, 1);
            }
        }
    }


    public void enableLoudSpeaker(boolean bEnable) {
        if (!mMeetingController.isValid()) {
            enableDeviceLoudSpeaker(bEnable);
            onLoudSpeakerStatusChanged(bEnable);
            return;
        }

        AudioSessionController audioSessionController = mMeetingController.getAudioSession();
        if (audioSessionController != null) {
            if (bEnable) {
                audioSessionController.enableLoudSpeaker();
            } else {
                audioSessionController.disableLoudSpeaker();
            }
        }
    }

    public boolean isLoudSpeakerStatus() {
        if (!mMeetingController.isValid()) {
            return isEnableDeviceLoudSpeaker();
        }

        boolean bLoudSpeakerStatus = false;

        AudioSessionController audioSessionController = mMeetingController.getAudioSession();
        if (audioSessionController != null) {
            CGNetTangVariant varLoudSpeakerStatus = new CGNetTangVariant();
            audioSessionController.getPropertyValue("loudSpeakerStatus", varLoudSpeakerStatus);
            bLoudSpeakerStatus = varLoudSpeakerStatus.getBoolVal();
        }

        return bLoudSpeakerStatus;
    }

    public void onLoudSpeakerStatusChanged(boolean bLoudSpeaker) {
        if (getDelegatePrivate() != null) {
            this.getDelegatePrivate().onLoudSpeakerStatusChanged(bLoudSpeaker);
        }
    }

    private void enableDeviceLoudSpeaker(boolean bEnable) {
        if (sAppContext == null) {
            return;
        }

        if (bEnable) {
            try {
                AudioManager audioManager = (AudioManager) sAppContext.getSystemService(Context.AUDIO_SERVICE);
                //if(!audioManager.isSpeakerphoneOn()) {
                //setSpeakerphoneOn() only work when audio mode set to MODE_IN_CALL.
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(true);
                //}
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                AudioManager audioManager = (AudioManager) sAppContext.getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null) {
                    //if(audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    //}
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isEnableDeviceLoudSpeaker() {
        if (sAppContext == null) {
            return false;
        }
        boolean bEnable = false;

        try {
            AudioManager audioManager = (AudioManager) sAppContext.getSystemService(Context.AUDIO_SERVICE);
            bEnable = audioManager.isSpeakerphoneOn();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bEnable;
    }

    @NonNull
    @Override
    public List<VoipMeetingMember> getVoipMemInMeetingList() {
        List<VoipMeetingMemberWrapData> wrapDataList = getUserItemDataList();
        List<VoipMeetingMember> memList = new ArrayList<>();
        for (VoipMeetingMemberWrapData wrapData : wrapDataList) {

            VoipMeetingMember member = wrapData.getUserEntity();

            if (UserStatus.UserStatus_Joined.equals(member.getUserStatus()) || UserStatus.UserStatus_NotJoined.equals(member.getUserStatus())) {
                memList.add(member);

            }
        }

        return memList;
    }

    /**
     * 当前参会人员的数据, 与状态
     *
     * @return mUserItemDataList
     */
    @NonNull
    public List<VoipMeetingMemberWrapData> getUserItemDataList() {
        if (mUserItemDataList == null) {
            mUserItemDataList = generateUserItemDataList();
        }
        return mUserItemDataList;
    }

    public List<String> getUserItemIDs() {
        List<String> IDs = new ArrayList<>();
        if (mUserItemDataList != null && mUserItemDataList.size() > 0) {
            for (VoipMeetingMemberWrapData user : mUserItemDataList) {
                IDs.add(user.getUserEntity().mUserId);
            }
        }
        return IDs;
    }

    public VoipMeetingMemberWrapData getUserItemDataByUserId(String userId, String domainId) {
        List<VoipMeetingMemberWrapData> list = getUserItemDataList();

        VoipMeetingMemberWrapData itemData = checkUserExistInUserItemDataList(userId, list);
        if (itemData != null) {
            if (!combineUserItemData(userId, domainId, itemData)) {
                list.remove(itemData);
                return null;
            }
        } else {
            itemData = new VoipMeetingMemberWrapData();
            if (!combineUserItemData(userId, domainId, itemData)) {
                return null;
            }
            list.add(itemData);
        }

        return itemData;
    }

    VoipMeetingMemberWrapData getUserItemDataByConfUserId(long confUserId) {
        List<VoipMeetingMemberWrapData> userList = getUserItemDataList();
        if (userList == null) {
            return null;
        }

        VoipMeetingMemberWrapData itemFind = null;
        for (VoipMeetingMemberWrapData item : userList) {
            if (item == null) {
                continue;
            }
            if (item.getConfUserId() == confUserId) {
                itemFind = item;
                break;
            }
        }
        return itemFind;
    }

    /**
     * @param
     * @brief 返回会议用户个数
     */
    public int getVoipMeetingMemCount() {
        if (!mMeetingController.isValid()) {
            return 0;
        }
        return (int) mMeetingController.getUserCount();
    }

    public void onUserAdded(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }

        String domainId = VoipManager.getInstance().findDomainId(mWorkplusVoipMeetingId, userId);


        VoipMeetingMemberWrapData itemData = getUserItemDataByUserId(userId, domainId);
        if (itemData == null) {
            return;
        }
        if(itemData.isVideoShared()){  //fix bug B160930-022
            onVideoItemAdded(itemData.getConfUserId());
        }
        //else{
        //    onVideoItemDeleted(itemData.getConfUserId());
        //}
        if (getDelegatePrivate() != null) {
            this.getDelegatePrivate().onUserListUpdated();
        }
        // 有新的人入会时，发起方和其他已加入的成员需要有震动+声音videoRingIn提示
        if (getCallState() == CallState.CallState_Calling && isGroupChat() && getVoipMeetingMemCount() >= 1) {
            VibratorUtil.Vibrate(sAppContext, 100);
            SoundUtil.getInstance().playSound(SoundUtil.KEY_RINGIN, 0);
        }

        checkCallSucceed();

        //Bind phone user and Data User
        final String strBindUserId = userId;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                bindPhoneUserAndDataUser(strBindUserId);
            }
        });
    }

    public void onUserUpdate(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }

        String domainId = VoipManager.getInstance().findDomainId(mWorkplusVoipMeetingId, userId);

        VoipMeetingMemberWrapData itemData = getUserItemDataByUserId(userId, domainId);
        if (itemData == null) {
            return;
        }
        if (itemData.isVideoShared()) {
            onVideoItemAdded(itemData.getConfUserId());
        } else {
            onVideoItemDeleted(itemData.getConfUserId());
        }

        if (getDelegatePrivate() != null) {
            this.getDelegatePrivate().onUserListUpdated();
        }
    }

    public void onUserRemoved(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        if (!isGroupChat() && mCallSelf != null && !userId.equals(mCallSelf.mUserId) && getCallState() != CallState.CallState_Ending && getVoipMeetingMemCount() <= 1) {
            stopCall();
            if (getCallState() == CallState.CallState_Ended) {
                notifyFinishCall(0);
                return;
            }
            return;
        }


        List<VoipMeetingMemberWrapData> list = getUserItemDataList();
        VoipMeetingMemberWrapData itemData = checkUserExistInUserItemDataList(userId, list);
        if (itemData != null) {

            if(itemData.isVideoShared()) {
                //fix bug: B160727-032
                //  refresh video ui before update user list, otherwise,
                //  when received onVideoIemDeleted, the user was removed, the video
                //  has no chance to remove.
                onVideoItemDeleted(itemData.getConfUserId());
            }


            onIsSpeakingChanged(itemData.getUserID(), false);
            itemData.getUserEntity().setUserStatus(UserStatus.UserStatus_Left);
            if (getDelegatePrivate() != null) {
                this.getDelegatePrivate().onUserListUpdated();
            }
        }
    }

    public void onIsSpeakingChanged(String userId, boolean bSpeaking) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }

        String domainId = VoipManager.getInstance().findDomainId(mWorkplusVoipMeetingId, userId);

        if (mSpeakingUserItemDataList == null) {
            mSpeakingUserItemDataList = new ArrayList<>();
        }

        VoipMeetingMemberWrapData userItem = getUserItemDataByUserId(userId, domainId);
        if (userItem != null) {
            userItem.setIsSpeaking(bSpeaking);
            if (getDelegatePrivate() != null) {
                getDelegatePrivate().onUserListUpdated();
            }
        }

        boolean bChanged = false;

        VoipMeetingMemberWrapData itemFind = null;
        for (VoipMeetingMemberWrapData item : mSpeakingUserItemDataList) {
            if (item == null) {
                continue;
            }
            if (item.getUserEntity().mUserId.equals(userId)) {
                itemFind = item;
                break;
            }
        }

        if (itemFind != null) {
            if (!bSpeaking) {
                mSpeakingUserItemDataList.remove(itemFind);
                bChanged = true;
            }
        } else {
            if (bSpeaking && userItem != null) {
                mSpeakingUserItemDataList.add(userItem);
                bChanged = true;
            }
        }

        if (getDelegatePrivate() != null) {
//            getDelegatePrivate().onUserListUpdated();
            if (bChanged) {
                getDelegatePrivate().onIsSpeakingChanged(getSpeakingUserNames());
            }
        }
    }

    public String getSpeakingUserNames() {
        if (!mMeetingController.isValid()) {
            return "";
        }
        List<VoipMeetingMemberWrapData> userList = getUserItemDataList();
        if (userList == null || userList.size() == 0) {
            mSpeakingUserItemDataList = null;
            return "";
        }

        if (mSpeakingUserItemDataList == null) {
            mSpeakingUserItemDataList = new ArrayList<>();
            for (VoipMeetingMemberWrapData item : userList) {
                if (item == null) {
                    continue;
                }
                if (item.isSpeaking()) {
                    mSpeakingUserItemDataList.add(item);
                }
            }
        }

        String strSpeakingNames = "";
        boolean bFirst = true;
        for (VoipMeetingMemberWrapData item : mSpeakingUserItemDataList) {
            if (item == null) {
                continue;
            }
            if (bFirst) {
                strSpeakingNames = item.getUserEntity().mShowName;
                bFirst = false;
            } else {
                strSpeakingNames += " ";
                strSpeakingNames += item.getUserEntity().mShowName;
            }
        }
        return strSpeakingNames;
    }

    public long getElapsedCallingTime() {

        return m_elapsedCallingTime;
    }

    public List<VoipMeetingMemberWrapData> getVideoInstanceListData() {
        if (!mMeetingController.isValid()) {
            return null;
        }
        List<VoipMeetingMemberWrapData> userList = getUserItemDataList();
        if (userList == null || userList.size() == 0) {
            mVideoUserItemDataList = null;
            return null;
        }

        if (mVideoUserItemDataList == null) {
            mVideoUserItemDataList = new ArrayList<>();
            for (VoipMeetingMemberWrapData item : userList) {
                if (item == null) {
                    continue;
                }
                if (item.isVideoShared()) {
                    mVideoUserItemDataList.add(item);
                }
            }
        }

        return mVideoUserItemDataList;
    }

    public VoipMeetingMemberWrapData getVideoInstanceListItemData(String userId) {

        List<VoipMeetingMemberWrapData> videoList = getVideoInstanceListData();
        if (videoList == null) {
            return null;
        }
        VoipMeetingMemberWrapData itemFind = null;

        for (VoipMeetingMemberWrapData item : videoList) {
            if (item == null) {
                continue;
            }
            if (item.getUserEntity().mUserId.equals(userId)) {
                itemFind = item;
                break;
            }
        }

        return itemFind;
    }

    public void onVideoItemAdded(long conUserId) {
        List<VoipMeetingMemberWrapData> userList = getUserItemDataList();
        if (userList == null) {
            return;
        }

        List<VoipMeetingMemberWrapData> videoList = getVideoInstanceListData();
        if (videoList == null) {
            return;
        }


        boolean bChanged = false;
        VoipMeetingMemberWrapData itemFind = null;
        for (VoipMeetingMemberWrapData item : videoList) {
            if (item == null) {
                continue;
            }
            if (item.getConfUserId() == conUserId) {
                item.setIsVideoShared(true);
                itemFind = item;
                break;
            }
        }

        if (itemFind == null) {
            for (VoipMeetingMemberWrapData item : userList) {
                if (item == null) {
                    continue;
                }
                if (item.getConfUserId() == conUserId) {
                    item.setIsVideoShared(true);
                    videoList.add(item);
                    itemFind = item;
                    bChanged = true;
                    break;
                }
            }
        }

        if (itemFind != null && bChanged) {
            notifyVideoItemAdded(itemFind);
        }
    }

    private void notifyVideoItemAdded(VoipMeetingMemberWrapData itemAdded) {
        //fix crash bug B160202-024]
        if (getDelegatePrivate() != null) {
            getDelegatePrivate().onVideoItemAdded(itemAdded.getUserEntity().mUserId);
        }

        if (this.m_videoCallOpened && m_bNeedSwitchVideo) {
            if (!mCallSelf.mUserId.equals(itemAdded.getUserEntity().mUserId)) {

                //auto select next video item to show
                List<VoipMeetingMemberWrapData> videoList = getVideoInstanceListData();
                if (videoList == null) {
                    return;
                }

                VoipMeetingMemberWrapData itemFind = null;
                VoipMeetingMemberWrapData itemSelf = null;
                for (VoipMeetingMemberWrapData item : videoList) {
                    if (item == null) {
                        continue;
                    }
                    if (item.getUserEntity().mUserId.equals(mCallSelf.mUserId)) {
                        itemSelf = item;
                    } else {
                        if (itemFind == null &&
                                !item.getUserEntity().mUserId.equals(itemAdded.getUserEntity().mUserId) &&
                                item.getVideoSurface() != null) {

                            itemFind = item;
                        }
                    }
                    if (itemSelf != null && itemFind != null) {
                        break;
                    }
                }

                //no other video show
                if (itemFind == null) {
                    videoShowVideoItem(itemAdded.getUserEntity().mUserId);
                    if (itemSelf != null && itemSelf.getVideoSurface() != null) {
                        videoShowVideoItem(itemSelf.getUserEntity().mUserId);
                    }
                } else {
                    //noting to do, only need show one other user's video
                }

            } else {
                if(itemAdded.isVideoShared()){ //fix bug B160930-022
                    videoShowVideoItem(itemAdded);
                }
            }
        }
    }


    public void onVideoItemDeleted(long conUserId) {
        List<VoipMeetingMemberWrapData> videoList = getVideoInstanceListData();
        if (videoList == null) {
            return;
        }

        VoipMeetingMemberWrapData itemFind = null;
        for (VoipMeetingMemberWrapData item : videoList) {
            if (item == null) {
                continue;
            }
            if (item.getConfUserId() == conUserId) {
                itemFind = item;
                break;
            }
        }

        if (itemFind != null) {
            itemFind.setIsVideoShared(false);
            videoList.remove(itemFind);
            notifyVideoItemDeleted(itemFind);
        }
    }

    private void notifyVideoItemDeleted(VoipMeetingMemberWrapData itemDeleted) {
        if (getDelegatePrivate() == null) {
            return;
        }

        getDelegatePrivate().onVideoItemDeleted(itemDeleted.getUserEntity().mUserId);

        if (this.m_videoCallOpened && m_bNeedSwitchVideo) {
            if (!mCallSelf.mUserId.equals(itemDeleted.getUserEntity().mUserId)) {

                //auto select next video item to show
                if (itemDeleted.getVideoSurface() != null) {
                    List<VoipMeetingMemberWrapData> videoList = getVideoInstanceListData();
                    if (videoList == null) {
                        return;
                    }

                    VoipMeetingMemberWrapData itemFind = null;
                    VoipMeetingMemberWrapData itemSelf = null;
                    for (VoipMeetingMemberWrapData item : videoList) {
                        if (item == null) {
                            continue;
                        }
                        if (item.getUserEntity().mUserId.equals(mCallSelf.mUserId)) {
                            itemSelf = item;
                        } else {
                            if (itemFind == null &&
                                    !item.getUserEntity().mUserId.equals(itemDeleted.getUserEntity().mUserId) &&
                                    item.getVideoSurface() == null) {

                                itemFind = item;
                            }
                        }
                        if (itemSelf != null && itemFind != null) {
                            break;
                        }
                    }

                    boolean bNoVideo = false;

                    //no other video can show
                    if (itemFind == null) {
                        videoHideVideoItem(itemDeleted, false);
                        if (itemSelf != null && itemSelf.getVideoSurface() != null) {
                            videoShowVideoItem(itemSelf);
                        } else {
                            bNoVideo = true;
                        }
                    } else { //auto switch to other video
                        videoHideVideoItem(itemDeleted, false);
                        videoShowVideoItem(itemFind);
                    }

                    if (bNoVideo) {
                        closeVideoCall();
                    }
                }
            } else {
                if (itemDeleted.getVideoSurface() != null) { //fix bug B160701-090
                    videoHideVideoItem(itemDeleted, false);
                }

                if (this.getShowedVideoItemCount() == 0) {
                    closeVideoCall();
                }
            }
        }
    }

    public void onVideoItemShowed(long meetingUserId) {
        List<VoipMeetingMemberWrapData> videoList = getVideoInstanceListData();
        if (videoList == null) {
            return;
        }

        VoipMeetingMemberWrapData itemFind = null;
        for (VoipMeetingMemberWrapData item : videoList) {
            if (item == null) {
                continue;
            }
            if (item.getConfUserId() == meetingUserId) {
                itemFind = item;
                break;
            }
        }

        if (itemFind != null) {
            //NOTIFY UI in main thread
            final String userId = itemFind.getUserEntity().mUserId;
            final String domainId = itemFind.getUserEntity().mDomainId;

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getDelegatePrivate() != null) {
                        getDelegatePrivate().onVideoItemShowed(userId, domainId);
                    }
                }
            }, 100);
        }
    }

    /////////////////////////////////////
    //Video methods
    ////////////////////////////////////
    public void openVideoCall() {
        m_videoCallOpened = true;
    }

    public boolean isVideoCallOpened() {
        return m_videoCallOpened;
    }

    public void closeVideoCall() {
        closeAllVideo(true);

        m_videoCallOpened = false;
        if (getDelegatePrivate() != null) {
            getDelegatePrivate().onVideoCallClosed();
        }
    }

    private void closeAllVideo(boolean bDetachSurface) {
        if (mVideoUserItemDataList == null) {
            return;
        }

        VoipMeetingMemberWrapData itemSelf = null;
        for (VoipMeetingMemberWrapData item : mVideoUserItemDataList) {
            if (item == null) {
                continue;
            }

            if (item.getVideoSurface() != null) {
                if (mCallSelf.mUserId.equals(item.getUserEntity().mUserId)) {
                    itemSelf = item;
                } else {
                    videoStopView(item.getConfUserId());
                    if (bDetachSurface) {
                        if (getDelegatePrivate() != null) {
                            getDelegatePrivate().onVideoItemDetachSurface(item.getUserEntity().mUserId, item.getVideoSurface());
                        }
                    }
                    item.setVideoSurface(null);
                }
            }
        }

        if (itemSelf != null && itemSelf.getVideoSurface() != null) {
            videoStopPreview();
            if (bDetachSurface) {
                if (getDelegatePrivate() != null) {
                    getDelegatePrivate().onVideoItemDetachSurface(itemSelf.getUserEntity().mUserId, itemSelf.getVideoSurface());
                }
            }
            videoStopShare(itemSelf.getConfUserId());
            itemSelf.setVideoSurface(null);
        }

        if (mVideoUserItemDataList != null) {
            mVideoUserItemDataList.clear();
        }
    }

    public void videoSetShareSize(int nWidth, int nHeight) {
        nDefaultVideoViewWidth = nWidth;
        nDefaultVideoViewHeight = nHeight;
    }

    public void videoShowVideoItem(String userId) {
        if (getDelegatePrivate() == null || mCallSelf == null) {
            return;
        }

        List<VoipMeetingMemberWrapData> videoList = getVideoInstanceListData();

        VoipMeetingMemberWrapData itemFind = null;
        if (videoList != null) {
            for (VoipMeetingMemberWrapData item : videoList) {
                if (item == null) {
                    continue;
                }
                if (item.getUserEntity().mUserId.equals(userId)) {
                    itemFind = item;
                    break;
                }
            }
        }

        //preview
        if (itemFind == null && userId.equals(mCallSelf.mUserId)) {
            List<VoipMeetingMemberWrapData> list = getUserItemDataList();
            VoipMeetingMemberWrapData itemData = checkUserExistInUserItemDataList(userId, list);
            if (itemData != null) {
                Object videoSurface = getDelegatePrivate().onVideoItemAttachSurface(userId);
                if (videoSurface == null) {
                    return;
                }
                videoStartPreview(videoSurface);

                //delay start share
                final long confUserId = itemData.getConfUserId();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        videoStartShare(confUserId);
                    }
                });

                itemData.setVideoSurface(videoSurface);
                itemData.setIsVideoShared(true);
                videoList.add(itemData);
            }
        } else {
            if (itemFind != null) {
                videoShowVideoItem(itemFind);
            }
        }
    }

    private void videoShowVideoItem(VoipMeetingMemberWrapData itemData) {
        if (getDelegatePrivate() == null || itemData == null) {
            return;
        }

        String userId = itemData.getUserEntity().mUserId;

        if (mCallSelf.mUserId.equals(userId)) {
            if (itemData.getVideoSurface() != null) {
                videoStopPreview();
                if (getDelegatePrivate() != null) {
                    getDelegatePrivate().onVideoItemDetachSurface(itemData.getUserEntity().mUserId, itemData.getVideoSurface());
                }
                itemData.setVideoSurface(null);
                Object videoSurface = getDelegatePrivate().onVideoItemAttachSurface(userId);
                if (videoSurface == null) {
                    return;
                }
                videoStartPreview(videoSurface);
                itemData.setVideoSurface(videoSurface);
            } else {
                Object videoSurface = getDelegatePrivate().onVideoItemAttachSurface(userId);
                if (videoSurface == null) {
                    return;
                }
                videoStartPreview(videoSurface);

                //delay start share
                final long confUserId = itemData.getConfUserId();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        videoStartShare(confUserId);
                    }
                });

                itemData.setVideoSurface(videoSurface);
            }

        } else {
            if (itemData.getVideoSurface() != null) {
                videoStopView(itemData.getConfUserId());
                if (getDelegatePrivate() != null) {
                    getDelegatePrivate().onVideoItemDetachSurface(itemData.getUserEntity().mUserId, itemData.getVideoSurface());
                }
                final Object videoSurface = getDelegatePrivate().onVideoItemAttachSurface(userId);
                if (videoSurface == null) {
                    return;
                }
                final long nConfUserId = itemData.getConfUserId();
                //delay start video view
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        videoStartView(nConfUserId, videoSurface);
                    }
                });
                itemData.setVideoSurface(videoSurface);
            } else {
                Object videoSurface = getDelegatePrivate().onVideoItemAttachSurface(userId);
                if (videoSurface == null) {
                    return;
                }
                videoStartView(itemData.getConfUserId(), videoSurface);
                itemData.setVideoSurface(videoSurface);
            }
        }
    }

    public void videoHideVideoItem(String userId, boolean bStopShare) {
        List<VoipMeetingMemberWrapData> videoList = getUserItemDataList();

        VoipMeetingMemberWrapData itemFind = null;
        if (videoList != null) {
            for (VoipMeetingMemberWrapData item : videoList) {
                if (item == null) {
                    continue;
                }
                if (item.getUserEntity().mUserId.equals(userId)) {
                    itemFind = item;
                    break;
                }
            }
        }

        if (itemFind != null) {
            videoHideVideoItem(itemFind, bStopShare);
        }
    }


    private void videoHideVideoItem(VoipMeetingMemberWrapData itemData, boolean bStopShare) {
        if (itemData == null) {
            return;
        }
        if (itemData.getVideoSurface() != null) {
            if (mCallSelf.mUserId.equals(itemData.getUserEntity().mUserId)) {
                videoStopPreview();
                if (getDelegatePrivate() != null) {
                    getDelegatePrivate().onVideoItemDetachSurface(itemData.getUserEntity().mUserId, itemData.getVideoSurface());
                }
                if (bStopShare) {
                    videoStopShare(itemData.getConfUserId());
                }
            } else {
                videoStopView(itemData.getConfUserId());
                if (getDelegatePrivate() != null) {
                    getDelegatePrivate().onVideoItemDetachSurface(itemData.getUserEntity().mUserId, itemData.getVideoSurface());
                }
            }
            itemData.setVideoSurface(null);
        } else {
            if (bStopShare) {
                videoStopShare(itemData.getConfUserId());
            }
        }
    }

    public void videoItemResetRender(String userId, Object surfaceView) {
        List<VoipMeetingMemberWrapData> videoList = getVideoInstanceListData();

        VoipMeetingMemberWrapData itemFind = null;
        if (videoList != null) {
            for (VoipMeetingMemberWrapData item : videoList) {
                if (item == null) {
                    continue;
                }
                if (item.getUserEntity().mUserId.equals(userId)) {
                    itemFind = item;
                    break;
                }
            }
        }

        if (itemFind != null) {
            videoResetRender(itemFind.getConfUserId(), surfaceView);
            itemFind.setVideoSurface(surfaceView);
        }
    }

    public int getShowedVideoItemCount() {
        List<VoipMeetingMemberWrapData> videoList = getVideoInstanceListData();
        if (videoList == null) {
            return 0;
        }

        int count = 0;
        for (VoipMeetingMemberWrapData item : videoList) {
            if (item == null) {
                continue;
            }
            if (item.getVideoSurface() != null) {
                count++;
            }
        }

        return count;
    }

    /**
     * 全时云的悬浮窗会根据其他用户关闭视频开关的场景, 自动切换画面, 所以在悬浮窗销毁时需要保存起来当前的状态
     * */
    @Override
    public void saveShowingVideo(int currentBigViewUid) {
        currentShowingVideoUserId = "";
        currentShowingVideoMySelf = false;
        if (!isVideoCallOpened() || 0 == getShowedVideoItemCount()) {
            return;
        }

        VoipMeetingMemberWrapData itemSelf = null;
        VoipMeetingMemberWrapData itemOther = null;
        List<VoipMeetingMemberWrapData> videoList = getVideoInstanceListData();
        for (VoipMeetingMemberWrapData item : videoList) {
            if (item == null) {
                continue;
            }
            if (item.isMySelf()) {
                itemSelf = item;
            } else {
                if (itemOther == null && item.getVideoSurface() != null) {
                    itemOther = item;
                } else if (item.getVideoSurface() != null) {
                    videoHideVideoItem(item, false);
                }
            }
        }

        if (itemOther != null && itemOther.getVideoSurface() != null) {
            if (itemSelf != null) {
                //videoShowVideoItem(itemSelf.getUserEntity().getUserId(), false);
                //videoStopPreview();
                //if(getDelegatePrivate()!=null){
                //    getDelegatePrivate().onVideoItemDetachSurface(itemSelf.getUserEntity().getUserId(),itemSelf.getVideoSurface());
                //}
                // itemSelf.setVideoSurface(null);
                if (itemSelf.getVideoSurface() != null) {
                    videoHideVideoItem(itemSelf, false);
                }
                currentShowingVideoMySelf = true;
            }
            videoHideVideoItem(itemOther, false);
            currentShowingVideoUserId = itemOther.getUserEntity().mUserId;
        } else {
            if (itemSelf != null) {
                //videoShowVideoItem(itemSelf.getUserEntity().getUserId(), false);
                //videoStopPreview();
                //if(getDelegatePrivate()!=null){
                //    getDelegatePrivate().onVideoItemDetachSurface(itemSelf.getUserEntity().getUserId(),itemSelf.getVideoSurface());
                //}
                //itemSelf.setVideoSurface(null);
                if (itemSelf.getVideoSurface() != null) {
                    videoHideVideoItem(itemSelf, false);
                }
                currentShowingVideoUserId = itemSelf.getUserEntity().mUserId;
                currentShowingVideoMySelf = true;
            }
        }
    }

    public boolean haveVideoNeedRestore() {
        return currentShowingVideoUserId.length() != 0;
    }

    public String getCurrentShowingVideoUserId() {
        return currentShowingVideoUserId;
    }

    public boolean getCurrentShowingVideoMySelf() {
        return currentShowingVideoMySelf;
    }

    public void restoreNeedShowVideo(boolean bNeedSwitchVideo) {
        m_bNeedSwitchVideo = bNeedSwitchVideo;
        //fix bug B160202-013, if it's mMeetingGroup chat, need restore video
        if (!bNeedSwitchVideo && isGroupChat()) {
            currentShowingVideoUserId = "";
            currentShowingVideoMySelf = false;
            return;
        }
        if (!haveVideoNeedRestore()) {
            currentShowingVideoUserId = "";
            currentShowingVideoMySelf = false;
            return;
        }

        if (isGroupChat()) {
            boolean bFind = false;
            List<VoipMeetingMemberWrapData> videoList = getVideoInstanceListData();
            for (VoipMeetingMemberWrapData item : videoList) {
                if (item == null) {
                    continue;
                }
                if (item.getUserEntity().mUserId.equals(currentShowingVideoUserId)) {
                    bFind = true;
                    openVideoCall();
                    videoShowVideoItem(currentShowingVideoUserId);
                    break;
                }
            }

            // restore myself preview
            if (!bFind) {
                if (mCallSelf != null && mCallSelf.mUserId.equals(currentShowingVideoUserId)) {
                    openVideoCall();
                    videoShowVideoItem(currentShowingVideoUserId);
                }
            }
        } else {
            //restore other video first
            List<VoipMeetingMemberWrapData> videoList = getVideoInstanceListData();
            if (videoList != null) {
                for (VoipMeetingMemberWrapData item : videoList) {
                    if (item == null) {
                        continue;
                    }
                    if (mCallSelf != null && !mCallSelf.mUserId.equals(item.getUserEntity().mUserId) &&
                            item.getUserEntity().mUserId.equals(currentShowingVideoUserId)) {
                        openVideoCall();
                        videoShowVideoItem(currentShowingVideoUserId);
                        break;
                    }
                }
            }

            // restore myself preview
            if ((mCallSelf != null && mCallSelf.mUserId.equals(currentShowingVideoUserId)) ||
                    currentShowingVideoMySelf) {
                openVideoCall();
                videoShowVideoItem(mCallSelf.mUserId);
            }
        }

        currentShowingVideoUserId = "";
        currentShowingVideoMySelf = false;
    }

    public boolean isVideoItemShowing(String userId) {
        List<VoipMeetingMemberWrapData> videoList = getVideoInstanceListData();
        if (videoList == null) {
            return false;
        }

        VoipMeetingMemberWrapData itemFind = null;
        for (VoipMeetingMemberWrapData item : videoList) {
            if (item == null) {
                continue;
            }
            if (item.getUserEntity().mUserId.equals(userId) && item.getVideoSurface() != null) {
                itemFind = item;
                break;
            }
        }

        return itemFind != null;

    }

    public void videoSwitchCamera() {
        List<VoipMeetingMemberWrapData> videoList = getVideoInstanceListData();
        if (videoList == null) {
            return;
        }

        VideoSessionController videoSession = mMeetingController.getVideoSession();

        if (videoSession == null) {
            return;
        }
        long videoDeviceCount = videoSession.getCameraCount();
        if (videoDeviceCount <= 0) {
            return;
        }

        if (videoDeviceCount > 0 && videoDeviceIndex >= videoDeviceCount) {
            videoDeviceIndex = (int) videoDeviceCount - 1;
        }

        VoipMeetingMemberWrapData itemFind = null;
        for (VoipMeetingMemberWrapData item : videoList) {
            if (item == null) {
                continue;
            }
            if (item.getUserEntity().mUserId.equals(mCallSelf.mUserId)) {
                itemFind = item;
                break;
            }
        }

        if (itemFind != null) {
            int oldVideoDeviceIndex = videoDeviceIndex;
            videoDeviceIndex = (videoDeviceIndex + 1) % (int) videoDeviceCount;
            if (oldVideoDeviceIndex != videoDeviceIndex && videoDeviceIndex >= 0 && videoDeviceIndex < videoDeviceCount) {
                if (itemFind.getVideoSurface() != null) {
                    videoSession.changeShareCamera(itemFind.getConfUserId(), videoDeviceIndex);

                    //videoStopPreview();
                    //
                    //if (getDelegatePrivate() != null) {
                    //    getDelegatePrivate().onVideoItemDetachSurface(itemFind.getUserEntity().getUserId(), itemFind.getVideoSurface());
                    //}

                    // fix bug B161010-001
                    videoStopPreview();
                    videoStartPreview(itemFind.getVideoSurface());

                    videoResetRender(itemFind.getConfUserId(), itemFind.getVideoSurface()); //ranger fix bug B160720-092
                } else {
                    Object videoSurface = getDelegatePrivate().onVideoItemAttachSurface(itemFind.getUserID());
                    if (videoSurface == null) {
                        return;
                    }
                    videoStartPreview(videoSurface);
                    itemFind.setVideoSurface(videoSurface);
                }
            }
        }
    }


    private void videoStartPreview(Object surfaceView) {
        if (!mMeetingController.isValid() || surfaceView == null) {
            return;
        }

        VideoSessionController videoSessionController = mMeetingController.getVideoSession();
        if (videoSessionController == null) {
            return;
        }
        long videoDeviceCount = videoSessionController.getCameraCount();
        if (videoDeviceIndex >= 0 && videoDeviceIndex < videoDeviceCount) {
            videoSessionController.startPreview(videoDeviceIndex, surfaceView, 1);
        }
    }

    private void videoStopPreview() {
        VideoSessionController videoSessionController = mMeetingController.getVideoSession();
        if (videoSessionController == null) {
            return;
        }

        videoSessionController.stopPreview();
    }

    private void videoStartView(long confUserId, Object surfaceView) {
        if (!mMeetingController.isValid()) {
            return;
        }
        final long tempConfUserId = confUserId;
        VideoSessionController videoSessionController = mMeetingController.getVideoSession();
        if (videoSessionController == null) {
            return;
        }
        videoSessionController.startView(confUserId, surfaceView, 1);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!mMeetingController.isValid()) {
                    return;
                }
                VideoSessionController videoSessionController1 = mMeetingController.getVideoSession();
                if (videoSessionController1 == null) {
                    return;
                }
                videoSessionController1.requestResolution(tempConfUserId, nDefaultVideoViewWidth, nDefaultVideoViewHeight);
            }
        });
    }

    private void videoResetRender(long confUserId, Object surfaceView) {
        if (!mMeetingController.isValid() || surfaceView == null) {
            return;
        }

        VideoSessionController videoSessionController = mMeetingController.getVideoSession();
        if (videoSessionController == null) {
            return;
        }
        videoSessionController.resetRenderWindow(confUserId, surfaceView, 1);
    }

    private void videoStartShare(long confUserId) {
        if (!mMeetingController.isValid()) {
            return;
        }
        final VideoSessionController videoSessionController = mMeetingController.getVideoSession();
        if (videoSessionController == null) {
            return;
        }
        long videoDeviceCount = videoSessionController.getCameraCount();
        if (videoDeviceIndex >= 0 && videoDeviceIndex < videoDeviceCount) {
            videoSessionController.startShare(confUserId, videoDeviceIndex, nDefaultVideoShareWidth, nDefaultVideoShareHeight);
        }
    }

    private void videoStopShare(long confUserId) {
        if (!mMeetingController.isValid()) {
            return;
        }
        final VideoSessionController videoSessionController = mMeetingController.getVideoSession();
        if (videoSessionController == null) {
            return;
        }
        long videoDeviceCount = videoSessionController.getCameraCount();
        if (videoDeviceIndex >= 0 && videoDeviceIndex < videoDeviceCount) {
            videoSessionController.stopShare(confUserId);
        }

    }

    private void videoStopView(long confUserId) {
        if (!mMeetingController.isValid()) {
            return;
        }
        VideoSessionController videoSessionController = mMeetingController.getVideoSession();
        if (videoSessionController == null) {
            return;
        }
        videoSessionController.stopView(confUserId);
    }


    public void desktopStartView(ImageView imageView) {
        if (imageView == null) {
            return;
        }
        if (!mMeetingController.isValid()) {
            return;
        }

        DesktopShareSessionController desktopShareSessionController = mMeetingController.getDesktopSession();
        if (desktopShareSessionController == null) {
            return;
        }
        desktopShareSessionController.startView(imageView);
    }

    public void desktopStopView() {
        if (!mMeetingController.isValid()) {
            return;
        }

        if (getCallState() == CallState.CallState_Ending || getCallState() == CallState.CallState_Ended) {
            return;
        }

        DesktopShareSessionController desktopShareSessionController = mMeetingController.getDesktopSession();
        if (desktopShareSessionController == null) {
            return;
        }
        desktopShareSessionController.stopView();

        //post onDesktopViewerStopped callback
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getDelegatePrivate() != null) {
                    getDelegatePrivate().onDesktopViewerStopped();
                }
            }
        });
    }

    public void onDesktopSessionCreated() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mMeetingController.isValid()) {
                    return;
                }

                DesktopShareSessionController desktopShareSessionController = mMeetingController.getDesktopSession();
                if (desktopShareSessionController == null) {
                    return;
                }

                CGNetTangVariant shareUserId = new CGNetTangVariant();
                desktopShareSessionController.getPropertyValue("shareUserID", shareUserId);
                if (shareUserId.getUintVal() != 0) {
                    m_nDesktopShareConfUserId = shareUserId.getUintVal();
                    onUserUpdate(getUserIdByConUserId(m_nDesktopShareConfUserId));
                    if (getDelegatePrivate() != null) {
                        getDelegatePrivate().onDesktopShared();
                    }
                }
            }
        }, 100);
    }

    public void onDesktopShared() {
        DesktopShareSessionController desktopShareSessionController = mMeetingController.getDesktopSession();
        if (desktopShareSessionController == null) {
            return;
        }

        CGNetTangVariant shareUserId = new CGNetTangVariant();
        desktopShareSessionController.getPropertyValue("shareUserID", shareUserId);

        m_nDesktopShareConfUserId = 0;
        if (shareUserId.getUintVal() != 0) {
            m_nDesktopShareConfUserId = shareUserId.getUintVal();
            this.onUserUpdate(getUserIdByConUserId(m_nDesktopShareConfUserId));
        }
        if (getDelegatePrivate() != null) {
            getDelegatePrivate().onDesktopShared();
        }
    }

    public void onDesktopShareStoped() {
        long nOldShareUserId = m_nDesktopShareConfUserId;
        m_nDesktopShareConfUserId = 0;
        this.onUserUpdate(getUserIdByConUserId(nOldShareUserId));

        if (getDelegatePrivate() != null) {
            getDelegatePrivate().onDesktopShareStopped();
        }
    }

    public void onDesktopViewerStarted() {
        if (getDelegatePrivate() != null) {
            getDelegatePrivate().onDesktopViewerShowed();
        }
    }

    public void onDesktopViewerStopped() { //this callback means the share instantce deleted, need stopview
        onDesktopShareStoped();
    }

    public boolean haveDesktopShare() {
        return m_nDesktopShareConfUserId != 0;
    }

    public VoipMeetingMemberWrapData getDesktopSharerUserData() {
        if (!haveDesktopShare()) {
            return null;
        }

        return getUserItemDataByConfUserId(m_nDesktopShareConfUserId);
    }

    public DesktopShareSessionController getDesktopShareSession() {
        if (!mMeetingController.isValid()) {
            return null;
        }
        DesktopShareSessionController desktopShareSessionController = mMeetingController.getDesktopSession();
        return desktopShareSessionController;
    }

    public void onConfJoined(int statusCode) {

        if (TANG_JOINCONF_STATUS.swigToEnum(statusCode) == TANG_JOINCONF_STATUS.JOINCONFSTATUS_SUCCEEDED) {
            boolean bCallSucceed = checkCallSucceed();
            if (!bCallSucceed) {
                if (!isGroupChat() && getCallState() == CallState.CallState_StartCall) {
                    changeCallState(CallState.CallState_Waiting);
                }
            }
        } else if (TANG_JOINCONF_STATUS.swigToEnum(statusCode) == TANG_JOINCONF_STATUS.JOINCONFSTATUS_RECONNECTSUCCEEDED) {
            onConfReconnected();

        } else if (TANG_JOINCONF_STATUS.swigToEnum(statusCode) == TANG_JOINCONF_STATUS.JOINCONFSTATUS_NETWORKCONNECTFAILED ||
                TANG_JOINCONF_STATUS.swigToEnum(statusCode) == TANG_JOINCONF_STATUS.JOINCONFSTATUS_NETWORKAUTHFAILED) {
            //start conf failed
            //fix mantis bug 0004116
            changeCallState(CallState.CallState_Ending);
            changeCallState(CallState.CallState_Ended);
            notifyFinishCall(0);

        } else if (TANG_JOINCONF_STATUS.swigToEnum(statusCode) == TANG_JOINCONF_STATUS.JOINCONFSTATUS_GETCONFINFOFAILED ||
                TANG_JOINCONF_STATUS.swigToEnum(statusCode) == TANG_JOINCONF_STATUS.JOINCONFSTATUS_GETUSERINFOFAILED) {
            //start conf failed
            //fix mantis bug 0004116
            changeCallState(CallState.CallState_Ending);
            changeCallState(CallState.CallState_Ended);
            notifyFinishCall(0);
        }
    }

    public void onConfLeft(int statusCode) {
        if (TANG_LEFTCONF_REASON.swigToEnum(statusCode) == TANG_LEFTCONF_REASON.LEFTCONFREASON_NETWORKDISCONNECT) {
            this.changeCallState(CallState.CallState_Disconnected);
        } else {
            //this.closeAllVideo(false);
            this.stopCountDuration();
            this.changeCallState(CallState.CallState_Ended);
            notifyFinishCall(0);
            this.clearData();
        }
    }

    public void onSessionErrorHandle(CGNetTangSessionErrorInfo pErrorInfo) {

        if (pErrorInfo == null) {
            return;
        }

        if (m_phoneState == PhoneState.PhoneState_Connecting &&
                pErrorInfo.getSessionType() == GNetTangSessionType.TMC_SESSIONTYPE_AUDIO && pErrorInfo.getMessageID() == 0) {
            m_phoneState = PhoneState.PhoneState_Idle;
            m_strPhoneCallNumber = "";
            if (getDelegatePrivate() != null) {
                getDelegatePrivate().onPhoneStateChanged(m_phoneState);
            }
            this.onPhoneCallResult(false);
        }
    }

    /**
     * @param joinKey
     * @brief 启动call
     */
    public void startCallByJoinKey(String workplusVoipMeetingId, @NonNull String joinKey) {
        if (mMeetingController.isValid()) {
            return;
        }

        if (mCallSelf == null) {
            return;
        }
        //Ranger fix mantis bug-0003444 bug-0003440
        if (getCallState() == CallState.CallState_Ended) {
            //    notifyFinishCall(0); //need't notify here
            return;
        }

        this.joinKey = joinKey;

        boolean bPeerCallByPhone = !this.isGroupChat() && mCallSelf.getUserType() == UserType.Originator && mCallPeer != null && !TextUtils.isEmpty(mCallPeer.getBindPhoneNumber());
        if (this.isGroupChat() || mCallSelf.getUserType() == UserType.Recipient || bPeerCallByPhone) {

            changeCallState(CallState.CallState_StartCall);
            mMeetingController.joinConferenceWithJoinKey(joinKey);

        } else if (!isGroupChat() &&
                mCallSelf != null && mCallSelf.getUserType() == UserType.Originator &&
                this.joinKey.length() > 0 &&
                getCallState() == CallState.CallState_Init) { //Ranger fix mantis bug-0003444 bug-0003440

            changeCallState(CallState.CallState_StartCall);
            this.mMeetingController.joinConferenceWithJoinKey(this.joinKey);
        }
    }

    /**
     * @brief 结束call
     */
    public void stopCall() {
        if (mCallSelf == null) {
            return;
        }

        CallState oldState = this.mCallState;
        stopCountDuration();
        stopTimerWaitPeer();

        stopPhone();
        stopVOIP();
        desktopStopView();

        int nRet = -1;
        changeCallState(CallState.CallState_Ending);
        if (isGroupChat()) {
            if (mMeetingController.isValid()) {
                this.closeAllVideo(false);
                if (isAllUsersLeft()) {
                    nRet = mMeetingController.endConf();
                } else {
                    nRet = mMeetingController.leaveConf();
                }
                startTimerWaitMeetingEnd(); //wait meeting end timeout
            }
        } else {
            if (mMeetingController.isValid()) {
                this.closeAllVideo(false);
                nRet = mMeetingController.endConf();
                startTimerWaitMeetingEnd(); //wait meeting end timeout
            }
        }

        if (nRet != 0) {
            changeCallState(CallState.CallState_Ended);
        }


        if (isGroupChat()) {
            VoipManager.getInstance().getTimeController().cancelAll();
        }

        VoipNoticeManager.getInstance().callingNotificationCancel(BaseApplicationLike.baseContext);

    }

    public void reconnectCall() {
        if (mMeetingController.isValid()) {
            changeCallState(CallState.CallState_ReConnecting);
            if (mMeetingController.reconnectConf() != 0) { //fix the reconnect issue
                changeCallState(CallState.CallState_Disconnected);
            }
        }
    }

    public void onConfReconnected() {
        if (getCallState() == CallState.CallState_ReConnecting || getCallState() == CallState.CallState_Disconnected) {
            changeCallState(CallState.CallState_Calling);
        }
    }

    /**
     * @param group
     * @brief 1对1切换为群聊
     */
    public void switchToGroup(VoipMeetingGroup group) {
        if (group == null) {
            return;
        }
        this.mMeetingGroup = group;
        //this.mCallPeer = null;  //fix switch peer pure phone call to mMeetingGroup call issue
        m_bNeedSwitchVideo = false;  //fix bug B160113-005

        List<VoipMeetingMemberWrapData> userList = getUserItemDataList();
        if (userList != null && userList.size() > 0) {
            for (VoipMeetingMemberWrapData userItem : userList) {
                if (userItem == null) {
                    continue;
                }
                getUserItemDataByUserId(userItem.getUserID(), userItem.getUserDomianId());
            }
        }

        for (VoipMeetingMember entity : this.mMeetingGroup.mParticipantList) {
            if (entity == null) {
                continue;
            }
            getUserItemDataByUserId(entity.mUserId, entity.mDomainId);
        }

        //fix meidi switch to group issue, add missed conf users
        if(mMeetingController.isValid()) {
            long nConfUserCount = mMeetingController.getUserCount();
            for (int i = 0; i < nConfUserCount; i++) {
                IGNetTangUser tangUser = mMeetingController.getUserByIndex(i);
                if (tangUser == null) {
                    continue;
                }
                String strUserId = IGnetTangUserHelper.getUserId(tangUser);
                VoipMeetingMemberWrapData itemData = checkUserExistInUserItemDataList(strUserId, userList);
                if (itemData != null) {
                    continue;
                }

                itemData = new VoipMeetingMemberWrapData();

                String domainId = VoipManager.getInstance().findDomainId(mWorkplusVoipMeetingId, strUserId);

                combineUserItemData(strUserId, domainId, itemData);
                userList.add(itemData);
            }
        }
        //end

        if (getDelegatePrivate() != null) {
            getDelegatePrivate().onUserListUpdated();
        }
    }

    /**
     * @param memberArray UserEntity Array
     * @brief 已经是群聊，新增参加人
     */
    public void addParticipants(final ArrayList<VoipMeetingMember> memberArray) {

        mHandler.post(() -> {
            if (mMeetingGroup == null) {
                return;
            }
            for (VoipMeetingMember invitedMembers : memberArray) {
                if (invitedMembers == null) {
                    continue;
                }

                boolean bFound = false;
                for (VoipMeetingMember participant : mMeetingGroup.mParticipantList) {
                    if (participant == null) {
                        continue;
                    }

                    if (invitedMembers.mUserId.equals(participant.mUserId)) {
                        bFound = true;

                        participant.setUserStatus(UserStatus.UserStatus_NotJoined);// switch status

                        break;
                    }
                }

                if (!bFound) {
                    mMeetingGroup.mParticipantList.add(invitedMembers);
                }

                getUserItemDataByUserId(invitedMembers.mUserId, invitedMembers.mDomainId);

            }

            if (getDelegatePrivate() != null) {
                getDelegatePrivate().onUserListUpdated();
            }
        });

    }

    public void removeParticipantAndRefreshUI(String userId) {
        List<String> singleList = new ArrayList<>();
        singleList.add(userId);

        removeParticipantsAndRefreshUI(singleList);
    }

    public void removeParticipantsAndRefreshUI(List<String> userIdList) {
        if (mMeetingGroup == null) {
            return;
        }

        removeParticipants(userIdList);

        if (getDelegatePrivate() != null) {
            getDelegatePrivate().onUserListUpdated();
        }

    }

    public void removeParticipants(List<String> userIdList) {
        for (String userId : userIdList) {
            removeParticipant(userId);
        }
    }

    public void removeParticipant(String userId) {
        VoipMeetingMember removedMember = null;
        for (VoipMeetingMember member : mMeetingGroup.mParticipantList) {
            if (userId.equals(member.mUserId)) {
                removedMember = member;
                break;
            }
        }

        if (null != removedMember) {
            mMeetingGroup.mParticipantList.remove(removedMember);
        }

        removeUserItemData(userId);
    }

    /**
     * 群组语音, 切换状态
     */
    public void setParticipantStatusAndRefreshUI(@NonNull VoipMeetingMember voipMeetingMember, UserStatus userStatus) {
        List<VoipMeetingMember> singleList = new ArrayList<>();
        singleList.add(voipMeetingMember);

        setParticipantsStatusAndRefreshUI(singleList, userStatus);
    }

    /**
     * 群组语音, 切换状态
     */
    public void setParticipantsStatusAndRefreshUI(List<VoipMeetingMember> voipMeetingMembers, UserStatus userStatus) {
        if (mMeetingGroup == null) {
            return;
        }

        setParticipantStatus(voipMeetingMembers, userStatus);


        if (getDelegatePrivate() != null) {
            getDelegatePrivate().onUserListUpdated();
        }
    }

    public void setParticipantStatus(List<VoipMeetingMember> voipMeetingMembers, UserStatus userStatus) {
        for (VoipMeetingMember voipMeetingMember : voipMeetingMembers) {
            boolean bFound = false;
            for (VoipMeetingMember participant : mMeetingGroup.mParticipantList) {
                if (participant == null) {
                    continue;
                }

                if (voipMeetingMember.mUserId.equals(participant.mUserId)) {
                    bFound = true;

                    participant.setUserStatus(userStatus);// switch status

                    break;
                }
            }

            if (!bFound) {
                voipMeetingMember.setUserStatus(userStatus);
                mMeetingGroup.mParticipantList.add(voipMeetingMember);
            }

            getUserItemDataByUserId(voipMeetingMember.mUserId, voipMeetingMember.mDomainId);
        }

    }

    /**
     * @param meetingMember UserEntity
     * @brief 刷新名称或avatar
     */
    public void refreshUserProfile(VoipMeetingMember meetingMember) {
        if (mCallSelf == null || (mMeetingGroup == null && mCallPeer == null) || meetingMember == null) {
            return;
        }

        VoipMeetingMember userFind = null;

        if (meetingMember.mUserId.equals(mCallSelf.mUserId)) {
            userFind = mCallSelf;
        } else {
            if (mMeetingGroup != null) {
                for (VoipMeetingMember entity : mMeetingGroup.mParticipantList) {
                    if (entity == null) {
                        continue;
                    }

                    if (meetingMember.mUserId.equals(entity.mUserId)) {
                        userFind = entity;
                        break;
                    }
                }
            } else {
                if (meetingMember.mUserId.equals(mCallPeer.mUserId)) {
                    userFind = mCallPeer;
                }
            }
        }

        if (userFind == null) {
            return;
        }

        boolean bChanged = false;
        if (userFind.mAvatar != meetingMember.mAvatar) {
            userFind.mAvatar = (meetingMember.mAvatar);
            bChanged = true;
        }

        if (meetingMember.mShowName != null && !meetingMember.mShowName.equals(userFind.mShowName)) {
            userFind.mShowName = (meetingMember.mShowName);
            bChanged = true;
        }

        if (bChanged) {
            if (getDelegatePrivate() != null) {
                getUserItemDataByUserId(meetingMember.mUserId, meetingMember.mDomainId);
                getDelegatePrivate().onUserListUpdated();
            }
        }
    }

    @Override
    public void tipToast(String tip) {
        if (getDelegatePrivate() != null) {
            getDelegatePrivate().onUserBusy(tip);
        }
    }

    /**
     * @param group GroupEntity
     * @brief 刷新名称或avatar
     */
    public void refreshGroupProfile(VoipMeetingGroup group) {
        if (this.mMeetingGroup == null || group == null) {
            return;
        }
        boolean bChanged = false;
        if (this.mMeetingGroup.mAvatar != group.mAvatar) {
            this.mMeetingGroup.mAvatar = (group.mAvatar);
            bChanged = true;
        }

        if (!this.mMeetingGroup.mName.equals(group.mName)) {
            this.mMeetingGroup.mName = (group.mName);
            bChanged = true;
        }

        if (bChanged) {
            if (getDelegatePrivate() != null) {
                getDelegatePrivate().onUserListUpdated();
            }
        }
    }

//    /**
//     * 处理消息: 如接收人暂时忙...
//     *
//     * @param messageType, String strContent
//     */
//    public void handleMessage(MessageType messageType, String userId, String domainId) {
//        if (mCallSelf == null) {
//            return;
//        }
//
//        if (messageType == MessageType.MessageType_InAnotherCall) {
//            if (!isGroupChat() && mCallSelf.getUserType() == UserType.Originator) {
//                stopCall();
//            }
//        } else if (messageType == MessageType.MessageType_Accept) {
//            if (mCallSelf != null && userId.equals(mCallSelf.mUserId) && getCallState() == CallState.CallState_Init) {
//                stopCall();
//
//            } else if (!isGroupChat() &&
//                    mCallSelf != null && mCallSelf.getUserType() == UserType.Originator &&
//                    this.joinKey.length() > 0 &&
//                    getCallState() == CallState.CallState_Init) { //Ranger fix mantis bug-0003444 bug-0003440
//
//                changeCallState(CallState.CallState_StartCall);
//                this.mMeetingController.joinConferenceWithJoinKey(this.joinKey);
//            }
//        } else if (messageType == MessageType.MessageType_Cancelled) {
//            //fix bug B160115-009
//            if (!isGroupChat() &&
//                    mCallSelf != null && mCallSelf.getUserType() == UserType.Recipient && mCallPeer != null && mCallPeer.mUserId.equals(userId)) { //Ranger fix mantis bug-0003444 bug-0003440
//                stopCall();
//                //Ranger fix mantis bug-0003444 bug-0003440
//                if (getCallState() == CallState.CallState_Ended) {
//                    notifyFinishCall(0);
//                    return;
//                }
//            }
//        } else if (messageType == MessageType.MessageType_Rejected) {
//            if (mCallSelf != null && userId.equals(mCallSelf.mUserId) && getCallState() == CallState.CallState_Init) {
//                stopCall();
//            } else if (!isGroupChat() && mCallSelf.getUserType() == UserType.Originator) {
//                // play du du du~ 忙音
//                isBusyTonePlayed = true;
//                SoundUtil.getInstance().playSound(SoundUtil.KEY_BUSYTONE, 0);
//                stopCall();
//                if (getCallState() == CallState.CallState_Ended) { //Ranger fix call status bug
//                    notifyFinishCall(0);
//                    return;
//                }
//            } else {
//                VoipMeetingMember userFind = null;
//                if (mMeetingGroup != null) {
//                    for (VoipMeetingMember entity : mMeetingGroup.mParticipantList) {
//                        if (entity == null) {
//                            continue;
//                        }
//                        if (userId.equals(entity.mUserId)) {
//                            userFind = entity;
//                            break;
//                        }
//                    }
//                } else {
//                    if (userId.equals(mCallPeer.mUserId)) {
//                        userFind = mCallPeer;
//                    }
//                }
//
//                if (userFind == null) {
//                    return;
//                }
//
//                userFind.setUserStatus(UserStatus.UserStatus_Rejected);
//                if (getDelegatePrivate() != null) {
//                    getUserItemDataByUserId(userId, domainId);
//                    getDelegatePrivate().onUserListUpdated();
//                }
//            }
//        }
//
//    }

    private void notifyFinishCall(int meetingMemberCount) {
        if (getDelegate() != null) {
            if (!StringUtils.isEmpty(LoginUserInfo.getInstance().getLoginUserAccessToken(sAppContext))) {
                ActivityManagerHelper.finishAll(); // finish all of call relate activity
            }
            getDelegate().onFinishCall(meetingMemberCount);
            setDelegate(null); // avoid duplicate call onFinishCall
        }
    }

    /**
     * 获取当前会议ID
     */
    public long getQsyMeetingId() {
        if (mMeetingController.isValid()) {
            return mMeetingController.getConfID();
        }
        return 0;
    }

    private List<VoipMeetingMemberWrapData> generateUserItemDataList() {
        List<VoipMeetingMemberWrapData> list = new ArrayList<>();

        if (mCallSelf == null) {
            return list;
        }

        VoipMeetingMemberWrapData itemData = new VoipMeetingMemberWrapData();
        itemData.setUserEntity(mCallSelf);
        combineUserItemData(mCallSelf.mUserId, mCallSelf.mDomainId, itemData);
        list.add(itemData);

        if (mMeetingController.isValid()) {
            long nConfUserCount = mMeetingController.getUserCount();
            for (int i = 0; i < nConfUserCount; i++) {
                IGNetTangUser tangUser = mMeetingController.getUserByIndex(i);
                if (tangUser == null) {
                    continue;
                }
                String strUserId = IGnetTangUserHelper.getUserId(tangUser);

                itemData = checkUserExistInUserItemDataList(strUserId, list);
                if (itemData != null) {
                    continue;
                }

                itemData = new VoipMeetingMemberWrapData();

                String domainId = VoipManager.getInstance().findDomainId(mWorkplusVoipMeetingId, strUserId);

                combineUserItemData(strUserId, domainId, itemData);
                list.add(itemData);
            }
        }


        if (mMeetingGroup != null) {
            List<VoipMeetingMember> participants = mMeetingGroup.mParticipantList;
            for (VoipMeetingMember meetingMember : participants) {
                itemData = checkUserExistInUserItemDataList(meetingMember.mUserId, list);
                if (itemData != null) {
                    continue;
                }

                itemData = new VoipMeetingMemberWrapData();
                combineUserItemData(meetingMember.mUserId, meetingMember.mDomainId, itemData);
                list.add(itemData);
            }

        } else {
            if (mCallPeer != null) {
                itemData = checkUserExistInUserItemDataList(mCallPeer.mUserId, list); //fix bug B160115-004
                if (itemData == null) {
                    itemData = new VoipMeetingMemberWrapData();
                    itemData.setUserEntity(mCallPeer);
                    combineUserItemData(mCallPeer.mUserId, mCallPeer.mDomainId, itemData);
                    list.add(itemData);
                }
            }
        }

        return list;
    }

    private void removeUserItemData(String userId) {
        List<VoipMeetingMemberWrapData> wrapDataList = getUserItemDataList();
        if (!ListUtil.isEmpty(wrapDataList)) {
            VoipMeetingMemberWrapData itemFind = checkUserExistInUserItemDataList(userId, wrapDataList);

            if (null != itemFind) {
                wrapDataList.remove(itemFind);
            }


        }
    }

    @Nullable
    private VoipMeetingMemberWrapData checkUserExistInUserItemDataList(String userId, List<VoipMeetingMemberWrapData> list) {
        if (list == null) {
            return null;
        }
        VoipMeetingMemberWrapData itemFind = null;
        for (VoipMeetingMemberWrapData item : list) {
            if (item == null) {
                continue;
            }
            if (item.getUserEntity().mUserId.equals(userId)) {
                itemFind = item;
                break;
            }
        }

        return itemFind;
    }

    private boolean combineUserItemData(String userId, String domainId, VoipMeetingMemberWrapData itemData) {
        if (itemData == null) {
            return false;
        }

        if (mCallSelf == null) {
            return false;
        }

        VoipMeetingMember meetingMemberMatched = null;

        if (mCallSelf.mUserId.equals(userId)) {
            itemData.setIsMySelf(true);
            meetingMemberMatched = mCallSelf;
        } else {
            itemData.setIsMySelf(false);

            if (isGroupChat()) {
                List<VoipMeetingMember> participants = mMeetingGroup.mParticipantList;
                for (VoipMeetingMember meetingMember : participants) {
                    if (meetingMember.mUserId.equals(userId)) {
                        meetingMemberMatched = meetingMember;
                        break;
                    }
                }
            } else {
                if (mCallPeer.mUserId.equals(userId)) {
                    meetingMemberMatched = mCallPeer;
                }
            }
        }

        //not found, call delegate to get the entity
        final String tempUserId = userId;

        if (meetingMemberMatched == null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (getDelegate() != null) {
                        getDelegate().onStartGetUserProfile(tempUserId, domainId);
                    }
                }
            });

            meetingMemberMatched = new VoipMeetingMember(userId, domainId, UserType.Recipient, userId, null, UserStatus.UserStatus_NotJoined);
            if (mMeetingGroup != null) {
                mMeetingGroup.mParticipantList.add(meetingMemberMatched); // add to list
            } else {
                if (mCallPeer == null) {
                    mCallPeer = meetingMemberMatched; // set peer
                } else { //fatal error, should Call switchtoGroup first
                    return false;
                }
            }
        } else {
            if (meetingMemberMatched.mUserId == null || TextUtils.isEmpty(meetingMemberMatched.mShowName)) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (getDelegate() != null) {
                            getDelegate().onStartGetUserProfile(tempUserId, domainId);
                        }
                    }
                });
            }
        }

        //set Conference user status
        {
            IGNetTangUser tangUser = null;
            if (mMeetingController.isValid()) {
                tangUser = mMeetingController.getUserByName(userId);
                if (tangUser == null) {
                    String strBindPhoneNumber = getBindPhoneNumberbyUserId(userId);
                    tangUser = mMeetingController.getUserByName(strBindPhoneNumber);
                }
            }
            if (tangUser == null) {
                itemData.setConfUserId(0);
                itemData.setIsMute(false);
                itemData.setVoiceType(VoiceType.NONE);
                itemData.setIsSpeaking(false);
                itemData.setIsVideoShared(false);
                itemData.setIsDesktopShared(false);
                if (meetingMemberMatched != null) {
                    UserStatus currentStatus = meetingMemberMatched.getUserStatus();
                    if (currentStatus == UserStatus.UserStatus_Joined) {
                        meetingMemberMatched.setUserStatus(UserStatus.UserStatus_Left);
                    } else {
                        meetingMemberMatched.setUserStatus(currentStatus);
                    }
                }
            } else {
                itemData.setConfUserId(tangUser.getUserID());
                //#define TANGUSERAUDIOSTATUS_NONE		0
                //#define TANGUSERAUDIOSTATUS_VOIPENABLE		1
                //#define TANGUSERAUDIOSTATUS_VOIPMUTEBYSELF	2
                //#define TANGUSERAUDIOSTATUS_VOIPMUTEBYHOST	3
                //#define TANGUSERAUDIOSTATUS_PSTNENABLE		4
                //#define TANGUSERAUDIOSTATUS_PSTNMUTEBYSELF	5
                //#define TANGUSERAUDIOSTATUS_PSTNMUTEBYHOST	6
                long nAudioStatus = tangUser.getAudioStatus(); //0, none;1,voip enable;2,voip mute by self; 3, voip mute by host;4,phone enable;5,phone mute by myself; 6,phone mute by host;
                if (nAudioStatus == 1) {
                    itemData.setVoiceType(VoiceType.VOIP);
                    itemData.setIsMute(false);
                } else if (nAudioStatus == 2) {
                    itemData.setVoiceType(VoiceType.VOIP);
                    itemData.setIsMute(true);
                } else if (nAudioStatus == 3) {
                    itemData.setVoiceType(VoiceType.VOIP);
                    itemData.setIsMute(true);
                } else if (nAudioStatus == 4) {
                    itemData.setIsMute(false);
                    itemData.setVoiceType(VoiceType.TEL);
                } else if (nAudioStatus == 5) {
                    itemData.setVoiceType(VoiceType.TEL);
                    itemData.setIsMute(true);
                } else if (nAudioStatus == 6) {
                    itemData.setVoiceType(VoiceType.TEL);
                    itemData.setIsMute(true);
                } else {
                    itemData.setVoiceType(VoiceType.NONE);
                    itemData.setIsMute(false); //if None audio, set mute is false, to avoid UI issue.
                }

                CGNetTangVariant varIsSpeaking = tangUser.getProperty("isSpeaking");
                if (varIsSpeaking == null || varIsSpeaking.getUintVal() == 0) {
                    itemData.setIsSpeaking(false);
                } else {
                    itemData.setIsSpeaking(true);
                }

                CGNetTangVariant varIsVideoShared = tangUser.getProperty("videoShareStatus");
                if (varIsVideoShared == null || varIsVideoShared.getIntVal() == 0) {
                    itemData.setIsVideoShared(false);
                } else {
                    itemData.setIsVideoShared(true);
                }

                if (haveDesktopShare() && tangUser.getUserID() == m_nDesktopShareConfUserId) {
                    itemData.setIsDesktopShared(true);
                } else {
                    itemData.setIsDesktopShared(false);
                }

                //update user status
                if (meetingMemberMatched != null) {
                    meetingMemberMatched.setUserStatus(UserStatus.UserStatus_Joined);
                }
            }
        }

        itemData.getUserEntity().mUserId = (meetingMemberMatched.mUserId);
        itemData.getUserEntity().mAvatar = (meetingMemberMatched.mAvatar);
        itemData.getUserEntity().mShowName = (meetingMemberMatched.mShowName);
        itemData.getUserEntity().setUserType(meetingMemberMatched.getUserType());
        itemData.getUserEntity().setUserStatus(meetingMemberMatched.getUserStatus());

        return true;
    }

    private long getMyselfConfUserId() {
        if (!mMeetingController.isValid()) {
            return 0;
        }
        if (mCallSelf == null) {
            return 0;
        }
        long confUserId = getConUserIdByUserId(mCallSelf.mUserId);
        return confUserId;
    }

    private long getConUserIdByUserId(String userId) {
        if (!mMeetingController.isValid()) {
            return 0;
        }
        if (mCallSelf == null) {
            return 0;
        }
        long confUserId = 0;
        long nConfUserCount = mMeetingController.getUserCount();
        for (int i = 0; i < nConfUserCount; i++) {
            IGNetTangUser tangUser = mMeetingController.getUserByIndex(i);
            if (tangUser == null) {
                continue;
            }

            if (tangUser.getUserType() == 0) {
                if (tangUser.getUserName().equals(userId)) {
                    confUserId = tangUser.getUserID();
                }
            } else if (tangUser.getUserType() == 1) {
                String strPhoneNumber = getBindPhoneNumberbyUserId(userId);
                if (!TextUtils.isEmpty(strPhoneNumber) && tangUser.getUserName().equals(strPhoneNumber)) {
                    confUserId = tangUser.getUserID();
                }
            }

            if (confUserId != 0) {
                break;
            }
        }
        return confUserId;
    }

    private String getUserIdByConUserId(long confUserId) {
        if (!mMeetingController.isValid()) {
            return "";
        }
        if (mCallSelf == null) {
            return "";
        }
        String userId = "";
        long nConfUserCount = mMeetingController.getUserCount();
        for (int i = 0; i < nConfUserCount; i++) {
            IGNetTangUser tangUser = mMeetingController.getUserByIndex(i);
            if (tangUser == null) {
                continue;
            }
            if (tangUser.getUserID() == confUserId) {
                userId = IGnetTangUserHelper.getUserId(tangUser);
            }
        }
        return userId;
    }

    private void changeCallState(CallState callState) {
        if (callState == CallState.CallState_Init) {
            stopCountDuration();
            this.m_elapsedCallingTime = 0;
        } else if (callState == CallState.CallState_Ended) {
            clearWorkplusMeetingInfo();

        } else if (callState == CallState.CallState_Calling) {
            m_bCallSucceeded = true;
        }
        if (this.mCallState != callState) {
            this.mCallState = callState;
            VoipManager.getInstance().setCallState(callState);

            if (getDelegatePrivate() != null) {
                getDelegatePrivate().onCallStateChanged(callState);
            }

            switch (callState) {
                case CallState_Idle:
                    break;
                case CallState_Init:
                    // 播放呼响铃
                    SoundUtil.getInstance().playSound(SoundUtil.KEY_RING, -1);
                    break;
                case CallState_StartCall:
                    break;
                case CallState_Waiting:
                    break;
                case CallState_Calling:
                    // 发起方才有震动+声音提醒；接收方只有声音提醒
                    if (getMySelf().getUserType() == UserType.Originator) {
                        VibratorUtil.Vibrate(sAppContext, 100);
                    }
                    SoundUtil.getInstance().playSound(SoundUtil.KEY_RINGIN, 0);
                    break;
                case CallState_Disconnected:
                    break;
                case CallState_ReConnecting:
                    break;
                case CallState_Ended:
                    // 播放通话结束提示音
                    if (!isBusyTonePlayed) {
                        SoundUtil.getInstance().playSound(SoundUtil.KEY_RINGIN, 0);
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SoundUtil.getInstance().release();
                        }
                    }, 1000);
                    break;
                default:
                    break;
            }
        }


    }

    public boolean isAllUsersLeft() {
        List<VoipMeetingMemberWrapData> userList = getUserItemDataList();
        if (userList == null || userList.size() == 0) {
            return true;
        }

        boolean bHaveNotLeft = false;

        for (VoipMeetingMemberWrapData item : userList) {
            if (item == null) {
                continue;
            }
            if (item.isMySelf()) {
                continue;
            }

            if (item.getUserStatus() != UserStatus.UserStatus_Left &&
                    item.getUserStatus() != UserStatus.UserStatus_Rejected) {
                bHaveNotLeft = true;
                break;
            }
        }
        return !bHaveNotLeft;
    }

    /**
     * @brief 开始计时
     */
    private void startCountDuration() {
        stopCountDuration();
        m_timerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(() -> {
                    m_elapsedCallingTime = System.currentTimeMillis() / 1000 - m_startTimeMillis;
                    if (getDelegatePrivate() != null) {
                        getDelegatePrivate().onCallingTimeElpased(m_elapsedCallingTime);
                    }
                });
            }
        };

        m_timer = new Timer();
        m_elapsedCallingTime = 0;
        m_startTimeMillis = System.currentTimeMillis() / 1000;
        if (getDelegatePrivate() != null) {
            getDelegatePrivate().onCallingTimeElpased(m_elapsedCallingTime);
        }
        // 设置计时任务每一秒执行一次
        m_timer.scheduleAtFixedRate(m_timerTask, 0L, ONE_SECOND);
    }

    /**
     * @brief 停止计时
     */
    private void stopCountDuration() {
        // 取消当前计时任务
        if (m_timerTask != null) {
            m_timerTask.cancel();
            m_timerTask = null;
        }

        // 清除计时器
        if (m_timer != null) {
            // 将取消的计时任务回收
            m_timer.purge();
            // 取消计时器内所有任务
            m_timer.cancel();
            m_timer = null;
        }
    }

    private void startTimerWaitPeer() {
        stopTimerWaitPeer();
        m_timerTaskWaitPeer = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (getCallState() == CallState.CallState_Init || getCallState() == CallState.CallState_StartCall || getCallState() == CallState.CallState_Waiting) {
                            // play du du du~ 忙音
                            isBusyTonePlayed = true;
                            SoundUtil.getInstance().playSound(SoundUtil.KEY_BUSYTONE, 0);
                            stopCall();
                            if (getDelegate() != null) {
                                getDelegate().onTimeOut();
                            }
                        }

                        stopTimerWaitPeer();
                    }
                });
            }
        };

        m_timerWaitPeer = new Timer();
        m_timerWaitPeer.schedule(m_timerTaskWaitPeer, 60 * ONE_SECOND);
    }

    private void stopTimerWaitPeer() {
        // 取消当前计时任务
        if (m_timerTaskWaitPeer != null) {
            m_timerTaskWaitPeer.cancel();
            m_timerTaskWaitPeer = null;
        }

        // 清除计时器
        if (m_timerWaitPeer != null) {
            // 将取消的计时任务回收
            m_timerWaitPeer.purge();
            // 取消计时器内所有任务
            m_timerWaitPeer.cancel();
            m_timerWaitPeer = null;
        }
    }

    private void startTimerWaitMeetingEnd() {
        stopTimerWaitMeetingEnd();
        m_timerTaskWaitMeetingEnd = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mMeetingController.isValid()) {
                            onConfLeft(TANG_LEFTCONF_REASON.LEFTCONFREASON_SELFLEFT.swigValue());
                            mMeetingController.releaseConference();
                        }

                        stopTimerWaitMeetingEnd();
                    }
                });
            }
        };

        m_timerWaitMeetingEnd = new Timer();
        m_timerWaitMeetingEnd.schedule(m_timerTaskWaitMeetingEnd, 2 * ONE_SECOND);
    }

    private void stopTimerWaitMeetingEnd() {
        // 取消当前计时任务
        if (m_timerTaskWaitMeetingEnd != null) {
            m_timerTaskWaitMeetingEnd.cancel();
            m_timerTaskWaitMeetingEnd = null;
        }

        // 清除计时器
        if (m_timerWaitMeetingEnd != null) {
            // 将取消的计时任务回收
            m_timerWaitMeetingEnd.purge();
            // 取消计时器内所有任务
            m_timerWaitMeetingEnd.cancel();
            m_timerWaitMeetingEnd = null;
        }
    }

    /**
     * @brief 检测相机是否可用(只有当上次检测结果不可用, 才继续检测. 反之, 不检测)
     */
    public boolean checkCameraIsOpen() {
        if (mLastCheckCameraIsOpen) {
            return mLastCheckCameraIsOpen;
        }
        mLastCheckCameraIsOpen = CameraUtil.getCameraPermission();
        return mLastCheckCameraIsOpen;
    }

    public void setWorkplusMeetingInfo(String meetingId, MeetingInfo meetingInfo, VoipType voipType) {
        setWorkplusVoipMeetingId(meetingId);
        setMeetingInfo(meetingInfo);
        setVoipType(voipType);
    }

    public void setWorkplusVoipMeetingId(String meetingId) {
        this.mWorkplusVoipMeetingId = meetingId;
    }

    @Override
    public boolean isCurrentVoipMeetingValid() {
        return null != mCallSelf;
    }

    public String getWorkplusVoipMeetingId() {
        return this.mWorkplusVoipMeetingId;
    }

    public void setMeetingInfo(MeetingInfo meetingInfo) {
        this.mMeetingInfo = meetingInfo;
    }

    public MeetingInfo getMeetingInfo() {
        return this.mMeetingInfo;
    }

    public void setVoipType(VoipType voipType) {
        this.mVoipType = voipType;
    }

    public VoipType getVoipType() {
        return this.mVoipType;
    }

    public void onAudioSessionCreated() {
        AudioSessionController audioSessionController = mMeetingController.getAudioSession();
        if (audioSessionController != null) {
            audioSessionController.startVoip(); //start self voip
            startCallPeerPhone(); //start call peer by phone if needed
        }
    }

    public void startCallPeerPhone() {
        if (mCallPeer == null || TextUtils.isEmpty(mCallPeer.getBindPhoneNumber())) {
            return;
        }

        startPurePhone(mCallPeer.getBindPhoneNumber());
    }

    public void startPurePhone(String strPhoneNumber) {
        if (!mMeetingController.isValid()) {
            return;
        }
        AudioSessionController audioSessionController = mMeetingController.getAudioSession();
        if (audioSessionController != null) {
            PhoneCallNum phoneCallNum = new PhoneCallNum();
            phoneCallNum.setNUserID(0);
            phoneCallNum.setSPhoneNum(strPhoneNumber);
            phoneCallNumArray phoneCalls = new phoneCallNumArray(1);
            phoneCalls.setitem(0, phoneCallNum);
            audioSessionController.call(phoneCalls, 1);
        }
    }

    private String getUserIdByPhoneNumber(String strPhoneNumber) {

        if (mCallPeer == null || TextUtils.isEmpty(strPhoneNumber)) {
            return "";
        }

        if (!strPhoneNumber.equals(mCallPeer.getBindPhoneNumber())) {
            return "";
        }

        return mCallPeer.mUserId;
    }

    private String getBindPhoneNumberbyUserId(String strUserId) {

        if (mCallPeer == null || TextUtils.isEmpty(strUserId)) {
            return "";
        }

        if (!strUserId.equals(mCallPeer.mUserId)) {
            return "";
        }

        return mCallPeer.getBindPhoneNumber();
    }


    public void bindPhoneUserAndDataUser(String userId) {
        String strPhoneNumber = getBindPhoneNumberbyUserId(userId);
        if (TextUtils.isEmpty(strPhoneNumber)) {
            return;
        }
        if (!mMeetingController.isValid()) {
            return;
        }

        long nDataUserConfUserId = 0;
        IGNetTangUser tangUser = mMeetingController.getUserByName(userId);
        if (tangUser != null) {
            nDataUserConfUserId = tangUser.getUserID();
        }
        if (nDataUserConfUserId == 0) {
            return;
        }

        long nPhoneUserConfUserId = 0;
        tangUser = mMeetingController.getUserByName(strPhoneNumber);
        if (tangUser != null) {
            nPhoneUserConfUserId = tangUser.getUserID();
        }
        if (nPhoneUserConfUserId == 0) {
            return;
        }

        AudioSessionController audioSessionController = mMeetingController.getAudioSession();
        if (audioSessionController != null) {
            audioSessionController.bind(nDataUserConfUserId, nPhoneUserConfUserId);
        }
    }


    public boolean checkUnbindedConfUserExist(IGNetTangUser tangUser) {
        if (!mMeetingController.isValid()) {
            return false;
        }
        String strUnbindedConfUser = "";
        if (tangUser.getUserType() == 0) {
            String strUserId = tangUser.getUserName();
            strUnbindedConfUser = getBindPhoneNumberbyUserId(strUserId);
        } else if (tangUser.getUserType() == 1) {
            String strPhoneNumber = tangUser.getUserName();
            strUnbindedConfUser = getUserIdByPhoneNumber(strPhoneNumber);
        }
        IGNetTangUser unbindedTangUser = mMeetingController.getUserByName(strUnbindedConfUser);
        return unbindedTangUser != null;
    }


    private void clearData() {
        Log.e("voip", "qsy sdk clear data");

        callType = CallType.CallType_Audio.value() | CallType.CallType_Video.value(); ///< 会议类型
        mCallState = CallState.CallState_Idle; ///< 呼状态
        VoipManager.getInstance().setCallState(CallState.CallState_Idle);

        mCallSelf = null; ///< 当前用户实体
        mCallPeer = null; ///< 一对一成员实体
        mMeetingGroup = null; ///< 群呼实体
        joinKey = ""; /// 会议吊起串
        //callBack = null; // should not clear this data
        //callBackPrivate = null;  // should not clear this data
        mMeetingController.releaseConference();
        mMeetingController = new VoipMeetingController();
        mUserItemDataList = null;
        mSpeakingUserItemDataList = null;
        currentShowingVideoUserId = "";
        currentShowingVideoMySelf = false;
        m_bNeedSwitchVideo = false;
        m_videoCallOpened = false;
        mVideoUserItemDataList = null;
        videoDeviceIndex = 1;
        m_strPhoneCallNumber = "";
        m_phoneState = PhoneState.PhoneState_Idle;
        mHandler = new Handler();
        mLastCheckCameraIsOpen = false;
        isBusyTonePlayed = false;
        clearWorkplusMeetingInfo();

        m_nDesktopShareConfUserId = 0;
        //m_timer = null; ///< 计时器
        //m_timerTask= null; ///< 执行的即时任务
    }

    private void clearWorkplusMeetingInfo() {
        setWorkplusVoipMeetingId(StringUtils.EMPTY);
        setMeetingInfo(null);
        setVoipType(null);
    }

}