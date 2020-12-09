package com.foreveross.atwork.infrastructure.model.voip;

import com.foreveross.atwork.infrastructure.support.AtworkConfig;

import java.io.Serializable;

/**
 * Created by peng.xu on 2015/12/10.
 */
public class VoipMeetingMemberWrapData implements Serializable, Comparable<VoipMeetingMemberWrapData> {
    private static final long serialVersionUID = -1863937580469469506L;
    private VoipMeetingMember meetingMember;
    private long    confUserId;
    private VoiceType voiceType;
    private boolean isSpeaking;
    private boolean isMute;
    private boolean isMySelf;
    private boolean isVideoShared;
    private boolean isDesktopShared;
    private Object videoSurface;

    public VoipMeetingMemberWrapData(){
        this.meetingMember = new VoipMeetingMember();
        this.confUserId = 0;
        this.voiceType = VoiceType.NONE;
        this.isSpeaking = false;
        this.isMute = false;
        this.isMySelf = false;
        this.isVideoShared = false;
        this.isDesktopShared = false;
        this.videoSurface = null;
    }
    public VoipMeetingMemberWrapData(VoipMeetingMember meetingMember,
                                     VoiceType voiceType,
                                     boolean isSpeaking, boolean isMute, boolean isMySelf,
                                     boolean isVideoShared, boolean isDesktopShared, long confUserId, Object videoSurface){
        this.meetingMember = meetingMember;
        this.confUserId = confUserId;
        this.voiceType = voiceType;
        this.isSpeaking = isSpeaking;
        this.isMute = isMute;
        this.isMySelf = isMySelf;
        this.isVideoShared = isVideoShared;
        this.isDesktopShared = isDesktopShared;
        this.videoSurface = videoSurface;
    }

    public String getUserID(){
        return meetingMember == null ? "" : meetingMember.mUserId;
    }

    public String getUserDomianId() {
        return meetingMember == null ? AtworkConfig.DOMAIN_ID : meetingMember.mDomainId;

    }

    public VoipMeetingMember getUserEntity() {
        return meetingMember;
    }

    public void setUserEntity(VoipMeetingMember meetingMember) {
        this.meetingMember = meetingMember;
    }

    public long getConfUserId() {
        return confUserId;
    }

    public void setConfUserId(long confUserId) {
        this.confUserId = confUserId;
    }

    public VoiceType getVoiceType() {
        return voiceType;
    }

    public void setVoiceType(VoiceType voiceType) {
        this.voiceType = voiceType;
    }

    public boolean isSpeaking(){
        return isSpeaking;
    }

    public void setIsSpeaking(boolean isSpeaking){
        this.isSpeaking = isSpeaking;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setIsMute(boolean isMute) {
        this.isMute = isMute;
    }

    public boolean isMySelf() {
        return isMySelf;
    }

    public void setIsMySelf(boolean isMySelf) {
        this.isMySelf = isMySelf;
    }

    public boolean isVideoShared() {
        return isVideoShared;
    }

    public void setIsVideoShared(boolean isVideoShared) {
        this.isVideoShared = isVideoShared;
    }

    public boolean isDesktopShared() {
        return isDesktopShared;
    }

    public void setIsDesktopShared(boolean isDesktopShared) {
        this.isDesktopShared = isDesktopShared;
    }
    public  void setVideoSurface(Object videoSurface){
        this.videoSurface = videoSurface;
    }
    public Object getVideoSurface(){
        return this.videoSurface;
    }

    public UserStatus getUserStatus(){
        return getUserEntity().getUserStatus();
    }

    /**
     * @brief 当前实体是否排在后面
     */
    public boolean isRankBehind(VoipMeetingMemberWrapData item) {
        return (!item.isMySelf && (item.getUserStatus() == UserStatus.UserStatus_Left
                || item.getUserStatus() == UserStatus.UserStatus_NotJoined
                || item.getUserStatus() == UserStatus.UserStatus_Rejected));
    }

    /**
     * @brief 排序
     * @param another 另一个对象
     * @return 判断的排序结果
     *  -1：当前对象排在前面
     *  1：当前对象排在后面
     *  0：顺序相同
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(VoipMeetingMemberWrapData another) {
        int result = 0;
        if (another == null) {
            result = -1;
        } else if (isRankBehind(this)) {
            if (isRankBehind(another))
                result = 0;
            else
                result = 1;
        } else if (isRankBehind(another)) {
            if (isRankBehind(this))
                result = 0;
            else
                result = -1;
        }
        return result;
    }
}
