package com.foreveross.atwork.infrastructure.model.chat;

import android.content.Context;

import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.BingNotifyMessage;

import java.util.Map;
import java.util.UUID;

/**
 * Created by dasunsy on 2017/9/17.
 */

public class BingConfirmChatMessage extends ChatPostMessage {

    public BingConfirmChatMessage() {
        deliveryId = UUID.randomUUID().toString();

        chatStatus = ChatStatus.Sended;
    }

    public static BingConfirmChatMessage newBingConfirmChatMessage(Context context, BingNotifyMessage bingNotifyMessage, String bingId, String bingDomainId, UserHandleInfo userHandleInfo) {
        BingConfirmChatMessage bingConfirmChatMessage = new BingConfirmChatMessage();
        //保证消息的唯一性
        bingConfirmChatMessage.deliveryId = bingNotifyMessage.mBingId + "_" + bingNotifyMessage.mOperator;

        bingConfirmChatMessage.deliveryTime = bingNotifyMessage.deliveryTime;

        bingConfirmChatMessage.to = bingId;
        bingConfirmChatMessage.mToDomain = bingDomainId;
        bingConfirmChatMessage.mToType = ParticipantType.Bing;

        bingConfirmChatMessage.from = userHandleInfo.mUserId;
        bingConfirmChatMessage.mFromDomain = userHandleInfo.mDomainId;
        bingConfirmChatMessage.mFromType = ParticipantType.User;

        bingConfirmChatMessage.mBodyType = BodyType.BingConfirm;

        bingConfirmChatMessage.read = bingNotifyMessage.read;

        if(User.isYou(context, bingConfirmChatMessage.from)) {
            bingConfirmChatMessage.chatSendType = ChatSendType.SENDER;
        } else {
            bingConfirmChatMessage.chatSendType = ChatSendType.RECEIVER;

        }


        return bingConfirmChatMessage;
    }

    @Override
    public ChatType getChatType() {
        return ChatType.BING_CONFIRM;
    }

    @Override
    public String getSessionShowTitle() {
        return null;
    }

    @Override
    public String getSearchAbleString() {
        return null;
    }

    @Override
    public boolean needNotify() {
        return false;
    }

    @Override
    public boolean needCount() {
        return false;
    }



    @Override
    public Map<String, Object> getChatBody() {
        return null;
    }
}
