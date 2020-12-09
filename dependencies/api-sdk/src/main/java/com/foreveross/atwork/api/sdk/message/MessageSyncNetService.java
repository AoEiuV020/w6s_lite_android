package com.foreveross.atwork.api.sdk.message;

import android.content.Context;
import android.text.TextUtils;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.message.model.EmergencyMessageConfirmRequest;
import com.foreveross.atwork.api.sdk.message.model.QueryChecksumResponse;
import com.foreveross.atwork.api.sdk.message.model.QueryMessageHistoryRequest;
import com.foreveross.atwork.api.sdk.message.model.QueryMessageHistoryResponse;
import com.foreveross.atwork.api.sdk.message.model.QueryMessageTagResponse;
import com.foreveross.atwork.api.sdk.message.model.QueryMessagesOnSessionRequest;
import com.foreveross.atwork.api.sdk.message.model.QuerySessionListRequest;
import com.foreveross.atwork.api.sdk.message.model.QuerySessionListResponse;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.infrastructure.model.user.LoginToken;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dasunsy on 2017/4/25.
 */

public class MessageSyncNetService {

    /**
     * 紧急消息确认
     *
     * @param context
     * @param emergencyMessageConfirmRequest
     */
    public static HttpResult confirmEmergencyMessage(Context context, EmergencyMessageConfirmRequest emergencyMessageConfirmRequest) {
        String loginToken = LoginUserInfo.getInstance().getLoginToken(context).mAccessToken;
        String url = String.format(UrlConstantManager.getInstance().getEmergencyMsgConfirmUrl(), emergencyMessageConfirmRequest.mDeliveryId, loginToken);
        GsonBuilder gsonBuilder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, gsonBuilder.create().toJson(emergencyMessageConfirmRequest));
        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }

        return httpResult;
    }

    /**
     * 查询阅后即焚消息的权限(服务器是否还存在消息)
     */
    public static HttpResult queryBurnMessageAuth(Context context, String messageId) {
        String loginToken = LoginUserInfo.getInstance().getLoginToken(context).mAccessToken;
        String userId = LoginUserInfo.getInstance().getLoginUserId(context);
        String url = String.format(UrlConstantManager.getInstance().V2_queryBurnMessageAuth(), userId, messageId, loginToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }
        return httpResult;

    }

    /**
     * 解析url为分享格式
     *
     * @param context
     * @param shareUrl
     * @return
     */
    public static HttpResult parseShareUrl(Context context, String shareUrl) {
        String loginToken = LoginUserInfo.getInstance().getLoginToken(context).mAccessToken;
        String requestUrl = String.format(UrlConstantManager.getInstance().parseUrlForShare(), loginToken);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ops", "URL");
            jsonObject.put("url", shareUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(requestUrl, jsonObject.toString());
        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }
        return httpResult;
    }


    /**
     * 服务号历史消息
     *
     * @param queryMessageHistoryRequest
     * @return httpResult
     *
     * */
    public static HttpResult queryMessageHistory(Context context,QueryMessageHistoryRequest queryMessageHistoryRequest) {
        String loginToken = LoginUserInfo.getInstance().getLoginToken(context).mAccessToken;
        String requestUrl = String.format(UrlConstantManager.getInstance().getQueryMessageHistoryUrl(), queryMessageHistoryRequest.getOrgCode(), queryMessageHistoryRequest.getAppId(), queryMessageHistoryRequest.getSkip(), queryMessageHistoryRequest.getLimit(), loginToken);
        StringBuilder builder = new StringBuilder(requestUrl);
        if (!TextUtils.isEmpty(queryMessageHistoryRequest.getMessageType())) {
            builder.append("&messageType=").append(queryMessageHistoryRequest.getMessageType());
        }
        if (!TextUtils.isEmpty(queryMessageHistoryRequest.getTagId())) {
            builder.append("&tagId=").append(queryMessageHistoryRequest.getTagId());
        }
        if (!TextUtils.isEmpty(queryMessageHistoryRequest.getKeyword())) {
            builder.append("&fileName=").append(queryMessageHistoryRequest.getKeyword());
        }

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(builder.toString());
        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, QueryMessageHistoryResponse.class));
        }
        return httpResult;
    }

    public static HttpResult queryMessageTags(Context context, String orgId, String appId) {
        String loginToken = LoginUserInfo.getInstance().getLoginToken(context).mAccessToken;
        String url = String.format(UrlConstantManager.getInstance().queryMessageTagsUrl(), orgId, appId, loginToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, QueryMessageTagResponse.class));
        }
        return httpResult;
    }


    public static HttpResult queryChecksum(Context context, long begin, long end, int limit) {
        final LoginToken token = LoginUserInfo.getInstance().getLoginToken(context);

        String url = String.format(UrlConstantManager.getInstance().V2_queryOfflineMessages(), token.mClientId, begin, limit, token.mAccessToken);

        url += ("&end=" + end);


        Logger.e("OfflineMessagesReplayStrategyTimeWatcher", "checksum url request -> " + url);


        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, null);

        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, QueryChecksumResponse.class));
        }

        Logger.e("OfflineMessagesReplayStrategyTimeWatcher", "checksum url response -> " + httpResult.result);


        return httpResult;
    }



    /**
     * 获取消息会话列表
     * */
    public static HttpResult querySessionList(QuerySessionListRequest request) {

        String url = String.format(UrlConstantManager.getInstance().querySessionListUrl(), request.getUserId(), request.getTimestamp(), request.getLimit(), request.getToken());

        LogUtil.e("OFFLINE_SESSION_STRATEGY", "offline session strategy querySessionList request : " + url);


        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if (httpResult.isNetSuccess()) {
            BasicResponseJSON basicResponse = NetGsonHelper.fromNetJson(httpResult.result, QuerySessionListResponse.class);
            if (null != basicResponse) {
                QuerySessionListResponse responseJSON = (QuerySessionListResponse) basicResponse;
                responseJSON.parse();
            }

            httpResult.result(basicResponse);

//            LogUtil.e("OFFLINE_SESSION_STRATEGY", "offline session strategy querySessionList response : " + httpResult.result);
        }
        return httpResult;
    }

    /**
     * 按指定会话拉取消息
     * */
    public static HttpResult queryMessagesOnSession(QueryMessagesOnSessionRequest request) {
        String url = String.format(UrlConstantManager.getInstance().queryMessagesOnSessionUrl(), request.getUserId(), request.getRemoteConversionId(), request.getBegin(), request.getEnd(), request.getLimit(), request.getToken());

        LogUtil.e("OFFLINE_SESSION_STRATEGY", "offline session strategy queryMessagesOnSession request : " + url);

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
           // LogUtil.e("OFFLINE_SESSION_STRATEGY", "offline session strategy queryMessagesOnSession response : " + httpResult.result);
        }
        return httpResult;
    }

}
