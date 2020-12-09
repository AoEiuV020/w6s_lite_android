package com.foreveross.atwork.infrastructure.newmessage.post;

import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lingen on 15/4/16.
 * Description:
 */
public class SystemPostMessage extends PostTypeMessage {

    private static final String TYPE = "type";

    private static final String OPERATION = "operation";

    private static final String OPERATION_APP = "operation_app";

    private static final String DESCRIPTION = "description";

    public String type;

    public Operation operation;

    public String operationAppId;

    public String desc = "";

    public static SystemPostMessage getSystemPostMessageFromJson(Map<String, Object> jsonMap) {
        SystemPostMessage systemPostMessage = new SystemPostMessage();
        systemPostMessage.initPostTypeMessageValue(jsonMap);

        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);
        systemPostMessage.type = (String) bodyMap.get(TYPE);
        systemPostMessage.operation = Operation.fromStringValue((String) bodyMap.get(OPERATION));
        systemPostMessage.operationAppId = (String) bodyMap.get(OPERATION_APP);
        return systemPostMessage;
    }

    public static SystemPostMessage generateUnknownMessageFromJson(Map<String, Object> jsonMap) {
        SystemPostMessage systemPostMessage = new SystemPostMessage();
        systemPostMessage.initPostTypeMessageValue(jsonMap);

        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);
        systemPostMessage.type = (String) bodyMap.get(TYPE);
        systemPostMessage.desc = (String) bodyMap.get(DESCRIPTION);
        systemPostMessage.operation = Operation.fromStringValue((String) bodyMap.get(OPERATION));
        systemPostMessage.operationAppId = (String) bodyMap.get(OPERATION_APP);
        return systemPostMessage;
    }

    @Override
    public Map<String, Object> getChatBody() {
        return new HashMap<>();
    }


    public enum Operation {


        /**
         * app不在范围, 应该做删除处理
         */
        APP_NOT_FOUND,

        /**
         * 未知类型
         * */
        UNKNOWN;


        public static Operation fromStringValue(String value) {

            if ("APP_NOT_FOUND".equalsIgnoreCase(value)) {
                return APP_NOT_FOUND;
            }
            return UNKNOWN;
        }
    }

}
