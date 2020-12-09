package com.foreveross.atwork.api.sdk.discussion;/**
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

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.discussion.requestJson.DiscussionSettingsRequest;
import com.foreveross.atwork.api.sdk.discussion.responseJson.DiscussionSettingsResponse;
import com.foreveross.atwork.api.sdk.discussion.responseJson.QueryReadOrUnreadResponse;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.users.UserSyncNetService;
import com.foreveross.atwork.api.sdk.util.NetWorkHttpResultHelper;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by reyzhang22 on 16/3/25.
 */
public class DiscussionAsyncNetService {

    private static final String TAG = DiscussionAsyncNetService.class.getName();

    private static DiscussionAsyncNetService sInstance = new DiscussionAsyncNetService();

    private DiscussionAsyncNetService() {

    }

    public static DiscussionAsyncNetService getInstance() {
        return sInstance;
    }


    @Deprecated
    @SuppressLint("StaticFieldLeak")
    public void queryReadUnread(final Context ctx, final String readOrUnread, final String messageId, final DiscussionQueryReadUnreadListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                return DiscussionSyncNetService.getInstance().queryReadUnread(ctx, messageId, readOrUnread);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
//                NetworkHttpResultErrorHandler.handleError(httpResult, listener);

                if (httpResult.isNetSuccess()) {
                    QueryReadOrUnreadResponse responseJSON = new Gson().fromJson(httpResult.result, QueryReadOrUnreadResponse.class);
                    if (responseJSON != null && responseJSON.status == 0) {
                        listener.success(responseJSON.resultList);
                    }
                }


            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }


    public void setDiscussionSettings(final Context context, final String discussionId, final DiscussionSettingsRequest request, final UserSyncNetService.OnUserConversationsListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                DiscussionSyncNetService syncNetService = DiscussionSyncNetService.getInstance();

                return syncNetService.setDiscussionSettings(context, discussionId, JsonUtil.toJson(request));
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

    public void getDiscussionSettings(final Context context, final String discussionId, final UserSyncNetService.GetUserConversationsListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                return DiscussionSyncNetService.getInstance().getDiscussionSettings(context, discussionId);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (listener == null) {
                    return;
                }
                if (httpResult.isRequestSuccess()) {
                    String resultText = NetWorkHttpResultHelper.getResultText(httpResult.result);
                    DiscussionSettingsResponse request = new Gson().fromJson(resultText, DiscussionSettingsResponse.class);
                    listener.getConversationsSuccess(request);
                    return;
                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    /**
     * 添加或删除成员或退群接口
     */
    public interface HandledResultListener extends NetWorkFailListener{
        void success();

    }

    public interface OnCreateDiscussionListener extends NetWorkFailListener {
        void onDiscussionSuccess(Discussion discussion);

    }

    /**
     * 查询群组的接口
     * */
    public interface OnQueryDiscussionListener extends NetWorkFailListener{
        void onSuccess(@NonNull Discussion discussion);
    }

    public interface OnDiscussionListListener{
        void onDiscussionSuccess(List<Discussion> discussionList);
    }

    /**
     * 修改群信息接口
     */
    public interface DiscussionQueryReadUnreadListener {

        void success(List<QueryReadOrUnreadResponse.QueryReadUnreadResult> resultJsonList);

    }
}
