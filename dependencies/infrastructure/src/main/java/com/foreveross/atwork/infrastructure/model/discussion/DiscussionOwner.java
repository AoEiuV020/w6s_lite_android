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
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * 群组拥有者
 * Created by reyzhang22 on 16/3/28.
 */
public class DiscussionOwner implements Parcelable {

    @SerializedName("domain_id")
    public String mDomainId;

    @SerializedName("join_time")
    public long mJoinTime;

    @SerializedName("modify_time")
    public long mModifyTime;

    @SerializedName("user_id")
    public String mUserId;

    @SerializedName("more_settings")
    public MoreSettings mMoreSettings;


    public void refresh(@NonNull ShowListItem contact) {
        mUserId = contact.getId();
        mDomainId = contact.getDomainId();
    }

    public void refresh(@NonNull UserHandleInfo contact) {
        mUserId = contact.mUserId;
        mDomainId = contact.mDomainId;
    }

    @Nullable
    public static DiscussionOwner build(String owner) {
        if(!StringUtils.isEmpty(owner)) {
            DiscussionOwner discussionOwner = new DiscussionOwner();
            String[] owners = owner.split("@");
            discussionOwner.mDomainId = owners[1];
            discussionOwner.mUserId = owners[0];

            return discussionOwner;
        }

        return null;
    }

    public boolean isYouOwner(Context context) {
        return User.isYou(context, mUserId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDomainId);
        dest.writeLong(this.mJoinTime);
        dest.writeLong(this.mModifyTime);
        dest.writeString(this.mUserId);
        dest.writeParcelable(this.mMoreSettings, flags);
    }

    public DiscussionOwner() {
    }

    protected DiscussionOwner(Parcel in) {
        this.mDomainId = in.readString();
        this.mJoinTime = in.readLong();
        this.mModifyTime = in.readLong();
        this.mUserId = in.readString();
        this.mMoreSettings = in.readParcelable(MoreSettings.class.getClassLoader());
    }

    public static final Parcelable.Creator<DiscussionOwner> CREATOR = new Parcelable.Creator<DiscussionOwner>() {
        @Override
        public DiscussionOwner createFromParcel(Parcel source) {
            return new DiscussionOwner(source);
        }

        @Override
        public DiscussionOwner[] newArray(int size) {
            return new DiscussionOwner[size];
        }
    };
}
