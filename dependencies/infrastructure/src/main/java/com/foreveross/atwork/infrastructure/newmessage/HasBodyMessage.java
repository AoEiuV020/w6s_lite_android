package com.foreveross.atwork.infrastructure.newmessage;

import com.foreveross.atwork.infrastructure.support.AtworkConfig;

import java.util.Map;

/**
 * Created by lingen on 15/4/16.
 * Description:
 */
public abstract class HasBodyMessage extends Message {

    public abstract Map<String, Object> getMessageBody();

    public boolean encryptHandle() {
        return AtworkConfig.OPEN_IM_CONTENT_ENCRYPTION;
    }
}
