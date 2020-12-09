package com.foreveross.atwork.infrastructure.newmessage;

/**
 * Created by lingen on 15/4/16.
 * Description:
 */
public class PongMessage extends HasTimestampResponse {
    @Override
    public int getMsgType() {
        return Message.PONG_TYPE;
    }
}
