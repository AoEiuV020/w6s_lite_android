package com.foreveross.atwork.modules.discussion.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreverht.cache.DiscussionCache;
import com.foreverht.db.service.repository.DiscussionMemberRepository;
import com.foreverht.db.service.repository.DiscussionRepository;
import com.foreveross.atwork.R;
import com.foreveross.atwork.db.daoService.DiscussionDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionMember;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionOwner;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionType;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.DiscussionNotifyMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.util.DiscussionHelper;
import com.foreveross.atwork.modules.chat.util.SystemChatMessageHelper;
import com.foreveross.atwork.utils.SystemMessageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import kotlin.collections.CollectionsKt;

/**
 * Created by dasunsy on 2017/4/1.
 */

public class DiscussionNotifyManger {

    private static DiscussionNotifyManger sInstance = new DiscussionNotifyManger();
    private HashMap<String, Object> mDiscussionIdLockMap = new HashMap<>();

    private DiscussionNotifyManger() {

    }

    public static DiscussionNotifyManger getInstance() {
        return sInstance;
    }
    /**
     * 处理通知类消息
     *
     * @param discussionNotifyMessage
     */
    public  void receiveDiscussionNotify(final DiscussionNotifyMessage discussionNotifyMessage) {

        //保证每个群组的通知操作的原子性
        synchronized (getDiscussionIdLockObject(discussionNotifyMessage)) {
            final LoginUserBasic me = LoginUserInfo.getInstance().getLoginUserBasic(BaseApplicationLike.baseContext);

            if (filterOperationNotify(discussionNotifyMessage, me.mUserId)) {
                notifyDiscussionEvent(BaseApplicationLike.baseContext, discussionNotifyMessage);
                return;
            }

            handleReceiveNotify(discussionNotifyMessage, me);
        }
    }

    private Object getDiscussionIdLockObject(DiscussionNotifyMessage discussionNotifyMessage) {
        if(!mDiscussionIdLockMap.containsKey(discussionNotifyMessage.to)) {
            mDiscussionIdLockMap.put(discussionNotifyMessage.to, new Object());
        }
        return mDiscussionIdLockMap.get(discussionNotifyMessage.to);
    }

    @Nullable
    private Void handleReceiveNotify(DiscussionNotifyMessage discussionNotifyMessage, LoginUserBasic me) {
        Context context = BaseApplicationLike.baseContext;

        final String discussionId = discussionNotifyMessage.to;

        final DiscussionNotifyMessage.GroupOperation groupOperation = discussionNotifyMessage.mOperation;

        SystemChatMessage systemChatMessage = null;


        //不处理未识别的操作
        if (null == discussionNotifyMessage.mOperation
                || DiscussionNotifyMessage.GroupOperation.UNKNOWN.equals(discussionNotifyMessage.mOperation)) {
            return null;
        }

        //更新群信息
        if (discussionNotifyMessage.mOperation.isNeedRefreshDiscussion()) {
            //移除群的缓存信息
            handleDiscussionNeedRefresh(context, discussionId);
        }

        //群名称变更
        if (DiscussionNotifyMessage.GroupOperation.NAME_CHANGED.equals(groupOperation)) {
            systemChatMessage = handleNameChanged(context, discussionId, discussionNotifyMessage, me);
        }
        //头像变更
        else if(DiscussionNotifyMessage.GroupOperation.AVATAR_CHANGED.equals(groupOperation)) {
            handleAvatarChanged(discussionId, discussionNotifyMessage);

        }
        //成员标签变更
        else if (DiscussionNotifyMessage.GroupOperation.TAG_CHANGED.equals(groupOperation)) {
            handleMemberTagsChanged(discussionId, discussionNotifyMessage);
        }
        //解散群
        else if (DiscussionNotifyMessage.GroupOperation.DISMISSED.equals(groupOperation)) {
            handleDismissed(discussionNotifyMessage);
        }
        //创建群
        else if (DiscussionNotifyMessage.GroupOperation.CREATED.equals(groupOperation)) {
            systemChatMessage = handleCreated(context, discussionId, discussionNotifyMessage);
        }
        //添加成员
        else if (DiscussionNotifyMessage.GroupOperation.MEMBER_JOINED.equals(groupOperation)) {
            systemChatMessage = handleMemberJoined(context, discussionId, discussionNotifyMessage, me);
        }

        //群邀请
        else if(DiscussionNotifyMessage.GroupOperation.MEMBER_INVITED.equals(groupOperation)) {
            systemChatMessage = handleMemberInvited(context, discussionId, discussionNotifyMessage, me);

        }
        //删除成员
        else if (DiscussionNotifyMessage.GroupOperation.MEMBER_KICKED.equals(groupOperation)) {
            systemChatMessage = handleMemberKicked(context, discussionId, discussionNotifyMessage, me);
        }
        //退群
        else if (DiscussionNotifyMessage.GroupOperation.MEMBER_LEAVED.equals(groupOperation)) {
            systemChatMessage = handleMemberLeaved(context, discussionId, me, discussionNotifyMessage);
        }
        //群主转让
        else if(DiscussionNotifyMessage.GroupOperation.TRANSFER.equals(groupOperation)) {
            systemChatMessage = handleTransfered(context, discussionId, discussionNotifyMessage);
        }
        //有人成为群主
        else if(DiscussionNotifyMessage.GroupOperation.OWNER.equals(groupOperation)) {
            systemChatMessage = handleOwner(context, discussionId, discussionNotifyMessage);
        }

        if (systemChatMessage != null) {
            //发出广播，通知收到新消息
            SystemMessageHelper.newSystemMessageNotice(context, systemChatMessage);
        }

        return null;
    }

