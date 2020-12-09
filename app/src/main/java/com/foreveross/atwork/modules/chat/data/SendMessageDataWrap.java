package com.foreveross.atwork.modules.chat.data;

import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.cache.MessageCache;
import com.foreverht.db.service.repository.MessageRepository;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.user.EndPoint;
import com.foreveross.atwork.infrastructure.model.user.UserHandleBasic;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.EventPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ack.AckPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.BingUndoEventMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.shared.EndPointInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.shared.dev.LogCommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.dev.SendMsgItemLogData;
import com.foreveross.atwork.infrastructure.shared.dev.SendMsgLogData;
import com.foreveross.atwork.infrastructure.shared.dev.SendMsgSpeed;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.utils.ChatMessageHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lingen on 15/4/20.
 * Description:
 * 正在发送消息的封装类，用于处理发送消息后接收的回执
 */
public class SendMessageDataWrap {

    private static SendMessageDataWrap sSendMessageDataWrap = new SendMessageDataWrap();
    private Map<String, ChatPostMessage> mChatMessageMap = Collections.synchronizedMap(new LinkedHashMap<>());
    private Map<String, ChatPostMessage> mBingReplyMessageMap = Collections.synchronizedMap(new LinkedHashMap<>());
    private Map<String, EventPostMessage> mEventMessageMap = Collections.synchronizedMap(new LinkedHashMap<>());
    private Map<String, AckPostMessage> mAckMessageMap = Collections.synchronizedMap(new LinkedHashMap<>());
    private Map<String, NotifyPostMessage> mNotifyPostMessageMap = Collections.synchronizedMap(new LinkedHashMap<>());

    private Map<String, Long> mSendMessageDurationDataWrap = Collections.synchronizedMap(new LinkedHashMap<>());

    private SendMessageDataWrap() {

    }

    public static SendMessageDataWrap getInstance() {
        return sSendMessageDataWrap;
    }


    public void addChatSendingMessage(ChatPostMessage chatPostMessage) {
        mChatMessageMap.put(chatPostMessage.deliveryId, chatPostMessage);
    }

    public void addBingReplySendingMessage(ChatPostMessage chatPostMessage) {
        mBingReplyMessageMap.put(chatPostMessage.deliveryId, chatPostMessage);
    }

    public void addEventSendingMessage(EventPostMessage eventPostMessage) {
        mEventMessageMap.put(eventPostMessage.deliveryId, eventPostMessage);
    }

    public void addAckSendingMessage(AckPostMessage ackPostMessage) {
        mAckMessageMap.put(ackPostMessage.deliveryId, ackPostMessage);
    }

    public void addNotifySendingMessage(NotifyPostMessage notifyPostMessage) {
        mNotifyPostMessageMap.put(notifyPostMessage.deliveryId, notifyPostMessage);
    }

    public void dealChatPostMessagesNotSend(List<ChatPostMessage> chatPostMessageNotSendList) {

        synchronized (mChatMessageMap) {
            for(ChatPostMessage msgNotSend : chatPostMessageNotSendList) {
                msgNotSend.setChatStatus(ChatStatus.Not_Send);
                mChatMessageMap.remove(msgNotSend.deliveryId);
            }
        }


        synchronized (mBingReplyMessageMap) {
            for(ChatPostMessage msgNotSend : chatPostMessageNotSendList) {
                msgNotSend.setChatStatus(ChatStatus.Not_Send);
                mBingReplyMessageMap.remove(msgNotSend.deliveryId);
            }
        }
    }

    /**
     * 标志某条消息为解决
     *
     * @param msgIds
     */
    public void dealChatMessageReceived(List<String> msgIds, long ackTime) {
        List<ChatPostMessage> waitSendChatList = new ArrayList<>();
        List<String> redEnvelopeMsgIdList = new ArrayList<>();

        for (String msgId : msgIds) {
            if (!mChatMessageMap.containsKey(msgId)) {
                continue;
            }

            ChatPostMessage chatPostMessage = mChatMessageMap.get(msgId);


            handleSentDurationLog(chatPostMessage, ackTime);

            chatPostMessage.setChatStatus(ChatStatus.Sended);

            MessageRepository.getInstance().setDeliveryTimeCalibrating(chatPostMessage.deliveryId, ackTime);

            if (chatPostMessage instanceof FileTransferChatMessage) {
                FileTransferChatMessage message = (FileTransferChatMessage)chatPostMessage;
                message.fileStatus = FileStatus.SENDED;
            }

            if(chatPostMessage instanceof ImageChatMessage) {
                ImageChatMessage message = (ImageChatMessage) chatPostMessage;
                //发送成功清除内存, 节省空间
                message.clearThumbnails();
            }

            waitSendChatList.add(chatPostMessage);
        }

        ChatDaoService.getInstance().batchInsertMessages(waitSendChatList);


    }

