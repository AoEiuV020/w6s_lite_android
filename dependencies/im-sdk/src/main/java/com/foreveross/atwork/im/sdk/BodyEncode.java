package com.foreveross.atwork.im.sdk;

import com.foreveross.atwork.infrastructure.newmessage.HasBodyMessage;

/**
 * Created by lingen on 15/4/7.
 */
public interface BodyEncode {

    /**
     * 获取消息体BYTE的算法
     *
     * @return
     */
    byte[] getBody(HasBodyMessage message);

}
