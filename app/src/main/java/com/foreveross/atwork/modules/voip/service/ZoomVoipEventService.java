package com.foreveross.atwork.modules.voip.service;

import android.content.Context;

import com.foreverht.db.service.repository.VoipMeetingRecordRepository;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.voip.VoipMeetingSyncService;
import com.foreveross.atwork.api.sdk.voip.requestJson.ZoomHandleInfo;
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
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.VoipPostMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.VoipSdkType;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.manager.VoipNoticeManager;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.modules.chat.util.SystemChatMessageHelper;
import com.foreveross.atwork.modules.voip.activity.CallActivity;
import com.foreveross.atwork.modules.voip.fragment.VoipHistoryFragment;
import com.foreveross.atwork.modules.voip.manager.ZoomVoipManager;
import com.foreveross.atwork.modules.voip.utils.VoipEventServiceHelper;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.SystemMessageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * voip 事件消息处理 service
 * <p>
 * Created by dasunsy on 16/7/15.
 */
public class ZoomVoipEventService {

    public static final String TAG = "VOIP";

    public static final Object sLock = new Object();

    public static final Object sHandleLock = new Object();

    public static ZoomVoipEventService sInstance = null;

    public OfflineEventController mOfflineEventController = new OfflineEventController();

    public List<String> mMeetingNeedLeaveList = new ArrayList<>();

    private ZoomVoipEventService() {

    }

