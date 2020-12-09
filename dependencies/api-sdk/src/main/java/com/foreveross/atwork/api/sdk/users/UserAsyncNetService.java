package com.foreveross.atwork.api.sdk.users;/**
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
import androidx.annotation.Nullable;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener;
import com.foreveross.atwork.api.sdk.BaseNetWorkListener;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.users.requestJson.ConversationSettingRequest;
import com.foreveross.atwork.api.sdk.users.responseJson.GetCustomizationsResponse;
import com.foreveross.atwork.api.sdk.users.responseJson.ModifyPasswordResponse;
import com.foreveross.atwork.api.sdk.users.responseJson.QueryUserResponseJson;
import com.foreveross.atwork.api.sdk.users.responseJson.SearchUserResponseJson;
import com.foreveross.atwork.api.sdk.util.NetWorkHttpResultHelper;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.shared.FavoriteShareInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by reyzhang22 on 16/3/28.
 */
public class UserAsyncNetService {

    private static final String TAG = UserAsyncNetService.class.getName();

    private static final long CHECK_CUSTOMIZATION_INFO_TIME_INTERVAL = 5 * 60 * 1000;

    private static UserAsyncNetService sInstance = new UserAsyncNetService();

    private long mLastCheckCustomizationInfoTime = -1;

    private UserAsyncNetService() {

    }

    public static UserAsyncNetService getInstance() {
        return sInstance;
    }


