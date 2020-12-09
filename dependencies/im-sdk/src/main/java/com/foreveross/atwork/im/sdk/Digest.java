package com.foreveross.atwork.im.sdk;

import com.foreveross.atwork.infrastructure.newmessage.Message;

/**
 * Created by lingen on 15/4/7.
 */
public interface Digest {

    /**
     * 获取消息体的摘要
     *
     * @return
     */
    byte[] digest(Message message);
}
