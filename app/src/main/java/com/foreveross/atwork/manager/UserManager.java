package com.foreveross.atwork.manager;/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreverht.cache.UserCache;
import com.foreverht.db.service.repository.RelationshipRepository;
import com.foreverht.db.service.repository.UserRepository;
import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.threadGear.DbThreadPoolExecutor;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.RequestRemoteInterceptor;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.api.sdk.users.UserSyncNetService;
import com.foreveross.atwork.api.sdk.users.requestJson.ModifyPersonalSignatureJson;
import com.foreveross.atwork.api.sdk.users.requestJson.ModifyUserInfoJson;
import com.foreveross.atwork.api.sdk.users.requestJson.UserAvatarPost;
import com.foreveross.atwork.api.sdk.users.responseJson.QueryUserResponseJson;
import com.foreveross.atwork.api.sdk.users.responseJson.SearchUserResponseJson;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.db.daoService.RelationshipDaoService;
import com.foreveross.atwork.db.daoService.UserDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Relationship;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.manager.model.MultiResult;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.fragment.ChatInfoFragment;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * ????????????????????????????????????????????????
 * Created by reyzhang22 on 16/3/24.
 */
public class UserManager {
    private static final String REQUEST_ID_CHECK_USER_EMP_INFO_REMOTE_PREFIX = "REQUEST_ID_CHECK_USER_EMP_INFO_REMOTE_";
    private static final String TAG = UserManager.class.getSimpleName();

    private static UserManager sInstance = new UserManager();

    private UserManager() {

    }

    public static UserManager getInstance() {
        return sInstance;
    }


    public void addInterceptRequestIdCheckCheckUserEmpInfoRemote(String userId) {
        RequestRemoteInterceptor.INSTANCE.addInterceptRequestId(getRequestIdCheckUserEmpInfoRemote(userId));
    }

    public void removeInterceptRequestCheckCheckUserEmpInfoRemote(String userId) {
        RequestRemoteInterceptor.INSTANCE.removeInterceptRequestId(getRequestIdCheckUserEmpInfoRemote(userId));
    }

    public boolean checkLegalRequestIdCheckCheckUserEmpInfoRemote(String userId) {
        return RequestRemoteInterceptor.INSTANCE.checkLegal(getRequestIdCheckUserEmpInfoRemote(userId), 2 * 60 * 1000L);
    }

    public String getRequestIdCheckUserEmpInfoRemote(String userId) {
        return REQUEST_ID_CHECK_USER_EMP_INFO_REMOTE_PREFIX + userId;
    }


    /**
     * ????????????????????????????????????????????????
     *
     * @param context
     * @param listener
     */
    public void asyncFetchLoginUserFromRemote(final Context context, final UserAsyncNetService.OnQueryUserListener listener) {
        UserAsyncNetService.getInstance().queryLoginUserFromRemote(context, listener);
    }


    /**
     * ??????????????????????????????????????????????????????
     *
     * @param context
     * @param userId
     * @param domainId
     * @param listener
     */
    public void asyncQueryUserInfoFromRemote(final Context context, final String userId, final String domainId, UserAsyncNetService.OnUserCallBackListener listener) {
        UserAsyncNetService.getInstance().queryUserInfoFromRemote(context, userId, domainId, listener);
    }



