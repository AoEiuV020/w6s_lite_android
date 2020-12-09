package com.foreveross.atwork.api.sdk.discussion.requestJson;

import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleBasic;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class AddOrRemoveDiscussionJSON {


    /**
     * 加人或是删除人员
     */
    @SerializedName("ops")
    public GroupOps ops;

    /**
     * 邀请人
     * */
    @SerializedName("inviter")
    public User inviter;


    /**
     * 被邀请人的 nickname
     * */
    @SerializedName("nickname")
    public String mShowName;

    /**
     * 被邀请人的 avatar
     * */
    @SerializedName("avatar")
    public String mAvatar;

    /**
     * 被邀请人的 status
     * */
    @SerializedName("status")
    public String mStatus;



    @SerializedName("members")
    public List<UserHandleInfo> members;

    @SerializedName("pin_code")
    public String pinCode;


    /**
     * 创建一个群加人的对象
     *
     * @param memberInfoList
     * @return
     */
    public static AddOrRemoveDiscussionJSON createAdd(final List<UserHandleInfo> memberInfoList) {
        AddOrRemoveDiscussionJSON groupAddOrRemoveJSON = new AddOrRemoveDiscussionJSON();
        groupAddOrRemoveJSON.ops = GroupOps.JOIN;
        groupAddOrRemoveJSON.members = new ArrayList<>();
        for(UserHandleInfo handleInfo : memberInfoList){
            groupAddOrRemoveJSON.members.add(handleInfo);
        }
        return groupAddOrRemoveJSON;
    }

    /**
     * 创建一个群邀请的对象
     * @return
     */
    public static AddOrRemoveDiscussionJSON createInvite(User inviter, @Nullable UserHandleInfo selfInfo, String pinCode) {
        AddOrRemoveDiscussionJSON groupAddOrRemoveJSON = new AddOrRemoveDiscussionJSON();
        groupAddOrRemoveJSON.inviter = inviter;
        groupAddOrRemoveJSON.ops = GroupOps.INVITE;
        groupAddOrRemoveJSON.pinCode = pinCode;
        if (null != selfInfo) {
            groupAddOrRemoveJSON.mAvatar = selfInfo.mAvatar;
            groupAddOrRemoveJSON.mShowName = selfInfo.mShowName;
            groupAddOrRemoveJSON.mStatus = selfInfo.mStatus;
        }

        return groupAddOrRemoveJSON;
    }

    /**
     * 创建一个删除人的对象
     *
     * @param contact
     * @return
     */
    public static AddOrRemoveDiscussionJSON createRemove(ShowListItem contact) {
        AddOrRemoveDiscussionJSON groupAddOrRemoveJSON = new AddOrRemoveDiscussionJSON();
        groupAddOrRemoveJSON.ops = GroupOps.KICK;
        groupAddOrRemoveJSON.members = new ArrayList<>();

        UserHandleInfo userHandleBasic = new UserHandleInfo();
        userHandleBasic.mUserId = contact.getId();
        userHandleBasic.mDomainId = contact.getDomainId();


        groupAddOrRemoveJSON.members.add(userHandleBasic);
        return groupAddOrRemoveJSON;
    }

    public enum GroupOps {

        /**
         * 添加人员
         */
        JOIN,

        /**
         * 删除人员
         */
        KICK,

        /**
         * 邀请人员
         * */
        INVITE
    }


}
