package com.foreveross.atwork.modules.chat.service.upload;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.api.sdk.upload.model.MediaCompressResponseJson;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.service.ChatSendOnMainViewService;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.utils.AtworkToast;

/**
 * Created by dasunsy on 2017/8/24.
 */

public class ImageMediaUploadListener implements MediaCenterNetManager.MediaUploadListener {

    private Session mSession;
    private ImageChatMessage mImageChatMessage;

    public ImageMediaUploadListener(Session session, ImageChatMessage imageChatMessage) {
        this.mSession = session;
        this.mImageChatMessage = imageChatMessage;
    }

    @Override
    public String getMsgId() {
        return mImageChatMessage.deliveryId;
    }

    @Override
    public MediaCenterNetManager.UploadType getType() {
        return MediaCenterNetManager.UploadType.CHAT_IMAGE;
    }

    @Override
    public void uploadSuccess(String mediaInfo) {
        mImageChatMessage.fileStatus = FileStatus.SENDED;

        MediaCompressResponseJson compressResponseJson = JsonUtil.fromJson(mediaInfo, MediaCompressResponseJson.class);

        if (mImageChatMessage.isFullMode()) {
            if (null != compressResponseJson && null != compressResponseJson.mMediaCompressInfo) {
                mImageChatMessage.fullMediaId = compressResponseJson.mMediaCompressInfo.mOriginalImg.mMediaId;
                mImageChatMessage.mediaId = compressResponseJson.mMediaCompressInfo.mCompressImg.mMediaId;
            } else {
                mImageChatMessage.fullMediaId = mediaInfo;
                mImageChatMessage.mediaId = mediaInfo;
            }

        } else {
            mImageChatMessage.mediaId = mediaInfo;

        }
        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();

        ChatSendOnMainViewService.sendNewMessageToIM(mSession, mImageChatMessage);

        MediaCenterNetManager.removeMediaUploadListener(this);

        ChatSessionDataWrap.getInstance().removeChatInFileStreaming(mSession.identifier, mImageChatMessage.deliveryId);

    }

    @Override
    public void uploadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
        if (errorCode != -99) {
            AtworkToast.sendToastDependOnActivity(R.string.upload_file_error);
            MediaCenterNetManager.removeUploadFailList(getMsgId());
        }

        mImageChatMessage.fileStatus = FileStatus.SEND_FAIL;
        mImageChatMessage.chatStatus = ChatStatus.Not_Send;
        ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, mImageChatMessage);
        MediaCenterNetManager.removeMediaUploadListener(this);
        ChatSessionDataWrap.getInstance().removeChatInFileStreaming(mSession.identifier, mImageChatMessage.deliveryId);

        if (doRefreshView) {
            ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
            ChatSessionDataWrap.getInstance().notifyMessageSendFail(mImageChatMessage.deliveryId);
        }
    }

    @Override
    public void uploadProgress(double progress) {
        mImageChatMessage.progress = (int) progress;
        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
    }
}
