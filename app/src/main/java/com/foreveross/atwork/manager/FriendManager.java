package com.foreveross.atwork.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.db.service.repository.UserRepository;
import com.foreverht.threadGear.DbThreadPoolExecutor;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.db.daoService.RelationshipDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Relationship;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.FriendNotifyMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.utils.AsyncTaskSentry;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.modules.chat.util.SystemChatMessageHelper;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by dasunsy on 16/5/20.
 */
public class FriendManager {
    private static final Object sLock = new Object();

    public static final String ACTION_REFRESH_NEW_FRIEND_APPLIES = "ACTION_REFRESH_NEW_FRIEND_APPLIES";

    private static FriendManager sInstance;

    private List<User> mFriendList = new CopyOnWriteArrayList<>();
    private boolean mReload = true;


    public static FriendManager getInstance() {


        if(null == sInstance) {

            //double check
            synchronized (sLock) {
                if (null == sInstance) {
                    sInstance = new FriendManager();
                }
            }
        }

        return sInstance;
    }

    public List<User> getFriendListSync() {
        checkFriendListData();
        return mFriendList;
    }

    @Nullable
    public List<User> getFriendListNotCheck() {
        return mFriendList;
    }

    public void setFriendsTotally(List<User> friendList) {
//        synchronized (sLock) {
//            if(null == mFriendList) {
//                mFriendList = new ArrayList<>();
//            }
//        }

        synchronized (sLock) {
            mFriendList.clear();

            for(User friend: friendList) {
                if(!mFriendList.contains(friend)) {
                    mFriendList.add(friend);
                }
            }

            mReload = false;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void addFriend(User friend) {

        new AsyncTaskSentry.AsyncTaskWatching<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                addFriendSync(friend);

                return null;
            }
        }.executeOnExecutor(DbThreadPoolExecutor.getInstance());

    }

    private void addFriendSync(User friend) {
        checkFriendListData();

        if(!mFriendList.contains(friend)) {
            mFriendList.add(friend);
        }
    }


