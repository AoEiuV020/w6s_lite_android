package com.foreveross.atwork.modules.chat.inter;

import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;

/**
 * Created by dasunsy on 2017/5/16.
 */
public interface OnMessageWrapListener {
    void onSuccess(PostTypeMessage postTypeMessage);
}
