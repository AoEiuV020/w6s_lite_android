package com.foreveross.atwork.utils;

import android.content.Context;

import com.foreverht.db.service.repository.MessageRepository;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;

/**
 * Created by dasunsy on 16/5/20.
 */
public class SystemMessageHelper {
    public static void newSystemMessageNotice(Context context, SystemChatMessage systemChatMessage) {
        MessageRepository.getInstance().insertOrUpdateMessage(context, systemChatMessage);

        SessionRefreshHelper.notifyRefreshSession(context);

        ChatMessageHelper.notifyMessageReceived(systemChatMessage);
    }
}
