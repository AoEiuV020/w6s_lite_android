package com.foreveross.atwork.modules.chat.service.upload;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.service.ChatSendOnMainViewService;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.utils.AtworkToast;

/**
 * Created by dasunsy on 2017/8/24.
 */

public class VoiceUploadListener implements MediaCenterNetManager.MediaUploadListener {

    private Session mSession;
    private VoiceChatMessage mVoiceChatMessage;

    public VoiceUploadListener(Session session, VoiceChatMessage voiceChatMessage) {
        this.mSession = session;
        this.mVoiceChatMessage = voiceChatMessage;
    }

    @Override
    public String getMsgId() {
        return mVoiceChatMessage.deliveryId;
    }

    @Override
    public MediaCenterNetManager.UploadType getType() {
        return MediaCenterNetManager.UploadType.VOICE;
    }

    @Override
    public void uploadSuccess(String mediaInfo) {
        mVoiceChatMessage.fileStatus = FileStatus.SENDED;
        mVoiceChatMessage.mediaId = mediaInfo;

        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();

        ChatSendOnMainViewService.sendNewMessageToIM(mSession, mVoiceChatMessage);

        MediaCenterNetManager.removeMediaUploadListener(this);
        ChatSessionDataWrap.getInstance().removeChatInFileStreaming(mSession.identifier, mVoiceChatMessage.deliveryId);

    }

    @Override
    public void uploadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
        if (errorCode != -99) {
            AtworkToast.sendToastDependOnActivity(R.string.upload_file_error);
            MediaCenterNetManager.removeUploadFailList(getMsgId());
        }

        mVoiceChatMessage.fileStatus = FileStatus.SEND_FAIL;
        mVoiceChatMessage.chatStatus = ChatStatus.Not_Send;
        ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, mVoiceChatMessage);
        ChatSessionDataWrap.getInstance().removeChatInFileStreaming(mSession.identifier, mVoiceChatMessage.deliveryId);

        if(doRefreshView){
            ChatSessionDataWrap.getInstance().notifyMessageSendFail(mVoiceChatMessage.deliveryId);
            ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
        }
    }

    @Override
    public void uploadProgress(double progress) {

    }


}

