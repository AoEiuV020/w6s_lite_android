package com.foreveross.atwork.modules.chat.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.cache.MessageCache;
import com.foreverht.db.service.repository.MessageRepository;
import com.foreverht.db.service.repository.SessionRepository;
import com.foreverht.db.service.repository.UnreadMessageRepository;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.threadGear.DbThreadPoolExecutor;
import com.foreverht.threadGear.HighPriorityCachedTreadPoolExecutor;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingParticipant;
import com.foreveross.atwork.api.sdk.message.MessageAsyncNetService;
import com.foreveross.atwork.api.sdk.message.model.MessagesResult;
import com.foreveross.atwork.api.sdk.message.model.OfflineMessageResponseJson;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.model.UploadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.api.sdk.users.UserSyncNetService;
import com.foreveross.atwork.api.sdk.users.responseJson.SearchUserResponseJson;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleBasic;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ack.AckPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.OrgPositionInfo;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.FriendNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.OrgNotifyMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.MessageCovertUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.chat.BasicMsgHelper;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.listener.BaseQueryListener;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.data.ReadMessageDataWrap;
import com.foreveross.atwork.modules.chat.data.SendMessageDataWrap;
import com.foreveross.atwork.modules.chat.inter.OnMessageWrapListener;
import com.foreveross.atwork.modules.chat.service.upload.FileMediaUploadListener;
import com.foreveross.atwork.modules.chat.service.upload.ImageMediaUploadListener;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.modules.chat.util.FileDataUtil;
import com.foreveross.atwork.services.ImSocketService;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.GifChatHelper;
import com.foreveross.atwork.utils.ImageChatHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import kotlin.collections.CollectionsKt;
import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by dasunsy on 16/6/5.
 */
public class ChatService {

    @Nullable
    public static ChatPostMessage queryMsgSync(ChatPostMessage chatPostMessage) {
        ChatPostMessage msg = MessageCache.getInstance().queryMessage(chatPostMessage);
        if (null == msg) {
            UserHandleBasic contact = BasicMsgHelper.getChatUser(chatPostMessage);

            msg = MessageRepository.getInstance().queryMessage(BaseApplicationLike.baseContext, contact.mUserId, chatPostMessage.deliveryId);
        }

        return msg;
    }

    /**
     * 在后台发送一个消息
     */
    public static void sendMessageOnBackground(final Session session, final ChatPostMessage message) {
        wrapParticipant(BaseApplicationLike.baseContext, message, session, postTypeMessage -> doSendMessageOnBackground(session, (ChatPostMessage) postTypeMessage));
    }


