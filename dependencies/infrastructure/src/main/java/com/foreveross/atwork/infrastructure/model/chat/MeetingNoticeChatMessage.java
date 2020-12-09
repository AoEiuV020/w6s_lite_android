package com.foreveross.atwork.infrastructure.model.chat;

import android.content.Context;

import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.DiscussionMeetingNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.MeetingNotifyMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.Expose;

import java.util.Map;

/**
 * Created by dasunsy on 2017/11/14.
 */

public class MeetingNoticeChatMessage extends ChatPostMessage {

    @Expose
    public MeetingNotifyMessage.Operation mOperation;

    @Expose
    public String mMeetingId;

    @Expose
    public MeetingNotifyMessage.Type mType;

    @Expose
    public UserHandleInfo mOperator;

    @Expose
    public UserHandleInfo mHost;

    @Expose
    public String mTitle = StringUtils.EMPTY;

    @Expose
    public long mMeetingTime;

    @Expose
    public String mMeetingParticipantsShow = StringUtils.EMPTY;

    @Expose
    public String mHostPassword = StringUtils.EMPTY;

    @Expose
    public String mHostJoinUrl = StringUtils.EMPTY;

    @Expose
    public String mAttendeePassword = StringUtils.EMPTY;

    @Expose
    public String mAttendeeJoinUrl = StringUtils.EMPTY;

    @Expose
    public String mUrl = StringUtils.EMPTY;

    @Expose
    public String mOperatorId = StringUtils.EMPTY;

    @Expose
    public String mOperationTitle = StringUtils.EMPTY;


    public static MeetingNoticeChatMessage newMessage(Context context, MeetingNotifyMessage meetingNotifyMessage) {
        MeetingNoticeChatMessage meetingNoticeChatMessage = new MeetingNoticeChatMessage();

        meetingNoticeChatMessage.mMeetingId = meetingNotifyMessage.mMeetingId;
        meetingNoticeChatMessage.mOperation = meetingNotifyMessage.mOperation;
        meetingNoticeChatMessage.mOperatorId = meetingNotifyMessage.mOperatorId;
        meetingNoticeChatMessage.mOperator = meetingNotifyMessage.mOperator;
        meetingNoticeChatMessage.mHost = meetingNotifyMessage.mHost;
        meetingNoticeChatMessage.mMeetingTime = meetingNotifyMessage.mMeetingTime;
        meetingNoticeChatMessage.mTitle = meetingNotifyMessage.mTitle;
        meetingNoticeChatMessage.mMeetingParticipantsShow = meetingNotifyMessage.mMeetingParticipantsShow;

        //保证消息的唯一性
        meetingNoticeChatMessage.deliveryId = meetingNotifyMessage.deliveryId;

        meetingNoticeChatMessage.deliveryTime = meetingNotifyMessage.deliveryTime;

        meetingNoticeChatMessage.to = meetingNotifyMessage.to;
        meetingNoticeChatMessage.mToDomain = meetingNotifyMessage.mToDomain;
        meetingNoticeChatMessage.mToType = meetingNotifyMessage.mToType;

        if(meetingNotifyMessage instanceof DiscussionMeetingNotifyMessage ){
            meetingNoticeChatMessage.from = meetingNotifyMessage.mOperator.mUserId;
            meetingNoticeChatMessage.mFromDomain = meetingNotifyMessage.mOperator.mDomainId;
            meetingNoticeChatMessage.mFromType = ParticipantType.User;

        } else {
            meetingNoticeChatMessage.from = Session.WORKPLUS_MEETING;
            meetingNoticeChatMessage.mFromDomain = meetingNotifyMessage.mFromDomain;
            meetingNoticeChatMessage.mFromType = meetingNotifyMessage.mFromType;
        }

        meetingNoticeChatMessage.mMyName = meetingNotifyMessage.mMyName;
        meetingNoticeChatMessage.mMyAvatar = meetingNotifyMessage.mMyAvatar;
        meetingNoticeChatMessage.mDisplayName = meetingNotifyMessage.mDisplayName;
        meetingNoticeChatMessage.mDisplayAvatar = meetingNotifyMessage.mDisplayAvatar;

        meetingNoticeChatMessage.mBodyType = BodyType.MeetingNotice;

        meetingNoticeChatMessage.read = meetingNotifyMessage.read;

        if(User.isYou(context, meetingNoticeChatMessage.from)) {
            meetingNoticeChatMessage.chatSendType = ChatSendType.SENDER;
        } else {
            meetingNoticeChatMessage.chatSendType = ChatSendType.RECEIVER;

        }

        meetingNoticeChatMessage.mType = meetingNotifyMessage.mType;
        meetingNoticeChatMessage.mHostJoinUrl = meetingNotifyMessage.mHostJoinUrl;
        meetingNoticeChatMessage.mHostPassword = meetingNotifyMessage.mHostPassword;
        meetingNoticeChatMessage.mAttendeeJoinUrl = meetingNotifyMessage.mAttendeeJoinUrl;
        meetingNoticeChatMessage.mAttendeePassword = meetingNotifyMessage.mAttendeePassword;
        meetingNoticeChatMessage.mUrl = meetingNotifyMessage.mUrl;
        meetingNoticeChatMessage.mOperationTitle = meetingNotifyMessage.mOperationTitle;
        meetingNoticeChatMessage.specialAction = meetingNotifyMessage.getSpecialAction();


        return meetingNoticeChatMessage;
    }

    public boolean isToDiscussion() {
        return ParticipantType.Discussion == mToType;
    }


    @Override
    public ChatType getChatType() {
        return ChatType.MEETING_NOTICE;
    }

    @Override
    public String getSessionShowTitle() {
        return mTitle;
    }

    @Override
    public String getSearchAbleString() {
        return null;
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
