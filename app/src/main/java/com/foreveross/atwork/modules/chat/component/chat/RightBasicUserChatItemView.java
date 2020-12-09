package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.util.AttributeSet;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.inter.ReSendListener;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.inter.HasResendListener;
import com.foreveross.atwork.utils.AvatarHelper;

/**
 * Created by dasunsy on 2017/8/30.
 */

public abstract class RightBasicUserChatItemView extends BasicUserChatItemView implements HasResendListener {

    private ReSendListener mReSendListener;

    protected abstract ChatSendStatusView getChatSendStatusView();


    public RightBasicUserChatItemView(Context context) {
        super(context);
    }

    public RightBasicUserChatItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void refreshAvatar(ChatPostMessage message) {
        //显示头像
        AvatarHelper.setUserAvatarById(getAvatarView(), LoginUserInfo.getInstance().getLoginUserId(getContext()), LoginUserInfo.getInstance().getLoginUserDomainId(getContext()), false, true);
    }

    @Override
    public void setReSendListener(ReSendListener reSendListener) {
        this.mReSendListener = reSendListener;
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);

        handleChatSendStatusView(message);
    }

    private void handleChatSendStatusView(ChatPostMessage message) {
        ChatSendStatusView chatSendStatusView = getChatSendStatusView();
        if(null == chatSendStatusView) {
            return;
        }

        chatSendStatusView.setChatPostMessage(message);
        chatSendStatusView.setReSendListener(mReSendListener);
    }

}
