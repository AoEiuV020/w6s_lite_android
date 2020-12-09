package com.foreveross.atwork.infrastructure;

import androidx.annotation.NonNull;

import com.foreveross.atwork.infrastructure.model.voip.UserStatus;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 2016/9/30.
 */

public interface VoipControllerStrategy {

    /**
     * 当前会议是否存在
     * */
    boolean isCurrentVoipMeetingValid();

    /**
     * 获取当前会议 id
     */
    String getWorkplusVoipMeetingId();


    /**
     * 添加新成员
     */
    void addParticipants(final ArrayList<VoipMeetingMember> memberArray);

    /**
     * 切换成群组类型
     */
    void switchToGroup(VoipMeetingGroup group);


    /**
     * 转换成群组类型
     */
    VoipMeetingGroup transfer2Group();

    /**
     * 挂断电话, 包括跟 workplus 后台请求相关接口, 清理相关 activity 等
     * */
    void finishCall();

    /**
     * 挂断电话, 维护通话界面的状态
     */
    void stopCall();

    /**
     * 是否是群类型
     */
    boolean isGroupChat();

    /**
     * 更新参会者状态, 并且刷新 UI
     */
    void setParticipantStatusAndRefreshUI(@NonNull VoipMeetingMember voipMeetingMember, UserStatus userStatus);

    /**
     * 去除参会者, 并且刷新 UI
     */
    void removeParticipantAndRefreshUI(String userId);

    /**
     * 去除参会者, 并且刷新 UI
     */
    void removeParticipantsAndRefreshUI(List<String> userIdList);

    /**
     * toast提醒
     */
    void tipToast(String tip);


    /**
     * 加入会议
     */
    void startCallByJoinKey(String workplusVoipMeetingId, String token);


    /**
     * 是否打开视频
     */
    boolean isVideoCallOpened();

    /**
     * 保存当前视频画面信息
     * @param currentBigViewUid 当前大画面的用户 id, -1表示用自己的逻辑来获取该 id
     * */
    void saveShowingVideo(int currentBigViewUid);

    /**
     * 返回正在会议中的人, 不包括离开, 或者拒绝的人
     */
    @NonNull
    List<VoipMeetingMember> getVoipMemInMeetingList();
}