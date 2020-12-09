package com.foreveross.atwork.modules.voip.activity.qsy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.api.sdk.voip.responseJson.CreateOrQueryMeetingResponseJson;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.CallParams;
import com.foreveross.atwork.infrastructure.model.voip.CallState;
import com.foreveross.atwork.infrastructure.model.voip.CallType;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.UserStatus;
import com.foreveross.atwork.infrastructure.model.voip.UserType;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.modules.discussion.activity.DiscussionMemberSelectActivity;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberSelectControlAction;
import com.foreveross.atwork.modules.group.module.UserSelectControlAction;
import com.foreveross.atwork.modules.voip.activity.CallActivity;
import com.foreveross.atwork.modules.voip.fragment.qsy.QsyCallFragment;
import com.foreveross.atwork.modules.voip.service.CallService;
import com.foreveross.atwork.modules.voip.support.qsy.TangSDKInstance;
import com.foreveross.atwork.modules.voip.support.qsy.interfaces.ICallDelegate;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import java.util.ArrayList;
import java.util.List;


public class QsyCallActivity extends CallActivity implements ICallDelegate {

    public static final String EXTRA_CALL_PARAMS = "extra_call_params";
    public static final String EXTRA_QSY_MEETING_ID = "extra_qsy_meeting_id";
    public static final String EXTRA_WORKPLUS_MEETING_ID = "extra_workplus_meeting_id";
    public static final String EXTRA_JOIN_TOKEN = "extra_join_token";
    public static final String EXTRA_MEETING_INFO = "extra_meeting_info";
    public static final String EXTRA_VOIP_TYPE = "extra_voip_type";
    public static final String EXTRA_INVITER = "extra_inviter";

    private QsyCallFragment mCallFragment = null;
    private boolean mIsFromFloating = false;
    private String mJoinToken = "";
    private String mQsyVoipMeetingId = "";
    private String mWorkplusVoipMeetingId = "";
    private MeetingInfo mMeetingInfo;
    private VoipType mVoipType;
    private UserHandleInfo mInviter;

    public CallParams mCallParams = new CallParams();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        processExtra();
        LogUtil.e("voip", "voip oncreate");

        super.onCreate(savedInstanceState);

        Context appContext = getApplicationContext();
        TangSDKInstance.getInstance().init(appContext, AtWorkDirUtils.getInstance().getQsyVoipLOG());

        requestPhoneStatePermission();

        //锁屏情况下需要优先显示接电话界面
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        CallService.sendRemoveFloatingWindow();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected Fragment createFragment() {
        return initFragment();
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, QsyCallActivity.class);
        return intent;
    }

    @Override
    public boolean checkDomainSettingUpdate() {
        //do nothing
        return false;
    }

    @Override
    public void registerUpdateReceiver() {
        //do nothing
    }

    private void processExtra() {

        Intent intent = getIntent();

        mQsyVoipMeetingId = "";
        mCallParams = null;

        mIsFromFloating = intent.getBooleanExtra(QsyCallActivity.EXTRA_START_FROM_OUTSIDE, false);
        if (mIsFromFloating) { // start from floating window, reinitialize data
            mCallParams = new CallParams(TangSDKInstance.getInstance().getMySelf(), TangSDKInstance.getInstance().getPeer(), TangSDKInstance.getInstance().getGroup());
            String meetingId = TangSDKInstance.getInstance().getWorkplusVoipMeetingId();
            if (!StringUtils.isEmpty(meetingId)) {
                mWorkplusVoipMeetingId = meetingId;
            }

            mMeetingInfo = TangSDKInstance.getInstance().getMeetingInfo();
            mVoipType = TangSDKInstance.getInstance().getVoipType();

        } else {

            mCallParams = intent.getParcelableExtra(QsyCallActivity.EXTRA_CALL_PARAMS);

            String qsyVoipMeetingId = intent.getStringExtra(QsyCallActivity.EXTRA_QSY_MEETING_ID);
            if (!TextUtils.isEmpty(qsyVoipMeetingId)) {
                mQsyVoipMeetingId = qsyVoipMeetingId;
            }

            mJoinToken = intent.getStringExtra(QsyCallActivity.EXTRA_JOIN_TOKEN);
            mWorkplusVoipMeetingId = intent.getStringExtra(QsyCallActivity.EXTRA_WORKPLUS_MEETING_ID);
            mMeetingInfo = intent.getParcelableExtra(QsyCallActivity.EXTRA_MEETING_INFO);
            mVoipType = (VoipType) intent.getSerializableExtra(QsyCallActivity.EXTRA_VOIP_TYPE);

        }


        mInviter = intent.getParcelableExtra(QsyCallActivity.EXTRA_INVITER);


    }

    private Fragment initFragment() {
        mCallFragment = QsyCallFragment.newInstance(getApplicationContext());
        mCallFragment.setDelegate(this);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_VOIP_TYPE, mVoipType);
        mCallFragment.setArguments(bundle);
