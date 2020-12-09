package com.foreveross.atwork.modules.chat.component;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;

import java.util.List;

public interface ChatDetailItemDataRefresh {

    void refreshItemView(ChatPostMessage message);

    void refreshMessagesContext(List<ChatPostMessage> messages);

    String getMsgId();

}
