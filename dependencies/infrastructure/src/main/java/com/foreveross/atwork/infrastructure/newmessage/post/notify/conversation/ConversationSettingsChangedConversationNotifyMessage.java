package com.foreveross.atwork.infrastructure.newmessage.post.notify.conversation;

import java.util.Map;

public class ConversationSettingsChangedConversationNotifyMessage extends ConversationNotifyMessage {

    public ConversationSettings conversationSettings;

    public static ConversationSettingsChangedConversationNotifyMessage getConversationSettingsChangedConversationNotifyMessageFromJson(Map<String, Object> jsonMap) {

        ConversationSettingsChangedConversationNotifyMessage notifyMessage = new ConversationSettingsChangedConversationNotifyMessage();
        notifyMessage.pareInfo(jsonMap);

        return notifyMessage;
    }

    protected void pareInfo(Map<String, Object> jsonMap) {
        this.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);
        conversationSettings = ConversationSettings.parseInfo(bodyMap);

    }
}
