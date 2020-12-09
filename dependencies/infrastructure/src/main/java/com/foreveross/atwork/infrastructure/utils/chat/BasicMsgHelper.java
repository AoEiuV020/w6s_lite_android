package com.foreveross.atwork.infrastructure.utils.chat;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.user.UserHandleBasic;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.VoipPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ack.AckPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;

/**
 * Created by dasunsy on 2017/7/25.
 */

public class BasicMsgHelper {

    public static String getChatUserSessionId(PostTypeMessage postTypeMessage) {
        if(null == postTypeMessage) {
            return StringUtils.EMPTY;
        }

        if(postTypeMessage instanceof AckPostMessage) {
            return ((AckPostMessage) postTypeMessage).getSessionChatId(BaseApplicationLike.baseContext);
        }

        if(postTypeMessage instanceof NotifyPostMessage) {
            return postTypeMessage.to;
        }

        if(postTypeMessage instanceof VoipPostMessage) {
            return ((VoipPostMessage) postTypeMessage).getSessionChatId();
        }

        if(postTypeMessage instanceof UndoEventMessage) {
            return ((UndoEventMessage) postTypeMessage).getSessionChatId();
        }


        return getChatUser(postTypeMessage).mUserId;
    }

    public static UserHandleBasic getChatUser(PostTypeMessage postTypeMessage) {
        UserHandleBasic user = new UserHandleBasic();
        if (LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext).equalsIgnoreCase(postTypeMessage.from)
                || (ParticipantType.Discussion.equals(postTypeMessage.mToType) && !BodyType.Notice.equals(postTypeMessage.mBodyType))) {
            user.mUserId = postTypeMessage.to;
            user.mDomainId = postTypeMessage.mToDomain;
            return user;
        }
        user.mUserId = postTypeMessage.from;
        user.mDomainId = postTypeMessage.mFromDomain;
        return user;
    }



    public static UserHandleInfo getChatUserInfo(PostTypeMessage postTypeMessage) {
        UserHandleInfo user = new UserHandleInfo();
        if (LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext).equalsIgnoreCase(postTypeMessage.from)
                || (ParticipantType.Discussion.equals(postTypeMessage.mToType) && !BodyType.Notice.equals(postTypeMessage.mBodyType))) {
            user.mUserId = postTypeMessage.to;
            user.mDomainId = postTypeMessage.mToDomain;
            user.mShowName = postTypeMessage.mDisplayName;
            user.mAvatar = postTypeMessage.mDisplayAvatar;
            return user;
        }


        user.mUserId = postTypeMessage.from;
        user.mDomainId = postTypeMessage.mFromDomain;
        user.mShowName = postTypeMessage.mMyName;
        user.mAvatar = postTypeMessage.mMyAvatar;

        return user;
    }


    public static List<ChatPostMessage> filterExpiredMsg(Collection<ChatPostMessage> msgList) {

        List<ChatPostMessage> msgsFilter = new ArrayList<>();
        long messagePullLatestTime = DomainSettingsManager.getInstance().getMessagePullLatestTime();
        long firstUnReadTime = findFirstUnReadTime(msgList);

        for(ChatPostMessage msg: msgList) {
             if(isMsgLegal(messagePullLatestTime, firstUnReadTime, msg)) {
                 msgsFilter.add(msg);
             }
        }

        return msgsFilter;
    }

    public static long findFirstUnReadTime(Collection<ChatPostMessage> msgList) {

        Collection<ChatPostMessage> unreadMsgList = CollectionsKt.filter(msgList, new Function1<ChatPostMessage, Boolean>() {
            @Override
            public Boolean invoke(ChatPostMessage chatPostMessage) {
                return isRealUnRead(chatPostMessage);
            }
        });

        ChatPostMessage firstUnreadMsg = CollectionsKt.minBy(unreadMsgList, new Function1<ChatPostMessage, Comparable>() {
            @Override
            public Comparable invoke(ChatPostMessage chatPostMessage) {
                return chatPostMessage.deliveryTime;
            }
        });

        if(null != firstUnreadMsg) {
            return firstUnreadMsg.deliveryTime;
        }

        return -1;
    }

    @NonNull
    public static boolean isMsgLegal(long messagePullLatestTime, long firstUnreadTime, PostTypeMessage msg) {
        if(-1 == messagePullLatestTime) {
            return true;
        }

        if(-1 != firstUnreadTime && firstUnreadTime <= msg.deliveryTime) {
            return true;
        }


        if(messagePullLatestTime <= msg.deliveryTime) {
            return true;
        }

        return false;
    }

    public static boolean isRealUnRead(ChatPostMessage chatPostMessage) {
        if(chatPostMessage.isUndo()) {
            return false;
        }

        if(chatPostMessage.isHide()) {
            return false;
        }

        if(BasicMsgHelper.isSender(chatPostMessage)) {
            return false;
        }

        if(ReadStatus.Unread != chatPostMessage.read) {
            return false;
        }

        return true;
    }


    public static boolean isReceiver(PostTypeMessage message){
        return (ChatSendType.RECEIVER.equals(message.chatSendType) || !isFromSelf(message));
    }

    public static boolean isSender(PostTypeMessage message){
        return (ChatSendType.SENDER.equals(message.chatSendType) || isFromSelf(message));
    }

    private static boolean isFromSelf(PostTypeMessage message){
        return !TextUtils.isEmpty(message.from) && message.from.equalsIgnoreCase(LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext));
    }


}
