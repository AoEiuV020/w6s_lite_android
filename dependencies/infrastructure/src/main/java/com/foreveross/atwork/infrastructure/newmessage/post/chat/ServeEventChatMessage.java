package com.foreveross.atwork.infrastructure.newmessage.post.chat;

import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lingen on 15/6/3.
 * Description:
 */
public class ServeEventChatMessage extends ChatPostMessage {


    private static final String EVENT_TYPE = "event_type";

    public String eventTypeValue;

    public String mOrgId;


    public static ServeEventChatMessage newEventChatMessage(String eventTypeValue, BodyType bodyType, String from, String to, ParticipantType fromType, ParticipantType toType, String fromDomainId, String toDomainId, String orgId, String myAvatar, String myName, String displayAvatar, String displayName) {
        ServeEventChatMessage eventChatMessage = new ServeEventChatMessage();
        eventChatMessage.eventTypeValue = eventTypeValue;
        eventChatMessage.deliveryId = UUID.randomUUID().toString();
        eventChatMessage.deliveryTime = TimeUtil.getCurrentTimeInMillis();
        eventChatMessage.from = from;
        eventChatMessage.to = to;
        eventChatMessage.mToType = toType;
        eventChatMessage.mFromType = fromType;
        eventChatMessage.mBodyType = bodyType;
        eventChatMessage.mFromDomain = fromDomainId;
        eventChatMessage.mToDomain = toDomainId;
        eventChatMessage.mOrgId = orgId;
        eventChatMessage.chatSendType = ChatSendType.SENDER;
        eventChatMessage.chatStatus = ChatStatus.Sending;
        eventChatMessage.read = ReadStatus.AbsolutelyRead;
        eventChatMessage.mDisplayAvatar = displayAvatar;
        eventChatMessage.mDisplayName = displayName;
        eventChatMessage.mMyAvatar = myAvatar;
        eventChatMessage.mMyName = myName;
        return eventChatMessage;
    }

    @Override
    public ChatType getChatType() {
        return ChatType.Event;
    }

    @Override
    public String getSessionShowTitle() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getSearchAbleString() {
        return StringUtils.EMPTY;
    }

    @Override
    public boolean needNotify() {
        return false;
    }

    @Override
    public boolean needCount() {
        return false;
    }



    @Override
    public Map<String, Object> getChatBody() {
        Map<String, Object> chatBody = new HashMap<>();
        chatBody.put(EVENT_TYPE, eventTypeValue);
        chatBody.put(DISPLAY_AVATAR, mDisplayAvatar);
        chatBody.put(DISPLAY_NAME, mDisplayName);
        chatBody.put(MY_AVATAR, mMyAvatar);
        chatBody.put(MY_NAME, mMyName);

        if (!StringUtils.isEmpty(mOrgId)) {
            chatBody.put(ORG_ID, mOrgId);
        }
        return chatBody;
    }
}