    private void handleSentDurationLog(ChatPostMessage chatPostMessage, long ackTime) {
        long msgSendDuration = ackTime - chatPostMessage.deliveryTime;

        if(0 >= msgSendDuration) {
            return;
        }

        if(!(chatPostMessage instanceof TextChatMessage)) {
            return;
        }


        EndPoint currentEndpointInfo = EndPointInfo.getInstance().getCurrentEndpointInfo(AtworkApplicationLike.sApp);
//        Logger.e("msg", "single msg id: " + chatPostMessage.deliveryId + " sent duration: " + msgSendDuration + " connect info : " + currentEndpointInfo);

//        mSendMessageDurationDataWrap.put(chatPostMessage.deliveryId, msgSendDuration);
//        Logger.e("msg", "batch msg count :" + mSendMessageDurationDataWrap.size()  +  " average sent duration: " + CollectionsKt.averageOfLong(mSendMessageDurationDataWrap.values()));

        SendMsgLogData log = LogCommonShareInfo.INSTANCE.getSentMsgLog(AtworkApplicationLike.baseContext, currentEndpointInfo.getSessionHostCheckConfig());
        HashSet<SendMsgItemLogData> sendMsgSpeedSet = log.getLogs();

        if(null == sendMsgSpeedSet) {
            sendMsgSpeedSet = new HashSet<>();
            log.setLogs(sendMsgSpeedSet);
        }

        SendMsgItemLogData itemLog = null;
        SendMsgSpeed sendMsgSpeed = SendMsgSpeed.matchSpeed(msgSendDuration);
        if (!ListUtil.isEmpty(sendMsgSpeedSet)) {
            for(SendMsgItemLogData itemLogLoop: sendMsgSpeedSet) {
                if(sendMsgSpeed == itemLogLoop.getSpeed()) {
                    itemLog = itemLogLoop;
                    break;
                }
            }
        }

        if(null == itemLog) {
            itemLog = new SendMsgItemLogData(sendMsgSpeed, 0, 0);
            sendMsgSpeedSet.add(itemLog);
        }


        double totalDuration = itemLog.getSentMsgCount() * itemLog.getAverageDuration() + msgSendDuration;
        long totalCount = itemLog.getSentMsgCount() + 1;
        double averageDuration = totalDuration / totalCount;

        itemLog.setSentMsgCount(totalCount);
        itemLog.setAverageDuration(averageDuration);

        LogCommonShareInfo.INSTANCE.updateSentMsgLog(AtworkApplicationLike.baseContext, log);

        LogUtil.e("msg", log.toString());

    }

    public void dealEventMessageReceived(List<String> msgIds) {
        for (String msgId : msgIds) {
            if (!mEventMessageMap.containsKey(msgId)) {
                continue;
            }

            EventPostMessage eventPostMessage = mEventMessageMap.get(msgId);

            if(eventPostMessage instanceof UndoEventMessage) {
                UndoEventMessage undoEventMessage = (UndoEventMessage) eventPostMessage;
                MessageCache.getInstance().undoMessage(undoEventMessage);
                ChatDaoService.getInstance().undoMessage(AtworkApplicationLike.baseContext, undoEventMessage);
                ChatDaoService.getInstance().undoNewsSummaryMessage(AtworkApplicationLike.baseContext, undoEventMessage);

                UserHandleBasic chatUser = ChatMessageHelper.getChatUser(undoEventMessage);
                Session session = ChatSessionDataWrap.getInstance().getSession(chatUser.mUserId, null);

                if(null != session) {
                    ChatSessionDataWrap.getInstance().updateSessionForEvent(session, undoEventMessage);
                }

                Intent intent = new Intent(ChatDetailFragment.UNDO_MESSAGE_SEND_SUCCESSFULLY);
                intent.putExtra(ChatDetailFragment.DATA_NEW_MESSAGE, undoEventMessage);
                LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
            }

            mEventMessageMap.remove(msgId);
        }

    }



    public void dealAckMessageReceived(List<String> msgIds) {
        for (String msgId : msgIds) {
            if (!mAckMessageMap.containsKey(msgId)) {
                continue;
            }

            AckPostMessage eventPostMessage = mAckMessageMap.get(msgId);
            if(AckPostMessage.AckType.REMOVE.equals(eventPostMessage.type)) {
                PersonalShareInfo.getInstance().removeAcksNeedCheck(BaseApplicationLike.baseContext, msgIds);
            }

            mAckMessageMap.remove(msgId);
        }

    }

    public void dealNotifyMessageReceived(List<String> msgIds) {
        for(String msgId : msgIds) {
            if(!mNotifyPostMessageMap.containsKey(msgId)) {
                continue;
            }

            mNotifyPostMessageMap.remove(msgId);
        }
    }

    public void forHandleChatMessageMap(OnIteratorNextListener<PostTypeMessage> onIteratorNextListener) {
        /**
         * It is imperative that the user manually synchronize on the returned map when iterating over any of its collection views:
         Map m = Collections.synchronizedMap(new HashMap());
         ...
         Set s = m.keySet();  // Needn't be in synchronized block
         ...
         synchronized (m) {  // Synchronizing on m, not s!
         Iterator i = s.iterator(); // Must be in synchronized block
         while (i.hasNext())
         foo(i.next());
         }
         * */

        doForHandleChatMessageMap(mChatMessageMap, onIteratorNextListener);
        doForHandleChatMessageMap(mBingReplyMessageMap, onIteratorNextListener);

    }

    private void doForHandleChatMessageMap(Map<String, ? extends PostTypeMessage> msgMap, OnIteratorNextListener<PostTypeMessage> onIteratorNextListener) {
        Set<? extends Map.Entry<String, ? extends PostTypeMessage>> set = msgMap.entrySet();

        synchronized (msgMap) {
            Iterator<? extends Map.Entry<String, ? extends PostTypeMessage>> iterator = set.iterator();
            while (iterator.hasNext()) {
                onIteratorNextListener.next(iterator.next().getValue());
            }
        }
    }

    public interface OnIteratorNextListener<T> {
        void next(T t);
    }
}
