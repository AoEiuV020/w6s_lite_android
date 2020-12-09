package com.foreveross.atwork.modules.chat.model.voip;


import java.io.Serializable;

/**
 * 会议实体类
 * @author reyzhang22
 */
public class ConferenceEntity implements Comparable<ConferenceEntity>, Serializable {
    @Override
    public int compareTo(ConferenceEntity another) {
        return 0;
    }
//    /**
//     * 注释内容
//     */
//    private static final long serialVersionUID = 1L;
//    /**
//     * 会议状态
//     */
//    public static final int STATUS_CREATED = 0;
//    public static final int STATUS_TO_ATTEND = 1;
//    public static final int STATUS_IN_PROGRESS = 2;
//    public static final int STATUS_END = 3;
//    /**
//     * 会议类型 TYPE_INSTANT:即时会议  TYPE_BOOKING：预约会议
//     */
//    public static final int TYPE_INSTANT = 1;
//    public static final int TYPE_BOOKING = 2;
//    /**
//     * 多媒体类型 / 语音
//     */
//    public static final int AUDIO_CONFERENCE_TYPE = 1;
//    public static final int MULTI_CONFERENCE_TYPE = 2;
//    /**
//     * 共享接受屏幕 /关闭
//     */
//    public static final int SHARE_SCREEN_OFF = 0;
//    public static final int SHARE_SCREEN_ON = 1;
//    /**
//     * 共享接受屏幕 /关闭
//     */
//    public static final int CONF_TYPE_BASE = 0;
//    public static final int CONF_TYPE_MYMEETING = 1;
//    public static final int CONF_TYPE_MEDIAX = 2;
//    public static final int CONF_TYPE_MCU = 3;
//
//    /**
//     * 会议ID
//     */
//    private String mMeetingId; //primary Key
//    /**
//     * 会议主题
//     */
//    private String mSubject;
//    /**
//     * 开始时间
//     */
//    private Timestamp mBeginTime;
//    /**
//     * 结束时间
//     */
//    private Timestamp mEndTime;
//    /**
//     * 会议主持人(入会号码)
//     */
//    private String mHost; //会议主持人
//
//    /**
//     * 会议主持人(帐号)
//     */
//    private String mHostAccount;
//
//    // 融合会议之类
//    private int mConfType;
//    /**
//     * 会议类型
//     */
//    private int mType = TYPE_INSTANT;
//    /**
//     * 多媒体会议类型
//     */
//    private int mMediaType = AUDIO_CONFERENCE_TYPE;
//    /**
//     * 会议状态
//     */
//    private int mStatus = STATUS_END;
//
//    /**
//     * 会议计时器
//     */
//    private int mTimeCount;
//
//    /**
//     * 与会人列表
//     */
//    private List<ConferenceMemberEntity> mConfMemberList = new CopyOnWriteArrayList<ConferenceMemberEntity>();
//
//    private VideoDeviceInfo mLastViewDeviceInfo;
//
//    public Timestamp getmBeginTime() {
//        return (mBeginTime == null) ? null : (Timestamp) mBeginTime.clone();
//    }
//
//    public Timestamp getmEndTime() {
//        return (mEndTime == null) ? null : (Timestamp) mEndTime.clone();
//    }
//
//    public ConferenceMemberEntity getHostMemEntity() {
//        for (ConferenceMemberEntity entity : mConfMemberList) {
//            if (entity != null && entity.isHost()) {
//                return entity;
//            }
//        }
//
//        return null;
//    }
//
//
//    public String getmHost() {
//        if (mHost == null) {
//            mHost = "";
//        }
//
//        if (mHost.equals("")) {
//            ConferenceMemberEntity hostEntity = getHostMemEntity();
//
//            if (hostEntity != null) {
//            }
//        }
//
//        return mHost;
//    }
//
//    public void setHost(String mHost) {
//        this.mHost = StringUtil.filterSpecialChar(mHost);
//    }
//
//    public boolean isNoneHost() {
//        return StringUtil.isStringEmpty(mHostAccount)
//                && StringUtil.isStringEmpty(mHost);
//    }
//
//    public boolean isMcu() {
//        return mConfType == CONF_TYPE_MCU;
//    }
//
//    public int getmType() {
//        return mType;
//    }
//
//    public void setmType(int mType) {
//        if (mType != TYPE_INSTANT && mType != TYPE_BOOKING) {
//            return;
//        }
//
//        this.mType = mType;
//    }
//
//    public List<ConferenceMemberEntity> getConfMemberList() {
//        // 如果一个与会人都没有的话，不往其中添加主持人信息，以免混淆某些需要靠与会人是否为空判断的流程
//        if (mConfMemberList.isEmpty() || isNoneHost()) {
//            return mConfMemberList;
//        }
//
//        return mConfMemberList;
//    }
//
//    public void setConfMemberList(List<ConferenceMemberEntity> mConfMemberList) {
//        if (mConfMemberList == null) {
//            mConfMemberList = new ArrayList<ConferenceMemberEntity>();
//        }
//
//        this.mConfMemberList = mConfMemberList;
//    }
//
//    @Override
//    public int compareTo(ConferenceEntity another) {
//        return this.getmBeginTime().compareTo(another.getmBeginTime());
//    }
//
//    public ConferenceMemberEntity queryConferenceMember(long userId) {
//        if (0 == userId) {
//            return null;
//        }
//
//        for (ConferenceMemberEntity member : getConfMemberList()) {
//            if (member != null && userId == member.mDataUserId) {
//                return member;
//            }
//        }
//
//        return null;
//    }
//
//    public ConferenceMemberEntity getLastViewMember() {
//        if (mLastViewDeviceInfo == null) {
//            return null;
//        }
//
//        return queryConferenceMember(mLastViewDeviceInfo.getUserId());
//    }
//
//
//    public boolean isConfControlEnable() {
//        return !isMcu();
//    }
//
//
//    /**
//     * 根据帐号匹配会中的自己
//     *
//     * @return [说明] 首选返回会议中的，如果没有会议中的，则返回离会的，没有则返回Null
//     */
//    public ConferenceMemberEntity getSelfInConf() {
//        List<ConferenceMemberEntity> mList = getConfMemberList();
//
//        if (mList.isEmpty()) {
//            return null;
//        }
//
//        //暂时 取出 第一个作为自己
//        int pos = -1;
//
//        ConferenceMemberEntity temp;
//
//        for (int i = 0; i < mList.size(); i++) {
//            temp = mList.get(i);
//
//            if (temp.ismIsSelf()) {
//                pos = i;
//                break;
//            }
//        }
//
//        if (-1 == pos) {
//            return null;
//        }
//
//        return mList.get(pos);
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        return super.equals(o);
//    }
//
//    @Override
//    public int hashCode() {
//        return super.hashCode();
//    }
//
//    @Override
//    public String toString() {
//        return "ConferenceEntity [mMeetingId=" + mMeetingId + ", mSubject=" + mSubject
//                + ", mBeginTime=" + mBeginTime + ", mEndTime=" + mEndTime
//                + ",  mHost=" + mHost + ",  type=" + mType
//                + ", mMediaType=" + mMediaType + ", mStatus=" + mStatus + ']';
//    }
//
}
