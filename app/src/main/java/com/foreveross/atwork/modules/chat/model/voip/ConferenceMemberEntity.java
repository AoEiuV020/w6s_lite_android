package com.foreveross.atwork.modules.chat.model.voip;


import java.io.Serializable;

/**
 * 与会人实体
 */
public class ConferenceMemberEntity implements Serializable {

//    /**
//     * 与会人状态
//     */
//    public static final int STATUS_SUCCESS = 1;
//    public static final int STATUS_LEAVE_CONF = 2;
//    public static final int STATUS_MUTE = 3;
//    public static final int STATUS_INVITE = 5;
//    /**
//     * 与会人状态:用户举手
//     */
//    public static final int STATUS_SPEAK_APPLY = 6;
//    /**
//     * 主持人 / 与会人
//     */
//    public static final int ROLE_PRESIDER = 1;
//    public static final int ROLE_MEMBER = 2;
//    public static final int ROLE_HOST = 3;
//    public static final int ROLE_HOSTANDPRESIDER = 4;
//    /**
//     * 话语权
//     */
//    public static final short TALKMODE_OPEN = 1;
//    public static final short TALKMODE_CLOSE = 0;
//
//    /**
//     * 与会者状态有效值
//     */
//    private static final int[] STATUS_VALUE =
//            {1, 2, 3, 5, 6};
//
//    private List<VideoDeviceInfo> videoDeviceInfos = new ArrayList<VideoDeviceInfo>();
//
//    /**
//     * 是否本人
//     */
//    public boolean mIsSelf;
//    /**
//     * 会议ID
//     */
//    public String mMeetingId;
//    /**
//     * 与会状态
//     */
//    public int mStatus;
//    /**
//     * mDisplayName
//     */
//    public String mDisplayName;
//    /**
//     * mDisplayName
//     */
//    public String mDisplayNamePinyin;
//    /**
//     * 与会者espace帐号  注： 只有创建会议时可以调用
//     * TODO s00200335 后续删除
//     */
//    public String mAccount;
//    /**
//     * 是否是主讲人
//     */
//    public boolean mPresent;
//
//    public int mRole = ROLE_MEMBER;
//
//    /**
//     * 数据会议用 用户名
//     */
//    public String mUsername;
//    /**
//     * 数据会议使用ID
//     */
//    public long mDataUserId;
//    /**
//     * 是否已经加入数据会议
//     */
//    public boolean mInDataConference;
//
//    /**
//     * 是否正在发言
//     */
//    public boolean mSpeaking;
//
//    public String mDomain;
//
//    public boolean mSelectSiteEnable;
//
//    public boolean mOnlyDevice;
//
//    /**
//     * 构造方法
//     * 此构造函数主要是用来匹配与会人的
//     *
//     * @param name 与会人的显示名字，无填null
//     * @author s00200335
//     */
//    public ConferenceMemberEntity(String name) {
//        mDisplayName = name;
//
//    }
//
//    public ConferenceMemberEntity(NewConfMsg newConfInfo) {
//        mMeetingId = newConfInfo.mMeetingId;
//
//        mUsername = newConfInfo.mUsername;
//
//        mDisplayName = newConfInfo.mUsername;
//
//        mDataUserId = newConfInfo.mUserId;
//
//        mInDataConference = true;
//
//        mSpeaking = true;
//    }
//
//
//    public int getmStatus() {
//        return mStatus;
//    }
//
//    public void setmStatus(int s) {
//        // MAA在有些时候也传-1为离会
//        if (s == -1 || s == -3) {
//            s = STATUS_LEAVE_CONF;
//        }
//
//        if (!checkStatus(s)) {
//            return;
//        }
//
//        mStatus = s;
//    }
//
//    private boolean checkStatus(int s) {
//        boolean result = false;
//
//        for (int v : STATUS_VALUE) {
//            if (v == s) {
//                result = true;
//                break;
//            }
//        }
//
//        return result;
//    }
//
//
//
//    public void setRole(int r) {
//        if (r == ROLE_MEMBER || r == ROLE_PRESIDER) {
//            mRole = r;
//        }
//    }
//
//    public boolean isHost() {
//        return mRole == ROLE_PRESIDER;
//    }
//
//
//
//    /**
//     * 判断与会人是否正在会议中，状态必须是正在会议中通话
//     *
//     * @return
//     */
//    public boolean isInConference() {
//        //TODO 会议中状态枚举增加时，需要更新这里的判断条件
//        return getmStatus() != ConferenceMemberEntity.STATUS_INVITE
//                && getmStatus() != ConferenceMemberEntity.STATUS_LEAVE_CONF;
//    }
//
//    public boolean isSelectSiteEnable() {
//        return mSelectSiteEnable && isInConference();
//    }
//
//
//    public VideoDeviceInfo getOpenedVideoDevice() {
//        for (VideoDeviceInfo info : videoDeviceInfos) {
//            if (info != null && info.getStatus() == ConfMsg.VideoDeviceStatus.Open) {
//                return info;
//            }
//        }
//
//        return null;
//    }
//
//    public VideoDeviceInfo getFrontVideo() {
//        // TODO 待修改
//        if (videoDeviceInfos == null || videoDeviceInfos.isEmpty()) {
//            return null;
//        }
//
//        int length = videoDeviceInfos.size();
//        VideoDeviceInfo result = videoDeviceInfos.get(0);
//        VideoDeviceInfo temp;
//        String name;
//
//        for (int index = 0; index < length; index++) {
//            temp = videoDeviceInfos.get(index);
//            name = temp.getDeviceName();
//
//            if (name != null && name.contains("ront")) {
//                result = temp;
//                break;
//            }
//        }
//
//        return result;
//    }
//
//    /**
//     * 修改与会人的视频设备信息
//     *
//     * @param operType 0 删除  1 新增  2 修改
//     * @param info
//     */
//    public void modifyVideoDeviceInfo(int operType, VideoDeviceInfo info) {
//        if (info == null) {
//            return;
//        }
//
//        int index = videoDeviceInfos.indexOf(info);
//        switch (operType) {
//            case 0:
//                if (index != -1) {
//                    videoDeviceInfos.remove(index);
//                }
//                break;
//            case 1:
//                if (index != -1) {
//                    videoDeviceInfos.set(index, info);
//                } else {
//                    videoDeviceInfos.add(info);
//                }
//                break;
//            case 2:
//                if (index != -1) {
//                    VideoDeviceInfo localInfo = videoDeviceInfos.get(index);
//                    localInfo.setStatus(info.getStatus());
//                }
//                break;
//            default:
//                break;
//        }
//    }
//
//    public boolean ismIsSelf() {
//        if (mIsSelf) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    @Override
//    public String toString() {
//        // TODO 参数有改动，待修改
//        return "ConferenceMemberEntity [mMeetingId=" + mMeetingId + ", mStatus=" + mStatus + ",  mDisplayName=" + mDisplayName + ", mPresent=" + mPresent
//                + ", mRole=" + mRole + ']';
//    }
}
