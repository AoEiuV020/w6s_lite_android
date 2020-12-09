package com.foreveross.atwork.infrastructure.newmessage.post;

import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.utils.LongUtil;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;


public class CmdPostMessage extends PostTypeMessage {

    private static final String OPERATION = "operation";
    private static final String EMPLOYEE_HANDLED = "recipient";
    private static final String ORG_CODE = "org_code";
    private static final String INTERVAL = "interval";

    public Operation operation;
    public EmployeeHandled employeeHandled;
    public String orgCode;
    public long intervalBegin = -1;
    public long intervalEnd = -1;


    public static CmdPostMessage getCmdPostMessageFromJson(Map<String, Object> jsonMap) {
        CmdPostMessage cmdPostMessage = new CmdPostMessage();
        cmdPostMessage.initPostTypeMessageValue(jsonMap);

        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);
        cmdPostMessage.operation = Operation.fromStringValue((String) bodyMap.get(OPERATION));

        if(Operation.EMPLOYEE_FIRE.equals(cmdPostMessage.operation)) {
            cmdPostMessage.employeeHandled = new Gson().fromJson(String.valueOf(bodyMap.get(EMPLOYEE_HANDLED)), EmployeeHandled.class);
            cmdPostMessage.orgCode = String.valueOf(bodyMap.get(ORG_CODE));
        }


        if(Operation.UPLOAD_LOG == cmdPostMessage.operation) {
            checkParseUploadCmd(cmdPostMessage, bodyMap);
        }

        return cmdPostMessage;
    }

    private static void checkParseUploadCmd(CmdPostMessage cmdPostMessage, Map<String, Object> bodyMap) {
        if(bodyMap.containsKey(INTERVAL)) {
            String interval = ChatPostMessage.getString(bodyMap, INTERVAL);
            String intervalArray[] = interval.split("-");
            try {
                cmdPostMessage.intervalBegin = LongUtil.parseLong(intervalArray[0]);
                cmdPostMessage.intervalEnd = LongUtil.parseLong(intervalArray[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public Map<String, Object> getChatBody() {
        return new HashMap<>();
    }

    public class EmployeeHandled {
        @SerializedName("user_id")
        public String mUserId;

        @SerializedName("domain_id")
        public String mDomainId;

        public String name;
    }


    public enum Operation {

        /**
         * 上传日志
         * */
        UPLOAD_LOG,

        /**
         * 被终端修改了密码，比如后台或者PC端
         */
        RESET_CREDENTIALS,

        /**
         * 踢人
         */
        KICK,

        /**
         * 后台删除人(旧版本的需求, 3.0 暂时不用)
         * @param value
         * @return
         */
        USER_REMOVED,

        /**
         *  后台删除雇员
         * */
        EMPLOYEE_FIRE,

        /**
         *  设备被禁止
         * */
        DEVICE_FORBIDDEN,

        /**
         * 未知类型
         * */
        UNKNOWN;


        public static Operation fromStringValue(String value) {
            if ("KICK".equalsIgnoreCase(value)) {
                return KICK;
            }

            if ("reset_credentials".equalsIgnoreCase(value)) {
                return RESET_CREDENTIALS;
            }

            if ("user_removed".equalsIgnoreCase(value)) {
                return USER_REMOVED;
            }

            if("employee_fire".equalsIgnoreCase(value)) {
                return EMPLOYEE_FIRE;
            }

            if("device_forbidden".equalsIgnoreCase(value)) {
                return DEVICE_FORBIDDEN;
            }

            if("upload_log".equalsIgnoreCase(value)) {
                return UPLOAD_LOG;
            }

            return UNKNOWN;
        }
    }

}
