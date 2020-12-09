package com.foreveross.atwork.api.sdk.agreement;

import android.content.Context;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.agreement.responseJson.AgreementStatusResponse;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;

/**
 * Created by dasunsy on 2017/7/20.
 */

public class UserLoginAgreementSyncService {

    public static String getUserLoginAgreementUrl(Context context) {
        String userAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().getUserLoginAgreement(), AtworkConfig.DOMAIN_ID, userAccessToken);
        return url;
    }

    public static String getFaceProtocolAgreementUrl(Context context) {
        String userAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().getFaceBioAgreementUrl(), AtworkConfig.DOMAIN_ID, userAccessToken);
        return url;
    }

    public static String getFaceLoginAgreementUrl(Context context) {
        final String url = String.format(UrlConstantManager.getInstance().getFaceLoginAgreementUrl(), AtworkConfig.DOMAIN_ID, "zh");
        return url;
    }



    public static HttpResult signUserLoginAgreement(Context context) {
        String userId = LoginUserInfo.getInstance().getLoginUserId(context);
        String userAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().signUserLoginAgreement(), userId, userAccessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, null);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }

        return httpResult;
    }

    public static HttpResult signFaceProtocolAgreement(Context context, String params) {
        String userId = LoginUserInfo.getInstance().getLoginUserId(context);
        String userAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().signFaceBioAgreementUrl(), userId, userAccessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, params);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }

        return httpResult;
    }

    public static HttpResult signFaceLoginAgreement(Context context, String ticket) {
        final String url = String.format(UrlConstantManager.getInstance().signFaceLoginAgreementUrl(), ticket);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, "");

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }

        return httpResult;
    }

    public static HttpResult getAgreementStatus(Context context) {
        final String url = String.format(UrlConstantManager.getInstance().V2_fetchUserFromRemoteUrl(), LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, AgreementStatusResponse.class));
        }

        return httpResult;
    }
}
