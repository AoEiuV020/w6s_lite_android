package com.foreveross.atwork.infrastructure.newmessage.post.notify;

import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;

import java.util.Map;

/**
 * Created by lingen on 15/6/29.
 * Description:
 */
public class P2PNotifyMessage extends NotifyPostMessage {


    public P2POperation operation;


    //群通知类开

    public static P2PNotifyMessage getP2PNotifyMessageFromJson(Map<String, Object> jsonMap) {
        P2PNotifyMessage p2PNotifyMessage = new P2PNotifyMessage();
        p2PNotifyMessage.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);
        p2PNotifyMessage.operation = P2POperation.fromStringValue((String) bodyMap.get(OPERATION));
        return p2PNotifyMessage;
    }

    public enum P2POperation {

        //REFRESH CONTACT
        REMOVE_CONTACT,

        NOTIFY_CHANGE,

        UNKNOWN;


        public static P2POperation fromStringValue(String operation) {
            if ("refresh_contact".equalsIgnoreCase(operation)) {
                return REMOVE_CONTACT;
            }else if ("user_profile_changed".equalsIgnoreCase(operation)){
                return NOTIFY_CHANGE;
            }
            return UNKNOWN;
        }
    }
}
