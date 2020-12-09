package com.foreveross.atwork.infrastructure.model.umeeting;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/11/10.
 */

public class UmeetingJoinRequest {

    public Context mContext;

    @SerializedName("meeting_id")
    public String mMeetingNo;

    @SerializedName("display_name")
    public String mDisplayName;

    @SerializedName("meeting_password")
    public String mMeetingPassword;

    public static UmeetingJoinRequest newInstance() {
        return new UmeetingJoinRequest();
    }

    public UmeetingJoinRequest setContext(Context context) {
        mContext = context;
        return this;
    }

    public UmeetingJoinRequest setMeetingNo(String meetingNo) {
        mMeetingNo = meetingNo;
        return this;
    }

    public UmeetingJoinRequest setDisplayName(String displayName) {
        mDisplayName = displayName;
        return this;
    }

    public UmeetingJoinRequest setMeetingPassword(String meetingPassword) {
        mMeetingPassword = meetingPassword;
        return this;
    }
}
