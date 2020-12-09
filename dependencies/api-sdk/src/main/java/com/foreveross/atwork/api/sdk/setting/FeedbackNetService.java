package com.foreveross.atwork.api.sdk.setting;

import android.content.Context;
import android.os.AsyncTask;

import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.setting.model.FeedbackJson;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.google.gson.Gson;

/**
 * Created by ReyZhang on 2015/5/11.
 */
public class FeedbackNetService {

    private static final String TAG = FeedbackNetService.class.getSimpleName();

    private static FeedbackNetService sInstance;


    public static FeedbackNetService getInstance() {
        synchronized (TAG) {
            if (sInstance == null) {
                sInstance = new FeedbackNetService();
            }
            return sInstance;
        }
    }


    public static HttpResult postFeedbackSync(final Context context, FeedbackJson feedbackJson) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().V2_getFeedBackUrl(), accessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(feedbackJson));
        return httpResult;
    }

    /**
     * 反馈接口
     *
     * @param feedbackContent
     * @param listener
     */
    public static void postFeedback(final Context context, final String feedbackContent, final OnFeedbackSuccess listener) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().V2_getFeedBackUrl(), accessToken);

        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                FeedbackJson feedbackJson = FeedbackJson.getFeedbackJson(feedbackContent);

                String param = new Gson().toJson(feedbackJson);
                HttpResult httpResult = null;
                httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, param);
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {


                if (httpResult.isNetSuccess()) {
                    BasicResponseJSON responseJSON = NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class);
                    if (responseJSON != null && responseJSON.status == 0) {
                        listener.onThankYou();
                        return;
                    }
                }

                NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public interface OnFeedbackSuccess extends NetWorkFailListener {
        void onThankYou();
    }
}
