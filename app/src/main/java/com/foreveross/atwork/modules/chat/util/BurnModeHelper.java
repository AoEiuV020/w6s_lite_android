package com.foreveross.atwork.modules.chat.util;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.services.receivers.AtworkReceiveListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2017/4/14.
 */

public class BurnModeHelper {
    public static boolean isBurnMode() {
        return DomainSettingsManager.getInstance().handleEphemeronSettingsFeature() && ChatDetailActivity.sIsBurnMode;
    }

    public static String getSessionContent(ChatPostMessage chatPostMessage) {

        if(User.isYou(BaseApplicationLike.baseContext, chatPostMessage.from)) {
            return BaseApplicationLike.baseContext.getString(R.string.session_txt_send_new_msg);
        } else {
            return BaseApplicationLike.baseContext.getString(R.string.session_txt_receive_new_msg);

        }
    }

    public static boolean isMsgExpired(ChatPostMessage chatPostMessage) {
        return chatPostMessage.isBurn() && !isPongTimeNoPower() && chatPostMessage.isExpired();
    }

    public static boolean isPongTimeNoPower() {
        return Math.abs(TimeUtil.getCurrentTimeInMillis() - AtworkReceiveListener.lastPongTimes) > 1000 * 60 * 10;
    }

    /**
     * 过滤掉超时的消息, 并做删除操作
     *
     * @param chatPostMessageList
     * @return tempSystemTipMsgList 返回临时的系统通知(不做存储)
     * */
    public static List<ChatPostMessage> checkMsgExpiredAndRemove(List<ChatPostMessage> chatPostMessageList, boolean removeLocal) {

        List<ChatPostMessage> tempSystemTipMsgList = new ArrayList<>();
        List<ChatPostMessage> msgExpiredList = new ArrayList<>();
        if (!isPongTimeNoPower()) {
            for(ChatPostMessage chatPostMessage : chatPostMessageList) {
                if(chatPostMessage.isBurn() && !chatPostMessage.isUndo()) {

                    if(chatPostMessage.isExpired()) {
                        msgExpiredList.add(chatPostMessage);

                        SystemChatMessage systemChatMessage = SystemChatMessageHelper.createMessageByExpiredMsg(chatPostMessage);
                        tempSystemTipMsgList.add(systemChatMessage);

                    }
                }
            }

            if (removeLocal) {
                ChatDaoService.getInstance().batchRemoveMessages(msgExpiredList, null);
            }

            chatPostMessageList.removeAll(msgExpiredList);
        }

        return tempSystemTipMsgList;
    }

    public static void checkMsgExpired(List<ChatPostMessage> chatPostMessageList) {
        if (!isPongTimeNoPower()) {
            List<ChatPostMessage> msgExpiredList = new ArrayList<>();

            for(ChatPostMessage chatPostMessage : chatPostMessageList) {
                if(chatPostMessage.isBurn() && !chatPostMessage.isUndo()) {

                    if(chatPostMessage.isExpired()) {
                        msgExpiredList.add(chatPostMessage);

                    }
                }
            }

            chatPostMessageList.removeAll(msgExpiredList);
        }

    }

}
