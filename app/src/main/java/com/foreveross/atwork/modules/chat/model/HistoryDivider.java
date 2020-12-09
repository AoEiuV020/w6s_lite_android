package com.foreveross.atwork.modules.chat.model;

import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;

import java.util.Map;
import java.util.UUID;


public class HistoryDivider extends ChatPostMessage {

    public HistoryDivider() {
        this.deliveryId = UUID.randomUUID().toString();
        this.deliveryTime = TimeUtil.getCurrentTimeInMillis();
        this.read = ReadStatus.AbsolutelyRead;
    }

    @Override
    public ChatType getChatType() {
        return ChatType.HistoryDivider;
    }

    @Override
    public String getSessionShowTitle() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getSearchAbleString() {
        return StringUtils.EMPTY;
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
        throw new UnsupportedOperationException();
    }
}
