package com.foreveross.atwork.modules.voip.utils;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.foreveross.atwork.BuildConfig;
import com.foreveross.atwork.R;
import com.foreveross.atwork.db.daoService.UserDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.chat.VoipChatMessage;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.CallParams;
import com.foreveross.atwork.infrastructure.model.voip.CallState;
import com.foreveross.atwork.infrastructure.model.voip.CurrentVoipMeeting;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.MeetingStatus;
import com.foreveross.atwork.infrastructure.model.voip.UserStatus;
import com.foreveross.atwork.infrastructure.model.voip.UserType;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.VoipSdkType;
import com.foreveross.atwork.infrastructure.utils.ContactHelper;
import com.foreveross.atwork.infrastructure.utils.CustomerHelper;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.manager.VoipMeetingController;
import com.foreveross.atwork.manager.VoipNoticeManager;
import com.foreveross.atwork.modules.chat.dao.VoipMeetingDaoService;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.modules.chat.util.SystemChatMessageHelper;
import com.foreveross.atwork.modules.voip.activity.agora.AgoraCallActivity;
import com.foreveross.atwork.modules.voip.activity.qsy.QsyCallActivity;
import com.foreveross.atwork.modules.voip.manager.ZoomVoipManager;
import com.foreveross.atwork.modules.voip.support.qsy.TangSDKInstance;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.SystemMessageHelper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by dasunsy on 16/7/12.
 */
public class VoipHelper {

    public static boolean isVoipEnable(Context context) {
        boolean isEnable = true;
        //开发调试跟中集都不受后台控制
        if (!BuildConfig.DEBUG && !CustomerHelper.isCimc(context)) {
            isEnable = OrganizationSettingsManager.getInstance().isCurrentOrgVoipEnable(context);
        }

        return AtworkConfig.OPEN_VOIP && isEnable;
    }

    public static boolean isMeetingOpening(String meetingId) {
        return isHandlingVoipCall() && meetingId.equals(VoipManager.getInstance().getVoipMeetingController().getWorkplusVoipMeetingId());
    }

    public static void toastSelectMax(Context context, int max) {
        AtworkToast.showToast(getMaxTip(context, max));
    }

    public static String getMaxTip(Context context, int max) {
        return context.getResources().getString(R.string.toast_select_voip_conf_max, max + "");
    }

    /**
     * 获取吊起 callActivity 的 参数
     * @param context
     * @param isGroupType
     * @param isOriginator  是否是拨打者
     * @param contactList
     * */
    public static CallParams getCallParams(Context context, boolean isGroupType, boolean isOriginator , List<? extends ShowListItem> contactList) {
        String loginUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        CallParams callParams = new CallParams();

        if(!isGroupType) {   //单对单聊天

            for(ShowListItem contact : contactList) {
                VoipMeetingMember voipMeetingMember;

                if(contact instanceof VoipMeetingMember) {
                    voipMeetingMember = (VoipMeetingMember) contact;
                } else {
                    voipMeetingMember = ContactHelper.toBasicVoipMeetingMember(contact);
                }

                if(loginUserId.equals(contact.getId())) {
                    if(isOriginator) {
                        voipMeetingMember.setUserType(UserType.Originator);

                    } else {
                        voipMeetingMember.setUserType(UserType.Recipient);

                    }

                    callParams.mMySelf = voipMeetingMember;

                } else {
                    if(isOriginator) {
                        voipMeetingMember.setUserType(UserType.Recipient);

                    } else {
                        voipMeetingMember.setUserType(UserType.Originator);

                    }
                    callParams.mPeer = voipMeetingMember;

                }
            }

        } else {    //群聊, 或者多人语音聊天

            callParams.mGroup = new VoipMeetingGroup();
            CopyOnWriteArrayList<VoipMeetingMember> meetingMemberList = new CopyOnWriteArrayList<>();

            for(ShowListItem contact : contactList) {
                VoipMeetingMember voipMeetingMember;
                if(contact instanceof VoipMeetingMember) {
                    voipMeetingMember = (VoipMeetingMember) contact;
                } else {
                    voipMeetingMember = ContactHelper.toBasicVoipMeetingMember(contact);
                }

                if(loginUserId.equals(contact.getId())) {

                    if(isOriginator) {
                        voipMeetingMember.setUserType(UserType.Originator);

                    } else {
                        voipMeetingMember.setUserType(UserType.Recipient);

                    }

                    callParams.mMySelf = voipMeetingMember;

                } else {
                    voipMeetingMember.setUserType(UserType.Recipient);

                }

                //已加入的不改变原本状态
                if(UserStatus.UserStatus_Joined != voipMeetingMember.getUserStatus()) {
                    voipMeetingMember.setUserStatus(UserStatus.UserStatus_NotJoined);

                }

                meetingMemberList.add(voipMeetingMember);
            }

            callParams.mGroup.mParticipantList = meetingMemberList;

        }

        return callParams;
    }


