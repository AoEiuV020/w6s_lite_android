package com.foreveross.atwork.api.sdk.voip.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2016/10/20.
 */

public class InviteMembersResponseJson extends BasicResponseJSON {
    @SerializedName("result")
    public List<MeetingMember> mMemberList = new ArrayList<>();

    public List<VoipMeetingMember> toParticipantList(String meetingId) {
        List<VoipMeetingMember> meetingMemberList = new ArrayList<>();

        for (MeetingMember member : mMemberList) {
            meetingMemberList.add(member.toVoipMeetingMember(meetingId));
        }

        return meetingMemberList;
    }
}
