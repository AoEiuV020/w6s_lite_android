package com.foreveross.atwork.inter;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;

/**
 * Created by lingen on 15/5/21.
 * Description:
 */
public interface ReSendListener {


    void reSendMessage(ChatPostMessage chatPostMessage);
}
