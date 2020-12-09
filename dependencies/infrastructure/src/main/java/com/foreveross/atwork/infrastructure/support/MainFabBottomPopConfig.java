package com.foreveross.atwork.infrastructure.support;

/**
 * Created by dasunsy on 2018/4/1.
 */

public class MainFabBottomPopConfig {

    private boolean mContactItemInChat = false;
    private boolean mFriendItemInChat = true;

    public boolean isContactItemInChat() {
        return mContactItemInChat;
    }

    public void setContactItemInChat(boolean contactItemInChat) {
        mContactItemInChat = contactItemInChat;
    }

    public boolean isFriendItemInChat() {
        return mFriendItemInChat;
    }

    public void setFriendItemInChat(boolean friendItemInChat) {
        mFriendItemInChat = friendItemInChat;
    }
}
