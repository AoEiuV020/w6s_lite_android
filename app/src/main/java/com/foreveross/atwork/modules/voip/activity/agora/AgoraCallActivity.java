package com.foreveross.atwork.modules.voip.activity.agora;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.api.sdk.voip.responseJson.CreateOrQueryMeetingResponseJson;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.SelectedContactList;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.CallParams;
import com.foreveross.atwork.infrastructure.model.voip.CurrentVoipMeeting;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.UserType;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.manager.VoipMeetingController;
import com.foreveross.atwork.modules.discussion.activity.DiscussionMemberSelectActivity;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberSelectControlAction;
import com.foreveross.atwork.modules.group.module.UserSelectControlAction;
import com.foreveross.atwork.modules.voip.activity.CallActivity;
import com.foreveross.atwork.modules.voip.fragment.agora.AgoraCallFragment;
import com.foreveross.atwork.modules.voip.service.CallService;
import com.foreveross.atwork.modules.voip.support.agora.interfaces.OnControllerVoipListener;
import com.foreveross.atwork.modules.voip.utils.SoundHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 16/9/13.
 */
public class AgoraCallActivity extends CallActivity implements OnControllerVoipListener {

    public static final String EXTRA_VOIP_TYPE = "extra_voip_type";
    public static final String EXTRA_JOIN_TOKEN = "extra_join_token";
    public static final String EXTRA_INVITER = "extra_inviter";

    private AgoraCallFragment mCallFragment;

    private boolean mIsFromOutSide = false;

    public CallParams mCallParams = null;

    private String mWorkplusVoipMeetingId;
    private MeetingInfo mMeetingInfo = null;
    private VoipType mVoipType = null;
    private UserHandleInfo mInviter = null;

    private String mJoinToken = "";


    private boolean mNeedCommonRequestTip = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        processExtra();

        super.onCreate(savedInstanceState);


        if(PermissionsManager.getInstance().hasPermission(this, Manifest.permission.RECORD_AUDIO)) {
            VoipMeetingController.getInstance().init(this);
        }

