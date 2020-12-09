package com.foreveross.atwork.im.sdk.receive;

import com.foreveross.atwork.im.sdk.protocol.Protocol;
import com.foreveross.atwork.im.sdk.socket.DecodeHandle;

/**
 * Created by lingen on 15/4/7.
 */
public class TypeDecodeHandle implements DecodeHandle {

    /**
     * 前4个字节是指明类型
     *
     * @param protocol
     * @return
     */
    public boolean decodeMessage(Protocol protocol)  throws Exception{
        //第一个字节前4位
        byte val = protocol.getContent()[0];
        int value = (val >>> 4) & 0x0F;
        protocol.setType(value);
        return true;
    }
}