    private static void doSendMessageOnBackground(Session session, ChatPostMessage message) {

        //更新消息缓存 先把之前的保存下来
        List<ChatPostMessage> messageList = MessageCache.getInstance().getMessageCache(session.identifier);

        if (messageList != null) {
            messageList.add(message);
        } else {
            messageList = new ArrayList<>();
            messageList.add(message);
            MessageCache.getInstance().updateMessageList(session.identifier, messageList);
        }

        updateSessionAndSendStatus(session, message);


        if (message instanceof FileTransferChatMessage) {
            FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) message;
            //need handle upload file
            if (StringUtils.isEmpty(fileTransferChatMessage.mediaId)) {

                //file not exist
                if (!FileUtil.isExist(fileTransferChatMessage.filePath)) {
                    fileTransferChatMessage.chatStatus = ChatStatus.Not_Send;
                    fileTransferChatMessage.setFileStatus(FileStatus.NOT_SENT);

                    ChatMessageHelper.notifyMessageReceived(message);
                    //更新数据库
                    ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, message);


                    return;
                }


                //start upload file
                uploadFileMsgAndSend(session, fileTransferChatMessage);
                return;
            }
        }


        if(message instanceof ImageChatMessage) {
            ImageChatMessage imageChatMessage = (ImageChatMessage) message;

            //need handle upload file
            if(StringUtils.isEmpty(imageChatMessage.mediaId)) {

                //file not exist
                if (!FileUtil.isExist(imageChatMessage.filePath)) {
                    imageChatMessage.chatStatus = ChatStatus.Not_Send;
                    imageChatMessage.setFileStatus(FileStatus.NOT_SENT);

                    ChatMessageHelper.notifyMessageReceived(message);
                    //更新数据库
                    ChatDaoService.getInstance().insertOrUpdateMessage(AtworkApplicationLike.baseContext, message);


                    return;
                }

                compressImgAndUploadSend(session, imageChatMessage);

                return;
            }
        }

        SendMessageDataWrap.getInstance().addChatSendingMessage(message);
        doSendDirectMessage(session, message);
    }


    private static void uploadFileMsgAndSend(Session session, FileTransferChatMessage fileTransferChatMessage) {
        ChatMessageHelper.notifyMessageReceived(fileTransferChatMessage);

        String fileType = MediaCenterNetManager.COMMON_FILE;

//        mediaCenterNetManager.uploadFile(BaseApplicationLike.baseContext, fileType, fileTransferChatMessage.deliveryId, fileTransferChatMessage.filePath, true, (errorCode, errorMsg)
//                -> ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Media, errorCode, errorMsg));

        UploadFileParamsMaker uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                .setType(fileType)
                .setMsgId(fileTransferChatMessage.deliveryId)
                .setFilePath(fileTransferChatMessage.filePath)
                .setNeedCheckSum(true);

        MediaCenterNetManager.uploadFile(BaseApplicationLike.baseContext, uploadFileParamsMaker);


        MediaCenterNetManager.MediaUploadListener mediaUploadListener
                = MediaCenterNetManager.getMediaUploadListenerById(fileTransferChatMessage.deliveryId, MediaCenterNetManager.UploadType.CHAT_FILE);

        if (null == mediaUploadListener) {
            mediaUploadListener = new FileMediaUploadListener(session, fileTransferChatMessage);
            MediaCenterNetManager.addMediaUploadListener(mediaUploadListener);

        }
    }


    private static void compressImgAndUploadSend(Session session, ImageChatMessage imageChatMessage) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                if(GifChatHelper.isGif(imageChatMessage.filePath)) {
                    handleGif(imageChatMessage);
                } else {
                    handleCompressImgAdjustOrientation(imageChatMessage);
                }


                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {


                UploadFileParamsMaker uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                        .setType(MediaCenterNetManager.IMAGE_FILE)
                        .setMsgId(imageChatMessage.deliveryId)
                        .setFilePath(ImageShowHelper.getOriginalPath(AtworkApplicationLike.baseContext, imageChatMessage.deliveryId))
                        .setNeedCheckSum(true);

                MediaCenterNetManager.uploadFile(AtworkApplicationLike.baseContext, uploadFileParamsMaker);


                MediaCenterNetManager.MediaUploadListener mediaUploadListener
                        = MediaCenterNetManager.getMediaUploadListenerById(imageChatMessage.deliveryId, MediaCenterNetManager.UploadType.CHAT_FILE);

                if (null == mediaUploadListener) {
                    mediaUploadListener = new ImageMediaUploadListener(session, imageChatMessage);
                    MediaCenterNetManager.addMediaUploadListener(mediaUploadListener);

                }
            }
        }.executeOnExecutor(HighPriorityCachedTreadPoolExecutor.getInstance());
    }



    @NonNull
    private  static void handleCompressImgAdjustOrientation(ImageChatMessage imageMessage) {
        //先旋转缩略图
        Bitmap thumbBitmap = ImageShowHelper.getRotateImageBitmap(imageMessage.filePath, true);
        byte[] thumbByte = BitmapUtil.compressImageForQuality(thumbBitmap, AtworkConfig.CHAT_THUMB_SIZE);
        imageMessage.setThumbnails(thumbByte);

//        imageMessage = sendImageMessage(thumbByte, false);
        //先填充原图的高宽进行优先的 UI 显示
//                                ImageChatHelper.setImageMessageInfo(imageMessage, imageItem.filePath);

        ChatMessageHelper.notifyMessageReceived(imageMessage);

        //旋转原图
        Bitmap bitmap = ImageShowHelper.getRotateImageBitmap(imageMessage.filePath, false);
        byte[] content = BitmapUtil.Bitmap2Bytes(bitmap);
        byte[] originalContent = ImageShowHelper.compressImageForOriginal(content);
        String originalPath = ImageShowHelper.saveOriginalImage(AtworkApplicationLike.baseContext, imageMessage.deliveryId, originalContent);
        ImageShowHelper.saveThumbnailImage(AtworkApplicationLike.baseContext, imageMessage.deliveryId, thumbByte);

        ImageChatHelper.setImageMessageInfo(imageMessage, originalPath);

        bitmap.recycle();
        bitmap = null;
        thumbBitmap.recycle();
        thumbBitmap = null;

    }


    @NonNull
    private static ImageChatMessage handleGif(ImageChatMessage imageMessage) {
        byte[] bitmapByte = null;
        byte[] thumbnails = null;

        try {
            GifDrawable gif = new GifDrawable(imageMessage.filePath);
            //the first frame is the cover
            Bitmap gifBitmap = gif.seekToFrameAndGet(0);
            thumbnails = BitmapUtil.compressImageForQuality(gifBitmap, AtworkConfig.CHAT_THUMB_SIZE);

            gif.recycle();
            gifBitmap.recycle();
            gif = null;
            gifBitmap = null;

        } catch (IOException e) {
            e.printStackTrace();
        }

        imageMessage.setThumbnails(thumbnails);

        ChatMessageHelper.notifyMessageReceived(imageMessage);

        bitmapByte = FileUtil.readFile(imageMessage.filePath);
        String originalPath = ImageShowHelper.saveOriginalImage(AtworkApplicationLike.baseContext, imageMessage.deliveryId, bitmapByte);
        ImageShowHelper.saveThumbnailImage(AtworkApplicationLike.baseContext, imageMessage.deliveryId, thumbnails);

        ImageChatHelper.setImageMessageInfo(imageMessage, originalPath);
        return imageMessage;
    }


    private static void doSendDirectMessage(Session session, ChatPostMessage message) {
        if (AtworkConfig.SPECIAL_ENABLE_DISCUSSION_FILE_WHEN_CLOSE_DROPBOX && message instanceof FileTransferChatMessage) {
            if (SessionType.Discussion.equals(session.type)) {
                DropboxManager.getInstance().saveToDropboxFromMessage(BaseApplicationLike.baseContext, session, (FileTransferChatMessage) message, false);
            }
            FileDataUtil.updateRecentFileDB((FileTransferChatMessage) message);
        }


        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (message == null) {
                return;
            }
            if (ChatStatus.Sending.equals(message.chatStatus)) {

                LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ImSocketService.ACTION_IM_RECONNECT));

            } else {
                message.chatStatus = ChatStatus.Sended;

            }

            ChatDaoService.getInstance().updateMessage(message);

        }, AtworkConfig.SOCKET_TIME_OUT);

        ChatMessageHelper.sendMessage(message);

        ChatMessageHelper.notifyMessageReceived(message);


        //更新数据库
        ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, message);
    }


    public static void updateSessionAndSendStatus(final Session session, final ChatPostMessage message) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                handleUpdateSessionAndSendStatusInSync(session, message);

                return null;
            }

        }.executeOnExecutor(DbThreadPoolExecutor.getInstance());
    }

    public static void handleUpdateSessionAndSendStatusInSync(Session session, ChatPostMessage message) {
        //更新会话
        ChatSessionDataWrap.getInstance().updateSession(session, message, false, true, true);
        ChatSessionDataWrap.getInstance().updateSendStatus(message.deliveryId, session);
    }


    /**
     * 单对多的回执(包括单聊, 群聊, 系统通知等场景)
     */
    public static void sendP2NReceiptsSync(Context context, Session session, List<ChatPostMessage> messageList) {
        if (ListUtil.isEmpty(messageList)) {
            return;
        }

        List<ChatPostMessage> chatPostMessages = new ArrayList<>();

        LoginUserBasic loginUserBasic = LoginUserInfo.getInstance().getLoginUserBasic(context);

        //回执 key 和对应的未读消息id列表
        Map<String, List<String>> unreadMessageMap = new HashMap<>();
        //回执 key 和对应的未读消息chatpostMsg列表
        Map<String, List<ChatPostMessage>> unReadChatPostMsgMap = new HashMap<>();





        for (ChatPostMessage chatPostMessage : messageList) {
            if(null == chatPostMessage) {
                continue;
            }

            if (chatPostMessage.isUndo() || loginUserBasic.mUserId.equals(chatPostMessage.from)) {
                continue;
            }

            if (!ReadStatus.AbsolutelyRead.equals(chatPostMessage.read)) {
                chatPostMessage.read = ReadStatus.LocalRead;
                chatPostMessages.add(chatPostMessage);

                //把每条回执发改成区分 key 来组装 ack
                String receiptKey = getReceiptKey(chatPostMessage);

                if (!unreadMessageMap.containsKey(receiptKey)) {
                    unreadMessageMap.put(receiptKey, new ArrayList<>());
                }
                unreadMessageMap.get(receiptKey).add(chatPostMessage.getMsgReadDeliveryId());

                if (!unReadChatPostMsgMap.containsKey(receiptKey)) {
                    unReadChatPostMsgMap.put(receiptKey, new ArrayList<>());
                }
                unReadChatPostMsgMap.get(receiptKey).add(chatPostMessage);
            }
        }



        for (Map.Entry<String, List<String>> entry : unreadMessageMap.entrySet()) {
            List<String> memberUnreadMessages = entry.getValue();
            String receiptKey = entry.getKey();

            if (!memberUnreadMessages.isEmpty()) {

                ParticipantType toType;
                if (FriendNotifyMessage.FROM.equals(receiptKey) || OrgNotifyMessage.FROM.equals(receiptKey)) {
                    toType = SessionType.Notice.getToType();

                } else {
                    toType = session.type.getToType();
                }

                int ackForward = 0;
                String sessionIdentifier;

                if (SessionType.User.equals(session.type) && ParticipantType.User.equals(session.type.getToType())) {
                    ackForward = 1;
                    sessionIdentifier = loginUserBasic.mUserId;

                } else {
                    sessionIdentifier = session.identifier;

                }

                List<Long> deliveryTimeList =  CollectionsKt.map(unReadChatPostMsgMap.get(receiptKey), message -> message.deliveryTime);

                long beginTime = CollectionsKt.min(deliveryTimeList);
                long endTime = CollectionsKt.max(deliveryTimeList);

                String ackTo = receiptKey;
                // 20200415调整
                // 假如是群组, 找未读里最新的那条消息作为群组的to
                if (ParticipantType.Discussion == toType
                        && !DiscussionManager.getInstance().touchDiscussionReadFeatureThresholdSync(context, receiptKey)) {

                    ackTo = CollectionsKt.maxBy(unReadChatPostMsgMap.get(receiptKey), chatPostMessage -> chatPostMessage.deliveryTime).from;
                    ackForward = 1;
                    toType = ParticipantType.User;
                }

                AckPostMessage.Builder ackPostMessageBuilder = AckPostMessage.newBuilder()
                        .setTo(ackTo)
                        .setToType(toType)
                        .setToDomainId(session.mDomainId)
                        .setAckForward(ackForward)
                        .setSessionIdentifier(sessionIdentifier)
                        .setType(AckPostMessage.AckType.READ);

                if(ParticipantType.App == toType
                        || ParticipantType.Discussion == toType
                        || ParticipantType.User == toType) {

                    ackPostMessageBuilder.setConversationId(session.identifier)
                            .setConversationDomain(session.mDomainId)
                            .setConversationType(session.type.getToType())
                            .setBeginTime(beginTime)
                            .setEndTime(endTime);

                } else {
                    ackPostMessageBuilder.setAckIds(memberUnreadMessages);
                }

                AckPostMessage ackPostMessage = ackPostMessageBuilder.build();
                ChatMessageHelper.sendMessage(ackPostMessage);
                ReadMessageDataWrap.getInstance().putReadSendingMessage(ackPostMessage.deliveryId, unReadChatPostMsgMap.get(receiptKey));
            }
        }

        ChatDaoService.getInstance().batchInsertMessages(chatPostMessages,SessionType.Service.equals(session.type));

       /* if(SessionType.Service.equals(session.type)) {
            ChatDaoService.getInstance().batchInsertAppMessages(chatPostMessages,session);
        }*/

    }

    public static void sendReceiptInSync(Context context, Session session, List<ChatPostMessage> messageList) {
        //发送的回执
        if (SessionType.User.equals(session.type) || session.type.isApp() || SessionType.Notice.equals(session.type) || SessionType.Discussion.equals(session.type)) {
            ChatService.sendP2NReceiptsSync(context, session, messageList);
        }

    }

    /**
     * 清空未读消息
     */
    @SuppressLint("StaticFieldLeak")
    public static void clearChatMsgReceipts(Context context, Session session, List<String> unreadIdList) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                if (null != session) {
                    List<ChatPostMessage> unreadMsgList = MessageRepository.getInstance().queryUnreadMsgList(context, session.identifier, unreadIdList);
                    ChatService.sendReceiptInSync(context, session, unreadMsgList);
                }
                return null;
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    /**
     * 清空未读消息
     */
    @SuppressLint("StaticFieldLeak")
    public static void clearSessionsFoldReceipts(Context context, Session session) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                if (null != session) {
                    if(session.isSessionsFold()) {
                        sendSessionFoldReceipts(context, session);
                    }
                }
                return null;
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 发送 session 的已读未读回执(好友通知, 组织通知, 轻应用网页等)
     */
    @SuppressLint("StaticFieldLeak")
    public static void sendSessionReceipts(Context context, Session session) {
        AsyncTaskThreadPool.getInstance().execute(() -> {
            Set<String> unreadSet = UnreadMessageRepository.getInstance().queryUnreadSet(session.identifier);
            sendSessionReceiptsSync(context, session, unreadSet);
        });
    }

    public static void sendSessionReceipts(Context context, Session session, Set<String> unreadMsgIdSet) {

//        AckPostMessage ackPostMessage = AckPostMessage.createReadAckPostMessage(unreadMsgIdList, loginUserBasic.mUserId, ParticipantType.User, loginUserBasic.mDomainId, session.identifier, session.type.getToType(), session.mDomainId, 0, session.identifier);

        AsyncTaskThreadPool.getInstance().execute(() -> {
            sendSessionReceiptsSync(context, session, unreadMsgIdSet);
        });

    }

    public static void sendSessionReceiptsSync(Context context, Session session, Set<String> unreadMsgIdSet) {

        if(null == unreadMsgIdSet || unreadMsgIdSet.isEmpty()) {
            return;
        }

        long beginTime = UnreadMessageRepository.getInstance().queryMinTime(session.identifier, unreadMsgIdSet);
        long endTIme = UnreadMessageRepository.getInstance().queryMaxTime(session.identifier, unreadMsgIdSet);

        AckPostMessage.Builder ackPostMessageBuilder = AckPostMessage.newBuilder()
                .setTo(session.identifier)
                .setToType(session.type.getToType())
                .setToDomainId(session.mDomainId)
                .setAckForward(0)
                .setSessionIdentifier(session.identifier)
                .setType(AckPostMessage.AckType.READ);

        if((session.isAppType() || session.isUserType() || session.isDiscussionType())
            && isUnreadTimeAreaLegal(beginTime, endTIme)) {

            ackPostMessageBuilder.setConversationId(session.identifier)
                    .setConversationDomain(session.mDomainId)
                    .setConversationType(session.type.getToType())
                    .setBeginTime(beginTime)
                    .setEndTime(endTIme);


        } else {
            ackPostMessageBuilder.setAckIds(new ArrayList<>(unreadMsgIdSet));
        }


        ChatMessageHelper.sendMessage(ackPostMessageBuilder.build());
    }


    public static void sendSessionFoldReceipts(Context context, Session session) {

        String sessionIdentifier = ConversionConfigSettingParticipant.getClientIdFromSessionId(session.identifier);


        AckPostMessage.Builder ackPostMessageBuilder = AckPostMessage.newBuilder()
                .setTo(sessionIdentifier)
                .setToType(ParticipantType.System)
                .setToDomainId(session.mDomainId)
                .setAckForward(1)
                .setSessionIdentifier(sessionIdentifier)
                .setType(AckPostMessage.AckType.READ)
                .setConversationId(sessionIdentifier)
                .setConversationDomain(session.mDomainId)
                .setConversationType(ParticipantType.System);


        ChatMessageHelper.sendMessage(ackPostMessageBuilder.build());
    }


    private static boolean isUnreadTimeAreaLegal(long beginTime, long endTIme) {
        return 0 < beginTime && 0 < endTIme;
    }


    public static void sendNotifyMessage(NotifyPostMessage notifyPostMessage) {
        SendMessageDataWrap.getInstance().addNotifySendingMessage(notifyPostMessage);
        ChatMessageHelper.sendMessage(notifyPostMessage);

    }

    /**
     * 检查本地需要发送的 ack remove 回执
     */
    public static void checkSendRemovedReceipts(Context context) {
        List<AckPostMessage> ackList = PersonalShareInfo.getInstance().getAcksNeedCheck(context);
        for (AckPostMessage ack : ackList) {
            SendMessageDataWrap.getInstance().addAckSendingMessage(ack);
            ChatMessageHelper.sendMessage(ack);
        }
    }

    /**
     * 发送ack_forward 为 0 的 remove 回执, 告诉后台确认了本地删除操作完成
     */
    public static void sendRemovedReceiptsNotForwards(Context context, AckPostMessage ackPostMessage) {
        ackPostMessage.ackForward = 0;
        String from = ackPostMessage.from;
        String to = ackPostMessage.to;

        //switch
        ackPostMessage.to = from;
        ackPostMessage.from = to;
        ackPostMessage.deliveryId = UUID.randomUUID().toString();
        ackPostMessage.deliveryTime = TimeUtil.getCurrentTimeInMillis();

        PersonalShareInfo.getInstance().setAckNeedCheck(context, ackPostMessage);

        SendMessageDataWrap.getInstance().addAckSendingMessage(ackPostMessage);
        ChatMessageHelper.sendMessage(ackPostMessage);
    }

    public static AckPostMessage createAckNeedCheck(Context context, Session session, List<String> removeMsgIdList, int ackForward) {
        LoginUserBasic loginUserBasic = LoginUserInfo.getInstance().getLoginUserBasic(context);

        return AckPostMessage.createRemoveAckPostMessage(removeMsgIdList, loginUserBasic.mUserId, ParticipantType.User, loginUserBasic.mDomainId, session.identifier, session.type.getToType(), session.mDomainId, ackForward, session.identifier);
    }


    public static String getReceiptKey(PostTypeMessage postTypeMessage) {
        if (isSystemMessageNotFileType(postTypeMessage)) {
            return ((SystemChatMessage) postTypeMessage).to;

        }

        //新版本的逻辑(20181217, 已读位置使用 beginTime 跟 endTime, 并且 to 不再需要指定具体发送者
        // (会引起服务器压力, 例如未读消息里有100个不同的发送者在群里, 就会产生100个回执让后台处理)

        // (20200415调整逻辑)
        //假如是群组, 最终receiptKey 会被转换为未读里最新的那条消息作为群组的to, 此处先用discussion id 做筛选汇总
        if(ParticipantType.Discussion == postTypeMessage.mToType) {
            return postTypeMessage.to;
        }

        return postTypeMessage.from;
    }

    private static boolean isSystemMessageNotFileType(PostTypeMessage postTypeMessage) {
        return postTypeMessage instanceof SystemChatMessage && SystemChatMessage.Type.FILE_DOWNLOAD != ((SystemChatMessage) postTypeMessage).type;
    }


    public static void wrapParticipant(Context context, PostTypeMessage postTypeMessage, Session session, OnMessageWrapListener listener) {
        //default no emp participant
        postTypeMessage.mMyNameInDiscussion = StringUtils.EMPTY;
        postTypeMessage.mMyAvatarInDiscussion = StringUtils.EMPTY;

        if (SessionType.Discussion.equals(session.type)
            || SessionType.User.equals(session.type)) {

            checkAssembleMessageInfo(context, postTypeMessage, session, listener);

        } else {
            listener.onSuccess(postTypeMessage);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static void checkAssembleMessageInfo(final Context context, final PostTypeMessage postTypeMessage, final Session session, final OnMessageWrapListener listener) {
        new AsyncTask<Void, Void, PostTypeMessage>() {
            @Override
            protected PostTypeMessage doInBackground(Void... params) {
                if (SessionType.Discussion.equals(session.type)) {
                    checkAssembleMyNameInDiscussion(context, session, postTypeMessage);
                }

                checkAssembleOrgPositionInfo(postTypeMessage);

                checkAssembleReferenceMessageSourceMsg(postTypeMessage);

                return postTypeMessage;
            }

            @Override
            protected void onPostExecute(PostTypeMessage postTypeMessage) {
                listener.onSuccess(postTypeMessage);
            }
        }.executeOnExecutor(HighPriorityCachedTreadPoolExecutor.getInstance());
    }

    private static void checkAssembleReferenceMessageSourceMsg(PostTypeMessage postTypeMessage) {
        if(postTypeMessage instanceof ReferenceMessage) {
            ReferenceMessage referenceMessage = (ReferenceMessage) postTypeMessage;
            referenceMessage.mSourceMsg = MessageCovertUtil.coverMessageToJsonWithLocalData(referenceMessage);
        }
    }

    private static void checkAssembleOrgPositionInfo(PostTypeMessage postTypeMessage) {
        if(AtworkConfig.CHAT_CONFIG.isChatDetailViewNeedOrgPosition()) {
            if(postTypeMessage instanceof ChatPostMessage) {


                List<Employee> employeeList = EmployeeManager.getInstance().queryUserEmployeeListSync(LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext));
                if(!ListUtil.isEmpty(employeeList)) {
                    Employee firstEmp = employeeList.get(0);

                    ChatPostMessage chatPostMessage = (ChatPostMessage) postTypeMessage;
                    chatPostMessage.orgPositionInfo = new OrgPositionInfo();
                    chatPostMessage.orgPositionInfo.jobTitle = firstEmp.positions.get(0).jobTitle;
                    chatPostMessage.orgPositionInfo.orgDeptInfos = firstEmp.getLast3DepartmentNameSplit();
                }

            }

        }
    }

    private static void checkAssembleMyNameInDiscussion(Context context, Session session, PostTypeMessage postTypeMessage) {
        Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(context, session.identifier);
        if (null != discussion) {
            if (discussion.showEmployeeInfo()) {
                Employee loginEmp = AtworkApplicationLike.getLoginUserEmpSync(discussion.getOrgCodeCompatible());
                if (null != loginEmp) {
                    postTypeMessage.mMyNameInDiscussion = loginEmp.getShowName();
                }

            }
        }
    }

    /**
     * 查询不带好友通知的消息列表
     *
     * @return
     */
    public static List<Session> queryFilteredSessionsFromDb() {
        List<Session> sessions = doQueryFilterSessionFromDb();
        calibrateSendStatusFromDb(sessions);

        //检查设置数据(消息免打扰, 置顶等)
        ChatSessionDataWrap.getInstance().checkSessionConfigData();

        return sessions;
    }

    private static long mLastCalibrateUserSessionsTime = -1;

    public static void calibrateUserSessions(boolean intervalProtect) {
        if(!AtworkConfig.CHAT_RECORD_CHECK_CONFIG.isNeedCalibrateUserSession()) {
            return;
        }

        if (intervalIllegal(intervalProtect)) return;

        Set<Session> sessions = ChatSessionDataWrap.getInstance().getSessions();
        List<String> userSessionIds = new ArrayList<>();
        for(Session session: sessions) {
            if(session.isUserType()) {
                userSessionIds.add(session.identifier);
            }
        }

        if (!ListUtil.isEmpty(userSessionIds)) {


            AsyncTaskThreadPool.getInstance().execute(()->{

                List<String> existUserIds = checkUserIdsExistRemoteSync(userSessionIds);

                userSessionIds.removeAll(existUserIds);
                //剩下的都是服务器确定不存在的 user, 即做清除操作
                if(!ListUtil.isEmpty(userSessionIds)) {
                    for(String userSessionId : userSessionIds) {
                        ChatSessionDataWrap.getInstance().removeSessionSyncSafely(userSessionId, true);
                    }
                }
            });
        }


    }

    public static boolean isCalibrateExpiredSessions = false;

    public static void calibrateExpiredSessions() {

        long messagePullLatestTime = DomainSettingsManager.getInstance().getMessagePullLatestTime();
        if (-1 == messagePullLatestTime) {
            return;
        }

        Set<Session> sessions = ChatSessionDataWrap.getInstance().getSessions();

        if(isCalibrateExpiredSessions) {
            return;
        }

        isCalibrateExpiredSessions = true;
        DbThreadPoolExecutor.getInstance().execute(() -> {

            LogUtil.e("trigger calibrateExpiredSessions");


            List<Session> sessionListFilter = CollectionsKt.filter(sessions, session -> session.lastTimestamp < messagePullLatestTime && !session.havingUnread());
            ChatSessionDataWrap.getInstance().removeSessionsSync(CollectionsKt.map(sessionListFilter, session -> session.identifier), true);

            isCalibrateExpiredSessions = false;
        });

    }



    public static HashMap<String, Boolean> isCalibrateExpiredMessagesMap = new HashMap<>();

    public static void calibrateExpiredMessages(String sessionId, BaseQueryListener<Boolean> listener) {


        long messagePullLatestTime = DomainSettingsManager.getInstance().getMessagePullLatestTime();
        if (-1 == messagePullLatestTime) {
            return;
        }

        Boolean isCalibrateExpiredMessage = isCalibrateExpiredMessagesMap.get(sessionId);
        if(null != isCalibrateExpiredMessage && isCalibrateExpiredMessage) {
            return;
        }

        isCalibrateExpiredMessagesMap.put(sessionId, true);

        DbThreadPoolExecutor.getInstance().execute(() -> {

            LogUtil.e("trigger calibrateExpiredMessages");

            boolean result = MessageRepository.getInstance().deleteMessages(sessionId, messagePullLatestTime);
            isCalibrateExpiredMessagesMap.put(sessionId, false);

            listener.onSuccess(result);

        });

    }
    private static boolean intervalIllegal(boolean intervalProtect) {
        if(intervalProtect) {
            return false;
        }

        long currentTimeMillis = System.currentTimeMillis();

        if(1 * 60 * 1000 > currentTimeMillis - mLastCalibrateUserSessionsTime) {
            return true;
        }

        mLastCalibrateUserSessionsTime = currentTimeMillis;

        return false;
    }

    @NonNull
    private static List<String> checkUserIdsExistRemoteSync(List<String> userSessionIds) {
        List<String> existUserIds = new ArrayList<>();
        //分批查询, 防止 http 414
        int loop = userSessionIds.size() / 100;
        for(int i = 0; i <= loop; i++) {
            int endIndex = (i + 1) * 100;
            int startIndex = i * 100;
            if(endIndex > userSessionIds.size()) {
                endIndex = userSessionIds.size();
            }

            List<String> subUserSessionIds = userSessionIds.subList(startIndex, endIndex);
            HttpResult httpResult = UserSyncNetService.getInstance().queryUserListFromRemote(BaseApplicationLike.baseContext, subUserSessionIds);

            if(httpResult.isRequestSuccess()) {
                SearchUserResponseJson searchUserResponseJson = (SearchUserResponseJson) httpResult.resultResponse;
                existUserIds.addAll(User.toUserIdList(searchUserResponseJson.mResult.mUsers));

            } else {
                if (null != httpResult.resultResponse) {
                    ErrorHandleUtil.handleTokenError(httpResult.resultResponse.status, httpResult.resultResponse.message);

                }
            }


        }
        return existUserIds;
    }

    private static void calibrateSendStatusFromDb(List<Session> sessions) {
        for (Session session : sessions) {
            if (ChatStatus.Sending == session.lastMessageStatus) {
                if (!ChatSessionDataWrap.getInstance().isSendingStatus(session)) {
                    //从数据库查询出来的发送中状态，但实际并没有在发送中的, 一律修正为发送失败状态
                    session.lastMessageStatus = ChatStatus.Not_Send;

                }
            }
        }
    }

    private static List<Session> doQueryFilterSessionFromDb() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select * from session_ where ");
        //代表sql需要拼装and标识
        boolean andExpress = false;
        if (!DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature()) {
            stringBuilder.append("identifier_ !='" + FriendNotifyMessage.FROM + "'");
            andExpress = true;
        }

        if (!DomainSettingsManager.getInstance().handleEmailSettingsFeature()) {
            if (andExpress) {
                stringBuilder.append(" and ");
            }
            stringBuilder.append("identifier_ != '" + Session.EMAIL_APP_ID + "'");
            andExpress = true;
        }

        if (!DomainSettingsManager.getInstance().handleOrgApplyFeature()) {
            if (andExpress) {
                stringBuilder.append(" and ");
            }
            stringBuilder.append("identifier_ != '" + OrgNotifyMessage.FROM + "'");
        }

        String sql = stringBuilder.toString();
        return SessionRepository.getInstance().querySessions(sql);
    }


    public static List<Session> queryAllSessionsDb() {
        List<Session> sessions = doQueryAllSessionDb();
        calibrateSendStatusFromDb(sessions);

        //检查设置数据(消息免打扰, 置顶等)
        ChatSessionDataWrap.getInstance().checkSessionConfigData();


        return sessions;
    }

    private static List<Session> doQueryAllSessionDb() {
        String sql = "select * from session_";
        return SessionRepository.getInstance().querySessions(sql);
    }


    /**
     * 判断服务器是否存在聊天记录
     *
     * @param context
     * @param participantUserId
     * @param participantDomain
     * @return 正常网络返回结果的话, 返回服务器里会话是否空,  网络不正常时返回 null
     */
    public static Boolean isSessionEmptyRemote(Context context, String participantUserId, String participantDomain) {
        long begin = TimeUtil.getTimeInMillisDaysBefore(DomainSettingsManager.getInstance().getConnectionRetainDays());
        MessagesResult messagesResult = MessageAsyncNetService.queryRoamingMessagesSync(context, AtworkConfig.ROAMING_AND_PULL_SYNC_INCLUDE_TYPE, null, "last_in", begin, -1, participantDomain, ParticipantType.User, participantUserId, 1);
        if (messagesResult.mSuccess) {
            return messagesResult.mRealOfflineMsgSize <= 0;
        }

        return null;
    }


    /**
     * 拉取消息或漫游消息
     *
     * @param context
     * @param participantDomain 参与者的域
     * @param participantType   参与者类型
     * @param participantId     参与者ID
     * @param begin             拉取开始时间
     * @param end               拉取结束时间
     * @param include           拉取包含的类型 ACK CMD TEXT。。。
     * @param exclude           拉取不包含类型 ACK CMD TEXT...
     * @param sort              排序         first_in last_in
     * @param listener          回调监听器
     */
    @SuppressLint("StaticFieldLeak")
    public static void queryRoamingMessages(final Context context, final String participantDomain, final ParticipantType participantType,
                                            final String participantId, final long begin, final long end, final String include,
                                            final String exclude, final String sort, final int limit, final boolean removeSession,
                                            final MessageAsyncNetService.GetHistoryMessageListener listener) {
        new AsyncTask<Void, Void, MessagesResult>() {
            @Override
            protected MessagesResult doInBackground(Void... params) {
                MessagesResult messagesResult = MessageAsyncNetService.queryRoamingMessagesSync(context, include, exclude, sort, begin, end, participantDomain, participantType, participantId, limit);
                if(removeSession && messagesResult.mSuccess) {

                    ChatDetailExposeBroadcastSender.clearMessageList();
                    MessageCache.getInstance().clearSessionMessage(participantId);
                    MessageRepository.getInstance().deleteAllMessages(participantId);

//                    ChatSessionDataWrap.getInstance().removeSessionSyncSafely(participantId, true);

                }

                return messagesResult;
            }

            @Override
            protected void onPostExecute(MessagesResult responseJson) {
                if (responseJson.mSuccess) {
                    List<ChatPostMessage> chatPostMessageList = OfflineMessageResponseJson.toChatPostMessages(responseJson.mPostTypeMessages);
                    listener.getHistoryMessageSuccess(chatPostMessageList, responseJson.mRealOfflineMsgSize);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(responseJson.mHttpResult, listener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }



}
