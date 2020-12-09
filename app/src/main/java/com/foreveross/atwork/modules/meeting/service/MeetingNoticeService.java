package com.foreveross.atwork.modules.meeting.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.foreverht.cache.MessageCache;
import com.foreverht.db.service.repository.MessageRepository;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.chat.MeetingNoticeChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.MeetingNotifyMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.UUID;

/**
 * Created by dasunsy on 2017/11/13.
 */

public class MeetingNoticeService {
    public static void receiveMeetingNotify(Context context, MeetingNotifyMessage meetingNotifyMessage, boolean isCameFromOnline) {
        MeetingNoticeChatMessage meetingNoticeChatMessage = MeetingNoticeChatMessage.newMessage(context, meetingNotifyMessage);
        meetingNoticeChatMessage.chatStatus = ChatStatus.Sended;

        //聊天界面交互
        ChatSessionDataWrap.getInstance().asyncReceiveMessage(meetingNoticeChatMessage, isCameFromOnline);


        if(isCameFromOnline) {
            //发出广播，通知聊天界面收到新消息
            ChatMessageHelper.notifyMessageReceived(meetingNoticeChatMessage);

            SessionRefreshHelper.notifyRefreshSession();
        }
    }


    public static void setMeetingNoticeAvatar(Session session, ImageView avatarView) {
        DisplayImageOptions displayImageOptions = ImageCacheHelper.getRoundOptions(R.mipmap.default_app, R.mipmap.default_app);

        if(StringUtils.isEmpty(session.avatar)) {
            ChatPostMessage chatPostMessage = MessageCache.getInstance().findMessage(session.identifier, session.lastMessageId);
            if(null != chatPostMessage) {
                ImageCacheHelper.displayImageByMediaId(chatPostMessage.mDisplayAvatar, avatarView, displayImageOptions);

            } else {
                setMeetingNoticeAvatarLoadDb(avatarView, session, displayImageOptions);
            }

        } else {
            ImageCacheHelper.displayImageByMediaId(session.avatar, avatarView, displayImageOptions);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static void setMeetingNoticeAvatarLoadDb(ImageView avatarView, Session session, DisplayImageOptions displayImageOptions) {
        final String tagHolding = UUID.randomUUID().toString();
        avatarView.setTag(tagHolding);

        new AsyncTask<Void, Void, ChatPostMessage>() {
            @Override
            protected ChatPostMessage doInBackground(Void... voids) {
                ChatPostMessage lastMsg = MessageRepository.getInstance().queryMessage(BaseApplicationLike.baseContext, session.identifier, session.lastMessageId);
                return lastMsg;
            }

            @Override
            protected void onPostExecute(ChatPostMessage lastMsg) {
                if(!(tagHolding.equals(avatarView.getTag()))) {
                    return;
                }

                if(null != lastMsg) {
                    ImageCacheHelper.displayImageByMediaId(lastMsg.mDisplayAvatar, avatarView, displayImageOptions);
                    session.avatar = lastMsg.mDisplayAvatar;
                } else {

                    ImageCacheHelper.setImageResource(avatarView, R.mipmap.default_app);
                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }
}