    private SystemChatMessage handleOwner(Context context, String discussionId, DiscussionNotifyMessage discussionNotifyMessage) {
        SystemChatMessage systemChatMessage;
        systemChatMessage = updateInternalBecomeOwnerIfNeed(context, discussionId, discussionNotifyMessage);
        if(null != systemChatMessage) {
            ChatSessionDataWrap.getInstance().asyncReceiveChatMessage(systemChatMessage);
        }
        return systemChatMessage;
    }

    private SystemChatMessage handleTransfered(Context context, String discussionId, DiscussionNotifyMessage discussionNotifyMessage) {
        SystemChatMessage systemChatMessage;
        systemChatMessage = updateTransferedNewOwnerIfNeed(context, discussionId, discussionNotifyMessage);
        if(null != systemChatMessage) {
            ChatSessionDataWrap.getInstance().asyncReceiveChatMessage(systemChatMessage);
        }
        return systemChatMessage;
    }

    private SystemChatMessage handleMemberLeaved(Context context, String discussionId, LoginUserBasic me, DiscussionNotifyMessage discussionNotifyMessage) {
        SystemChatMessage systemChatMessage = null;
        //如果退群者是自己，则删除群组
        if (me.mUserId.equals(discussionNotifyMessage.mOperator.mUserId)) {
            DiscussionDaoService.getInstance().removeDiscussion(discussionId);
            ChatSessionDataWrap.getInstance().removeSessionSafely(discussionId);
            DiscussionHelper.notifySessionInvalidDiscussionKicked(discussionId);

        } else {
            String content = getSystemMessageContent(context,discussionNotifyMessage);
            if (!StringUtils.isEmpty(content)) {
                systemChatMessage = SystemChatMessageHelper.createMessageByDiscussionNoticeMessage(content, me.mUserId, discussionNotifyMessage);
                ChatSessionDataWrap.getInstance().asyncReceiveChatMessage(systemChatMessage);
            }


            DiscussionMemberRepository.getInstance().removeDiscussionMember(discussionId, discussionNotifyMessage.mOperator.mUserId);
            //检查退群的人是否是群主, 是群主的话则去除掉群主关系
            Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(context, discussionId);
            if(null != discussion && discussion.isOwner(discussionNotifyMessage.mOperator.mUserId)) {
                DiscussionRepository.getInstance().updateDiscussionOwner(discussion.mDiscussionId, null);
            }

            DiscussionCache.getInstance().removeDiscussionCache(discussionId);
            updateNewOwnerIfNeed(context, discussionId, discussionNotifyMessage);
        }
        return systemChatMessage;
    }


    @Nullable
    private SystemChatMessage handleMemberKicked(Context context, String discussionId, DiscussionNotifyMessage discussionNotifyMessage, LoginUserBasic me) {
        SystemChatMessage systemChatMessage = null;


        //如果被踢者包括自己，则删除群组
        if (discussionContainsMe(discussionNotifyMessage.mMembers, me.mUserId)) {
            DiscussionDaoService.getInstance().removeDiscussion(discussionId);
            ChatSessionDataWrap.getInstance().removeSessionSafely(discussionId);
            DiscussionHelper.notifySessionInvalidDiscussionKicked(discussionId);
            DiscussionHelper.notifyDiscussionRemovedSystemSession(discussionNotifyMessage, true);


        } else {

            systemChatMessage = removeMemberEvent(context, discussionNotifyMessage);
            if (null != systemChatMessage) {
                ChatSessionDataWrap.getInstance().asyncReceiveChatMessage(systemChatMessage);
            }

            for (DiscussionMember member : discussionNotifyMessage.mMembers) {
                DiscussionMemberRepository.getInstance().removeDiscussionMember(discussionId, member.userId);
            }

            DiscussionCache.getInstance().removeDiscussionCache(discussionId);

        }
        return systemChatMessage;
    }


    /**
     * 判断你是否群通知对应的群组的群主, 弥补后台缺少 owner 字段的场景
     * @param discussionNotifyMessage
     * */
    private boolean isYouOwnerCompatible(DiscussionNotifyMessage discussionNotifyMessage) {
        Context context = BaseApplicationLike.baseContext;
        if(discussionNotifyMessage.isOwnerLegal()) {
            return discussionNotifyMessage.isYouOwner(context);
        }

        Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(context, discussionNotifyMessage.getDiscussionId());
        if(null != discussion && discussion.isOwner(LoginUserInfo.getInstance().getLoginUserId(context))) {
            return true;
        }

        return false;
    }

    private SystemChatMessage handleMemberInvited(Context context, String discussionId, DiscussionNotifyMessage discussionNotifyMessage, LoginUserBasic me) {
        SystemChatMessage systemChatMessage;
        systemChatMessage = inviteMemberEvent(context, discussionNotifyMessage);

        ChatSessionDataWrap.getInstance().asyncReceiveChatMessage(systemChatMessage);

        if(discussionContainsMe(discussionNotifyMessage.mMembers, me.mUserId)) {
            Discussion discussion = DiscussionManager.getInstance().updateDiscussionByRemoteSync(context, discussionId);
            if (null != discussion) {
                syncNotExistMember(context, discussionId, discussion.mMemberList);
            }
        } else {
            DiscussionMemberRepository.getInstance().batchInsertDiscussionMembers(discussionNotifyMessage.mMembers);
            syncNotExistMember(context, discussionId, discussionNotifyMessage.mMembers);
            DiscussionCache.getInstance().removeDiscussionCache(discussionId);

        }
        return systemChatMessage;
    }

