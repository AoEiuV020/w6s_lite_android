package com.foreveross.atwork.infrastructure.newmessage.post.notify.user;

import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class ChatSettings {

    public final static String CHAT_SETTINGS = "chat_settings";
    public final static String CHAT_ASSISTANT_ENABLED = "chat_assistant_enabled";

    @SerializedName("chat_assistant_enabled")
    public boolean chatAssistantEnabled = false;


    @Nullable
    protected static ChatSettings pareInfo(Map<String, Object> bodyMap) {

        if(bodyMap.containsKey(CHAT_SETTINGS)) {
            ChatSettings chatSettings = new ChatSettings();
            chatSettings.chatAssistantEnabled = ChatPostMessage.getBoolean((Map<String, Object>) bodyMap.get(CHAT_SETTINGS), CHAT_ASSISTANT_ENABLED);
            return chatSettings;
        }

        return null;
    }
}
