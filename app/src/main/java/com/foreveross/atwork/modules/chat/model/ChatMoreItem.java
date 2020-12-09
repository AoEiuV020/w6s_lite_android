package com.foreveross.atwork.modules.chat.model;

/**
 * Created by dasunsy on 2017/11/13.
 */

public class ChatMoreItem {

    public ChatMoreAction mChatMoreAction;

    public int mResId;

    public String mText;


    public boolean mBurnMode;

    public static ChatMoreItem newInstance() {
        return new ChatMoreItem();
    }

    public ChatMoreItem setChatMoreAction(ChatMoreAction chatMoreAction) {
        mChatMoreAction = chatMoreAction;
        return this;
    }

    public ChatMoreItem setResId(int resId) {
        mResId = resId;
        return this;
    }

    public ChatMoreItem setText(String text) {
        mText = text;
        return this;
    }

    public ChatMoreItem setBurnMode(boolean burnMode) {
        mBurnMode = burnMode;
        return this;
    }


    public enum ChatMoreAction {
        CAMERA,

        PHOTO,

        FILE,

        MICRO_VIDEO,

        LOCATION,

        VOIP,

        Meeting,

        CARD,

        BING
    }

}
