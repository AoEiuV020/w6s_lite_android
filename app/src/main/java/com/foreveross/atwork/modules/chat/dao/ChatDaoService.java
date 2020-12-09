package com.foreveross.atwork.modules.chat.dao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.foreverht.cache.MessageCache;
import com.foreverht.cache.MessageTagCache;
import com.foreverht.db.service.BaseDbService;
import com.foreverht.db.service.repository.EmergencyMessageUnconfirmedRepository;
import com.foreverht.db.service.repository.MessageAppRepository;
import com.foreverht.db.service.repository.MessageRepository;
import com.foreverht.db.service.repository.MessageTagsRepository;
import com.foreverht.db.service.repository.SessionFaultageRecordRepository;
import com.foreverht.db.service.repository.SessionRepository;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.threadGear.DbThreadPoolExecutor;
import com.foreveross.atwork.api.sdk.message.MessageAsyncNetService;
import com.foreveross.atwork.api.sdk.message.MessageSyncNetService;
import com.foreveross.atwork.api.sdk.message.model.QueryMessageTagResponse;
import com.foreveross.atwork.api.sdk.message.model.QueryMessageTagResult;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionFaultage;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.chat.SimpleMessageData;
import com.foreveross.atwork.infrastructure.model.newsSummary.NewsSummaryPostMessage;
import com.foreveross.atwork.infrastructure.model.user.UserHandleBasic;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.HideEventMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.dev.DevCommonShareInfo;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.MapUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.chat.BasicMsgHelper;
import com.foreveross.atwork.manager.listener.BaseQueryListener;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.service.upload.FileMediaUploadListener;
import com.foreveross.atwork.modules.chat.util.BurnModeHelper;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.MessagesRemoveHelper;
import com.google.gson.internal.LinkedTreeMap;
import com.w6s.module.MessageTags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatDaoService extends BaseDbService {


    private static ChatDaoService chatDaoService = new ChatDaoService();


    private ChatDaoService() {

    }

    public static ChatDaoService getInstance() {
        return chatDaoService;
    }

    /**
     * 刷新一个session,可能是unread或是最后一条消息变更
     *
     * @param session
     */
    @SuppressLint("StaticFieldLeak")
    public void sessionRefresh(final Session session) {
        new AsyncTask<String, Double, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                return SessionRepository.getInstance().updateSession(session);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 异步删除
     *
     * @param identifier
     */
    @SuppressLint("StaticFieldLeak")
    public void asyncRemoveSession(final String identifier, final boolean removeMessageTable, BaseQueryListener<Boolean> baseQueryListener) {
        new AsyncTask<String, Double, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                return syncRemoveSession(identifier, removeMessageTable);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                baseQueryListener.onSuccess(result);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    public void saveTags(List<MessageTags> tags) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                syncSaveTag(tags);
                return null;
            }
        }.executeOnExecutor(DbThreadPoolExecutor.getInstance());
    }

    private void syncSaveTag(List<MessageTags> tags) {
        MessageTagsRepository.getInstance().deleteAllTags();
        MessageTagCache.getInstance().removeCache();
        if (!ListUtil.isEmpty(tags)) {
            MessageTagsRepository.getInstance().batchInsertMessageTags(tags);
            MessageTagCache.getInstance().setMessageTagsCache(tags);
        }
    }

    public void getMessageTagsByTagId(final Context context, final String tagId, Session session, MessageAsyncNetService.OnMessageTagsListener listener) {
        new AsyncTask<Void, Void, MessageTags>() {
            @Override
            protected MessageTags doInBackground(Void... voids) {
                MessageTags tag = MessageTagCache.getInstance().getMessageTagCache(tagId);
                if (tag == null) {
                    tag = syncFindMessageTagInDB(tagId, session);
                }
                if (tag == null) {
                    tag = syncFindMessageTagRemote(context, tagId, session);
                }
                return tag;
            }

            @Override
            protected void onPostExecute(MessageTags messageTags) {
                if (listener == null) {
                    return;
                }
                ArrayList list = new ArrayList<MessageTags>();
                list.add(messageTags);
                listener.getMessageTagsSuccess(list);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    private MessageTags syncFindMessageTagInDB(String tagId, Session session) {
        App app = new App();
        app.mAppId = session.identifier;
        app.mDomainId = session.mDomainId;
        app.mOrgId = session.orgId;
        List<MessageTags> tags = MessageTagsRepository.getInstance().getMessageTags(app);
        return compareMessageTagsById(tagId, tags);
    }

    private MessageTags syncFindMessageTagRemote(Context context, String tagId, Session session) {
        HttpResult httpResult = MessageSyncNetService.queryMessageTags(context, session.orgId, session.identifier);
        if (!httpResult.isRequestSuccess()) {
            return null;
        }
        QueryMessageTagResponse queryMessageTagResponse = (QueryMessageTagResponse) httpResult.resultResponse;
        QueryMessageTagResult result = queryMessageTagResponse.getResult();
        List<MessageTags> tags = result.getRecords();
        syncSaveTag(tags);
        return compareMessageTagsById(tagId, tags);
    }

    private MessageTags compareMessageTagsById(String tagId, List<MessageTags> messageTags) {
        if (messageTags == null || TextUtils.isEmpty(tagId)) {
            return null;
        }
        for (MessageTags sub: messageTags) {
            if (tagId.equalsIgnoreCase(sub.getTagId())) {
                return sub;
            }
        }
        return null;
    }

    /**
     * 同步删除
     *
     * @param identifier
     * @return
     */
    public boolean syncRemoveSession(String identifier, boolean removeMessageTable) {
        MessageCache.getInstance().clearSessionMessage(identifier);
        return SessionRepository.getInstance().removeSession(identifier, removeMessageTable);
    }


    /**
     * 批量同步删除
     *
     * @param identifiers
     * @return
     */
    public boolean syncBatchRemoveSession(List<String> identifiers, boolean removeMessageTable) {
        MessageCache.getInstance().clearSessionsMessage(identifiers);
        return SessionRepository.getInstance().batchRemoveSessions(identifiers, removeMessageTable);
    }


    private List<ChatPostMessage> mOfflineBatchInsertMsgs = new ArrayList<>();

    private HashMap<String, List<ChatPostMessage>> mOfflineBatchInsertMsgMapInfo = new HashMap<>();

    public void clear() {
        mOfflineBatchInsertMsgs.clear();
        mOfflineBatchInsertMsgMapInfo.clear();
    }

    public boolean insertOrUpdateMessageSync(Context context, ChatPostMessage message, boolean online) {
        if(online) {
            return MessageRepository.getInstance().insertOrUpdateMessage(context, message);
        }


        if(DevCommonShareInfo.INSTANCE.isOpenOfflineMessageSessionStrategy(context)) {
            String sessionId = ChatMessageHelper.getChatUser(message).mUserId;
            List<ChatPostMessage> offlineBatchInsertMsgs = mOfflineBatchInsertMsgMapInfo.get(sessionId);
            if(null == offlineBatchInsertMsgs) {
                offlineBatchInsertMsgs = new ArrayList<>();
                mOfflineBatchInsertMsgMapInfo.put(sessionId, offlineBatchInsertMsgs);
            }

            offlineBatchInsertMsgs.add(message);

        } else {
            mOfflineBatchInsertMsgs.add(message);
        }

        return false;
    }

    public void checkOfflineMessagesBatchInsert(String sessionIdPullingMessages) {

        if(!StringUtils.isEmpty(sessionIdPullingMessages)) {
            checkOfflineMessagesBatchInsert(mOfflineBatchInsertMsgMapInfo.get(sessionIdPullingMessages));

            return;
        }


        //不是按会话插入的, 则全部插入处理
        if(!MapUtil.isEmpty(mOfflineBatchInsertMsgMapInfo)) {
            for(List<ChatPostMessage> msgList:  mOfflineBatchInsertMsgMapInfo.values()) {
                checkOfflineMessagesBatchInsert(msgList);
            }
        }

        checkOfflineMessagesBatchInsert(mOfflineBatchInsertMsgs);


    }

    private void checkOfflineMessagesBatchInsert(List<ChatPostMessage> offlineBatchInsertMsgs) {

        if (!ListUtil.isEmpty(offlineBatchInsertMsgs)) {
            List<ChatPostMessage> messagesInserted = new ArrayList<>(offlineBatchInsertMsgs);
            MessageRepository.getInstance().batchInsertMessages(messagesInserted);

            offlineBatchInsertMsgs.clear();
        }
    }


    /**
     * 新收到一个MESSAGE消息
     *
     * @param message
     */
    @SuppressLint("StaticFieldLeak")
    public void insertOrUpdateMessage(Context context, final ChatPostMessage message) {
        new AsyncTask<String, Double, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                return MessageRepository.getInstance().insertOrUpdateMessage(context, message);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 更新 message, 区别于{@link #insertOrUpdateMessage(Context, ChatPostMessage)},只做纯粹的更新操作
     *
     * @param message
     */
    public void updateMessage(final ChatPostMessage message) {
        new AsyncTask<String, Double, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                return MessageRepository.getInstance().updateMessage(message);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 更新服务号消息汇总数据
     * @param message
     */
    public void updateNesSummaryMessage(final ChatPostMessage message) {
        new AsyncTask<String, Double, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                return MessageAppRepository.getInstance().updateMessage(message);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 撤销消息
     */
    public void undoMessage(Context context, final UndoEventMessage undoEventMessage) {
        new AsyncTask<String, Double, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                MessageRepository.getInstance().undoMessage(context, undoEventMessage);
                return null;
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 撤销服务号订阅消息
     */
    public void undoNewsSummaryMessage(Context context, final UndoEventMessage undoEventMessage) {
        new AsyncTask<String, Double, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                //服务号消息汇总表的数据设置为撤销
                UserHandleBasic chatUser = BasicMsgHelper.getChatUser(undoEventMessage);
                MessageAppRepository.getInstance().undoMessage(context, chatUser.mUserId, undoEventMessage);
                return null;
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 隐藏消息
     */
    @SuppressLint("StaticFieldLeak")
    public void hideMessage(final HideEventMessage hideEventMessage) {
        new AsyncTask<String, Double, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                MessageRepository.getInstance().hideMessage(hideEventMessage);
                return null;
            }
        }.executeOnExecutor(mDbExecutor);
    }


    /**
     * 查询指定消息以后的所有消息, 并检查超时是否应该删除
     *
     * @param keyId
     * @param loadMessageListener
     */
    @SuppressLint("StaticFieldLeak")
    public void queryFixedMessageAndCheckExpired(final Context context, final String keyId, final String msgId, final LoadMessageAndCheckExpiredListener loadMessageListener) {
        new AsyncTask<String, Double, List<ChatPostMessage>>() {
            @Override
            protected List<ChatPostMessage> doInBackground(String... params) {
                List<ChatPostMessage> chatPostMessages = MessageRepository.getInstance().queryFixMessages(context, keyId, msgId);
                return chatPostMessages;
            }

            @Override
            protected void onPostExecute(List<ChatPostMessage> chatPostMessages) {
                List<ChatPostMessage> systemMessageTipList = BurnModeHelper.checkMsgExpiredAndRemove(chatPostMessages, true);
                correctFileChatMessageStatus(chatPostMessages);

                loadMessageListener.loadComplete(chatPostMessages, systemMessageTipList);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    private void correctFileChatMessageStatus(List<ChatPostMessage> chatPostMessages) {
        for(ChatPostMessage chatPostMessage: chatPostMessages) {
            if(chatPostMessage instanceof FileTransferChatMessage) {

                FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) chatPostMessage;

                if(FileStatus.DOWNLOADING == fileTransferChatMessage.fileStatus) {
                    if(!MediaCenterNetManager.isDownloading(fileTransferChatMessage.deliveryId)) {
                        fileTransferChatMessage.fileStatus = FileStatus.DOWNLOAD_FAIL;
                    }
                }


                if(FileStatus.SENDING == fileTransferChatMessage.fileStatus) {
                    if (MediaCenterNetManager.isUploading(fileTransferChatMessage.deliveryId)) {

                         if(ChatStatus.Not_Send == fileTransferChatMessage.chatStatus) {
                             fileTransferChatMessage.chatStatus = ChatStatus.Sending;
                         }

                        MediaCenterNetManager.MediaUploadListener mediaUploadListener = MediaCenterNetManager.getMediaUploadListenerById(fileTransferChatMessage.deliveryId, MediaCenterNetManager.UploadType.CHAT_FILE);
                         if(mediaUploadListener instanceof FileMediaUploadListener) {
                             FileMediaUploadListener fileMediaUploadListener = (FileMediaUploadListener) mediaUploadListener;
                             fileMediaUploadListener.setFileTransferChatMessage(fileTransferChatMessage);
                         }


                    } else {

                        fileTransferChatMessage.fileStatus = FileStatus.SEND_FAIL;
                    }
                }

            }
        }
    }

    /**
     * 查询最近的消息, 并检查超时是否应该删除
     *
     * @param keyId
     * @param firstMessageTimeStamp
     * @param limitCount
     * @param loadMessageListener
     */
    @SuppressLint("StaticFieldLeak")
    public void queryLastMessageInLimitAndCheckExpired(final Context context, final String keyId, final long firstMessageTimeStamp, final int limitCount, final LoadMessageAndCheckExpiredListener loadMessageListener) {
        new AsyncTask<String, Double, List<ChatPostMessage>>() {
            @Override
            protected List<ChatPostMessage> doInBackground(String... params) {
                List<ChatPostMessage> chatPostMessages = MessageRepository.getInstance().queryLastMessagesInLimitCount(context, keyId, firstMessageTimeStamp, limitCount);
                //查询所有断层情况
                List<SessionFaultage> sessionFaultages = SessionFaultageRecordRepository.querySessionFaultages(keyId);

                LogUtil.e("断层情况 -> " + sessionFaultages.toString());

                return chatPostMessages;
            }

            @Override
            protected void onPostExecute(List<ChatPostMessage> chatPostMessages) {
                List<ChatPostMessage> systemMessageTipList = BurnModeHelper.checkMsgExpiredAndRemove(chatPostMessages, true);
                correctFileChatMessageStatus(chatPostMessages);

                loadMessageListener.loadComplete(chatPostMessages, systemMessageTipList);
            }
        }.executeOnExecutor(mDbExecutor);

    }



    @SuppressLint("StaticFieldLeak")
    public void queryImageMessagesInLimitAndCheckExpired(Context context, String identifier, int limitCount, final LoadMessageAndCheckExpiredListener loadMessageListener) {
        new AsyncTask<String, Double, List<ChatPostMessage>>() {
            @Override
            protected List<ChatPostMessage> doInBackground(String... params) {
                List<ChatPostMessage> chatPostMessages = MessageRepository.getInstance().queryImageTypeMessagesInLimitCount(context, identifier, limitCount);


                return chatPostMessages;
            }

            @Override
            protected void onPostExecute(List<ChatPostMessage> chatPostMessages) {
                List<ChatPostMessage> systemMessageTipList = BurnModeHelper.checkMsgExpiredAndRemove(chatPostMessages, true);
                correctFileChatMessageStatus(chatPostMessages);

                loadMessageListener.loadComplete(chatPostMessages, systemMessageTipList);
            }
        }.executeOnExecutor(mDbExecutor);

    }

    /**
     * 根据指定数量, 检查未读的消息, 并检查超时是否应该删除
     */
    @SuppressLint("StaticFieldLeak")
    public void queryLastMessagesUnreadInLimitCountAndCheckExpired(final Context context, final String keyId, final long firstMessageTimeStamp, final int limitCount, final boolean includeLocal, final LoadMessageAndCheckExpiredListener loadMessageListener) {
        new AsyncTask<String, Double, List<ChatPostMessage>>() {
            @Override
            protected List<ChatPostMessage> doInBackground(String... params) {
                List<ChatPostMessage> chatPostMessages = MessageRepository.getInstance().queryLastMessagesUnreadInLimitCount(context, keyId, firstMessageTimeStamp, limitCount, includeLocal);
                return chatPostMessages;
            }

            @Override
            protected void onPostExecute(List<ChatPostMessage> chatPostMessages) {
                List<ChatPostMessage> systemMessageTipList = BurnModeHelper.checkMsgExpiredAndRemove(chatPostMessages, true);
                correctFileChatMessageStatus(chatPostMessages);

                loadMessageListener.loadComplete(chatPostMessages, systemMessageTipList);
            }
        }.executeOnExecutor(mDbExecutor);

    }

    @SuppressLint("StaticFieldLeak")
    public void queryUnreadAtAllMessageList(Context context, String sessionId, List<String> unreadMsgId, final LoadMessageListener loadMessageListener) {
        new AsyncTask<String, Double, List<ChatPostMessage>>() {
            @Override
            protected List<ChatPostMessage> doInBackground(String... params) {
                List<ChatPostMessage> chatPostMessages = MessageRepository.getInstance().queryUnreadAtAllMessageList(context, sessionId, unreadMsgId);
                return chatPostMessages;
            }

            @Override
            protected void onPostExecute(List<ChatPostMessage> chatPostMessages) {
                loadMessageListener.loadComplete(chatPostMessages);
            }
        }.executeOnExecutor(mDbExecutor);

    }


    @SuppressLint("StaticFieldLeak")
    public void queryMessagesBetween2PointsAndCheckExpired(Context context, String sessionId, List<String> earlyMsgIdList, String farMsgId, long farMsgDeliveryTime, final LoadMessageAndCheckExpiredListener loadMessageListener) {
        new AsyncTask<String, Double, List<ChatPostMessage>>() {
            @Override
            protected List<ChatPostMessage> doInBackground(String... params) {
                List<ChatPostMessage> chatPostMessages = MessageRepository.getInstance().queryMessagesBetween2Points(context, sessionId, earlyMsgIdList, farMsgId, farMsgDeliveryTime);
                return chatPostMessages;
            }

            @Override
            protected void onPostExecute(List<ChatPostMessage> chatPostMessages) {
                List<ChatPostMessage> systemMessageTipList = BurnModeHelper.checkMsgExpiredAndRemove(chatPostMessages, true);
                correctFileChatMessageStatus(chatPostMessages);

                loadMessageListener.loadComplete(chatPostMessages, systemMessageTipList);
            }
        }.executeOnExecutor(mDbExecutor);

    }


    @SuppressLint("StaticFieldLeak")
    public void queryMessagesBetween2PointsAndCheckExpired(Context context, String sessionId, String earlyMsgTime, String farMsgId, long farMsgDeliveryTime, final LoadMessageAndCheckExpiredListener loadMessageListener) {
        new AsyncTask<String, Double, List<ChatPostMessage>>() {
            @Override
            protected List<ChatPostMessage> doInBackground(String... params) {
                List<ChatPostMessage> chatPostMessages = MessageRepository.getInstance().queryMessagesBetween2Points(context, sessionId, earlyMsgTime, farMsgId, farMsgDeliveryTime);
                return chatPostMessages;
            }

            @Override
            protected void onPostExecute(List<ChatPostMessage> chatPostMessages) {
                List<ChatPostMessage> systemMessageTipList = BurnModeHelper.checkMsgExpiredAndRemove(chatPostMessages, true);
                correctFileChatMessageStatus(chatPostMessages);

                loadMessageListener.loadComplete(chatPostMessages, systemMessageTipList);
            }
        }.executeOnExecutor(mDbExecutor);

    }


    /**
     * 查询最近的消息
     *
     * @param keyId
     * @param loadMessageListener
     */
    @SuppressLint("StaticFieldLeak")
    public void queryLastMessageUnread(final Context context, final String keyId, final LoadMessageListener loadMessageListener) {
        new AsyncTask<String, Double, List<ChatPostMessage>>() {
            @Override
            protected List<ChatPostMessage> doInBackground(String... params) {
                List<ChatPostMessage> chatPostMessages = MessageRepository.getInstance().queryLastMessageUnread(context, keyId);
                return chatPostMessages;
            }

            @Override
            protected void onPostExecute(List<ChatPostMessage> chatPostMessages) {
                loadMessageListener.loadComplete(chatPostMessages);
            }
        }.executeOnExecutor(mDbExecutor);
    }


    /**
     * 判断消息是否存在
     * @param identifier
     * @param msgId
     * @param listener
     */
    @SuppressLint("StaticFieldLeak")
    public void existMessage(String identifier, String msgId, BaseQueryListener<Boolean> listener) {
        new AsyncTask<String, Double, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                return MessageRepository.getInstance().existMessage(identifier, msgId);
            }

            @Override
            protected void onPostExecute(Boolean existMessage) {
                listener.onSuccess(existMessage);
            }
        }.executeOnExecutor(mDbExecutor);

    }

    /**
     * 查询最新的一条消息
     *
     * @param keyId
     * @param loadMessageListener
     */
    @SuppressLint("StaticFieldLeak")
    public void queryLatestMessage(final Context context, final String keyId, final LoadMessageListener loadMessageListener) {
        new AsyncTask<String, Double, List<ChatPostMessage>>() {
            @Override
            protected List<ChatPostMessage> doInBackground(String... params) {
                List<ChatPostMessage> chatPostMessages = MessageRepository.getInstance().queryLatestMessage(context, keyId);
                return chatPostMessages;
            }

            @Override
            protected void onPostExecute(List<ChatPostMessage> chatPostMessages) {
                loadMessageListener.loadComplete(chatPostMessages);
            }
        }.executeOnExecutor(mDbExecutor);
    }




    @SuppressLint("StaticFieldLeak")
    public void batchInsertMessages(final List<ChatPostMessage> messages) {
        new AsyncTask<String, Double, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    MessageRepository.getInstance().batchInsertMessages(messages);
                } catch (Exception e) {
                    e.printStackTrace();

                    return false;
                }

                return true;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void batchInsertMessages(final List<ChatPostMessage> messages,boolean isService) {
        new AsyncTask<String, Double, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    List<ChatPostMessage> messagesList = new ArrayList<>();
                    if(isService){
                        for(ChatPostMessage chatPostMessage : messages){
                            if(chatPostMessage.mTargetScope != ChatPostMessage.TARGET_SCOPE_NEWS_SUMMARY){
                                messagesList.add(chatPostMessage);
                            }
                        }
                    }
                    MessageRepository.getInstance().batchInsertMessages(messagesList);
                } catch (Exception e) {
                    e.printStackTrace();

                    return false;
                }

                return true;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void batchInsertMessages(final List<ChatPostMessage> messages, final BatchAddOrRemoveListener batchAddOrRemoveListener) {
        new AsyncTask<String, Double, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    MessageRepository.getInstance().batchInsertMessages(messages);
                } catch (Exception e) {
                    e.printStackTrace();

                    return false;
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    batchAddOrRemoveListener.addOrRemoveSuccess();
                } else {
                    batchAddOrRemoveListener.addOrRemoveFail();
                }
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 新增或更新服务号汇总表数据
     * @param messages
     */
    @SuppressLint("StaticFieldLeak")
    public void batchInsertAppMessages(final List<ChatPostMessage> messages,Session session) {
        new AsyncTask<String, Double, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    List<NewsSummaryPostMessage> messageList = new ArrayList<>();
                    for(ChatPostMessage chatPostMessage : messages){
                        NewsSummaryPostMessage newsSummaryRVData = new NewsSummaryPostMessage();
                        newsSummaryRVData.setChatPostMessage(chatPostMessage);
                        newsSummaryRVData.setChatId(session.identifier);
                        newsSummaryRVData.setOrgId(session.orgId);
                        if(!LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext).equals(chatPostMessage.from) &&
                                (chatPostMessage.mTargetScope == ChatPostMessage.TARGET_SCOPE_ALL || chatPostMessage.mTargetScope == ChatPostMessage.TARGET_SCOPE_NEWS_SUMMARY)) {
                            messageList.add(newsSummaryRVData);
                        }
                    }
                    MessageAppRepository.getInstance().batchInsertTotleAppMessages(messageList);
                } catch (Exception e) {
                    e.printStackTrace();

                    return false;
                }

                return true;
            }
        }.execute();
    }

    /**
     * 批量从数据库中删除消息
     *
     * @param messages
     * @param batchRemoveListener
     */
    @SuppressLint("StaticFieldLeak")
    public void batchRemoveMessages(final List<ChatPostMessage> messages, @Nullable final BatchAddOrRemoveListener batchRemoveListener) {
        new AsyncTask<String, Double, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                boolean result = MessageRepository.getInstance().batchRemoveMessage(messages);
                if (result) {
                    for (ChatPostMessage chatPostMessage : messages) {

                        String sessionId = ChatMessageHelper.getChatUser(chatPostMessage).mUserId;
                        if (ChatSessionDataWrap.getInstance().isChatInFileSteaming(sessionId, chatPostMessage.deliveryId)) {
                            MediaCenterNetManager.brokenDownloadingOrUploading(chatPostMessage.deliveryId);
                        }

                        MessagesRemoveHelper.removeFile(BaseApplicationLike.baseContext, chatPostMessage);
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (null != batchRemoveListener) {
                    if (result) {
                        batchRemoveListener.addOrRemoveSuccess();
                    } else {
                        batchRemoveListener.addOrRemoveFail();
                    }
                }
            }
        }.executeOnExecutor(mDbExecutor);
    }


    /**
     * 搜索聊天记录
     *
     * @param searchKey
     * @param searchValue
     * @param identifier
     * @param searchMessagesListener
     */
    @SuppressLint("StaticFieldLeak")
    public void searchMessages(final Context context, final String searchKey, final String searchValue, final String identifier, final SearchMessagesListener searchMessagesListener) {

        new AsyncTask<Void, Void, List<ChatPostMessage>>() {
            @Override
            protected List<ChatPostMessage> doInBackground(Void... params) {
                return MessageRepository.getInstance().searchMessages(context, identifier, searchValue);
            }

            @Override
            protected void onPostExecute(List<ChatPostMessage> chatPostMessages) {
                searchMessagesListener.searchMessagesSuccess(searchKey, chatPostMessages);
            }
        }.executeOnExecutor(mDbExecutor);

    }

    public void searchMessagesByMessageType(final Context context, final String searchKey, final String searchValue, final String identifier, final String messageType, final SearchMessagesListener searchMessagesListener) {
        new AsyncTask<Void, Void, List<ChatPostMessage>>() {
            @Override
            protected List<ChatPostMessage> doInBackground(Void... params) {
                return MessageRepository.getInstance().searchMessagesByMessageType(context, identifier, searchValue, messageType);
            }

            @Override
            protected void onPostExecute(List<ChatPostMessage> chatPostMessages) {
                searchMessagesListener.searchMessagesSuccess(searchKey, chatPostMessages);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 搜索会话媒体(图片和视频)消息
     * @param context
     * @param sessionId
     * @param searchMessagesListener
     */
    public void searchMediaMessages(final Context context, final String sessionId, final SearchMessagesListener searchMessagesListener) {
        new AsyncTask<Void, Void, List<ChatPostMessage>>() {
            @Override
            protected List<ChatPostMessage> doInBackground(Void... params) {
                return MessageRepository.getInstance().searchMediaMessages(context, sessionId);
            }

            @Override
            protected void onPostExecute(List<ChatPostMessage> chatPostMessages) {
                if (searchMessagesListener == null) {
                    return;
                }
                searchMessagesListener.searchMessagesSuccess("", chatPostMessages);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    public void mapMessageByDay(final Context context, final String sessionId, final OnSearchSimpleMessagesListener searchSimpleMessagesListener) {
        new AsyncTask<Void, Void, Map<String, SimpleMessageData>>() {
            @Override
            protected Map<String, SimpleMessageData> doInBackground(Void... params) {
                List<SimpleMessageData> datas = MessageRepository.getInstance().searchAllMessageIds(context, sessionId);
                Map<String, SimpleMessageData> map = new LinkedTreeMap<>();
                for (SimpleMessageData data : datas) {
                    String monthOfDay = TimeUtil.getStringForMillis(data.getMessageTime(), TimeUtil.getTimeFormat1(BaseApplicationLike.baseContext));
                    if (map.containsKey(monthOfDay)) {
                        continue;
                    }
                    map.put(monthOfDay, data);
                }
                return map;
            }

            @Override
            protected void onPostExecute(Map<String, SimpleMessageData> dataMap) {
                if (searchSimpleMessagesListener == null) {
                    return;
                }
                searchSimpleMessagesListener.searchSimpleMessagesSuccess("", dataMap);
            }
        }.executeOnExecutor(mDbExecutor);
    }


    @SuppressLint("StaticFieldLeak")
    public void deleteEmergencyMessages(List<String> msgIds) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {

                return EmergencyMessageUnconfirmedRepository.getInstance().deleteEmergencyMessages(msgIds);
            }
        }.executeOnExecutor(mDbExecutor);
    }




    public interface LoadMessageListener {
        void loadComplete(List<ChatPostMessage> chatPostMessageList);
    }

    public interface LoadMessageAndCheckExpiredListener {
        void loadComplete(List<ChatPostMessage> chatPostMessageList, List<ChatPostMessage> systemMessageTipList);
    }

    public interface BatchAddOrRemoveListener {

        void addOrRemoveSuccess();


        void addOrRemoveFail();
    }

    public interface SearchMessagesListener {

        void searchMessagesSuccess(String searchKey, List<ChatPostMessage> messages);

    }

    public interface OnSearchSimpleMessagesListener {

        void searchSimpleMessagesSuccess(String searchKey, Map<String, SimpleMessageData> simpleMessageDatas);

    }
}
