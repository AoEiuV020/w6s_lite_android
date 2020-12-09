package com.foreveross.atwork.modules.chat.service;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatEnvironment;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.UserFileDownloadNotifyMessage;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.util.SystemChatMessageHelper;
import com.foreveross.atwork.utils.SystemMessageHelper;

public class FileDownloadNotifyService {

    public static void receiveUserFileDownloadNotifyMessage(UserFileDownloadNotifyMessage userFileDownloadNotifyMessage, boolean isCameFromOnline) {

        if (UserFileDownloadNotifyMessage.Operation.FILE_DOWNLOAD_SUCCESS == userFileDownloadNotifyMessage.mOperation) {
            //目前只需要单聊显示文件下载成功的通知, 自己发送的文件通知, 无需显示出来
            if(ParticipantType.Discussion == userFileDownloadNotifyMessage.mToType || User.isYou(BaseApplicationLike.baseContext, userFileDownloadNotifyMessage.from)) {
                return;
            }


            SystemChatMessage systemChatMessage = SystemChatMessageHelper.createUserFileDownloadNoticeMessage(userFileDownloadNotifyMessage);
            ChatSessionDataWrap.getInstance().asyncReceiveMessage(systemChatMessage, isCameFromOnline);

            SystemMessageHelper.newSystemMessageNotice(BaseApplicationLike.baseContext, systemChatMessage);
        }
    }


    public static void handleFileDownloadSuccessfullyNotify(FileTransferChatMessage fileTransferChatMessage) {
        if(ChatEnvironment.FAVORITE == fileTransferChatMessage.chatEnvironment) {
            return;
        }


        if(ChatEnvironment.CHAT == fileTransferChatMessage.chatEnvironment
                && !fileTransferChatMessage.isLegalP2pUserChat(AtworkApplicationLike.baseContext)) {
            return;
        }

        if(ChatEnvironment.MULTIPART == fileTransferChatMessage.chatEnvironment
                && !fileTransferChatMessage.isParentLegalP2pUserChat(AtworkApplicationLike.baseContext)) {
            return;
        }


        String from = fileTransferChatMessage.from;
//        if(ChatEnvironment.CHAT == fileTransferChatMessage.chatEnvironment) {
//            from = (fileTransferChatMessage.from);
//
//        }

        if(ChatEnvironment.MULTIPART == fileTransferChatMessage.chatEnvironment) {
            from = (fileTransferChatMessage.parentMultipartChatMessage.from);
        }

        if(User.isYou(AtworkApplicationLike.baseContext, from)) {
            return;
        }


        UserFileDownloadNotifyMessage.Builder builder = UserFileDownloadNotifyMessage.newBuilder()
                .setOperation(UserFileDownloadNotifyMessage.Operation.FILE_DOWNLOAD_SUCCESS)
                .setFileName(fileTransferChatMessage.name)
                .setFileSize(fileTransferChatMessage.size)
                .setToDomainId(fileTransferChatMessage.mFromDomain)
                .setDisplayName(fileTransferChatMessage.mMyName)
                .setDisplayAvatar(fileTransferChatMessage.mMyAvatar)
                .setOrgId(fileTransferChatMessage.orgId)
                .setTo(from)
                .setToType(ParticipantType.User);


        UserFileDownloadNotifyMessage fileDownloadNotifyMessage = builder.build();

        ChatService.sendNotifyMessage(fileDownloadNotifyMessage);

    }

}
