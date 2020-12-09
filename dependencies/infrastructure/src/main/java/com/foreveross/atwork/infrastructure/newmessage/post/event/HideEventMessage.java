package com.foreveross.atwork.infrastructure.newmessage.post.event;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by dasunsy on 2017/5/19.
 */

public class HideEventMessage extends UndoEventMessage {

    public static UndoEventMessage getHideEventMessageFromJson(Map<String, Object> jsonMap) {
        HideEventMessage hideEventMessage = new HideEventMessage();
        hideEventMessage.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        hideEventMessage.initChatTypeMessageValue(bodyMap);

        String envIdStr = ((String) bodyMap.get(EVENT_ID));
        String[] envIdArray = envIdStr.split(",");
        hideEventMessage.mEnvIds.addAll(Arrays.asList(envIdArray));

        return hideEventMessage;
    }

    @Override
    public EventType getEventType() {
        return EventType.Remove;
    }
}
