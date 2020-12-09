package com.foreveross.atwork.im.sdk.send;

import com.foreveross.atwork.im.sdk.protocol.Protocol;
import com.foreveross.atwork.im.sdk.socket.EncodeHandle;
import com.foreveross.atwork.infrastructure.newmessage.Message;

/**
 * Created by lingen on 15/4/7.
 * 处理消息的type
 */
public class TypeEncodeHandle implements EncodeHandle {

    /**
     * @param protocol
     * @return
     */
    @Override
    public Protocol encode(Protocol protocol) {
        Message message = protocol.getMessage();
        protocol.setType(message.getMsgType());
        return protocol;
    }

}
