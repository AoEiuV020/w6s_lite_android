package com.foreveross.atwork.manager.im;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.db.service.repository.ReceiptRepository;
import com.foreveross.atwork.api.sdk.message.MessageAsyncNetService;
import com.foreveross.atwork.api.sdk.message.model.MessagesResult;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.ReceiptMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.CmdPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.EventPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.VoipPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ack.AckPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.gather.CmdGatherMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.ContactNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.DiscussionMeetingNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.DiscussionNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.EmergencyNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.FriendNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.MeetingNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.OrgNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.UserFileDownloadNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.conversation.ConversationNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.user.UserNotifyMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.manager.ContactManager;
import com.foreveross.atwork.modules.discussion.manager.DiscussionNotifyManger;
import com.foreveross.atwork.manager.FriendManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.manager.ReceivingTitleQueueManager;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.service.FileDownloadNotifyService;
import com.foreveross.atwork.modules.chat.util.BurnModeHelper;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.modules.chat.util.EmergencyMessageConfirmHelper;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.modules.configSettings.manager.ConfigSettingsManager;
import com.foreveross.atwork.modules.gather.manager.GaherMessageManager;
import com.foreveross.atwork.modules.meeting.service.MeetingNoticeService;
import com.foreveross.atwork.modules.voip.service.VoipEventService;
import com.foreveross.atwork.services.receivers.AtworkReceiveListener;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.MessagesRemoveHelper;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dasunsy on 2016/12/15.
 */
public class OfflineMessageReplayStrategyManager extends OfflineMessageManager {

    private static final Object sLock = new Object();

    public static final String TAG = "OFFLINE_SESSION_STRATEGY";



    public int mOfflineErrorTimes = 0;

    public int mOfflineMsgCount;  //离线所拉取的消息数, 用以统计

    private static OfflineMessageReplayStrategyManager sInstance = null;

    public static OfflineMessageReplayStrategyManager getInstance() {
        if (null == sInstance) {
            //double check
            synchronized (sLock) {
                if (null == sInstance) {
                    sInstance = new OfflineMessageReplayStrategyManager();
                }
            }
        }

        return sInstance;
    }

    public void clearOfflineMsgCount() {
        setOfflineMsgCount(0);
    }

    public void clearOfflineErrorTimes() {
        setOfflineErrorTimes(0);
    }


    public void setOfflineMsgCount(int count) {
        this.mOfflineMsgCount = count;
    }

    public void setOfflineErrorTimes(int times) {
        this.mOfflineErrorTimes = times;
    }


    public void getMessagePerPage(Context context, AtworkReceiveListener atworkReceiveListener, long begin, String lastMsgId) {
        ReceivingTitleQueueManager.getInstance().push(context, ReceivingTitleQueueManager.TAG_GET_OFFLINE);

        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);

        if (TextUtils.isEmpty(accessToken)) {
            Logger.e(TAG, "accessToken is missing, return");
            return;
        }

