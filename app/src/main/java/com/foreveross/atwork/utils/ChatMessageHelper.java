package com.foreveross.atwork.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.db.service.repository.MessageRepository;
import com.foreverht.db.service.repository.SessionRepository;
import com.foreverht.db.service.repository.UnreadMessageRepository;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingParticipant;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.api.sdk.upload.MediaCenterSyncNetService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.ReceiptMessage;
import com.foreveross.atwork.infrastructure.newmessage.UserTypingMessage;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ack.AckPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.shared.ReadAckPersonShareInfo;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.chat.BasicMsgHelper;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.services.ImSocketService;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ChatMessageHelper extends BasicMsgHelper {

    private static final String MB = "M";

    private static final String KB = "KB";

    private static final String B = "B";


    public static Set<String> getIds(List<? extends PostTypeMessage> msgList) {

        Set<String> msgIdSet = new HashSet<>();
        for(PostTypeMessage message : msgList) {
            msgIdSet.add(message.deliveryId);
        }

        return msgIdSet;
    }

    public static boolean contains(List<? extends PostTypeMessage> msgList, List<String> msgIdList) {
        for(PostTypeMessage msg : msgList) {

            for (String msgId : msgIdList) {
                if(msgId.equals(msg.deliveryId)) {
                    return true;
                }
            }
        }

        return false;
    }


    public static boolean remove(List<? extends PostTypeMessage> msgList, List<String> msgIdList) {
        List<PostTypeMessage> waitRemovedMsgList = new ArrayList<>();
        for(PostTypeMessage msg : msgList) {

            for(String msgId : msgIdList) {
                if(msgId.equals(msg.deliveryId)) {
                    waitRemovedMsgList.add(msg);
                }
            }

        }

        if(!ListUtil.isEmpty(waitRemovedMsgList)) {
            return msgList.removeAll(waitRemovedMsgList);
        }

        return false;

    }
    /**
     * 往 IM 服务 发送消息
     * */
    public static void sendMessage(PostTypeMessage message) {
        Intent intent = new Intent(ImSocketService.ACTION_NEW_SEND_MESSAGE);
        intent.putExtra(ImSocketService.DATA_NEW_MESSAGE, message);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
    }

    public static void sendTypingStatus(UserTypingMessage message) {
        Intent intent = new Intent(ImSocketService.ACTION_TYPING);
        intent.putExtra(ImSocketService.DATA_NEW_MESSAGE, message);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
    }

    public static List<String> toMsgIdList(List<ChatPostMessage> messageList) {
        List<String> msgIdList = new ArrayList<>();

        for(ChatPostMessage chatPostMessage : messageList) {
            msgIdList.add(chatPostMessage.deliveryId);
        }

        return msgIdList;
    }


    public static void makeAckIdsCompatibleSync(AckPostMessage ackPostMessage) {
        if(ackPostMessage.isReadAckInDurationAndAckIdsEmpty()) {
            ackPostMessage.ackIds = new ArrayList<>(MessageRepository.getInstance().queryMsgIds(ackPostMessage.getSessionChatId(BaseApplicationLike.baseContext), ackPostMessage.beginTime, ackPostMessage.endTime));
        }
    }

    public static List<ReceiptMessage> toReceiptMessage(AckPostMessage ackPostMessage) {
        if (!AckPostMessage.AckType.READ.equals(ackPostMessage.type)) {
            return null;
        }
        List<ReceiptMessage> receiptMessages = new ArrayList<>();
        if (ackPostMessage.ackIds == null) {
            return receiptMessages;
        }

        try {
            for (String msgId : ackPostMessage.ackIds) {
                ReceiptMessage receiptMessage = new ReceiptMessage();
                receiptMessage.msgId = msgId;
                receiptMessage.timestamp = ackPostMessage.deliveryTime;
                receiptMessage.receiveFrom = ackPostMessage.from;
                receiptMessage.sessionIdentifier = ChatMessageHelper.getChatUser(ackPostMessage).mUserId;
                receiptMessages.add(receiptMessage);
                LogUtil.d(ImSocketService.TAG, "已读回执:" + receiptMessage.msgId + ":" + receiptMessage.sessionIdentifier + ":" + receiptMessage.receiveFrom);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receiptMessages;
    }

    public static void handlePeerReadMessage(AckPostMessage ackPostMessage, boolean isFromOnLine) {
        String sessionIdFromAck = ackPostMessage.getSessionChatId(BaseApplicationLike.baseContext);
        if (StringUtils.isEmpty(sessionIdFromAck)) {
            return;
        }

        Long targetReadTime = ackPostMessage.endTime;
        if(null == targetReadTime || 0 >= targetReadTime) {
            targetReadTime = ackPostMessage.deliveryTime;
        }
        ReadAckPersonShareInfo.INSTANCE.updateTargetReadTime(AtworkApplicationLike.baseContext, sessionIdFromAck, targetReadTime);

        String realSessionIdFromClientId = ConversionConfigSettingParticipant.getSessionIdFromClientId(sessionIdFromAck);

        Session session = ChatSessionDataWrap.getInstance().getSessionSafely(realSessionIdFromClientId, null);
        if (null != session) {
            if(ackPostMessage.ackIds.contains(session.lastMessageId)) {
                if (ChatStatus.UnDo != session.lastMessageStatus
                        && ChatStatus.Hide != session.lastMessageStatus) {
                    session.lastMessageStatus = ChatStatus.Peer_Read;

                    SessionRepository.getInstance().updateSession(session);

                }
            }
        }

        if (isFromOnLine) {
            //声明更新列表
            SessionRefreshHelper.notifyRefreshSessionAndCount();
            ChatDetailExposeBroadcastSender.refreshMessageListViewUI();

        }
    }

    public static void handleSelfReadMessages(AckPostMessage ackPostMessage, boolean isFromOnLine) {
        if (StringUtils.isEmpty(ackPostMessage.getSessionChatId(BaseApplicationLike.baseContext))) {
            return;
        }


        String sessionIdFromAck = ackPostMessage.getSessionChatId(BaseApplicationLike.baseContext);

        String realSessionIdFromClientId = ConversionConfigSettingParticipant.getSessionIdFromClientId(sessionIdFromAck);

        handleSessionsFoldRead(realSessionIdFromClientId, ackPostMessage);


        Session session = ChatSessionDataWrap.getInstance().getSessionSafely(realSessionIdFromClientId, null);


        if (null != session) {
            UnreadMessageRepository.getInstance().batchRemoveUnread(session.identifier, new HashSet<>(ackPostMessage.ackIds));

            //更新session里面的值
            if (session.unreadMessageIdSet != null) {
                session.unreadMessageIdSet.removeAll(ackPostMessage.ackIds);
            }



            //清除@人状态
            if(Session.ShowType.At == session.lastMessageShowType && ackPostMessage.ackIds.contains(session.atMessageId)) {
                session.lastMessageShowType = Session.ShowType.Text;
                SessionRepository.getInstance().updateSession(session);
            }


            if (isFromOnLine) {
                //声明更新列表
                SessionRefreshHelper.notifyRefreshSessionAndCount();

            }

        }


    }

    private static void handleSessionsFoldRead(String realSessionIdFromClientId, AckPostMessage ackPostMessage) {
        if(Session.COMPONENT_ANNOUNCE_APP.equals(realSessionIdFromClientId)) {

            long lastTimeEnterAnnounceApp = PersonalShareInfo.getInstance().getLastTimeEnterAnnounceApp(AtworkApplicationLike.baseContext);
            if(lastTimeEnterAnnounceApp < ackPostMessage.ackTime) {
                PersonalShareInfo.getInstance().setLastTimeEnterAnnounceApp(AtworkApplicationLike.baseContext, ackPostMessage.ackTime);
            }
        }
    }


    /**
     * 通知收到一条新消息
     */
    public static void notifyMessageReceived(ChatPostMessage message) {
        Intent intent = new Intent(ChatDetailFragment.CHAT_MESSAGE_RECEIVED);
        intent.putExtra(ChatDetailFragment.DATA_NEW_MESSAGE, message);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
    }

    /**
     * 通知消息进行更新
     * */
    public static void notifyMessageInChatUpdated(ChatPostMessage message) {
        Intent intent = new Intent(ChatDetailFragment.CHAT_MESSAGE_RECEIVED_SELF_UPDATE);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatDetailFragment.DATA_NEW_MESSAGE, message);
        intent.putExtra(ChatDetailFragment.DATA_BUNDLE, bundle);

        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
    }


    private static void handleSetChatStatus(PostTypeMessage postTypeMessage) {

        if(postTypeMessage instanceof TextChatMessage) {
            TextChatMessage textChatMessage = (TextChatMessage) postTypeMessage;
            if(textChatMessage.atAll) {

                if(null != BaseApplicationLike.baseContext && !User.isYou(BaseApplicationLike.baseContext, textChatMessage.from)) {
                    textChatMessage.setChatStatus(ChatStatus.At_All);
                    return;
                }
            }

        }


        postTypeMessage.setChatStatus(ChatStatus.Sended);

    }



    /**
     * 处理消息发送者接收者状态, 以及媒体下载操作
     *
     * @param context
     * @param message  消息
     * @param isOnline 是否为在线消息
     */
    public static void dealWithChatMessage(Context context, ChatPostMessage message, boolean isOnline) {


        dealWithSelf(message);
        handleSetChatStatus(message);
        dealMedia(context, message, isOnline);

    }

    private static void dealMedia(Context context, ChatPostMessage message, boolean isOnline) {


        //出于优化离线同步消息的速度以及流量节省考虑, 离线时不处理媒体下载
        if (isOnline) {
            dealMediaOnline(context, message);


        } else {
            dealMediaOffline(context, message);


        }
    }

    private static void dealMediaOffline(Context context, ChatPostMessage message) {
        //save thumb to local
        if(message instanceof ImageChatMessage) {
            ImageChatMessage imageChatMessage = (ImageChatMessage) message;

            if(null != imageChatMessage.getThumbnails()) {
                ImageShowHelper.saveThumbnailImage(context, imageChatMessage.deliveryId, imageChatMessage.getThumbnails());
                imageChatMessage.clearThumbnails();
            }

            return;
        }

        if(message instanceof MicroVideoChatMessage) {
            MicroVideoChatMessage microVideoChatMessage = (MicroVideoChatMessage) message;

            if(null != microVideoChatMessage.thumbnails) {
                ImageShowHelper.saveThumbnailImage(context, microVideoChatMessage.deliveryId, microVideoChatMessage.thumbnails);

            }

            return;
        }
    }

    private static void dealMediaOnline(Context context, ChatPostMessage message) {
        //保存语音
        if (message instanceof VoiceChatMessage) {
            VoiceChatMessage voiceChatMessage = (VoiceChatMessage) message;
            //下载语音
            MediaCenterNetManager mediaCenterNetManager = new MediaCenterNetManager(BaseApplicationLike.baseContext);
            String filePath = VoiceChatMessage.getAudioPath(context, voiceChatMessage.deliveryId);

            mediaCenterNetManager.syncDownloadFile(voiceChatMessage.mediaId, voiceChatMessage.deliveryId, filePath);
            voiceChatMessage.content = FileStreamHelper.readFile(filePath);

            if (voiceChatMessage.content != null && voiceChatMessage.content.length != 0) {
                VoiceChatMessage.receiveAudio(context, voiceChatMessage.deliveryId, voiceChatMessage.content);
            }

            return;
        }

        //保存图片
        if (message instanceof ImageChatMessage) {
            ImageChatMessage imageChatMessage = (ImageChatMessage) message;


            if(null != imageChatMessage.getThumbnails()) {
                ImageShowHelper.saveThumbnailImage(context, imageChatMessage.deliveryId, imageChatMessage.getThumbnails());
                imageChatMessage.clearThumbnails();

            } else {

                if(imageChatMessage.thumbnailMediaId != null) {
                    MediaCenterSyncNetService mediaCenterSyncNetService = MediaCenterSyncNetService.getInstance();

                    String filePath = ImageShowHelper.getThumbnailPath(context, imageChatMessage.deliveryId);
                    mediaCenterSyncNetService.getMediaContent(BaseApplicationLike.baseContext, imageChatMessage.thumbnailMediaId, filePath, MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.THUMBNAIL);
                }

            }


            return;
        }

        //下载视频缩略图
         if (message instanceof MicroVideoChatMessage) {
            MicroVideoChatMessage microVideoChatMessage = (MicroVideoChatMessage) message;
            if (microVideoChatMessage.thumbnailMediaId != null && microVideoChatMessage.thumbnails == null) {
                MediaCenterSyncNetService mediaCenterSyncNetService = MediaCenterSyncNetService.getInstance();

                String filePath = ImageShowHelper.getThumbnailPath(context, microVideoChatMessage.deliveryId);
                mediaCenterSyncNetService.getMediaContent(BaseApplicationLike.baseContext, microVideoChatMessage.thumbnailMediaId, filePath, MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.THUMBNAIL);
                microVideoChatMessage.thumbnails = FileStreamHelper.readFile(filePath);
                LogUtil.d("image,,,", microVideoChatMessage.thumbnailMediaId);
            }
            ImageShowHelper.saveThumbnailImage(context, microVideoChatMessage.deliveryId, microVideoChatMessage.thumbnails);

            return;
        }
    }


    private static void dealWithSelf(ChatPostMessage message) {
        String currentUser = LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext);

        if (currentUser.equalsIgnoreCase(message.from)) {
            message.chatSendType = ChatSendType.SENDER;
            //pc web暂时没有做状态处理, 这里客户端暂时做下面处理
            if (message instanceof FileTransferChatMessage) {
                FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) message;
                fileTransferChatMessage.fileStatus = FileStatus.SENDED;
            }

        } else {
            message.chatSendType = ChatSendType.RECEIVER;
        }
    }


    public static String getMBOrKBString(long size) {


        double mb = (double) size / 1024 / 1024;


        if (mb < 1.0) {
            mb = (double) size / 1024;

            //小于1KB，用B
            if (mb < 1.0) {
                mb = size;
                return mb + B;
            }

            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(mb) + KB;
        } else {
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(mb) + MB;
        }

    }

    public static String getReadableNameShow(Context context, PostTypeMessage message) {
        String contact;
        if (message.needEmpParticipant()) {
            contact = message.mMyNameInDiscussion;

        } else {
            contact = message.mMyName;

        }
        if (StringUtils.isEmpty(contact)) {
            contact = UserManager.getInstance().getReadableName(context, message.from, message.mFromDomain);

        }

        return contact;
    }

    public static boolean isOverdue(FileTransferChatMessage fileTransferChatMessage) {
        boolean isOverdue = TimeUtil.isOverdueDate(TimeUtil.getCurrentTimeInMillis(), fileTransferChatMessage.expiredTime);
        boolean isLocalFileExist = !TextUtils.isEmpty(fileTransferChatMessage.filePath) && new File(fileTransferChatMessage.filePath).exists();

        return isOverdue && !isLocalFileExist;

    }


    public static ReadAckPersonShareInfo.ReadAckInfo getReadAckInfoAndCheck(Context context, String sessionId) {
        ReadAckPersonShareInfo.ReadAckInfo readAckInfo = ReadAckPersonShareInfo.INSTANCE.getReadAckInfo(context, sessionId);
        if(null == readAckInfo) {
            readAckInfo = new ReadAckPersonShareInfo.ReadAckInfo();
            readAckInfo.setTargetReadTimeOldDataLine(TimeUtil.getCurrentTimeInMillis());
            ReadAckPersonShareInfo.INSTANCE.updateReadAckInfo(context, sessionId, readAckInfo);
        }

        return readAckInfo;

    }




}

