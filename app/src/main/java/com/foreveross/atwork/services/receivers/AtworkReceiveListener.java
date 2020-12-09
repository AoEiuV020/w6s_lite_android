package com.foreveross.atwork.services.receivers;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.cache.AppCache;
import com.foreverht.cache.UserCache;
import com.foreverht.db.service.repository.ReceiptRepository;
import com.foreverht.db.service.repository.UserRepository;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.db.daoService.EmployeeDaoService;
import com.foreveross.atwork.db.daoService.UserDaoService;
import com.foreveross.atwork.im.sdk.ReceiveListener;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleBasic;
import com.foreveross.atwork.infrastructure.newmessage.ConnectAckTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.HasTimestampResponse;
import com.foreveross.atwork.infrastructure.newmessage.PongMessage;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.ReceiptMessage;
import com.foreveross.atwork.infrastructure.newmessage.UserTypingMessage;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.Operation;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.CmdPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.DeviceInfoMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.DeviceOnlineMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.EventPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.SystemPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.VoipPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ack.AckPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.HideEventMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.gather.CmdGatherMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.ContactNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.DiscussionMeetingNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.DiscussionNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.EmergencyNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.FriendNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.MeetingNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.OrgNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.P2PNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.UserFileDownloadNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.conversation.ConversationNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.user.UserNotifyMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.shared.dev.DevCommonShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.manager.ContactManager;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.modules.discussion.manager.DiscussionNotifyManger;
import com.foreveross.atwork.manager.FriendManager;
import com.foreveross.atwork.manager.MessageNoticeManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.im.OfflineMessageReplayStrategyManager;
import com.foreveross.atwork.manager.im.OfflineMessageSessionStrategyManager;
import com.foreveross.atwork.manager.im.OfflineMessagesReplayStrategyTimeWatcher;
import com.foreveross.atwork.modules.app.dao.AppDaoService;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.data.ReadMessageDataWrap;
import com.foreveross.atwork.modules.chat.data.SendMessageDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.fragment.ChatListFragment;
import com.foreveross.atwork.modules.chat.service.FileDownloadNotifyService;
import com.foreveross.atwork.modules.chat.util.DiscussionHelper;
import com.foreveross.atwork.modules.chat.util.EmergencyMessageConfirmHelper;
import com.foreveross.atwork.modules.chat.util.HideMessageHelper;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.modules.chat.util.UndoMessageHelper;
import com.foreveross.atwork.modules.configSettings.manager.ConfigSettingsManager;
import com.foreveross.atwork.modules.contact.fragment.ContactFragment;
import com.foreveross.atwork.modules.gather.manager.GaherMessageManager;
import com.foreveross.atwork.modules.log.service.LoggerManager;
import com.foreveross.atwork.modules.main.helper.NetworkErrorViewManager;
import com.foreveross.atwork.modules.meeting.service.MeetingNoticeService;
import com.foreveross.atwork.modules.voip.service.VoipEventService;
import com.foreveross.atwork.modules.voip.service.ZoomVoipEventService;
import com.foreveross.atwork.services.ImSocketService;
import com.foreveross.atwork.services.support.AlarmMangerHelper;
import com.foreveross.atwork.support.AtworkBaseActivity;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.MessagesRemoveHelper;
import com.foreveross.atwork.utils.UserRemoveHelper;

import java.util.List;
import java.util.UUID;

/**
 * Created by lingen on 15/4/15.
 * Description:
 */
public class AtworkReceiveListener implements ReceiveListener {

    public static String pintPongUUID = null;
    private ImSocketService mImSocketService;

    public static long lastPongTimes = TimeUtil.getCurrentTimeInMillis();
    public static long lastPingTimes = TimeUtil.getCurrentTimeInMillis();


    public AtworkReceiveListener(ImSocketService imSocketService) {
        this.mImSocketService = imSocketService;
    }


    @Override
    public void beforeHandle(PostTypeMessage postTypeMessage) {
        OfflineMessagesReplayStrategyTimeWatcher.INSTANCE.updateMessageRecordSync(postTypeMessage);
    }

    @Override
    public void receiveCmdGatherMessage(CmdGatherMessage gatherMessage) {
        GaherMessageManager.INSTANCE.handleGatherMessage(gatherMessage);
    }

