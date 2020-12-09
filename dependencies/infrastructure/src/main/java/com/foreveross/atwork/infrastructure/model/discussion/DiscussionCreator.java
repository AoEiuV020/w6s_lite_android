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
 *
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 群组创建者
 * Created by reyzhang22 on 16/3/28.
 */
public class DiscussionCreator implements Parcelable {

    @SerializedName("domain_id")
    public String mDomainId;

    @SerializedName("join_time")
    public String mJoinTime;

    @SerializedName("modify_time")
    public String mModifyTime;

    @SerializedName("more_settings")
    public MoreSettings mMoreSetting;

    @SerializedName("user_id")
    public String mUserId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDomainId);
        dest.writeString(this.mJoinTime);
        dest.writeString(this.mModifyTime);
        dest.writeParcelable(this.mMoreSetting, flags);
        dest.writeString(this.mUserId);
    }

    public DiscussionCreator() {
    }

    protected DiscussionCreator(Parcel in) {
        this.mDomainId = in.readString();
        this.mJoinTime = in.readString();
        this.mModifyTime = in.readString();
        this.mMoreSetting = in.readParcelable(MoreSettings.class.getClassLoader());
        this.mUserId = in.readString();
    }

    public static final Creator<DiscussionCreator> CREATOR = new Creator<DiscussionCreator>() {
        @Override
        public DiscussionCreator createFromParcel(Parcel source) {
            return new DiscussionCreator(source);
        }

        @Override
        public DiscussionCreator[] newArray(int size) {
            return new DiscussionCreator[size];
        }
    };
}
