package com.foreverht.cache;

import androidx.annotation.Nullable;
import android.util.LruCache;

import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.user.UserHandleBasic;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.chat.BasicMsgHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lingen on 15/4/17.
 * Description:
 */
public class MessageCache {

    private static int MAX_CACHE_COUNT_IN_SESSION = 30;

    private static MessageCache messageCacheInstance = new MessageCache();
    private LruCache<String, List<ChatPostMessage>> messageCache = new LruCache<>(Integer.MAX_VALUE);



    private MessageCache() {

    }

    public static MessageCache getInstance() {
        return messageCacheInstance;
    }

    public void clear() {
        messageCache.evictAll();
    }

    /**
     * 更新Messages
     *
     * @param identifier
     */
    public void updateMessageList(String identifier, List<ChatPostMessage> messageList) {
        List<ChatPostMessage> tempMessageList = new ArrayList<>();
        tempMessageList.addAll(messageList);

        tempMessageList = cutMessages(tempMessageList, MAX_CACHE_COUNT_IN_SESSION);

        messageCache.put(identifier, tempMessageList);
    }

    private List<ChatPostMessage> cutMessages(List<ChatPostMessage> tempMessageList, int maxCount) {
        Collections.sort(tempMessageList, (lhs, rhs) -> TimeUtil.compareTo(rhs.deliveryTime, lhs.deliveryTime));

        tempMessageList = ListUtil.subListSafely(tempMessageList, 0, maxCount);
        return tempMessageList;
    }

    public void removeMessages(String identifier, List<String> msgIdList) {
        List<ChatPostMessage> chatPostMessageLst = messageCache.get(identifier);
        if(!ListUtil.isEmpty(chatPostMessageLst)) {
            List<ChatPostMessage> removedMsgList = new ArrayList<>();

            for(ChatPostMessage chatPostMessage : chatPostMessageLst) {
                if(msgIdList.contains(chatPostMessage.deliveryId)) {
                    removedMsgList.add(chatPostMessage);
                }
            }

            chatPostMessageLst.removeAll(removedMsgList);
        }
    }

    public void removeMessage(String identifier, String msgId) {
        removeMessages(identifier, ListUtil.makeSingleList(msgId));
    }

    public void clearSessionMessage(String sessiondId){
        messageCache.remove(sessiondId);
    }

    public void clearSessionsMessage(List<String> sessionIds){
        for(String sessionId: sessionIds) {
            messageCache.remove(sessionId);
        }
    }
    /**
     * 收到一条新聊天消息
     *
     * @param postTypeMessage
     */
    public void receiveMessage(ChatPostMessage postTypeMessage) {
        UserHandleBasic chatUser = BasicMsgHelper.getChatUser(postTypeMessage);
        List<ChatPostMessage> postTypeMessageArrayList = null;
        postTypeMessageArrayList = getMessageCache(chatUser.mUserId);
        //如果未有缓存，则不缓存，这时候缓存会导致只读最近收取的消息而不会从数据库中查询
        if (postTypeMessageArrayList == null) {
            return;
        }
        if(!postTypeMessageArrayList.contains(postTypeMessage)){
            postTypeMessageArrayList.add(postTypeMessage);

        } else {

        }
        updateMessageList(chatUser.mUserId, postTypeMessageArrayList);
    }

    public void receiveSystemMessage(SystemChatMessage systemChatMessage, String sessionId) {

        List<ChatPostMessage> postTypeMessageArrayList = getMessageCache(sessionId);
        //如果未有缓存，则不缓存，这时候缓存会导致只读最近收取的消息而不会从数据库中查询
        if (postTypeMessageArrayList == null) {
            return;
        }
        if(!postTypeMessageArrayList.contains(systemChatMessage)){
            postTypeMessageArrayList.add(systemChatMessage);

        }
        updateMessageList(sessionId, postTypeMessageArrayList);
    }

