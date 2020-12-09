package com.foreveross.atwork.infrastructure.newmessage.post.notify;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;

import java.util.Map;

/**
 * 紧急呼消息的操作通知
 */
public class EmergencyNotifyMessage extends NotifyPostMessage {

    public static String FROM = "APP_HELPER";

    public static String APP_ID = "app_id";

    public Operation mOperation;
    public String mMsgIdConfirmed;
    public String mSourceId;


    public static EmergencyNotifyMessage getEmergencyNotifyMessageFromJson(Map<String, Object> jsonMap) {
        EmergencyNotifyMessage emergencyNotifyMessage = new EmergencyNotifyMessage();
        emergencyNotifyMessage.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        emergencyNotifyMessage.mOperation = Operation.fromStringValue(ChatPostMessage.getString(bodyMap, OPERATION));
        emergencyNotifyMessage.mMsgIdConfirmed = ChatPostMessage.getString(bodyMap, DELIVER_ID);
        emergencyNotifyMessage.mSourceId = ChatPostMessage.getString(bodyMap, APP_ID);

        return emergencyNotifyMessage;
    }


    public enum Operation {
        CONFIRMED,

        UNKNOWN;

        public static Operation fromStringValue(String value) {
            if ("CONFIRMED".equalsIgnoreCase(value)) {
                return CONFIRMED;
            }


            return UNKNOWN;
        }

    }

}