    public static ZoomVoipEventService getInstance() {
        /**
         * double check
         * */

        if (null == sInstance) {
            synchronized (sLock) {
                if (null == sInstance) {
                    sInstance = new ZoomVoipEventService();
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

            handleZoomCreatedEvent(baseContext, voipPostMessage);
            handleZoomCreatedChatMessage(baseContext, voipPostMessage, true);

        } else if (VoipPostMessage.Operation.MEMBER_INVITED.equals(voipPostMessage.mOperation)) {

            handleZoomInvitedEvent(baseContext, voipPostMessage);
            handleZoomInvitedChatMessage(baseContext, voipPostMessage, true);


        } else if (VoipPostMessage.Operation.MEMBER_LEAVED.equals(voipPostMessage.mOperation)) {

//            handleLeavedEvent(baseContext, voipPostMessage);
//
            handleZoomLeaveChatMessage(baseContext, voipPostMessage, true);

        } else if (VoipPostMessage.Operation.MEMBER_REJECTED.equals(voipPostMessage.mOperation)) {

            handleZoomRejectedEvent(baseContext, voipPostMessage);
            handleZoomRejectedChatMessage(baseContext, voipPostMessage, true);


        } else if (VoipPostMessage.Operation.ENDED.equals(voipPostMessage.mOperation)) {
            handleZoomEndedEvent(baseContext, voipPostMessage);
//
            handleZoomEndedChatMessage(baseContext, voipPostMessage, true);

        } else if (VoipPostMessage.Operation.MEMBER_BUSY.equals(voipPostMessage.mOperation)) {
            handleZoomBusyEvent(baseContext, voipPostMessage);

        } else if (VoipPostMessage.Operation.MEMBER_JOINED.equals(voipPostMessage.mOperation)) {
            handleZoomJoinedEvent(baseContext, voipPostMessage);
        }
    }

    public void doReject(final Context context, VoipPostMessage voipPostMessage) {
        VoipManager.getInstance().rejectMeeting(context, null, null, voipPostMessage.mMeetingId, new ZoomHandleInfo(), new VoipManager.OnHandleVoipMeetingListener() {
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

    public void handleZoomCreatedEvent(Context context, VoipPostMessage voipPostMessage) {
        synchronized (sHandleLock) {
            //非自己则吊起电话
            if (!User.isYou(context, voipPostMessage.mOperator.mUserId)) {

                if (!VoipHelper.isHandlingCall()) {

                    boolean isGroupType = VoipHelper.isGroupType(voipPostMessage.mMeetingInfo);

                    ZoomVoipManager.INSTANCE.goToCallActivity(context, voipPostMessage.mMeetingId, voipPostMessage.mMeetingInfo, voipPostMessage.mGateWay.mVoipType, isGroupType, false, voipPostMessage.mMemberList, null, voipPostMessage.mOperator);

                } else {
                    doBusy(context, voipPostMessage);
                }

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


    public void handleZoomInvitedEvent(final Context context, final VoipPostMessage voipPostMessage) {
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

                            ZoomVoipManager.INSTANCE.goToCallActivity(context, voipMeetingGroup.mMeetingId, voipMeetingGroup.mMeetingInfo, voipMeetingGroup.mVoipType, true, false, voipMeetingGroup.mParticipantList, null, voipPostMessage.mOperator);

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



    public void handleZoomJoinedEvent(Context context, VoipPostMessage voipPostMessage) {
        synchronized (sHandleLock) {
            if (ZoomVoipManager.INSTANCE.isCurrentMeetingHandling(voipPostMessage)) {

                if (ZoomVoipManager.INSTANCE.isVoipCalling()) {

                    LogUtil.e("bizconf", "join bug in");


                    if(!ZoomVoipManager.INSTANCE.isGroupChat()) {
                        if (MeetingInfo.Type.USER.equals(voipPostMessage.mMeetingInfo.mType)) {


                            if(User.isYou(context, voipPostMessage.mCreator.mUserId)
                                    && voipPostMessage.mOperator.mUserId.equals(voipPostMessage.mMeetingInfo.mId)) {
                                VoipManager.joinMeeting(context, voipPostMessage.mMeetingInfo, voipPostMessage.mMeetingId, voipPostMessage.mGateWay.mVoipType, new ZoomHandleInfo(), new VoipManager.OnGetJoinTokenListener() {
                                    @Override
                                    public void onSuccess(String token) {

                                        if (null != ZoomVoipManager.INSTANCE.getOnZoomVoipHandleListener()) {
                                            ZoomVoipManager.INSTANCE.getOnZoomVoipHandleListener().goMeeting(token);
                                        }
                                    }

                                    @Override
                                    public void networkFail(int errorCode, String errorMsg) {
                                        ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
                                    }
                                });

                                //todo 有个设备判断更好
                            } else if(!User.isYou(context, voipPostMessage.mCreator.mUserId)
                                        && User.isYou(context, voipPostMessage.mOperator.mUserId)) {


                                if(CallState.CallState_Init == ZoomVoipManager.INSTANCE.getCallState()) {
                                    ZoomVoipManager.INSTANCE.stopCall();
                                }
                            }


                        }

                    } else {
                        //todo 有个设备判断更好
                        if(!User.isYou(context, voipPostMessage.mCreator.mUserId)
                                && User.isYou(context, voipPostMessage.mOperator.mUserId)) {


                            if(CallState.CallState_Init == ZoomVoipManager.INSTANCE.getCallState()) {
                                ZoomVoipManager.INSTANCE.stopCall();
                            }
                        }
                    }


                } else {

                    LogUtil.e("join_bug", "join bug else wrong");
                }

            }
        }

    }


    public void handleZoomRejectedEvent(Context context, VoipPostMessage voipPostMessage) {
        synchronized (sHandleLock) {
            //单人的语音会议里, 收到对方的拒绝, 需要退掉会议


            if (ZoomVoipManager.INSTANCE.isCurrentMeetingHandling(voipPostMessage)) {
                if (!ZoomVoipManager.INSTANCE.isGroupChat()) {
                    if (!User.isYou(context, voipPostMessage.mOperator.mUserId)) {
                        toastZoomPeerRejectedTip(context, voipPostMessage);
                    }

                    ZoomVoipManager.INSTANCE.stopCall();

                } else {

                    //todo 有个设备判断更好
                    if(User.isYou(context, voipPostMessage.mOperator.mUserId)) {

                        if(CallState.CallState_Init == ZoomVoipManager.INSTANCE.getCallState()) {
                            ZoomVoipManager.INSTANCE.stopCall();
                        }
                    }

                }
            }


        }

    }




    public void toastZoomPeerRejectedTip(Context context, VoipPostMessage voipPostMessage) {
        if(User.isYou(context, voipPostMessage.mCreator.mUserId)) {
            BaseApplicationLike.runOnMainThread(() -> AtworkToast.showResToast(R.string.voip_meeting_has_rejected_peer));

        } else {


            BaseApplicationLike.runOnMainThread(() -> AtworkToast.showResToast(R.string.voip_meeting_has_canceled_peer));


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

    public void handleZoomEndedEvent(Context context, VoipPostMessage voipPostMessage) {

        synchronized (sHandleLock) {
            if (User.isYou(context, voipPostMessage.mOperator.mUserId)) {


            } else {

                //其他成员收到End, 意味着最后一个在会议的人取消了会议, 此时关闭"呼叫中"的界面, 以及更新数据库

                if(ZoomVoipManager.INSTANCE.isCurrentMeetingHandling(voipPostMessage)) {
                    AtworkApplicationLike.runOnMainThread(()-> AtworkToast.showResToast(R.string.voip_meeting_has_canceled_peer));
                    ZoomVoipManager.INSTANCE.stopCall();

                }


            }
        }


    }

    public void handleZoomBusyEvent(Context context, VoipPostMessage voipPostMessage) {
        synchronized (sHandleLock) {
            if (!User.isYou(context, voipPostMessage.mOperator.mUserId) && ZoomVoipManager.INSTANCE.isCurrentMeetingHandling(voipPostMessage)) {

                if (ZoomVoipManager.INSTANCE.isGroupChat()) {
                    if (ZoomVoipManager.INSTANCE.isHandlingVoipCallExcludeInit()) {
                        String busyTip = context.getString(R.string.being_busy, voipPostMessage.mOperator.mShowName);
                        AtworkApplicationLike.runOnMainThread(()-> AtworkToast.showToast(busyTip));
                    }

                } else {


                    if(ZoomVoipManager.INSTANCE.isVoipCalling()) {
                        AtworkApplicationLike.runOnMainThread(()-> AtworkToast.showResToast(R.string.peer_is_busy));
                        ZoomVoipManager.INSTANCE.stopCall();
                        doReject(context, voipPostMessage);
                    }


                }


            }
        }
    }



    public void handleZoomCreatedChatMessage(Context context, VoipPostMessage voipPostMessage, boolean isCameOnline) {
        //没有会议的状态才发送通知
        if (!User.isYou(context, voipPostMessage.mOperator.mUserId)) {

            if (!VoipHelper.isHandlingCall()) {
                sendVoipCreateMessageWhenDiscussionType(context, voipPostMessage, isCameOnline);

            }
        } else {
            sendVoipCreateMessageWhenDiscussionType(context, voipPostMessage, isCameOnline);

        }

    }

    public void handleZoomInvitedChatMessage(Context context, VoipPostMessage voipPostMessage, boolean isCameOnline) {
        if (voipPostMessage.membersContainsMe(context)) {

            if (!VoipHelper.isHandlingCall()) {
                sendVoipCreateMessageWhenDiscussionType(context, voipPostMessage, isCameOnline);

            }
        }

    }


    public void handleZoomRejectedChatMessage(Context context, VoipPostMessage voipPostMessage, boolean isCameOnline) {
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
    public void handleZoomLeaveChatMessage(Context context, VoipPostMessage voipPostMessage, boolean isCameOnline) {
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
    public void handleZoomEndedChatMessage(Context context, VoipPostMessage voipPostMessage, boolean isCameOnline) {
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

        voipChatMessage.mVoipSdkType  = voipPostMessage.mGateWay.mType;

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



        public void handleZoomInstantVoipOfflineMessage(Context context, VoipPostMessage voipPostMessage, HashMap<String, VoipPostMessage> mWaitSendCreatedDiscussionMessageMap) {


            ZoomVoipEventService voipEventService = ZoomVoipEventService.getInstance();
            if (VoipPostMessage.Operation.MEMBER_LEAVED.equals(voipPostMessage.mOperation)) {
                voipEventService.handleZoomLeaveChatMessage(context, voipPostMessage, false);

            } else if (VoipPostMessage.Operation.ENDED.equals(voipPostMessage.mOperation)) {
                voipEventService.handleZoomEndedChatMessage(context, voipPostMessage, false);

            } else if (VoipPostMessage.Operation.CREATED.equals(voipPostMessage.mOperation)) {

                if (MeetingInfo.Type.DISCUSSION.equals(voipPostMessage.mMeetingInfo.mType)) {
                    if (User.isYou(context, voipPostMessage.mOperator.mUserId)) {
                        VoipEventService.getInstance().sendVoipCreateMessageWhenDiscussionType(context, voipPostMessage, false);
                    } else {
                        mWaitSendCreatedDiscussionMessageMap.put(voipPostMessage.mMeetingId, voipPostMessage);
                    }
                }


//                VoipEventServiceHelper.insertVoipMeeting(voipPostMessage, false);

            } else if (VoipPostMessage.Operation.MEMBER_REJECTED.equals(voipPostMessage.mOperation)) {
                voipEventService.handleZoomRejectedChatMessage(context, voipPostMessage, false);

            } else if (VoipPostMessage.Operation.MEMBER_INVITED.equals(voipPostMessage.mOperation)) {
                if (MeetingInfo.Type.DISCUSSION.equals(voipPostMessage.mMeetingInfo.mType)) {
                    if (voipPostMessage.membersContainsMe(context)) {
                        mWaitSendCreatedDiscussionMessageMap.put(voipPostMessage.mMeetingId, voipPostMessage);
                    }
                }

//                VoipEventServiceHelper.insertVoipMeeting(voipPostMessage, false);

            } else if (VoipPostMessage.Operation.MEMBER_BUSY.equals(voipPostMessage.mOperation)) {
                if (MeetingInfo.Type.DISCUSSION.equals(voipPostMessage.mMeetingInfo.mType)) {
                    if (User.isYou(context, voipPostMessage.mOperator.mUserId)) {
                        mWaitSendCreatedDiscussionMessageMap.remove(voipPostMessage.mMeetingId);
                    }
                }

            }
        }


        public void handleZoomCreatedEventOffline(Context context, VoipPostMessage voipPostMessage) {

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

                        ZoomVoipManager.INSTANCE.goToCallActivity(context, voipMeetingGroup.mMeetingId, voipMeetingGroup.mMeetingInfo, voipMeetingGroup.mVoipType, isGroupType, false, voipMeetingGroup.mParticipantList, null, voipPostMessage.mOperator);



                    }

                    @Override
                    public void networkFail(int errorCode, String errorMsg) {
                        ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Voip, errorCode, errorMsg);
                    }
                });

            }

        }


        public void handleZoomInvitedEventOffline(Context context, VoipPostMessage voipPostMessage) {

            if (!VoipHelper.isHandlingCall()) {

                VoipManager.queryMeetingRemote(context, voipPostMessage.mMeetingId, new VoipManager.OnCreateAndQueryVoipMeetingListener() {
                    @Override
                    public void onSuccess(CreateOrQueryMeetingResponseJson responseJson) {
                        VoipMeetingGroup voipMeetingGroup = responseJson.toVoipMeetingGroup(voipPostMessage.mGateWay.mVoipType);

                        if (null != voipPostMessage.mMeetingInfo) {
                            voipMeetingGroup.mMeetingInfo.mType = voipPostMessage.mMeetingInfo.mType;

                            voipMeetingGroup.mAvatar = voipPostMessage.mMeetingInfo.mAvatar;
                        }

                        ZoomVoipManager.INSTANCE.goToCallActivity(context, voipMeetingGroup.mMeetingId, voipMeetingGroup.mMeetingInfo, voipMeetingGroup.mVoipType, true, false, voipMeetingGroup.mParticipantList, null, voipPostMessage.mOperator);



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