    public void batchAddMessage(String identifier, List<ChatPostMessage> messageList) {
        List<ChatPostMessage> postTypeMessageArrayList = null;
        postTypeMessageArrayList = getMessageCache(identifier);
        //如果未有缓存，则不缓存，这时候缓存会导致只读最近收取的消息而不会从数据库中查询
        if (postTypeMessageArrayList == null) {
            return;
        }
        for (ChatPostMessage chatPostMessage : messageList) {
            if (!postTypeMessageArrayList.contains(chatPostMessage)) {
                postTypeMessageArrayList.add(chatPostMessage);
            }
        }
        updateMessageList(identifier, postTypeMessageArrayList);
    }

    /**
     * 获取一个identifier的所有消息数
     *
     * @param identifier
     * @return
     */
    public List<ChatPostMessage> getMessageCache(String identifier) {
        return messageCache.get(identifier);
    }

    public List<ChatPostMessage> getFixSizeMessageCacheInChatView(String identifier, int fixedSize) {
        List<ChatPostMessage> resultList = new ArrayList<>();

        if(!ListUtil.isEmpty(messageCache.get(identifier))) {


            List<ChatPostMessage> msgList = new ArrayList<>(BasicMsgHelper.filterExpiredMsg(messageCache.get(identifier)));

            resultList.addAll(cutMessages(msgList, fixedSize));

//            Collections.sort(msgList, (lhs, rhs) -> TimeUtil.compareTo(lhs.deliveryTime, rhs.deliveryTime));
//
//            if(fixedSize >= msgList.size()) {
//                resultList.addAll(msgList);
//            } else {
//                resultList.addAll(msgList.subList(msgList.size() - fixedSize, msgList.size()));
//            }
        }

        return resultList;
    }

    public boolean isMessageShouldNotReceive(String identifier, ChatPostMessage chatPostMessage) {
        if(chatPostMessage.isEmergency()) {
            return false;
        }


        return isMessageExist(identifier, chatPostMessage);
    }

    private boolean isMessageExist(String identifier, ChatPostMessage chatPostMessage) {
        List<ChatPostMessage> chatPostMessageList = getMessageCache(identifier);
        if (chatPostMessageList != null) {
            return chatPostMessageList.contains(chatPostMessage);
        }
        return false;
    }


    public void undoMessage(UndoEventMessage undoEventMessage) {
        List<ChatPostMessage> msgList = queryMessageListNeedUndo(undoEventMessage);
        for(ChatPostMessage msg : msgList) {
            msg.chatStatus = ChatStatus.UnDo;
            msg.undoSuccessTime = undoEventMessage.deliveryTime;
        }
    }

    public List<ChatPostMessage> queryMessageListNeedUndo(UndoEventMessage undoEventMessage) {
        UserHandleBasic user = BasicMsgHelper.getChatUser(undoEventMessage);
        List<ChatPostMessage> chatPostMessageList = getMessageCache(user.mUserId);
        List<ChatPostMessage> msgUndoList = new ArrayList<>();
        if (chatPostMessageList != null) {

            for(ChatPostMessage chatPostMessage : chatPostMessageList) {

                if(undoEventMessage.isMsgUndo(chatPostMessage.deliveryId)) {
                    msgUndoList.add(chatPostMessage);
                }
            }
        }

        return msgUndoList;
    }


    @Nullable
    public ChatPostMessage queryMessage(ChatPostMessage chatPostMessage) {
        UserHandleBasic contact = BasicMsgHelper.getChatUser(chatPostMessage);
        List<ChatPostMessage> chatPostMessageList = getMessageCache(contact.mUserId);

        if(ListUtil.isEmpty(chatPostMessageList)) {
            return null;
        }

        for(ChatPostMessage msgInCache : chatPostMessageList) {
            if(msgInCache.equals(chatPostMessage)) {
                return msgInCache;
            }
        }

        return null;
    }


    @Nullable
    public ChatPostMessage findMessage(String sessionId, String msgId) {
        List<ChatPostMessage> chatPostMessageList = getMessageCache(sessionId);
        if(null == chatPostMessageList) {
            return null;
        }

        for(ChatPostMessage msgInCache : chatPostMessageList) {
            if(msgInCache.deliveryId.equals(msgId)) {
                return msgInCache;
            }
        }

        return null;

    }


}
