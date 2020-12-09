package com.foreveross.atwork.modules.chat.service.upload;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.service.ChatSendOnMainViewService;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.modules.chat.util.MultipartMsgHelper;
import com.foreveross.atwork.utils.AtworkToast;

/**
 * Created by dasunsy on 2017/8/24.
 */

public class MultipartUploadListener implements MediaCenterNetManager.MediaUploadListener {

    private Session mSession;
    private MultipartChatMessage mMultipartChatMessage;
    private boolean justUpload = false;

    private MultipartMsgHelper.OnMessageFileUploadedListener mListener;


    public MultipartUploadListener(Session session, MultipartChatMessage multipartChatMessage) {
        this.mSession = session;
        this.mMultipartChatMessage = multipartChatMessage;
    }

    public MultipartUploadListener(MultipartChatMessage multipartChatMessage, MultipartMsgHelper.OnMessageFileUploadedListener listener) {
        this.mMultipartChatMessage = multipartChatMessage;
        justUpload = true;
        mListener = listener;
    }

    @Override
    public String getMsgId() {
        return mMultipartChatMessage.deliveryId;
    }

    @Override
    public MediaCenterNetManager.UploadType getType() {
        return MediaCenterNetManager.UploadType.VOICE;
    }

    @Override
    public void uploadSuccess(String mediaInfo) {
        mMultipartChatMessage.fileStatus = FileStatus.SENDED;
        mMultipartChatMessage.mFileId = mediaInfo;
        if (justUpload) {
            if (mListener != null) {
                mListener.onSuccess(mMultipartChatMessage);
            }
            return;
        }

        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();

        ChatSendOnMainViewService.sendNewMessageToIM(mSession, mMultipartChatMessage);

        MediaCenterNetManager.removeMediaUploadListener(this);
        ChatSessionDataWrap.getInstance().removeChatInFileStreaming(mSession.identifier, mMultipartChatMessage.deliveryId);
    }

    @Override
    public void uploadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
        MediaCenterNetManager.removeUploadFailList(getMsgId());

        if (justUpload) {
            if (mListener != null) {
                mListener.onError(errorCode, errorMsg);
            }
            return;
        }

        if (errorCode != -99) {
            AtworkToast.sendToastDependOnActivity(R.string.network_error);
        }

        mMultipartChatMessage.fileStatus = FileStatus.SEND_FAIL;
        mMultipartChatMessage.chatStatus = ChatStatus.Not_Send;
        ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, mMultipartChatMessage);

        ChatSessionDataWrap.getInstance().removeChatInFileStreaming(mSession.identifier, mMultipartChatMessage.deliveryId);


        if (doRefreshView) {
            ChatSessionDataWrap.getInstance().notifyMessageSendFail(mMultipartChatMessage.deliveryId);
            ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
        }
    }

    @Override
    public void uploadProgress(double progress) {

    }


}