    private SystemChatMessage handleMemberJoined(Context context, String discussionId, DiscussionNotifyMessage discussionNotifyMessage, LoginUserBasic me) {
        SystemChatMessage systemChatMessage;
        systemChatMessage = addMemberEvent(context, discussionNotifyMessage);
        ChatSessionDataWrap.getInstance().asyncReceiveChatMessage(systemChatMessage);


        //若添加的是自己, 则需要添加群组信息
        if(discussionContainsMe(discussionNotifyMessage.mMembers, me.mUserId)) {
            Discussion discussion = DiscussionManager.getInstance().updateDiscussionByRemoteSync(context, discussionId);
            if (null != discussion) {
                syncNotExistMember(context, discussionId, discussion.mMemberList);
            }

        } else {
            DiscussionMemberRepository.getInstance().batchInsertDiscussionMembers(discussionNotifyMessage.mMembers);
            syncNotExistMember(context, discussionId, discussionNotifyMessage.mMembers);
            DiscussionCache.getInstance().removeDiscussionCache(discussionId);

        }
        return systemChatMessage;
    }

    private SystemChatMessage handleCreated(Context context, String discussionId, DiscussionNotifyMessage discussionNotifyMessage) {
        SystemChatMessage systemChatMessage = null;
        DiscussionManager.getInstance().updateDiscussionByRemoteSync(context, discussionId);
        syncNotExistMember(context, discussionId, discussionNotifyMessage.mMembers);

        //部门群且消息体里 members 为空时, 过滤掉创群通知
        if (DiscussionType.SYSTEM.equals(discussionNotifyMessage.mType) && ListUtil.isEmpty(discussionNotifyMessage.mMembers)) {
            //do nothing
        } else {
            systemChatMessage = createDiscussionEvent(context, discussionNotifyMessage);
            ChatSessionDataWrap.getInstance().asyncReceiveChatMessage(systemChatMessage);
        }

        return systemChatMessage;
    }

    private void handleDismissed(DiscussionNotifyMessage discussionNotifyMessage) {
        DiscussionDaoService.getInstance().removeDiscussionSync(discussionNotifyMessage.getDiscussionId());
        ChatSessionDataWrap.getInstance().removeSessionSafely(discussionNotifyMessage.getDiscussionId());


        if (!discussionNotifyMessage.isFromSelfFromThisDevice(BaseApplicationLike.baseContext)) {
            DiscussionHelper.notifySessionInvalidDiscussionDismissed(discussionNotifyMessage.getDiscussionId());
            DiscussionHelper.notifyDiscussionRemovedSystemSession(discussionNotifyMessage, true);
        }

    }

    private void handleAvatarChanged(String discussionId, DiscussionNotifyMessage discussionNotifyMessage) {
        String newAvatar = discussionNotifyMessage.mMoreInfo.get("display_avatar");
        DiscussionRepository.getInstance().updateDiscussionAvatar(discussionId, newAvatar);

        Discussion cache = DiscussionCache.getInstance().getDiscussionCache(discussionId);
        if(null != cache) {
            cache.mAvatar = newAvatar;
        }
    }

    private void handleMemberTagsChanged(String discussionId, DiscussionNotifyMessage discussionNotifyMessage) {
        DiscussionMemberRepository.getInstance().batchUpdateDiscussionMembersTags(discussionNotifyMessage.mMembers);
        Discussion cache = DiscussionCache.getInstance().getDiscussionCache(discussionId);
        if(null != cache) {
            cache.mMemberList.removeAll(discussionNotifyMessage.mMembers);
            cache.mMemberList.addAll(discussionNotifyMessage.mMembers);
        }
    }

    private SystemChatMessage handleNameChanged(Context context, String discussionId, DiscussionNotifyMessage discussionNotifyMessage, LoginUserBasic me) {
        SystemChatMessage systemChatMessage = null;
        String modifyDiscussionName = null;
        String modifyDiscussionPinyin = null;
        String modifyDiscussionInitial = null;

        if (discussionNotifyMessage.mMembers != null) {
            modifyDiscussionName = discussionNotifyMessage.mMoreInfo.get("name");
            modifyDiscussionPinyin = discussionNotifyMessage.mMoreInfo.get("pinyin");
            modifyDiscussionInitial = discussionNotifyMessage.mMoreInfo.get("initial");
        }

        if (!StringUtils.isEmpty(modifyDiscussionName)) {
            String content;
            if (discussionNotifyMessage.isInternalDiscussion()) {
                content = context.getResources().getString(R.string.internal_discussion_modify_name, modifyDiscussionName);

            } else {
                content = context.getResources().getString(R.string.modify_group_name, getOperationReadableNameReplaceMe(context, discussionNotifyMessage.mOperator), modifyDiscussionName);

            }
            systemChatMessage = SystemChatMessageHelper.createMessageByDiscussionNoticeMessage(content, me.mUserId, discussionNotifyMessage);

            DiscussionRepository.getInstance().updateDiscussionName(discussionId, modifyDiscussionName, modifyDiscussionPinyin, modifyDiscussionInitial);

            Discussion cache = DiscussionCache.getInstance().getDiscussionCache(discussionId);
            if(null != cache) {
                cache.mName = modifyDiscussionName;
                if (!StringUtils.isEmpty(modifyDiscussionPinyin)) {
                    cache.mPinyin = modifyDiscussionPinyin;
                }

                if (!StringUtils.isEmpty(modifyDiscussionInitial)) {
                    cache.mInitial = modifyDiscussionInitial;
                }
            }

            ChatSessionDataWrap.getInstance().updateSessionByName(discussionId, modifyDiscussionName);
            ChatSessionDataWrap.getInstance().asyncReceiveChatMessage(systemChatMessage);

        }
        return systemChatMessage;
    }

