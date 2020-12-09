package com.foreveross.atwork.modules.chat.service.upload;

import android.os.AsyncTask;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.service.ChatSendOnMainViewService;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.utils.AtworkToast;

import java.io.File;
import java.io.IOException;

/**
 * Created by dasunsy on 2017/8/24.
 */

public class MicroVideoUploadListener implements MediaCenterNetManager.MediaUploadListener {

    private Session mSession;
    private MicroVideoChatMessage mMicroVideoChatMessage;

    public MicroVideoUploadListener(Session session, MicroVideoChatMessage message) {
        this.mSession = session;
        this.mMicroVideoChatMessage = message;
    }

    @Override
    public String getMsgId() {
        return mMicroVideoChatMessage.deliveryId;
    }

    @Override
    public MediaCenterNetManager.UploadType getType() {
        return MediaCenterNetManager.UploadType.MICRO_VIDEO;
    }

    @Override
    public void uploadSuccess(String mediaInfo) {
        mMicroVideoChatMessage.fileStatus = FileStatus.SENDED;
        mMicroVideoChatMessage.mediaId = mediaInfo;
        File videoFile = new File(AtWorkDirUtils.getInstance().getMicroVideoHistoryDir(BaseApplicationLike.baseContext), mMicroVideoChatMessage.deliveryId + ".mp4");
        if (videoFile.exists()) {

            //因为目前所有的 thumb_img 都用 deliveryId 为命名, 而小视频相册目前根据文件名去找缩略图
            //当前小视频相册改为 mediaId 的名字会导致找不到缩略图, 因此暂时用 copy 多份缩略图的方式来处理
            //todo 后期再整体考虑全部文件的 deliveryId与 mediaId命名, 或者用更好的实现方式预防类似的问题
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        FileUtil.copyFile(ImageShowHelper.getThumbnailPath(BaseApplicationLike.baseContext, mMicroVideoChatMessage.deliveryId), ImageShowHelper.getThumbnailPath(BaseApplicationLike.baseContext, mMicroVideoChatMessage.mediaId));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            }.execute();
        }
        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();

        ChatSendOnMainViewService.sendNewMessageToIM(mSession, mMicroVideoChatMessage);

        MediaCenterNetManager.removeMediaUploadListener(this);
        ChatSessionDataWrap.getInstance().removeChatInFileStreaming(mSession.identifier, mMicroVideoChatMessage.deliveryId);

    }

    @Override
    public void uploadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
        if (errorCode != -99) {
            AtworkToast.sendToastDependOnActivity(R.string.upload_file_error);
            MediaCenterNetManager.removeUploadFailList(getMsgId());
        }

        mMicroVideoChatMessage.fileStatus = FileStatus.SEND_FAIL;
        mMicroVideoChatMessage.chatStatus = ChatStatus.Not_Send;
        ChatDaoService.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, mMicroVideoChatMessage);
        MediaCenterNetManager.removeMediaUploadListener(this);
        ChatSessionDataWrap.getInstance().removeChatInFileStreaming(mSession.identifier, mMicroVideoChatMessage.deliveryId);

        if (doRefreshView) {
            ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
            ChatSessionDataWrap.getInstance().notifyMessageSendFail(mMicroVideoChatMessage.deliveryId);
        }
    }

    @Override
    public void uploadProgress(double progress) {
        mMicroVideoChatMessage.progress = (int) progress;
        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
    }
}
