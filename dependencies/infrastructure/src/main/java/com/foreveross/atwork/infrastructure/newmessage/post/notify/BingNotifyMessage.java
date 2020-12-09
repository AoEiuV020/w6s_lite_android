package com.foreveross.atwork.infrastructure.newmessage.post.notify;

import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;

import java.util.Map;

/**
 * 必应消息操作通知(星标, 取消星标, 确认)
 */
public class BingNotifyMessage extends NotifyPostMessage {

    public static String FROM = "BING_HELPER";

    public Operation mOperation;
    public String mOperator;
    public String mDeviceId;
    public String mBingId;


    public static BingNotifyMessage getBingNotifyMessageFromJson(Map<String, Object> jsonMap) {
        BingNotifyMessage bingNotifyMessage = new BingNotifyMessage();
        bingNotifyMessage.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        bingNotifyMessage.mOperation = Operation.fromStringValue((String) bodyMap.get(OPERATION));

        bingNotifyMessage.mOperator = (String) bodyMap.get(OPERATOR);

        bingNotifyMessage.mDeviceId = (String) bodyMap.get(DEVICE_ID);

        bingNotifyMessage.mBingId = bingNotifyMessage.to;

        return bingNotifyMessage;
    }

    public static class Contact {
        public String mUserId;
        public String mDomainId;
        public String mName;
        public String mAvatar;
    }


    public enum Operation {
        COMPLETED,

        STICK,

        UN_STICK,

        UNKNOWN;

        public static Operation fromStringValue(String value) {
            if ("COMPLETED".equalsIgnoreCase(value)) {
                return COMPLETED;
            }

            if ("STICK".equalsIgnoreCase(value)) {
                return STICK;
            }

            if ("UN_STICK".equalsIgnoreCase(value)) {
                return UN_STICK;
            }


            return UNKNOWN;
        }

    }

}
