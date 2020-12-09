package com.foreveross.atwork.modules.chat.util;

import com.foreverht.cache.MessageCache;
import com.foreverht.db.service.repository.MessageAppRepository;
import com.foreverht.db.service.repository.MessageRepository;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.newsSummary.NewsSummaryPostMessage;
import com.foreveross.atwork.infrastructure.model.user.UserHandleBasic;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.HideEventMessage;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.newsSummary.util.CheckUnReadUtil;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.MessagesRemoveHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2017/5/19.
 */

public class HideMessageHelper {

    public static void hideMessage(HideEventMessage hideEventMessage) {
        UserHandleBasic chatUser = ChatMessageHelper.getChatUser(hideEventMessage);
        String sessionId = chatUser.mUserId;

        List<NewsSummaryPostMessage> lists = MessageAppRepository.getInstance().queryMessagesByMsgId(BaseApplicationLike.baseContext,hideEventMessage.mEnvIds.get(0));
        doHideMessagesData(hideEventMessage, sessionId);
        if(lists.size() > 0) {
            ChatSessionDataWrap.getInstance().updateSessionForRemoveMsgs(Session.WORKPLUS_SUMMARY_HELPER, hideEventMessage.mEnvIds,true);
        }
        //刷新 session
        ChatSessionDataWrap.getInstance().updateSessionForRemoveMsgs(sessionId, hideEventMessage.mEnvIds,false);

        //通知聊天界面刷新
        MessagesRemoveHelper.notifyChatDetailUI(BaseApplicationLike.baseContext, (ArrayList<String>) hideEventMessage.mEnvIds);

        UndoMessageHelper.notifyUndoEvent(hideEventMessage);
    }

    private static void doHideMessagesData(HideEventMessage hideEventMessage, String sessionId) {
        MessageCache.getInstance().removeMessages(sessionId, hideEventMessage.mEnvIds);
        MessageRepository.getInstance().hideMessage(hideEventMessage);
        MessageAppRepository.getInstance().hideMessage(hideEventMessage);
        if(hideEventMessage.mEnvIds.size() > 0) {
            CheckUnReadUtil.Companion.CompareTimeById(sessionId, hideEventMessage.mEnvIds.get(0));
        }
    }


    public static void hideMessageList(List<ChatPostMessage> chatPostMessageList) {
        List<ChatPostMessage> msgListHidden  = new ArrayList<>();
        for (ChatPostMessage chatPostMessage : chatPostMessageList) {
            if(chatPostMessage.isHide()) {
                msgListHidden.add(chatPostMessage);
            }
        }

        chatPostMessageList.removeAll(msgListHidden);
    }

}
