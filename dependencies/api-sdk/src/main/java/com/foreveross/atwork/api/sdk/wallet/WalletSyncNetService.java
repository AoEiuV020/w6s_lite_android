package com.foreveross.atwork.api.sdk.wallet;

import android.content.Context;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.api.sdk.wallet.requestJson.CheckRedEnvelopeGainDetailRequestJson;
import com.foreveross.atwork.api.sdk.wallet.requestJson.ForcedSetPayPasswordRequestJson;
import com.foreveross.atwork.api.sdk.wallet.requestJson.GiveMultiDiscussionRedEnvelopeRequestJson;
import com.foreveross.atwork.api.sdk.wallet.requestJson.GiveRedEnvelopeRequestJson;
import com.foreveross.atwork.api.sdk.wallet.requestJson.GrabRedEnvelopeRequestJson;
import com.foreveross.atwork.api.sdk.wallet.requestJson.ModifyMobileRequestJson;
import com.foreveross.atwork.api.sdk.wallet.requestJson.ModifyPayPasswordRequestJson;
import com.foreveross.atwork.api.sdk.wallet.requestJson.SendMobileSecureCodeRequestJson;
import com.foreveross.atwork.api.sdk.wallet.requestJson.VerifyWalletMobileSecureCodeRequestJson;
import com.foreveross.atwork.api.sdk.wallet.responseJson.GiveMultiDiscussionRedEnvelopeResponseJson;
import com.foreveross.atwork.api.sdk.wallet.responseJson.GiveRedEnvelopeResponseJson;
import com.foreveross.atwork.api.sdk.wallet.responseJson.GrabRedEnvelopeResponseJson;
import com.foreveross.atwork.api.sdk.wallet.responseJson.QueryRedEnvelopeGainDetailResponseJson;
import com.foreveross.atwork.api.sdk.wallet.responseJson.QueryWalletAccountResponse;
import com.foreveross.atwork.api.sdk.wallet.responseJson.SendWalletMobileSecureCodeResponseJson;
import com.foreveross.atwork.api.sdk.wallet.responseJson.VerifyWalletMobileSecureCodeResponseJson;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;

/**
 * Created by dasunsy on 2017/12/29.
 */

public class WalletSyncNetService {


    /**
     * ????????????????????????
     */
    public static HttpResult queryMyWalletAccount(Context context) {
        String queryUrl = String.format(UrlConstantManager.getInstance().getQueryWalletAccount(), AtworkConfig.DOMAIN_ID, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(queryUrl);
        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, QueryWalletAccountResponse.class));
        }

        return httpResult;
    }


    /**
     * ????????????, ?????????????????????
     */
    public static HttpResult sendMobileSecureCode(Context context, SendMobileSecureCodeRequestJson sendMobileSecureCodeRequestJson) {
        String loginUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        String loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getSendWalletMobileSecureCodeUrl(), AtworkConfig.DOMAIN_ID, loginUserId, loginUserAccessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(sendMobileSecureCodeRequestJson));

        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, SendWalletMobileSecureCodeResponseJson.class));
        }

        return httpResult;


    }


    /**
     * ?????????????????????
     */
    public static HttpResult verifyWalletMobileSecureCode(Context context, VerifyWalletMobileSecureCodeRequestJson verifyWalletMobileSecureCodeRequestJson) {
        String loginUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        String loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getVerifyWalletMobileSecureCodeUrl(), AtworkConfig.DOMAIN_ID, loginUserId, loginUserAccessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(verifyWalletMobileSecureCodeRequestJson));

        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, VerifyWalletMobileSecureCodeResponseJson.class));
        }


        return httpResult;
    }

    /**
     * ??????????????????
     * */
    public static HttpResult modifyWalletMobile(Context context, ModifyMobileRequestJson modifyMobileRequestJson) {
        String loginUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        String loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getModifyWalletMobileUrl(), AtworkConfig.DOMAIN_ID, loginUserId, loginUserAccessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(modifyMobileRequestJson));

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));

        }
        return httpResult;
    }


    /**
     * ????????????????????????????????????????????????
     */
    public static HttpResult forcedSetPayPassword(Context context, ForcedSetPayPasswordRequestJson forcedSetPlayPasswordRequestJson) {
        String loginUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        String loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getForcedSetPayPasswordUrl(), AtworkConfig.DOMAIN_ID, loginUserId, loginUserAccessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(forcedSetPlayPasswordRequestJson));

        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }

        return httpResult;
    }

    /**
     * ??????????????????
     */
    public static HttpResult modifyPayPassword(Context context, ModifyPayPasswordRequestJson modifyPayPasswordRequest) {
        String loginUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        String loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getSetPayPasswordUrl(), AtworkConfig.DOMAIN_ID, loginUserId, loginUserAccessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(modifyPayPasswordRequest));

        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }

        return httpResult;
    }


    /**
     * ?????????
     */
    public static HttpResult giveRedEnvelope(Context context, GiveRedEnvelopeRequestJson giveRedEnvelopeRequestJson) {
        String loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getGiveRedEnvelopeUrl(), AtworkConfig.DOMAIN_ID, loginUserAccessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(giveRedEnvelopeRequestJson));

        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, GiveRedEnvelopeResponseJson.class));
        }

        return httpResult;

    }


    /**
     * ????????????
     * */
    public static HttpResult giveMultiDiscussionRedEnvelope(Context context, GiveMultiDiscussionRedEnvelopeRequestJson giveRedEnvelopeRequestJson) {
        String loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getMultiDiscussionGiveRedEnvelopeUrl(), AtworkConfig.DOMAIN_ID, loginUserAccessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(giveRedEnvelopeRequestJson));

        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, GiveMultiDiscussionRedEnvelopeResponseJson.class));
        }

        return httpResult;

    }


    /**
     * ?????????
     * */
    public static HttpResult grabRedEnvelope(Context context, GrabRedEnvelopeRequestJson grabRedEnvelopeRequestJson) {
        String loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getGrabRedEnvelopeUrl(), AtworkConfig.DOMAIN_ID, grabRedEnvelopeRequestJson.mEnvelopeId, loginUserAccessToken);

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(grabRedEnvelopeRequestJson));
        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, GrabRedEnvelopeResponseJson.class));
        }

        return httpResult;

    }


    /**
     * ????????????????????????
     */
    public static HttpResult queryRedEnvelopeGainDetail(Context context, CheckRedEnvelopeGainDetailRequestJson checkRedEnvelopeGainDetailRequestJson) {
        String loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getQueryRedEnvelopeGainDetailUrl(), AtworkConfig.DOMAIN_ID, checkRedEnvelopeGainDetailRequestJson.mEnvelopeId, loginUserAccessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);

        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, QueryRedEnvelopeGainDetailResponseJson.class));
        }

        return httpResult;

    }

    /**
     * ??????
     * */
    public static HttpResult makeTransaction(Context context, String makeTransactionRequestStr) {
        String loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getMakeTransactionRequestUrl(), AtworkConfig.DOMAIN_ID, loginUserAccessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, makeTransactionRequestStr);

        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }

        return httpResult;

    }

}
