package com.foreveross.atwork.modules.voip.support.qsy.interfaces;

import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;

import java.util.ArrayList;

/**
 * Created by quanshi on 2015/12/7.
 */
public interface ICallDelegate {
    /************** Common method **************/

    /**
     * 通知应用去开启会议
     */
    void onStartVoipMeeting();

    /**
     * 获取用户信息
     */
    void onStartGetUserProfile(String userId, String domainId);

    /**
     * 获取群信息
     */
    void onStartGetGroupProfile(VoipMeetingGroup voipMeetingGroup);

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
    void onFinishCall(int nConfUserNum);


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

    /**
     *  获取常用回复消息
     *
     */
    ArrayList<String> getQuickReplayMessages();

    /**
     *  发送回复消息
     *
     */
    void onSendQuickReplayMessage(int index);

    /**************获取会议对象信息**************/

    /**
     * @return 我所有的号码
     * @brief 获取我的手机号码列表
     */
    ArrayList<String> getMyPhoneNumberList();

    /**
     * 新输入了电话号码呼叫
     */
    void onNewPhoneNumberCalled(String phoneNumber);


    /**
     * @return 某被叫方所有电话号码
     * @brief 获取某被叫方所有的号码
     */
    ArrayList<String> getPhoneNumberList(String userId);
}
