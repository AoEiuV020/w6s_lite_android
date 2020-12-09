package com.foreveross.atwork.api.sdk.auth;

import android.content.Context;
import android.os.Build;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.auth.model.AuthPostJson;
import com.foreveross.atwork.api.sdk.auth.model.AuthResponseJson;
import com.foreveross.atwork.api.sdk.auth.model.LoginEndpointPostJson;
import com.foreveross.atwork.api.sdk.auth.model.LoginEndpointResponseJSON;
import com.foreveross.atwork.api.sdk.auth.model.LoginInitRequest;
import com.foreveross.atwork.api.sdk.auth.model.LoginInitResp;
import com.foreveross.atwork.api.sdk.auth.model.LoginTokenJSON;
import com.foreveross.atwork.api.sdk.auth.model.LoginTokenResponseJSON;
import com.foreveross.atwork.api.sdk.auth.model.LoginWithMobileRequest;
import com.foreveross.atwork.api.sdk.auth.model.LoginWithFaceBioRequest;
import com.foreveross.atwork.api.sdk.auth.model.PhoneSecureCodeRequestJson;
import com.foreveross.atwork.api.sdk.auth.model.PreUserRegistryRequestJson;
import com.foreveross.atwork.api.sdk.auth.model.PreUserRegistryResponseJson;
import com.foreveross.atwork.api.sdk.auth.model.UserRegistryRequestJson;
import com.foreveross.atwork.api.sdk.auth.util.EncryptHelper;
import com.foreveross.atwork.api.sdk.discussion.responseJson.DiscussionListResponseJson;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.users.responseJson.ContactSyncResponse;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.DeviceUtil;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by dasunsy on 2017/1/17.
 */

public class LoginSyncNetService {

    public static HttpResult encryptModeInit() {
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(UrlConstantManager.getInstance().getTokenInitUrl(), new Gson().toJson(new LoginInitRequest()));
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, LoginInitResp.class));
        }
        return httpResult;
    }

    public static HttpResult login(LoginTokenJSON loginTokenJSON) {

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        final String request = gson.toJson(loginTokenJSON);

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(UrlConstantManager.getInstance().getTokenUrl(), request);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, LoginTokenResponseJSON.class));
        }

        return httpResult;

    }


    public static HttpResult loginWithFaceBio(Context context, LoginWithFaceBioRequest request) {


        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(UrlConstantManager.getInstance().getTokenUrl(), JsonUtil.toJson(request));
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, LoginTokenResponseJSON.class));
        }

        return httpResult;
    }


    public static HttpResult loginWithMobile(Context context, LoginWithMobileRequest request) {


        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(UrlConstantManager.getInstance().getTokenUrl(), JsonUtil.toJson(request));
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, LoginTokenResponseJSON.class));
        }

        return httpResult;
    }

    public static HttpResult auth(final Context context, AuthPostJson autoPostJson) {
        final String request = new Gson().toJson(autoPostJson);
        String postUrl = String.format(UrlConstantManager.getInstance().getAuthUrl(), LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        //认证
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(postUrl, request);

        if(httpResult.isNetSuccess()) {
            BasicResponseJSON basicResponseJSON = NetGsonHelper.fromNetJson(httpResult.result, AuthResponseJson.class);
            if(null == basicResponseJSON) {
                basicResponseJSON = NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class);
            }
            httpResult.result(basicResponseJSON);
        }

        return httpResult;
    }

    public static HttpResult endpoint(Context context, final LoginEndpointPostJson postJson) {

        final String url = String.format(UrlConstantManager.getInstance().getENDPOINT(), LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, new Gson().toJson(postJson));
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, LoginEndpointResponseJSON.class));

        }

        return httpResult;
    }


    /**
     * 发送手机验证码
     * @param context
     * @param phoneSecureCodeRequest
     *
     * @return httpResult
     * */
    public static HttpResult requestPhoneSecureCode(Context context, PhoneSecureCodeRequestJson phoneSecureCodeRequest) {
        final String url = String.format(UrlConstantManager.getInstance().getSendMobileSecureCodeUrl());
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, new Gson().toJson(phoneSecureCodeRequest));
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }

        return httpResult;

    }


    /**
     * 填写验证码后, 验证码预处理
     * @param context
     * @param preUserRegistryRequestJson
     *
     * @return httpResult
     * */
    public static HttpResult requestPreUserRegistry(Context context, PreUserRegistryRequestJson preUserRegistryRequestJson) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().getPreUserRegistryUrl(), accessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, new Gson().toJson(preUserRegistryRequestJson));
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, PreUserRegistryResponseJson.class));
        }

        return httpResult;
    }


    /**
     * 验证码预登陆后的处理, 设置用户个人信息, 或者密码
     *
     * @param context
     * @param userRegistryRequestJson
     *
     * @return httpResult
     * */
    public static HttpResult requestUserRegistry(Context context, UserRegistryRequestJson userRegistryRequestJson) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        final String url = String.format(UrlConstantManager.getInstance().getUserRegistryUrl(), accessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, new Gson().toJson(userRegistryRequestJson));
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, UserRegistryResponseJson.class));
        }

        return httpResult;
    }



    public static HttpResult syncDiscussions(Context context) {
        String url = UrlConstantManager.getInstance().V2_fetchUserDiscussionsUrl();
        LoginUserInfo loginUserInfo = LoginUserInfo.getInstance();
        final String accessToken = loginUserInfo.getLoginToken(context).mAccessToken;
        final String userId = loginUserInfo.getLoginToken(context).mClientId;
        url = String.format(url, userId, accessToken);

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, DiscussionListResponseJson.class));

        }

        return httpResult;

    }

    public static HttpResult syncFlagContacts(Context context) {
        String url = UrlConstantManager.getInstance().V2_fetchUserFlagContactsUrl();

        LoginUserInfo loginUserInfo = LoginUserInfo.getInstance();
        final String accessToken = loginUserInfo.getLoginToken(context).mAccessToken;
        final String userId = loginUserInfo.getLoginToken(context).mClientId;
        url = String.format(url, userId, accessToken);

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, ContactSyncResponse.class));

        }

        return httpResult;

    }
}
