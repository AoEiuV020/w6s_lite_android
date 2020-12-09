package com.foreveross.atwork.infrastructure.model.chat;

import android.content.Context;

import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.UUID;

/**
 * Created by lingen on 15/5/11.
 * Description:
 */
public class SystemChatMessage extends ChatPostMessage {

    @Expose
    @SerializedName("content")
    public String content;

    @Expose
    public int type = -1;

    /**
     * 系统消息的发起者
     * */
    public String addressor = StringUtils.EMPTY;

    @Expose
    public String orgLogo;

    public SystemChatMessage() {
        this.deliveryId = UUID.randomUUID().toString();
        this.deliveryTime = TimeUtil.getCurrentTimeInMillis();
    }

    public SystemChatMessage(String content, int type) {
        this.deliveryId = UUID.randomUUID().toString();
        this.deliveryTime = TimeUtil.getCurrentTimeInMillis();
        this.content = content;
        this.chatSendType = ChatSendType.RECEIVER;
        this.chatStatus = ChatStatus.Sended;
        this.type = type;
    }

    /**
     * 需要注意群通知类型的话 to 是 discussionId
     * */
    public SystemChatMessage(int type, String content, String from, String to, String fromDomain, String toDomain) {
        this.type = type;
        this.deliveryId = UUID.randomUUID().toString();
        this.deliveryTime = TimeUtil.getCurrentTimeInMillis();
        this.content = content;
        this.mBodyType = BodyType.System;
        this.from = from;
        this.mToType = ParticipantType.Discussion;
        this.to = to;
        this.chatSendType = ChatSendType.RECEIVER;
        this.chatStatus = ChatStatus.Sended;
        this.mFromDomain = fromDomain;
        this.mToDomain = toDomain;
//        this.read = true;
    }



    @Override
    public ChatType getChatType() {
        return ChatType.System;
    }

    @Override
    public String getSessionShowTitle() {
        return content;
    }

    @Override
    public String getSearchAbleString() {
        return StringUtils.EMPTY;
    }

    @Override
    public boolean needNotify() {
        if(Type.FILE_DOWNLOAD == type){
            return true;
        }

        return false;
    }

    @Override
    public boolean needCount() {
        if(Type.NOTIFY_DISCUSSION == type || Type.DROPBOX_IS_GOING_OVERDUE == type) {
            return false;
        }


        return true;
    }


    @Override
    public Map<String, Object> getChatBody() {
        throw new UnsupportedOperationException();
    }

    /**
     * 是否自己是发起者
     * */
    public boolean isYouAddresser(Context context) {
        return LoginUserInfo.getInstance().getLoginUserId(context).equals(this.addressor);
    }

    public final class Type {

        public final static int TIME_SHOW = 0;

        public final static int NOT_INITIALIZE_TIP = 2;

        public final static int NOTIFY_DISCUSSION = 1;

        public final static int NOTIFY_FRIEND = 3;

        public final static int NOTIFY_ORG = 4;

        public final static int NOTIFY_VOIP = 5;

        public final static int MSG_EXPIRED = 6;

        public final static int FILE_DOWNLOAD = 7;

        public final static int NOTIFY_DISCUSSION_REMOVE_HANDLE = 8;

        public final static int DROPBOX_IS_GOING_OVERDUE = 9;

        public final static int UNKNOWN = 100;

    }

}
