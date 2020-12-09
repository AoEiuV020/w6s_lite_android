package com.foreveross.atwork.im.sdk.socket;

import com.foreveross.atwork.im.sdk.protocol.Protocol;

/**
 * Created by lingen on 15/4/7.
 * 接收消息的处理类
 */
public interface DecodeHandle {

    boolean decodeMessage(Protocol protocol) throws Exception;

}
