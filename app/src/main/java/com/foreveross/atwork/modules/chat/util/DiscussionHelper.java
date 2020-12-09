package com.foreveross.atwork.modules.chat.util;

import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.DiscussionNotifyMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.discussion.manager.DiscussionNotifyManger;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.utils.SystemMessageHelper;

public class DiscussionHelper {

    public static final String SESSION_INVALID = "SESSION_INVALID";

    public static final String SESSION_INVALID_ID = "SESSION_INVALID_ID";
    public static final String SESSION_INVALID_TYPE = "SESSION_INVALID_TYPE";

    public static final int SESSION_INVALID_SERVE_NO = 0x1;
    public static final int SESSION_INVALID_DISCUSSION_KICKED = 0x2;
    public static final int SESSION_INVALID_DISCUSSION_DISMISSED = 0x4;

    public static void notifySessionInvalidDiscussionKicked(String identifier) {
        Intent intent = new Intent(DiscussionHelper.SESSION_INVALID);
        intent.putExtra(SESSION_INVALID_ID, identifier);
        intent.putExtra(SESSION_INVALID_TYPE, SESSION_INVALID_DISCUSSION_KICKED);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);



    }


    public static void notifyDiscussionRemovedSystemSession(DiscussionNotifyMessage discussionNotifyMessage, boolean isCameFromOnline) {
        String content = StringUtils.EMPTY;
        String operatorName = DiscussionNotifyManger.getInstance().getOperationReadableNameReplaceMe(BaseApplicationLike.baseContext, discussionNotifyMessage.mOperator);

        if(DiscussionNotifyMessage.GroupOperation.DISMISSED == discussionNotifyMessage.mOperation) {

            if(discussionNotifyMessage.isInternalDiscussion() || StringUtils.isEmpty(operatorName)) {
                content = AtworkApplicationLike.getResourceString(R.string.system_notice_org_discussion_dismissed, discussionNotifyMessage.mDisplayName);

            } else {
                content = AtworkApplicationLike.getResourceString(R.string.system_notice_discussion_dismissed, discussionNotifyMessage.mDisplayName, operatorName);

            }



        } else if(DiscussionNotifyMessage.GroupOperation.MEMBER_KICKED == discussionNotifyMessage.mOperation) {
            if(discussionNotifyMessage.isInternalDiscussion() || StringUtils.isEmpty(operatorName)) {
                content = AtworkApplicationLike.getResourceString(R.string.system_notice_discussion_kicked_no_operator, discussionNotifyMessage.mDisplayName);
            } else {
                content = AtworkApplicationLike.getResourceString(R.string.system_notice_discussion_kicked, operatorName, discussionNotifyMessage.mDisplayName);

            }


        }

        if (!StringUtils.isEmpty(content)) {
            SystemChatMessage systemChatMessage = SystemChatMessageHelper.createMessageByDiscussionRemovedNoticeMessage(content, discussionNotifyMessage);
            ChatSessionDataWrap.getInstance().asyncReceiveMessage(systemChatMessage, isCameFromOnline);

            SystemMessageHelper.newSystemMessageNotice(BaseApplicationLike.baseContext, systemChatMessage);
        }
    }



    public static void notifySessionInvalidDiscussionDismissed(String identifier) {
        Intent intent = new Intent(DiscussionHelper.SESSION_INVALID);
        intent.putExtra(SESSION_INVALID_ID, identifier);
        intent.putExtra(SESSION_INVALID_TYPE, SESSION_INVALID_DISCUSSION_DISMISSED);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
    }
}
