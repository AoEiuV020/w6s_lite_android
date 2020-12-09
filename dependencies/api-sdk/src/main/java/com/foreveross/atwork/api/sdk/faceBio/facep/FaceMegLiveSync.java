package com.foreveross.atwork.api.sdk.faceBio.facep;

import android.content.Context;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.faceBio.facep.requestModel.BizVerifyRequest;
import com.foreveross.atwork.api.sdk.faceBio.facep.responseModel.BizTokenResponse;
import com.foreveross.atwork.api.sdk.faceBio.facep.responseModel.BizVerifiedResponse;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.google.gson.Gson;

public class FaceMegLiveSync {

    private static final FaceMegLiveSync sInstance = new FaceMegLiveSync();

    public static FaceMegLiveSync getInstance() {
        return sInstance;
    }


    public HttpResult getBizToken(Context context, String type) {
        String userId = LoginUserInfo.getInstance().getLoginUserId(context);
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getFaceBioTokenUrl(), AtworkConfig.DOMAIN_ID, userId, type, accessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, "");
        if (httpResult != null) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BizTokenResponse.class));
        }
        return httpResult;
    }

    public HttpResult verify(Context context, BizVerifyRequest request, String type) {
        String userId = LoginUserInfo.getInstance().getLoginUserId(context);
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().verifyFaceBioUrl(), AtworkConfig.DOMAIN_ID, userId, type, accessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, new Gson().toJson(request));
        if (httpResult != null) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BizVerifiedResponse.class));
        }
        return httpResult;
    }
}
