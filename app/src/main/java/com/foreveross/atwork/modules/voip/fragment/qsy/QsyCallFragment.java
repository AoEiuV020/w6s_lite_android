package com.foreveross.atwork.modules.voip.fragment.qsy;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.PixelFormat;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.CallState;
import com.foreveross.atwork.infrastructure.model.voip.PhoneState;
import com.foreveross.atwork.infrastructure.model.voip.UserType;
import com.foreveross.atwork.infrastructure.model.voip.VoiceType;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMemberWrapData;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.rom.FloatWindowPermissionUtil;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.foreveross.atwork.modules.voip.activity.CallActivity;
import com.foreveross.atwork.modules.voip.activity.qsy.QsyCallActivity;
import com.foreveross.atwork.modules.voip.activity.qsy.SwitchVoiceActivity;
import com.foreveross.atwork.modules.voip.adapter.qsy.AudioUserListAdapter;
import com.foreveross.atwork.modules.voip.adapter.qsy.GroupVideoPagerAdapter;
import com.foreveross.atwork.modules.voip.component.qsy.DesktopViewer;
import com.foreveross.atwork.modules.voip.component.qsy.LazyViewPager;
import com.foreveross.atwork.modules.voip.component.qsy.TangVideoView;
import com.foreveross.atwork.modules.voip.fragment.VoipSelectModeFragment;
import com.foreveross.atwork.modules.voip.model.ViewMode;
import com.foreveross.atwork.modules.voip.support.qsy.Constants;
import com.foreveross.atwork.modules.voip.support.qsy.TangSDKInstance;
import com.foreveross.atwork.modules.voip.support.qsy.interfaces.ICallDelegate;
import com.foreveross.atwork.modules.voip.support.qsy.interfaces.ICallDelegatePrivate;
import com.foreveross.atwork.modules.voip.utils.qsy.PromptUtil;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.AvatarHelper;
import com.foreveross.atwork.utils.FloatWinHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by peng.xu on 2015/12/7.
 */
public class QsyCallFragment extends BackHandledFragment implements View.OnClickListener, ICallDelegatePrivate, LazyViewPager.OnPageChangeListener, View.OnTouchListener {

    private static final String TAG = "CallFragment";

    /**
     * 点击最小化按钮时, 申请 system_windows_overlay 权限
     */
    public final static int REQUEST_CODE_PERMISSION_WINDOWS_OVERLAY_ON_BTN = 1;

    /**
     * 按返回时, 申请 system_windows_overlay 权限
     */
    public final static int REQUEST_CODE_PERMISSION_WINDOWS_OVERLAY_ON_BACK = 2;

    private ImageView mBtnMinimizeWindow, videoMode_CameraSwitch, videoGroupMode_viewAllBtn, mIvHangupCall, mIvPickupCall, mBtnReplyMsg, mIvAvatar, noticeBottom,
            audioStateImg, mIvWaittingLoading, speakerButton, mBtnMute, mBtnSwitchVideo;
    private ListView audioUserListView;
    private TextView mTvVoipChatStatus, mTvShowName, videoEnterTV, userMsgTV, noticeMsgTV, controlNoticeTV, speakerDescTV, videoDescTV, muteDescTV, audioDescTV, mTvVideoUserName, mTvVoipCallTip, mTvHangupCall;
    private LinearLayout audioSingleLayout, buttomLayout, mLlControl, mLLCover, replyMsgLay, enterVideoAndDeskshareLay, dotContainer;
    private RelativeLayout topLay, mNoAnswerNoticeLay, mRlPoorNetworkNotice, noticeLay, audioContentLay, mRlPickUpCall, mRlHangUpCall, receiveVideoLay, viewdeskshareLay, receiveVideoBtn, viewdeskshareBtn;
    private RelativeLayout desktopViewRoot;
    private ViewGroup videoUserNameContainer;
    private DesktopViewer desktopViewer;
    private Animation rotateAnimation, showAnimation, hiddenAnimation;
    private FrameLayout videoP2pLay, videoGroupLay;
    private TangVideoView videoP2pSmallView;
    private TangVideoView videoP2pBigView;
    private Button audioSwitchBtn, mBtnInvite;
    private LazyViewPager groupVideoViewPager = null;
    private List<TangVideoView> groupVideoViews = new ArrayList<>(0);
    private Map<String, TangVideoView> groupVideoViewMap = new HashMap<>();
    private List<ImageView> dotImgs = new ArrayList<>(); ///< 分页符视图集合
    private GroupVideoPagerAdapter pagerAdapter = null;
    private LinearLayout speakingLay; ///< 正在说话的布局
    private TextView speakingNameTV; ///< 正在说话用户姓名文本框
    private boolean isFullScreenMode = false; // 是否全屏模式
    private boolean isVideoSwitchAudio = false; ///< 是否从群视频切回群音频
    private OrientationEventListener sensorEvent = null;
    private int nCurrentOrientaion = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;

    private AudioUserListAdapter mAdapter;

    // 当前View模式
    private ViewMode mViewMode;

    private SurfaceView mSurfaceLocal = null;
    private boolean mIsReconnecting = false;
    private int mRetriesTimes = 0;
    private int m_nVideoPageBeforeDrag = -1;
    private boolean mNeedResetDelegate = false;
    public static long lastClickTime;
    private PowerManager.WakeLock mWakelock;

    // 视频画面宽高比例
    private static float ratio = (float) 9.0 / (float) 16.0;
    private TimerTask mFullScreenTimerTask;
    private TimerTask mWaitAnswerTimerTask;
    private Timer mTimer;
    private int mFullScreenSecondsLeft = 5; // 切全屏的倒计时剩余时间
    private int mNoAnswerLayStayDuration = 30; // 无人答复提示逗留时间
    private int mWaitAnswerSecondsLeft = AtworkConfig.VOIP_MAX_WAIT_ANSWER_DURATION - mNoAnswerLayStayDuration; // 等待对方接听的倒计时剩余时间

    private static final int MSGWHAT_TIMER_SECOND_FULLSCREEN = 0x01;
    private static final int MSGWHAT_TIMER_SECOND_WAITANSWER = 0x02;
    private static boolean mAuoEnableLoudSpeaker = false; //only need auto enable once
    private static boolean mFirstInitCall = false; //delay show the bottom buttons for the first time

    private VoipType mVoipType = VoipType.VOICE;

    private Runnable mNoAnswerShowRunnable =  new Runnable() {
        @Override
        public void run() {
            mNoAnswerNoticeLay.setVisibility(View.GONE);
            onAutoStopCall();
        }
    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSGWHAT_TIMER_SECOND_FULLSCREEN:
                    if (mFullScreenSecondsLeft > 0) {
                        mFullScreenSecondsLeft--;
                    } else {
                        if (!isFullScreenMode) {
                            startHiddenAnimation();
                        }
                        stopFullScreenCountDown();
                    }
                    break;
                case MSGWHAT_TIMER_SECOND_WAITANSWER:
                    if (mWaitAnswerSecondsLeft > 0) {
                        LogUtil.e("voip", "waitting answer second left : " + mWaitAnswerSecondsLeft);
                        mWaitAnswerSecondsLeft--;

                    } else {
                        // prompt user
                        if (UserType.Originator.equals(getMySelf().getUserType())) {
                            mNoAnswerNoticeLay.setVisibility(View.VISIBLE);
                            //#mNoAnswerLayStayDuration 时间后, 自动消失
                            mHandler.postDelayed(mNoAnswerShowRunnable, mNoAnswerLayStayDuration * 1000);

                        } else {
                            onAutoStopCall();

                        }


                        stopWaitAnswerCountDown();
                    }
                    break;
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_call_qsy, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initListener();

        nCurrentOrientaion = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //enableSensor(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
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
        clearData();
        releaseWakeLock();
        super.onDestroy();
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

//                } else {
//                    onClickStopCall();
//
//                }


                }
            }
        }
    }

    /**
     * 最小化操作
     */
    public void doMinimize() {
        CallActivity.sIsOpening = false;
        LogUtil.e("voip", "set IsOpening");

        TangSDKInstance.getInstance().saveShowingVideo(-1);
        TangSDKInstance.getInstance().getDelegate().onHideView();
    }

    @Override
    protected boolean onBackPressed() {

        checkPopPermissionAndDoMinimize();

        return true;
    }


    public QsyCallFragment() {
        // must have an empty constructor
    }

    public static QsyCallFragment newInstance(Context appContext) {
        QsyCallFragment callFragment = new QsyCallFragment();
        TangSDKInstance.getInstance().init(appContext, AtWorkDirUtils.getInstance().getQsyVoipLOG());
        TangSDKInstance.getInstance().setDelegatePrivate(callFragment);
        callFragment.mNeedResetDelegate = true;

        return callFragment;
    }

    /**
     * @param callType     会议类型, CallType类型的组合
     * @param mySelfEntity 当前用户信息
     * @param peerEntity   一对一对方的信息
     * @brief 初始化1对1呼
     */
    public void initPeerCall(int callType, VoipMeetingMember mySelfEntity, VoipMeetingMember peerEntity) {
        if (peerEntity == null) { //fix crash
            return;
        }

        //delay call to let initView called
        this.mHandler.postDelayed(() -> {
            VoipMeetingMember peer = TangSDKInstance.getInstance().getPeer();
            if (peer == null) {
                return;
            }
            // 刷新对方的头像和名称
            String strPeerDisplayName = peer.mShowName;
            mTvShowName.setText(strPeerDisplayName);

            AvatarHelper.setUserInfoAvatar(peer, mIvAvatar);


        }, 10);

        mAuoEnableLoudSpeaker = true;
        mFirstInitCall = true;

        TangSDKInstance.getInstance().initPeerCall(callType, mySelfEntity, peerEntity);
    }

    /**
     * @param callType 会议类型, CallType类型的组合
     * @param group    组信息
     * @brief 初始化群呼
     */

    public void initGroupCall(int callType, VoipMeetingMember mySelfEntity, VoipMeetingGroup group) {
        if (group == null) { //fix crash
            return;
        }

        //delay call to let initView called
        this.mHandler.postDelayed(() -> {
            VoipMeetingGroup meetingGroup = TangSDKInstance.getInstance().getGroup();
            if (meetingGroup == null) {
                return;
            }

            mTvShowName.setText(meetingGroup.mName);

        }, 10);

        mAuoEnableLoudSpeaker = true;
        mFirstInitCall = true;

        TangSDKInstance.getInstance().initGroupCall(callType, mySelfEntity, group);
    }

    /**
     * @param callBack
     * @brief 注册回调
     */
    public void setDelegate(ICallDelegate callBack) {
        TangSDKInstance.getInstance().setDelegate(callBack);
    }

    /**
     * @param joinKey
     * @brief 启动call
     */
    public void startCallByJoinKey(String joinKey) {
        TangSDKInstance.getInstance().startCallByJoinKey(null, joinKey);
    }

    /**
     * @param group
     * @brief 1对1切换为群聊
     */
    public void switchToGroup(VoipMeetingGroup group) {
        TangSDKInstance.getInstance().switchToGroup(group);
    }

    /**
     * @param memberArray UserEntity Array
     * @brief 已经是群聊，新增参加人
     */
    public void addParticipants(ArrayList<VoipMeetingMember> memberArray) {
        TangSDKInstance.getInstance().addParticipants(memberArray);
    }

    /**
     * @param meetingMember UserEntity
     * @brief 刷新名称或avatar
     */
    public void refreshUserProfile(VoipMeetingMember meetingMember) {
        if (getMySelf() == null) { //fix crash
            return;
        }
        TangSDKInstance.getInstance().refreshUserProfile(meetingMember);

        if (TangSDKInstance.getInstance().isGroupChat()) {
            // 刷新群呼的用户列表
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
        } else {
            if (getPeer() == null) { //fix crash
                return;
            }
            // 刷新一对一的用户
            if (meetingMember.mUserId.equals(getPeer().mUserId)) {
                this.mTvShowName.setText(meetingMember.mShowName);
                AvatarHelper.setUserInfoAvatar(meetingMember, mIvAvatar);

            }
        }
    }

    /**
     * @param group GroupEntity
     * @brief 刷新名称或avatar
     */
    public void refreshGroupProfile(VoipMeetingGroup group, UserHandleInfo inviter) {
        TangSDKInstance.getInstance().refreshGroupProfile(group);
        if (null != inviter) {
            this.mTvShowName.setText(inviter.mShowName);
            AvatarHelper.setUserInfoAvatar(inviter, mIvAvatar);

        }

    }

    /**
     * 处理消息: 如接收人暂时忙...
     */
