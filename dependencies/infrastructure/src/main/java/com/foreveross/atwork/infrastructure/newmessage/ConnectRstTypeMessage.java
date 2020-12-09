package com.foreveross.atwork.infrastructure.newmessage;

/**
 * Created by lingen on 15/4/16.
 * Description:
 */
public class ConnectRstTypeMessage extends NoBodyMessage {
    @Override
    public int getMsgType() {
        return Message.CONNECT_RST_TYPE;
    }
}