    /**
     * 收到聊天消息(在线)
     *
     * @param message
     */
    @Override
    public void receiveChatMessage(ChatPostMessage message) {
        receiveChatMessageWithoutReceiptHandle(message);

//        setLatestMessageToSp(message.deliveryTime, message.deliveryId);
        OfflineMessagesReplayStrategyTimeWatcher.INSTANCE.setLatestMessageTime(message);

        LogUtil.d(ImSocketService.TAG, "收到一条消息：" + message);
    }


    /**
     * 收到在线消息, 不包括回执处理
     */
    public void receiveChatMessageWithoutReceiptHandle(ChatPostMessage message) {
        ChatMessageHelper.dealWithChatMessage(mImSocketService, message, true);

        //通知session有新消息
        ChatSessionDataWrap.getInstance().asyncReceiveChatMessage(message);

        //发出广播，通知聊天界面收到新消息
        ChatMessageHelper.notifyMessageReceived(message);

        Context context = BaseApplicationLike.baseContext;

        //更新个人信息(PC 同时在线, 不用更新自己)
        updateFromSenderInfo(context, message);

        LogUtil.d(ImSocketService.TAG, "收到一条消息：" + message);
        SessionRefreshHelper.notifyRefreshSession();

    }

    private void updateFromSenderInfo(Context context, ChatPostMessage message) {
        UserDaoService.getInstance().updateUserBasicInfo(context, message.from, message.mFromDomain, message.mMyName, message.mMyAvatar, message.mMyStatus, message.deliveryTime);

        if(ParticipantType.Discussion == message.mToType && !StringUtils.isEmpty(message.mMyNameInDiscussion)) {

            Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(context, message.to);
            if(null != discussion) {
                EmployeeDaoService.getInstance().updateEmpAvatarAndName(context, message.from, discussion.getOrgCodeCompatible(), message.mMyNameInDiscussion, message.mMyAvatarInDiscussion);

            }
        }

    }


    @Override
    public void receiveNoticeMessage(NotifyPostMessage notifyPostMessage) {
        if (notifyPostMessage instanceof DiscussionNotifyMessage) {
            DiscussionNotifyManger.getInstance().receiveDiscussionNotify((DiscussionNotifyMessage) notifyPostMessage);

        } else if (notifyPostMessage instanceof FriendNotifyMessage) {
            FriendManager.getInstance().receiveFriendNotify((FriendNotifyMessage) notifyPostMessage, true);

        } else if (notifyPostMessage instanceof OrgNotifyMessage) {
            OrganizationManager.getInstance().receiveOrgNotify((OrgNotifyMessage) notifyPostMessage, true);

        } else if (notifyPostMessage instanceof ContactNotifyMessage) {
            ContactManager.receiveContactNotify((ContactNotifyMessage) notifyPostMessage, true);

        } else if (notifyPostMessage instanceof P2PNotifyMessage) {
            P2PNotifyMessage p2PNotifyMessage = (P2PNotifyMessage) notifyPostMessage;
            dealWithP2PNotifyMessage(p2PNotifyMessage);

        }else if(notifyPostMessage instanceof DiscussionMeetingNotifyMessage) {
            DiscussionMeetingNotifyMessage discussionMeetingNotifyMessage = (DiscussionMeetingNotifyMessage) notifyPostMessage;
            MeetingNoticeService.receiveMeetingNotify(BaseApplicationLike.baseContext, discussionMeetingNotifyMessage, true);

        } else if(notifyPostMessage instanceof MeetingNotifyMessage) {
            MeetingNotifyMessage meetingNotifyMessage = (MeetingNotifyMessage) notifyPostMessage;
            MeetingNoticeService.receiveMeetingNotify(BaseApplicationLike.baseContext, meetingNotifyMessage, true);

        } else if(notifyPostMessage instanceof EmergencyNotifyMessage) {
            EmergencyNotifyMessage emergencyNotifyMessage = (EmergencyNotifyMessage) notifyPostMessage;
            EmergencyMessageConfirmHelper.updateConfirmResultAndUpdateDbSync(emergencyNotifyMessage.mSourceId, emergencyNotifyMessage.mMsgIdConfirmed);

        }else if(notifyPostMessage instanceof UserFileDownloadNotifyMessage) {
            UserFileDownloadNotifyMessage fileDownloadNotifyMessage = (UserFileDownloadNotifyMessage) notifyPostMessage;
            FileDownloadNotifyService.receiveUserFileDownloadNotifyMessage(fileDownloadNotifyMessage, true);

        } else if(notifyPostMessage instanceof UserNotifyMessage) {
            ConfigSettingsManager.INSTANCE.receiveSettingChangedUserNotifyMessage((UserNotifyMessage) notifyPostMessage, true);

        } else if(notifyPostMessage instanceof ConversationNotifyMessage) {
            ConfigSettingsManager.INSTANCE.receiveConversationSettingChangedNotifyMessage((ConversationNotifyMessage) notifyPostMessage, true);
        }


        OfflineMessagesReplayStrategyTimeWatcher.INSTANCE.setLatestMessageTime(notifyPostMessage);
//        setLatestMessageToSp(notifyPostMessage.deliveryTime, notifyPostMessage.deliveryId);
    }


