package com.foreveross.atwork.api.sdk.cordova;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.cordova.requestJson.UserTicketPostJson;
import com.foreveross.atwork.api.sdk.cordova.responseJson.UserTicketResponseJSON;
import com.foreveross.atwork.api.sdk.cordova.responseJson.UserTokenResponseJSON;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;


public class CordovaAsyncNetService {



    @SuppressLint("StaticFieldLeak")
    public static void getUserToken(Context context, final GetUserTokenListener getUserTokenListener) {
        String url = AtworkConfig.API_URL + "token/temporary?access_token=%s";
        final String getUserTokenUrl = String.format(url, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                return HttpURLConnectionComponent.getInstance().postHttp(getUserTokenUrl, "{}");
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {

                if(httpResult.isNetSuccess()) {
                    UserTokenResponseJSON userTokenResponseJSON = (UserTokenResponseJSON) NetGsonHelper.fromNetJson(httpResult.result, UserTokenResponseJSON.class);
                    if(null != userTokenResponseJSON && 0 == userTokenResponseJSON.status) {
                        getUserTokenListener.getUserTokenSuccess(userTokenResponseJSON.getUserToken());
                        return;
                    }

                }

                NetworkHttpResultErrorHandler.handleHttpError(httpResult, getUserTokenListener);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void getUserTicket(Context context, final GetUserTicketListener getUserTicketListener) {
        getUserTicket(context, null, null, getUserTicketListener);
    }



    @SuppressLint("StaticFieldLeak")
    public static void getUserTicket(final Context context, @Nullable final String orgCode, @Nullable final Integer readTimeReceiving, final GetUserTicketListener getUserTicketListener) {
        final String getUserTicketUrl = String.format(UrlConstantManager.getInstance().getTicketUrl(), LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {

                return getUserTicketSync(context, orgCode, getUserTicketUrl, readTimeReceiving);
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {


                if (httpResult.isRequestSuccess()) {
                    UserTicketResponseJSON userTicketResponseJSON = (UserTicketResponseJSON) httpResult.resultResponse;
                    if(!StringUtils.isEmpty(userTicketResponseJSON.getUserTicket())) {
                        getUserTicketListener.getUserTicketSuccess(userTicketResponseJSON.getUserTicket());
                        return;
                    }
                }


                CrashReport.postCatchedException(new Throwable("{{ticket}} http status : " + httpResult.status  + "info -> " + httpResult.result));

                getUserTicketListener.getUserTicketSuccess(StringUtils.EMPTY);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @NonNull
    public static HttpResult getUserTicketSync(Context context, @Nullable String orgCode, String getUserTokenUrl, @Nullable Integer readTimeReceiving) {
        UserTicketPostJson postJson = new UserTicketPostJson();

        if(!StringUtils.isEmpty(orgCode)) {
            postJson.mOgId = orgCode;

        } else {
            if (!StringUtils.isEmpty(BaseApplicationLike.sOrgId)) {
                postJson.mOgId = BaseApplicationLike.sOrgId;
            } else {
                postJson.mOgId = PersonalShareInfo.getInstance().getCurrentOrg(context);
            }
        }


        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(getUserTokenUrl, new Gson().toJson(postJson), readTimeReceiving);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, UserTicketResponseJSON.class));
        }

        return httpResult;
    }

    public static String getUserTicketResultSync(Context context, @Nullable String orgCode, String getUserTicketUrl, @Nullable Integer readTimeReceiving) {
        HttpResult httpResult = getUserTicketSync(context, orgCode, getUserTicketUrl, readTimeReceiving);
        if(httpResult.isRequestSuccess()) {
            UserTicketResponseJSON userTicketResponseJSON = (UserTicketResponseJSON) httpResult.resultResponse;
            if(!StringUtils.isEmpty(userTicketResponseJSON.getUserTicket())) {
                return userTicketResponseJSON.getUserTicket();
            }
        }

        return StringUtils.EMPTY;
    }


    public interface GetUserTokenListener extends NetWorkFailListener {
        void getUserTokenSuccess(String accessToken);
    }

    public interface GetUserTicketListener extends NetWorkFailListener {
        void getUserTicketSuccess(String userTicket);
    }
}
