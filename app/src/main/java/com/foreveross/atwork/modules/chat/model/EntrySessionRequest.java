package com.foreveross.atwork.modules.chat.model;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;

/**
 * Created by dasunsy on 2017/5/28.
 */

public class EntrySessionRequest {
    public SessionType mChatType;
    public String mName;
    public String mAvatar;
    public String mIdentifier;
    public String mDomainId;
    public String mOrgId;
    public ChatPostMessage mMessage;
    public boolean mRemoteTop;
    public boolean mUpdateDb = true;
    public boolean mUpdateLastMessageText = true;

    public static EntrySessionRequest newRequest() {
        return new EntrySessionRequest();
    }

    public static EntrySessionRequest newRequest(SessionType sessionType, ShowListItem contact) {
        EntrySessionRequest entrySessionRequest = newRequest()
                .setChatType(sessionType)
                .setName(contact.getTitleI18n(BaseApplicationLike.baseContext))
                .setIdentifier(contact.getId())
                .setDomainId(contact.getDomainId());


        if(contact instanceof AppBundles) {
            entrySessionRequest.setRemoteTop(((AppBundles)contact).mTop == 1);
            entrySessionRequest.setIdentifier(((AppBundles)contact).appId);
        }

        return entrySessionRequest;
    }

    public EntrySessionRequest setChatType(SessionType chatType) {
        this.mChatType = chatType;
        return this;
    }

    public EntrySessionRequest setName(String name) {
        this.mName = name;
        return this;
    }

    public EntrySessionRequest setAvatar(String avatar) {
        mAvatar = avatar;
        return this;
    }

    public EntrySessionRequest setIdentifier(String identifier) {
        this.mIdentifier = identifier;
        return this;
    }

    public EntrySessionRequest setDomainId(String domainId) {
        this.mDomainId = domainId;
        return this;
    }

    public EntrySessionRequest setOrgId(String orgId) {
        this.mOrgId = orgId;
        return this;
    }

    public EntrySessionRequest setMessage(ChatPostMessage message) {
        this.mMessage = message;
        return this;
    }

    public EntrySessionRequest setRemoteTop(boolean top) {
        this.mRemoteTop = top;
        return this;
    }

    public EntrySessionRequest setUpdateDb(boolean updateDb) {
        mUpdateDb = updateDb;
        return this;
    }
}

