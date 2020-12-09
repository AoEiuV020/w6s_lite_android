package com.foreveross.atwork.api.sdk.voip.responseJson;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.MeetingStatus;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by dasunsy on 16/7/14.
 */
public class CreateOrQueryMeetingResponseJson extends BasicResponseJSON {

    @SerializedName("result")
    public Result mResult;

    public class Result {
        @SerializedName("id")
        public String mMeetingId;

        @SerializedName("domain_id")
        public String mDomainId;

        @SerializedName("conversation")
        public MeetingInfo mMeetingInfo;

        @SerializedName("gateway")
        public QsyConferenceInfo mQsyConferenceInfo;

        @SerializedName("members")
        public List<MeetingMember> meetingMemberList;

        @SerializedName("creator")
        public String mCreator;
    }


    public class QsyConferenceInfo {
        @SerializedName("type")
        public String mType;

        @SerializedName("id")
        public String mConferenceId;
    }



    public VoipMeetingGroup toVoipMeetingGroup(VoipType voipType) {
        VoipMeetingGroup voipMeetingGroup = new VoipMeetingGroup();

        voipMeetingGroup.mMeetingId = this.mResult.mMeetingId;

        String creatorUserId = this.mResult.mCreator.substring(0, this.mResult.mCreator.indexOf("@"));
        String creatorDomainId = mResult.mCreator.substring(this.mResult.mCreator.indexOf("@") + 1, this.mResult.mCreator.length());

        UserHandleInfo creator = new UserHandleInfo();
        creator.mUserId = creatorUserId;
        creator.mDomainId = creatorDomainId;

        voipMeetingGroup.mCreator = creator;

        voipMeetingGroup.mParticipantList = new CopyOnWriteArrayList<>();

        voipMeetingGroup.mParticipantList.addAll(toParticipantList());

        voipMeetingGroup.mStatus = MeetingStatus.FAILED;
        voipMeetingGroup.mMeetingInfo = this.mResult.mMeetingInfo;
        voipMeetingGroup.mVoipType = voipType;
        voipMeetingGroup.mCallTime = System.currentTimeMillis();


        return voipMeetingGroup;
    }

    public List<VoipMeetingMember> toParticipantList() {
        List<VoipMeetingMember> meetingMemberList = new ArrayList<>();

        for(MeetingMember member : this.mResult.meetingMemberList) {
            meetingMemberList.add(member.toVoipMeetingMember(this.mResult.mMeetingId));
        }

        return meetingMemberList;
    }
}
