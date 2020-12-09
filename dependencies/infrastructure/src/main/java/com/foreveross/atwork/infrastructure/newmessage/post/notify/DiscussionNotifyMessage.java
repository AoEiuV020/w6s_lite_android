package com.foreveross.atwork.infrastructure.newmessage.post.notify;

import android.content.Context;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.model.discussion.DiscussionMember;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionOwner;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionType;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class DiscussionNotifyMessage extends NotifyPostMessage {

    public static String FROM = "DISCUSSION_HELPER";

    public static final String MEMBERS = "members";

    public static final String MORE_INFO = "more_info";

    /**
     * 通知涉及的人员
     * */
    public List<DiscussionMember> mMembers = new ArrayList<>();

    public Operator mOperator;

    public GroupOperation mOperation;

    public Map<String, String> mMoreInfo;

    public String mDeviceId;

    public DiscussionOwner mDiscussionOwner;

    /**
     * 群类型,
     * ORG, 则为部门群
     * INTERNAL_ORG 为内部群
     * */
    public String mType;




    public String getDiscussionId() {
        return to;
    }


    public boolean isInternalDiscussion() {
        return DiscussionType.INTERNAL_ORG.equalsIgnoreCase(mType) || DiscussionType.SYSTEM.equalsIgnoreCase(mType);
    }

    public boolean isFromThisDevice() {
        return AtworkConfig.getDeviceId().equalsIgnoreCase(this.mDeviceId);

    }

    public boolean isFromSelfFromThisDevice(Context context) {
        return isFromThisDevice() && User.isYou(context, mOperator.mUserId);
    }

    public static DiscussionNotifyMessage getDiscussionNotifyMessageFromJson(Map<String, Object> jsonMap) {
        DiscussionNotifyMessage discussionNotifyMessage = new DiscussionNotifyMessage();
        discussionNotifyMessage.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);
        List<LinkedTreeMap<String, String>> members = (ArrayList) bodyMap.get(MEMBERS);
        for (LinkedTreeMap<String,String> map : members) {
            DiscussionMember discussionMember = new DiscussionMember();
            discussionMember.userId = map.get("user_id");
            discussionMember.domainId = map.get("domain_id");
            discussionMember.name = map.get("name");
            discussionMember. discussionId = discussionNotifyMessage.to;
            if(map.containsKey("tags")) {
                String tagsValue = map.get("tags");
                if(null != tagsValue) {
                    String[] tagsArray = tagsValue.split(",");
                    discussionMember.tags = new ArrayList<>(Arrays.asList(tagsArray));
                }

            }
            discussionNotifyMessage.mMembers.add(discussionMember);
        }

        discussionNotifyMessage.mDisplayAvatar = ChatPostMessage.getString(bodyMap, ChatPostMessage.DISPLAY_AVATAR);
        discussionNotifyMessage.mDisplayName = ChatPostMessage.getString(bodyMap, ChatPostMessage.DISPLAY_NAME);
        discussionNotifyMessage.mOperator = Operator.getOperator(bodyMap.get(OPERATOR));
        discussionNotifyMessage.mOperation = GroupOperation.fromStringValue((String) bodyMap.get(OPERATION));
        discussionNotifyMessage.mMoreInfo = (Map<String, String>) bodyMap.get(MORE_INFO);
        discussionNotifyMessage.mDeviceId = (String)bodyMap.get(DEVICE_ID);

        beCompatibleGroupNotifyMessage(discussionNotifyMessage);
        try {
            if (null != discussionNotifyMessage.mMoreInfo) {

                String owner = discussionNotifyMessage.mMoreInfo.get("owner");
                discussionNotifyMessage.mDiscussionOwner = DiscussionOwner.build(owner);

                String type = discussionNotifyMessage.mMoreInfo.get("discussion_type");
                if(!StringUtils.isEmpty(type)) {
                    discussionNotifyMessage.mType = type;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return discussionNotifyMessage;
    }


    /**
     * 为了防止旧版本 operation空指针的问题( 调用 fromStringValue return null)作出的兼容操作, 若 moreInfo
     * 存在 operation, 则以moreInfo 的为准
     *
     * @param discussionNotifyMessage
     *
     * */
    private static void beCompatibleGroupNotifyMessage(DiscussionNotifyMessage discussionNotifyMessage) {
        if(null != discussionNotifyMessage.mMoreInfo) {
            String operationStr = discussionNotifyMessage.mMoreInfo.get("operation");

            if(!StringUtils.isEmpty(operationStr)) {
                discussionNotifyMessage.mOperation = GroupOperation.fromStringValue(operationStr);

            }

        }
    }

    public boolean isYouOwner(Context context) {
        if(isOwnerLegal() && User.isYou(context, mDiscussionOwner.mUserId)) {
            return true;
        }

        return false;
    }

    public boolean isOwnerLegal() {
        return null != mDiscussionOwner;
    }

    public static class Operator {

        @SerializedName("domain_id")
        public String mDomainId;

        @SerializedName("name")
        public String mName;

        @SerializedName("user_id")
        public String mUserId;

        @Nullable
        public static Operator getOperator(Object bodyMap) {
            Operator operator = null;
            try {
                LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap<String, String>) bodyMap;

                operator = new Operator();

                operator.mUserId = linkedTreeMap.get("user_id");
                operator.mDomainId = linkedTreeMap.get("domain_id");
                operator.mName = linkedTreeMap.get("name");

            } catch (Exception e) {
                e.printStackTrace();
            }

            return operator;
        }
    }


    public enum GroupOperation {
        //成员发生变化(暂时未使用)
        MEMBERS_CHANGED,

        //群主转让
        TRANSFER,

        //有人成为群主
        OWNER,

        //添加群组成员
        MEMBER_JOINED,

        //群信息变更
        PROFILE_CHANGED,

        //创建群
        CREATED,

        //解散群
        DISMISSED,

        //群修改
        SETTINGS_CHANGED,

        //头像变更
        AVATAR_CHANGED,

        //群删除人员
        MEMBER_KICKED,

        //退群操作
        MEMBER_LEAVED,

        //添加管理员
        ADMIN_ADDED,

        //群名称变更
        NAME_CHANGED,

        //移动管理员
        ADMIN_REMOVED,

        //群邀请
        MEMBER_INVITED,

        //标签关系变更
        TAG_CHANGED,

        //模板变更
        TEMPLATE_CHANGED,

        //未知类型
        UNKNOWN;

        public static GroupOperation fromStringValue(String value) {

            if("MEMBERS_CHANGED".equalsIgnoreCase(value)) {
                return MEMBERS_CHANGED;
            }

            if("TRANSFER".equalsIgnoreCase(value)) {
                return TRANSFER;
            }

            if("OWNER".equalsIgnoreCase(value)) {
                return OWNER;
            }

            if ("MEMBER_JOINED".equalsIgnoreCase(value)) {
                return MEMBER_JOINED;
            }
            if ("PROFILE_CHANGED".equalsIgnoreCase(value)) {
                return PROFILE_CHANGED;
            }
            if ("CREATED".equalsIgnoreCase(value)) {
                return CREATED;
            }
            if ("DISMISSED".equalsIgnoreCase(value)) {
                return DISMISSED;
            }
            if ("SETTINGS_CHANGED".equalsIgnoreCase(value)) {
                return SETTINGS_CHANGED;
            }

            if ("AVATAR_CHANGED".equalsIgnoreCase(value)) {
                return AVATAR_CHANGED;
            }
            if ("MEMBER_KICKED".equalsIgnoreCase(value)) {
                return MEMBER_KICKED;
            }
            if ("MEMBER_LEAVED".equalsIgnoreCase(value)) {
                return MEMBER_LEAVED;
            }

            if ("ADMIN_ADDED".equalsIgnoreCase(value)) {
                return ADMIN_ADDED;
            }

            if ("NAME_CHANGED".equalsIgnoreCase(value)) {
                return NAME_CHANGED;
            }

            if ("ADMIN_REMOVED".equalsIgnoreCase(value)) {
                return ADMIN_REMOVED;
            }

            if("MEMBER_INVITED".equals(value)) {
                return MEMBER_INVITED;
            }

            if("TAG_CHANGED".equals(value)) {
                return TAG_CHANGED;
            }

            if("TEMPLATE_CHANGED".equals(value)) {
                return TEMPLATE_CHANGED;
            }

            return UNKNOWN;
        }

        public boolean isNeedRefreshDiscussion() {
            return PROFILE_CHANGED.equals(this) || this.equals(CREATED) || this.equals(SETTINGS_CHANGED);
        }

    }

}