//    public void handleMessage(MessageType messageType, String userId, String strContent) {
//        TangSDKInstance.getInstance().handleMessage(messageType, userId, strContent);
//    }
    public CallState getCallState() {
        return TangSDKInstance.getInstance().getCallState();
    }

    public VoipMeetingMember getMySelf() {
        return TangSDKInstance.getInstance().getMySelf();
    }

    public VoipMeetingMember getPeer() {
        return TangSDKInstance.getInstance().getPeer();
    }

    public VoipMeetingGroup getGroup() {
        return TangSDKInstance.getInstance().getGroup();
    }

    public ICallDelegate getDelegate() {
        return TangSDKInstance.getInstance().getDelegate();
    }

    /**
     * @brief 获取当前会议ID
     */
    public long getQsyMeetingId() {
        return TangSDKInstance.getInstance().getQsyMeetingId();
    }

    @Override
    protected void findViews(View view) {
        // 头部最小化、title、前后置摄像头切换、查看成员、邀请按钮
        topLay = view.findViewById(R.id.common_top_bar);
        mBtnMinimizeWindow = view.findViewById(R.id.btn_minimize_window);
        mTvVoipChatStatus = view.findViewById(R.id.common_chat_status);
        mBtnInvite = view.findViewById(R.id.invite_btn);
        videoMode_CameraSwitch = view.findViewById(R.id.video_mode_camera_switch_btn);
        videoGroupMode_viewAllBtn = view.findViewById(R.id.video_group_mode_viewall_btn);

        // 接听、挂断按钮
        mRlHangUpCall = view.findViewById(R.id.rl_hangup_call);
        mRlPickUpCall = view.findViewById(R.id.rl_pickup_call);
        mIvHangupCall = view.findViewById(R.id.iv_hangup_call);
        mTvHangupCall = view.findViewById(R.id.tv_hangup_call);
        mIvPickupCall = view.findViewById(R.id.iv_pickup_call);

        // 底部区域
        buttomLayout = view.findViewById(R.id.buttom_area);

        // 接收视频画面、查看桌面共享区域
        enterVideoAndDeskshareLay = view.findViewById(R.id.enter_video_and_deskshare_area);
        receiveVideoLay = view.findViewById(R.id.video_enter_area);
        receiveVideoBtn = view.findViewById(R.id.video_enter_btn);
        videoEnterTV = view.findViewById(R.id.video_enter_tv);
        viewdeskshareLay = view.findViewById(R.id.desk_share_enter_area);
        viewdeskshareBtn = view.findViewById(R.id.desk_share_enter_btn);
        desktopViewRoot = view.findViewById(R.id.desktopViewRoot);

        // 控制静音、免提、切换语音、开视频的相关view
        mLlControl = view.findViewById(R.id.ll_control);
        mBtnMute = view.findViewById(R.id.btn_mute);
        speakerButton = view.findViewById(R.id.speaker_button);
        mBtnSwitchVideo = view.findViewById(R.id.video_button);
        audioSwitchBtn = view.findViewById(R.id.audio_btn);
        muteDescTV = view.findViewById(R.id.mute_checkbox_desc);
        speakerDescTV = view.findViewById(R.id.speaker_checkbox_desc);
        videoDescTV = view.findViewById(R.id.video_checkbox_desc);
        audioDescTV = view.findViewById(R.id.audio_btn_desc);

        // 页面提示相关
        mTvShowName = view.findViewById(R.id.tv_user_name);
        mNoAnswerNoticeLay = view.findViewById(R.id.no_answer_prompt_lay);
        mRlPoorNetworkNotice = view.findViewById(R.id.rl_network_poor_prompt);
//        userMsgTV = (TextView) view.findViewById(R.id.user_msg);
//        noticeMsgTV = (TextView) view.findViewById(R.id.notice_msg);
        mLLCover = view.findViewById(R.id.ll_cover);
        mLLCover.setVisibility(View.GONE);

        // 等待中动画
        rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.tangsdk_rotate_loading);
        // 控件隐藏显示动画
        initShowHiddenAnim();
        mIvWaittingLoading = view.findViewById(R.id.img_waiting);
        // 一对一视频画中画容器
        videoP2pLay = view.findViewById(R.id.video_p2p_lay);
        // 群视频容器
        videoGroupLay = view.findViewById(R.id.video_group_lay);
        // viewpager分页符容器
        dotContainer = view.findViewById(R.id.viewpager_dots_container);

        videoUserNameContainer = view.findViewById(R.id.viewpager_video_username_container);
        mTvVideoUserName = view.findViewById(R.id.video_user_name);

        mIvAvatar = view.findViewById(R.id.iv_avatar);
        mTvVoipCallTip = view.findViewById(R.id.tv_pre_call_tip);

        // 音频相关view
        audioContentLay = view.findViewById(R.id.audio_content_lay);
        audioUserListView = view.findViewById(R.id.audio_user_listview);
        audioUserListView.setVisibility(View.GONE);

        audioSingleLayout = view.findViewById(R.id.audio_single_user_lay);
        audioStateImg = view.findViewById(R.id.img_audio_state);
        // 快速回复按钮
        replyMsgLay = view.findViewById(R.id.reply_msg_area);
        mBtnReplyMsg = view.findViewById(R.id.reply_msg_btn);

        /// 群视频模式：正在说话的人
        speakingLay = view.findViewById(R.id.video_group_speaking_lay);
        speakingNameTV = view.findViewById(R.id.speaking_name_tv);

    }


    /**
     * @brief 初始化群视频容器Viewpager
     */
    private void initGroupVideoView() {
        // 先创建viewpager
        if (groupVideoViewPager == null) {
            groupVideoViewPager = new LazyViewPager(getActivity());
        }
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dm.widthPixels, (int) (dm.widthPixels / ratio));
        params.gravity = Gravity.CENTER;
        videoGroupLay.addView(groupVideoViewPager, params);

        List<VoipMeetingMemberWrapData> videoItemList = TangSDKInstance.getInstance().getVideoInstanceListData();
        if (videoItemList == null) {
            return;
        }

        int nSelect = 0;
        int Count = 0;
        boolean bFirst = true;
        String selfUserId = getMySelf().mUserId;
        if (mBtnSwitchVideo.isSelected() ||
                TangSDKInstance.getInstance().getCurrentShowingVideoMySelf()) {
            TangVideoView itemView = createTangVideoView(selfUserId);
            groupVideoViews.add(itemView);
            groupVideoViewMap.put(selfUserId, itemView);
            addDot(getMySelf().mUserId, true); // 初始化分页符

            bFirst = false;
            if (!mBtnSwitchVideo.isSelected()) {
                setVideoButtonChecked(true);
            }

            Count++;
            if (TangSDKInstance.getInstance().getCurrentShowingVideoUserId().equals(selfUserId)) {
                nSelect = Count - 1;
            }
        }

        int videoCount = videoItemList.size();
        for (int i = 0; i < videoCount; i++) {
            VoipMeetingMemberWrapData videoItem = videoItemList.get(i);
            String userid = videoItem.getUserID();

            if (userid.equals(selfUserId)) {
                continue;
            }

            TangVideoView itemView = createTangVideoView(userid);
            groupVideoViews.add(itemView);
            groupVideoViewMap.put(userid, itemView);

            // 初始化分页符
            addDot(videoItem.getUserEntity().mUserId, bFirst);
            if (bFirst) {
                bFirst = false;
            }

            Count++;

            if (TangSDKInstance.getInstance().getCurrentShowingVideoUserId().equals(userid)) {
                nSelect = Count - 1;
            }
        }

        // 初始化viewpager
        pagerAdapter = new GroupVideoPagerAdapter(groupVideoViews);
        pagerAdapter.notifyDataSetChanged();
        groupVideoViewPager.setAdapter(pagerAdapter);
        groupVideoViewPager.setOnPageChangeListener(this);
        if (groupVideoViews.size() > 0) {
            groupVideoViewPager.setCurrentItem(nSelect);
        }
        // 判断是否显示视频引导手势
        if (groupVideoViews.size() >= 2 && this.mViewMode == ViewMode.VIDEO_GROUP) {
            if (!PromptUtil.isGuideShowed(getActivity())) {
                PromptUtil.showVideoGuideDialog(getActivity());
            }
        }
    }

    /**
     * @param userid
     * @brief 动态创建一个显示视频的View
     */
    private TangVideoView createTangVideoView(String userid) {
        TangVideoView itemView = new TangVideoView(getActivity(), userid, false);
        itemView.setOnTouchListener(this);
        return itemView;
    }


    /**
     * @param userId
     * @param isFirst
     * @brief 增加一个分页符
     */
    private void addDot(String userId, boolean isFirst) {
        if (dotImgs != null) {
            int dotCount = dotImgs.size();
            for (int i = 0; i < dotCount; i++) {
                if ((dotImgs.get(i).getTag().equals(userId))) {
                    return;
                }
            }
        }
        ImageView dot = new ImageView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (int) PromptUtil.convertDipToPx(getActivity(), 8.0f),
                (int) PromptUtil.convertDipToPx(getActivity(), 8.0f));
        if (isFirst) {
            dot.setImageResource(R.mipmap.tangsdk_dot_selected);
        } else {
            dot.setImageResource(R.mipmap.tangsdk_dot_unselect);
            params.leftMargin = (int) PromptUtil.convertDipToPx(getActivity(), 9.0f);
        }
        dot.setTag(userId);
        dotImgs.add(dot);
        dotContainer.addView(dot, params);
    }

    /**
     * @param userid
     * @brief 删除一个分页符
     */
    private void deleteDot(String userid) {
        if (dotImgs == null) {
            return;
        }
        for (ImageView dot : dotImgs) {
            if (userid.equals(dot.getTag())) {
                dotContainer.removeView(dot);
                dotImgs.remove(dot);
                break;
            }
        }
    }

    /**
     * @brief 创建一对一视频画中画（大）
     */
    private TangVideoView createP2PBigView(String userId) {
        removeBigSurfaceView();
        Context context = getActivity();
        videoP2pBigView = new TangVideoView(context, userId, false);
        // 将创建好的bigview添加到容器中
        final DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        FrameLayout.LayoutParams bigViewparams = new FrameLayout.LayoutParams(dm.widthPixels, (int) (dm.widthPixels / ratio));
        bigViewparams.gravity = Gravity.CENTER;
        videoP2pLay.addView(videoP2pBigView, bigViewparams);

        videoP2pBigView.getSurfaceView().setZOrderOnTop(false);
        if (videoP2pSmallView != null) {
            SurfaceView smallSurface = (SurfaceView) videoP2pSmallView.getChildAt(0);
            if (smallSurface != null) {
                smallSurface.setZOrderMediaOverlay(true);
                smallSurface.getHolder().setFormat(PixelFormat.TRANSPARENT);
            }
        }

        videoP2pBigView.setOnTouchListener(this);
        if (!TangSDKInstance.getInstance().isGroupChat()) {
            videoP2pBigView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!p2pVideoOriginalState) {
                        changeP2pViewLayout(p2pVideoOriginalState);
                    }
                }
            });

        }

        return videoP2pBigView;
    }

    /**
     * @brief 创建一对一视频画中画（小）
     */
    private TangVideoView createP2PSmallView(String userid) {
        removeSmallSurfaceView();
        Context context = getActivity();
        videoP2pSmallView = new TangVideoView(context, userid, true);

        // 将创建好的smallview添加到容器中
        FrameLayout.LayoutParams smallViewparams = new FrameLayout.LayoutParams((int) PromptUtil.convertDipToPx(context, 90), (int) PromptUtil.convertDipToPx(context, 90 / ratio));
        smallViewparams.gravity = Gravity.CENTER | Gravity.RIGHT;
        smallViewparams.rightMargin = (int) PromptUtil.convertDipToPx(getActivity(), 10);
        videoP2pLay.addView(videoP2pSmallView, smallViewparams);

        if (videoP2pBigView != null) {
            videoP2pBigView.getSurfaceView().setZOrderOnTop(false);
        }
        videoP2pSmallView.getSurfaceView().setZOrderMediaOverlay(true);
        videoP2pSmallView.getSurfaceView().getHolder().setFormat(PixelFormat.TRANSPARENT);

        videoP2pSmallView.setOnTouchListener(this);
        videoP2pSmallView.setOnClickListener(v -> {
            if (p2pVideoOriginalState) {
                changeP2pViewLayout(p2pVideoOriginalState);
            }
        });
        return videoP2pSmallView;
    }


    /**
     * @param p2pVideoOriginalState
     * @brief 调整控件大小实现一对一视频画中画大小互换，
     */
    private void changeP2pViewLayout(boolean p2pVideoOriginalState) {
        final DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (!p2pVideoOriginalState) {
            FrameLayout.LayoutParams bigParams = (FrameLayout.LayoutParams) videoP2pBigView.getLayoutParams();
            bigParams.width = dm.widthPixels;
            bigParams.height = (int) (dm.widthPixels / ratio);
            bigParams.rightMargin = 0;
            bigParams.gravity = Gravity.CENTER;
            // 大切成小
            FrameLayout.LayoutParams smallParams = (FrameLayout.LayoutParams) videoP2pSmallView.getLayoutParams();
            smallParams.width = (int) PromptUtil.convertDipToPx(getActivity(), 90);
            smallParams.height = (int) PromptUtil.convertDipToPx(getActivity(), 90 / ratio);
            smallParams.gravity = Gravity.CENTER | Gravity.RIGHT;
            smallParams.rightMargin = (int) PromptUtil.convertDipToPx(getActivity(), 10);
            videoP2pLay.removeAllViews();
            videoP2pLay.addView(videoP2pBigView, bigParams);
            videoP2pLay.addView(videoP2pSmallView, smallParams);
            if (videoP2pBigView != null) {
                videoP2pBigView.getSurfaceView().setZOrderOnTop(false);
                String userIdBig = videoP2pBigView.getBindUserID();
                TangSDKInstance.getInstance().videoItemResetRender(userIdBig, videoP2pBigView.getSurfaceView());
            }
            videoP2pSmallView.getSurfaceView().setZOrderMediaOverlay(true);
            videoP2pSmallView.getSurfaceView().getHolder().setFormat(PixelFormat.TRANSPARENT);
            String userIdSmall = videoP2pSmallView.getBindUserID();
            TangSDKInstance.getInstance().videoItemResetRender(userIdSmall, videoP2pSmallView.getSurfaceView());


            this.p2pVideoOriginalState = true;
        } else {
            // 小切成大
            FrameLayout.LayoutParams smallParams = (FrameLayout.LayoutParams) videoP2pSmallView.getLayoutParams();
            smallParams.width = dm.widthPixels;
            smallParams.height = (int) (dm.widthPixels / ratio);
            smallParams.rightMargin = 0;
            smallParams.gravity = Gravity.CENTER;
            // 大切成小
            FrameLayout.LayoutParams bigParams = (FrameLayout.LayoutParams) videoP2pBigView.getLayoutParams();
            bigParams.width = (int) PromptUtil.convertDipToPx(getActivity(), 90);
            bigParams.height = (int) PromptUtil.convertDipToPx(getActivity(), 90 / ratio);
            bigParams.gravity = Gravity.CENTER | Gravity.RIGHT;
            bigParams.rightMargin = (int) PromptUtil.convertDipToPx(getActivity(), 10);

            videoP2pLay.removeAllViews();
            videoP2pLay.addView(videoP2pSmallView, smallParams);
            videoP2pLay.addView(videoP2pBigView, bigParams);

            if (videoP2pSmallView != null) {
                videoP2pSmallView.getSurfaceView().setZOrderOnTop(false);
                String userIdSmall = videoP2pSmallView.getBindUserID();
                TangSDKInstance.getInstance().videoItemResetRender(userIdSmall, videoP2pSmallView.getSurfaceView());
            }
            videoP2pBigView.getSurfaceView().setZOrderMediaOverlay(true);
            videoP2pBigView.getSurfaceView().getHolder().setFormat(PixelFormat.TRANSPARENT);
            String userIdBig = videoP2pBigView.getBindUserID();
            TangSDKInstance.getInstance().videoItemResetRender(userIdBig, videoP2pBigView.getSurfaceView());

            this.p2pVideoOriginalState = false;
        }
    }

    private boolean p2pVideoOriginalState = true; // 一对一视频画中画是否初始状态，即我的是小画面，对方是大画面

    /**
     * @brief 移除视频画中画（大）
     * @note 如果是群视频模式，不需要移除
     */
    private void removeBigSurfaceView() {
        if (TangSDKInstance.getInstance().isGroupChat())
            return;
        if (videoP2pBigView != null) {
            videoP2pBigView.setVisibility(View.INVISIBLE);
            if (videoP2pBigView.getParent() != null) {
                ((ViewGroup) videoP2pBigView.getParent()).removeView(videoP2pBigView);
            }
            videoP2pBigView = null;
        }
    }

    /**
     * @brief 移除视频画中画（小）
     * @note 如果是群视频模式，不需要移除
     */
    private void removeSmallSurfaceView() {
        if (TangSDKInstance.getInstance().isGroupChat())
            return;
        if (videoP2pSmallView != null) {
            videoP2pSmallView.setVisibility(View.INVISIBLE);
            if (videoP2pSmallView.getParent() != null) {
                ((ViewGroup) videoP2pSmallView.getParent()).removeView(videoP2pSmallView);
            }
            videoP2pSmallView = null;
        }
    }

    private void initListener() {
        mBtnMinimizeWindow.setOnClickListener(this);
        videoMode_CameraSwitch.setOnClickListener(this);
        videoGroupMode_viewAllBtn.setOnClickListener(this);
        mBtnInvite.setOnClickListener(this);
        mIvHangupCall.setOnClickListener(this);
        mIvPickupCall.setOnClickListener(this);
        mBtnSwitchVideo.setOnClickListener(this);
        speakerButton.setOnClickListener(this);
        mBtnMute.setOnClickListener(this);
        audioSwitchBtn.setOnClickListener(this);
        mBtnReplyMsg.setOnClickListener(this);
        viewdeskshareBtn.setOnClickListener(this);
        receiveVideoBtn.setOnClickListener(this);
        videoP2pLay.setOnTouchListener(this);
        videoGroupLay.setOnTouchListener(this);
    }


    @Override
    public void onClick(View view) {
        stopFullScreenCountDown();
        if (isFullScreenMode) {
            return;
        }
        int id = view.getId();
        if (R.id.btn_minimize_window == id) {
            checkPopPermissionAndDoMinimize();


            return;
        }
        // 添加参会人按钮
        if (R.id.invite_btn == id) {
            if (getDelegate() != null) {
                getDelegate().onInviteMember();
            }
            return;
        }
        // 挂断按钮
        if (R.id.iv_hangup_call == id) {
            onClickStopCall();
            return;
        }
        // 接听按钮
        if (R.id.iv_pickup_call == id) {
            onClickPickupBtn();
            return;
        }

        // 静音按钮
        if (R.id.btn_mute == id) {
            TangSDKInstance.getInstance().muteMySelf(!mBtnMute.isSelected());
            return;
        }
        // 免提按钮
        if (R.id.speaker_button == id) {
            onClickLouderBtn();
            return;
        }
        // 切换到视频
        if (R.id.video_button == id) {
            if(CommonUtil.isFastClick(1000)) {
                return;
            }

            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(), new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    setVideoButtonEnabled(false);//fix bug B160203-002]
                    onClickVideoBtn();
                    setVideoButtonEnabled(true);
                }

                @Override
                public void onDenied(String permission) {
                    AtworkUtil.popAuthSettingAlert(getContext(), permission);
                }
            });

            return;
        }
        // 切换语音
        if (R.id.audio_btn == id) {
            startActivity(new Intent(getActivity(), SwitchVoiceActivity.class));
            getActivity().overridePendingTransition(R.anim.tangsdk_push_bottom_in, 0);
            return;
        }
        if (R.id.video_mode_camera_switch_btn == id) {
            TangSDKInstance.getInstance().videoSwitchCamera();
            return;
        }
        // 从群视频切回音频
        if (R.id.video_group_mode_viewall_btn == id) {
            this.isVideoSwitchAudio = true;
            setViewMode(ViewMode.AUDIO_GROUP);
            return;
        }
        if (R.id.reply_msg_btn == id) {
            showReplyMenu();
            return;
        }
        if (R.id.desk_share_enter_btn == id) {
            onClickReceiveDesktopShareBtn();
            return;
        }
        if (R.id.video_enter_btn == id) {
            onClickReceiveVideoBtn();
            return;
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
     * @brief 免提按钮点击响应
     */
    private void onClickLouderBtn() {
        TangSDKInstance.getInstance().enableLoudSpeaker(!speakerButton.isSelected());
    }

    /**
     * @brief 视频共享按钮点击响应
     */
    private void onClickVideoBtn() {
//        if (isFastClick(1000)) { //fix bug B160203-002]
//            return;
//        }
        if (!NetworkStatusUtil.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.tangsdk_network_invalid_prompt), Toast.LENGTH_SHORT).show();
            return;
        }
        if (NetworkStatusUtil.isWifiConnectedOrConnecting(getActivity())) {
            if (mBtnSwitchVideo.isSelected()) {
                detachMySelfVideo();
            } else {
                attchMySelfVideo();
            }
        } else {
            PromptUtil.showCustomAlertMessage(getString(R.string.tangsdk_prompt_text),
                    getString(R.string.tangsdk_not_wifi_prompt),
                    getString(R.string.tangsdk_continue_btn_text), null, getActivity(),
                    (dialog, which) -> {
                        if (mBtnSwitchVideo.isSelected()) {
                            detachMySelfVideo();
                        } else {
                            attchMySelfVideo();
                        }
                    }, (dialog, which) -> {
                    }, true);
        }
    }


    /**
     * @brief 接收视频画面 按钮点击响应
     */
    private void onClickReceiveDesktopShareBtn() {
        if (!NetworkStatusUtil.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.tangsdk_network_invalid_prompt), Toast.LENGTH_SHORT).show();
            return;
        }
        if (NetworkStatusUtil.isWifiConnectedOrConnecting(getActivity())) {
            loadDesktopViewer();
        } else {
            PromptUtil.showCustomAlertMessage(getString(R.string.tangsdk_prompt_text),
                    getString(R.string.tangsdk_not_wifi_prompt),
                    getString(R.string.tangsdk_continue_btn_text), null, getActivity(),
                    (dialog, which) -> loadDesktopViewer(), (dialog, which) -> {
                    }, true);
        }
    }

    /**
     * @brief 接收视频画面 按钮点击响应
     */
    private void onClickReceiveVideoBtn() {
        if (isVideoSwitchAudio) {
            setViewMode(ViewMode.VIDEO_GROUP);
            this.isVideoSwitchAudio = false;
        } else {
            if (!NetworkStatusUtil.isNetworkAvailable(getActivity())) {
                Toast.makeText(getActivity(), getString(R.string.tangsdk_network_invalid_prompt), Toast.LENGTH_SHORT).show();
                return;
            }
            if (NetworkStatusUtil.isWifiConnectedOrConnecting(getActivity())) {
                TangSDKInstance.getInstance().openVideoCall();
                if (TangSDKInstance.getInstance().isGroupChat()) {
                    receiveGroupVideo();
                } else {
                    receiveP2pVideo();
                }
            } else {
                PromptUtil.showCustomAlertMessage(getString(R.string.tangsdk_prompt_text),
                        getString(R.string.tangsdk_not_wifi_prompt),
                        getString(R.string.tangsdk_continue_btn_text), null, getActivity(),
                        (dialog, which) -> {
                            TangSDKInstance.getInstance().openVideoCall();
                            if (TangSDKInstance.getInstance().isGroupChat()) {
                                receiveGroupVideo();
                            } else {
                                receiveP2pVideo();
                            }
                        }, (dialog, which) -> {
                        }, true);
            }
        }
    }

    /**
     * 挂断按钮点击响应
     */
    private void onClickStopCall() {
        mLLCover.setVisibility(View.VISIBLE);
        TangSDKInstance.getInstance().finishCall();
    }


    /**
     * 超时呼叫时的挂断处理
     */
    private void onAutoStopCall() {
        mLLCover.setVisibility(View.VISIBLE);
        TangSDKInstance.getInstance().autoFinishCall();
    }


    /**
     * @brief 接听按钮点击响应
     */
    private void onClickPickupBtn() {
        TangSDKInstance.getInstance().acceptCall();
        if (TangSDKInstance.getInstance().isGroupChat()) {
            setViewMode(ViewMode.AUDIO_GROUP);
        } else {
            setViewMode(ViewMode.AUDIO_P2P);
        }

    }

    /**
     * @brief 接收1对1视频画面
     */
    private void receiveP2pVideo() {
        int videoItemCount = TangSDKInstance.getInstance().getShowedVideoItemCount();
        if (videoItemCount == 0) {
            setViewMode(ViewMode.VIDEO_P2P);
        } else if (videoItemCount == 1) {
            TangSDKInstance.getInstance().videoShowVideoItem(getMySelf().mUserId);
        }
        TangSDKInstance.getInstance().videoShowVideoItem(getPeer().mUserId);
    }


    /**
     * @brief 接收群视频画面
     */
    private void receiveGroupVideo() {
        if (this.getViewMode() != ViewMode.VIDEO_GROUP) {
            setViewMode(ViewMode.VIDEO_GROUP);
        }
    }

    /**
     * @brief 显示我的视频view
     */
    private void attchMySelfVideo() {
        if (!isVideoSwitchAudio) {
            if (!TangSDKInstance.getInstance().checkCameraIsOpen()) {
                Toast.makeText(getActivity(), getString(R.string.tangsdk_no_camera_permission_prompt), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        TangSDKInstance.getInstance().openVideoCall();
        String curUserId = getMySelf().mUserId;
        if (TangSDKInstance.getInstance().isGroupChat()) {
            setVideoButtonChecked(true);
            // 切换到群视频模式
            if (this.getViewMode() != ViewMode.VIDEO_GROUP || pagerAdapter == null) {
                setViewMode(ViewMode.VIDEO_GROUP);
            } else {
                // 往viewpager后添加我的视图
                TangVideoView itemView = createTangVideoView(curUserId);
                groupVideoViewMap.put(curUserId, itemView);
                pagerAdapter.addItem(curUserId, itemView);
                addDot(curUserId, false);

                groupVideoViewPager.setCurrentItem(groupVideoViewMap.size() - 1);
                videoMode_CameraSwitch.setVisibility(View.VISIBLE);

                int videoItemCount = TangSDKInstance.getInstance().getShowedVideoItemCount();
                // 判断是否显示视频引导手势
                if (videoItemCount >= 2 && this.mViewMode == ViewMode.VIDEO_GROUP) {
                    if (!PromptUtil.isGuideShowed(getActivity())) {
                        PromptUtil.showVideoGuideDialog(getActivity());
                    }
                }
            }

        } else {
            // 切换到1对1视频模式
            if (this.getViewMode() != ViewMode.VIDEO_P2P) {
                setViewMode(ViewMode.VIDEO_P2P);
            }

            if (TangSDKInstance.getInstance().getShowedVideoItemCount() == 0) {
                String otherUserId = "";
                List<VoipMeetingMemberWrapData> videoList = TangSDKInstance.getInstance().getVideoInstanceListData();
                if (videoList != null) {
                    for (VoipMeetingMemberWrapData item : videoList) {
                        if (item == null) {
                            continue;
                        }
                        if (!item.getUserEntity().mUserId.equals(getMySelf().mUserId)) {
                            otherUserId = item.getUserEntity().mUserId;
                            break;
                        }
                    }
                }
                if (otherUserId.length() > 0) {
                    TangSDKInstance.getInstance().videoShowVideoItem(otherUserId);
                }
            }
            TangSDKInstance.getInstance().videoShowVideoItem(curUserId);
            setVideoButtonChecked(true);
            videoMode_CameraSwitch.setVisibility(View.VISIBLE);
        }

    }


    /**
     * @brief 取消显示我的视频view
     */
    private void detachMySelfVideo() {
        String userId = getMySelf().mUserId;

        if (this.mViewMode == ViewMode.VIDEO_GROUP) {
            if (pagerAdapter == null) {
                return;
            }

            int nDeletePos = pagerAdapter.getItemPosition(groupVideoViewMap.get(userId));
            if (nDeletePos >= 0) {
                deleteDot(userId);
                groupVideoViewMap.remove(userId);
                pagerAdapter.deleteItem(userId);
            }

            TangSDKInstance.getInstance().videoHideVideoItem(userId, true); //stop share

            if (groupVideoViewMap.size() == 0) {
                TangSDKInstance.getInstance().closeVideoCall();
                this.isVideoSwitchAudio = false;
            }
        } else if (this.mViewMode == ViewMode.VIDEO_P2P) {
            TangSDKInstance.getInstance().videoHideVideoItem(userId, true); //stop share
        }
        setVideoButtonChecked(false);
        videoMode_CameraSwitch.setVisibility(View.GONE);
    }

    /**
     * 通知Attach Surface, 返回的Surface必须是New出来的，不然无法显示
     */
    @Override
    public Object onVideoItemAttachSurface(String userId) {
        boolean isMe = userId.equals(getMySelf().mUserId);
        SurfaceView surfaceView = null;
        // 群呼
        if (TangSDKInstance.getInstance().isGroupChat()) {
            TangVideoView itemView = groupVideoViewMap.get(userId);
            surfaceView = itemView.renewSurfaceView();
            if (surfaceView != null) {
                // fix bug [B160201-002]
                itemView.setVisibility(View.VISIBLE);
                surfaceView.setVisibility(View.VISIBLE);
                // 自己视频打开时，没有首帧提示，所以在此处手动把盖板拿掉
                if (userId.equals(getMySelf().mUserId)) {
                    itemView.getCoverView().setVisibility(View.INVISIBLE);
                }

            }
        } else {
            // 1对1呼
            int videoItemCount = TangSDKInstance.getInstance().getShowedVideoItemCount();
            if (videoItemCount == 0) {
                TangVideoView bigView = createP2PBigView(userId);
                surfaceView = bigView.getSurfaceView();
                if (!userId.equals(getMySelf().mUserId)) {
                    bigView.onLoadStart();
                } else {
                    surfaceView.setVisibility(View.VISIBLE);
                    bigView.getCoverView().setVisibility(View.INVISIBLE);
                    bigView.setProgressBarVisible(View.GONE);
                }
            } else if (videoItemCount >= 1) {
                if (isMe && p2pVideoOriginalState && TangSDKInstance.getInstance().isVideoItemShowing(getPeer().mUserId)) {
                    TangVideoView smallView = createP2PSmallView(userId);
                    surfaceView = smallView.getSurfaceView();
                } else {
                    TangVideoView bigView = createP2PBigView(userId);
                    surfaceView = bigView.getSurfaceView();
                    if (!userId.equals(getMySelf().mUserId)) {
                        bigView.onLoadStart();
                    } else {
                        surfaceView.setVisibility(View.VISIBLE);
                        bigView.getCoverView().setVisibility(View.INVISIBLE);
                        bigView.setProgressBarVisible(View.GONE);
                    }
                }
            }
        }
        return surfaceView;
    }

    /**
     * 通知Attach Surface detach
     */
    @Override
    public void onVideoItemDetachSurface(String userId, Object videoSurface) {
        // 群视频模式，在onVideoItemDeleted回调中会执行销毁相应surfaceView操作
        SurfaceView surfaceView = (SurfaceView) videoSurface;
        if (surfaceView == null) {
            return;
        }
        if (!(surfaceView.getParent() instanceof TangVideoView)) {
            return;
        }

        TangVideoView itemView = (TangVideoView) surfaceView.getParent();
        if (itemView == null) {
            return;
        }
        itemView.onLoadComplete();
//        stoploadingVideo(itemView);

        surfaceView.setVisibility(View.INVISIBLE);
        itemView.setVisibility(View.INVISIBLE);

        if (TangSDKInstance.getInstance().isGroupChat()) {
            if (surfaceView.getParent() != null) {
                ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
            }
        } else {
            if (itemView.getParent() != null) {
                ((ViewGroup) itemView.getParent()).removeView(itemView);
                if (!p2pVideoOriginalState) {
                    // dettach我的画面
                    if (userId.equals(getMySelf().mUserId)) {
                        // 将对方的视频画面放大
                        final DisplayMetrics dm = new DisplayMetrics();
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                        FrameLayout.LayoutParams bigParams = (FrameLayout.LayoutParams) videoP2pBigView.getLayoutParams();
                        bigParams.width = dm.widthPixels;
                        bigParams.height = (int) (dm.widthPixels / ratio);
                        bigParams.rightMargin = 0;
                        bigParams.gravity = Gravity.CENTER;
                        videoP2pLay.removeAllViews();
                        videoP2pLay.addView(videoP2pBigView, bigParams);
                        videoP2pBigView.getSurfaceView().setZOrderOnTop(false);
                        // 自己的画面dettach掉了，只显示对方的大画面，所以重置该属性
                        this.p2pVideoOriginalState = true;

                        String userIdBig = videoP2pBigView.getBindUserID();
                        TangSDKInstance.getInstance().videoItemResetRender(userIdBig,videoP2pBigView.getSurfaceView());
                    } else {

                    }
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //setScreenOrientation(Surface.ROTATION_0);
    }


    private void setActivityFullScreen(boolean bFullScreen) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
        if (bFullScreen) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getActivity().getWindow().setAttributes(attrs);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            mPreferences.edit().putBoolean("fullScreen", true).commit();
        } else {
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getActivity().getWindow().setAttributes(attrs);
            //取消全屏设置
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            mPreferences.edit().putBoolean("fullScreen", false).commit();
        }
    }

    public void setScreenOrientation(int nOrientation) {

        if (!isShowingDesktopViewer()) {
            nCurrentOrientaion = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            if (getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                setActivityFullScreen(false);
            }
            return;
        }

        if (nOrientation == nCurrentOrientaion) {
            return;
        }
        nCurrentOrientaion = nOrientation;
        getActivity().setRequestedOrientation(nCurrentOrientaion);

        if (nCurrentOrientaion == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || nCurrentOrientaion == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            setActivityFullScreen(false);
        } else {
            setActivityFullScreen(true);
        }
    }


    private void enableSensor(boolean enable) {

        if (sensorEvent == null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            sensorEvent = new OrientationEventListener(getActivity(), SensorManager.SENSOR_DELAY_NORMAL) {
                @Override
                public void onOrientationChanged(int orientation) {
                     /*
                     * This logic is useful when user explicitly changes orientation using player controls, in which case orientation changes gives no callbacks.
                     * we use sensor angle to anticipate orientation and make changes accordingly.
                     */
                    if (nCurrentOrientaion == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        if (orientation >= 60 && orientation <= 120) {
                            setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        } else if (orientation >= 240 && orientation <= 300) {
                            setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                    } else if (nCurrentOrientaion == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                        if (orientation >= 320 || orientation >= 0 && orientation <= 40) { //Ranger fix Mantis bug 5346
                            setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        } else if (orientation >= 240 && orientation <= 300) {
                            setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                    } else if (nCurrentOrientaion == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            ) {
                        if (orientation >= 320 || orientation >= 0 && orientation <= 40) { //Ranger fix Mantis bug 5346
                            setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        } else if (orientation >= 60 && orientation <= 120) {
                            setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        }
                    }
                }
            };
        }

        if (enable)
            sensorEvent.enable();
        else
            sensorEvent.disable();
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

    public void clearData() {
        unloadDesktopViewer();
        enableSensor(false);
        stopFullScreenCountDown();
        stopWaitAnswerCountDown();
        stopWaitAnswerHandler();

        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        mFullScreenTimerTask = null;
        mWaitAnswerTimerTask = null;
        if (mNeedResetDelegate) {
            TangSDKInstance.getInstance().setDelegatePrivate(null);
            mNeedResetDelegate = false;
        }
        stopWaitingAnim();
    }


    /**
     * @brief 弹出快速回复菜单
     */
    private void showReplyMenu() {
        if (getDelegate() != null) {
            final ArrayList<String> menuList = new ArrayList<>();
            ArrayList<String> messages = getDelegate().getQuickReplayMessages();
            if (messages == null) {
                return;
            }
            menuList.addAll(messages);
            menuList.add(getString(R.string.tangsdk_cancel_btn_title)); // 添加取消项
            if (menuList.isEmpty()) {
                Log.e(TAG, "messages is null");
                return;
            }
            Dialog dialog = PromptUtil.showMenu(null, menuList, getActivity(), (dialog1, which) -> {
                // 回调给客户，由客户进行下一步操作
                if (which == menuList.size() - 1) {
                    dialog1.dismiss();
                } else {
                    if (getDelegate() != null) {
                        getDelegate().onSendQuickReplayMessage(which);
                        onClickStopCall();
                        dialog1.dismiss();
                    }
                }
            });
        }
    }

    private void startWaitingAnim() {
        mIvWaittingLoading.setVisibility(View.VISIBLE);
        LinearInterpolator li = new LinearInterpolator();
        rotateAnimation.setInterpolator(li);
        mIvWaittingLoading.startAnimation(rotateAnimation);
    }

    private void stopWaitingAnim() {
        mIvWaittingLoading.setVisibility(View.INVISIBLE);
        mIvWaittingLoading.clearAnimation();
    }

    private void initData() {
        Bundle bundle = getArguments();
        mVoipType = (VoipType) bundle.getSerializable(QsyCallActivity.EXTRA_VOIP_TYPE);

        mTimer = new Timer();
        VoipMeetingMember mySelf = TangSDKInstance.getInstance().getMySelf();
        if (mySelf == null) {
            return;
        }
        refreshMyControlButton();
        if (mySelf.getUserType() == UserType.Originator) { // 我是发送方
            setViewMode(TangSDKInstance.getInstance().isGroupChat() ? ViewMode.AUDIO_GROUP : ViewMode.AUDIO_P2P);
        } else if (mySelf.getUserType() == UserType.Recipient) { // 我是接收方
            // 判断呼状态
            if (TangSDKInstance.getInstance().getCallState() == CallState.CallState_Init) {
                setViewMode(ViewMode.RECEIVE_CALL);
            } else {
                if (TangSDKInstance.getInstance().isGroupChat()) {
                    setViewMode(ViewMode.AUDIO_GROUP);
                } else {
                    setViewMode(ViewMode.AUDIO_P2P);
                }
            }
        }
        if (TangSDKInstance.getInstance().isGroupChat()) {
            // 有video需要恢复，则切到群视频模式
            if (TangSDKInstance.getInstance().haveVideoNeedRestore()) {
                setViewMode(ViewMode.VIDEO_GROUP);
                if (groupVideoViewPager != null) {
                    this.groupVideoViewPager.postDelayed(() -> TangSDKInstance.getInstance().restoreNeedShowVideo(false), 100);
                }
            } else {
                mAdapter = new AudioUserListAdapter(getActivity(), TangSDKInstance.getInstance().getUserItemDataList());
                audioUserListView.setAdapter(mAdapter);
            }
        } else {
            // 有video需要恢复，则切到1对1视频模式
            if (TangSDKInstance.getInstance().haveVideoNeedRestore()) {
                setViewMode(ViewMode.VIDEO_P2P);
                TangSDKInstance.getInstance().restoreNeedShowVideo(true);
            }
        }

        mHandler.postDelayed(() -> {
            if (getDelegate() != null) {
                //通知应用开始异步去取user, group图像
                if (TangSDKInstance.getInstance().isGroupChat()) {
                    if (getGroup() != null) {
                        getDelegate().onStartGetGroupProfile(getGroup());
                    }
                } else {
                    if (getPeer() != null) {
                        getDelegate().onStartGetUserProfile(getPeer().mUserId, getPeer().mDomainId);
                    }
                }
                onUserListUpdated();
            }
        }, 10);

        // 初始化数据库
        new InitDBTask().execute();


    }

    class InitDBTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            initDB(Constants.COUNTRY_CODE_DB_NAME, 1, R.raw.tangsdkui);
            return null;
        }
    }


    /**
     * 通知用户列表刷新
     */
    @Override
    public void onUserListUpdated() {
        if (isAdded()) {
            getActivity().runOnUiThread(() -> {
                Log.d(TAG, "CallFragment::onUserListUpdated");
                // 刷新用户列表
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();

                boolean isGroupCall = TangSDKInstance.getInstance().isGroupChat();

                // 判断是否从P2P切换成group
                if (isGroupCall) {
                    // init audio user list mAdapter
                    if (mAdapter == null) {
                        mAdapter = new AudioUserListAdapter(getActivity(), TangSDKInstance.getInstance().getUserItemDataList());
                        audioUserListView.setAdapter(mAdapter);
                    }
                    if (this.mViewMode == ViewMode.AUDIO_P2P) {
                        // view模式切换成Audio_Group
                        setViewMode(ViewMode.AUDIO_GROUP);

                    } else if (this.mViewMode == ViewMode.VIDEO_P2P) {
                        // view模式切换成Video_Group
                        if (videoP2pSmallView != null) {
                            String bindUserId = videoP2pSmallView.getBindUserID();
                            TangSDKInstance.getInstance().videoHideVideoItem(bindUserId, false);
                            videoP2pSmallView.setVisibility(View.GONE);
                            if (videoP2pSmallView.getParent() != null) {
                                ((ViewGroup) videoP2pSmallView.getParent()).removeView(videoP2pSmallView);
                            }
                        }

                        if (videoP2pBigView != null) {
                            String bindUserId = videoP2pBigView.getBindUserID();
                            TangSDKInstance.getInstance().videoHideVideoItem(bindUserId, false);
                            videoP2pBigView.setVisibility(View.GONE);
                            if (videoP2pBigView.getParent() != null) {
                                ((ViewGroup) videoP2pBigView.getParent()).removeView(videoP2pBigView);
                            }
                        }
                        setViewMode(ViewMode.VIDEO_GROUP);
                    }
                }
                // 在此处也刷新自己的免提/静音/视频/语音状态
                refreshMyControlButton();
                // 1对1，刷新对方信息(语音状态)
                if (!isGroupCall) {
                    VoipMeetingMemberWrapData peerData = TangSDKInstance.getInstance().getUserItemDataByUserId(getPeer().mUserId, getPeer().mDomainId);
                    if (peerData == null) {
                        Log.e(TAG, "mPeer data is null");
                    } else {
                        refreshPeerVoiceIcon(peerData.getVoiceType(), peerData.isMute());
                    }
                }
            });

        }
    }

    @Override
    public void onUserBusy(String busyTip) {
        if (isAdded()) {
            getActivity().runOnUiThread(() -> {
                AtworkToast.showToast(busyTip);

            });
        }
    }

    /**
     * @brief 刷新控制区域的四个按钮状态
     */
    private void refreshMyControlButton() {
        CallState callState = TangSDKInstance.getInstance().getCallState();
        if (callState == CallState.CallState_Calling) {
            VoipMeetingMemberWrapData mySelfData = TangSDKInstance.getInstance().getUserItemDataByUserId(getMySelf().mUserId, getMySelf().mDomainId);
            setMuteButtonChecked(mySelfData.isMute()); // 静音
//            setVideoButtonChecked(mySelfData.isVideoShared()); // 共享视频
            if (mySelfData.getVoiceType() == VoiceType.TEL) {
                setMuteButtonEnable(false);
            } else if (mySelfData.getVoiceType() == VoiceType.VOIP) {
                setMuteButtonEnable(true);
            }
            setAudioSwitchButtonEnable(true); // 切换语音按钮
            mBtnInvite.setEnabled(true); // 邀请按钮
            boolean isLoudSpeaker = TangSDKInstance.getInstance().isLoudSpeakerStatus();
            setSpeakerButtonChecked(isLoudSpeaker); // 免提按钮

        } else if (callState == CallState.CallState_Idle
                || callState == CallState.CallState_Init
                || callState == CallState.CallState_StartCall
                || callState == CallState.CallState_Waiting) {
            // 控制区域设置不可见
            mLlControl.setVisibility(View.GONE);
            mBtnInvite.setVisibility(View.INVISIBLE);
        } else {
            setAllBtnAvailable(false);
        }

    }


    @Override
    public void onLoudSpeakerStatusChanged(boolean bLoudSpeaker) {
        setSpeakerButtonChecked(bLoudSpeaker);
    }

    /**
     * 通知说话人变化
     */
    @Override
    public void onIsSpeakingChanged(String strSpeakingNames) {
        // 只有群视频模式下，才响应该事件
        if (this.mViewMode == ViewMode.VIDEO_GROUP && TangSDKInstance.getInstance().isGroupChat()) {
            Log.d(TAG, "CallFragment::onIsSpeakingChanged");

            if (TextUtils.isEmpty(strSpeakingNames)) {
                speakingLay.setVisibility(View.GONE);

            } else {
                speakingLay.setVisibility(View.VISIBLE);
                speakingNameTV.setText(strSpeakingNames);
            }

        } else {
            speakingLay.setVisibility(View.GONE);
        }
    }

    /**
     * 通知呼叫时间状态变化
     */
    @Override
    public void onCallingTimeElpased(long nSeconds) {
        updateDurationText(nSeconds);
    }


    /**
     * 根据音频类型刷新1对1对方的语音图标
     */
    private void refreshPeerVoiceIcon(VoiceType voiceType, boolean isMute) {
        if (isMute) {
            audioStateImg.setVisibility(View.VISIBLE);
            audioStateImg.setImageResource(R.mipmap.tangsdk_mute_state_icon);
            return;
        }

        if (voiceType == VoiceType.TEL) {
            audioStateImg.setVisibility(View.VISIBLE);
            audioStateImg.setImageResource(R.mipmap.tangsdk_tel_normal_icon);

        } else if (voiceType == VoiceType.VOIP) {
            audioStateImg.setVisibility(View.VISIBLE);
            audioStateImg.setImageResource(R.mipmap.tangsdk_voip_normal_icon);

        } else if (voiceType == VoiceType.NONE) {
            audioStateImg.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * @param checked
     * @brief 设置免提按钮是否选中
     */
    private void setSpeakerButtonChecked(boolean checked) {
        if (speakerButton == null) {
            return;
        }
        if (checked) {
            speakerButton.setImageResource(R.mipmap.tangsdk_hand_free_pressed);
        } else {
            speakerButton.setImageResource(R.mipmap.tangsdk_hand_free_normal);
        }
        speakerButton.setSelected(checked);
    }

    /**
     * @param checked
     * @brief 设置静音按钮是否选中
     */
    private void setMuteButtonChecked(boolean checked) {
        if (checked) {
            mBtnMute.setImageResource(R.mipmap.tangsdk_mute_button_pressed);
        } else {
            mBtnMute.setImageResource(R.mipmap.tangsdk_mute_button_normal);
        }
        mBtnMute.setSelected(checked);
    }

    /**
     * @param checked
     * @brief 设置视频按钮是否选中
     */
    private void setVideoButtonChecked(boolean checked) {
        if (checked) {
            mBtnSwitchVideo.setImageResource(R.mipmap.tangsdk_btn_video_switch_pressed);
        } else {
            mBtnSwitchVideo.setImageResource(R.mipmap.tangsdk_btn_video_switch_normal);
        }
        mBtnSwitchVideo.setSelected(checked);
    }

    /**
     * @param enabled
     * @brief 设置视频按钮是否可用
     */
    private void setVideoButtonEnabled(boolean enabled) {
        if (enabled) {
            if (mBtnSwitchVideo.isSelected()) {
                mBtnSwitchVideo.setImageResource(R.mipmap.tangsdk_btn_video_switch_pressed);
            } else {
                mBtnSwitchVideo.setImageResource(R.mipmap.tangsdk_btn_video_switch_normal);
            }
        } else {
            mBtnSwitchVideo.setImageResource(R.mipmap.tangsdk_btn_video_switch_disabled);
        }
        mBtnSwitchVideo.setEnabled(enabled);
    }

    /**
     * @param enabled
     * @brief 设置视频按钮是否可用
     */
    private void setSpeakerButtonEnabled(boolean enabled) {
        if (enabled) {
            if (speakerButton.isSelected()) {
                speakerButton.setImageResource(R.mipmap.tangsdk_hand_free_pressed);
            } else {
                speakerButton.setImageResource(R.mipmap.tangsdk_hand_free_normal);
            }
        } else {
            speakerButton.setImageResource(R.mipmap.tangsdk_hand_free_disabled);
        }
        speakerButton.setEnabled(enabled);
    }


    /**
     * @param enabled
     * @brief 设置静音按钮是否可用
     */
    private void setMuteButtonEnable(boolean enabled) {
        if (enabled) {
            if (mBtnMute.isSelected()) {
                mBtnMute.setImageResource(R.mipmap.tangsdk_mute_button_pressed);
            } else {
                mBtnMute.setImageResource(R.mipmap.tangsdk_mute_button_normal);
            }
        } else {
            mBtnMute.setImageResource(R.mipmap.tangsdk_mute_button_diabled);
        }
        mBtnMute.setEnabled(enabled);
    }

    /**
     * @param enabled
     * @brief 设置语音切换按钮是否可用
     */
    private void setAudioSwitchButtonEnable(boolean enabled) {
        audioSwitchBtn.setEnabled(enabled);
    }

    /**
     * 通知视频实例列表变化
     */
    @Override
    public void onVideoItemAdded(String userId) {
        int videoItemCount = TangSDKInstance.getInstance().getShowedVideoItemCount();
        // 判断是否显示视频引导手势
        if (videoItemCount >= 1 && this.mViewMode == ViewMode.VIDEO_GROUP) {
            if (!PromptUtil.isGuideShowed(getActivity())) {
                PromptUtil.showVideoGuideDialog(getActivity());
            }
        }
        if (TangSDKInstance.getInstance().isGroupChat()) {
            // 自己打开视频的事件在此处不处理，在主动触发时已经初始化了
            if (userId.equals(getMySelf().mUserId)) {
                // fix bug B161009-009
                if (pagerAdapter != null) {
                    TangVideoView itemView = createTangVideoView(userId);
                    groupVideoViewMap.put(userId, itemView);
                    pagerAdapter.addItem(userId, itemView);
                    addDot(userId, false);
                    dotImgs.get(0).setImageResource(R.mipmap.tangsdk_dot_selected);

                    int currentItem = groupVideoViewPager.getCurrentItem();
                    TangSDKInstance.getInstance().videoShowVideoItem(groupVideoViews.get(currentItem).getBindUserID());
                }

                return;
            }
            if (pagerAdapter != null) { // 无人共享，则初始化
                TangVideoView itemView = createTangVideoView(userId);
                groupVideoViewMap.put(userId, itemView);
                pagerAdapter.addItem(userId, itemView);
                addDot(userId, false);
            }
        } else {

        }
        checkShowReceiveVideoAndDesktopButton();

    }

    private void checkShowReceiveVideoAndDesktopButton() {
        boolean bHaveOtherVideo = false;
        List<VoipMeetingMemberWrapData> videoList = TangSDKInstance.getInstance().getVideoInstanceListData();
        if (videoList != null) {
            for (VoipMeetingMemberWrapData item : videoList) {
                if (item == null) {
                    continue;
                }
                if (!item.getUserEntity().mUserId.equals(getMySelf().mUserId)) {
                    bHaveOtherVideo = true;
                    break;
                }
            }
        }
        boolean bShowReceiveVideoButton = ((mViewMode == ViewMode.AUDIO_P2P || mViewMode == ViewMode.AUDIO_GROUP) && bHaveOtherVideo);
        boolean bHaveDesktopShare = TangSDKInstance.getInstance().haveDesktopShare();
        enterVideoAndDeskshareLay.setVisibility((bShowReceiveVideoButton || bHaveDesktopShare) ? View.VISIBLE : View.GONE);

        if (bShowReceiveVideoButton) {
            receiveVideoLay.setVisibility(View.VISIBLE);
            if (isVideoSwitchAudio)
                videoEnterTV.setText(getString(R.string.tangsdk_view_video_text));
            else
                videoEnterTV.setText(getString(R.string.tangsdk_receive_video_text));

        } else {
            receiveVideoLay.setVisibility(View.GONE);
        }

        if (bHaveDesktopShare) {
            viewdeskshareLay.setVisibility(View.VISIBLE);
            viewdeskshareBtn.setVisibility(View.VISIBLE);
        } else {
            viewdeskshareLay.setVisibility(View.GONE);
            viewdeskshareBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onVideoItemDeleted(String userId) {
        if (TangSDKInstance.getInstance().isGroupChat()) {
            if (pagerAdapter != null && groupVideoViewPager != null) {
                int nDeletePos = pagerAdapter.getItemPosition(groupVideoViewMap.get(userId));
                if (nDeletePos >= 0) {
                    deleteDot(userId);
                    groupVideoViewMap.remove(userId);
                    pagerAdapter.deleteItem(userId);
                }

                if (groupVideoViewMap.size() == 0) {
                    TangSDKInstance.getInstance().closeVideoCall();
                }
            }
        } else {
            //if (userId == getMySelf().getUserId()) {
            //    handleMyselfCloseVideo();
            //} else if (userId == getPeer().getUserId()) {
            //    handlePeerCloseVideo();
            //}
        }

        checkShowReceiveVideoAndDesktopButton();
    }

    /**
     * 通知视频呼叫结束了
     */
    @Override
    public void onVideoCallClosed() {
        // 如果是全屏模式，则退出
        stopFullScreenCountDown();

        if (TangSDKInstance.getInstance().isGroupChat()) {
            setViewMode(ViewMode.AUDIO_GROUP);

            if (groupVideoViewPager != null && groupVideoViewPager.getParent() != null) {
                groupVideoViewPager.setVisibility(View.GONE);
                ((ViewGroup) groupVideoViewPager.getParent()).removeView(groupVideoViewPager);
            }
            groupVideoViewPager = null;
            if (pagerAdapter != null) {
                pagerAdapter.clear();
                pagerAdapter = null;
            }
            if (groupVideoViews != null) {
                groupVideoViews.clear();
            }
            if (groupVideoViewMap != null) {
                groupVideoViewMap.clear();
            }
        } else {
            setViewMode(ViewMode.AUDIO_P2P);
        }
        if (isFullScreenMode) {
            startShowAnimation();
        }
    }

    /**
     * 桌面共享开始
     */
    @Override
    public void onDesktopShared() {
        this.checkShowReceiveVideoAndDesktopButton();
    }

    /**
     * 桌面共享停止
     */
    @Override
    public void onDesktopShareStopped() {
        unloadDesktopViewer();
        this.checkShowReceiveVideoAndDesktopButton();
    }

    /**
     * 桌面首帧显示通知
     */
    @Override
    public void onDesktopViewerShowed() {
        if (desktopViewer == null) {
            return;
        }
        desktopViewer.onDesktopViewerShowed();
    }

    /**
     * 桌面显示关闭通知
     */
    @Override
    public void onDesktopViewerStopped() {
        unloadDesktopViewer();
        this.checkShowReceiveVideoAndDesktopButton();
    }

    private boolean isShowingDesktopViewer() {
        return !(desktopViewer == null || !desktopViewer.isVisible());
    }

    /**
     * @brief 处理自己关闭视频共享事件
     */
    private void handleMyselfCloseVideo() {
        int videoItemCount = TangSDKInstance.getInstance().getShowedVideoItemCount();
        if (videoItemCount <= 0) {
            TangSDKInstance.getInstance().closeVideoCall();
        }
        stopWaitingAnim();
    }

    /**
     * @brief 处理1对1对方关闭视频共享事件
     */
    private void handlePeerCloseVideo() {
        int videoItemCount = TangSDKInstance.getInstance().getShowedVideoItemCount();
        if (videoItemCount <= 0) {
            TangSDKInstance.getInstance().closeVideoCall();
        }
        stopWaitingAnim();
    }

    /**
     * 通知视频首帧显示
     */
    @Override
    public void onVideoItemShowed(String userId, String domainId) {
        if (TangSDKInstance.getInstance().isGroupChat()) {
            if (groupVideoViewMap.size() <= 0)
                return;
            TangVideoView itemView = groupVideoViewMap.get(userId);
            if (itemView != null) {
                itemView.onLoadComplete();

                setVideoUserName(userId, domainId);
                showVideoUserName(true);
            }
        } else {
            if (videoP2pBigView != null) {
                videoP2pBigView.onLoadComplete();
            }
        }
    }

    @Override
    public void onVOIPQualityIsBad() {
        // prompt user
        mRlPoorNetworkNotice.setVisibility(View.VISIBLE);
        // 3秒后自动消失
        mRlPoorNetworkNotice.postDelayed(() -> mRlPoorNetworkNotice.setVisibility(View.GONE), 3000);
    }

    /**
     * 通知电话呼叫结果通知
     */
    @Override
    public void onPhoneCallResult(boolean bSucceeded) {
        if (bSucceeded) {
            Toast.makeText(getActivity(), getString(R.string.tangsdk_switch_to_phone_call_success), Toast.LENGTH_SHORT).show();
        } else {
            //自动切换回voip
            TangSDKInstance.getInstance().startVOIP();
            Toast.makeText(getActivity(), getString(R.string.tangsdk_switch_to_phone_call_failed), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 通知电话呼叫状态变化
     */
    @Override
    public void onPhoneStateChanged(PhoneState phoneState) {
        //如果电话呼叫挂断，则自动退出整个呼
        if (phoneState == PhoneState.PhoneState_Disconnect) {
            onClickStopCall();
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (dotImgs == null)
            return;
        int dotCount = dotImgs.size();
        for (int i = 0; i < dotCount; i++) {
            if (i == position) {
                dotImgs.get(i).setImageResource(R.mipmap.tangsdk_dot_selected);
            } else {
                dotImgs.get(i).setImageResource(R.mipmap.tangsdk_dot_unselect);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        showVideoUserName(false);
    }


    float yPos = 0; // down的y坐标
    float yMovedPos = 0; // move后的y坐标

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (this.mViewMode == ViewMode.VIDEO_P2P && p2pVideoOriginalState && videoP2pSmallView != null && v.hashCode() == videoP2pSmallView.hashCode()) {
            return false;
        }
        stopFullScreenCountDown();
        int minPixel = (int) PromptUtil.convertDipToPx(getActivity(), 85.0f);
        int maxPixel = (int) PromptUtil.convertDipToPx(getActivity(), 385.0f);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                yPos = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                yMovedPos = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(yMovedPos - yPos) < 20 && (yPos > minPixel && yPos < maxPixel)) {
                    if (this.isFullScreenMode) {
                        startShowAnimation();
                    } else {
                        startHiddenAnimation();
                    }
                }
                break;
        }
        return TangSDKInstance.getInstance().isGroupChat();
    }

    /**
     * @return
     * @brief 获取当前页面View模式
     */
    private ViewMode getViewMode() {
        return mViewMode;
    }

    /**
     * @param viewMode
     * @brief 根据mode设置页面当前显示
     */
    private void setViewMode(ViewMode viewMode) {
        this.mViewMode = viewMode;
        switch (viewMode) {
            case AUDIO_P2P:
                switchToAudioP2pView();
                break;
            case AUDIO_GROUP:
                switchToAudioGroupView();
                break;
            case VIDEO_P2P:
                switchToVideoP2pView();
                break;
            case VIDEO_GROUP:
                switchToVideoGroupView();
                break;
            case RECEIVE_CALL:
                switchToReceiveCallView();
                break;
            default:
                break;
        }
        boolean checked = checkVideoBtnPressed();
        videoMode_CameraSwitch.setVisibility(checked ? View.VISIBLE : View.GONE);
        checkShowReceiveVideoAndDesktopButton();
    }

    /**
     * @brief 切换到一对一音频视图
     */
    private void switchToAudioP2pView() {
        mBtnMinimizeWindow.setVisibility(View.VISIBLE);
        videoMode_CameraSwitch.setVisibility(View.GONE);
        videoGroupMode_viewAllBtn.setVisibility(View.GONE);
        videoP2pLay.setVisibility(View.GONE);
        startWaitingAnim();
        handleCallStateChanged(TangSDKInstance.getInstance().getCallState());

//        mBtnInvite.setVisibility(View.VISIBLE);
        audioContentLay.setVisibility(View.VISIBLE);
        audioUserListView.setVisibility(View.GONE);
        audioSingleLayout.setVisibility(View.VISIBLE);

        VoipMeetingMember meetingMember = TangSDKInstance.getInstance().getPeer();
        if (meetingMember == null) {
            return;
        }
        this.mTvShowName.setText(meetingMember.mShowName);

        replyMsgLay.setVisibility(View.GONE);
//        mLlControl.setVisibility(View.VISIBLE);
        mRlPickUpCall.setVisibility(View.GONE);
        handleHangUpCallView(true);
        videoGroupLay.setVisibility(View.GONE);
        dotContainer.setVisibility(View.GONE);
        speakingLay.setVisibility(View.GONE);
    }

    /**
     * @brief 切换到群音频视图
     */
    private void switchToAudioGroupView() {
        mBtnMinimizeWindow.setVisibility(View.VISIBLE);
        videoMode_CameraSwitch.setVisibility(View.GONE);
        videoGroupMode_viewAllBtn.setVisibility(View.GONE);
        handleCallStateChanged(TangSDKInstance.getInstance().getCallState());
//        mBtnInvite.setVisibility(View.VISIBLE);
        audioContentLay.setVisibility(View.VISIBLE);
        audioUserListView.setVisibility(View.VISIBLE);
        audioSingleLayout.setVisibility(View.GONE);
        mRlPickUpCall.setVisibility(View.GONE);
        handleHangUpCallView(true);
        audioStateImg.setVisibility(View.GONE);
//        mLlControl.setVisibility(View.VISIBLE);
        replyMsgLay.setVisibility(View.GONE);
        mIvWaittingLoading.setVisibility(View.INVISIBLE);
        videoGroupLay.setVisibility(View.GONE);
        dotContainer.setVisibility(View.GONE);
        speakingLay.setVisibility(View.GONE);
        // TODO 初始化列表，填充数据源，设置适配器


    }

    /**
     * @brief 切换到一对一视频视图
     */
    private void switchToVideoP2pView() {
        mBtnMinimizeWindow.setVisibility(View.VISIBLE);
        videoP2pLay.setVisibility(View.VISIBLE);
        videoGroupMode_viewAllBtn.setVisibility(View.GONE);
        handleCallStateChanged(TangSDKInstance.getInstance().getCallState());
//        mBtnInvite.setVisibility(View.VISIBLE);
        audioContentLay.setVisibility(View.GONE);
        audioUserListView.setVisibility(View.GONE);
        audioSingleLayout.setVisibility(View.GONE);
        mRlPickUpCall.setVisibility(View.GONE);
        handleHangUpCallView(true);
        audioStateImg.setVisibility(View.GONE);
//        mLlControl.setVisibility(View.VISIBLE);
        replyMsgLay.setVisibility(View.GONE);
        mIvWaittingLoading.setVisibility(View.INVISIBLE);
        videoGroupLay.setVisibility(View.GONE);
        dotContainer.setVisibility(View.GONE);
        speakingLay.setVisibility(View.GONE);
    }

    /**
     * @brief 切换到群视频视图
     */
    private void switchToVideoGroupView() {
        mBtnMinimizeWindow.setVisibility(View.VISIBLE);
        videoGroupMode_viewAllBtn.setVisibility(View.VISIBLE);
        handleCallStateChanged(TangSDKInstance.getInstance().getCallState());
//        mBtnInvite.setVisibility(View.VISIBLE);
        audioContentLay.setVisibility(View.GONE);
        audioUserListView.setVisibility(View.GONE);
        audioSingleLayout.setVisibility(View.GONE);
        mRlPickUpCall.setVisibility(View.GONE);
        handleHangUpCallView(true);
        audioStateImg.setVisibility(View.GONE);
//        mLlControl.setVisibility(View.VISIBLE);
        replyMsgLay.setVisibility(View.GONE);
        mIvWaittingLoading.setVisibility(View.INVISIBLE);
        // 初始化viewPager & surfaceView
        videoGroupLay.setVisibility(View.VISIBLE);
        videoP2pLay.setVisibility(View.GONE);
        dotContainer.setVisibility(View.VISIBLE);
        if (!isVideoSwitchAudio) {
            initGroupVideoView();
        }
        startFullScreenCountDown();
    }

    /**
     * @brief 切换到收到呼叫视图
     */
    private void switchToReceiveCallView() {

        mBtnMinimizeWindow.setVisibility(View.GONE);
        videoMode_CameraSwitch.setVisibility(View.GONE);
        videoGroupMode_viewAllBtn.setVisibility(View.GONE);

        handleCallStateChanged(TangSDKInstance.getInstance().getCallState());

//        if (TangSDKInstance.getInstance().isGroupChat()) {
//            setVoipCallStatusTipTop(R.string.tangsdk_group_call_request);
//        } else {
//            setVoipCallStatusTipTop(R.string.tangsdk_p2p_call_request);
//        }
        mBtnInvite.setVisibility(View.GONE);
        audioContentLay.setVisibility(View.VISIBLE);
        audioUserListView.setVisibility(View.GONE);
        audioSingleLayout.setVisibility(View.VISIBLE);
        mRlPickUpCall.setVisibility(View.VISIBLE);
        handleHangUpCallView(false);
        audioStateImg.setVisibility(View.GONE);
        mLlControl.setVisibility(View.GONE);
        replyMsgLay.setVisibility(View.VISIBLE);
        mIvWaittingLoading.setVisibility(View.VISIBLE);
        videoGroupLay.setVisibility(View.GONE);
        dotContainer.setVisibility(View.GONE);
        speakingLay.setVisibility(View.GONE);
        startWaitingAnim();
    }

    private void handleHangUpCallView(boolean isCancelStatus) {
        mRlHangUpCall.setVisibility(View.VISIBLE);
        if(isCancelStatus) {
            if(CallState.CallState_Init == TangSDKInstance.getInstance().getCallState() && UserType.Originator == getMySelf().getUserType()) {
                mTvHangupCall.setText(R.string.cancel);

            } else {
                mTvHangupCall.setText(R.string.hand_up);

            }

        } else {
            mTvHangupCall.setText(R.string.reject);

        }
    }

    /**
     * @brief 切换到视频共享视图
     */
    private void loadDesktopViewer() {
        if (desktopViewRoot == null) {
            return;
        }

        desktopViewRoot.setVisibility(View.VISIBLE);

        if (desktopViewer == null || !desktopViewer.isLoaded()) {
            desktopViewer = new DesktopViewer();
        }
        desktopViewer.loadView(this, desktopViewRoot, true);

        setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        enableSensor(true);
    }

    private void unloadDesktopViewer() {
        if (desktopViewer == null) {
            return;
        }

        desktopViewer.unloadView(false);
        desktopViewer = null;
        enableSensor(false);
        setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        desktopViewRoot.setVisibility(View.GONE);
    }

    @Override
    public void onCallStateChanged(CallState callState) {
        if (isAdded()) {
            getActivity().runOnUiThread(() -> handleCallStateChanged(callState));
        }
    }

    /**
     * @brief 刷新标题状态文字
     */
    private void handleCallStateChanged(CallState callState) {
        if (mTvVoipChatStatus == null || getMySelf() == null)
            return;

        UserType userType = getMySelf().getUserType();

        boolean isGroupCall = TangSDKInstance.getInstance().isGroupChat();

        Log.i(TAG, "callState->" + callState.name());

        switch (callState) {
            case CallState_Idle:
                break;
            case CallState_Init:
                if (UserType.Originator.equals(getMySelf().getUserType())) {
                    mWaitAnswerSecondsLeft = AtworkConfig.VOIP_MAX_WAIT_ANSWER_DURATION - mNoAnswerLayStayDuration;

                } else {
                    mWaitAnswerSecondsLeft = AtworkConfig.VOIP_MAX_WAIT_ANSWER_DURATION;

                }

                stopWaitAnswerCountDown();

                startWaitAnswerCountDown(); // 开启等待接听倒计时

                if (isGroupCall) {
                    if (UserType.Originator == userType) {
                        setVoipCallStatusTipTop(R.string.tangsdk_connecting_msg);

                    } else {
                        if (VoipType.VIDEO.equals(mVoipType)) {
                            setVoipCallStatusTipMiddle(R.string.voip_tip_invite_join_video_meeting);

                        } else {
                            setVoipCallStatusTipMiddle(R.string.voip_tip_invite_join_audio_meeting);

                        }
                    }


                } else {
                    if (UserType.Originator == userType) {
                        setVoipCallStatusTipMiddle(R.string.voip_tip_wait_peer_accept);
                    } else {
                        if (VoipType.VIDEO.equals(mVoipType)) {
                            setVoipCallStatusTipMiddle(R.string.voip_tip_invite_join_video_meeting);

                        } else {
                            setVoipCallStatusTipMiddle(R.string.voip_tip_invite_join_audio_meeting);

                        }
                    }
                }

                Logger.e("qsy", "qsy voip init call time -> " + System.currentTimeMillis());


                break;
            case CallState_StartCall:
                setVoipCallStatusTipTop(R.string.tangsdk_connecting_msg);
                break;
            case CallState_Waiting:
                if (UserType.Originator == userType)
                    setVoipCallStatusTipTop(R.string.tangsdk_waitting_peer_answer);
                break;
            case CallState_Calling:
                stopWaitAnswerHandler();   //结束"无人接听"提示的handler 定时任务
                stopWaitAnswerCountDown(); // 结束等待接听倒计时

                if (mFirstInitCall) {
                    // 延迟1s点亮按钮
                    mHandler.postDelayed(() -> {
                        setAllBtnAvailable(true);
                        if (VoipType.VIDEO.equals(mVoipType)) {
                            attchMySelfVideo();

                        }

                    }, 1000);

                } else {
                    setAllBtnAvailable(true);
                }
                mFirstInitCall = false;

                stopWaitingAnim();
                mIsReconnecting = false;
                mRetriesTimes = 0;

                if (UserType.Originator == userType) {
                    VoipSelectModeFragment.finishActivity();

                }

                Logger.e("qsy", "qsy voip connect success time -> " + System.currentTimeMillis());


                break;
            case CallState_Disconnected:
                setAllBtnAvailable(false);
                if (!mIsReconnecting) {
                    Toast.makeText(getActivity(), getString(R.string.tangsdk_chat_audio_reconnect_msg), Toast.LENGTH_SHORT).show();
                    //fix bug [B160114-002]
                    mHandler.postDelayed(() -> {
                        TangSDKInstance.getInstance().reconnectCall();
                        mRetriesTimes++;
                    }, 5000);
                } else {
                    if (mRetriesTimes < 6) {
                        //fix bug [B160114-002]
                        mHandler.postDelayed(() -> {
                            TangSDKInstance.getInstance().reconnectCall();
                            mRetriesTimes++;
                        }, 5000);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.tangsdk_reconnect_failed), Toast.LENGTH_SHORT).show();
                        onClickStopCall();
                        mIsReconnecting = false;
                        mRetriesTimes = 0;
                    }
                }
                break;
            case CallState_ReConnecting:
                setAllBtnAvailable(false);
                mIsReconnecting = true;
                break;
            case CallState_Ended:
                setAllBtnAvailable(false);
                mLLCover.setVisibility(View.VISIBLE);
                setVoipCallStatusTipTop(R.string.tangsdk_call_will_soon_end);

                clearData();

                finish();
                break;
            default:
                break;
        }
    }

    private void setAllBtnAvailable(boolean isAvailable) {
        if (isAvailable) {
            mLlControl.setVisibility(View.VISIBLE);
            mBtnInvite.setVisibility(View.VISIBLE);
            handleHangUpCallView(true);
        }
        mBtnInvite.setEnabled(isAvailable);
        setSpeakerButtonEnabled(isAvailable);
        setMuteButtonEnable(isAvailable);
        setVideoButtonEnabled(isAvailable);
        setAudioSwitchButtonEnable(isAvailable);
        speakerDescTV.setAlpha(isAvailable ? 1.0f : 0.35f);
        muteDescTV.setAlpha(isAvailable ? 1.0f : 0.35f);
        videoDescTV.setAlpha(isAvailable ? 1.0f : 0.35f);
        audioDescTV.setAlpha(isAvailable ? 1.0f : 0.35f);
    }

    private boolean checkVideoBtnPressed() {
        boolean checked = false;
        if (this.mViewMode == ViewMode.VIDEO_P2P || this.mViewMode == ViewMode.VIDEO_GROUP) {
            // 切换到视频模式时，若用户尚未打开免提且没有插入耳机，则自动打开免提
            if (mAuoEnableLoudSpeaker && !speakerButton.isSelected() && !isWiredHeadsetOn()) {
                onClickLouderBtn();
                mAuoEnableLoudSpeaker = false;
            }

            List<VoipMeetingMemberWrapData> videoList = TangSDKInstance.getInstance().getVideoInstanceListData();
            if (videoList != null) {
                for (VoipMeetingMemberWrapData item : videoList) {
                    if (item == null) {
                        continue;
                    }
                    if (item.getUserEntity().mUserId.equals(getMySelf().mUserId)) {
                        checked = true;
                        break;
                    }
                }
            }
            boolean bIsMeVideoItemShowed = false;
            if (this.mViewMode == ViewMode.VIDEO_GROUP) {
                if (groupVideoViewPager != null) {
                    int currentItem = groupVideoViewPager.getCurrentItem();
                    TangVideoView itemView = null;
                    if (currentItem >= 0 && currentItem < groupVideoViews.size()) {
                        itemView = groupVideoViews.get(currentItem);
                    }
                    if (itemView != null) {
                        String userId = itemView.getBindUserID();
                        bIsMeVideoItemShowed = userId.equals(getMySelf().mUserId);
                    }
                }

            }
            checked = (checked || TangSDKInstance.getInstance().getCurrentShowingVideoMySelf() || bIsMeVideoItemShowed);
        }
        if (TangSDKInstance.getInstance().getCallState() == CallState.CallState_Calling) {
            setVideoButtonChecked(checked);
        } else {
            setVideoButtonEnabled(false);
        }
        return checked;
    }

    /**
     * @brief 判断用户是否插入耳机
     */
    private boolean isWiredHeadsetOn() {
        if (getActivity() == null) {
            return false;
        }
        AudioManager am = (AudioManager) getActivity()
                .getSystemService(Context.AUDIO_SERVICE);
        return am.isWiredHeadsetOn();
    }


    /**
     * @brief 初始化显示/隐藏动画
     */
    private void initShowHiddenAnim() {
        // 控件显示动画
        showAnimation = new AlphaAnimation(0.0f, 1.0f);
        showAnimation.setDuration(300);
        showAnimation.setFillAfter(true);
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                topLay.setVisibility(View.VISIBLE);
                buttomLayout.setVisibility(View.VISIBLE);
                if (mViewMode == ViewMode.VIDEO_P2P || mViewMode == ViewMode.VIDEO_GROUP) {
                    startFullScreenCountDown();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        // 控件隐藏动画
        hiddenAnimation = new AlphaAnimation(1.0f, 0.0f);
        hiddenAnimation.setDuration(300);
        hiddenAnimation.setFillAfter(true);
        hiddenAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                topLay.setVisibility(View.GONE);
                buttomLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startShowAnimation() {
        buttomLayout.startAnimation(showAnimation);
        topLay.startAnimation(showAnimation);
        this.isFullScreenMode = false;
    }

    private void startHiddenAnimation() {
        buttomLayout.startAnimation(hiddenAnimation);
        topLay.startAnimation(hiddenAnimation);
        this.isFullScreenMode = true;
    }


    /**
     * @brief 刷新显示时长的文本框
     */
    private void updateDurationText(long nTotalSeconds) {
        long hours = nTotalSeconds / 3600;
        long left = nTotalSeconds % 3600;
        long mins = left / 60;
        long seconds = left % 60;
        setVoipCallStatusTipTop((hours > 0 ? hours + ":" : "") + String.format("%02d", mins) + ":" + String.format("%02d", seconds));
    }

    /**
     * @brief 全屏倒计时启动
     */
    private void startFullScreenCountDown() {
        mFullScreenTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage(MSGWHAT_TIMER_SECOND_FULLSCREEN);
                mHandler.sendMessage(msg);
            }
        };
        if (null != mTimer) {
            mTimer.scheduleAtFixedRate(mFullScreenTimerTask, 0L, 1000);
        }
    }

    /**
     * @brief 停止全屏计时并重置
     */
    public void stopFullScreenCountDown() {
        this.mFullScreenSecondsLeft = 5;
        if (mFullScreenTimerTask != null) {
            mFullScreenTimerTask.cancel();
            mFullScreenTimerTask = null;
        }
    }

    /**
     * @brief 等待接听倒计时启动
     */
    private void startWaitAnswerCountDown() {
        mWaitAnswerTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage(MSGWHAT_TIMER_SECOND_WAITANSWER);
                mHandler.sendMessage(msg);
            }
        };
        mTimer.scheduleAtFixedRate(mWaitAnswerTimerTask, 0L, 1000);
    }

    /**
     * @brief 停止等待接听计时并重置
     */
    public void stopWaitAnswerCountDown() {
        if (mWaitAnswerTimerTask != null) {
            mWaitAnswerTimerTask.cancel();
            mWaitAnswerTimerTask = null;
        }
    }

    public void stopWaitAnswerHandler() {
        mHandler.removeCallbacks(mNoAnswerShowRunnable);
        mNoAnswerNoticeLay.setVisibility(View.GONE);
    }

    /**
     * @param
     * @brief 防止按钮重复点击
     */
    public synchronized static boolean isFastClick(int blockTime) {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < blockTime) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * @brief 初始化数据库环境
     */
    public void initDB(String DB_NAME, int curVersion, int dbResId) {
        try {
            // 数据库文件目标存放路径为系统默认位置
            Context context = getActivity();
            String dbFileName = context.getDatabasePath(DB_NAME).getAbsolutePath();

            // 如果数据库目录尚不存在，则创建它
            File dirFile = new File(dbFileName.substring(0,
                    dbFileName.lastIndexOf(File.separatorChar)));
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }
            File file = new File(dbFileName);
            boolean isReady = false;
            if (file.exists()) {  // 数据库文件已经存在
                if (checkDBReady(DB_NAME, curVersion)) {  // 检查数据库是否可用（包括是否能打开，版本是否为最新）
                    isReady = true;
                }
            }
            if (!isReady) { // 如果数据库尚未OK，则从资源raw目录下重新初始化一份儿
                InputStream inputStream = context.getResources()
                        .openRawResource(dbResId);
                FileOutputStream out;
                out = new FileOutputStream(file);
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                byte[] buff = new byte[1024];
                int len = 0;
                while ((len = bis.read(buff)) != -1) {
                    out.write(buff, 0, len);
                }
                out.close();
                bis.close();
                inputStream.close();
            } else {
                Log.i(TAG, "initDB->数据库文件已存在");
            }
        } catch (Exception e) {
            Log.e(TAG, "initDB->拷贝失败,failedReason=" + e.getMessage());
        }
        return;
    }

    /**
     * @return boolean true:数据库环境正常，false:数据库环境需要初始化
     * @brief 检查数据库环境是否正常
     */
    public boolean checkDBReady(String DB_NAME, int curVersion) {
        Context context = getActivity();
        // 为每个帐户命名自己的数据库名称
        SQLiteDatabase checkDB = null;
        boolean isReady = false;
        try {
            // 调用系统API来获取数据库绝对路径
            String dbPath = context.getDatabasePath(DB_NAME).getAbsolutePath();
            Log.d(TAG, "checkDBReady->dbPath=" + dbPath);
            // 根据数据库绝对路径来获取数据库操作句柄
            checkDB = SQLiteDatabase.openDatabase(dbPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
            if (checkDB != null) {
                isReady = true;
            }
        } catch (SQLiteException e) {
            isReady = false;
        } finally {
            if (checkDB != null) {
                checkDB.close();
            }
        }
        return isReady;
    }

    private void showVideoUserName(boolean bShow) {
        if (bShow) {
            if (videoUserNameContainer.getVisibility() != View.VISIBLE) {
                videoUserNameContainer.clearAnimation();
                videoUserNameContainer.setAlpha(1.0f);
                videoUserNameContainer.setVisibility(View.VISIBLE);
                videoUserNameContainer.postDelayed(() -> {
                    if (videoUserNameContainer == null) {
                        return;
                    }
                    Activity activity = getActivity();
                    if (activity != null && !activity.isFinishing()) { //Ranger fix crash bug B160427-013
                        Animation hideAnimation = AnimationUtils.loadAnimation(activity, R.anim.tangsdk_hide_view_gradually);
                        if (hideAnimation != null) {
                            videoUserNameContainer.setAnimation(hideAnimation);
                            videoUserNameContainer.setVisibility(View.GONE);
                        }
                    }

                }, 5000);
            }

        } else {
            if (videoUserNameContainer.getVisibility() != View.GONE) {
                videoUserNameContainer.clearAnimation();
                videoUserNameContainer.setAlpha(0.0f);
                videoUserNameContainer.setVisibility(View.GONE);
            }
        }
    }

    private void setVideoUserName(String strUserId, String domainId) {
        if (groupVideoViews == null || groupVideoViewPager == null) {
            return;
        }
        VoipMeetingMemberWrapData userItemData = TangSDKInstance.getInstance().getUserItemDataByUserId(strUserId, domainId);
        if (userItemData != null) {
            String strUserName = userItemData.getUserEntity().mShowName;
            if (TextUtils.isEmpty(strUserName)) {
                strUserName = userItemData.getUserEntity().mUserId;
            }
            if (!TextUtils.isEmpty(strUserName)) {
                mTvVideoUserName.setText(strUserName);
            }
        }
    }

    private void setVoipCallStatusTipMiddle(int contentRes) {
        setVoipCallStatusTipMiddle(getString(contentRes));
    }

    private void setVoipCallStatusTipMiddle(String content) {
        mTvVoipChatStatus.setText(StringUtils.EMPTY);
        mTvVoipCallTip.setText(content);
    }


    private void setVoipCallStatusTipTop(int contentRes) {
        setVoipCallStatusTipTop(getString(contentRes));
    }

    private void setVoipCallStatusTipTop(String content) {
        mTvVoipChatStatus.setText(content);
        mTvVoipCallTip.setText(StringUtils.EMPTY);
    }


}