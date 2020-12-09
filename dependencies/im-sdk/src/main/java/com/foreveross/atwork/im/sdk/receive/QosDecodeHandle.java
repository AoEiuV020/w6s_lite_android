package com.foreveross.atwork.im.sdk.receive;

import com.foreveross.atwork.im.sdk.protocol.Protocol;
import com.foreveross.atwork.im.sdk.socket.DecodeHandle;

/**
 * Created by lingen on 15/4/7.
 */
public class QosDecodeHandle implements DecodeHandle {

    public boolean decodeMessage(Protocol protocol) throws Exception{
        //第一个字节前4位
        byte val = protocol.getContent()[0];
        int value = val & 0x0F;
        protocol.setQos(value);
        return true;
    }
}
