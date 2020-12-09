package com.foreveross.atwork.infrastructure.newmessage.post.chat;

import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.Expose;

import java.util.Map;

/**
 * Created by dasunsy on 2017/8/31.
 */

public class UnknownChatMessage extends ChatPostMessage {

    @Expose
    public String mSourceMsg;

    @Expose
    public String mRealBodyType;

    /**
     * 生成无法解析的消息
     *
     * @return
     */
    @Nullable
    public static UnknownChatMessage generaUnknownMessage(Map<String, Object> jsonMap, String sourceMsg) {
        UnknownChatMessage unknownChatMessage = new UnknownChatMessage();
        unknownChatMessage.initPostTypeMessageValue(jsonMap);
        unknownChatMessage.mBodyType = BodyType.UnKnown;

        if(ParticipantType.User == unknownChatMessage.mFromType || ParticipantType.App == unknownChatMessage.mFromType) {
            unknownChatMessage.mSourceMsg = sourceMsg;
            unknownChatMessage.mRealBodyType = (String) jsonMap.get(BODY_TYPE);
            return unknownChatMessage;
        }

        return null;
    }


    @Override
    public ChatType getChatType() {
        return ChatType.UNKNOWN;
    }

    @Override
    public String getSessionShowTitle() {
        return StringConstants.SESSION_CONTENT_UNKNOWN_MESSAGE;
    }

    @Override
    public String getSearchAbleString() {
        return StringUtils.EMPTY;
    }

    @Override
    public boolean needNotify() {
        return true;
    }

    @Override
    public boolean needCount() {
        return true;
    }



    @Override
    public Map<String, Object> getChatBody() {
        return null;
    }
}
