package com.foreveross.atwork.infrastructure.model.voip;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.newmessage.post.voip.GateWay;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.VoipSdkType;
import com.foreveross.atwork.infrastructure.utils.MessageCovertUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

public class VoipMeetingMember extends UserHandleInfo implements Parcelable, ShowListItem, Comparable<VoipMeetingMember> {

    public String mMeetingId;

    private UserType mUserType;

    private UserStatus mUserStatus;

    public String mMeetingStatus;

    @SerializedName("create_time")
    public long mCreateTime;

    @SerializedName("join_time")
    public long mJoinTime;

    @SerializedName("refresh_time")
    public long mRefreshTime = -1;

    public long mLeaveTime;

    /**
     * 全时云用于拨打电话
     * */
    public String mBindPhoneNumber;

    /**
     * 以下状态用以 UI 交互显示
     * */

    public VoiceType mVoiceType;

    public boolean mIsSpeaking;

    public boolean mIsMute;

    public boolean mIsVideoShared;

    @SerializedName("gateway")
    public GateWay mGateWay;

    @SerializedName("settings")
    public VoipMeetingMemberSettingInfo mVoipMeetingMemberSettingInfo;




    public VoipMeetingMember() {
        mBindPhoneNumber = "";
        mUserType = UserType.Recipient;
        mUserStatus = UserStatus.UserStatus_NotJoined;
    }

    public VoipMeetingMember(String userId, String domainId, UserType userType, String showName, String avatar, UserStatus userStatus) {
        this.mUserId = userId;
        this.mDomainId = domainId;
        this.mShowName = showName;
        this.mAvatar = avatar;
        this.mBindPhoneNumber = "";
        this.mUserType = userType;
        this.mUserStatus = userStatus;
    }


    public String getBindPhoneNumber() {
        return mBindPhoneNumber;
    }

    public void setBindPhoneNumber(String bindPhoneNumber) {
        this.mBindPhoneNumber = bindPhoneNumber;
    }

    public UserType getUserType() {
        return mUserType;
    }

    public void setUserType(UserType userType) {
        this.mUserType = userType;
    }


    public Boolean isMutedRemote() {
        if(null != mVoipMeetingMemberSettingInfo && !StringUtils.isEmpty(mVoipMeetingMemberSettingInfo.muteVoice)) {
            return VoipMeetingMemberSettingInfo.VOICE_MUTED.equals(mVoipMeetingMemberSettingInfo.muteVoice);
        }

        return null;
    }

    public boolean isAlive() {
        long currentTime = System.currentTimeMillis();
        //refreshTime -1 时表示从没发送过会议心跳包, 视为旧版本或者w6s 服务一直有问题, 极端情况网络异常等

        if(-1 < mRefreshTime && AtworkConfig.VOIP_KEEP_ALIVE_THRESHOLD * 1000 < currentTime - mRefreshTime) {
            //超时没心跳, 认为leave状态
            return false;
        }

        if(UserStatus.UserStatus_Joined == getUserStatus()) {
            return true;
        }

        return false;


    }

    public UserStatus getUserStatus() {
        return mUserStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.mUserStatus = userStatus;
    }

