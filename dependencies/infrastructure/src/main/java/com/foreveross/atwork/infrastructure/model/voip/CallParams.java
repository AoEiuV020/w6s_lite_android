package com.foreveross.atwork.infrastructure.model.voip;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ranger Liao on 2/25/16.
 */

public class CallParams implements Parcelable {

    public VoipMeetingMember mMySelf = null;
    public VoipMeetingMember mPeer = null;
    public VoipMeetingGroup mGroup = null;

    public CallParams(){
        this.mMySelf = null;
        this.mPeer = null;
        this.mGroup = null;
    }

    public CallParams(VoipMeetingMember mySelf, VoipMeetingMember peer, VoipMeetingGroup group ) {
        this.mMySelf = mySelf;
        this.mPeer = peer;
        this.mGroup = group;
    }

    public boolean isGroupChat() {
        return null != mGroup;
    }

    public List<UserHandleInfo> getCallMemberList() {
        List<UserHandleInfo> userHandleInfoList = new ArrayList<>();

        if(null != mGroup) {
            userHandleInfoList.addAll(mGroup.mParticipantList);

        } else {
            userHandleInfoList.add(mMySelf);
            userHandleInfoList.add(mPeer);
        }

        return userHandleInfoList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mMySelf, flags);
        dest.writeParcelable(this.mPeer, flags);
        dest.writeParcelable(this.mGroup, flags);
    }

    protected CallParams(Parcel in) {
        this.mMySelf = in.readParcelable(VoipMeetingMember.class.getClassLoader());
        this.mPeer = in.readParcelable(VoipMeetingMember.class.getClassLoader());
        this.mGroup = in.readParcelable(VoipMeetingGroup.class.getClassLoader());
    }

    public static final Creator<CallParams> CREATOR = new Creator<CallParams>() {
        @Override
        public CallParams createFromParcel(Parcel source) {
            return new CallParams(source);
        }

        @Override
        public CallParams[] newArray(int size) {
            return new CallParams[size];
        }
    };
}
