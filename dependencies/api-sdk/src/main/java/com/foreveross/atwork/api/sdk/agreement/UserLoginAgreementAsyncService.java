package com.foreveross.atwork.api.sdk.agreement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dasunsy on 2017/7/20.
 */

public class UserLoginAgreementAsyncService {


    @SuppressLint("StaticFieldLeak")
    public static void signUserLoginAgreement(final Context context, final BaseCallBackNetWorkListener listener) {
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... voids) {
                return UserLoginAgreementSyncService.signUserLoginAgreement(context);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    listener.onSuccess();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);

                }


            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    public static void signFaceProtocolAgreement(final Context context, final BaseCallBackNetWorkListener listener) {


        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... voids) {
                JSONObject json = new JSONObject();
                try {
                    json.put("type", "BIOLOGICAL_AUTH");
                } catch (JSONException e) {
                }
                return UserLoginAgreementSyncService.signFaceProtocolAgreement(context, json.toString());
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (listener == null) {
                    return;
                }
                if(httpResult.isRequestSuccess()) {
                    listener.onSuccess();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);

                }


            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public static void signFaceLoginAgreement(final Context context, final String ticket, final BaseCallBackNetWorkListener listener) {


        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... voids) {
                return UserLoginAgreementSyncService.signFaceLoginAgreement(context, ticket);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (listener == null) {
                    return;
                }
                if(httpResult.isRequestSuccess()) {
                    listener.onSuccess();

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);

                }


            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }
}