    private void handleDiscussionNeedRefresh(Context context, String discussionId) {
        Log.d("PROFILE_CHANGED", "PROFILE_CHANGED");
        DiscussionCache.getInstance().removeDiscussionCache(discussionId);
        Discussion discussion = DiscussionManager.getInstance().updateDiscussionByRemoteSync(context, discussionId);
        ChatSessionDataWrap.getInstance().updateSessionByDiscussion(discussion);
    }

    /**
     * 一个群组收到批量消息
     *
     * @param discussionId
     * @param discussionNotifyMessageList
     */
    public void receiveDiscussionOfflineNotifys(Context context, final String discussionId, final List<DiscussionNotifyMessage> discussionNotifyMessageList) {
        final LoginUserBasic me = LoginUserInfo.getInstance().getLoginUserBasic(context);

        DiscussionNotifyMessage lastDiscussionNotifyMessage = discussionNotifyMessageList.get(discussionNotifyMessageList.size() - 1);


        boolean remove = shouldRemoveDiscussion(lastDiscussionNotifyMessage, me.mUserId);

        if (!remove) {
            //TIP: 首先先判断群是否在服务器端存在,服务器端不存在，直接不处理下面消息
            Discussion discussion = DiscussionManager.getInstance().updateDiscussionByRemoteSync(context, discussionId);
            if (discussion == null) {
                return;
            }
            handleNotifysHavingSystemMessage(context, discussionNotifyMessageList, me);


            //更新群信息
            DiscussionCache.getInstance().removeDiscussionCache(discussionId);

            ChatSessionDataWrap.getInstance().updateSessionByDiscussion(discussion);
        }

    }


    public void receiveDiscussionOfflineRemoveNotifys(Context context, final String discussionId, final List<DiscussionNotifyMessage> discussionNotifyMessageList) {
        final LoginUserBasic me = LoginUserInfo.getInstance().getLoginUserBasic(context);

        //最后一条群消息，是否是删除？是的话直接删除群
        DiscussionNotifyMessage lastDiscussionNotifyMessage = discussionNotifyMessageList.get(discussionNotifyMessageList.size() - 1);

        handleOfflineRemoveDiscussion(lastDiscussionNotifyMessage, me.mUserId);
    }

    public void handleNotifysHavingSystemMessage(Context context, List<DiscussionNotifyMessage> discussionNotifyMessageList, LoginUserBasic me) {
        //否则进行群更新
        for (DiscussionNotifyMessage discussionNotifyMessage : discussionNotifyMessageList) {

            String content = getSystemMessageContent(context, discussionNotifyMessage);

            if (!StringUtils.isEmpty(content)) {
                SystemChatMessage systemChatMessage = SystemChatMessageHelper.createMessageByDiscussionNoticeMessage(content, me.mUserId, discussionNotifyMessage);


                ChatSessionDataWrap.getInstance().asyncReceiveChatMessage(systemChatMessage);

                SystemMessageHelper.newSystemMessageNotice(context, systemChatMessage);
            }
        }
    }

