package com.foreveross.atwork.infrastructure.model.voip;

import android.os.Parcel;
import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.utils.MessageCovertUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

/**
 * Meeting 信息
 */
public class MeetingInfo implements Parcelable {
    @SerializedName("type")
    public Type mType;

    @SerializedName("id")
    public String mId;


    @SerializedName("avatar")
    public String mAvatar;

    @SerializedName("org_id")
    public String mOrgCode;

    public static MeetingInfo getMeetingInfo(Object bodyMap) {
        LinkedTreeMap<String, Object> linkedTreeMap = (LinkedTreeMap<String, Object>) bodyMap;

        MeetingInfo meetingInfo = new MeetingInfo();
        //sessionInfo  赋值
        meetingInfo.mId = (String) linkedTreeMap.get("id");
        meetingInfo.mType = Type.valueOf((String) linkedTreeMap.get("type"));
        meetingInfo.mAvatar = (String) linkedTreeMap.get("avatar");


        return meetingInfo;
    }

    /**
     * 会议室是否是群聊类型
     * */
    public boolean isGroupType() {
        return (null == mType || !MeetingInfo.Type.USER.equals(mType));
    }


    public enum Type {
        /**
         * 单人语音会议
         */
        USER,

        /**
         * 群组语音会议
         */
        DISCUSSION,


        /**
         * 多人语音会议
         */
        MULTI
    }


    public MeetingInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType == null ? -1 : this.mType.ordinal());
        dest.writeString(this.mId);
    }

    protected MeetingInfo(Parcel in) {
        int tmpMType = in.readInt();
        this.mType = tmpMType == -1 ? null : Type.values()[tmpMType];
        this.mId = in.readString();
    }

    public static final Creator<MeetingInfo> CREATOR = new Creator<MeetingInfo>() {
        @Override
        public MeetingInfo createFromParcel(Parcel source) {
            return new MeetingInfo(source);
        }

        @Override
        public MeetingInfo[] newArray(int size) {
            return new MeetingInfo[size];
        }
    };
}
