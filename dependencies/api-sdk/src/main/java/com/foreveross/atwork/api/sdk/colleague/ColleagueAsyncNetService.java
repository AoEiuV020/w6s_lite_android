package com.foreveross.atwork.api.sdk.colleague;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper;

import java.util.HashMap;

/**
 * Created by reyzhang22 on 15/9/17.
 */
public class ColleagueAsyncNetService {

    private static ColleagueAsyncNetService sInstance = new ColleagueAsyncNetService();

    public static ColleagueAsyncNetService getInstance() {
        return sInstance;
    }

    @SuppressLint("StaticFieldLeak")
    public void getLightNotice(final Context context, final String noticeUrl, final String ticket, final OnLightNoticeListener listener){

        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... params) {
                 String url = null;
                LoginUserBasic userBasic = LoginUserInfo.getInstance().getLoginUserBasic(context);
                try {
                    if (!TextUtils.isEmpty(AtworkConfig.COLLEAGUE_URL) && AtworkConfig.COLLEAGUE_URL.equalsIgnoreCase(noticeUrl)) {
                        String replaceStr = "%s"+"notify/?ticket=%s&domainId=%s&orgId=%s&username=%s&userId=%s";

                        url = String.format(replaceStr, noticeUrl, ticket, userBasic.mDomainId, PersonalShareInfo.getInstance().getCurrentOrg(context), userBasic.mUsername, userBasic.mUserId);

                    } else {
                        url = UrlHandleHelper.replaceBasicKeyParams(context, noticeUrl);

                        HashMap<String, String> insertParams = new HashMap<>();
                        insertParams.put("tenantId", AtworkConfig.DOMAIN_ID);
                        insertParams.put("username", userBasic.mUsername);
                        insertParams.put("userId", userBasic.mUserId);
                        insertParams.put("domainId", userBasic.mDomainId);
                        insertParams.put("orgId", PersonalShareInfo.getInstance().getCurrentOrg(context));
                        insertParams.put("ticket", ticket);

                        url = UrlHandleHelper.addParams(url, insertParams);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

                HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url, 5 * 1000);
                if(httpResult.isNetSuccess()) {
                    httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, LightNoticeData.class));
                }
                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {
                if (httpResult.isRequestSuccess()) {
                    LightNoticeData lightNoticeData = (LightNoticeData) httpResult.resultResponse;
                    if(lightNoticeData.isLegal()) {
                        listener.onSuccess((LightNoticeData) httpResult.resultResponse);
                        return;
                    }



                }

                NetworkHttpResultErrorHandler.handleHttpError(httpResult, listener);
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }



    public interface OnLightNoticeListener extends NetWorkFailListener{
        void onSuccess(LightNoticeData lightNoticeJson);
    }

}
