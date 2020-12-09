package com.foreveross.atwork.modules.chat.util;

import android.content.Context;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.voip.CurrentVoipMeeting;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.VoipPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.DiscussionNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.FriendNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.OrgNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.UserFileDownloadNotifyMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;

import java.util.UUID;

/**
 * Created by dasunsy on 2017/7/25.
 */

public class SystemChatMessageHelper {

    public static SystemChatMessage createUserFileDownloadNoticeMessage(UserFileDownloadNotifyMessage userFileDownloadNotifyMessage) {
        String content;
        String timeDownloadShow = TimeUtil.getStringForMillis(userFileDownloadNotifyMessage.deliveryTime, TimeUtil.TIME_HHH_MM_SS);
        if (userFileDownloadNotifyMessage.isMobile()) {
            content = AtworkApplicationLike.getResourceString(R.string.file_download_successfully_notify_in_where, timeDownloadShow, AtworkApplicationLike.getResourceString(R.string.mobile_client), userFileDownloadNotifyMessage.mFileName);

        } else if(userFileDownloadNotifyMessage.isPc()) {
            content = AtworkApplicationLike.getResourceString(R.string.file_download_successfully_notify_in_where, timeDownloadShow, AtworkApplicationLike.getResourceString(R.string.pc_client), userFileDownloadNotifyMessage.mFileName);

        } else {
            content = AtworkApplicationLike.getResourceString(R.string.file_download_successfully_notify, timeDownloadShow, userFileDownloadNotifyMessage.mFileName);
        }

        SystemChatMessage systemChatMessage = new SystemChatMessage(content, SystemChatMessage.Type.FILE_DOWNLOAD);
        systemChatMessage.deliveryId = userFileDownloadNotifyMessage.deliveryId;
        systemChatMessage.deliveryTime = userFileDownloadNotifyMessage.deliveryTime;
        systemChatMessage.mToType = ParticipantType.User;
        systemChatMessage.mToDomain = userFileDownloadNotifyMessage.mToDomain;
        systemChatMessage.to = userFileDownloadNotifyMessage.to;
        systemChatMessage.mDisplayName = userFileDownloadNotifyMessage.mDisplayName;
        systemChatMessage.mDisplayAvatar = userFileDownloadNotifyMessage.mDisplayAvatar;
        systemChatMessage.mMyName = userFileDownloadNotifyMessage.mMyName;
        systemChatMessage.mMyAvatar = userFileDownloadNotifyMessage.mMyAvatar;
        systemChatMessage.mFromType = ParticipantType.User;
        systemChatMessage.from = userFileDownloadNotifyMessage.from;
        systemChatMessage.mFromDomain = userFileDownloadNotifyMessage.mFromDomain;
        systemChatMessage.read = userFileDownloadNotifyMessage.read;
        systemChatMessage.mBodyType = BodyType.System;


        return systemChatMessage;
    }

    public static SystemChatMessage createMessageByFriendNoticeMessage(String content, String fromId, String fromDomainId, FriendNotifyMessage friendNotifyMessage) {
        LoginUserBasic loginUserBasic = LoginUserInfo.getInstance().getLoginUserBasic(BaseApplicationLike.baseContext);
        SystemChatMessage systemChatMessage = new SystemChatMessage(SystemChatMessage.Type.NOTIFY_FRIEND, content, fromId, FriendNotifyMessage.FROM, fromDomainId, loginUserBasic.mDomainId);
        systemChatMessage.deliveryId = friendNotifyMessage.deliveryId;
        systemChatMessage.deliveryTime = friendNotifyMessage.deliveryTime;
        systemChatMessage.mToType = friendNotifyMessage.mToType;
        systemChatMessage.mFromType = ParticipantType.User;
        systemChatMessage.deliveryTime = friendNotifyMessage.deliveryTime;
        systemChatMessage.read = friendNotifyMessage.read;
        systemChatMessage.to = loginUserBasic.mUserId;
        systemChatMessage.mToDomain = loginUserBasic.mDomainId;
        systemChatMessage.mToType = ParticipantType.User;
        return systemChatMessage;
    }

    /**
     * 通过组织通知创建 SystemChatMessage
     *
     * @param content
     * @param orgNotifyMessage
     * @return
     */
    public static SystemChatMessage createMessageByOrgNoticeMessage(String content, OrgNotifyMessage orgNotifyMessage) {
        SystemChatMessage systemChatMessage = new SystemChatMessage(SystemChatMessage.Type.NOTIFY_ORG, content, Session.WORKPLUS_SYSTEM, OrgNotifyMessage.FROM, orgNotifyMessage.mFromDomain, orgNotifyMessage.mToDomain);
        systemChatMessage.deliveryId = orgNotifyMessage.deliveryId;
        systemChatMessage.deliveryTime = orgNotifyMessage.deliveryTime;
        systemChatMessage.mToType = orgNotifyMessage.mToType;
        systemChatMessage.mFromType = orgNotifyMessage.mFromType;
        systemChatMessage.deliveryTime = orgNotifyMessage.deliveryTime;
        systemChatMessage.read = orgNotifyMessage.read;
        systemChatMessage.mOrgId = orgNotifyMessage.mOrgCode;
        return systemChatMessage;
    }

