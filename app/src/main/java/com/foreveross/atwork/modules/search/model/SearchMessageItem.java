package com.foreveross.atwork.modules.search.model;

import android.content.Context;
import android.os.Parcel;

import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;

/**
 * Created by lingen on 15/5/11.
 * Description:
 */
public class SearchMessageItem implements ShowListItem {

    public Session session;

    public String msgId;

    public long msgTime;

    public String content;

    public ChatPostMessage message;

    @Override
    public String getTitle() {
        return session.name;
    }

    @Override
    public String getTitleI18n(Context context) {
        return getTitle();
    }

    @Override
    public String getTitlePinyin() {
        return null;
    }

    @Override
    public String getParticipantTitle() {
        return session.name;
    }

    @Override
    public String getInfo() {
        return content;
    }

    @Override
    public String getAvatar() {
        if (message == null) {
            return null;
        }
        return message.mDisplayAvatar;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getDomainId() {
        return null;
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public boolean isSelect() {
        return false;
    }

    @Override
    public void select(boolean isSelect) {

    }

    @Override
    public boolean isOnline() {
        return false;
    }

    public SearchMessageItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.session, flags);
        dest.writeString(this.msgId);
        dest.writeLong(this.msgTime);
        dest.writeString(this.content);
        dest.writeSerializable(this.message);
    }

    protected SearchMessageItem(Parcel in) {
        this.session = in.readParcelable(Session.class.getClassLoader());
        this.msgId = in.readString();
        this.msgTime = in.readLong();
        this.content = in.readString();
        this.message = (ChatPostMessage) in.readSerializable();
    }

    public static final Creator<SearchMessageItem> CREATOR = new Creator<SearchMessageItem>() {
        @Override
        public SearchMessageItem createFromParcel(Parcel source) {
            return new SearchMessageItem(source);
        }

        @Override
        public SearchMessageItem[] newArray(int size) {
            return new SearchMessageItem[size];
        }
    };
}
