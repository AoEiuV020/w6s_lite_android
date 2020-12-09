package com.foreveross.atwork.api.sdk.voip.requestJson;

import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMemberSettingInfo;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dasunsy on 16/7/14.
 */
public class HandleMeetingRequestJson {

    @SerializedName("ops")
    public String mOps;

    @SerializedName("gateway")
    public String mGateway;

    @SerializedName("operator")
    public UserHandleInfo mOperator;

    @SerializedName("members")
    public List<UserHandleInfo> mMemberList;

    /**
     * 以下为全时云所需参数
     * */
    @SerializedName("client_type")
    public String mClientType;

    @SerializedName("ip_addr")
    public String mIpAddr;

    @SerializedName("is_owner")
    public boolean mIsOwner;

    @SerializedName("role_map")
    public HashMap<String, String[]> mRoleMap;

    @SerializedName("conversation")
    public MeetingInfo mMeetingInfo;

    @SerializedName("voip_type")
    public VoipType mVoipType;

    /**
     * 会畅会议时长(单位为分钟)
     * */
    @SerializedName("hold_time")
    public int mBizconfHoldDuration;


    @SerializedName("settings")
    public VoipMeetingMemberSettingInfo mVoipMeetingMemberSettingInfo;

    /**
     * 默认为创建会议
     * */
    public enum ConfOps {
        /**
         * 邀请人员
         * */
        INVITE,

        /**
         * 加入会议
         * */
        JOIN,

        /**
         * 挂电话
         * */
        REJECT,

        /**
         * 离开
         * */
        LEAVE,

        /**
         * 繁忙
         * */
        BUSY,

        /**
         * 刷新, 告知后台会议还存活
         * */
        REFRESH
    }

}
