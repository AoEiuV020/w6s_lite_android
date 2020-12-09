package com.foreveross.atwork.api.sdk.auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.BaseNetWorkListener;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.auth.inter.OnEncryptInitListener;
import com.foreveross.atwork.api.sdk.auth.model.AuthPostJson;
import com.foreveross.atwork.api.sdk.auth.model.AuthResponseJson;
import com.foreveross.atwork.api.sdk.auth.model.LoginEndpointPostJson;
import com.foreveross.atwork.api.sdk.auth.model.LoginEndpointResponseJSON;
import com.foreveross.atwork.api.sdk.auth.model.LoginInitResp;
import com.foreveross.atwork.api.sdk.discussion.responseJson.DiscussionListResponseJson;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.users.responseJson.ContactSyncResponse;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;
import com.foreveross.atwork.infrastructure.utils.encryption.RsaUtilKt;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 * Created by lingen on 15/4/9.
 * Description:
 * 登录的网络请求服务
 */
public class LoginAsyncNetService {

    private Context context;

    private static final String TAG = LoginAsyncNetService.class.getSimpleName();

    public LoginAsyncNetService(Context context) {
        this.context = context.getApplicationContext();
    }



    /**
     * 请求IM信息
     *
     * @param endpointListener
     */
    @SuppressLint("StaticFieldLeak")
    public void endpointSync(final LoginEndpointPostJson postJson, final EndpointListener endpointListener) {

        HttpResult httpResult = LoginSyncNetService.endpoint(context, postJson);
        LoginEndpointResponseJSON loginEndpointResponseJSON = (LoginEndpointResponseJSON) httpResult.resultResponse;
        if(null != loginEndpointResponseJSON && null != loginEndpointResponseJSON.result && loginEndpointResponseJSON.apiSuccess()) {
            loginEndpointResponseJSON.saveToShared(context);
            endpointListener.endpointSuccess();

        } else {
            NetworkHttpResultErrorHandler.handleHttpError(httpResult, endpointListener);

        }


    }

    @SuppressLint("StaticFieldLeak")
    public void auth(final Context context, final String password, final String secureCode, final OnAuthListener onAuthListener) {
        //定义登录请求认证的参数对象
        final AuthPostJson autoPostJson = new AuthPostJson();
        //获取相应的参数
        autoPostJson.clientSecret = password;
        autoPostJson.originalPassword = password;

        if(!StringUtils.isEmpty(secureCode)) {
            autoPostJson.secureCode = secureCode;
        }
        doAuth(autoPostJson, onAuthListener);
    }

    private void doAuth(final AuthPostJson authPostJson, final OnAuthListener onAuthListener) {
        new AsyncTask<String, Double, HttpResult>() {
            @Override
            protected HttpResult doInBackground(String... params) {
                //认证
                HttpResult httpResult = LoginSyncNetService.auth(context, authPostJson);

                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {

                if(httpResult.isRequestSuccess()) {

                    onAuthListener.onAuth(((AuthResponseJson) httpResult.resultResponse).result);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, onAuthListener);

                }

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @SuppressLint("StaticFieldLeak")
    public void syncDiscussions(final BaseNetWorkListener<List<Discussion>> listener) {
        new AsyncTask<String, Double, HttpResult>() {
            @Override
            protected HttpResult doInBackground(String... params) {

                HttpResult httpResult = LoginSyncNetService.syncDiscussions(context);
                if(httpResult.isRequestSuccess()) {
                    DiscussionListResponseJson discussionListResponseJson = (DiscussionListResponseJson) httpResult.resultResponse;
                }
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    DiscussionListResponseJson discussionListResponseJson = (DiscussionListResponseJson) httpResult.resultResponse;
                    listener.onSuccess(discussionListResponseJson.mDiscussionList);

                } else {

                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @SuppressLint("StaticFieldLeak")
    public void syncFlagContacts(final BaseNetWorkListener<ContactSyncResponse> listener) {
        new AsyncTask<String, Double, HttpResult>() {
            @Override
            protected HttpResult doInBackground(String... params) {

                return LoginSyncNetService.syncFlagContacts(context);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if(httpResult.isRequestSuccess()) {
                    ContactSyncResponse resultResponse = (ContactSyncResponse) httpResult.resultResponse;
                    listener.onSuccess(resultResponse);

                } else {
                    NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    public interface OnAuthListener extends NetWorkFailListener {
        void onAuth(boolean result);
    }


    public interface EndpointListener extends NetWorkFailListener {

        void endpointSuccess();
    }

}