    @Nullable
    private String getSystemMessageContent(Context context, DiscussionNotifyMessage discussionNotifyMessage) {
        final String discussionIdTo = discussionNotifyMessage.to;
        final DiscussionNotifyMessage.GroupOperation groupOperation = discussionNotifyMessage.mOperation;

        String content = null;
        //创建群
        if (DiscussionNotifyMessage.GroupOperation.CREATED.equals(groupOperation)) {
            content = context.getResources().getString(R.string.enter_discussion_tip, getMembers(context, discussionIdTo, discussionNotifyMessage.mMembers));

        }

        //添加成员
        if (DiscussionNotifyMessage.GroupOperation.MEMBER_JOINED.equals(groupOperation)) {
            if(discussionNotifyMessage.isInternalDiscussion()) {
                content = context.getResources().getString(R.string.enter_discussion_tip, getMembersForReplaceMe(context, discussionNotifyMessage.mMembers));

            } else {
                String operationReadableNameReplaceMe = getOperationReadableNameReplaceMe(context, discussionNotifyMessage.mOperator);
                if(StringUtils.isEmpty(operationReadableNameReplaceMe)) {
                    content = context.getResources().getString(R.string.enter_discussion_tip, getMembersForReplaceMe(context, discussionNotifyMessage.mMembers));

                } else {
                    content = context.getResources().getString(R.string.add_group_member, operationReadableNameReplaceMe, getMembersForReplaceMe(context, discussionNotifyMessage.mMembers));

                }

            }
        }

        if (DiscussionNotifyMessage.GroupOperation.MEMBER_INVITED.equals(groupOperation)) {
            content = context.getResources().getString(R.string.invite_group_member,
                    getMembersForReplaceMe(context, discussionNotifyMessage.mMembers),
                    getOperationReadableNameReplaceMe(context, discussionNotifyMessage.mOperator))

                    + context.getResources().getString(R.string.key_tip_invite_group_member);

        }


        //删除成员
        if (DiscussionNotifyMessage.GroupOperation.MEMBER_KICKED.equals(groupOperation)) {

            if(!AtworkConfig.DISSCUSSION_CONFIG.isUserLeaveNotify()) {
                return null;
            }

            //只有群猪才有通知
            if (!isYouOwnerCompatible(discussionNotifyMessage)) {
                return null;
            }

            if (discussionNotifyMessage.isInternalDiscussion()) {
                content = context.getResources().getString(R.string.leave_group_member, getMembersForReplaceMe(context, discussionNotifyMessage.mMembers));

            } else {
                content = context.getResources().getString(R.string.kick_group_member, getOperationReadableNameReplaceMe(context, discussionNotifyMessage.mOperator), getMembersForReplaceMe(context, discussionNotifyMessage.mMembers));

            }
        }

        //群名称变更
        if (DiscussionNotifyMessage.GroupOperation.NAME_CHANGED.equals(groupOperation)) {
            String modifyGroupName = null;

            if (discussionNotifyMessage.mMoreInfo != null) {
                modifyGroupName = discussionNotifyMessage.mMoreInfo.get("name");
            }

            if (!StringUtils.isEmpty(modifyGroupName)) {
                content = context.getResources().getString(R.string.modify_group_name, getOperationReadableNameReplaceMe(context, discussionNotifyMessage.mOperator), modifyGroupName);
            }

        }

        //退群
        if (DiscussionNotifyMessage.GroupOperation.MEMBER_LEAVED.equals(groupOperation)) {

            if(!AtworkConfig.DISSCUSSION_CONFIG.isUserLeaveNotify()) {
                return null;
            }


            //只有群猪才有通知
            if (!isYouOwnerCompatible(discussionNotifyMessage)) {
                return null;
            }

            content = context.getResources().getString(R.string.leave_group_member, getOperationReadableNameReplaceMe(context, discussionNotifyMessage.mOperator));
        }

        //转让群主
        if(DiscussionNotifyMessage.GroupOperation.TRANSFER.equals(groupOperation)) {
            if(null != discussionNotifyMessage.mMoreInfo) {
                DiscussionOwner newDiscussionOwner = DiscussionOwner.build(discussionNotifyMessage.mMoreInfo.get("new_owner"));
                if(null != newDiscussionOwner && newDiscussionOwner.isYouOwner(context)) {
                    content = context.getResources().getString(R.string.you_become_owner, getOperationReadableName(context, discussionNotifyMessage.mOperator));
                }
            }
        }

        if(DiscussionNotifyMessage.GroupOperation.OWNER.equals(groupOperation)) {
            if(null != discussionNotifyMessage.mMoreInfo) {
                DiscussionOwner newDiscussionOwner = DiscussionOwner.build(discussionNotifyMessage.mMoreInfo.get("owner"));
                if(null != newDiscussionOwner) {
                    content = context.getResources().getString(R.string.internal_discussion_someone_become_owner, getOperationReadableNameReplaceMe(context, newDiscussionOwner.mUserId, newDiscussionOwner.mDomainId, null));
                }
            }

        }
        return content;
    }

    @Nullable
    private SystemChatMessage updateTransferedNewOwnerIfNeed(Context context, String discussionId, DiscussionNotifyMessage discussionNotifyMessage) {
        SystemChatMessage systemChatMessage = null;

        if (null != discussionNotifyMessage.mMoreInfo) {

            String newOwner = discussionNotifyMessage.mMoreInfo.get("new_owner");
            DiscussionOwner discussionOwner = DiscussionOwner.build(newOwner);
            if(null != discussionOwner) {
                DiscussionRepository.getInstance().updateDiscussionOwner(discussionId, discussionOwner);
                systemChatMessage = transferOwnerEvent(context, discussionOwner, discussionNotifyMessage);
            }
        }

        return systemChatMessage;
    }

    @Nullable
    private SystemChatMessage updateInternalBecomeOwnerIfNeed(Context context, String discussionId, DiscussionNotifyMessage discussionNotifyMessage) {
        SystemChatMessage systemChatMessage = null;

        if (null != discussionNotifyMessage.mMoreInfo) {
            //如果owner有值，表示群主要变更
            String owner = discussionNotifyMessage.mMoreInfo.get("owner");
            DiscussionOwner discussionOwner = DiscussionOwner.build(owner);
            if(null != discussionOwner) {
                DiscussionRepository.getInstance().updateDiscussionOwner(discussionId, discussionOwner);
                systemChatMessage = internalBecomeOwnerEvent(context, discussionOwner, discussionNotifyMessage);

            }

        }

        return systemChatMessage;
    }

    private void updateNewOwnerIfNeed(Context context, String discussionId, DiscussionNotifyMessage discussionNotifyMessage) {
        if(null != discussionNotifyMessage.mDiscussionOwner) {
            DiscussionRepository.getInstance().updateDiscussionOwner(discussionId, discussionNotifyMessage.mDiscussionOwner);

        }
    }