    /**
    * 通过群通知(涉及清除群记录)创建 "系统通知" 的 SystemChatMessage
    *
    * @param content
    * @param discussionNotifyMessage
    * */
    public static SystemChatMessage createMessageByDiscussionRemovedNoticeMessage(String content, DiscussionNotifyMessage discussionNotifyMessage) {
        SystemChatMessage systemChatMessage = new SystemChatMessage(SystemChatMessage.Type.NOTIFY_DISCUSSION_REMOVE_HANDLE, content, Session.WORKPLUS_SYSTEM, DiscussionNotifyMessage.FROM, discussionNotifyMessage.mFromDomain, discussionNotifyMessage.mToDomain);
        systemChatMessage.deliveryId = discussionNotifyMessage.deliveryId;
        systemChatMessage.deliveryTime = discussionNotifyMessage.deliveryTime;
        systemChatMessage.mToType = discussionNotifyMessage.mFromType;
        systemChatMessage.mFromType = discussionNotifyMessage.mFromType;
        systemChatMessage.deliveryTime = discussionNotifyMessage.deliveryTime;
        systemChatMessage.read = discussionNotifyMessage.read;
        systemChatMessage.mOrgId = discussionNotifyMessage.orgId;
        return systemChatMessage;

    }


    /**
     * 通过群通知创建 SystemChatMessage
     *
     * @param content
     * @param fromUserId
     * @param discussionNotifyMessage
     * @return
     */
    public static SystemChatMessage createMessageByDiscussionNoticeMessage(String content, String fromUserId, DiscussionNotifyMessage discussionNotifyMessage) {
        SystemChatMessage systemChatMessage = new SystemChatMessage(SystemChatMessage.Type.NOTIFY_DISCUSSION, content, fromUserId, discussionNotifyMessage.to, discussionNotifyMessage.mFromDomain, discussionNotifyMessage.mToDomain);
        systemChatMessage.deliveryId = discussionNotifyMessage.deliveryId;
        systemChatMessage.mToType = discussionNotifyMessage.mToType;
        systemChatMessage.mFromType = discussionNotifyMessage.mFromType;
        systemChatMessage.deliveryTime = discussionNotifyMessage.deliveryTime;
        return systemChatMessage;
    }

    /**
     * 通过群 voip 通知创建 SystemChatMessage
     * */
    public static SystemChatMessage createMessageByVoipNoticeMessage(String content, String userId, VoipPostMessage voipPostMessage) {
        SystemChatMessage systemChatMessage = new SystemChatMessage(SystemChatMessage.Type.NOTIFY_VOIP, content, userId, voipPostMessage.mMeetingInfo.mId, voipPostMessage.mFromDomain, voipPostMessage.mToDomain);
        systemChatMessage.deliveryId = voipPostMessage.deliveryId;
        systemChatMessage.mToType = ParticipantType.Discussion;
        systemChatMessage.mFromType = voipPostMessage.mFromType;
        systemChatMessage.deliveryTime = voipPostMessage.deliveryTime;
        return systemChatMessage;
    }

    /**
     * 通过currentVoipMeeting 创建 SystemChatMessage
     * */
    public static SystemChatMessage createMessageByCurrentVoipMeeting(Context context, String content, CurrentVoipMeeting currentVoipMeeting) {
        String userId = LoginUserInfo.getInstance().getLoginUserId(context);
        String domainId = LoginUserInfo.getInstance().getLoginUserDomainId(context);
        String discussionId = currentVoipMeeting.mMeetingInfo.mId;
        String discussionDomainId;
        Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(context, discussionId);
        if(null != discussion) {
            discussionDomainId = discussion.mDomainId;

        } else {
            discussionDomainId = domainId;
        }


        SystemChatMessage systemChatMessage = new SystemChatMessage(SystemChatMessage.Type.NOTIFY_VOIP, content, userId, discussionId, domainId, discussionDomainId);
        systemChatMessage.deliveryId = UUID.randomUUID().toString();
        systemChatMessage.mToType = ParticipantType.Discussion;
        systemChatMessage.mFromType = ParticipantType.System;
        systemChatMessage.deliveryTime = TimeUtil.getCurrentTimeInMillis();
        return systemChatMessage;
    }


    /**
     * 创建"消息过期提示"的系统通知
     * */
    public static SystemChatMessage createMessageByExpiredMsg(ChatPostMessage expiredMsg) {
        String content;
        if (User.isYou(BaseApplicationLike.baseContext, expiredMsg.from)) {
            content = BaseApplicationLike.baseContext.getString(R.string.sender_burn_msg_expired_tip);
        } else {
            content = BaseApplicationLike.baseContext.getString(R.string.receiver_burn_msg_expired_tip);

        }
        SystemChatMessage systemChatMessage = new SystemChatMessage(content, SystemChatMessage.Type.MSG_EXPIRED);
        systemChatMessage.deliveryId = expiredMsg.deliveryId;
        systemChatMessage.deliveryTime = expiredMsg.deliveryTime;
        return systemChatMessage;
    }

    /**
     * 创建网盘将要过期的系统通知
     * @return
     */
    public static SystemChatMessage createDropboxIsGoingOverdueMsg(int overdueFiles) {
        String content = String.format(BaseApplicationLike.baseContext.getString(R.string.dropbox_is_overduing_tip), String.valueOf(overdueFiles));
        SystemChatMessage systemChatMessage = new SystemChatMessage(content, SystemChatMessage.Type.DROPBOX_IS_GOING_OVERDUE);
        systemChatMessage.mBodyType = BodyType.System;
        return systemChatMessage;
    }
}
