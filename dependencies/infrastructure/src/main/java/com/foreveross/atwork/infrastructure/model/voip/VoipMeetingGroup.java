package com.foreveross.atwork.infrastructure.model.voip;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class VoipMeetingGroup implements Parcelable, Comparable {

    /**
     * 会议室id
     */
    public String mMeetingId;

    /**
     * 会议名称
     */
    public String mName;

    /**
     * 会议头像
     */
    public String mAvatar;


    /**
     * 会议发起者
     */
    public UserHandleInfo mCreator;

    /**
     * 会话类型
     */
    public MeetingInfo mMeetingInfo;

    /**
     * 会议呼叫时间
     */
    public long mCallTime;

    /**
     * 会议的通话时长
     */
    public long mDuration;

    /**
     * 会议通话状态
     */
    public MeetingStatus mStatus;

    /**
     * 初始会议类型
     * */
    public VoipType mVoipType;

    /**
     * 是否需要使用(如显示在 voip 历史列表)
     * */
    public boolean mEnable = true;

    /**
     * 参会者
     */
    public CopyOnWriteArrayList<VoipMeetingMember> mParticipantList;

    public VoipMeetingGroup() {
        this.mName = "";
        this.mAvatar = null;
        this.mParticipantList = new CopyOnWriteArrayList<>();
    }

    /**
     * 2个人时, 获取对方
     * */
    @Nullable
    public VoipMeetingMember getPeer(Context context) {
        for(VoipMeetingMember member : mParticipantList) {
            if(!User.isYou(context, member.mUserId)) {
                return member;
            }
        }

        return null;
    }

    @Nullable
    public VoipMeetingMember findMember(String userId) {
        for(VoipMeetingMember member : mParticipantList) {
            if(member.mUserId.equals(userId)) {
                return member;
            }
        }

        return null;
    }



    @Override
    public int compareTo(Object another) {
        if (another == null || !(another instanceof VoipMeetingGroup)) {
            return -1;
        }

        VoipMeetingGroup antherMeeting = (VoipMeetingGroup) another;

        long resultLong = antherMeeting.mCallTime - this.mCallTime;
        int result;
        if (0 < resultLong) {
            result = 1;
        } else if (0 == resultLong) {
            result = 0;
        } else {
            result = -1;
        }

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMeetingId);
        dest.writeString(this.mName);
        dest.writeString(this.mAvatar);
        dest.writeParcelable(this.mCreator, flags);
        dest.writeParcelable(this.mMeetingInfo, flags);
        dest.writeLong(this.mCallTime);
        dest.writeLong(this.mDuration);
        dest.writeInt(this.mStatus == null ? -1 : this.mStatus.ordinal());
        dest.writeInt(this.mVoipType == null ? -1 : this.mVoipType.ordinal());
        dest.writeTypedList(this.mParticipantList);
    }

    protected VoipMeetingGroup(Parcel in) {
        this.mMeetingId = in.readString();
        this.mName = in.readString();
        this.mAvatar = in.readString();
        this.mCreator = in.readParcelable(VoipMeetingMember.class.getClassLoader());
        this.mMeetingInfo = in.readParcelable(MeetingInfo.class.getClassLoader());
        this.mCallTime = in.readLong();
        this.mDuration = in.readLong();
        int tmpMStatus = in.readInt();
        this.mStatus = tmpMStatus == -1 ? null : MeetingStatus.values()[tmpMStatus];
        int tmpMVoipType = in.readInt();
        this.mVoipType = tmpMVoipType == -1 ? null : VoipType.values()[tmpMVoipType];
        this.mParticipantList = new CopyOnWriteArrayList<>(in.createTypedArrayList(VoipMeetingMember.CREATOR));
    }

    public static final Creator<VoipMeetingGroup> CREATOR = new Creator<VoipMeetingGroup>() {
        @Override
        public VoipMeetingGroup createFromParcel(Parcel source) {
            return new VoipMeetingGroup(source);
        }

        @Override
        public VoipMeetingGroup[] newArray(int size) {
            return new VoipMeetingGroup[size];
        }
    };
}
