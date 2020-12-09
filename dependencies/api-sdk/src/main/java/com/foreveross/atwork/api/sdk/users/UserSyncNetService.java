package com.foreveross.atwork.api.sdk.users;
/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.content.Context;
import android.util.Log;

import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.response.ConversionConfigSettingResponse;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.BaseSyncNetService;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.users.requestJson.AddUserFlagRequestJson;
import com.foreveross.atwork.api.sdk.users.requestJson.ModifyPasswordJson;
import com.foreveross.atwork.api.sdk.users.requestJson.ModifyPersonalSignatureJson;
import com.foreveross.atwork.api.sdk.users.requestJson.ModifyUserInfoJson;
import com.foreveross.atwork.api.sdk.users.requestJson.RemoveUserFlagRequestJson;
import com.foreveross.atwork.api.sdk.users.requestJson.UserAvatarPost;
import com.foreveross.atwork.api.sdk.users.responseJson.CheckSpecialViewResponse;
import com.foreveross.atwork.api.sdk.users.responseJson.GetCustomizationsResponse;
import com.foreveross.atwork.api.sdk.users.responseJson.ModifyPasswordResponse;
import com.foreveross.atwork.api.sdk.users.responseJson.QueryUserResponseJson;
import com.foreveross.atwork.api.sdk.users.responseJson.SearchUserResponseJson;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper;
import com.google.gson.Gson;

import java.util.List;

/**
 * 从网络中同步获取用户
 * Created by reyzhang22 on 16/3/24.
 *
 */
public class UserSyncNetService extends BaseSyncNetService {

    private static final String TAG = UserSyncNetService.class.getName();

    private static UserSyncNetService sInstance = new UserSyncNetService();

    private UserSyncNetService() {

    }

    public static UserSyncNetService getInstance() {
        return sInstance;
    }

