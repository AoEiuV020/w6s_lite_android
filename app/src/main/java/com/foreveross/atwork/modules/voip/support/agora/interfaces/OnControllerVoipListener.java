package com.foreveross.atwork.modules.voip.support.agora.interfaces;

import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;

public interface OnControllerVoipListener {

    /**
     * 通知应用去开启会议
     */
    void onStartVoipMeeting();


    /**
     * 获取在会议过程中添加的成员
     */
    void onInviteMember();


    /**
     * 会议启动成功
     */
    void onStartCallSuccess();

    /**
     * 会议启动失败
     */
    void onStartCallFailure();


    /**
     * 隐藏呼叫界面，呼叫在后台进行
     */
    void onHideView();

    /**
     * 主动取消呼叫
     */
    void onCancelCall();

    /**
     * 结束呼叫
     */
    void onFinishCall(int memberCount);


    /**
     * 1对1呼叫超时
     */
    void onTimeOut();

    /**
     * 有参会人加入
     */
    void onParticipantEnter(VoipMeetingMember participant);

    /**
     * 有参会人离开
     */
    void onParticipantLeave(VoipMeetingMember participant);


    /**************接收方**************/

    /**
     * 接受服务
     */
    void onAcceptCall();

    /**
     * 拒绝服务
     */
    void onRejectCall();


}
