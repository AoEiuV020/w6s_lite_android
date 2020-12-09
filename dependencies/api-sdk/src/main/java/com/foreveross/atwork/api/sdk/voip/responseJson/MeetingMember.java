package com.foreveross.atwork.api.sdk.voip.responseJson;

import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMemberSettingInfo;
import com.foreveross.atwork.infrastructure.newmessage.post.voip.GateWay;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.VoipSdkType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2016/10/20.
 */

public class MeetingMember {
    @SerializedName("id")
    public String mId;

    @SerializedName("status")
    public String mStatus;

    @SerializedName("user")
    public UserHandleInfo mUser;

    @SerializedName("gateway")
    public GateWay mGateWay;

    @SerializedName("refresh_time")
    public long mRefreshTime = -1;

    @SerializedName("settings")
    public VoipMeetingMemberSettingInfo mVoipMeetingMemberSettingInfo;

    public VoipMeetingMember toVoipMeetingMember(String meetingId) {
        VoipMeetingMember voipMeetingMember = new VoipMeetingMember();
        voipMeetingMember.mUserId = mUser.mUserId;
        voipMeetingMember.mDomainId = mUser.mDomainId;
        voipMeetingMember.mAvatar = mUser.mAvatar;
        voipMeetingMember.mShowName = mUser.mShowName;

        voipMeetingMember.mMeetingId = meetingId;
        voipMeetingMember.mMeetingStatus = this.mStatus;
        voipMeetingMember.mGateWay = this.mGateWay;
        voipMeetingMember.mRefreshTime = this.mRefreshTime;
        voipMeetingMember.mVoipMeetingMemberSettingInfo = this.mVoipMeetingMemberSettingInfo;

        if(VoipSdkType.QSY != AtworkConfig.VOIP_SDK_TYPE) {
            voipMeetingMember.refreshUserStatus();
        }

        return voipMeetingMember;
    }
}
