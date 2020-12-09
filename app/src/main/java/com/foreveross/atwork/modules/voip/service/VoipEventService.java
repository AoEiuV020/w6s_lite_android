package com.foreveross.atwork.modules.voip.service;

import android.content.Context;

import com.foreverht.db.service.repository.VoipMeetingRecordRepository;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.voip.VoipMeetingSyncService;
import com.foreveross.atwork.api.sdk.voip.responseJson.CreateOrQueryMeetingResponseJson;
import com.foreveross.atwork.db.daoService.UserDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.chat.VoipChatMessage;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.CallState;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.MeetingStatus;
import com.foreveross.atwork.infrastructure.model.voip.UserStatus;
import com.foreveross.atwork.infrastructure.model.voip.UserType;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.VoipPostMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.VoipSdkType;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.MapUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.manager.VoipMeetingController;
import com.foreveross.atwork.manager.VoipNoticeManager;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.modules.chat.util.SystemChatMessageHelper;
import com.foreveross.atwork.modules.voip.activity.CallActivity;
import com.foreveross.atwork.modules.voip.fragment.VoipHistoryFragment;
import com.foreveross.atwork.modules.voip.utils.VoipEventServiceHelper;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.SystemMessageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * voip 事件消息处理 service
 * <p>
 * Created by dasunsy on 16/7/15.
 */
public class VoipEventService {

    public static final String TAG = "VOIP";

    public static final Object sLock = new Object();

    public static final Object sHandleLock = new Object();

    public static VoipEventService sInstance = null;

    public OfflineEventController mOfflineEventController = new OfflineEventController();

    public List<String> mMeetingNeedLeaveList = new ArrayList<>();

    private VoipEventService() {

    }

    public static VoipEventService getInstance() {
        /**
         * double check
         * */

        if (null == sInstance) {
            synchronized (sLock) {
                if (null == sInstance) {
                    sInstance = new VoipEventService();
                }

            }
        }

        return sInstance;

    }

    public OfflineEventController getOfflineEventController() {
        return mOfflineEventController;
    }


    /**
     * 添加"退出会议"队列
     * */
    public void enqueueLeavingMeeting(String meetingId) {
        mMeetingNeedLeaveList.add(meetingId);
    }

    /**
     * 处理在线 voip 消息
     */
    public void receiveVoipPostMessage(VoipPostMessage voipPostMessage) {
        Context baseContext = BaseApplicationLike.baseContext;

        VoipEventServiceHelper.assembleVoipType(voipPostMessage);

        //未知类型的 voip, 不做任何处理
        if(!voipPostMessage.isLegal()) {
            return;
        }


        //收到呼叫会议
        if (VoipPostMessage.Operation.CREATED.equals(voipPostMessage.mOperation)) {

            handleCreatedEvent(baseContext, voipPostMessage);

            handleCreatedChatMessage(baseContext, voipPostMessage, true);



        } else if (VoipPostMessage.Operation.MEMBER_INVITED.equals(voipPostMessage.mOperation)) {

            handleInvitedEvent(baseContext, voipPostMessage);

            handleInvitedChatMessage(baseContext, voipPostMessage, true);



        } else if (VoipPostMessage.Operation.MEMBER_LEAVED.equals(voipPostMessage.mOperation)) {

            handleLeavedEvent(baseContext, voipPostMessage);

            handleLeaveChatMessage(baseContext, voipPostMessage, true);

        } else if (VoipPostMessage.Operation.MEMBER_REJECTED.equals(voipPostMessage.mOperation)) {

            handleRejectedEvent(baseContext, voipPostMessage);

            handleRejectedChatMessage(baseContext, voipPostMessage, true);

//            handleRejectedNotification(baseContext, voipPostMessage, true);




        } else if (VoipPostMessage.Operation.ENDED.equals(voipPostMessage.mOperation)) {
            handleEndedEvent(baseContext, voipPostMessage);

            handleEndedChatMessage(baseContext, voipPostMessage, true);

        } else if (VoipPostMessage.Operation.MEMBER_BUSY.equals(voipPostMessage.mOperation)) {
            handleBusyEvent(baseContext, voipPostMessage);

        } else if (VoipPostMessage.Operation.MEMBER_JOINED.equals(voipPostMessage.mOperation)) {
            handleJoinedEvent(baseContext, voipPostMessage);
        }
    }

