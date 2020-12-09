package com.foreveross.atwork.db.daoService;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.foreverht.cache.UserCache;
import com.foreverht.db.service.BaseDbService;
import com.foreverht.db.service.repository.UserRepository;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;

import java.util.List;

/**
 * 异步获取数据库user数据
 * Created by reyzhang22 on 16/4/8.
 */
public class UserDaoService extends BaseDbService {

    private static final String TAG = UserDaoService.class.getSimpleName();

    private static UserDaoService sInstance = new UserDaoService();

    private UserDaoService() {

    }

    public static UserDaoService getInstance() {
        return sInstance;
    }



    public void queryUserByUsername(final String username, final OnUserRepositoryListener listener) {
        new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... params) {
                return UserRepository.getInstance().queryUserByUsername(username);
            }

            @Override
            protected void onPostExecute(User user) {
                if (listener == null) {
                    return;
                }
                listener.onUserDataCallback(user);
            }
        }.executeOnExecutor(mDbExecutor);
    }


    /**
     * 插入一个人员
     *
     * @param user
     */
    public void insertUser(final User user) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return UserRepository.getInstance().insertUser(user);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 批量异步插入人员
     *
     * @param userList
     */
    @SuppressLint("StaticFieldLeak")
    public void batchInsertUser(final List<User> userList, int conflictAlgorithm) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return UserRepository.getInstance().batchInsertUsers(userList, conflictAlgorithm);
            }
        }.executeOnExecutor(mDbExecutor);
    }

    /**
     * 搜索 users
     * @param searchKey
     * @param searchVal
     * @param type
     * @param searchLocalContactListener
     * */
    @NonNull
    public void searchUsers(final String searchKey, final String searchVal, int type, final SearchLocalUsersListener searchLocalContactListener) {
        new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... params) {
                return UserRepository.getInstance().searchUsers(searchVal, type);
            }

            @Override
            protected void onPostExecute(List<User> users) {
                searchLocalContactListener.searchSuccess(searchKey, users);
            }
        }.executeOnExecutor(mDbExecutor);

    }

    @SuppressLint("StaticFieldLeak")
    public void updateUserBasicInfo(Context ctx, final String userId, String domainId, final String name, final String avatar, final String signature, long msgTimeStamp) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(userId)) {
            return;
        }

        User userCache = UserCache.getInstance().getUserCache(userId);
        if(userCache != null && !isDifferent(userCache, name, avatar, signature, msgTimeStamp)) {
            return;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                User user = UserManager.getInstance().queryUserInSyncByUserId(ctx, userId, domainId);

                if (user != null && isDifferent(user, name, avatar, signature, msgTimeStamp)) {
                    doUpdateUserBasicInfo(ctx, userId, domainId, name, avatar, signature, user);
                }
                return null;
            }




        }.executeOnExecutor(mDbExecutor);

    }


    private void doUpdateUserBasicInfo(Context ctx, final String userId, String domainId, final String name, final String avatar, final String signature, User user) {
        //update cache
        user.mAvatar = avatar;
        user.mName = name;
        user.mSignature = signature;
        user.mStatus = User.STATUS_INITIALIZED;

        //update session
        updateSessionName(userId, name);

        //notify change
        ChatDetailExposeBroadcastSender.changeUser(ctx, user);

        //update db
        UserRepository.getInstance().insertUser(user);

    }


    private boolean isDifferent(@NonNull User user, final String name, final String avatar, final String signature, long msgTimeStamp) {

        if(msgTimeStamp < user.mLastUpdateTime) {
            return false;
        }

        if(!user.isStatusInitialized()) {
            return true;
        }

        if(!name.equals(user.mName)) {
            return true;
        }

        if(!avatar.equals(user.mAvatar)) {
            return true;
        }

        if(!signature.equals(user.mSignature)) {
            return true;
        }

        return false;
    }

    private void updateSessionName(String userId, String name) {
        Session session = ChatSessionDataWrap.getInstance().getSession(userId, null);
        if(null != session) {
            session.name = name;

        }
    }

    @SuppressLint("StaticFieldLeak")
    public void updateUserActiveStatus(Context ctx, final String userId, String domainId) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                User user = UserManager.getInstance().queryUserInSyncByUserId(ctx, userId, domainId);
                if (user != null && !user.isStatusInitialized()) {

                    //update cache
                    user.mStatus = User.STATUS_INITIALIZED;

                    //notify change
                    ChatDetailExposeBroadcastSender.changeUser(ctx, user);

                    //update db
                    UserRepository.getInstance().insertUser(user);
                }
                return null;
            }

        }.executeOnExecutor(mDbExecutor);

    }

    public interface SearchLocalUsersListener {
        void searchSuccess(String searchKey, List<User> users);
    }

    public interface OnUserRepositoryListener {

        void onUserDataCallback(Object... objects);
    }
}