        //锁屏情况下需要优先显示接电话界面
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AgoraCallActivity.class);
        return intent;
    }

    private void processExtra() {
        Intent intent = getIntent();
        mIsFromOutSide = intent.getBooleanExtra(AgoraCallActivity.EXTRA_START_FROM_OUTSIDE, false);
        mInviter = intent.getParcelableExtra(AgoraCallActivity.EXTRA_INVITER);


        if(mIsFromOutSide) {

        } else {
            mJoinToken = intent.getStringExtra(AgoraCallActivity.EXTRA_JOIN_TOKEN);

            SoundHelper.getInstance().init(this);
        }

        CurrentVoipMeeting currentVoipMeeting = VoipMeetingController.getInstance().getCurrentVoipMeeting();

        if (null != currentVoipMeeting) {
            mCallParams = currentVoipMeeting.mCallParams;
            mWorkplusVoipMeetingId = currentVoipMeeting.mWorkplusVoipMeetingId;
            mMeetingInfo = currentVoipMeeting.mMeetingInfo;
            mVoipType = currentVoipMeeting.mVoipType;

        }  else {
            finish();

//            AtworkToast.showResToast(R.string.);
        }


    }

    @Override
    protected Fragment createFragment() {
        initFragment();
        return mCallFragment;
    }

    private void initFragment() {
        mCallFragment = new AgoraCallFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_VOIP_TYPE, mVoipType);
        bundle.putBoolean(EXTRA_START_FROM_OUTSIDE, mIsFromOutSide);
        bundle.putParcelable(EXTRA_INVITER, mInviter);
        mCallFragment.setArguments(bundle);

        VoipMeetingController.getInstance().setOnControllerVoipListener(this);
        VoipMeetingController.getInstance().setOnVoipStatusListener(mCallFragment);

    }

    public boolean isNeedCommonRequestTip() {
        return mNeedCommonRequestTip;
    }

    public void setNeedCommonRequestTip(boolean mNeedCommonRequestTip) {
        this.mNeedCommonRequestTip = mNeedCommonRequestTip;
    }


    @Override
    public void onStartVoipMeeting() {
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

    @Override
    public void onInviteMember() {
        List<VoipMeetingMember> voipMemInMeetingList = VoipMeetingController.getInstance().getVoipMemInMeetingList();

        Intent intent;
        if (null != mMeetingInfo && MeetingInfo.Type.DISCUSSION.equals(mMeetingInfo.mType)) {
            DiscussionMemberSelectControlAction discussionMemberSelectControlAction = new DiscussionMemberSelectControlAction();
            discussionMemberSelectControlAction.setSelectedContacts(voipMemInMeetingList);
            discussionMemberSelectControlAction.setDiscussionId(mMeetingInfo.mId);
            discussionMemberSelectControlAction.setSelectMode(DiscussionMemberSelectActivity.Mode.VOIP);

            intent = DiscussionMemberSelectActivity.getIntent(this, discussionMemberSelectControlAction);

        } else {
            UserSelectControlAction userSelectControlAction = new UserSelectControlAction();
            userSelectControlAction.setSelectMode(UserSelectActivity.SelectMode.SELECT);
            userSelectControlAction.setSelectAction(UserSelectActivity.SelectAction.VOIP);
            userSelectControlAction.setSelectedContacts(voipMemInMeetingList);
            userSelectControlAction.setFromTag(TAG);


            intent = UserSelectActivity.getIntent(this, userSelectControlAction);

        }

        startActivityForResult(intent, CODE_INVITE_VOIP_MEETING);

    }

    @Override
    public void onStartCallSuccess() {

    }

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


    @Override
    public void onCancelCall() {
        rejectVoipMeeting();

    }

    @Override
    public void onFinishCall(int memberCount) {
        leaveVoipMeeting();
    }

    @Override
    public void onTimeOut() {

    }

    @Override
    public void onParticipantEnter(VoipMeetingMember participant) {

    }

    @Override
    public void onParticipantLeave(VoipMeetingMember participant) {

    }

    @Override
    public void onAcceptCall() {

    }

    @Override
    public void onRejectCall() {
        rejectVoipMeeting();

    }

    /**
     * 通知服务器拒绝电话
     */
    public void rejectVoipMeeting() {
        if (!StringUtils.isEmpty(mWorkplusVoipMeetingId)) {
            VoipManager.getInstance().rejectMeeting(AgoraCallActivity.this, null, VoipMeetingController.getInstance().getCurrentVoipMeeting(), mWorkplusVoipMeetingId, null, new VoipManager.OnHandleVoipMeetingListener() {
                @Override
                public void onSuccess() {
                    LogUtil.e("VOIP", "reject success");

                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    //todo 优化ErrorHandleUtil代码
                    if(!ErrorHandleUtil.handleTokenError(errorCode, errorMsg)) {


                        if(isNeedCommonRequestTip()) {
                            ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);

                        }
                    }

                }
            });
        }
    }


    /**
     * 通知服务器加入会议
     */
    public void joinMeeting(String workplusVoipMeetingId) {
        VoipManager.joinMeeting(AgoraCallActivity.this, mMeetingInfo, workplusVoipMeetingId, mVoipType, null, new VoipManager.OnGetJoinTokenListener() {
            @Override
            public void onSuccess(String token) {
                mJoinToken = token;
//                startMeetingCall();
                VoipMeetingController.getInstance().startCallByJoinKey(workplusVoipMeetingId, token);

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

                //单对单语音会话, 在对方 join 了后才自己加入
                if (!MeetingInfo.Type.USER.equals(mMeetingInfo.mType)) {
                    joinMeeting(responseJson.mResult.mMeetingId);
                }

                //自己主动创建会议的时候, 会议 id 需要接口回调后设置
                VoipMeetingController.getInstance().setCurrentVoipMeetingId(mWorkplusVoipMeetingId);
//                VoipMeetingController.getInstance().setCurrentVoipMeetingDomainId(responseJson.mResult.mDomainId);

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);

                VoipMeetingController.getInstance().stopCall();
            }
        });
    }

    /**
     * 通知服务器离开会议
     */
    public void leaveVoipMeeting() {
        VoipManager.leaveMeeting(AgoraCallActivity.this, null, VoipMeetingController.getInstance().getCurrentVoipMeeting(), mWorkplusVoipMeetingId, AtworkApplicationLike.getLoginUserHandleInfo(AgoraCallActivity.this), null, new VoipManager.OnHandleVoipMeetingListener() {
            @Override
            public void onSuccess() {
                LogUtil.e("VOIP", "leave success");
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                //todo 优化ErrorHandleUtil代码
                if(!ErrorHandleUtil.handleTokenError(errorCode, errorMsg)) {


                    if(isNeedCommonRequestTip()) {
                        ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);

                    }
                }


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

                    for (VoipMeetingMember member : meetingMemberInvitedList) {

                        member.setUserType(UserType.Recipient);
                    }
                    if (null == VoipMeetingController.getInstance().getGroup()) {
                        VoipMeetingGroup newGroup = VoipMeetingController.getInstance().transfer2Group();
                        VoipMeetingController.getInstance().switchToGroup(newGroup);
                    }

                    VoipManager.getInstance().getTimeController().monitorVoipMembers(AgoraCallActivity.this, meetingMemberInvitedList);

                    VoipMeetingController.getInstance().addParticipants((ArrayList<VoipMeetingMember>) meetingMemberInvitedList);


                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);

                }
            });

        }
    }


}
