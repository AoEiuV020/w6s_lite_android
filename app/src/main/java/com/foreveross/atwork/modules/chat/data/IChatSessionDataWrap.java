package com.foreveross.atwork.modules.chat.data;

import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;

public interface IChatSessionDataWrap {

    @Nullable
    Session buildP2pSession(ChatPostMessage chatPostMessage, boolean isCameFromOnline);

    @Nullable
    Session buildWorkplusAssetNotifySession(ChatPostMessage chatPostMessage);

    @Nullable
    Session buildDiscussionSession(final ChatPostMessage chatPostMessage, final boolean isCameFromOnline);

    @Nullable
    Session buildMeetingNotifySession(ChatPostMessage chatPostMessage);

    @Nullable
    Session buildNotifySystemSession(ChatPostMessage chatPostMessage);

    @Nullable
    Session buildAppSession(ChatPostMessage chatPostMessage);
}
