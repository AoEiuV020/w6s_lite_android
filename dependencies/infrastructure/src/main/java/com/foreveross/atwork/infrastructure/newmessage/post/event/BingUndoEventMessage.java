package com.foreveross.atwork.infrastructure.newmessage.post.event;

import android.content.Context;

import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;

import java.util.Map;

/**
 * Created by dasunsy on 2017/9/14.
 */

public class BingUndoEventMessage extends UndoEventMessage {

    @Override
    public EventType getEventType() {
        return EventType.BingUndo;
    }

    public static BingUndoEventMessage newUndoEventMessage(Context context, String to, String toDomain, String envId) {
        BingUndoEventMessage undoEventMessage = new BingUndoEventMessage();
        LoginUserBasic meInfo = LoginUserInfo.getInstance().getLoginUserBasic(context);

        undoEventMessage.from = meInfo.mUserId;
        undoEventMessage.mFromDomain = meInfo.mDomainId;
        undoEventMessage.to = to;

        undoEventMessage.mMyAvatar = meInfo.mAvatar;
        undoEventMessage.mMyName = meInfo.getShowName();

        undoEventMessage.chatSendType = ChatSendType.SENDER;
        undoEventMessage.chatStatus = ChatStatus.Sending;
        undoEventMessage.mEnvIds.add(envId);
        undoEventMessage.mBodyType = BodyType.Event;
        undoEventMessage.mFromType = ParticipantType.User;
        undoEventMessage.mToType = ParticipantType.Bing;
        undoEventMessage.mToDomain = toDomain;

        return undoEventMessage;
    }


    public static BingUndoEventMessage getUndoEventMessageFromJson(Map<String, Object> jsonMap) {
        BingUndoEventMessage undoEventMessage = new BingUndoEventMessage();
        undoEventMessage.makeUndoEventMessage(jsonMap);
        return undoEventMessage;
    }
}
