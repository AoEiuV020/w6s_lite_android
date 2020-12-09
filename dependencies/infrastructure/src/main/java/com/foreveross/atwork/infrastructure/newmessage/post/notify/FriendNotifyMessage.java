package com.foreveross.atwork.infrastructure.newmessage.post.notify;

import com.foreveross.atwork.infrastructure.newmessage.Participator;
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;

import java.util.Map;

/**
 * Created by dasunsy on 16/5/20.
 */
public class FriendNotifyMessage extends NotifyPostMessage {
    public static String FROM = "FRIENDSHIP_HELPER";
    public String mDeviceId;
    public Operation mOperation;

    public Participator mAddresser; //发送者
    public Participator mOperator; //篡改者



    public static FriendNotifyMessage getFriendNotifyMessageFromJson(Map<String, Object> jsonMap) {
        FriendNotifyMessage friendNotifyMessage = new FriendNotifyMessage();
        friendNotifyMessage.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        friendNotifyMessage.mAddresser = Participator.getParticipator(bodyMap.get("addresser"));
        if(bodyMap.containsKey(OPERATOR)) {
            friendNotifyMessage.mOperator = Participator.getParticipator(bodyMap.get(OPERATOR));

        } else if(bodyMap.containsKey("recipient")){ //兼容处理
            friendNotifyMessage.mOperator = Participator.getParticipator(bodyMap.get("recipient"));


        }

        friendNotifyMessage.mOperation = Operation.fromStringValue((String) bodyMap.get(OPERATION));
        friendNotifyMessage.mDeviceId = (String) bodyMap.get(DEVICE_ID);

        return friendNotifyMessage;
    }


    public enum Operation {
        APPLYING,
        APPROVED,
        REJECTED,
        REMOVED,
        UNKNOWN;


        public static Operation fromStringValue(String value) {
            if ("APPLYING".equalsIgnoreCase(value)) {
                return APPLYING;
            }

            if ("APPROVED".equalsIgnoreCase(value)) {
                return APPROVED;
            }

            if ("REMOVED".equalsIgnoreCase(value)) {
                return REMOVED;
            }

            if ("REJECTED".equalsIgnoreCase(value)) {
                return REJECTED;
            }

            return UNKNOWN;
        }

    }

}