    /**
     * 从远端获取登录用户
     * @param context
     * @return
     */
    public HttpResult queryLoginUserFromRemote(Context context) {

        final String url = String.format(UrlConstantManager.getInstance().V2_fetchUserFromRemoteUrl(), LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, QueryUserResponseJson.class));
        }

        return httpResult;
    }


    /**
     * 搜索人员列表
     * @param context
     * @param key
     * @return
     */
    public HttpResult searchUserListFromRemote(Context context, String key) {
        final String url = String.format(UrlConstantManager.getInstance().V2_searchUserListUrl(), key, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, SearchUserResponseJson.class));
        }


        return httpResult;
    }

    public HttpResult queryUserListFromRemote(Context context, List<String> unExistsUserIds) {
        StringBuilder sb = new StringBuilder();
        for(String id : unExistsUserIds) {
            sb.append(id).append(",");
        }

        final String url = String.format(UrlConstantManager.getInstance().V2_fetchUserListUrl(), sb.toString(), LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, SearchUserResponseJson.class));
        }

        return httpResult;
    }

    public HttpResult queryUserInfoFromRemote(Context context, String query) {
        return queryUserInfoFromRemote(context, query, "id");
    }

    /**
     * 查询人员
     * @param context
     * @param query
     * @param type 决定搜索条件的类型, 当type=id时query表示user_id, 当type=username时query表示username
     * @return
     */
    public HttpResult queryUserInfoFromRemote(Context context, String query, String type) {
        final String url = String.format(UrlConstantManager.getInstance().V2_queryUserInfoUrl(), query, type, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, QueryUserResponseJson.class));
        }

        try {
            Logger.e("user", "app request -> " + url);
            Logger.e("user", "app response -> " + httpResult.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return httpResult;
    }

    /**
     * 设置或去除星标联系人
     * @param context
     * @param user
     * @Param opts
     * @return
     */
    public HttpResult addOrRemoveFlagUser(Context context, User user, boolean opts) {
        final String url = String.format(UrlConstantManager.getInstance().V2_addOrRemoveFlagUserUrl(), user.mUserId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        String params = StringUtils.EMPTY;
        if (opts) {
            AddUserFlagRequestJson requestJson = new AddUserFlagRequestJson();
            requestJson.mDomainId = user.mDomainId;
            requestJson.mName = user.getShowName();
            requestJson.mPhone = user.mPhone;
            requestJson.mUserId = user.mUserId;
            requestJson.mAvatar = user.mAvatar;
            requestJson.mStatus = user.mStatus;
            params = new Gson().toJson(requestJson);
        } else {
            RemoveUserFlagRequestJson requestJson = new RemoveUserFlagRequestJson();
            requestJson.mDomainId = user.mDomainId;
            requestJson.mUserId = user.mUserId;
            params = new Gson().toJson(requestJson);
        }

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, params);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }

        return httpResult;
    }

    public HttpResult modifyUserInfo(Context ctx, String userId, ModifyUserInfoJson infoJson) {
        String postUrl = String.format(UrlConstantManager.getInstance().V2_modifyUserInfo(), userId, LoginUserInfo.getInstance().getLoginUserAccessToken(ctx));
        String postParms = new Gson().toJson(infoJson);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(postUrl, postParms);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }
        return httpResult;
    }

    public HttpResult modifyPersonalSignature(Context ctx, String userId, ModifyPersonalSignatureJson infoJson) {
        String postUrl = String.format(UrlConstantManager.getInstance().V2_modifyPersonalSignature(), userId, LoginUserInfo.getInstance().getLoginUserAccessToken(ctx));
        String postParms = new Gson().toJson(infoJson);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(postUrl, postParms);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }
        return httpResult;
    }

    public HttpResult modifyUserName(Context context, String userId, ModifyUserInfoJson infoJson) {
        String postUrl = String.format(UrlConstantManager.getInstance().V2_modifyUserName(), userId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        String postParms = new Gson().toJson(infoJson);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(postUrl, postParms);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }
        return httpResult;
    }


    public HttpResult modifyUserAvatar(Context ctx, String userId, UserAvatarPost avatarPost) {
        String postUrl = String.format(UrlConstantManager.getInstance().V2_modifyUserAvatar(), userId, LoginUserInfo.getInstance().getLoginUserAccessToken(ctx));
        String postParms = new Gson().toJson(avatarPost);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(postUrl, postParms);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }
        return httpResult;
    }


    /**
     * 修改密码
     * @param ctx
     * @param userId
     * @param oldPassword
     * @param newPassword
     *
     * */
    public HttpResult modifyPassword(Context ctx, String userId, String oldPassword, String newPassword) {

        String apiUrl = null;
        if (StringUtils.isEmpty(AtworkConfig.CUSTOM_MODIFY_API_URL)) {
            apiUrl = UrlConstantManager.getInstance().V2_modifyUserPassword();

        } else {
            apiUrl = AtworkConfig.CUSTOM_MODIFY_API_URL;
        }
        String postUrl = String.format(apiUrl, userId, LoginUserInfo.getInstance().getLoginUserAccessToken(ctx));
        ModifyPasswordJson modifyPasswordJson = ModifyPasswordJson.getChangePasswordJson(ctx, oldPassword, newPassword);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(postUrl, new Gson().toJson(modifyPasswordJson));

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, ModifyPasswordResponse.class));
        }
        return httpResult;
    }

    /**
     * 解除好友关系
     * */
    public HttpResult dismissFriend(Context context, String friendDomainId, String friendFriendId) {
        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String deleteUrl = String.format(UrlConstantManager.getInstance().V2_dismissFriendUrl(), meUserId, friendDomainId, friendFriendId, accessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().deleteHttp(deleteUrl);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }
        return httpResult;
    }

    public HttpResult setConversationSetting(Context context, String params, String type, String language) {
        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().setConversationSetting(), meUserId, type, accessToken);

        if(!StringUtils.isEmpty(language)) {
            UrlHandleHelper.addParam(url, "language", language);
        }

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, params);
        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }
        return httpResult;
    }

    public HttpResult getConversationSetting(Context context, String userId, String domainId, String type) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        String url = String.format(UrlConstantManager.getInstance().getUserConversations(), meUserId, userId, domainId, type, accessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if (httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, ConversionConfigSettingResponse.class));
        }
        return httpResult;
    }

    public HttpResult getUserOnlineStatus(Context context, String params) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getUserOnlineStatus(), accessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, params);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }
        return httpResult;
    }


    public HttpResult getCustomizationInfo(Context context) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getCustomizationInfoUrl(), AtworkConfig.DOMAIN_ID, accessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, GetCustomizationsResponse.class));
        }
        return httpResult;
    }

    public HttpResult getSpecialViewCheck(Context context, String userId) {
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context);
        String url = String.format(UrlConstantManager.getInstance().getSpecialViewCheckUrl(), userId, accessToken);
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, CheckSpecialViewResponse.class));
        }

        return httpResult;

    }

    public interface OnUserConversationsListener extends NetWorkFailListener {
        void onSetConversationsSuccess();
    }

    public interface GetUserConversationsListener {
        void getConversationsSuccess(Object object);
    }

}