    @SuppressLint("StaticFieldLeak")
    public void removeFriend(String friendUserId) {

        new AsyncTaskSentry.AsyncTaskWatching<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                removeFriendSync(friendUserId);
                return null;
            }
        }.executeOnExecutor(DbThreadPoolExecutor.getInstance());

    }

    public void removeFriendSync(String friendUserId) {
        checkFriendListData();

        User friendUserRemoved = null;

        for(User friendUser : mFriendList) {
            if(friendUserId.equals(friendUser.mUserId)) {
                friendUserRemoved = friendUser;
                break;
            }
        }

        if(null != friendUserRemoved) {
            mFriendList.remove(friendUserRemoved);
        }
    }

    public void clear() {
        if (null != mFriendList) {
            mFriendList.clear();
        }

        mReload = true;
    }

    public boolean containsKey(String friendUserId) {
        if(null == mFriendList) {
            return false;
        }

        for(User friendUser : mFriendList) {
            if(friendUserId.equals(friendUser.mUserId)) {
                return true;
            }
        }

        return false;
    }

    public void notifyRefreshNewFriendApplies() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ACTION_REFRESH_NEW_FRIEND_APPLIES));
    }

    private void checkFriendListData() {
        synchronized (sLock) {

            if(mReload) {
                mFriendList.clear();
                mFriendList.addAll(UserRepository.getInstance().queryFriendUsers());

                mReload = true;
            }
        }
    }

    public void receiveFriendNotify(FriendNotifyMessage friendNotifyMessage, boolean isCameFromOnline) {


        switch (friendNotifyMessage.mOperation) {
            case APPLYING:
                receiveFriendApplyingNotify(friendNotifyMessage, isCameFromOnline);

                break;

            case APPROVED:
                receiveFriendApprovedNotify(friendNotifyMessage, isCameFromOnline);

                break;

            case REMOVED:
                receiveFriendRemovedNotify(friendNotifyMessage);

                break;
        }


    }

    private void receiveFriendRemovedNotify(FriendNotifyMessage friendNotifyMessage) {
        Context context = BaseApplicationLike.baseContext;
        final LoginUserBasic me = LoginUserInfo.getInstance().getLoginUserBasic(context);

        RelationshipDaoService.getInstance().removeRelationShip(friendNotifyMessage.mAddresser.mUserId, String.valueOf(Relationship.Type.FRIEND));

        removeChatData(me, friendNotifyMessage);
    }

    private void receiveFriendApprovedNotify(FriendNotifyMessage friendNotifyMessage, boolean isCameFromOnline) {
        Context context = BaseApplicationLike.baseContext;
        final LoginUserBasic me = LoginUserInfo.getInstance().getLoginUserBasic(BaseApplicationLike.baseContext);

        if(LoginUserInfo.getInstance().getLoginUserId(context).equals(friendNotifyMessage.mOperator.mUserId)) {
            PersonalShareInfo.getInstance().removeNewFriendApplyUserId(context, friendNotifyMessage.mAddresser.mUserId);
            FriendManager.getInstance().notifyRefreshNewFriendApplies();
        }


        String userIdSaved = "";
        String domainIdSaved = "";
        String nameSaved = "";

        //如果接受者是自己, 那需要保存申请者的关系
        if (me.mUserId.equals(friendNotifyMessage.mOperator.mUserId)) {
            userIdSaved = friendNotifyMessage.mAddresser.mUserId;
            domainIdSaved = friendNotifyMessage.mAddresser.mDomainId;
            nameSaved = friendNotifyMessage.mAddresser.mName;

            FriendManager.getInstance().notifyRefreshNewFriendApplies();

        } else {
            userIdSaved = friendNotifyMessage.mOperator.mUserId;
            domainIdSaved = friendNotifyMessage.mOperator.mDomainId;
            nameSaved = friendNotifyMessage.mOperator.mName;



        }

        String content;

        if(LoginUserInfo.getInstance().getLoginUserId(context).equals(friendNotifyMessage.mOperator.mUserId)) {
            content = context.getResources().getString(R.string.me_accept_friend_tip_head) + friendNotifyMessage.mAddresser.mName + context.getString(R.string.me_accept_friend_tip_tail);

        } else {
            content = context.getResources().getString(R.string.other_accept_friend_tip);

        }


        SystemChatMessage systemChatMessage = SystemChatMessageHelper.createMessageByFriendNoticeMessage(content, userIdSaved, domainIdSaved, friendNotifyMessage);
        ChatSessionDataWrap.getInstance().asyncReceiveMessage(systemChatMessage, isCameFromOnline);
        ChatMessageHelper.notifyMessageReceived(systemChatMessage);
        SessionRefreshHelper.notifyRefreshSessionAndCount();


        Relationship friend = new Relationship();
        friend.mType = Relationship.Type.FRIEND;
        friend.mUserId = userIdSaved;

        RelationshipDaoService.getInstance().insertRelationship(friend, null);

        updateUserFromRemote(context, userIdSaved, domainIdSaved);

        handleNotification(context, friendNotifyMessage, isCameFromOnline, me, userIdSaved, systemChatMessage);
    }


    private void receiveFriendApplyingNotify(FriendNotifyMessage friendNotifyMessage, boolean isCameFromOnline) {
        Context context = BaseApplicationLike.baseContext;
        final LoginUserBasic me = LoginUserInfo.getInstance().getLoginUserBasic(BaseApplicationLike.baseContext);

        if (me.mUserId.equals(friendNotifyMessage.mAddresser.mUserId)) {
            return;
        }

        PersonalShareInfo.getInstance().addNewFriendsApplyUserId(context, friendNotifyMessage.mAddresser.mUserId);
        FriendManager.getInstance().notifyRefreshNewFriendApplies();

//            String sessionName = context.getString(R.string.new_friend_in_btn);
        String sessionName = StringConstants.SESSION_NAME_FRIEND_NOTICE;

        EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest()
                .setChatType(SessionType.Notice)
                .setName(sessionName)
                .setIdentifier(friendNotifyMessage.from)
                .setDomainId(friendNotifyMessage.mFromDomain);
        //创建会话
        Session session = ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);
        ChatSessionDataWrap.getInstance().updateSessionForNotify(session, friendNotifyMessage, isCameFromOnline);

        if(isCameFromOnline && DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature()) {
            MessageNoticeManager.getInstance().showNotifyNotification(context, session);
        }
    }

    private void handleNotification(Context context, FriendNotifyMessage friendNotifyMessage, boolean isCameFromOnline, LoginUserBasic me, String userIdSaved, SystemChatMessage systemChatMessage) {
        //自己处理的不用通知栏提醒
        if(!me.mUserId.equals(friendNotifyMessage.mOperator.mUserId)) {
            Session session = ChatSessionDataWrap.getInstance().getSession(userIdSaved, systemChatMessage);

            if(null == session) {
                return;
            }

            if(isCameFromOnline && DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature()) {
                //通知栏通知
                MessageNoticeManager.getInstance().showChatMsgNotification(context, systemChatMessage, session, false);
            }

        }
    }

    private void updateUserFromRemote(Context context, String userIdSaved, String domainIdSaved) {
        UserManager.getInstance().asyncQueryUserInfoFromRemote(context, userIdSaved, domainIdSaved, new UserAsyncNetService.OnUserCallBackListener() {
            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
            }

            @Override
            public void onFetchUserDataSuccess(Object... object) {
                User user = (User) object[0];

                //todo user 拉取失败的情况
                addFriend(user);
                UserManager.getInstance().asyncAddUserToLocal(user);

                //update online status
                OnlineManager.getInstance().setOnlineStatus(user.mUserId, user.isOnline());
            }

        });
    }

    private void removeChatData(LoginUserBasic me, FriendNotifyMessage friendNotifyMessage) {
        if(!AtworkConfig.CHAT_CONFIG.getDismissFriendDeleteChatData()) {
            return;
        }

        String userIdHandle;
        if (me.mUserId.equals(friendNotifyMessage.mOperator.mUserId)) {
            userIdHandle = friendNotifyMessage.mAddresser.mUserId;

        } else {
            userIdHandle = friendNotifyMessage.mOperator.mUserId;

        }

        removeFriendSync(userIdHandle);
        ChatSessionDataWrap.getInstance().removeSession(userIdHandle, true);
    }


}
