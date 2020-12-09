package com.foreveross.atwork.im.sdk.send;

import com.foreveross.atwork.im.sdk.BodyEncode;
import com.foreveross.atwork.im.sdk.protocol.Protocol;
import com.foreveross.atwork.im.sdk.socket.EncodeHandle;
import com.foreveross.atwork.infrastructure.newmessage.HasBodyMessage;

/**
 * Created by lingen on 15/4/7.
 */
public class BodyEncodeHandle implements EncodeHandle {

    private BodyEncode bodyEncode;

    public BodyEncodeHandle(BodyEncode bodyEncode) {
        this.bodyEncode = bodyEncode;
    }

    @Override
    public Protocol encode(Protocol protocol) {
        if(protocol.getMessage() instanceof HasBodyMessage){
            byte[] body = bodyEncode.getBody((HasBodyMessage) protocol.getMessage());
            protocol.setBody(body);
        }
        return protocol;
    }
}