    /**
     * 2个人时, 获取对方
     * @param context
     * @param memberList size 为2时才调用该方法
     *
     * @return  showListItem
     * */
    @Nullable
    public static ShowListItem getPeer(Context context, List<? extends ShowListItem> memberList) {
        for(ShowListItem member : memberList) {
            if(!User.isYou(context, member.getId())) {
                return member;
            }
        }

        return null;
    }

    public static void goToCallActivity(Context context, MeetingInfo meetingInfo, VoipType voipType, boolean isGroupType, List<? extends ShowListItem> contactSelectList, boolean isOriginator,
                                        @Nullable String qsyMeetingId, @Nullable String workplusMeetingId, @Nullable String joinToken, UserHandleInfo invitor) {
        if (isHandlingCall()) {

            LogUtil.e("VOIP", "VOIP MEETING_ID : "  + qsyMeetingId + "   joinToken : " + joinToken);
            return;
        }

        if (!isOriginator) {
            VoipNoticeManager.getInstance().initCallNotificationShow(context, voipType, invitor);
        }

        CallParams callParams = VoipHelper.getCallParams(context, isGroupType, isOriginator, contactSelectList);

        if (VoipSdkType.QSY == AtworkConfig.VOIP_SDK_TYPE) {
            Intent intent = QsyCallActivity.getIntent(context);


            intent.putExtra(QsyCallActivity.EXTRA_CALL_PARAMS, callParams);
            intent.putExtra(QsyCallActivity.EXTRA_QSY_MEETING_ID, qsyMeetingId);
            intent.putExtra(QsyCallActivity.EXTRA_WORKPLUS_MEETING_ID, workplusMeetingId);
            intent.putExtra(QsyCallActivity.EXTRA_JOIN_TOKEN, joinToken);
            intent.putExtra(QsyCallActivity.EXTRA_MEETING_INFO, meetingInfo);
            intent.putExtra(QsyCallActivity.EXTRA_VOIP_TYPE, voipType);
            intent.putExtra(QsyCallActivity.EXTRA_INVITER, invitor);

            TangSDKInstance.getInstance().setWorkplusVoipMeetingId(workplusMeetingId);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);

        } else {
            Intent intent = AgoraCallActivity.getIntent(context);

            intent.putExtra(QsyCallActivity.EXTRA_INVITER, invitor);
            intent.putExtra(QsyCallActivity.EXTRA_JOIN_TOKEN, joinToken);

            VoipMeetingController.getInstance().setCurrentVoipMeeting(workplusMeetingId, meetingInfo, voipType, callParams);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }

    }

    public static boolean isHandlingVoipCallExcludeInit() {
        return isHandlingVoipCall() && CallState.CallState_Init != VoipManager.getInstance().getCallState();
    }

    public static boolean isHandlingVoipCallAndInit() {
        return isHandlingVoipCall() && CallState.CallState_Init == VoipManager.getInstance().getCallState();
    }

    /**
     * 是否正在通话中, 包括 voip 通话, 以及系统通话
     * */
    public static boolean isHandlingCall() {
        return isHandlingVoipCall() || AtworkUtil.isSystemCalling();
    }

    public static boolean isHandlingVoipCall() {
        return isSdkBasedVoipHandlingCall() || ZoomVoipManager.INSTANCE.isCalling();
    }

    public static boolean isSdkBasedVoipHandlingCall() {
        boolean isHandlingCall = false;

        try {
            isHandlingCall = AtworkConfig.OPEN_VOIP && CallState.CallState_Idle != VoipManager.getInstance().getCallState() &&
                    CallState.CallState_Ended != VoipManager.getInstance().getCallState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isHandlingCall;
    }

    public static boolean isHandlingVoiceVoipCall() {
        return isHandlingVoipCall() && !VoipManager.getInstance().getVoipMeetingController().isVideoCallOpened();
    }

    public static boolean isHandingVideoVoipCall() {
        return isHandlingVoipCall() && VoipManager.getInstance().getVoipMeetingController().isVideoCallOpened();
    }



    /**
     * 返回显示的通话时间   ps : 01:55:33 / 55:33
     * @param callDuration 通话时长, 单位: s
     * */
    public static String toCallDurationShow(long callDuration) {
        if (3600 >= callDuration) {
            int minute = (int) (callDuration / 60);
            long second = (int) (callDuration % 60);

            return String.format("%02d:%02d", minute, second);

        } else {
            int hour = (int) (callDuration / 3600);

            long minute = (int) (callDuration - hour * 3600) / 60;

            long second = (int) (callDuration % 60);

            return String.format("%02d:%02d:%02d",hour, minute, second);
        }
    }

    /**
     * 会议室是否是群聊类型
     * */
    public static boolean isGroupType(MeetingInfo meetingInfo) {
        return (null == meetingInfo || meetingInfo.isGroupType());
    }



    public static void handleUpdateMeetingRecord(Context context, CurrentVoipMeeting currentVoipMeeting, MeetingStatus meetingStatus) {
        //重连失败, 更新相关信息(历史纪录, 聊天界面的消息)
        VoipMeetingDaoService.getInstance().updateMeetingInfo(VoipMeetingController.getInstance().getWorkplusVoipMeetingId(), MeetingStatus.SUCCESS, currentVoipMeeting.mCallDuration * 1000);

        if(MeetingInfo.Type.DISCUSSION.equals(currentVoipMeeting.mMeetingInfo.mType)) {
            sendDiscussionSystemMessage(context, currentVoipMeeting, meetingStatus);


        } else if(MeetingInfo.Type.USER.equals(currentVoipMeeting.mMeetingInfo.mType)) {
            sendVoipChatMessage(context, currentVoipMeeting, currentVoipMeeting.mCallDuration, meetingStatus);


        }
    }

    private  static void sendDiscussionSystemMessage(Context context, CurrentVoipMeeting currentVoipMeeting, MeetingStatus meetingStatus) {
        String systemContent;
        String meetingTypeTip;
        if (VoipType.VIDEO.equals(currentVoipMeeting.mVoipType)) {
            meetingTypeTip = context.getString(R.string.voip_video_meeting);
        } else {
            meetingTypeTip = context.getString(R.string.voip_voice_meeting);
        }

        if (MeetingStatus.SUCCESS.equals(meetingStatus)) {
            systemContent = context.getString(R.string.chat_tip_voip_end, meetingTypeTip);

        } else {
            systemContent = context.getString(R.string.chat_tip_voip_failed, meetingTypeTip);

        }

        SystemChatMessage systemChatMessage = SystemChatMessageHelper.createMessageByCurrentVoipMeeting(context, systemContent, currentVoipMeeting);
        //偶尔出现 android.database.sqlite.SQLiteDiskIOException, disk I/O error (code 778)的 bug,
        // 可能是 #tableExists方法的 cursor 没关闭导致待观察
        //https://bugly.qq.com/v2/crash/apps/900033769/issues/955?pid=1
        try {
            ChatSessionDataWrap.getInstance().asyncReceiveMessage(systemChatMessage, true);
            SystemMessageHelper.newSystemMessageNotice(context, systemChatMessage);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("voip", "voip # newSendDiscussionSystemMessage  SQLiteDiskIOException");
        }
    }

    private static void sendVoipChatMessage(Context context, CurrentVoipMeeting currentVoipMeeting, long callSecDuration, MeetingStatus meetingStatus) {

        UserHandleInfo toUserHandleInfo = new UserHandleInfo();
        toUserHandleInfo.mUserId = currentVoipMeeting.mMeetingInfo.mId;
        VoipEventServiceHelper.assembleDomainId(context, currentVoipMeeting, toUserHandleInfo);

        String voipChatContent;
        if(MeetingStatus.SUCCESS.equals(meetingStatus)) {
            voipChatContent = context.getString(R.string.call_duration, VoipHelper.toCallDurationShow(callSecDuration));

        } else {
            //被呼叫是自己, 则表示创建会议的人是呼叫方
            if (User.isYou(context, toUserHandleInfo.mUserId)) {
                voipChatContent = context.getString(R.string.tip_voip_reject_self);

            } else {
                voipChatContent = context.getString(R.string.tip_voip_cancel_self);

            }
        }



        VoipChatMessage voipChatMessage = VoipChatMessage.newVoipChatMessage(context, voipChatContent, toUserHandleInfo, meetingStatus, currentVoipMeeting);

        voipChatMessage.read = ReadStatus.AbsolutelyRead;


        //更新激活状态
        if (!User.isYou(context, voipChatMessage.from)) {
            UserDaoService.getInstance().updateUserActiveStatus(BaseApplicationLike.baseContext, voipChatMessage.from, voipChatMessage.mFromDomain);
        }

        //偶尔出现 android.database.sqlite.SQLiteDiskIOException, disk I/O error (code 778)的 bug,
        // 可能是 #tableExists方法的 cursor 没关闭导致待观察
        //https://bugly.qq.com/v2/crash/apps/900033769/issues/955?pid=1
        try {
            ChatSessionDataWrap.getInstance().asyncReceiveMessage(voipChatMessage, true);

            ChatMessageHelper.notifyMessageReceived(voipChatMessage);
        } catch (Exception e) {
            e.printStackTrace();

            Logger.e("voip", "voip # newSendVoipChatMessage  SQLiteDiskIOException");

        }


        SessionRefreshHelper.notifyRefreshSessionAndCount();
    }
}
