package com.foreveross.atwork.modules.voip.utils;

import android.content.Context;

import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.CurrentVoipMeeting;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.MeetingStatus;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingGroup;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.newmessage.post.VoipPostMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.modules.voip.manager.ZoomVoipManager;

/**
 * Created by dasunsy on 16/9/9.
 */
public class VoipEventServiceHelper {
    /**
     * 最初的单对单语音聊天是否包含自己
     */
    public static boolean isVoipUserPeersHavingMe(Context context, VoipPostMessage voipPostMessage) {
        String loginUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        return MeetingInfo.Type.USER.equals(voipPostMessage.mMeetingInfo.mType) && (voipPostMessage.mCreator.mUserId.equals(loginUserId) || voipPostMessage.mMeetingInfo.mId.equals(loginUserId));
    }


    /**
     * 最初的单对单语音聊天是否包含操作者
     */
    public static boolean isVoipUserPeersHavingOperator(VoipPostMessage voipPostMessage) {
        return MeetingInfo.Type.USER.equals(voipPostMessage.mMeetingInfo.mType) && (voipPostMessage.mCreator.mUserId.equals(voipPostMessage.mOperator.mUserId) || voipPostMessage.mMeetingInfo.mId.equals(voipPostMessage.mOperator.mUserId));

    }

    /**
     * 该 voip 消息是否符合 User 单对单的条件
     * */
    public static boolean isVoipUserPeersLegal(Context context, VoipPostMessage voipPostMessage) {
        return isVoipUserPeersHavingMe(context, voipPostMessage) && isVoipUserPeersHavingOperator(voipPostMessage);
    }

    public static boolean isCurrentMeetingHandling(VoipPostMessage voipPostMessage) {
        return isSdkBasedCurrentMeetingHandling(voipPostMessage) || ZoomVoipManager.INSTANCE.isCurrentMeetingHandling(voipPostMessage);
    }


    public static boolean isSdkBasedCurrentMeetingHandling(VoipPostMessage voipPostMessage) {
        return voipPostMessage.mMeetingId.equals(VoipManager.getInstance().getVoipMeetingController().getWorkplusVoipMeetingId());
    }


    public static void insertVoipMeeting(VoipPostMessage voipPostMessage, boolean isOnline) {
        VoipMeetingGroup voipMeetingGroup = new VoipMeetingGroup();
        voipMeetingGroup.mParticipantList.addAll(voipPostMessage.mMemberList);
        voipMeetingGroup.mCallTime = System.currentTimeMillis();
        voipMeetingGroup.mCreator = voipPostMessage.mOperator;
        voipMeetingGroup.mMeetingId = voipPostMessage.mMeetingId;
        voipMeetingGroup.mMeetingInfo = voipPostMessage.mMeetingInfo;
        voipMeetingGroup.mVoipType = voipPostMessage.mGateWay.mVoipType;
        if (null != voipPostMessage.mMeetingInfo) {
            voipMeetingGroup.mAvatar = voipPostMessage.mMeetingInfo.mAvatar;

        }

        //init status
        voipMeetingGroup.mStatus = MeetingStatus.FAILED;

        voipMeetingGroup.mEnable = isOnline;

        VoipManager.getInstance().insertVoipMeetingGroupSync(voipMeetingGroup);
    }

    public static void assembleDomainId(Context context, CurrentVoipMeeting currentVoipMeeting, UserHandleInfo toUserHandleInfo) {
        if (User.isYou(context, toUserHandleInfo.mUserId)) {
            toUserHandleInfo.mDomainId = LoginUserInfo.getInstance().getLoginUserDomainId(context);

        } else {
            if(toUserHandleInfo.mUserId.equals(currentVoipMeeting.mCallPeer.mUserId)) {
                toUserHandleInfo.mDomainId = currentVoipMeeting.mCallPeer.mDomainId;

            } else {
                toUserHandleInfo.mDomainId = currentVoipMeeting.mCallSelf.mDomainId;

            }
        }
    }


    public static void assembleDomainId(Context context, VoipPostMessage voipPostMessage, UserHandleInfo toUserHandleInfo) {
        if (User.isYou(context, toUserHandleInfo.mUserId)) {
            toUserHandleInfo.mDomainId = LoginUserInfo.getInstance().getLoginUserDomainId(context);

        } else {
            VoipMeetingGroup voipMeetingGroup = VoipManager.getInstance().queryVoipMeetingGroup(voipPostMessage.mMeetingId);
            VoipMeetingMember peerMember = null;
            if (null != voipMeetingGroup) {
                peerMember = voipMeetingGroup.findMember(voipPostMessage.mMeetingInfo.mId);
            }

            if (null != peerMember) {
                toUserHandleInfo.mDomainId = peerMember.mDomainId;

            } else {
                toUserHandleInfo.mDomainId = voipPostMessage.mFromDomain;

            }
        }
    }

    public static void assembleVoipType(VoipPostMessage voipPostMessage) {
        if (null == voipPostMessage.mGateWay.mVoipType) {
            VoipMeetingGroup voipMeetingGroup = VoipManager.getInstance().queryVoipMeetingGroup(voipPostMessage.mMeetingId);

            if (null != voipMeetingGroup) {
                voipPostMessage.mGateWay.mVoipType = voipMeetingGroup.mVoipType;
            }
        }

    }
}
