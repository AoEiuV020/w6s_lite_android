package com.foreveross.atwork.infrastructure.newmessage.post.notify.conversation;

import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.Participant;

import java.util.Map;

public class ConversationSettings {

    public static final String CONVERSATION_SETTINGS = "conversation_settings";
    public static final String STICKY_ENABLED = "sticky_enabled";
    public static final String NOTIFY_ENABLED = "notify_enabled";
    public static final String MODIFY_TIME = "modify_time";
    public static final String LANGUAGE = "language";



    public Participant participant;
    public Boolean notifyEnabled = null;
    public Boolean stickyEnabled = null;
    public String translationEnabled = null;
    public long modifyTime;

    @Nullable
    public static ConversationSettings parseInfo(Map<String, Object> bodyMap) {
        if(bodyMap.containsKey(CONVERSATION_SETTINGS)) {
            ConversationSettings conversationSettings = new ConversationSettings();
            Map<String, Object> conversationSettingsMap = (Map<String, Object>) bodyMap.get(CONVERSATION_SETTINGS);
            conversationSettings.participant = Participant.parseInfo(conversationSettingsMap);

            if (conversationSettingsMap.containsKey(STICKY_ENABLED)) {
                conversationSettings.stickyEnabled = (Boolean) conversationSettingsMap.get(STICKY_ENABLED);
            }


            if (conversationSettingsMap.containsKey(NOTIFY_ENABLED)) {
                conversationSettings.notifyEnabled = (Boolean) conversationSettingsMap.get(NOTIFY_ENABLED);
            }

            if (conversationSettingsMap.containsKey(LANGUAGE)) {
                conversationSettings.translationEnabled = (String) conversationSettingsMap.get(LANGUAGE);
            }

            conversationSettings.modifyTime = ChatPostMessage.getLong(conversationSettingsMap, MODIFY_TIME);

            return conversationSettings;
        }

        return null;
    }
}
