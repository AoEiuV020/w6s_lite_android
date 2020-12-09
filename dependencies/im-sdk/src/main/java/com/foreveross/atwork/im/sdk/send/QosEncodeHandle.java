package com.foreveross.atwork.im.sdk.send;

import com.foreveross.atwork.im.sdk.protocol.Protocol;
import com.foreveross.atwork.im.sdk.socket.EncodeHandle;

/**
 * Created by lingen on 15/4/7.
 * 处理POS
 */
public class QosEncodeHandle implements EncodeHandle {


    @Override
    public Protocol encode(Protocol protocol) {
        protocol.setQos(0);
        return protocol;
    }
}