    /**
     * ??????????????????????????????, ??????????????????, ????????????????????????
     *
     * @param context
     * @param user
     * @param isAdded
     * @param listener
     */
    public void addOrRemoveStarUser(final Context context, final User user, final boolean isAdded, UserAsyncNetService.OnHandleUserInfoListener listener) {
        AsyncTask<Void, Void, HttpResult> task = new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                HttpResult httpResult = UserSyncNetService.getInstance().addOrRemoveFlagUser(context, user, isAdded);
                if (httpResult.isRequestSuccess()) {

                    if (isAdded) {
                        RelationshipRepository.getInstance().addFlagRelationShip(user.mUserId);

                    } else {
                        RelationshipRepository.getInstance().removeFlagRelationShip(user.mUserId);
                    }
                }

                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult result) {
                if (result.isRequestSuccess()) {
                    listener.success();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(result, listener);
                }


            }
        };
        task.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    /**
     * ????????????????????????????????????
     *
     * @param users
     * @param isFriend
     */
    public void asyncAddUserRelationShipToLocal(Context context, List<User> users, boolean isFriend) {

        AsyncTaskThreadPool.getInstance().execute(() -> {
            batchSyncUserSync(context, User.toUserIdList(users));
        });

//        UserDaoService.getInstance().batchInsertUser(users, SQLiteDatabase.CONFLICT_REPLACE);
        RelationshipDaoService.getInstance().batchInsertRelationshipsAndClean(context, users, isFriend);
    }

    /**
     * ?????? username, ?????????????????????User
     * */
    @SuppressLint("StaticFieldLeak")
    public void queryUserByUserName(final Context context, final String username, final String domainId, UserAsyncNetService.OnQueryUserListener listener) {

        if (TextUtils.isEmpty(username)) {
            return;
        }

        //??????????????????
        User userCache = UserCache.getInstance().getUserCache(username);
        if(null != userCache) {
            listener.onSuccess(userCache);
            return;
        }

        new AsyncTask<Void, Void, MultiResult<User>>() {
            @Override
            protected MultiResult<User> doInBackground(Void... params) {
                return queryUserResultInSyncByUserName(context, username, domainId);
            }

            @Override
            protected void onPostExecute(@Nullable MultiResult<User> multiResult) {
                User user = getUserFromMultiResult(multiResult);

                if (null != user) {
                    listener.onSuccess(user);

                } else {
                    if (null != multiResult) {
                        NetworkHttpResultErrorHandler.handleHttpError(multiResult.httpResult, listener);
                    }
                }

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }




    /**
     * ?????? userId, ?????????????????????User
     * */
    @SuppressLint("StaticFieldLeak")
    public void queryUserByUserId(final Context context, final String userId, final String domainId, UserAsyncNetService.OnQueryUserListener listener) {

        if (TextUtils.isEmpty(userId)) {
            return;
        }

        //??????????????????
        User userCache = UserCache.getInstance().getUserCache(userId);
        if(null != userCache) {
            listener.onSuccess(userCache);
            return;
        }

        new AsyncTask<Void, Void, MultiResult<User>>() {
            @Override
            protected MultiResult<User> doInBackground(Void... params) {
                return queryUserResultInSyncByUserId(context, userId, domainId);
            }

            @Override
            protected void onPostExecute(@Nullable MultiResult<User> multiResult) {
                User user = getUserFromMultiResult(multiResult);

                if (null != user) {
                    listener.onSuccess(user);

                } else {
                    if (null != multiResult) {
                        NetworkHttpResultErrorHandler.handleHttpError(multiResult.httpResult, listener);
                    }
                }

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * ??????????????????id??????id????????????????????????????????????????????????????????????
     *
     * @param userId
     * @return
     */
    @Nullable
    public User queryUserInSyncByUserId(Context context, String userId, String domainId) {
        MultiResult<User> multiResult = queryUserResultInSyncByUserId(context, userId, domainId);
        User resultUser = getUserFromMultiResult(multiResult);
        return resultUser;
    }

    /**
     * ??????????????????username ??? domain_id????????????????????????????????????????????????????????????
     *
     * @param username
     * @return
     */
    @Nullable
    public User queryUserInSyncByUsername(Context context, String username, String domainId) {
        MultiResult<User> multiResult = queryUserResultInSyncByUserName(context, username, domainId);
        User resultUser = getUserFromMultiResult(multiResult);
        return resultUser;
    }

    @Nullable
    public User getUserFromMultiResult(MultiResult<User> multiResult) {
        if (multiResult == null) {
            return null;
        }
        User resultUser = null;
        if (null != multiResult.localResult) {
            resultUser = multiResult.localResult;
            return resultUser;
        }
        if (multiResult.httpResult.isRequestSuccess()) {
            User responseUser = ((QueryUserResponseJson) multiResult.httpResult.resultResponse).mUser;

            if(responseUser.isLegal()) {
                resultUser = responseUser;

            }
        }
        return resultUser;
    }

    public MultiResult<User> queryUserResultInSyncByUserId(Context context, String key, String domainId) {
        return queryUserResultInSyncById(context, key, domainId, "id");
    }

    public MultiResult<User> queryUserResultInSyncByUserName(Context context, String key, String domainId) {
        return queryUserResultInSyncById(context, key, domainId, "username");
    }

    @Nullable
    public MultiResult<User> queryUserResultInSyncById(Context context, String key, String domainId, String type) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        if(TextUtils.isEmpty(domainId)) {
            return null;
        }

        MultiResult<User> multiResult = new MultiResult<>();

        //????????????
        User user = UserCache.getInstance().getUserCache(key);
        //???????????????
        if (user == null) {
            if("id".equals(type)) {
                user = UserRepository.getInstance().queryUserByUserId(key);

            } else if("username".equals(type)){
                user = UserRepository.getInstance().queryUserByUsername(key);

            }

            if (user != null) {
                //???????????????
                UserCache.getInstance().setUserCache(user, type);
            }

        }

        //???????????????
        if (user == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(key).append("@").append(domainId);
            HttpResult httpResult = UserSyncNetService.getInstance().queryUserInfoFromRemote(context, sb.toString(), type);
            if (httpResult.isRequestSuccess()) {
                User userResult = ((QueryUserResponseJson) httpResult.resultResponse).mUser;
                if (userResult.isLegal()) {
                    user = userResult;
                }

            }

            if (user != null) {
                //???????????????????????????
                UserRepository.getInstance().insertUser(user, type);
            }

            multiResult.httpResult = httpResult;
        } else {
            multiResult.localResult = user;
        }
        return multiResult;

    }


    /**
     * ??????????????????????????????(?????? -> ??????)
     * */
    public List<User> batchQueryLocalUsers(List<String> userIdList) {
        List<User> localUserList = new ArrayList<>();
        List<String> userIdNotInCache = new ArrayList<>();

        for(String userId : userIdList) {
            User cacheUser = UserCache.getInstance().getUserCache(userId);
            if(null != cacheUser) {
                localUserList.add(cacheUser);

            } else {
                userIdNotInCache.add(userId);
            }
        }

        if (!ListUtil.isEmpty(userIdNotInCache)) {
            List<User> userInDbInList = UserRepository.getInstance().queryUsersByIdsWithNotExist(userIdNotInCache);
            localUserList.addAll(userInDbInList);
        }

        return localUserList;
    }

    /**
     * ????????????Id??????????????????
     *
     * @param userId
     * @return
     */
    public User queryLocalUser(String userId) {
        User user = UserCache.getInstance().getUserCache(userId);
        if (user != null) {
            return user;
        }
        return UserRepository.getInstance().queryUserByUserId(userId);
    }

    public void asyncAddUserToLocal(User user) {
        UserDaoService.getInstance().insertUser(user);
    }

    /**
     * ????????????Users
     *
     * @param userIdList
     * @param listener
     * @param needSyncNotExists
     */
    public void batchQueryUsers(Context context, final List<String> userIdList, final boolean needSyncNotExists, final UserAsyncNetService.OnUserCallBackListener listener) {
        new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... params) {
                if (needSyncNotExists) {
                    //??????????????????????????????
                    syncNotExistUsersSync(context, userIdList);
                }

                return UserRepository.getInstance().queryUsersByIdsWithNotExist(userIdList);
            }

            @Override
            protected void onPostExecute(List<User> contacts) {
                listener.onFetchUserDataSuccess(contacts);
            }
        }.executeOnExecutor(DbThreadPoolExecutor.getInstance());
    }


    public void syncNotExistUsers(Context context, List<String> userIdList) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                syncNotExistUsersSync(context, userIdList);
                return null;
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    public void syncNotExistUsersSync(Context context, List<String> userIdList) {
        List<String> unExistsUserIds = UserRepository.getInstance().queryUnExistUserIds(userIdList);
        batchSyncUserSync(context, unExistsUserIds);

    }

    public void batchSyncUserSync(Context context, List<String> userIds) {
        if (!ListUtil.isEmpty(userIds)) {

            //????????????, ?????? http 414
            int loop = userIds.size() / 100;
            for(int i = 0; i <= loop; i++) {
                int endIndex = (i + 1) * 100;
                int startIndex = i * 100;
                if(endIndex > userIds.size()) {
                    endIndex = userIds.size();
                }

                List<String> subUnExistsUserIds = userIds.subList(startIndex, endIndex);
                doSyncNotExistUserSync(context, subUnExistsUserIds);

            }

        }
    }

    private void doSyncNotExistUserSync(Context context, List<String> unExistsUserIds) {
        HttpResult httpResult = UserSyncNetService.getInstance().queryUserListFromRemote(context, unExistsUserIds);
        if (httpResult.isRequestSuccess()) {
            SearchUserResponseJson searchUserResponseJson = (SearchUserResponseJson) httpResult.resultResponse;
            List<User> unExistUserList = searchUserResponseJson.mResult.mUsers;

            if (!ListUtil.isEmpty(unExistUserList)) {
                UserRepository.getInstance().batchInsertUsers(unExistUserList, SQLiteDatabase.CONFLICT_REPLACE);

            }

        } else {
            if (null != httpResult.resultResponse) {
                ErrorHandleUtil.handleTokenError(httpResult.resultResponse.status, httpResult.resultResponse.message);

            }
        }
    }

    /**
     * ??????????????????
     * */
    @SuppressLint("StaticFieldLeak")
    public void dismissYourFriend(final Context context, final String friendDomainId, final String friendUserId, final OnDismissFriendListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                HttpResult httpResult = UserSyncNetService.getInstance().dismissFriend(context, friendDomainId, friendUserId);
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    if (0 == httpResult.resultResponse.status) {
                        //??????????????????
                        RelationshipDaoService.getInstance().removeRelationShip(friendUserId, String.valueOf(Relationship.Type.FRIEND));

                        //??????????????????, ???????????????????????????????????????????????????(??????????????????)
                        ChatSessionDataWrap.getInstance().removeSessionSafely(friendUserId);
                        ChatDetailExposeBroadcastSender.finishView(BaseApplicationLike.baseContext, friendUserId);
                        ChatInfoFragment.finishView(BaseApplicationLike.baseContext, friendUserId);

                        if(AtworkConfig.CHAT_CONFIG.getDismissFriendDeleteChatData()) {
                            ChatSessionDataWrap.getInstance().removeSession(friendUserId, true);
                        }

                        listener.onSuccess();

                    }
                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);

                }

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    /**
     * ??????????????????
     * */
    @SuppressLint("StaticFieldLeak")
    public void modifyUserAvatar(final Context ctx, String avatar, final UserAsyncNetService.OnHandleUserInfoListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                MultiResult<User> multiResult = AtworkApplicationLike.getLoginUserResultSync();
                User loginUser = getUserFromMultiResult(multiResult);


                if (null != loginUser) {
                    UserAvatarPost userAvatarPost = new UserAvatarPost();
                    userAvatarPost.avatar = avatar;
                    return UserSyncNetService.getInstance().modifyUserAvatar(ctx, loginUser.mUserId, userAvatarPost);

                } else {
                    //querying user's network failed
                    return multiResult.httpResult;
                }
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    listener.success();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    /**
     * ??????????????????
     * */
    @SuppressLint("StaticFieldLeak")
    public void modifyUserInfo(final Context ctx, String type, final ModifyUserInfoJson json, final UserAsyncNetService.OnHandleUserInfoListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                MultiResult<User> multiResult = AtworkApplicationLike.getLoginUserResultSync();
                User loginUser = getUserFromMultiResult(multiResult);

                if (null != loginUser) {
                    HttpResult httpResult = UserSyncNetService.getInstance().modifyUserInfo(ctx, loginUser.mUserId, json);

                    if (httpResult.isRequestSuccess()) {
                        if (ctx.getString(R.string.nickname).equals(type)) {
                            loginUser.mName = json.name;
                        }
                        if (ctx.getString(R.string.tel).equals(type)) {
                            loginUser.mPhone = json.phone;
                        }
                        if (ctx.getString(R.string.email).equals(type)) {
                            loginUser.mEmail = json.email;
                        }
                        if (ctx.getString(R.string.birthday).equals(type)) {
                            loginUser.mBirthday = json.birthday + "";
                        }
                        if (ctx.getString(R.string.sex).equals(type)) {
                            if (ctx.getString(R.string.male).equalsIgnoreCase(json.gender)) {
                                loginUser.mGender = "MALE";
                            }
                            if (ctx.getString(R.string.female).equalsIgnoreCase(json.gender)) {
                                loginUser.mGender = "FEMALE";
                            }
                        }

                        UserRepository.getInstance().insertUser(loginUser);
                    }

                    return httpResult;

                } else {
                    //querying user's network failed
                    return multiResult.httpResult;
                }
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {

                if (httpResult.isRequestSuccess()) {
                    listener.success();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    @SuppressLint("StaticFieldLeak")
    public void modifyUserName(final Context context, String type, final ModifyUserInfoJson json, final UserAsyncNetService.OnHandleUserInfoListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                MultiResult<User> multiResult = AtworkApplicationLike.getLoginUserResultSync();
                User loginUser = getUserFromMultiResult(multiResult);

                if (null != loginUser) {
                    HttpResult httpResult = UserSyncNetService.getInstance().modifyUserName(context, loginUser.mUserId, json);

                    if (httpResult.isRequestSuccess()) {
                        loginUser.mName = json.name;
                        UserRepository.getInstance().insertUser(loginUser);
                    }


                    return httpResult;
                } else {
                    //querying user's network failed
                    return multiResult.httpResult;
                }
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    listener.success();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }
    @SuppressLint("StaticFieldLeak")
    public void modifyPersonalSignature(final Context context, final ModifyPersonalSignatureJson json, final UserAsyncNetService.OnHandleUserInfoListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                MultiResult<User> multiResult = AtworkApplicationLike.getLoginUserResultSync();
                User loginUser = getUserFromMultiResult(multiResult);

                if (null != loginUser) {
                    HttpResult httpResult = UserSyncNetService.getInstance().modifyPersonalSignature(context, loginUser.mUserId, json);//????????????????????????
                    if (httpResult.isRequestSuccess()) {
                        loginUser.mSignature = json.moments;
                        UserRepository.getInstance().insertUser(loginUser);//?????????????????????
                    }


                    return httpResult;
                } else {
                    //querying user's network failed
                    return multiResult.httpResult;
                }
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    listener.success();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * ???????????????????????????
     * */
    @SuppressLint("StaticFieldLeak")
    public void isYourFriend(String otherUserId, OnCheckCanStarRelationListener listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return isYourFriendSync(otherUserId);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                listener.result(result);
            }
        }.executeOnExecutor(DbThreadPoolExecutor.getInstance());
    }

    @NonNull
    public Boolean isYourFriendSync(String otherUserId) {
        Relationship relationship = RelationshipRepository.getInstance().queryRelationShip(otherUserId, String.valueOf(Relationship.Type.FRIEND));
        return null != relationship;

    }




    /**
     * ????????????, ????????????, ????????????
     * */
    public String getReadableName(Context context, String userId, String domainId) {
        String name = userId;
        User user = queryUserInSyncByUserId(context, userId, domainId);
        if (null != user) {
            name = user.getShowName();
        }
        return name;
    }

    /**
     * ????????????, ????????????
     * */
    public void getReadableName(Context context, String userId, String domainId, OnGetNameListener listener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String name = userId;
                User user = queryUserInSyncByUserId(context, userId, domainId);
                if (null != user) {
                    name = user.getShowName();
                }
                return name;
            }

            @Override
            protected void onPostExecute(@NonNull String name) {
                listener.onBackName(name);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    public boolean isMe(String userId) {
        return LoginUserInfo.getInstance().getLoginUserId(AtworkApplicationLike.sApp).equalsIgnoreCase(userId);
    }

    public interface OnGetNameListener {
        void onBackName(String name);
    }


    public interface OnDismissFriendListener extends NetWorkFailListener {
        void onSuccess();
    }

    public interface OnCheckCanStarRelationListener {
        void result(boolean right);
    }



}
