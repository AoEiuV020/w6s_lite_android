package com.foreveross.atwork.infrastructure.model.discussion;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.content.Context;
import android.os.Parcel;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by reyzhang22 on 16/4/5.
 */
public class DiscussionMember implements ShowListItem {

    @SerializedName("discussion_id")
    public String discussionId;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("domain_id")
    public String domainId;


    @SerializedName("join_time")
    public String joinTime;

    @SerializedName("name")
    public String name;

    @SerializedName("avatar")
    public String avatar;

    @SerializedName("tags")
    public List<String> tags;

    public boolean select;

    public boolean online = false;

    public DiscussionMember() {

    }

    public DiscussionMember(String discussionId, String userId, String domainId) {
        this.discussionId = discussionId;
        this.userId = userId;
        this.domainId = domainId;
        this.joinTime = Long.toString(TimeUtil.getCurrentTimeInMillis());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscussionMember that = (DiscussionMember) o;
        return discussionId.equals(that.discussionId) &&
                userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(discussionId, userId);
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getTitleI18n(Context context) {
        return null;
    }

    @Override
    public String getTitlePinyin() {
        return null;
    }

    @Override
    public String getParticipantTitle() {
        return name;
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
    public String getId() {
        return userId;
    }

    @Override
    public String getDomainId() {
        return domainId;
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public boolean isSelect() {
        return select;
    }

    @Override
    public void select(boolean isSelect) {
        select = isSelect;
    }

    @Override
    public boolean isOnline() {
        return online;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.discussionId);
        dest.writeString(this.userId);
        dest.writeString(this.domainId);
        dest.writeString(this.joinTime);
        dest.writeString(this.name);
        dest.writeString(this.avatar);
        dest.writeStringList(this.tags);
        dest.writeByte(this.select ? (byte) 1 : (byte) 0);
        dest.writeByte(this.online ? (byte) 1 : (byte) 0);
    }

    protected DiscussionMember(Parcel in) {
        this.discussionId = in.readString();
        this.userId = in.readString();
        this.domainId = in.readString();
        this.joinTime = in.readString();
        this.name = in.readString();
        this.avatar = in.readString();
        this.tags = in.createStringArrayList();
        this.select = in.readByte() != 0;
        this.online = in.readByte() != 0;
    }

    public static final Creator<DiscussionMember> CREATOR = new Creator<DiscussionMember>() {
        @Override
        public DiscussionMember createFromParcel(Parcel source) {
            return new DiscussionMember(source);
        }

        @Override
        public DiscussionMember[] newArray(int size) {
            return new DiscussionMember[size];
        }
    };
}
