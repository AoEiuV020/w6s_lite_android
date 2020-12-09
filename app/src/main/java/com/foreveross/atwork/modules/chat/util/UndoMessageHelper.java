package com.foreveross.atwork.modules.chat.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.cache.MessageCache;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.user.UserHandleBasic;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.utils.ChatMessageHelper;

/**
 * Created by dasunsy on 16/2/4.
 */
public class UndoMessageHelper {

    public static final String UNDO_MESSAGE_RECEIVED = "UNDO_MESSAGE_RECEIVED";


    public static boolean isHandleUndoLegal(ChatPostMessage chatPostMessage) {
        if(BaseApplicationLike.sIsDebug) {
            return true;
        }

        long maxUndoTime = DomainSettingsManager.getInstance().getMaxUndoTime();

        if(0 >= maxUndoTime) {
            return true;
        }

        return TimeUtil.getCurrentTimeInMillis() - chatPostMessage.deliveryTime <= maxUndoTime;
    }

    public static boolean isFirstClickUndoMessage() {
        long maxUndoTime = DomainSettingsManager.getInstance().getMaxUndoTime();

        if(0 >= maxUndoTime) {
            return false;
        }

        return PersonalShareInfo.getInstance().getFirstClickUndoMessage(BaseApplicationLike.baseContext);
    }

    @NonNull
    public static String getUndoContent(Context context, PostTypeMessage message) {
        if(ParticipantType.App == message.mFromType) {
            return context.getString(R.string.tip_undo_message_from_service_app);
        }

        String content;
        String currentUser = LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext);
        if(currentUser.equals(message.from)) {
            content = context.getString(R.string.tip_undo_message, context.getString(R.string.you));

        } else {
            String name = ChatMessageHelper.getReadableNameShow(context, message);
            content = context.getString(R.string.tip_undo_message, name + " ");

        }
        return content;
    }

    public static void undoMessages(UndoEventMessage undoEventMessage) {
        UserHandleBasic chatUser = ChatMessageHelper.getChatUser(undoEventMessage);

        MessageCache.getInstance().undoMessage(undoEventMessage);
        ChatDaoService.getInstance().undoMessage(AtworkApplicationLike.baseContext, undoEventMessage);
        ChatDaoService.getInstance().undoNewsSummaryMessage(AtworkApplicationLike.baseContext, undoEventMessage);

        Session session = ChatSessionDataWrap.getInstance().getSession(chatUser.mUserId, null);

        if (null != session) {
            ChatSessionDataWrap.getInstance().updateSessionForEvent(session, undoEventMessage);
        }

        new Handler(Looper.getMainLooper()).post(()->{
            tryBreakDownload(undoEventMessage);
        });

        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();

        notifyUndoEvent(undoEventMessage);
    }

    private static void tryBreakDownload(UndoEventMessage undoEventMessage) {
        for(String msgId : undoEventMessage.mEnvIds) {
            MediaCenterNetManager.brokenDownloadingOrUploading(msgId);
        }
    }

    public static void notifyUndoEvent(UndoEventMessage undoEventMessage) {
        Intent intentUndo = new Intent(UndoMessageHelper.UNDO_MESSAGE_RECEIVED);
        intentUndo.putExtra(ChatDetailFragment.DATA_NEW_MESSAGE, undoEventMessage);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intentUndo);
    }


}
