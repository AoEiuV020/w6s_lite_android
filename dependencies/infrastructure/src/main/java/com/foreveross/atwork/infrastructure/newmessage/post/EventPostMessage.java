package com.foreveross.atwork.infrastructure.newmessage.post;

import android.content.Context;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import java.util.Map;


/**
 * Created by dasunsy on 16/2/3.
 */
public abstract class EventPostMessage extends PostTypeMessage {

    public static final String CONVERSATION_ID =  "conversation_id";
    public static final String CONVERSATION_TYPE = "conversation_type";
    public static final String CONVERSATION_DOMAIN = "conversation_domain";

    public String conversationId;
    public ParticipantType conversationType;
    public String conversationDomain;

    public abstract EventType getEventType();

    public String getSessionChatId() {
        if(!StringUtils.isEmpty(conversationId)) {
            return conversationId;
        }

        return getSessionChatIdFromFromOrTo(BaseApplicationLike.baseContext);
    }

    public String getSessionChatIdFromFromOrTo(Context context) {
        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        if(meUserId.equals(from)) {
            return to;
        }

        return from;
    }

    protected void initEventPostMessageValue(Map<String, Object> jsonMap) {
        conversationId = ChatPostMessage.getString(jsonMap, CONVERSATION_ID);
    }

    @Override
    public boolean equals(Object o) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        if (o == null || o instanceof EventPostMessage == false) {
            return false;
        }
        EventPostMessage other = (EventPostMessage) o;
        if (StringUtils.isEmpty(deliveryId) == false) {
            return deliveryId.equals(other.deliveryId);
        }
        return false;
    }

    public enum EventType {
        BingUndo {
            @Override
            public String toString() {
                return "bing_undo";
            }
        },

        //撤销消息
        Undo {
            @Override
            public String toString() {
                return "undo";
            }
        },
        //目前后台消息审计发送该消息删除消息(因以后涉及还原的问题, 所以我们视为隐藏的概念)
        Remove {
            @Override
            public String toString() {
                return "remove";
            }
        },

        Unknown {
            @Override
            public String toString() {
                return "unknown";
            }
        };


        public static EventType fromStringValue(String value) {
            if ("undo".equalsIgnoreCase(value)) {
                return Undo;

            } else if ("remove".equalsIgnoreCase(value)) {
                return Remove;

            } else if("bing_undo".equalsIgnoreCase(value)) {
                return BingUndo;
            }

            return Unknown;
        }

        public abstract String toString();
    }
}
