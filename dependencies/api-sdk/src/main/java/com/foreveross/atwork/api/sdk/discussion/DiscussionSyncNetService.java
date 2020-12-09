package com.foreveross.atwork.api.sdk.discussion;/**
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

import androidx.annotation.Nullable;

import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.discussion.requestJson.CreateDiscussionRequestJson;
import com.foreveross.atwork.api.sdk.discussion.requestJson.DismissDiscussionRequest;
import com.foreveross.atwork.api.sdk.discussion.responseJson.CreateDiscussionResponseJson;
import com.foreveross.atwork.api.sdk.discussion.responseJson.QueryDiscussionResponseJson;
import com.foreveross.atwork.api.sdk.discussion.responseJson.QueryReadOrUnreadResponse;
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reyzhang22 on 16/3/25.
 */
public class DiscussionSyncNetService {

    private static final String TAG = DiscussionSyncNetService.class.getName();

    private static DiscussionSyncNetService sInstance = new DiscussionSyncNetService();

    private DiscussionSyncNetService() {

    }

    public static DiscussionSyncNetService getInstance() {
        return sInstance;
    }


    public HttpResult queryReadUnread(Context ctx, String messageId, String readOrUnread) {
        String url = String.format(UrlConstantManager.getInstance().V2_queryDiscussionReadUnread(), LoginUserInfo.getInstance().getLoginUserId(ctx), messageId, LoginUserInfo.getInstance().getLoginToken(ctx).mAccessToken);

        if ("read".equalsIgnoreCase(readOrUnread)) {
            url += "READ";

        } else if ("unread".equalsIgnoreCase(readOrUnread)) {

            url += "RECEIVED,UNRECEIVED";

        }
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, QueryReadOrUnreadResponse.class));

        }

        return httpResult;
    }



    /**
     * 获取群基本信息
     * @param context
     * @param discussionId
     * @return
     */
    public HttpResult queryDiscussionBasicInfo(final Context context, final String discussionId) {
        String url = String.format(UrlConstantManager.getInstance().V2_getDiscussionInfoUrl(), discussionId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, QueryDiscussionResponseJson.class));

        }

        return httpResult;
    }


    /**
     * 获取群详情接口
     * @param context
     * @param discussionId
     * @return
     */

    public HttpResult queryDiscussionDetailInfo(final Context context, final String discussionId) {
        String url = String.format(UrlConstantManager.getInstance().V2_getDiscussionDetailUrl(), discussionId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, QueryDiscussionResponseJson.class));
        }
        return httpResult;
    }

    /**
     * 操作群成员接口
     * @param context
     * @param discussionId
     * @param params
     * @return
     */
    public HttpResult addOrRemoveDiscussionMember(final Context context, final String discussionId, final String params) {
        String url = String.format(UrlConstantManager.getInstance().V2_discussionMemberOptsUrl(), discussionId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, params);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));

        }
        return httpResult;
    }

    /**
     * 转移群主接口
     * */
    public HttpResult transferOrBecomeOwner(final Context context, final String discussionId, final String params) {
        String url = String.format(UrlConstantManager.getInstance().V2_transferDiscussionOwnerUrl(), discussionId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, params);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));

        }
        return httpResult;
    }


    /**
     * 修改群信息接口
     * @param context
     * @param discussionId
     * @return 服务器是否修改成功
     */
    public HttpResult modifyDiscussion(final Context context, final String discussionId, final String params) {
        String url = String.format(UrlConstantManager.getInstance().V2_modifyDiscussionUrl(), discussionId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, params);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }
        return httpResult;
    }

    /**
     * 群聊信息设置
     * @param context
     * @param discussionId
     * @param params
     * @return
     */
    public HttpResult setDiscussionSettings(final Context context, final String discussionId, final String params) {
        String url = String.format(UrlConstantManager.getInstance().setDiscussionSettings(), discussionId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, params);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }
        return httpResult;
    }

    public HttpResult getDiscussionSettings(final Context context, final String discussionId) {
        String url = String.format(UrlConstantManager.getInstance().getDiscussionConversations(), discussionId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);
        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }
        return httpResult;
    }


    /**
     * 创建群聊
     * @param context
     * @param discussionMembers
     * @param name
     * @param orgCode
     * @param templateId
     * @return
     */
    public HttpResult createDiscussion(final Context context, @Nullable User creator, @Nullable String name, @Nullable String avatar, @Nullable String orgCode,  @Nullable String templateId,  final List<ShowListItem> discussionMembers) {
        String url = String.format(UrlConstantManager.getInstance().V2_createDiscussionUrl(), LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        List<UserHandleInfo> users = new ArrayList<>();

        String discussionName = processDiscussionName(name, discussionMembers);


        for(ShowListItem member : discussionMembers) {
            UserHandleInfo user = new UserHandleInfo();
            user.mUserId = member.getId();
            user.mDomainId = member.getDomainId();
            user.mShowName = member.getParticipantTitle();
            user.mAvatar = member.getAvatar();
            user.mStatus = member.getStatus();
            users.add(user);
        }

        CreateDiscussionRequestJson requestJson = new CreateDiscussionRequestJson();

        requestJson.name = discussionName;
        requestJson.avatar = avatar;
        requestJson.ownerCode = orgCode;
        requestJson.members = users;

        if(!StringUtils.isEmpty(templateId)) {
            requestJson.type = "template";
            requestJson.templateId = templateId;
        }

        if (null != creator) {
            requestJson.creator = creator.toUserHandleInfo();
        }


        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, new Gson().toJson(requestJson));

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, CreateDiscussionResponseJson.class));
        }

        return httpResult;

    }

    @NotNull
    private String processDiscussionName(@Nullable String name, List<ShowListItem> discussionMembers) {
        String discussionName;

        if (StringUtils.isEmpty(name)) {
            StringBuilder names = new StringBuilder();

            int i = 0;
            for (ShowListItem member : discussionMembers) {
                if (i != 0) {
                    names.append(",");
                }
                names.append(member.getParticipantTitle());

                i++;
            }

            discussionName = names.toString();
            if (discussionName.length() > 15) {
                discussionName = discussionName.substring(0, 14);
            }
        } else {
            discussionName = name;
        }
        return discussionName;
    }

    /**
     * 删除群组退群
     * */
    public HttpResult deleteDiscussion(final Context context, String userId, String discussionId) {
        String url = String.format(UrlConstantManager.getInstance().V2_deleteDiscussionUrl(), userId, discussionId, LoginUserInfo.getInstance().getLoginUserAccessToken(context));
        HttpResult httpResult = HttpURLConnectionComponent.getInstance().deleteHttp(url);

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }

        return httpResult;
    }


    /**
     * 解散群组
     * */
    public HttpResult dismissDiscussion(Context context, String discussionId){
        String url = String.format(UrlConstantManager.getInstance().V2_dismissDiscussionUrl(), discussionId, AtworkConfig.DOMAIN_ID, LoginUserInfo.getInstance().getLoginUserAccessToken(context));

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(new DismissDiscussionRequest()));

        if(httpResult.isNetSuccess()) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON.class));
        }

        return httpResult;
    }



    /**
     *
     * @param httpResult
     * @return
     */
    private boolean isHttpError(HttpResult httpResult) {
        return httpResult.isNetFail() || httpResult.isNetError() || httpResult.status != 0;
    }
}
