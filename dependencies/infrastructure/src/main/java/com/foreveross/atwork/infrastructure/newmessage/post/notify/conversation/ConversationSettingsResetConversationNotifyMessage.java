package com.foreveross.atwork.infrastructure.newmessage.post.notify.conversation;

import java.util.Map;

public class ConversationSettingsResetConversationNotifyMessage extends ConversationNotifyMessage {


    public static ConversationSettingsResetConversationNotifyMessage getConversationSettingsResetConversationNotifyMessageFromJson(Map<String, Object> jsonMap) {

        ConversationSettingsResetConversationNotifyMessage notifyMessage = new ConversationSettingsResetConversationNotifyMessage();
        notifyMessage.pareInfo(jsonMap);

        return notifyMessage;
    }

    protected void pareInfo(Map<String, Object> jsonMap) {
        this.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

    }
}
