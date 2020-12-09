package com.foreveross.atwork.infrastructure.newmessage;

import java.io.Serializable;

/**
 * Created by lingen on 15/4/16.
 * Description:
 */
public abstract class Message implements Serializable{

    /**
     * unknown
     * */
    public static final int UNKNOWN_TYPE = 0;

    /**
     * 连接协议
     */
    public static final int CONNECT_TYPE = 1;
    /**
     * 服务器返回给客户端的确认协议
     */
    public static final int CONNECT_ACK_TYPE = 2;
    /**
     * 表明错误的认证情况或消息
     */
    public static final int CONNECT_RST_TYPE = 3;
    /**
     * 客户端发送的心跳
     */
    public static final int PING_TYPE = 4;
    /**
     * 服务器端回的心跳
     */
    public static final int PONG_TYPE = 5;
    /**
     * 聊天协议
     */
    public static final int POST_TYPE = 6;

    /**
     * 设备上线
     */
    public static final int DEVICE_ONLINE = 7;

    /**
     * 设备下线
     */
    public static final int DEVICE_OUTLINE = 8;

    /**
     * 用户正在输入中
     * */
    public static final int USER_TYPING = 9;

    /**
     * 退出协议
     */
    public static final int DISCONNECT_TYPE = 15;

    public static final String MESSAGE_TABLE_PRE = "message_";

    public static String getMessageTableName(String identifier) {
        return "'" + MESSAGE_TABLE_PRE + identifier + "'";
    }

    public abstract int getMsgType();

}
