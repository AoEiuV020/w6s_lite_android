package com.foreveross.atwork.infrastructure.model.umeeting;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/11/10.
 */

public class UmeetingStartRequest {

    private static final int USER_TYPE_API_USER = 99;
    private static final int USER_TYPE_ZOOM = 100;
    private static final int USER_TYPE_SSO = 101;

    public Context mContext;

    @SerializedName("user_id")
    public String mUserId;

    @SerializedName("zoom_token")
    public String mZoomToken;

    @SerializedName("user_type")
    public int mUserType = USER_TYPE_API_USER;

    @SerializedName("meeting_id")
    public String mMeetingNo;


    @SerializedName("display_name")
    public String mDisplayName;

    public UmeetingStartRequest setContext(Context context) {
        mContext = context;
        return this;
    }

    public UmeetingStartRequest setUserId(String userId) {
        mUserId = userId;
        return this;
    }

    public UmeetingStartRequest setZoomToken(String zoomToken) {
        mZoomToken = zoomToken;
        return this;
    }

    public UmeetingStartRequest setUserType(int userType) {
        mUserType = userType;
        return this;
    }

    public UmeetingStartRequest setDisplayName(String displayName) {
        mDisplayName = displayName;
        return this;
    }

    public UmeetingStartRequest setMeetingNo(String meetingNo) {
        mMeetingNo = meetingNo;
        return this;
    }
}