    public int getUid() {

        try {
            if (null != mGateWay) {
                return Integer.valueOf(mGateWay.mUid);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static VoipMeetingMember getMeetingMember(Object bodyMap, String meetingId) {
        LinkedTreeMap<String, Object> linkedTreeMap = (LinkedTreeMap<String, Object>) bodyMap;

        LinkedTreeMap<String, String> userBasicInfoTreeMap = (LinkedTreeMap<String, String>) linkedTreeMap.get("user");

        VoipMeetingMember voipMeetingMember = new VoipMeetingMember();
        //user  赋值
        voipMeetingMember.mUserId = userBasicInfoTreeMap.get("user_id");
        voipMeetingMember.mDomainId = userBasicInfoTreeMap.get("domain_id");
        voipMeetingMember.mShowName = userBasicInfoTreeMap.get("nickname");
        voipMeetingMember.mAvatar = userBasicInfoTreeMap.get("avatar");

        voipMeetingMember.mMeetingId = meetingId;
        voipMeetingMember.mMeetingStatus = (String) linkedTreeMap.get("status");

        if(VoipSdkType.QSY != AtworkConfig.VOIP_SDK_TYPE) {
            voipMeetingMember.refreshUserStatus();
        }

        voipMeetingMember.mCreateTime = MessageCovertUtil.getLong(linkedTreeMap, "create_time");
        voipMeetingMember.mJoinTime = MessageCovertUtil.getLong(linkedTreeMap, "join_time");
        voipMeetingMember.mLeaveTime = MessageCovertUtil.getLong(linkedTreeMap, "leave_time");

        Object gateway = MessageCovertUtil.getObject(linkedTreeMap, "gateway");
        if(null != gateway) {
            voipMeetingMember.mGateWay = GateWay.getGateWay(gateway);
        }

        return voipMeetingMember;
    }

    public static List<VoipMeetingMember> getMeetingMemberList(Object bodyMap, String conferenceId) {
        List<LinkedTreeMap<String, Object>> linkedTreeMapList = (List<LinkedTreeMap<String, Object>>) bodyMap;

        List<VoipMeetingMember> voipMeetingMemberList = new ArrayList<>();

        for (LinkedTreeMap<String, Object> confMemberMap : linkedTreeMapList) {
            VoipMeetingMember voipMeetingMember = getMeetingMember(confMemberMap, conferenceId);
            voipMeetingMemberList.add(voipMeetingMember);
        }

        return voipMeetingMemberList;
    }

    public void refreshUserStatus() {
        if("meeting".equalsIgnoreCase(mMeetingStatus)) {
            mUserStatus = UserStatus.UserStatus_Joined;

        } else if("initial".equalsIgnoreCase(mMeetingStatus)) {
            mUserStatus = UserStatus.UserStatus_NotJoined;

        } else {
            mUserStatus = UserStatus.UserStatus_NotJoined;

        }
    }

    @Override
    public String getTitle() {
        return mShowName;
    }

    @Override
    public String getTitleI18n(Context context) {
        return null;
    }

    @Override
    public String getTitlePinyin() {
        return null;
    }

    @Override
    public String getParticipantTitle() {
        return mShowName;
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public String getAvatar() {
        return mAvatar;
    }

    @Override
    public String getId() {
        return mUserId;
    }

    @Override
    public String getDomainId() {
        return mDomainId;
    }

    @Override
    public String getStatus() {
        return mStatus;
    }

    @Override
    public boolean isSelect() {
        return false;
    }

    @Override
    public void select(boolean isSelect) {

    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if(null != o && o instanceof VoipMeetingMember) {
            return this.getPrimaryKey().equals(((VoipMeetingMember)o).getPrimaryKey());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getPrimaryKey().hashCode();
    }

    public boolean isRankBehind() {
        return ((UserStatus.UserStatus_Left == mUserStatus
                || UserStatus.UserStatus_NotJoined == mUserStatus
                || UserStatus.UserStatus_Rejected == mUserStatus));
    }

    public int isRankMeFirst(VoipMeetingMember another) {
        if(User.isYou(BaseApplicationLike.baseContext, this.mUserId)) {

            if(User.isYou(BaseApplicationLike.baseContext, another.mUserId)) {
                return 0;
            } else {
                return -1;
            }

        } else {
            if(User.isYou(BaseApplicationLike.baseContext, another.mUserId)) {
                return 1;
            } else {
                return 0;
            }
        }
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mMeetingId);
        dest.writeInt(this.mUserType == null ? -1 : this.mUserType.ordinal());
        dest.writeInt(this.mUserStatus == null ? -1 : this.mUserStatus.ordinal());
        dest.writeString(this.mMeetingStatus);
        dest.writeLong(this.mJoinTime);
        dest.writeLong(this.mLeaveTime);
        dest.writeString(this.mBindPhoneNumber);
        dest.writeString(this.mShowName);
        dest.writeString(this.mAvatar);
        dest.writeString(this.mUserId);
        dest.writeString(this.mDomainId);
    }

    protected VoipMeetingMember(Parcel in) {
        super(in);
        this.mMeetingId = in.readString();
        int tmpMUserType = in.readInt();
        this.mUserType = tmpMUserType == -1 ? null : UserType.values()[tmpMUserType];
        int tmpMUserStatus = in.readInt();
        this.mUserStatus = tmpMUserStatus == -1 ? null : UserStatus.values()[tmpMUserStatus];
        this.mMeetingStatus = in.readString();
        this.mJoinTime = in.readLong();
        this.mLeaveTime = in.readLong();
        this.mBindPhoneNumber = in.readString();
        this.mShowName = in.readString();
        this.mAvatar = in.readString();
        this.mUserId = in.readString();
        this.mDomainId = in.readString();
    }

    public static final Creator<VoipMeetingMember> CREATOR = new Creator<VoipMeetingMember>() {
        @Override
        public VoipMeetingMember createFromParcel(Parcel source) {
            return new VoipMeetingMember(source);
        }

        @Override
        public VoipMeetingMember[] newArray(int size) {
            return new VoipMeetingMember[size];
        }
    };

    @Override
    public int compareTo(VoipMeetingMember another) {
        int result = 0;
        if (another == null) {
            result = -1;

        } else if (this.isRankBehind()) {
            if (another.isRankBehind())
                result = isRankMeFirst(another);
            else
                result = 1;

        } else if (another.isRankBehind()) {
            if (this.isRankBehind())
                result = isRankMeFirst(another);
            else
                result = -1;
        } else {
            result = isRankMeFirst(another);

        }
        return result;

    }
}
