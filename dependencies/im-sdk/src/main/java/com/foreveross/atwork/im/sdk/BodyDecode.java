package com.foreveross.atwork.im.sdk;

import com.foreveross.atwork.infrastructure.newmessage.HasBodyMessage;

/**
 * Created by lingen on 15/4/7.
 */
public interface BodyDecode {

    HasBodyMessage getMessage(int type, byte[] bytes);

}