        MessageAsyncNetService.getMessagesPerPage(context, (messagesResult, begin1) -> {
            if (messagesResult.mSuccess) {
                mOfflineErrorTimes = 0;

                if (shouldStopOfflineBeforeHandled(begin, messagesResult)) {
                    finishOfflinePulling(context, messagesResult);

                    return;
                }


                mOfflineMsgCount += messagesResult.mRealOfflineMsgSize;

                handleOfflineMessage(context, atworkReceiveListener, begin, messagesResult);
            } else {
//                if(!messagesResult.mHttpResult.isTimeOutError()) {
//                    mOfflineErrorTimes++;
//
//                }
                mOfflineErrorTimes++;

                //尝试3次去拉取离线
                if (3 > mOfflineErrorTimes) {
                    Logger.e(TAG, "get offline error times = " + mOfflineErrorTimes);
                    getMessagePerPage(context, atworkReceiveListener, begin1, lastMsgId);
                } else {
                    //finally server return error more than 3 times
                    LoginUserInfo.getInstance().setOfflinePullingError(true);
                    LoginUserInfo.getInstance().setOfflineIsPulling(context, false);
                    notifyRefreshSessionInOffline(context);
                }

            }
        }, begin, lastMsgId);
    }



    @SuppressLint("StaticFieldLeak")
    private void handleOfflineMessage(Context context, AtworkReceiveListener atworkReceiveListener, final long begin, final MessagesResult messagesResult) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                return replayOfflineMessage(context, messagesResult, atworkReceiveListener);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (shouldStopOfflineAfterHandled(begin, messagesResult)) {
                    finishOfflinePulling(context, messagesResult);
                    return;
                }

                PostTypeMessage lastPostTypeMessage = messagesResult.getLastMessage();

                if (null != lastPostTypeMessage) {
                    PersonalShareInfo.getInstance().setLatestMessageTime(context, lastPostTypeMessage.deliveryTime, lastPostTypeMessage.deliveryId);
                    OfflineMessagesReplayStrategyTimeWatcher.INSTANCE.removeMessageRecordsAndSaveLast(lastPostTypeMessage);

                    getMessagePerPage(context, atworkReceiveListener, lastPostTypeMessage.deliveryTime, lastPostTypeMessage.deliveryId);

                }


            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Nullable
    public Void replayOfflineMessage(Context context, MessagesResult messagesResult, AtworkReceiveListener atworkReceiveListener) {
        long start = System.currentTimeMillis();

        List<ChatPostMessage> chatPostMessages = new ArrayList<>();
        List<EventPostMessage> eventPostMessageList = new ArrayList<>();
        List<NotifyPostMessage> notifyPostMessages = new ArrayList<>();
        List<AckPostMessage> ackPostMessages = new ArrayList<>();
        List<CmdPostMessage> cmdPostMessages = new ArrayList<>();
        LinkedHashMap<String, List<VoipPostMessage>> voipMap = new LinkedHashMap<>();

        List<ReceiptMessage> receiptMessages = new ArrayList<>(messagesResult.mReceiptMessages);



        for (PostTypeMessage postTypeMessage : messagesResult.mPostTypeMessages) {
            if (postTypeMessage instanceof ChatPostMessage) {
                ChatPostMessage chatPostMessage = (ChatPostMessage) postTypeMessage;
                if (BurnModeHelper.isMsgExpired(chatPostMessage)
                        || chatPostMessage.isHide())
                    continue;
            }


            if (postTypeMessage instanceof ChatPostMessage) {
                if(!postTypeMessage.isBingReplyType()) {
                    chatPostMessages.add((ChatPostMessage) postTypeMessage);
                }
                continue;
            }

            if (postTypeMessage instanceof NotifyPostMessage) {
                notifyPostMessages.add((NotifyPostMessage) postTypeMessage);
                continue;
            }

            if (postTypeMessage instanceof AckPostMessage) {
                ackPostMessages.add((AckPostMessage) postTypeMessage);
                continue;
            }

            if (postTypeMessage instanceof CmdPostMessage) {
                cmdPostMessages.add((CmdPostMessage) postTypeMessage);
                continue;
            }

            if (postTypeMessage instanceof EventPostMessage) {
                eventPostMessageList.add((EventPostMessage) postTypeMessage);
                continue;
            }


            if(postTypeMessage instanceof VoipPostMessage) {
                VoipPostMessage voipPostMessage = (VoipPostMessage) postTypeMessage;
                if (voipPostMessage.isLegal()) {
                    List<VoipPostMessage> voipPostMessageList = voipMap.get(voipPostMessage.mMeetingId);

                    if(null == voipPostMessageList) {
                        voipPostMessageList = new ArrayList<>();
                    }

                    voipPostMessageList.add(voipPostMessage);

                    voipMap.put(voipPostMessage.mMeetingId, voipPostMessageList);
                }
                continue;
            }

            if(postTypeMessage instanceof CmdGatherMessage) {
                CmdGatherMessage cmdGatherMessage = (CmdGatherMessage) postTypeMessage;
                GaherMessageManager.INSTANCE.updateOfflinePullingData(cmdGatherMessage);
                continue;
            }


        }

        ReceiptRepository.getInstance().batchInsertReceipt(receiptMessages);

        //先处理通知
        Map<String, List<DiscussionNotifyMessage>> discussionNotifyMap = new HashMap<>();

        batchDealNotifyMessages(context, notifyPostMessages, discussionNotifyMap);

        long batchDealChatMessagesBeginTime = System.currentTimeMillis();

        //再接收消息
        Map<String, List<ChatPostMessage>> chatMessageMap
                = batchDealChatMessages(context, chatPostMessages);

        long batchDealChatMessagesEndTime = System.currentTimeMillis();
        LogUtil.e(TAG, "offline msg batchDealChatMessages duration -> " + (batchDealChatMessagesEndTime - batchDealChatMessagesBeginTime));


        //处理完消息后才处理"紧急呼"通知
        batchDealEmergencyNotifyMessages(notifyPostMessages);


        //处理 voip 消息
        if (AtworkConfig.OPEN_VOIP) {

            VoipEventService.getInstance().getOfflineEventController().batchHandleVoipMessageOffline(context, voipMap);
        }


        //处理群删除类型的通知
        Set<String> discussionIds = discussionNotifyMap.keySet();
        for (String discussionId : discussionIds) {
            DiscussionNotifyManger.getInstance().receiveDiscussionOfflineRemoveNotifys(context, discussionId, discussionNotifyMap.get(discussionId));
        }

        //update session count in cache
        ChatSessionDataWrap.getInstance().updateSessionCountFromCache();

        batchDealEventMessages(atworkReceiveListener, eventPostMessageList);

        //刷新最后一条 session
        handleSessionLastInOffLine(context, discussionNotifyMap, chatMessageMap);

        batchDealAcks(ackPostMessages);


        ChatDaoService.getInstance().checkOfflineMessagesBatchInsert(messagesResult.mSessionIdPullingMessages);


        SessionRefreshHelper.notifyRefreshSessionAndCount();
        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();

        handleOfflineCmd(context, cmdPostMessages, atworkReceiveListener);

        Long end = System.currentTimeMillis();
        LogUtil.e(TAG, "offline msg TOTAL = " + (end - start));
        //处理CMD消息
        return null;
    }

    private void handleOfflineCmd(Context context, List<CmdPostMessage> cmdPostMessages, AtworkReceiveListener atworkReceiveListener) {
        for (CmdPostMessage cmdPostMessage : cmdPostMessages) {
            if (LoginUserInfo.getInstance().getLoginTime(context) < cmdPostMessage.deliveryTime) {
                atworkReceiveListener.receiveCmdMessage(cmdPostMessage);
            }
        }
    }



    private void finishOfflinePulling(Context context, MessagesResult messagesResult) {

        LoginUserInfo.getInstance().setOfflinePullingError(false);
        LoginUserInfo.getInstance().setOfflineIsPulling(context, false);

        //回放 voip 事件
        VoipEventService.getInstance().getOfflineEventController().replayOfflineEvent(context);

        //回放 gather 消息
        GaherMessageManager.INSTANCE.replayOfflinePullingData();

        LogUtil.e("offline count", "offline count -----> " + mOfflineMsgCount);

        LogUtil.e(TAG, "消息拉取完毕");

        PostTypeMessage lastPostTypeMessage = messagesResult.getLastMessage();

        if(null != lastPostTypeMessage) {
            PersonalShareInfo.getInstance().setLatestMessageTime(context, lastPostTypeMessage.deliveryTime, lastPostTypeMessage.deliveryId);
            OfflineMessagesReplayStrategyTimeWatcher.INSTANCE.removeMessageRecordsAndSaveLast(lastPostTypeMessage);
        }

        notifyRefreshSessionInOffline(context);
    }

    private boolean shouldStopOfflineAfterHandled(long beginTime, MessagesResult messagesResult) {
        boolean shouldStopOffline = messagesResult.mRealOfflineMsgSize < AtworkConfig.COUNT_SYNC_MESSAGE_BATCH;

//        if(1 == messagesResult.mPostTypeMessages.size()) {
//            PostTypeMessage lastPostTypeMessage = messagesResult.mPostTypeMessages.get(messagesResult.mPostTypeMessages.size() - 1);
//            if(beginTime == lastPostTypeMessage.deliveryTime){
//                shouldStopOfflineAfterHandled = true;
//            }
//
//        } else if(0 == messagesResult.mPostTypeMessages.size()){
//            shouldStopOfflineAfterHandled = true;
//        }
        return shouldStopOffline;
    }


    private boolean shouldStopOfflineBeforeHandled(long beginTime, MessagesResult messagesResult) {
        boolean shouldStopOffline = false;
        //当拉取回来的消息只有1条, 且时间戳与 began 一样, 这条消息肯定已经处理过, 直接过滤掉
        if (0 == messagesResult.mRealOfflineMsgSize) {
            shouldStopOffline = true;

        } else if (1 == messagesResult.mRealOfflineMsgSize && !ListUtil.isEmpty(messagesResult.mPostTypeMessages)) {

            PostTypeMessage postTypeMessage = messagesResult.mPostTypeMessages.get(0);
            if (null != postTypeMessage && beginTime == postTypeMessage.deliveryTime) {
                shouldStopOffline = true;
            }
        }

        return shouldStopOffline;
    }

    public void batchNotifyMessageReceived(Context context, List<ChatPostMessage> messages) {
        Intent intent = new Intent(ChatDetailFragment.BATCH_MESSAGES_RECEIVED);
        intent.putExtra(ChatDetailFragment.INTENT_BATCH_MESSAGES, (Serializable) messages);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }



    /**
     * 收到离线消息
     *
     * @param context
     * @param chatPostMessageList
     */
    public Map<String, List<ChatPostMessage>> batchDealChatMessages(Context context, List<ChatPostMessage> chatPostMessageList) {
        for (ChatPostMessage chatPostMessage : chatPostMessageList) {
            ChatMessageHelper.dealWithChatMessage(context, chatPostMessage, false);
        }
        batchNotifyMessageReceived(context, chatPostMessageList);
        Map<String, List<ChatPostMessage>> notifyMap = ChatSessionDataWrap.getInstance().asyncOfflineReceiveMessages(chatPostMessageList);
        return notifyMap;
    }


    public void batchDealAcks(List<AckPostMessage> ackPostMessages) {

        LoginUserBasic meUser = LoginUserInfo.getInstance().getLoginUserBasic(BaseApplicationLike.baseContext);
        List<ReceiptMessage> receiptMessages = new ArrayList<>();
        List<AckPostMessage> bingReplyAcks = new ArrayList<>();

        for (AckPostMessage ackPostMessage : ackPostMessages) {
            if (AckPostMessage.AckType.READ.equals(ackPostMessage.type)) {

                ChatMessageHelper.makeAckIdsCompatibleSync(ackPostMessage);

                List<ReceiptMessage> receiptMessageList = ChatMessageHelper.toReceiptMessage(ackPostMessage);
                if (!ListUtil.isEmpty(receiptMessageList)) {
                    receiptMessages.addAll(receiptMessageList);
                }



                if (ackPostMessage.from.equalsIgnoreCase(meUser.mUserId)) {
                    if(!ackPostMessage.isFromBing()) {
                        ChatMessageHelper.handleSelfReadMessages(ackPostMessage, false);
                    }
                } else {
                    ChatMessageHelper.handlePeerReadMessage(ackPostMessage, false);
                }

            } else if(AckPostMessage.AckType.REMOVE.equals(ackPostMessage.type)) {
                MessagesRemoveHelper.removeMessages(ackPostMessage);
            }
        }

        ReceiptRepository.getInstance().batchInsertReceipt(receiptMessages);
    }


    private void batchDealEventMessages(AtworkReceiveListener atworkReceiveListener, List<EventPostMessage> eventPostMessageList) {
        for (EventPostMessage eventPostMessage : eventPostMessageList) {
            atworkReceiveListener.receiveEventMessage(eventPostMessage);
        }
    }

    private void batchDealNotifyMessages(Context context, List<NotifyPostMessage> notifyPostMessages, Map<String, List<DiscussionNotifyMessage>> discussionNotifyMessageMap) {

        for (NotifyPostMessage notifyMessage : notifyPostMessages) {

            if (notifyMessage instanceof DiscussionNotifyMessage) {
                String discussionId = ((DiscussionNotifyMessage) notifyMessage).to;
                if (discussionNotifyMessageMap.containsKey(discussionId)) {
                    List<DiscussionNotifyMessage> discussionNotifyMessages = discussionNotifyMessageMap.get(discussionId);
                    discussionNotifyMessages.add((DiscussionNotifyMessage) notifyMessage);

                } else {
                    List<DiscussionNotifyMessage> discussionNotifyMessages = new ArrayList<>();
                    discussionNotifyMessages.add((DiscussionNotifyMessage) notifyMessage);
                    discussionNotifyMessageMap.put(discussionId, discussionNotifyMessages);
                }

            } else if (notifyMessage instanceof FriendNotifyMessage) {
                FriendManager.getInstance().receiveFriendNotify((FriendNotifyMessage) notifyMessage, false);

            } else if (notifyMessage instanceof OrgNotifyMessage) {
                OrganizationManager.getInstance().receiveOrgNotify((OrgNotifyMessage) notifyMessage, false);

            } else if (notifyMessage instanceof ContactNotifyMessage) {
                ContactManager.receiveContactNotify((ContactNotifyMessage) notifyMessage, false);

            } else if(notifyMessage instanceof DiscussionMeetingNotifyMessage) {
                DiscussionMeetingNotifyMessage discussionMeetingNotifyMessage = (DiscussionMeetingNotifyMessage) notifyMessage;
                MeetingNoticeService.receiveMeetingNotify(BaseApplicationLike.baseContext, discussionMeetingNotifyMessage, false);

            } else if(notifyMessage instanceof MeetingNotifyMessage) {
                MeetingNotifyMessage meetingNotifyMessage = (MeetingNotifyMessage) notifyMessage;
                MeetingNoticeService.receiveMeetingNotify(BaseApplicationLike.baseContext, meetingNotifyMessage, false);

            } else if(notifyMessage instanceof UserFileDownloadNotifyMessage) {
                UserFileDownloadNotifyMessage userFileDownloadNotifyMessage = (UserFileDownloadNotifyMessage) notifyMessage;
                FileDownloadNotifyService.receiveUserFileDownloadNotifyMessage(userFileDownloadNotifyMessage, false);

            } else if(notifyMessage instanceof UserNotifyMessage) {
                ConfigSettingsManager.INSTANCE.receiveSettingChangedUserNotifyMessage((UserNotifyMessage) notifyMessage, false);

            }else if(notifyMessage instanceof ConversationNotifyMessage) {
                ConfigSettingsManager.INSTANCE.receiveConversationSettingChangedNotifyMessage((ConversationNotifyMessage) notifyMessage, false);
            }
        }

        //群通知根据会话来分类处理
        Set<String> discussionIds = discussionNotifyMessageMap.keySet();
        for (String discussionId : discussionIds) {
            DiscussionNotifyManger.getInstance().receiveDiscussionOfflineNotifys(context, discussionId, discussionNotifyMessageMap.get(discussionId));
        }

    }


    private void batchDealEmergencyNotifyMessages(List<NotifyPostMessage> notifyPostMessages) {
        for (NotifyPostMessage notifyMessage : notifyPostMessages) {
            if(notifyMessage instanceof EmergencyNotifyMessage) {
                EmergencyNotifyMessage emergencyNotifyMessage  = (EmergencyNotifyMessage) notifyMessage;
                EmergencyMessageConfirmHelper.updateConfirmResultAndUpdateDbSync(emergencyNotifyMessage.mSourceId, emergencyNotifyMessage.mMsgIdConfirmed);
            }
        }
    }


    /**
     * 处理离线后 session 最后条消息是系统通知还是 chat 的内容
     */
    private void handleSessionLastInOffLine(Context context, Map<String, List<DiscussionNotifyMessage>> groupNotifyMap
            , Map<String, List<ChatPostMessage>> chatMessageMap) {
        LoginUserBasic me = LoginUserInfo.getInstance().getLoginUserBasic(context);

        for (Map.Entry<String, List<DiscussionNotifyMessage>> entry : groupNotifyMap.entrySet()) {
            String discussionId = entry.getKey();

            List<DiscussionNotifyMessage> groupNotifyList = entry.getValue();
            DiscussionNotifyMessage latestGroupNotification = getLatestDiscussionNotification(groupNotifyList);

            if (null == latestGroupNotification) {
                continue;
            }
            if (null != latestGroupNotification.mOperator && me.mUserId.equals(latestGroupNotification.mOperator.mUserId)) {
                continue;
            }
            //对比同个 session 下的最后条消息跟最后条通知, 并根据结果更新 session
            if (chatMessageMap.containsKey(discussionId)) {


                List<ChatPostMessage> chatMessageList = chatMessageMap.get(discussionId);
                ChatPostMessage latestChatMessage = chatMessageList.get(chatMessageList.size() - 1);
                if (latestGroupNotification.deliveryTime > latestChatMessage.deliveryTime) {
                    SystemChatMessage systemChatMessage
                            = DiscussionNotifyManger.getInstance().getOfflineSystemChatMessage(context, latestGroupNotification, me);

                    ChatSessionDataWrap.getInstance().asyncReceiveChatMessage(systemChatMessage);
                }
            } else {
                SystemChatMessage systemChatMessage
                        = DiscussionNotifyManger.getInstance().getOfflineSystemChatMessage(context, latestGroupNotification, me);

                ChatSessionDataWrap.getInstance().asyncReceiveChatMessage(systemChatMessage);
            }

        }
    }


    private DiscussionNotifyMessage getLatestDiscussionNotification(List<DiscussionNotifyMessage> discussionNotifyList) {
        int size = discussionNotifyList.size();
        for (int i = size - 1; i >= 0; i--) {
            DiscussionNotifyMessage discussionNotifyMessage = discussionNotifyList.get(i);
            if (!discussionNotifyMessage.mOperation.isNeedRefreshDiscussion()) {
                return discussionNotifyMessage;
            }
        }

        return null;
    }



}