    public SystemChatMessage getOfflineSystemChatMessage(Context context, final DiscussionNotifyMessage discussionNotifyMessage, LoginUserBasic me){
        final String discussionId = discussionNotifyMessage.to;
        SystemChatMessage systemChatMessage = new SystemChatMessage();
        systemChatMessage.deliveryId = discussionNotifyMessage.deliveryId;
        systemChatMessage.from = discussionId;
        systemChatMessage.to = me.mUserId;
        systemChatMessage.type = SystemChatMessage.Type.NOTIFY_DISCUSSION;

        String content = getSystemMessageContent(context, discussionNotifyMessage);

        if (!StringUtils.isEmpty(content)) {
            //发出广播，通知收到新消息
            systemChatMessage.content = content;
            systemChatMessage.deliveryTime = discussionNotifyMessage.deliveryTime;
            return systemChatMessage;

        }else{
            return null;
        }

    }


    private void handleOfflineRemoveDiscussion(DiscussionNotifyMessage discussionNotifyMessage, String meUserId) {
        if(shouldRemoveDiscussion(discussionNotifyMessage, meUserId)) {
            DiscussionDaoService.getInstance().removeDiscussionSync(discussionNotifyMessage.to);
            ChatSessionDataWrap.getInstance().removeSessionSyncSafely(discussionNotifyMessage.to, true);


            if(DiscussionNotifyMessage.GroupOperation.DISMISSED.equals(discussionNotifyMessage.mOperation)) {
                DiscussionHelper.notifySessionInvalidDiscussionDismissed(discussionNotifyMessage.to);
                DiscussionHelper.notifyDiscussionRemovedSystemSession(discussionNotifyMessage, false);


            } else if(DiscussionNotifyMessage.GroupOperation.MEMBER_KICKED.equals(discussionNotifyMessage.mOperation)){
                DiscussionHelper.notifySessionInvalidDiscussionKicked(discussionNotifyMessage.to);
                DiscussionHelper.notifyDiscussionRemovedSystemSession(discussionNotifyMessage, false);


            }

        }
    }


    private boolean shouldRemoveDiscussion(DiscussionNotifyMessage discussionNotifyMessage, String meUserId) {


        if ((DiscussionNotifyMessage.GroupOperation.DISMISSED.equals(discussionNotifyMessage.mOperation))
                || ((DiscussionNotifyMessage.GroupOperation.MEMBER_KICKED.equals(discussionNotifyMessage.mOperation) && discussionContainsMe(discussionNotifyMessage.mMembers, meUserId)))) {

            return true;
        }

        return false;
    }

    public boolean discussionContainsMe(List<DiscussionMember> members, String meUserId) {
        if (StringUtils.isEmpty(meUserId)) {
            return false;
        }
        for (DiscussionMember member : members) {
            if (member == null) {
                continue;
            }
            if (meUserId.equalsIgnoreCase(member.userId)) {
                return true;
            }
        }
        return false;
    }


    public boolean filterOperationNotify(DiscussionNotifyMessage discussionNotifyMessage, String meUserId) {
        if(null == discussionNotifyMessage.mOperator) {
            return false;
        }

        return filterNotifyBasic(discussionNotifyMessage, meUserId) || filterNotifyMemberInvite(discussionNotifyMessage, meUserId);
    }

    public boolean filterNotifyBasic(DiscussionNotifyMessage discussionNotifyMessage, String meUserId) {
        return meUserId.equalsIgnoreCase(discussionNotifyMessage.mOperator.mUserId)
                && discussionNotifyMessage.isFromThisDevice()
                && (DiscussionNotifyMessage.GroupOperation.MEMBER_LEAVED.equals(discussionNotifyMessage.mOperation)
                || DiscussionNotifyMessage.GroupOperation.MEMBER_JOINED.equals(discussionNotifyMessage.mOperation)
                || DiscussionNotifyMessage.GroupOperation.MEMBER_KICKED.equals(discussionNotifyMessage.mOperation)
                || DiscussionNotifyMessage.GroupOperation.CREATED.equals(discussionNotifyMessage.mOperation)
                || DiscussionNotifyMessage.GroupOperation.TRANSFER.equals(discussionNotifyMessage.mOperation)
                || DiscussionNotifyMessage.GroupOperation.OWNER.equals(discussionNotifyMessage.mOperation)
        );
    }

    public boolean filterNotifyMemberInvite(DiscussionNotifyMessage discussionNotifyMessage, String meUserId) {
        return discussionNotifyMessage.isFromThisDevice()
                && DiscussionNotifyMessage.GroupOperation.MEMBER_INVITED.equals(discussionNotifyMessage.mOperation)
                && !ListUtil.isEmpty(discussionNotifyMessage.mMembers)
                && meUserId.equalsIgnoreCase(discussionNotifyMessage.mMembers.get(0).userId);


    }

    private void notifyDiscussionEvent(Context context, DiscussionNotifyMessage discussionNotifyMessage) {
        SystemChatMessage systemChatMessage = null;

        if (DiscussionNotifyMessage.GroupOperation.CREATED.equals(discussionNotifyMessage.mOperation)) {
            systemChatMessage = createDiscussionEvent(context, discussionNotifyMessage);

        } else if(DiscussionNotifyMessage.GroupOperation.MEMBER_JOINED.equals(discussionNotifyMessage.mOperation)) {
            systemChatMessage = addMemberEvent(context, discussionNotifyMessage);

        } else if(DiscussionNotifyMessage.GroupOperation.MEMBER_KICKED.equals(discussionNotifyMessage.mOperation)) {
            systemChatMessage = removeMemberEvent(context, discussionNotifyMessage);

        } else if(DiscussionNotifyMessage.GroupOperation.MEMBER_INVITED.equals(discussionNotifyMessage.mOperation)){
            systemChatMessage = inviteMemberEvent(context, discussionNotifyMessage);

        } else if(DiscussionNotifyMessage.GroupOperation.OWNER.equals(discussionNotifyMessage.mOperation)) {
            String owner = discussionNotifyMessage.mMoreInfo.get("owner");
            DiscussionOwner discussionOwner = DiscussionOwner.build(owner);
            if(null != discussionOwner) {
                systemChatMessage = internalBecomeOwnerEvent(context, discussionOwner, discussionNotifyMessage);

            }

        }

        if(null != systemChatMessage) {
            ChatSessionDataWrap.getInstance().asyncReceiveChatMessage(systemChatMessage);

            SystemMessageHelper.newSystemMessageNotice(context, systemChatMessage);
        }

    }

