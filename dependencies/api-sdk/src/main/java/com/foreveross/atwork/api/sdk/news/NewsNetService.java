package com.foreveross.atwork.api.sdk.news;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.news.model.News;
import com.foreveross.atwork.api.sdk.news.model.NewsJson;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;

/**
 * Created by reyzhang22 on 15/9/9.
 */
public class NewsNetService {

    private static final String TAG = NewsNetService.class.getSimpleName();

    private static final String NEWS_API_KEY = "04b50c58699d0b6be7c949025099034a";

    private static final String NEWS_SOCIAL_URL = "http://api.huceo.com/social/?key=%s&num=1";

    private static NewsNetService sInstance = new NewsNetService();

    public static NewsNetService getInstance() {
        synchronized (TAG) {
            if (sInstance == null) {
                sInstance = new NewsNetService();
            }
            return sInstance;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void getNews(final GetNewsListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                String url = String.format(NEWS_SOCIAL_URL, NEWS_API_KEY);
                return HttpURLConnectionComponent.getInstance().getHttp(url);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {

                if (httpResult.isNetSuccess()) {
                    NewsJson json = JsonUtil.fromJson(httpResult.result, NewsJson.class);
                    if (json != null) {
                        News news  = json.toNews();
                        listener.getNewsSuccess(news);
                        return;
                    }

                }

                NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public interface GetNewsListener extends NetWorkFailListener {
        void getNewsSuccess(News news);
    }

}
