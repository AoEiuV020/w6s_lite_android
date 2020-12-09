package com.foreveross.atwork.modules.chat.service.upload;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.service.ChatSendOnMainViewService;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.utils.AtworkToast;

/**
 * Created by dasunsy on 2017/8/24.
 */

public class FileMediaUploadListener implements MediaCenterNetManager.MediaUploadListener {
    private Session mSession;
    private FileTransferChatMessage mFileTransferChatMessage;

    public FileMediaUploadListener(Session session, FileTransferChatMessage fileTransferChatMessage) {
        this.mSession = session;
        this.mFileTransferChatMessage = fileTransferChatMessage;
    }

    @Override
    public String getMsgId() {
        return mFileTransferChatMessage.deliveryId;
    }

    @Override
    public MediaCenterNetManager.UploadType getType() {
        return MediaCenterNetManager.UploadType.CHAT_FILE;
    }

    @Override
    public void uploadSuccess(String mediaInfo) {
        mFileTransferChatMessage.mediaId = mediaInfo;
        mFileTransferChatMessage.fileStatus = FileStatus.SENDED;
        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
        ChatSendOnMainViewService.sendNewMessageToIM(mSession, mFileTransferChatMessage);
//        FileDataUtil.updateRecentFileDB(mFileTransferChatMessage);

        MediaCenterNetManager.removeMediaUploadListener(this);
        ChatSessionDataWrap.getInstance().removeChatInFileStreaming(mSession.identifier, mFileTransferChatMessage.deliveryId);

    }



    @Override
    public void uploadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
        if (errorCode != -99) {
            AtworkToast.sendToastDependOnActivity(R.string.upload_file_error);
            mFileTransferChatMessage.fileStatus = FileStatus.SEND_FAIL;

            MediaCenterNetManager.removeUploadFailList(getMsgId());

        } else {
            mFileTransferChatMessage.fileStatus = FileStatus.SEND_CANCEL;
        }

        mFileTransferChatMessage.chatStatus = ChatStatus.Not_Send;

        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();

        MediaCenterNetManager.removeMediaUploadListener(this);
        ChatSessionDataWrap.getInstance().removeChatInFileStreaming(mSession.identifier, mFileTransferChatMessage.deliveryId);

        ChatSessionDataWrap.getInstance().notifyMessageSendFail(mFileTransferChatMessage.deliveryId);

    }

    @Override
    public void uploadProgress(double progress) {
        mFileTransferChatMessage.progress = (int) progress;
        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();



    }

    public void setFileTransferChatMessage(FileTransferChatMessage fileTransferChatMessage) {
        this.mFileTransferChatMessage = fileTransferChatMessage;
    }
}

