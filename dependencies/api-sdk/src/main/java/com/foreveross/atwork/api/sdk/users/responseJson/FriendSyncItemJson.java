package com.foreveross.atwork.api.sdk.users.responseJson;/**
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


import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.model.user.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户好友返回json集合处理
 * Created by reyzhang22 on 16/4/9.
 */
public class FriendSyncItemJson implements Parcelable {

    @SerializedName("id")
    public String mId;

    @SerializedName("friend")
    public FriendInfo mFriendInfo;

    public static class FriendInfo implements Parcelable {
        @SerializedName("domain_id")
        public String mDomainId;

        @SerializedName("user_id")
        public String mUserId;

        @SerializedName("name")
        public String mName;

        @SerializedName("pinyin")
        public String mPinyin;

        @SerializedName("initial")
        public String mInitial;

        @SerializedName("first_letter")
        public String mFirstLetter;

        @SerializedName("phone")
        public String mPhone;

        @SerializedName("avatar")
        public String mAvatar;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mDomainId);
            dest.writeString(this.mUserId);
            dest.writeString(this.mName);
            dest.writeString(this.mPinyin);
            dest.writeString(this.mInitial);
            dest.writeString(this.mFirstLetter);
            dest.writeString(this.mPhone);
            dest.writeString(this.mAvatar);
        }

        public FriendInfo() {
        }

        protected FriendInfo(Parcel in) {
            this.mDomainId = in.readString();
            this.mUserId = in.readString();
            this.mName = in.readString();
            this.mPinyin = in.readString();
            this.mInitial = in.readString();
            this.mFirstLetter = in.readString();
            this.mPhone = in.readString();
            this.mAvatar = in.readString();
        }

        public static final Creator<FriendInfo> CREATOR = new Creator<FriendInfo>() {
            @Override
            public FriendInfo createFromParcel(Parcel source) {
                return new FriendInfo(source);
            }

            @Override
            public FriendInfo[] newArray(int size) {
                return new FriendInfo[size];
            }
        };
    }

    @SerializedName("status")
    public String mStatus;

    @SerializedName("create_time")
    public long mCreateTime;

    @SerializedName("modify_time")
    public long mModifyTime;

    public static List<User> toUserList(List<FriendSyncItemJson> friendInfoList) {
        List<User> userList = new ArrayList<>();

        for(FriendSyncItemJson friendSyncItemJson : friendInfoList) {
            User user = new User();
            user.mUserId = friendSyncItemJson.mFriendInfo.mUserId;
            user.mDomainId = friendSyncItemJson.mFriendInfo.mDomainId;
            user.mAvatar = friendSyncItemJson.mFriendInfo.mAvatar;
            user.mPinyin = friendSyncItemJson.mFriendInfo.mPinyin;
            user.mInitial = friendSyncItemJson.mFriendInfo.mInitial;
            user.mFirstLetter = friendSyncItemJson.mFriendInfo.mFirstLetter;
            user.mName = friendSyncItemJson.mFriendInfo.mName;
            user.mPhone = friendSyncItemJson.mFriendInfo.mPhone;
            user.mStatus = friendSyncItemJson.mStatus;

            userList.add(user);
        }

        return userList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeParcelable(this.mFriendInfo, flags);
        dest.writeString(this.mStatus);
        dest.writeLong(this.mCreateTime);
        dest.writeLong(this.mModifyTime);
    }

    public FriendSyncItemJson() {
    }

    protected FriendSyncItemJson(Parcel in) {
        this.mId = in.readString();
        this.mFriendInfo = in.readParcelable(FriendInfo.class.getClassLoader());
        this.mStatus = in.readString();
        this.mCreateTime = in.readLong();
        this.mModifyTime = in.readLong();
    }

    public static final Creator<FriendSyncItemJson> CREATOR = new Creator<FriendSyncItemJson>() {
        @Override
        public FriendSyncItemJson createFromParcel(Parcel source) {
            return new FriendSyncItemJson(source);
        }

        @Override
        public FriendSyncItemJson[] newArray(int size) {
            return new FriendSyncItemJson[size];
        }
    };
}