    @Nullable
    private SystemChatMessage transferOwnerEvent(Context context, @NonNull DiscussionOwner newOwner, DiscussionNotifyMessage discussionNotifyMessage) {
        SystemChatMessage systemChatMessage = null;
        if(newOwner.isYouOwner(context)) {
            LoginUserBasic me = LoginUserInfo.getInstance().getLoginUserBasic(context);
            String  content = context.getString(R.string.you_become_owner, getOperationReadableName(context, discussionNotifyMessage.mOperator));
            systemChatMessage = SystemChatMessageHelper.createMessageByDiscussionNoticeMessage(content, me.mUserId, discussionNotifyMessage);

        }

        return systemChatMessage;
    }

    private SystemChatMessage internalBecomeOwnerEvent(Context context, @NonNull DiscussionOwner newOwner, DiscussionNotifyMessage discussionNotifyMessage) {
        LoginUserBasic me = LoginUserInfo.getInstance().getLoginUserBasic(context);

        SystemChatMessage systemChatMessage;
        String content = context.getResources().getString(R.string.internal_discussion_someone_become_owner, getOperationReadableNameReplaceMe(context, newOwner.mUserId, newOwner.mDomainId, null));
        systemChatMessage = SystemChatMessageHelper.createMessageByDiscussionNoticeMessage(content, me.mUserId, discussionNotifyMessage);

        return systemChatMessage;
    }

    @Nullable
    private SystemChatMessage removeMemberEvent(Context context, DiscussionNotifyMessage discussionNotifyMessage) {
        LoginUserBasic me = LoginUserInfo.getInstance().getLoginUserBasic(context);

        SystemChatMessage systemChatMessage = null;
        String content = getSystemMessageContent(context, discussionNotifyMessage);
        if (!StringUtils.isEmpty(content)) {
            systemChatMessage = SystemChatMessageHelper.createMessageByDiscussionNoticeMessage(content, me.mUserId, discussionNotifyMessage);
        }

        return systemChatMessage;
    }

    private SystemChatMessage addMemberEvent(Context context, DiscussionNotifyMessage discussionNotifyMessage) {
        LoginUserBasic me = LoginUserInfo.getInstance().getLoginUserBasic(context);
        String content;

        if(discussionNotifyMessage.isInternalDiscussion()) {
            content = context.getResources().getString(R.string.enter_discussion_tip, getMembersForReplaceMe(context, discussionNotifyMessage.mMembers));

        } else {


            String operationReadableNameReplaceMe = getOperationReadableNameReplaceMe(context, discussionNotifyMessage.mOperator);
            if(StringUtils.isEmpty(operationReadableNameReplaceMe)) {
                content = context.getResources().getString(R.string.enter_discussion_tip, getMembersForReplaceMe(context, discussionNotifyMessage.mMembers));

            } else {
                content = context.getResources().getString(R.string.add_group_member, operationReadableNameReplaceMe, getMembersForReplaceMe(context, discussionNotifyMessage.mMembers));

            }

        }

        SystemChatMessage systemChatMessage = SystemChatMessageHelper.createMessageByDiscussionNoticeMessage(content, me.mUserId, discussionNotifyMessage);
        return systemChatMessage;
    }

    /**
     * 添加人员消息通知
     *
     * @param discussion
     * @param fakeUserList
     */
    public void addMemberToGroupEvent(Context context, final Discussion discussion, List<User> fakeUserList) {
        final String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        final String meUserDomainId = LoginUserInfo.getInstance().getLoginUserDomainId(context);

        List<DiscussionMember> memberList = new ArrayList<>();
        for(User fakeUser : fakeUserList) {
            DiscussionMember member = new DiscussionMember();
            member.userId = fakeUser.mUserId;
            member.domainId = fakeUser.mDomainId;
            member.name = UserManager.getInstance().getReadableName(context, fakeUser.mUserId, fakeUser.mDomainId);

            memberList.add(member);
        }


        String content =  context.getResources().getString(R.string.add_group_member, context.getResources().getString(R.string.you), getMembersForReplaceMe(context, memberList));
        SystemChatMessage systemChatMessage = new SystemChatMessage(SystemChatMessage.Type.NOTIFY_DISCUSSION, content, meUserId, discussion.mDiscussionId, meUserDomainId, discussion.mDomainId);

        ChatSessionDataWrap.getInstance().asyncReceiveChatMessage(systemChatMessage);
        SystemMessageHelper.newSystemMessageNotice(context, systemChatMessage);
    }