    private void dealWithP2PNotifyMessage(P2PNotifyMessage p2PNotifyMessage) {
        LogUtil.d("REMOVE_CONTACT", p2PNotifyMessage.from);
        if (P2PNotifyMessage.P2POperation.REMOVE_CONTACT.equals(p2PNotifyMessage.operation)) {
            //删除会话
            ChatSessionDataWrap.getInstance().removeSessionSafely(p2PNotifyMessage.from);
            //删除缓存
            UserCache.getInstance().removeUserCache(p2PNotifyMessage.from);
            //删除联系人
            UserRepository.getInstance().removeUserById(p2PNotifyMessage.from);
        }

        //获得修改用户名 的通知
        if (P2PNotifyMessage.P2POperation.NOTIFY_CHANGE.equals(p2PNotifyMessage.operation)) {
//            updateNewContact(); // todo 更新自己??
        }

        ContactFragment.contactsDataChanged(BaseApplicationLike.baseContext);
    }


    @Override
    public void receiveCmdMessage(CmdPostMessage cmdPostMessage) {
        //收到踢人命令 或者 被重置密码命令
        if (CmdPostMessage.Operation.KICK.equals(cmdPostMessage.operation)) {
            AtworkApplicationLike.clearData();
            LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(AtworkBaseActivity.KICK));
            Logger.e("ACCESSTOEKN", "ACCESSTOKEN , kick cmd from server");

        } else if (CmdPostMessage.Operation.RESET_CREDENTIALS.equals(cmdPostMessage.operation)) {
            AtworkApplicationLike.clearData();
            LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(AtworkBaseActivity.RESET_CREDENTIALS));
            Logger.e("ACCESSTOEKN", "ACCESSTOKEN ,reset cmd from server");

        } else if (CmdPostMessage.Operation.EMPLOYEE_FIRE.equals(cmdPostMessage.operation)) {
            if (null != cmdPostMessage.employeeHandled) {

                OrganizationManager.getInstance().handleRemoveOrgAction(BaseApplicationLike.baseContext, cmdPostMessage.orgCode);

            }


        } else if (CmdPostMessage.Operation.USER_REMOVED.equals(cmdPostMessage.operation)) {
            AtworkApplicationLike.clearData();
            LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(AtworkBaseActivity.USER_REMOVED));
            Logger.e("ACCESSTOEKN", "ACCESSTOKEN , remove cmd from server");

        } else if (CmdPostMessage.Operation.DEVICE_FORBIDDEN.equals(cmdPostMessage.operation)) {
            AtworkApplicationLike.clearData();
            UserRemoveHelper.clean(BaseApplicationLike.baseContext);

            LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(AtworkBaseActivity.DEVICE_FORBIDDEN));
            Logger.e("ACCESSTOEKN", "ACCESSTOKEN , remove cmd from server");

        } else if(CmdPostMessage.Operation.UPLOAD_LOG.equals(cmdPostMessage.operation)) {
            Logger.e("UPLOAD_LOG", "UPLOAD_LOG receive " + JsonUtil.toJson(cmdPostMessage));
            LoggerManager.INSTANCE.uploadLog(cmdPostMessage.intervalBegin, cmdPostMessage.intervalEnd);
        }

        OfflineMessagesReplayStrategyTimeWatcher.INSTANCE.setLatestMessageTime(cmdPostMessage);

