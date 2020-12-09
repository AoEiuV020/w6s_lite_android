package com.foreveross.atwork.modules.chat.service;

import android.content.Intent;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.EventPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.manager.DropboxManager;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.data.SendMessageDataWrap;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.modules.chat.util.FileDataUtil;
import com.foreveross.atwork.services.ImSocketService;
import com.foreveross.atwork.utils.ChatMessageHelper;

/**
 * Created by dasunsy on 2017/8/24.
 */

public class ChatSendOnMainViewService {

    public static void sendNewMessageToIM(@Nullable final Session session, final PostTypeMessage message) {
        if (message instanceof FileTransferChatMessage) {
            if (AtworkConfig.SPECIAL_ENABLE_DISCUSSION_FILE_WHEN_CLOSE_DROPBOX && SessionType.Discussion.equals(session.type)) {
                DropboxManager.getInstance().saveToDropboxFromMessage(BaseApplicationLike.baseContext, session, (FileTransferChatMessage) message, false);
                FileDataUtil.updateRecentFileDB((FileTransferChatMessage)message);

            }
        }

        if (message instanceof ChatPostMessage) {
            SendMessageDataWrap.getInstance().addChatSendingMessage((ChatPostMessage) message);

        } else if (message instanceof EventPostMessage) {
            SendMessageDataWrap.getInstance().addEventSendingMessage((EventPostMessage) message);
        }

        ChatSendOnMainViewService.ackCheckIMTimeOut(message);

        //清除上一次AT信息
        ChatDetailExposeBroadcastSender.clearAtContacts();

        ChatMessageHelper.sendMessage(message);
    }


    /**
     * 检查消息是否正常发出, 进而判断 IM 是否正常
     */
    public static void ackCheckIMTimeOut(PostTypeMessage message) {
        new Handler().postDelayed(() -> {
            if (ChatStatus.Sending.equals(message.chatStatus)) {
                LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ImSocketService.ACTION_IM_RECONNECT));

            } else {

                if (message instanceof ChatPostMessage) {
                    if (!((ChatPostMessage) message).isUndo()) {
                        message.chatStatus = ChatStatus.Sended;

                    }
                }

            }
            ChatDetailExposeBroadcastSender.refreshMessageListViewUI();

            if (message instanceof ChatPostMessage) {
                ChatDaoService.getInstance().updateMessage((ChatPostMessage) message);
            }

        }, AtworkConfig.SOCKET_TIME_OUT);
    }

}