    private SystemChatMessage inviteMemberEvent(Context context, DiscussionNotifyMessage discussionNotifyMessage) {
        LoginUserBasic me = LoginUserInfo.getInstance().getLoginUserBasic(context);

        SystemChatMessage systemChatMessage;
        String content = context.getResources().getString(R.string.invite_group_member,
                getMembersForReplaceMe(context, discussionNotifyMessage.mMembers),
                getOperationReadableNameReplaceMe(context, discussionNotifyMessage.mOperator))
                + context.getResources().getString(R.string.key_tip_invite_group_member);

        systemChatMessage = SystemChatMessageHelper.createMessageByDiscussionNoticeMessage(content, me.mUserId, discussionNotifyMessage);
        return systemChatMessage;
    }


    private SystemChatMessage createDiscussionEvent(Context context, DiscussionNotifyMessage discussionNotifyMessage) {
        String discussionId = discussionNotifyMessage.to;
        LoginUserBasic me = LoginUserInfo.getInstance().getLoginUserBasic(context);

        String content = context.getResources().getString(R.string.enter_discussion_tip, getMembers(context, discussionId, discussionNotifyMessage.mMembers));
        SystemChatMessage systemChatMessage = SystemChatMessageHelper.createMessageByDiscussionNoticeMessage(content, me.mUserId, discussionNotifyMessage);
        return systemChatMessage;
    }




    /**
     * 创建群通知
     *
     * @param discussion
     */
    @Deprecated
    public void createDiscussionEvent(Context context, final Discussion discussion) {
        final String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        final String meUserDomainId = LoginUserInfo.getInstance().getLoginUserDomainId(context);
        String content = context.getResources().getString(R.string.enter_discussion_tip, getMembers(context, discussion.mDomainId, discussion.mMemberList));
        SystemChatMessage systemChatMessage = new SystemChatMessage(SystemChatMessage.Type.NOTIFY_DISCUSSION, content, meUserId, discussion.mDiscussionId, meUserDomainId, discussion.mDomainId);
        ChatSessionDataWrap.getInstance().asyncReceiveChatMessage(systemChatMessage);

        SystemMessageHelper.newSystemMessageNotice(context, systemChatMessage);
    }

    public String getMembers(Context context, String discussionDomainId, List<DiscussionMember> memberList) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (DiscussionMember member : memberList) {
            if (i != 0) {
                stringBuilder.append("、");
            }
            if (TextUtils.isEmpty(member.name)) {
                User queryUser = UserManager.getInstance().queryUserInSyncByUserId(context, member.userId, discussionDomainId);

                if (queryUser != null && !TextUtils.isEmpty(queryUser.getShowName())) {
                    stringBuilder.append(queryUser.getShowName());
                    i++;
                    continue;
                }
            }
            stringBuilder.append(member.name);
            i++;
        }
        return stringBuilder.toString();
    }


    public String getOperationReadableNameReplaceMe(Context context, DiscussionNotifyMessage.Operator operator) {
        String name = StringUtils.EMPTY;
        if(null != operator) {
            return getOperationReadableNameReplaceMe(context, operator.mUserId, operator.mDomainId, operator.mName);
        }

        return name;

    }

    public String getOperationReadableName(Context context, DiscussionNotifyMessage.Operator operator) {
        String name = StringUtils.EMPTY;
        if(null != operator) {
            return getOperationReadableName(context, operator.mUserId, operator.mDomainId, operator.mName);
        }

        return name;

    }


    public String getOperationReadableNameReplaceMe(Context context, String operatorUserId, String operatorDomainId, String operatorName) {

        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);

        if (meUserId.equalsIgnoreCase(operatorUserId)) {
            return BaseApplicationLike.baseContext.getString(R.string.you);
        }

        return getOperationReadableName(context, operatorUserId, operatorDomainId, operatorName);
    }

    public String getOperationReadableName(Context context, String operatorUserId, String operatorDomainId, String operatorName) {
        if(!StringUtils.isEmpty(operatorName)) {
            return operatorName;
        }

        String name = StringUtils.EMPTY;

        User user = UserManager.getInstance().queryUserInSyncByUserId(context, operatorUserId, operatorDomainId);
        if (user != null && !TextUtils.isEmpty(user.getShowName())) {
            return user.getShowName();
        }
        return name;
    }

    public String getMembersForReplaceMe(Context context, List<DiscussionMember> members) {
        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        StringBuilder builder = new StringBuilder();
        int i = 0;

        for (DiscussionMember member : members) {
            if (i != 0) {
                builder.append("，");
            }
            if (member.userId != null && member.userId.equalsIgnoreCase(meUserId)) {
                builder.append(BaseApplicationLike.baseContext.getString(R.string.you));
                i++;
                continue;
            }
            if (TextUtils.isEmpty(member.name)) {
                User queryUser = UserManager.getInstance().queryUserInSyncByUserId(context, member.userId, member.domainId);
                if (queryUser != null && !TextUtils.isEmpty(queryUser.getShowName())) {
                    builder.append(queryUser.getShowName());
                    i++;
                    continue;
                }
            }
            builder.append(member.name);
            i++;
        }
        return builder.toString();
    }

    private void syncNotExistMember(Context ctx, String discussionId, List<DiscussionMember> memberList) {
        List<String> idList = new ArrayList<>();
        for(DiscussionMember member : memberList) {
            idList.add(member.userId);
        }

        Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(ctx, discussionId);
        if (null != discussion && discussion.showEmployeeInfo()) {
            EmployeeManager.getInstance().syncNotExistEmpListSync(ctx, idList, discussion.getOrgCodeCompatible());
        } else {
            UserManager.getInstance().syncNotExistUsers(ctx, idList);

        }
    }





}
