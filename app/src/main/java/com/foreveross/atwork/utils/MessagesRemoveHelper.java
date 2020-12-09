package com.foreveross.atwork.utils;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.cache.MessageCache;
import com.foreverht.db.service.repository.MessageRepository;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ack.AckPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.service.ChatService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dasunsy on 2017/4/28.
 */

public class MessagesRemoveHelper {

    /**
     * 收到删除回执, 删除消息后通知界面
     * */
    public static void removeMessages(AckPostMessage ackMessage) {
        //0 为自己发出的, 不用处理
        if(0 == ackMessage.ackForward) {
            return;
        }

        String sessionId = ChatMessageHelper.getChatUser(ackMessage).mUserId;
        Context context = BaseApplicationLike.baseContext;

        removeFiles(context, ackMessage, sessionId);
        removeMessages(ackMessage, sessionId);

        if (!User.isYou(context, ackMessage.from)) {
            ChatService.sendRemovedReceiptsNotForwards(context, ackMessage);
        }

        //刷新 session
        ChatSessionDataWrap.getInstance().updateSessionForRemoveMsgs(sessionId, ackMessage.ackIds,false);

        //通知聊天界面刷新
        notifyChatDetailUI(context, (ArrayList<String>) ackMessage.ackIds);

    }

    public static void notifyChatDetailUI(Context context, ArrayList<String> removeIds) {
        Intent intent = new Intent(ChatDetailFragment.REMOVE_MESSAGE_SUCCESSFULLY);
        intent.putStringArrayListExtra(ChatDetailFragment.DATA_MSG_ID_LIST, removeIds);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private static void removeMessages(AckPostMessage ackMessage, String sessionId) {
        Map<String, List<String>> msgIdMap = new HashMap<>();
        msgIdMap.put(sessionId, ackMessage.ackIds);
        MessageCache.getInstance().removeMessages(sessionId, ackMessage.ackIds);
        MessageRepository.getInstance().batchRemoveMessage(msgIdMap);
    }

    private static void removeFiles(Context context, AckPostMessage ackMessage, String sessionId) {
        for(String msgRemovedId : ackMessage.ackIds) {
            ChatPostMessage chatPostMessage = MessageRepository.getInstance().queryMessage(context, sessionId, msgRemovedId);
            removeFile(context, chatPostMessage);

        }
    }

    /**
     * 删除消息对应的本地文件
     * */
    public static void removeFile(Context context, ChatPostMessage chatPostMessage) {
        if(chatPostMessage instanceof ImageChatMessage) {
            String imgOriginalPath = ImageShowHelper.getOriginalPath(BaseApplicationLike.baseContext, chatPostMessage.deliveryId);
            String imgThumbnailPath = ImageShowHelper.getThumbnailPath(BaseApplicationLike.baseContext, chatPostMessage.deliveryId);

            FileUtil.delete(imgOriginalPath);
            FileUtil.delete(imgThumbnailPath);

        } else if(chatPostMessage instanceof VoiceChatMessage) {
            String audioPath = VoiceChatMessage.getAudioPath(context, chatPostMessage.deliveryId);
            FileUtil.delete(audioPath);
        }
    }
}
