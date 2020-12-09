package com.foreveross.atwork.infrastructure.newmessage.post.event;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.EventPostMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dasunsy on 16/2/3.
 */
public class UndoEventMessage extends EventPostMessage {
    public static final String EVENT_TYPE = "event_type";

    protected static final String EVENT_ID = "event_id";

    public List<String> mEnvIds = new ArrayList<>();


    public UndoEventMessage() {
        deliveryTime = TimeUtil.getCurrentTimeInMillis();
        deliveryId = UUID.randomUUID().toString();
    }



    public static UndoEventMessage newUndoEventMessage(ShowListItem sender, ShowListItem showItem, String to, String envId, ParticipantType fromType, ParticipantType toType, String toDomain, BodyType bodyType) {
        UndoEventMessage undoEventMessage = new UndoEventMessage();
        undoEventMessage.from = sender.getId();
        undoEventMessage.mFromDomain = sender.getDomainId();
        undoEventMessage.to = to;
        undoEventMessage.mFromType = fromType;
        undoEventMessage.mToType = toType;

        if(null != showItem) {
            undoEventMessage.mDisplayAvatar = showItem.getAvatar();
            undoEventMessage.mDisplayName = showItem.getTitle();
            undoEventMessage.conversationId = to;
            undoEventMessage.conversationDomain = toDomain;
            undoEventMessage.conversationType = toType;
        }

        undoEventMessage.mMyAvatar = sender.getAvatar();
        undoEventMessage.mMyName = sender.getTitle();

        undoEventMessage.chatSendType = ChatSendType.SENDER;
        undoEventMessage.chatStatus = ChatStatus.Sending;
        undoEventMessage.mEnvIds.add(envId);
        undoEventMessage.mBodyType = bodyType;
        undoEventMessage.mToDomain = toDomain;



        return undoEventMessage;
    }

    /**
     * 指定消息类型是否被撤销了
     */
    public boolean isMsgUndo(String msgId) {
        return mEnvIds.contains(msgId);
    }


    @Override
    public EventType getEventType() {
        return EventType.Undo;
    }

    @Override
    public Map<String, Object> getChatBody() {
        Map<String, Object> chatBody = new HashMap<>();
        chatBody.put(EVENT_TYPE, getEventType().toString());
        chatBody.put(EVENT_ID, mEnvIds.get(0));
        if (!StringUtils.isEmpty(conversationId)) {
            chatBody.put(CONVERSATION_ID, conversationId);
        }

        if (!StringUtils.isEmpty(conversationDomain)) {
            chatBody.put(CONVERSATION_DOMAIN, conversationDomain);
        }

        if (null != conversationType) {
            chatBody.put(CONVERSATION_TYPE, conversationType.stringValue());
        }
        setBasicChatBody(chatBody);
        return chatBody;
    }

    public static UndoEventMessage getUndoEventMessageFromJson(Map<String, Object> jsonMap) {
        UndoEventMessage undoEventMessage = new UndoEventMessage();
        undoEventMessage.makeUndoEventMessage(jsonMap);

        return undoEventMessage;
    }

    protected void makeUndoEventMessage(Map<String, Object> jsonMap) {
        initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        initChatTypeMessageValue(bodyMap);
        initEventPostMessageValue(bodyMap);


        String envIdStr = ((String) bodyMap.get(EVENT_ID));
        String[] envIdArray = envIdStr.split(",");
        mEnvIds.addAll(Arrays.asList(envIdArray));
    }
}
