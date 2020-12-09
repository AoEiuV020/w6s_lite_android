package com.foreveross.atwork.infrastructure.newmessage.messageEnum;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


/**
 * 参与类型枚举
 * Created by reyzhang22 on 16/4/13.
 */
public enum ParticipantType {

    /**
     * 应用
     */
    App {
        @Override
        public String stringValue() {
            return APP;
        }
    },
    /**
     * 群组
     */
    Discussion {
        @Override
        public String stringValue() {
            return DISCUSSION;
        }
    },
    /**
     * 会议
     */
    Meeting {
        @Override
        public String stringValue() {
            return MEETING;
        }
    },
    /**
     * 聊天室
     */
    Room {
        @Override
        public String stringValue() {
            return ROOM;
        }
    },
    /**
     * 用户
     */
    User {
        @Override
        public String stringValue() {
            return USER;
        }
    },
    /**
     * 系统
     */
    System {
        @Override
        public String stringValue() {
            return SYSTEM;
        }
    },

    /**
     * 必应消息
     * */
    Bing {
        @Override
        public String stringValue() {
            return BING;
        }
    },
    /**
     * 未知
     */
    Unknown {
        @Override
        public String stringValue() {
            return UNKNOWN;
        }
    };

    public abstract String stringValue();

    public static ParticipantType toParticipantType(String type) {
        if(null == type) {
            return Unknown;
        }

        if (type.equalsIgnoreCase(APP)) {
            return App;
        }
        if (type.equalsIgnoreCase(DISCUSSION)) {
            return Discussion;
        }
        if (type.equalsIgnoreCase(MEETING)) {
            return Meeting;
        }
        if (type.equalsIgnoreCase(ROOM)) {
            return Room;
        }
        if (type.equalsIgnoreCase(USER)) {
            return User;
        }

        if (type.equalsIgnoreCase(BING)) {
            return Bing;
        }

        if (type.equalsIgnoreCase(SYSTEM)) {
            return System;
        }
        return Unknown;
    }

    public static final String APP = "APP";
    public static final String DISCUSSION = "DISCUSSION";
    public static final String MEETING = "MEETING";
    public static final String ROOM = "ROOM";
    public static final String USER = "USER";
    public static final String SYSTEM = "SYSTEM";
    public static final String BING = "BING";
    public static final String UNKNOWN = "UNKNOWN";
}