    /**
     * 从服务器远端获取当前登录用户消息
     */
    public void queryLoginUserFromRemote(final Context context, final OnQueryUserListener listener) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, HttpResult> task = new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                UserSyncNetService service = UserSyncNetService.getInstance();
                HttpResult httpResult = service.queryLoginUserFromRemote(context);
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    QueryUserResponseJson resultResponse = (QueryUserResponseJson) httpResult.resultResponse;
                    listener.onSuccess(resultResponse.mUser);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }
            }
        };

        task.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 从服务器查询用户列表
     * @param context
     * @param searchKey
     * @param searchValue
     * @param listener
     */
    public void searchUserListFromRemote(final Context context, final String searchKey, final String searchValue, final OnSearchUserListListener listener) {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, HttpResult> task = new AsyncTask<Void, Void, HttpResult>() {

            @Override
            protected HttpResult doInBackground(Void... params) {
                String encodeSearchKey = searchValue.trim();
                try {
                    encodeSearchKey = URLEncoder.encode(encodeSearchKey, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                }
                UserSyncNetService service = UserSyncNetService.getInstance();
                HttpResult httpResult = service.searchUserListFromRemote(context, encodeSearchKey);
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {

                if(httpResult.isRequestSuccess()) {
                    SearchUserResponseJson searchUserResponseJson = (SearchUserResponseJson) httpResult.resultResponse;
                    listener.onSuccess(searchKey, searchUserResponseJson.mResult.mUsers);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }

            }
        };
        task.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    /**
     * 获取用其他户信息详情接口
     * @param context
     * @param userId
     * @param domainId
     * @param listener
     */
    public void queryUserInfoFromRemote(final Context context, final String userId, final String domainId, final OnUserCallBackListener listener) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, HttpResult> task = new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(userId).append("@").append(domainId);
                return UserSyncNetService.getInstance().queryUserInfoFromRemote(context, sb.toString());
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (listener == null) {
                    return;
                }

                if(httpResult.isRequestSuccess()) {
                    listener.onFetchUserDataSuccess(((QueryUserResponseJson)httpResult.resultResponse).mUser);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
                }
            }
        };
        task.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    @SuppressLint("StaticFieldLeak")
    public void modifyPassword(final Context context, final String userId, final String oldPassword, final String newPassword, final BaseNetWorkListener<ModifyPasswordResponse> listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                return UserSyncNetService.getInstance().modifyPassword(context, userId, oldPassword, newPassword);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    ModifyPasswordResponse response = (ModifyPasswordResponse) httpResult.resultResponse;
                    listener.onSuccess(response);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    /**
     * 设置相关开关
     * @param context
     * @param userId
     * @param domainId
     * @Param type      user/app
     * @param supportSyncWeChat  根据开关类型来设置, 若非微信开关操作, 需传入 null
     * @param remindMe  根据开关类型来设置, 若非消息提醒开关操作, 需传入 null
     * @param listener
     * */
    @SuppressLint("StaticFieldLeak")
    public void setConversationSetting(final Context context, final String userId, final String domainId, final String type, @Nullable final Boolean supportSyncWeChat, @Nullable final Boolean remindMe, final UserSyncNetService.OnUserConversationsListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                ConversationSettingRequest request = new ConversationSettingRequest();
                ConversationSettingRequest.Participant participant = new ConversationSettingRequest.Participant();
                participant.mClientId = userId;
                participant.mDomainId = domainId;
                participant.mType = type;

                request.mParticipant = participant;
                request.mWeChatSyncEnable = supportSyncWeChat;
                request.mNotifyEnable = remindMe;

                return UserSyncNetService.getInstance().setConversationSetting(context, new Gson().toJson(request), type, null);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (listener == null) {
                    return;
                }
                if (httpResult.isRequestSuccess()) {
                    listener.onSetConversationsSuccess();
                    return;
                }
                listener.networkFail(httpResult.statusCode, httpResult.error);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @SuppressLint("StaticFieldLeak")
    public void getUserConversations(final Context context, final String userId, final String domainId, final String type, final UserSyncNetService.GetUserConversationsListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                return UserSyncNetService.getInstance().getConversationSetting(context, userId, domainId, type);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (listener == null) {
                    return;
                }
                if (httpResult.isRequestSuccess()) {
                    String resultText = NetWorkHttpResultHelper.getResultText(httpResult.result);
                    ConversationSettingRequest request = new Gson().fromJson(resultText, ConversationSettingRequest.class);
                    listener.getConversationsSuccess(request);
                    return;
                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @SuppressLint("StaticFieldLeak")
    public void getCustomizationInfo(final Context context, @Nullable final BaseCallBackNetWorkListener baseNetWorkListener) {

        if(CHECK_CUSTOMIZATION_INFO_TIME_INTERVAL > System.currentTimeMillis() - mLastCheckCustomizationInfoTime) {
            return;
        }

        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... voids) {
                HttpResult httpResult = UserSyncNetService.getInstance().getCustomizationInfo(context);
                if(httpResult.isRequestSuccess()) {
                    GetCustomizationsResponse getCustomizationsResponse = (GetCustomizationsResponse) httpResult.resultResponse;

                    GetCustomizationsResponse.Result customizationsResponseResult = getCustomizationsResponse.getResult();
                    if(null != customizationsResponseResult) {
                        PersonalShareInfo.getInstance().setChatFileInWhitelist(context, customizationsResponseResult.isInChatFileWhitelist());
                        FavoriteShareInfo.getInstance().setFavWhiteList(context, customizationsResponseResult.isFavoriteWhiteList());
                        mLastCheckCustomizationInfoTime = System.currentTimeMillis();
                    }

                }
                return httpResult;
            }


            @Override
            protected void onPostExecute(HttpResult httpResult) {

                if(null == baseNetWorkListener ) {
                    return;
                }

                if(httpResult.isRequestSuccess()) {
                    GetCustomizationsResponse getCustomizationsResponse = (GetCustomizationsResponse) httpResult.resultResponse;
                    GetCustomizationsResponse.Result customizationsResponseResult = getCustomizationsResponse.getResult();

                    if(null != customizationsResponseResult) {
                        baseNetWorkListener.onSuccess();
                        return;
                    }

                }


                NetworkHttpResultErrorHandler.handleHttpError(httpResult, baseNetWorkListener);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    /**
     * 查询用户监听器
     */
    public interface OnQueryUserListener extends NetWorkFailListener{

        void onSuccess(@NonNull User user);
    }

    public interface OnSearchUserListListener extends NetWorkFailListener {
        void onSuccess(String searchKey, List<User> userList);
    }

    /**
     * 用户数据回调
     */
    public interface OnUserCallBackListener extends NetWorkFailListener {

        void onFetchUserDataSuccess(Object... object);
    }


    public interface OnUserOnlineListener {
        void onOnlineList(@NonNull List<String> onlineList);
    }

    /**
     * 操作 user 监听器
     * */
    public interface OnHandleUserInfoListener extends NetWorkFailListener{
        void success();
    }

}
