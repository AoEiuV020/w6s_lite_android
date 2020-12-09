package com.foreveross.atwork.infrastructure.newmessage.post.notify.conversation;

import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;

public abstract class ConversationNotifyMessage extends NotifyPostMessage {

    public static String FROM = "CONVERSATION_HELPER";

    public enum Operation {
        SETTINGS_CHANGED,

        SETTINGS_RESET,

        UNKNOWN;

        public static Operation fromStringValue(String value) {
            if ("SETTINGS_CHANGED".equalsIgnoreCase(value)) {
                return SETTINGS_CHANGED;
            }

            if("SETTINGS_RESET".equalsIgnoreCase(value)) {
                return SETTINGS_RESET;
            }


            return UNKNOWN;
        }

    }
}