    public void doReject(final Context context, VoipPostMessage voipPostMessage) {
        VoipManager.getInstance().rejectMeeting(context, null, null, voipPostMessage.mMeetingId, null, new VoipManager.OnHandleVoipMeetingListener() {
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



    public void handleCreatedEvent(Context context, VoipPostMessage voipPostMessage) {
        synchronized (sHandleLock) {
            //非自己则吊起电话
            if (!User.isYou(context, voipPostMessage.mOperator.mUserId)) {

                if (!VoipHelper.isHandlingCall()) {
                    //insert db
                    VoipEventServiceHelper.insertVoipMeeting(voipPostMessage, true);

                    boolean isGroupType = VoipHelper.isGroupType(voipPostMessage.mMeetingInfo);

                    //群组时, 维护定时器. 用于管理群聊成员的状态
                    if (isGroupType) {
                        VoipManager.getInstance().getTimeController().monitorVoipMembers(context, voipPostMessage.mMemberList);
                    }

                    VoipHelper.goToCallActivity(context, voipPostMessage.mMeetingInfo, voipPostMessage.mGateWay.mVoipType, isGroupType, voipPostMessage.mMemberList, false, voipPostMessage.mGateWay.mId, voipPostMessage.mMeetingId, null, voipPostMessage.mOperator);

                } else {
                    doBusy(context, voipPostMessage);
                }

            } else {

                VoipMeetingController.getInstance().refreshCurrentMeetingMembersUid(voipPostMessage.mMemberList);

                //insert db
                VoipEventServiceHelper.insertVoipMeeting(voipPostMessage, true);
            }
        }


    }

    public void doBusy(Context context, VoipPostMessage voipPostMessage) {
        VoipManager.busyMeeting(context, voipPostMessage.mMeetingInfo, voipPostMessage.mMeetingId, new VoipManager.OnHandleVoipMeetingListener() {
            @Override
            public void onSuccess() {
                LogUtil.e("voip", "busying~~~");
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
            }
        });
    }



    public void handleInvitedEvent(final Context context, final VoipPostMessage voipPostMessage) {

        synchronized (sHandleLock) {
            if (voipPostMessage.membersContainsMe(context)) {

                if (AtworkUtil.isSystemCalling()) {
                    doBusy(context, voipPostMessage);

                } else if(VoipHelper.isHandlingVoipCall()){

                    //存在同时被多人同会议的人邀请的可能性, 所以如果是同个会议的邀请通知, 不发出busy
                    if(!VoipEventServiceHelper.isCurrentMeetingHandling(voipPostMessage)) {
                        doBusy(context, voipPostMessage);
                    }

                } else {
                    VoipManager.queryMeetingRemote(context, voipPostMessage.mMeetingId, new VoipManager.OnCreateAndQueryVoipMeetingListener() {
                        @Override
                        public void onSuccess(CreateOrQueryMeetingResponseJson responseJson) {
                            VoipMeetingGroup voipMeetingGroup = responseJson.toVoipMeetingGroup(voipPostMessage.mGateWay.mVoipType);
                            if (null != voipPostMessage.mMeetingInfo) {
                                voipMeetingGroup.mMeetingInfo.mType = voipPostMessage.mMeetingInfo.mType;
                                voipMeetingGroup.mAvatar = voipPostMessage.mMeetingInfo.mAvatar;
                            }

                            VoipManager.getInstance().getOfflineController().checkOfflineStatus(context, voipMeetingGroup.mParticipantList);

                            VoipManager.getInstance().getTimeController().monitorVoipMembers(context, voipMeetingGroup.mParticipantList);

                            VoipManager.getInstance().insertVoipMeetingGroupSync(voipMeetingGroup);

                            VoipHelper.goToCallActivity(context, voipMeetingGroup.mMeetingInfo, voipMeetingGroup.mVoipType, true, voipMeetingGroup.mParticipantList, false, voipPostMessage.mGateWay.mId, voipPostMessage.mMeetingId, null, voipPostMessage.mOperator);


                        }

                        @Override
                        public void networkFail(int errorCode, String errorMsg) {
                            ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);
                        }
                    });

                }

            } else { //邀请他人, 此时需要转换

                if (!User.isYou(context, voipPostMessage.mOperator.mUserId)) {
                    if (!VoipManager.getInstance().getVoipMeetingController().isGroupChat() && VoipEventServiceHelper.isSdkBasedCurrentMeetingHandling(voipPostMessage)) {
                        VoipMeetingGroup newGroup = VoipManager.getInstance().getVoipMeetingController().transfer2Group();
                        VoipManager.getInstance().getVoipMeetingController().switchToGroup(newGroup);
                    }

                    for (VoipMeetingMember member : voipPostMessage.mMemberList) {
                        member.setUserType(UserType.Recipient);

                    }

                    //维护定时器. 用于管理群聊成员的状态
                    VoipManager.getInstance().getTimeController().monitorVoipMembers(context, voipPostMessage.mMemberList);

                    VoipManager.getInstance().getVoipMeetingController().addParticipants((ArrayList<VoipMeetingMember>) voipPostMessage.mMemberList);
                }


            }
        }
    }

    public void handleJoinedEvent(Context context, VoipPostMessage voipPostMessage) {
        synchronized (sHandleLock) {
            if (VoipEventServiceHelper.isSdkBasedCurrentMeetingHandling(voipPostMessage)) {

                if (VoipHelper.isHandlingVoipCall()) {

                    LogUtil.e("join_bug", "join bug in");

                    VoipManager.getInstance().getTimeController().cancel(voipPostMessage.mOperator.mUserId);

                    if (!VoipManager.getInstance().getVoipMeetingController().isGroupChat()) {

                        if (MeetingInfo.Type.USER.equals(voipPostMessage.mMeetingInfo.mType)
                                && User.isYou(context, voipPostMessage.mCreator.mUserId)
                                && voipPostMessage.mOperator.mUserId.equals(voipPostMessage.mMeetingInfo.mId)
                                && isNotTryingCalling()) {

                            VoipManager.joinMeeting(context, voipPostMessage.mMeetingInfo, voipPostMessage.mMeetingId, voipPostMessage.mGateWay.mVoipType, null, new VoipManager.OnGetJoinTokenListener() {
                                @Override
                                public void onSuccess(String token) {
                                    VoipManager.getInstance().getVoipMeetingController().startCallByJoinKey(voipPostMessage.mMeetingId, token);

                                }

                                @Override
                                public void networkFail(int errorCode, String errorMsg) {
                                    ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
                                }
                            });

                        }
                    }

                } else {

                    LogUtil.e("join_bug", "join bug else wrong");
                }


                //非全时的 sdk 需要用 im 辅助来维护会议成员的状态
                if (VoipSdkType.QSY != AtworkConfig.VOIP_SDK_TYPE) {

                    if(VoipMeetingController.getInstance().isExist(voipPostMessage.mOperator.getUid())) {
                        VoipManager.getInstance().getVoipMeetingController().setParticipantStatusAndRefreshUI(voipPostMessage.mOperator, UserStatus.UserStatus_Joined);

                    } else {
                        ArrayList<VoipMeetingMember> singleList = new ArrayList<>();
                        singleList.add(voipPostMessage.mOperator);
                        VoipManager.getInstance().getVoipMeetingController().addParticipants(singleList);

                    }

                }

            }
        }

    }

    private boolean isNotTryingCalling() {
        return CallState.CallState_StartCall != VoipManager.getInstance().getCallState()
                && CallState.CallState_Calling != VoipManager.getInstance().getCallState();
    }


    public void handleRejectedEvent(Context context, VoipPostMessage voipPostMessage) {
        synchronized (sHandleLock) {
            //单人的语音会议里, 收到对方的拒绝, 需要退掉会议
            if (!User.isYou(context, voipPostMessage.mOperator.mUserId)) {
                if (VoipEventServiceHelper.isSdkBasedCurrentMeetingHandling(voipPostMessage)) {
                    if (!VoipManager.getInstance().getVoipMeetingController().isGroupChat()) {

                        toastPeerRejectedTip(context, voipPostMessage);

                        stopCall();

                    } else {
                        VoipManager.getInstance().getVoipMeetingController().setParticipantStatusAndRefreshUI(voipPostMessage.mOperator, UserStatus.UserStatus_Rejected);

                        VoipManager.getInstance().getTimeController().cancel(voipPostMessage.mOperator.mUserId);
                    }
                }


            }
        }

    }


    public void toastPeerRejectedTip(Context context, VoipPostMessage voipPostMessage) {
        if(User.isYou(context, voipPostMessage.mCreator.mUserId)) {
            VoipManager.getInstance().getVoipMeetingController().tipToast(context.getString(R.string.voip_meeting_has_rejected_peer));

        } else {
            VoipManager.getInstance().getVoipMeetingController().tipToast(context.getString(R.string.voip_meeting_has_canceled_peer));

        }
    }

    private void stopCall() {
        VoipManager.getInstance().getVoipMeetingController().stopCall();
    }

    public void handleLeavedEvent(Context context, VoipPostMessage voipPostMessage) {
        synchronized (sHandleLock) {
            if (User.isYou(context, voipPostMessage.mOperator.mUserId)) {
                //自己 leave 标记着自己方面的会议结束, 以此更新数据库状态, 通话时长等
                LogUtil.e("voip", "update meeting db in leave event");

                long callDuration = voipPostMessage.mOperator.mLeaveTime - voipPostMessage.mOperator.mJoinTime;

                boolean success = VoipMeetingRecordRepository.getInstance().updateMeetingInfo(voipPostMessage.mMeetingId, MeetingStatus.SUCCESS, callDuration);
                if(success) {
                    VoipHistoryFragment.refresh();
                }

            } else {

                if (VoipManager.getInstance().getVoipMeetingController().isCurrentVoipMeetingValid()) {
                    //单对单, 对方离开, 你也需要离开会议
                    if (!VoipManager.getInstance().getVoipMeetingController().isGroupChat()) {

                        VoipManager.getInstance().getVoipMeetingController().tipToast(context.getString(R.string.voip_meeting_end_tip_peer));

                        if (VoipSdkType.QSY != AtworkConfig.VOIP_SDK_TYPE) {
                            stopCall();
                        }

                        leaveMeeting(context, voipPostMessage);

                    } else {
                        //非全时的 sdk 需要用 im 辅助来维护会议成员的状态
                        if (VoipSdkType.QSY != AtworkConfig.VOIP_SDK_TYPE) {

                            VoipManager.getInstance().getVoipMeetingController().setParticipantStatusAndRefreshUI(voipPostMessage.mOperator, UserStatus.UserStatus_Left);

                        }

                    }


                } else {
                    //若当前会议已经被清空, 检查"离开会议"队列是否处理
                    if(mMeetingNeedLeaveList.contains(voipPostMessage.mMeetingId)) {
                        leaveMeeting(context, voipPostMessage);
                        mMeetingNeedLeaveList.remove(voipPostMessage.mMeetingId);
                    }
                }

            }
        }
    }

    public void leaveMeeting(Context context, VoipPostMessage voipPostMessage) {
        UserHandleInfo loginUser = AtworkApplicationLike.getLoginUserHandleInfo(context);
        //离开会议
        if (null != loginUser) {
            VoipMeetingSyncService.leaveMeeting(context, voipPostMessage.mMeetingId, null, loginUser, null);
        }
    }

    public void handleEndedEvent(Context context, VoipPostMessage voipPostMessage) {

        synchronized (sHandleLock) {
            if (User.isYou(context, voipPostMessage.mOperator.mUserId)) {
                LogUtil.e("voip", "update meeting db in leave event");

                long callDuration = voipPostMessage.mOperator.mLeaveTime - voipPostMessage.mOperator.mJoinTime;
                boolean success = VoipMeetingRecordRepository.getInstance().updateMeetingInfo(voipPostMessage.mMeetingId, MeetingStatus.SUCCESS, callDuration);
                if(success) {
                    VoipHistoryFragment.refresh();
                }

            } else {
                //群聊会议, 其他成员收到End, 意味着最后一个在会议的人取消了会议, 此时关闭"呼叫中"的界面, 以及更新数据库
                if (VoipManager.getInstance().getVoipMeetingController().isGroupChat() && VoipEventServiceHelper.isSdkBasedCurrentMeetingHandling(voipPostMessage)) {

                    VoipManager.getInstance().getVoipMeetingController().tipToast(context.getString(R.string.voip_meeting_has_canceled_peer));

                    stopCall();

                    VoipManager.getInstance().getTimeController().cancelAll();
                }

            }
        }


    }

    public void handleBusyEvent(Context context, VoipPostMessage voipPostMessage) {
        synchronized (sHandleLock) {
            if (!User.isYou(context, voipPostMessage.mOperator.mUserId) && VoipEventServiceHelper.isSdkBasedCurrentMeetingHandling(voipPostMessage)) {

                if (VoipManager.getInstance().getVoipMeetingController().isGroupChat()) {
                    if (VoipHelper.isHandlingVoipCallExcludeInit()) {
                        String busyTip = context.getString(R.string.being_busy, voipPostMessage.mOperator.mShowName);
                        VoipManager.getInstance().getVoipMeetingController().tipToast(busyTip);
                    }
                    VoipManager.getInstance().getVoipMeetingController().removeParticipantAndRefreshUI(voipPostMessage.mOperator.mUserId);

                    VoipManager.getInstance().getTimeController().cancel(voipPostMessage.mOperator.mUserId);
                } else {

                    if (VoipHelper.isHandlingVoipCall()) {

                        VoipManager.getInstance().getVoipMeetingController().tipToast(context.getString(R.string.peer_is_busy));

                        stopCall();

                    }
                }


            }
        }
    }


    public void handleCreatedChatMessage(Context context, VoipPostMessage voipPostMessage, boolean isCameOnline) {
        //没有会议的状态才发送通知
        if (!User.isYou(context, voipPostMessage.mOperator.mUserId)) {

            if (!VoipHelper.isHandlingCall()) {
                sendVoipCreateMessageWhenDiscussionType(context, voipPostMessage, isCameOnline);

            }
        } else {
            sendVoipCreateMessageWhenDiscussionType(context, voipPostMessage, isCameOnline);

        }

    }

    public void handleInvitedChatMessage(Context context, VoipPostMessage voipPostMessage, boolean isCameOnline) {
        if (voipPostMessage.membersContainsMe(context)) {

            if (!VoipHelper.isHandlingCall()) {
                sendVoipCreateMessageWhenDiscussionType(context, voipPostMessage, isCameOnline);

            }
        }

    }


    public void handleRejectedChatMessage(Context context, VoipPostMessage voipPostMessage, boolean isCameOnline) {
        if (!User.isYou(context, voipPostMessage.mOperator.mUserId)) {
            if (VoipEventServiceHelper.isVoipUserPeersLegal(context, voipPostMessage)) {

                String content;
                if (User.isYou(context, voipPostMessage.mCreator.mUserId)) {
                    content = context.getString(R.string.tip_voip_reject_peer);

                } else {
                    content = context.getString(R.string.tip_voip_cancel_peer);

                }

                newSendVoipChatMessage(context, content, MeetingStatus.FAILED, voipPostMessage, true, isCameOnline);

            }


        } else {
            if (VoipEventServiceHelper.isVoipUserPeersHavingMe(context, voipPostMessage)) {
                String content;
                if (User.isYou(context, voipPostMessage.mCreator.mUserId)) {
                    content = context.getString(R.string.tip_voip_cancel_self);

                } else {
                    content = context.getString(R.string.tip_voip_reject_self);

                }

                newSendVoipChatMessage(context, content, MeetingStatus.FAILED, voipPostMessage, false, true);
            } else if (MeetingInfo.Type.DISCUSSION.equals(voipPostMessage.mMeetingInfo.mType)) {
                sendVoipEndDiscussionMessage(context, voipPostMessage, isCameOnline, true);
            }

        }
    }

    public void handleRejectedNotification(Context context, VoipPostMessage voipPostMessage, boolean isCameOnline) {
        if (!User.isYou(context, voipPostMessage.mOperator.mUserId) && User.isYou(context, voipPostMessage.mCreator.mUserId)) {

            if(VoipEventServiceHelper.isVoipUserPeersLegal(context, voipPostMessage)) {
                Session session = ChatSessionDataWrap.getInstance().getSession(voipPostMessage.mOperator.mUserId, null);
                if(null == session || !session.visible) {
                    VoipNoticeManager.getInstance().rejectNotificationShow(context, voipPostMessage.mOperator.getUid());
                }

            } else {
                if(!CallActivity.sIsVisible) {
                    VoipNoticeManager.getInstance().rejectNotificationShow(context, voipPostMessage.mOperator.getUid());

                }
            }



        }
    }


    /**
     * 处理 leave voip 的界面消息处理
     */
    public void handleLeaveChatMessage(Context context, VoipPostMessage voipPostMessage, boolean isCameOnline) {
        if (User.isYou(context, voipPostMessage.mOperator.mUserId)) {
            long callDuration = voipPostMessage.mOperator.mLeaveTime - voipPostMessage.mOperator.mJoinTime;

            if (VoipEventServiceHelper.isVoipUserPeersHavingMe(context, voipPostMessage)) {
                String voipChatContent = context.getString(R.string.call_duration, VoipHelper.toCallDurationShow(callDuration / 1000));
                newSendVoipChatMessage(context, voipChatContent, MeetingStatus.SUCCESS, voipPostMessage, false, isCameOnline);

            } else if (MeetingInfo.Type.DISCUSSION.equals(voipPostMessage.mMeetingInfo.mType)) {
                sendVoipEndDiscussionMessage(context, voipPostMessage, isCameOnline, true);

            }
        }

    }

    /**
     * 处理 ended voip 的界面消息
     */
    public void handleEndedChatMessage(Context context, VoipPostMessage voipPostMessage, boolean isCameOnline) {
        if (User.isYou(context, voipPostMessage.mOperator.mUserId)
                && VoipEventServiceHelper.isVoipUserPeersHavingMe(context, voipPostMessage)) {
            long callDuration = voipPostMessage.mOperator.mLeaveTime - voipPostMessage.mOperator.mJoinTime;

            String voipChatContent = context.getString(R.string.call_duration, VoipHelper.toCallDurationShow(callDuration / 1000));
            newSendVoipChatMessage(context, voipChatContent, MeetingStatus.SUCCESS, voipPostMessage, false, isCameOnline);

        } else if (MeetingInfo.Type.DISCUSSION.equals(voipPostMessage.mMeetingInfo.mType)) {
            sendVoipEndDiscussionMessage(context, voipPostMessage, isCameOnline, User.isYou(context, voipPostMessage.mOperator.mUserId));
        }
    }


    /**
     * 发送群里的 voip 通知
     */
    public void newSendDiscussionSystemMessage(Context context, String content, VoipPostMessage voipPostMessage, boolean isCameFromOnline) {


        SystemChatMessage systemChatMessage = SystemChatMessageHelper.createMessageByVoipNoticeMessage(content, LoginUserInfo.getInstance().getLoginUserId(context), voipPostMessage);
        //偶尔出现 android.database.sqlite.SQLiteDiskIOException, disk I/O error (code 778)的 bug,
        // 可能是 #tableExists方法的 cursor 没关闭导致待观察
        //https://bugly.qq.com/v2/crash/apps/900033769/issues/955?pid=1
        try {
            ChatSessionDataWrap.getInstance().asyncReceiveMessage(systemChatMessage, isCameFromOnline);
            SystemMessageHelper.newSystemMessageNotice(context, systemChatMessage);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("voip", "voip # newSendDiscussionSystemMessage  SQLiteDiskIOException");
        }
    }

    /**
     * 拼装 voip 消息发到单聊界面
     */
    public void newSendVoipChatMessage(Context context, String voipChatContent, MeetingStatus status, VoipPostMessage voipPostMessage, boolean needUnreadNotice, boolean isCameFromOnline) {
        UserHandleInfo toUserHandleInfo = new UserHandleInfo();
        toUserHandleInfo.mUserId = voipPostMessage.mMeetingInfo.mId;
        VoipEventServiceHelper.assembleDomainId(context, voipPostMessage, toUserHandleInfo);

        VoipChatMessage voipChatMessage = VoipChatMessage.newVoipChatMessage(context, voipChatContent, toUserHandleInfo, status, voipPostMessage);

        if (needUnreadNotice) {
            voipChatMessage.read = voipPostMessage.read;
        } else {
            voipChatMessage.read = ReadStatus.AbsolutelyRead;
        }

        //偶尔出现 android.database.sqlite.SQLiteDiskIOException, disk I/O error (code 778)的 bug,
        // 可能是 #tableExists方法的 cursor 没关闭导致待观察
        //https://bugly.qq.com/v2/crash/apps/900033769/issues/955?pid=1
        try {
            ChatSessionDataWrap.getInstance().asyncReceiveMessage(voipChatMessage, isCameFromOnline);

            ChatMessageHelper.notifyMessageReceived(voipChatMessage);
        } catch (Exception e) {
            e.printStackTrace();

            Logger.e("voip", "voip # newSendVoipChatMessage  SQLiteDiskIOException");

        }

        if(isCameFromOnline) {
            //更新激活状态(PC 同时在线, 不用更新自己)
            if (!LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext).equals(voipChatMessage.from)) {
                UserDaoService.getInstance().updateUserActiveStatus(BaseApplicationLike.baseContext, voipChatMessage.from, voipChatMessage.mFromDomain);
            }
        }


        SessionRefreshHelper.notifyRefreshSessionAndCount();
    }



    /**
     * 群组里会议开始的 voip 通知
     */
    public void sendVoipCreateMessageWhenDiscussionType(Context context, VoipPostMessage voipPostMessage, boolean isCameOnline) {

        if(!voipPostMessage.isSdkTypeSupportInstant()) {
            return;
        }

        //群通知
        if (MeetingInfo.Type.DISCUSSION.equals(voipPostMessage.mMeetingInfo.mType)) {

            String systemContent;
            String meetingTypeTip;
            if (VoipType.VIDEO.equals(voipPostMessage.mGateWay.mVoipType)) {
                meetingTypeTip = context.getString(R.string.voip_video_meeting);
            } else {
                meetingTypeTip = context.getString(R.string.voip_voice_meeting);
            }

            if (User.isYou(context, voipPostMessage.mCreator.mUserId)) {
                final String me = context.getString(R.string.item_about_me);
                systemContent = context.getString(R.string.chat_tip_voip_start, me, meetingTypeTip);

            } else {
                String creatorName = getCreatorName(context, voipPostMessage);
                systemContent = context.getString(R.string.chat_tip_voip_start, creatorName, meetingTypeTip);

            }
            newSendDiscussionSystemMessage(context, systemContent, voipPostMessage, isCameOnline);

        }
    }

    private String getCreatorName(Context context, VoipPostMessage voipPostMessage) {
        String creatorName = voipPostMessage.mCreator.mShowName;
        if(StringUtils.isEmpty(creatorName)) {
            if(voipPostMessage.mCreator.mUserId.equals(voipPostMessage.mOperator.mUserId)) {
                creatorName = voipPostMessage.mOperator.mShowName;
            }

            if(StringUtils.isEmpty(creatorName)) {
                creatorName = UserManager.getInstance().getReadableName(context, voipPostMessage.mCreator.mUserId, voipPostMessage.mCreator.mDomainId);

            }
        }
        return creatorName;
    }


    /**
     * 群组里会议结束的 voip 通知
     */
    public void sendVoipEndDiscussionMessage(Context context, VoipPostMessage voipPostMessage, boolean isCameFromOnline, boolean isEndedType) {
        String systemContent;
        String meetingTypeTip;
        if (VoipType.VIDEO.equals(voipPostMessage.mGateWay.mVoipType)) {
            meetingTypeTip = context.getString(R.string.voip_video_meeting);
        } else {
            meetingTypeTip = context.getString(R.string.voip_voice_meeting);
        }

        if (isEndedType) {
            systemContent = context.getString(R.string.chat_tip_voip_end, meetingTypeTip);

        } else {
            systemContent = context.getString(R.string.chat_tip_voip_failed, meetingTypeTip);

        }

        newSendDiscussionSystemMessage(context, systemContent, voipPostMessage, isCameFromOnline);
    }


    public static class OfflineEventController {

        //管理离线 voip 未结束会议的 create 处理, 必要时, 需要弹出接电话的界面
        public LinkedHashMap<String, List<VoipPostMessage>> mOfflineReplayCreateEventPool = new LinkedHashMap<>();

        //管理群会议开启通知的发送, 离线时需要注意繁忙的, 正在会议中的需要过滤
        public HashMap<String, VoipPostMessage> mWaitSendCreatedDiscussionMessageMap = new HashMap<>();

        /**
         * 处理离线 voip 消息
         *
         * @param context
         * @param voipMap 离线拉回来的 voip 消息按照会议 id 分组
         */
        public void batchHandleVoipMessageOffline(Context context, LinkedHashMap<String, List<VoipPostMessage>> voipMap) {
            for (Map.Entry<String, List<VoipPostMessage>> entry : voipMap.entrySet()) {
                String meetingId = entry.getKey();
                List<VoipPostMessage> voipList = entry.getValue();

                MapUtil.update(mOfflineReplayCreateEventPool, meetingId, voipList);

                boolean isVoipMeetingMayOpening = true;
                for (VoipPostMessage voipPostMessage : voipList) {
                    VoipEventServiceHelper.assembleVoipType(voipPostMessage);

                    if(voipPostMessage.isSdkBasedType()) {
                        handleSdkTypeVoipOfflineMessage(context, voipPostMessage);

                    } else if(voipPostMessage.isZoomProduct()) {
                        ZoomVoipEventService.getInstance().getOfflineEventController().handleZoomInstantVoipOfflineMessage(context, voipPostMessage,mWaitSendCreatedDiscussionMessageMap);
                    }

                    isVoipMeetingMayOpening = checkVoipMeetingStatus(context, voipPostMessage);

                }

                //标记着此会议已经结束
                if (!isVoipMeetingMayOpening) {
                    //把开启会议的通知发送出去(繁忙以及仍在呼叫,  正在会议中的不发送)
                    VoipPostMessage createdPostMessageWaitSended = mWaitSendCreatedDiscussionMessageMap.get(meetingId);
                    if (null != createdPostMessageWaitSended && createdPostMessageWaitSended.isSdkTypeSupportInstant()) {

                        if(createdPostMessageWaitSended.isSdkBasedType()) {
                            VoipEventService.getInstance().sendVoipCreateMessageWhenDiscussionType(context, createdPostMessageWaitSended, false);

                        } else {
                            ZoomVoipEventService.getInstance().sendVoipCreateMessageWhenDiscussionType(context, createdPostMessageWaitSended, false);

                        }
                        mWaitSendCreatedDiscussionMessageMap.remove(meetingId);
                    }

                    mOfflineReplayCreateEventPool.remove(meetingId);
                }
            }
        }


        /**
         * 检查 voip 会议是开启着还是已经结束
         */
        public boolean checkVoipMeetingStatus(Context context, VoipPostMessage voipPostMessage) {
            boolean isVoipMeetingMayOpening = true;

            if (VoipPostMessage.Operation.MEMBER_BUSY.equals(voipPostMessage.mOperation)) {
                if (User.isYou(context, voipPostMessage.mOperator.mUserId)) {
                    isVoipMeetingMayOpening = false;

                } else {
                    if (VoipEventServiceHelper.isVoipUserPeersLegal(context, voipPostMessage)) {
                        isVoipMeetingMayOpening = false;
                    }
                }

            } else if (VoipPostMessage.Operation.ENDED.equals(voipPostMessage.mOperation)) {
                isVoipMeetingMayOpening = false;

            } else if (VoipPostMessage.Operation.MEMBER_REJECTED.equals(voipPostMessage.mOperation)) {

                if (User.isYou(context, voipPostMessage.mOperator.mUserId)) {
                    isVoipMeetingMayOpening = false;
                } else {
                    if (VoipEventServiceHelper.isVoipUserPeersLegal(context, voipPostMessage)) {
                        isVoipMeetingMayOpening = false;

                    }
                }
            } else if (VoipPostMessage.Operation.MEMBER_LEAVED.equals(voipPostMessage.mOperation)) {

                if (User.isYou(context, voipPostMessage.mOperator.mUserId)) {
                    isVoipMeetingMayOpening = false;

                } else {
                    if (VoipEventServiceHelper.isVoipUserPeersLegal(context, voipPostMessage)) {
                        isVoipMeetingMayOpening = false;

                    }
                }
            }

            return isVoipMeetingMayOpening;
        }

        /**
         * 回放离线的 voip 消息
         */
        public void replayOfflineEvent(Context context) {
            List<List<VoipPostMessage>> offlineEventList = new ArrayList<>(mOfflineReplayCreateEventPool.values());
            int size = offlineEventList.size();
            for (int i = size - 1; i >= 0; i--) {
                List<VoipPostMessage> batchVoipEventList = offlineEventList.get(i);

                boolean hadHandled = false;
                for (VoipPostMessage voipPostMessage : batchVoipEventList) {
                    if (VoipPostMessage.Operation.CREATED.equals(voipPostMessage.mOperation)) {
                        //非自己则吊起电话
                        if (!User.isYou(context, voipPostMessage.mOperator.mUserId)) {

                            if ((MeetingInfo.Type.USER.equals(voipPostMessage.mMeetingInfo.mType))) {
                                if (TimeUtil.getCurrentTimeInMillis() - voipPostMessage.deliveryTime < 60 * 1000) {
                                    handleCreatedEventOffline(context, voipPostMessage);
                                }

                            } else {// 多人会议类型
                                if (!VoipHelper.isHandlingCall()) {
                                    VoipEventService.getInstance().sendVoipCreateMessageWhenDiscussionType(context, voipPostMessage, false);

                                    if (TimeUtil.getCurrentTimeInMillis() - voipPostMessage.deliveryTime < 30 * 60 * 1000) {
                                        handleCreatedEventOffline(context, voipPostMessage);

                                    } else {
                                        VoipEventService.getInstance().doReject(context, voipPostMessage);//是否不发送拒绝请求比较好?

                                    }
                                }


                            }

                            hadHandled = true;
                            break;

                        }

                    } else if (VoipPostMessage.Operation.MEMBER_INVITED.equals(voipPostMessage.mOperation)) {
                        if (voipPostMessage.membersContainsMe(context)) {
                            if (!VoipHelper.isHandlingCall()) {
                                VoipEventService.getInstance().sendVoipCreateMessageWhenDiscussionType(context, voipPostMessage, false);

                                //30min 以内的才接听, 否则发送拒绝通知
                                if (TimeUtil.getCurrentTimeInMillis() - voipPostMessage.deliveryTime < 30 * 60 * 1000) {

                                    handleInvitedEventOffline(context, voipPostMessage);

                                } else {
                                    VoipEventService.getInstance().doReject(context, voipPostMessage); //是否不发送拒绝请求比较好?
                                }
                            }

                            hadHandled = true;
                            break;
                        }

                    }
                }

                if (hadHandled) {
                    break;
                }
            }

            mOfflineReplayCreateEventPool.clear();
            mWaitSendCreatedDiscussionMessageMap.clear();
        }

        private void handleCreatedEventOffline(Context context, VoipPostMessage voipPostMessage) {
            if (voipPostMessage.isSdkBasedType()) {
                sdkBasedHandleCreatedEventOffline(context, voipPostMessage);

            } else {
                ZoomVoipEventService.getInstance().getOfflineEventController().handleZoomCreatedEventOffline(context, voipPostMessage);
            }
        }

        private void handleInvitedEventOffline(Context context, VoipPostMessage voipPostMessage) {
            if(voipPostMessage.isSdkBasedType()) {
                sdkBasedHandleInvitedEventOffline(context, voipPostMessage);

            } else {
                ZoomVoipEventService.getInstance().getOfflineEventController().handleZoomInvitedEventOffline(context, voipPostMessage);

            }
        }

        public void handleSdkTypeVoipOfflineMessage(Context context, VoipPostMessage voipPostMessage) {


            VoipEventService voipEventService = VoipEventService.getInstance();
            if (VoipPostMessage.Operation.MEMBER_LEAVED.equals(voipPostMessage.mOperation)) {
                voipEventService.handleLeaveChatMessage(context, voipPostMessage, false);

            } else if (VoipPostMessage.Operation.ENDED.equals(voipPostMessage.mOperation)) {
                voipEventService.handleEndedChatMessage(context, voipPostMessage, false);

            } else if (VoipPostMessage.Operation.CREATED.equals(voipPostMessage.mOperation)) {

                if (MeetingInfo.Type.DISCUSSION.equals(voipPostMessage.mMeetingInfo.mType)) {
                    if (User.isYou(context, voipPostMessage.mOperator.mUserId)) {
                        VoipEventService.getInstance().sendVoipCreateMessageWhenDiscussionType(context, voipPostMessage, false);
                    } else {
                        mWaitSendCreatedDiscussionMessageMap.put(voipPostMessage.mMeetingId, voipPostMessage);
                    }
                }


                VoipEventServiceHelper.insertVoipMeeting(voipPostMessage, false);

            } else if (VoipPostMessage.Operation.MEMBER_REJECTED.equals(voipPostMessage.mOperation)) {
                voipEventService.handleRejectedChatMessage(context, voipPostMessage, false);

            } else if (VoipPostMessage.Operation.MEMBER_INVITED.equals(voipPostMessage.mOperation)) {
                if (MeetingInfo.Type.DISCUSSION.equals(voipPostMessage.mMeetingInfo.mType)) {
                    if (voipPostMessage.membersContainsMe(context)) {
                        mWaitSendCreatedDiscussionMessageMap.put(voipPostMessage.mMeetingId, voipPostMessage);
                    }
                }

                VoipEventServiceHelper.insertVoipMeeting(voipPostMessage, false);

            } else if (VoipPostMessage.Operation.MEMBER_BUSY.equals(voipPostMessage.mOperation)) {
                if (MeetingInfo.Type.DISCUSSION.equals(voipPostMessage.mMeetingInfo.mType)) {
                    if (User.isYou(context, voipPostMessage.mOperator.mUserId)) {
                        mWaitSendCreatedDiscussionMessageMap.remove(voipPostMessage.mMeetingId);
                    }
                }

            }
        }

        public void sdkBasedHandleCreatedEventOffline(Context context, VoipPostMessage voipPostMessage) {

            if (!VoipHelper.isHandlingCall()) {

                VoipManager.queryMeetingRemote(context, voipPostMessage.mMeetingId, new VoipManager.OnCreateAndQueryVoipMeetingListener() {
                    @Override
                    public void onSuccess(CreateOrQueryMeetingResponseJson responseJson) {
                        VoipMeetingGroup voipMeetingGroup = responseJson.toVoipMeetingGroup(voipPostMessage.mGateWay.mVoipType);

                        if (null != voipPostMessage.mMeetingInfo) {
                            voipMeetingGroup.mMeetingInfo.mType = voipPostMessage.mMeetingInfo.mType;

                            voipMeetingGroup.mAvatar = voipPostMessage.mMeetingInfo.mAvatar;
                        }

                        boolean isGroupType = VoipHelper.isGroupType(voipPostMessage.mMeetingInfo);

                        //群组时, 维护定时器. 用于管理群聊成员的状态
                        if (isGroupType) {

                            VoipManager.getInstance().getOfflineController().checkOfflineStatus(context, voipMeetingGroup.mParticipantList);

                            VoipManager.getInstance().getTimeController().monitorVoipMembers(context, voipMeetingGroup.mParticipantList);
                        }


                        VoipHelper.goToCallActivity(context, voipMeetingGroup.mMeetingInfo, voipMeetingGroup.mVoipType, isGroupType, voipMeetingGroup.mParticipantList, false, voipPostMessage.mGateWay.mId, voipPostMessage.mMeetingId, null, voipPostMessage.mOperator);


                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);
                    }
                });

            }

        }


        public void sdkBasedHandleInvitedEventOffline(Context context, VoipPostMessage voipPostMessage) {

            if (!VoipHelper.isHandlingCall()) {

                VoipManager.queryMeetingRemote(context, voipPostMessage.mMeetingId, new VoipManager.OnCreateAndQueryVoipMeetingListener() {
                    @Override
                    public void onSuccess(CreateOrQueryMeetingResponseJson responseJson) {
                        VoipMeetingGroup voipMeetingGroup = responseJson.toVoipMeetingGroup(voipPostMessage.mGateWay.mVoipType);

                        if (null != voipPostMessage.mMeetingInfo) {
                            voipMeetingGroup.mMeetingInfo.mType = voipPostMessage.mMeetingInfo.mType;

                            voipMeetingGroup.mAvatar = voipPostMessage.mMeetingInfo.mAvatar;
                        }

                        VoipManager.getInstance().getOfflineController().checkOfflineStatus(context, voipMeetingGroup.mParticipantList);

                        VoipManager.getInstance().getTimeController().monitorVoipMembers(context, voipMeetingGroup.mParticipantList);

                        VoipHelper.goToCallActivity(context, voipMeetingGroup.mMeetingInfo, voipMeetingGroup.mVoipType, true, voipMeetingGroup.mParticipantList, false, voipPostMessage.mGateWay.mId, voipPostMessage.mMeetingId, null, voipPostMessage.mOperator);


                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);
                    }
                });

            }

        }
    }

}
