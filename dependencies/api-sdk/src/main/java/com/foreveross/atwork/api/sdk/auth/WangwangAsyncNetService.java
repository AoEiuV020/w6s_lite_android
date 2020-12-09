package com.foreveross.atwork.api.sdk.auth;

import android.content.Context;
import android.os.AsyncTask;

import com.foreverht.threadGear.AsyncTaskThreadPool;

public class WangwangAsyncNetService {

    public static final String AUTH_TAG = "wantwant";


    public static void auth(final Context context, final OnResultListener onResultListener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {

                return WangwangSyncNetService.auth(context);
            }

            @Override
            protected void onPostExecute(String result) {
                onResultListener.onResult(result);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }


    public interface OnResultListener {
        void onResult(String result);
    }
}