//        getSupportFragmentManager().beginTransaction().replace(R.id.call_fragment_holder, mCallFragment).commit();

        // P2P
        if (!mIsFromFloating) {
            if (isGroupCall()) {
                mCallFragment.initGroupCall(CallType.CallType_Audio.value(), mCallParams.mMySelf, mCallParams.mGroup);
            } else {
                if (UserType.Originator == mCallParams.mMySelf.getUserType()) {
                    TangSDKInstance.getInstance().enableLoudSpeaker(false);
                } else {
                    TangSDKInstance.getInstance().enableLoudSpeaker(true);
                }
                mCallFragment.initPeerCall(CallType.CallType_Audio.value(), mCallParams.mMySelf, mCallParams.mPeer);
            }

            if (!TextUtils.isEmpty(mWorkplusVoipMeetingId)) {
                TangSDKInstance.getInstance().setWorkplusMeetingInfo(mWorkplusVoipMeetingId, mMeetingInfo, mVoipType);
            }
        }

        return mCallFragment;
    }

    private boolean isGroupCall() {
        return !(mCallParams == null || mCallParams.mMySelf == null || mCallParams.mGroup == null);
    }


    public void startMeetingCall() {

        if (mCallParams == null || mCallParams.mMySelf == null) {
            TangSDKInstance.getInstance().stopCall();
            return;
        }

        mCallFragment.startCallByJoinKey(mJoinToken);

        LogUtil.e("VOIP", "VOIP CONF_ID : " + mQsyVoipMeetingId + "   joinToken : " + mJoinToken);
    }


    //Implement ICallDelegate interface

    /**
     * 通知应用去获取调起串
     */
    @Override
    public void onStartVoipMeeting() {
        // 取调起串
        if (null == mCallParams || null == mCallParams.mMySelf)
            return;

        if (UserType.Originator == mCallParams.mMySelf.getUserType()) {
            if (!StringUtils.isEmpty(mWorkplusVoipMeetingId)) { // 加入已存在的呼场景

                joinMeeting(mWorkplusVoipMeetingId);

            } else {
                createMeeting();


            }
        } else if (UserType.Recipient == mCallParams.mMySelf.getUserType()) {

            joinMeeting(mWorkplusVoipMeetingId);

        }
    }


    /**
     * 获取用户信息
     */
    @Override
    public void onStartGetUserProfile(String userId, String domainId) {

        UserManager.getInstance().queryUserByUserId(this, userId, domainId, new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void onSuccess(@NonNull User user) {
                VoipMeetingMember meetingMember = new VoipMeetingMember(userId, domainId, UserType.Recipient, user.getShowName(), user.mAvatar, UserStatus.UserStatus_NotJoined);
                if (mCallFragment != null) {
                    mCallFragment.refreshUserProfile(meetingMember);
                }
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleBaseError(errorCode, errorMsg);
            }
        });


    }

    /**
     * 获取群信息
     */
    @Override
    public void onStartGetGroupProfile(VoipMeetingGroup voipMeetingGroup) {
        if (mCallFragment != null) {
            mCallFragment.refreshGroupProfile(voipMeetingGroup, mInviter);
        }

    }

    /**
     * 获取在会议过程中添加的成员
     */
    @Override
    public void onInviteMember() {
        Intent intent;
        if (null != mMeetingInfo && MeetingInfo.Type.DISCUSSION.equals(mMeetingInfo.mType)) {
            DiscussionMemberSelectControlAction discussionMemberSelectControlAction = new DiscussionMemberSelectControlAction();
            discussionMemberSelectControlAction.setSelectedContacts(TangSDKInstance.getInstance().getVoipMemInMeetingList());
            discussionMemberSelectControlAction.setDiscussionId(mMeetingInfo.mId);
            discussionMemberSelectControlAction.setSelectMode(DiscussionMemberSelectActivity.Mode.VOIP);

            intent = DiscussionMemberSelectActivity.getIntent(this, discussionMemberSelectControlAction);

        } else {
            UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
            userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
            userSelectControlAction.setSelectAction(UserSelectActivity.SelectAction.VOIP);
            userSelectControlAction.setSelectedContacts(TangSDKInstance.getInstance().getVoipMemInMeetingList());
            userSelectControlAction.setFromTag(TAG);

            intent = UserSelectActivity.getIntent(this, userSelectControlAction);

        }

        startActivityForResult(intent, CODE_INVITE_VOIP_MEETING);
    }


    /**
     * 会议启动成功
     */
    @Override
    public void onStartCallSuccess() {

    }

    /**
     * 会议启动失败
     */
    @Override
    public void onStartCallFailure() {

    }


    /**
     * 隐藏呼叫界面，呼叫在后台进行
     */
    @Override
    public void onHideView() {
        if (mCallFragment != null) {
            mCallFragment.clearData();
        }
        finish();

        if (AtworkConfig.VOIP_NEED_FLOATVIEW_DESKTOP_SHOW) {
            CallService.sendCreateFloatingWindow();

        }
    }


    /**
     * 1对1呼叫超时
     */
    @Override
    public void onTimeOut() {

    }

    /**
     * 有参会人加入
     */
    @Override
    public void onParticipantEnter(VoipMeetingMember participant) {

    }

    /**
     * 有参会人离开
     */
    @Override
    public void onParticipantLeave(VoipMeetingMember participant) {

    }

    /**************拨打方**************/


    /**
     * 主动取消呼叫(尚未拨通)
     */
    @Override
    public void onCancelCall() {
        rejectVoipMeeting();
    }


    /**
     * 结束呼叫(已经拨通)
     */
    @Override
    public void onFinishCall(int memberCount) {
        if (0 < memberCount) {
            leaveVoipMeeting();
        }
    }


    /**************接收方**************/

    /**
     * 接受服务
     */
    @Override
    public void onAcceptCall() {

    }

    /**
     * 拒绝服务
     */
    @Override
    public void onRejectCall() {
        rejectVoipMeeting();
    }

    /**
     * 获取常用回复消息
     */
    @Override
    public ArrayList<String> getQuickReplayMessages() {

        return null;
    }

    /**
     * 发送回复消息
     */
    @Override
    public void onSendQuickReplayMessage(int index) {

    }

    /**************获取会议对象信息**************/

    /**
     * @return 我所有的号码
     * @brief 获取我的手机号码列表
     */
    @Override
    public ArrayList<String> getMyPhoneNumberList() {

        return null;
    }

    /**
     * 新输入了电话号码呼叫
     */
    @Override
    public void onNewPhoneNumberCalled(String phoneNumber) {

    }

    /**
     * @return 某被叫方所有电话号码
     * @brief 获取某被叫方所有的号码
     */
    @Override
    public ArrayList<String> getPhoneNumberList(String userId) {

        return null;
    }

    /**
     * 通知服务器离开会议
     */
    public void leaveVoipMeeting() {
        VoipManager.leaveMeeting(QsyCallActivity.this, null, null, mWorkplusVoipMeetingId, AtworkApplicationLike.getLoginUserHandleInfo(QsyCallActivity.this), null, new VoipManager.OnHandleVoipMeetingListener() {
            @Override
            public void onSuccess() {
                LogUtil.e("VOIP", "leave success");
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);

            }
        });

    }


    /**
     * 通知服务器拒绝电话
     */
    public void rejectVoipMeeting() {
        if (!StringUtils.isEmpty(mWorkplusVoipMeetingId)) {
            VoipManager.getInstance().rejectMeeting(QsyCallActivity.this, null, null, mWorkplusVoipMeetingId, null, new VoipManager.OnHandleVoipMeetingListener() {
                @Override
                public void onSuccess() {
                    LogUtil.e("VOIP", "reject success");

                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);

                }
            });
        }
    }


    /**
     * 通知服务器加入会议
     */
    public void joinMeeting(String workplusVoipMeetingId) {
        VoipManager.joinMeeting(QsyCallActivity.this, mMeetingInfo, workplusVoipMeetingId, mVoipType, null, new VoipManager.OnGetJoinTokenListener() {
            @Override
            public void onSuccess(String token) {
                mJoinToken = token;
                startMeetingCall();

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);

            }
        });
    }

    /**
     * 通知服务器创建会议
     */
    public void createMeeting() {
        VoipManager.getInstance().createMeeting(this, mMeetingInfo, mVoipType, mCallParams.getCallMemberList(), new VoipManager.OnCreateAndQueryVoipMeetingListener() {

            @Override
            public void onSuccess(CreateOrQueryMeetingResponseJson responseJson) {
                VoipMeetingGroup voipMeetingGroup = responseJson.toVoipMeetingGroup(mVoipType);

                mWorkplusVoipMeetingId = responseJson.mResult.mMeetingId;
                mQsyVoipMeetingId = responseJson.mResult.mQsyConferenceInfo.mConferenceId;
                TangSDKInstance.getInstance().setWorkplusMeetingInfo(mWorkplusVoipMeetingId, mMeetingInfo, mVoipType);

                //单对单语音会话, 在对方 join 了后才自己加入
                if (!MeetingInfo.Type.USER.equals(mMeetingInfo.mType)) {
                    joinMeeting(responseJson.mResult.mMeetingId);
                }

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);

                TangSDKInstance.getInstance().stopCall();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CODE_INVITE_VOIP_MEETING == requestCode && RESULT_OK == resultCode) {
            List<ShowListItem> contacts = SelectedContactList.getContactList();

            VoipManager.getInstance().filterContactsInMeeting(contacts);

            VoipManager.getInstance().inviteMeeting(this, mWorkplusVoipMeetingId, mMeetingInfo, mVoipType, ContactHelper.transferContactList(contacts), new VoipManager.OnInviteVoipMeetingListener() {
                @Override
                public void onSuccess(List<VoipMeetingMember> meetingMemberInvitedList) {

                    ArrayList<VoipMeetingMember> meetingMemberList = new ArrayList<>();
                    for (ShowListItem userSelect : contacts) {

                        VoipMeetingMember voipMeetingMember = ContactHelper.toBasicVoipMeetingMember(userSelect);
                        voipMeetingMember.setUserType(UserType.Recipient);
                        meetingMemberList.add(voipMeetingMember);
                    }
                    if (null == TangSDKInstance.getInstance().getGroup()) {
                        VoipMeetingGroup newGroup = TangSDKInstance.getInstance().transfer2Group();
                        TangSDKInstance.getInstance().switchToGroup(newGroup);
                    }

                    VoipManager.getInstance().getTimeController().monitorVoipMembers(QsyCallActivity.this, meetingMemberList);

                    mCallFragment.addParticipants(meetingMemberList);


                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);

                }
            });

        }
    }

    private void requestPhoneStatePermission() {

        if(CallState.CallState_Init != VoipManager.getInstance().getCallState()) {
            return;
        }

        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(QsyCallActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, new PermissionsResultAction() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(String permission) {
                AtworkAlertDialog alertDialog = AtworkUtil.getAuthSettingAlert(QsyCallActivity.this, permission);
                alertDialog.show();
            }
        });
    }
}
