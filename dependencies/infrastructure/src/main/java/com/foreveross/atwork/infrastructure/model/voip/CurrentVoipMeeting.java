package com.foreveross.atwork.infrastructure.model.voip;

import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;

/**
 * Created by dasunsy on 16/9/21.
 */
public class CurrentVoipMeeting {
    public String mWorkplusVoipMeetingId;

    public String mWorkplusVoipMeetingDomainId;

    public MeetingInfo mMeetingInfo;

    public VoipType mVoipType;

    /**
     * 在zoom voip 使用, 后面可统一
     * */
    public UserHandleInfo mCallInviter = null;

    public VoipMeetingMember mCallSelf = null;

    public VoipMeetingMember mCallPeer = null;

    public VoipMeetingGroup mMeetingGroup = null;

    public CallParams mCallParams = null;

    /**
     * 当前大屏幕的用户 id
     * */
    public int mCurrentBigVideoUid = -1;

    public long mCallDuration;


    public String mJoinToken = null;
}
