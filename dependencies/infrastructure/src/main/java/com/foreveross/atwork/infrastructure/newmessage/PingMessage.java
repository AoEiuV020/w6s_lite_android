package com.foreveross.atwork.infrastructure.newmessage;

/**
 * Created by lingen on 15/4/16.
 * Description:
 */
public class PingMessage extends NoBodyMessage {

    @Override
    public int getMsgType() {
        return Message.PING_TYPE;
    }

    @Override
    public String toString() {
        return "【消息】: 心跳Ping";
    }
}