//        setLatestMessageToSp(cmdPostMessage.deliveryTime, cmdPostMessage.deliveryId);
    }

    @Override
    public void receiveSystemMessage(final SystemPostMessage systemPostMessage) {
        if (Operation.APP_NOT_FOUND.equals(systemPostMessage.operation)) {
            //TODO groupMemberKick整合成可扩展的形式, 暂时现在不把踢出广播加到方法里

            if (!TextUtils.isEmpty(systemPostMessage.operationAppId)) {
                //clear all data about this id in app
                ChatSessionDataWrap.getInstance().removeSession(systemPostMessage.operationAppId, true);
                AppDaoService.getInstance().removeApp(systemPostMessage.operationAppId, null);
                AppCache.getInstance().removeAppCache(systemPostMessage.operationAppId);

                Intent intent = new Intent(DiscussionHelper.SESSION_INVALID);
                intent.putExtra(DiscussionHelper.SESSION_INVALID_ID, systemPostMessage.operationAppId);
                intent.putExtra(DiscussionHelper.SESSION_INVALID_TYPE, DiscussionHelper.SESSION_INVALID_SERVE_NO);
                LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
            }
            return;
        }
        if (!TextUtils.isEmpty(systemPostMessage.from)) {
            UserManager.getInstance().queryUserByUserId(BaseApplicationLike.baseContext, systemPostMessage.from, systemPostMessage.mFromDomain, new UserAsyncNetService.OnQueryUserListener() {
                @Override
                public void onSuccess(@NonNull User loginUser) {
                    handleUnknownSystemMessage(systemPostMessage, loginUser);

                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
                }

            });
        }
    }

    @Override
    public void receiveEventMessage(EventPostMessage eventPostMessage) {

        if (eventPostMessage instanceof HideEventMessage) {
            HideMessageHelper.hideMessage((HideEventMessage) eventPostMessage);

        } else if (eventPostMessage instanceof UndoEventMessage) {
            UndoMessageHelper.undoMessages((UndoEventMessage) eventPostMessage);

        }

//        setLatestMessageToSp(eventPostMessage.deliveryTime, eventPostMessage.deliveryId);
        OfflineMessagesReplayStrategyTimeWatcher.INSTANCE.setLatestMessageTime(eventPostMessage);


    }


    @Override
    public void receiveVoipMessage(VoipPostMessage voipPostMessage) {
//        setLatestMessageToSp(voipPostMessage.deliveryTime, voipPostMessage.deliveryId);

        OfflineMessagesReplayStrategyTimeWatcher.INSTANCE.setLatestMessageTime(voipPostMessage);

        try {
            if(AtworkConfig.OPEN_VOIP) {
                if(voipPostMessage.isZoomProduct()) {
                    ZoomVoipEventService.getInstance().receiveVoipPostMessage(voipPostMessage);

                } else {
                    VoipEventService.getInstance().receiveVoipPostMessage(voipPostMessage);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void handleUnknownSystemMessage(SystemPostMessage message, User user) {
        String meUserId = LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext);
        String meDomainId = LoginUserInfo.getInstance().getLoginUserDomainId(BaseApplicationLike.baseContext);

        String content = user.getShowName() + String.format(BaseApplicationLike.baseContext.getResources().getString(R.string.known_message), message.desc == null ? "" : message.desc);
        SystemChatMessage systemChatMessage = new SystemChatMessage(SystemChatMessage.Type.UNKNOWN, content, message.from, meUserId, message.mFromDomain, meDomainId);
        systemChatMessage.deliveryId = message.deliveryId;
        systemChatMessage.deliveryTime = message.deliveryTime;

//        ChatSessionDataWrap.getInstance().updateSessionByName(message.from, contact.name);
        ChatSessionDataWrap.getInstance().asyncReceiveChatMessage(systemChatMessage);
    }

    @Override
    public void ack(AckPostMessage ackMessage) {
        //IM报告消息发出去了
        if (AckPostMessage.AckType.WRITE.equals(ackMessage.type)) {
            ackWrite(ackMessage);
        }
        //收到已读回执
        else if (AckPostMessage.AckType.READ.equals(ackMessage.type)) {
            ackRead(ackMessage);

        } else if(AckPostMessage.AckType.REMOVE.equals(ackMessage.type)) {
            ackRemove(ackMessage);

        }
//        setLatestMessageToSp(ackMessage.deliveryTime, ackMessage.deliveryId);
        OfflineMessagesReplayStrategyTimeWatcher.INSTANCE.setLatestMessageTime(ackMessage);

    }

    private void ackRemove(AckPostMessage ackMessage) {
        MessagesRemoveHelper.removeMessages(ackMessage);
    }

    private void ackRead(AckPostMessage ackMessage) {
        ChatMessageHelper.makeAckIdsCompatibleSync(ackMessage);

        List<ReceiptMessage> receiptMessages = ChatMessageHelper.toReceiptMessage(ackMessage);
        //保存已读回执
        ReceiptRepository.getInstance().batchInsertReceipt(receiptMessages);
        UserHandleBasic user = LoginUserInfo.getInstance().getLoginUserBasic(BaseApplicationLike.baseContext);

        //如果from是自己，说明这个已读回执来自多终端(pc)，需要处理未读消息,不用再发已读回执了
        if (ackMessage.from.equalsIgnoreCase(user.mUserId)) {
            if(!ackMessage.isFromBing()) {
                ChatMessageHelper.handleSelfReadMessages(ackMessage, true);
            }


            MessageNoticeManager.getInstance().clear(ackMessage.getSessionChatId(BaseApplicationLike.baseContext).hashCode());
        } else {
            ChatMessageHelper.handlePeerReadMessage(ackMessage, true);

        }
    }

    private void ackWrite(AckPostMessage ackMessage) {
        SendMessageDataWrap.getInstance().dealChatMessageReceived(ackMessage.ackIds, ackMessage.ackTime);
        SendMessageDataWrap.getInstance().dealEventMessageReceived(ackMessage.ackIds);
        SendMessageDataWrap.getInstance().dealAckMessageReceived(ackMessage.ackIds);
        SendMessageDataWrap.getInstance().dealNotifyMessageReceived(ackMessage.ackIds);
        ReadMessageDataWrap.getInstance().dealMessageReadReceived(ackMessage);

        //通知SESSION刷新状态
        ChatSessionDataWrap.getInstance().notifyMessageSendSuccess(ackMessage.ackIds);
        mImSocketService.notifySendingMessageSuccess();
    }


    @Override
    public void confirmAuthorization(ConnectAckTypeMessage connectAckTypeMessage) {

        Logger.e(ImSocketService.TAG, "接入socket授权成功");

        mImSocketService.resetEndpointRetryDurationControl();

        if (connectAckTypeMessage != null) {
            TimeUtil.timeToServer = System.currentTimeMillis() - connectAckTypeMessage.timestamp;

            LogUtil.e("timechange", "confirmAuthorization time to server : " + TimeUtil.timeToServer);
        }
        Log.e("eee", "AtworkReceiveListener confirmAuthorization: " );
        handleOtherInfo(connectAckTypeMessage);

        mImSocketService.mConnecting = false;
        lastPongTimes = TimeUtil.getCurrentTimeInMillis();

        //通知IM连接成功
        NetworkErrorViewManager.notifyIMSuccess(mImSocketService);

        mImSocketService.startHeartBeat();
        LoginUserInfo info = LoginUserInfo.getInstance();
        if (info == null) {
            Logger.e("AtworkReceiveListener", "login info is null...");
            return;
        }


        if (!info.isOfflinePulling()) {
            info.setOfflineIsPulling(mImSocketService, true);

            if(DevCommonShareInfo.INSTANCE.isOpenOfflineMessageSessionStrategy(AtworkApplicationLike.baseContext)) {
                OfflineMessageSessionStrategyManager.INSTANCE.querySessionList(this);

            } else {
                OfflineMessageReplayStrategyManager.getInstance().clearOfflineMsgCount();
                OfflineMessageReplayStrategyManager.getInstance().getMessagePerPage(mImSocketService, this, PersonalShareInfo.getInstance().getLatestMessageTime(BaseApplicationLike.baseContext), PersonalShareInfo.getInstance().getLatestMessageId(BaseApplicationLike.baseContext));
            }


        } else {
            //如果正在拉离线, 重新刷新错误次数
            OfflineMessageReplayStrategyManager.getInstance().clearOfflineErrorTimes();
        }



        mImSocketService.resendSendingMessage();
    }

    @Override
    public void receiveError(boolean reSetConnectStatus, String error) {

        LogUtil.e(ImSocketService.TAG, "reSetConnectStatus : " + reSetConnectStatus + " error : " + error);

        if(reSetConnectStatus) {
            mImSocketService.mConnecting = false;
        }

        if (!mImSocketService.mConnecting) {
            Logger.e(ImSocketService.TAG, "receiveError");
            //准备重置连接
            mImSocketService.notifyConnectionError();
        }
    }

    @Override
    public void receiveDeviceOnlineMessage(DeviceInfoMessage message) {
        if (message instanceof DeviceOnlineMessage) {
            sendDeviceOnlineBroadcast(true, message.mDeviceSystem, message.mDeviceId);
            return;
        }
        sendDeviceOnlineBroadcast(false, message.mDeviceSystem, message.mDeviceId);
    }

    @Override
    public void receiveUserTyping(UserTypingMessage typingMessage) {
        sendUserTypingBroadcast(typingMessage);
    }

    private void sendUserTypingBroadcast(UserTypingMessage typingMessage) {
        if(null == typingMessage) {
            return;
        }

        Session session = ChatSessionDataWrap.getInstance().getSession(typingMessage.getSessionChatId(), null);

        if(null != session && session.visible) {
            Intent intent = new Intent(ChatDetailFragment.CHAT_MESSAGE_USER_TYPING);
            intent.putExtra(ChatDetailFragment.DATA_NEW_MESSAGE, typingMessage);
            LocalBroadcastManager.getInstance(AtworkApplicationLike.sApp).sendBroadcast(intent);
        }

    }



    //    private void sendDeviceOnlineBroadcast(boolean status) {
//        Intent intent = new Intent(ChatListFragment.DEVICE_ONLINE_STATUS);
//        intent.putExtra(ChatListFragment.INTENT_DEVICE_ONLINE_STATUS, status);
//        LocalBroadcastManager.getInstance(AtworkApplicationLike.sApp).sendBroadcast(intent);
//    }
    private void sendDeviceOnlineBroadcast(boolean status, String deviceSystem, String pcDeviceId) {
        Intent intent = new Intent(ChatListFragment.DEVICE_ONLINE_STATUS);
        intent.putExtra(ChatListFragment.INTENT_DEVICE_ONLINE_STATUS, status);
        intent.putExtra(ChatListFragment.INTENT_DEVICE_SYSTEM, deviceSystem);
        intent.putExtra(ChatListFragment.INTENT_PC_DEVICE_ID, pcDeviceId);
        LocalBroadcastManager.getInstance(AtworkApplicationLike.sApp).sendBroadcast(intent);
    }

    @Override
    public void exit() {
        LogUtil.d(ImSocketService.TAG, "SOCKET连接中断");
    }

    @Override
    public void pong(PongMessage pongMessage) {

        LogUtil.e(ImSocketService.TAG, "收到心跳 pong");

        if (pongMessage != null) {
            TimeUtil.timeToServer = System.currentTimeMillis() - pongMessage.timestamp;

            LogUtil.e("timechange", "time to server : " + TimeUtil.timeToServer);
        }
        pintPongUUID = UUID.randomUUID().toString();
        lastPongTimes = TimeUtil.getCurrentTimeInMillis();
        handleOtherInfo(pongMessage);

        AlarmMangerHelper.setHeartBeatAlarm(BaseApplicationLike.baseContext);
    }

    private void handleOtherInfo(HasTimestampResponse response) {
        if(null == response) {
            return;
        }

        String deviceSystem = "";
        String deviceId = "";
        if (!ListUtil.isEmpty(response.mOthers)) {
            boolean isContainPCDevice = false;
            for (HasTimestampResponse.OthersInfo others : response.mOthers) {
                deviceSystem = others.mDeviceSystem;
                deviceId = others.mDeviceId;
                if ("pc".equalsIgnoreCase(others.mDevicePlatform) || "pcweb".equalsIgnoreCase(others.mDevicePlatform)) {
                    isContainPCDevice = true;
                }
            }
//            //判断当前状态是否相等,避免一直重复更新UI
//            Boolean spInOnline = PersonalShareInfo.getInstance().isPCOnline();
//            String spDeviceSystem = PersonalShareInfo.getInstance().getDeviceSystem();

            sendDeviceOnlineBroadcast(isContainPCDevice, deviceSystem, deviceId);
        } else {
            sendDeviceOnlineBroadcast(false, deviceSystem, deviceId);
        }
    }


    private void setLatestMessageToSp(long time, String messageId) {
        boolean isOfflinePulling = LoginUserInfo.getInstance().isOfflinePulling();
        boolean isOfflinePullingError = LoginUserInfo.getInstance().isOfflinePullingError();
        if (!isOfflinePulling && !isOfflinePullingError) {
            PersonalShareInfo.getInstance().setLatestMessageTime(BaseApplicationLike.baseContext, time, messageId);
        }
    }

}
