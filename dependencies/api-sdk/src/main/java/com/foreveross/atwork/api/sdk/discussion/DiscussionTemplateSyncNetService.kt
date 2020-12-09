package com.foreveross.atwork.api.sdk.discussion

import android.content.Context
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.discussion.requestJson.MapDiscussionTagRequest
import com.foreveross.atwork.api.sdk.discussion.requestJson.ModifyDiscussionTemplateRequest
import com.foreveross.atwork.api.sdk.discussion.responseJson.DiscussionFeaturesResponse
import com.foreveross.atwork.api.sdk.discussion.responseJson.DiscussionTagsResponse
import com.foreveross.atwork.api.sdk.discussion.responseJson.DiscussionTemplatesResponse
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent
import com.foreveross.atwork.api.sdk.util.NetGsonHelper
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionMemberTag
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.JsonUtil
import org.json.JSONArray
import org.json.JSONObject

/**
 * 群模板列表
 */

fun queryDiscussionTemplatesSync(context: Context): HttpResult {
    val url = String.format(UrlConstantManager.getInstance().discussionTemplatesUrl(), LoginUserInfo.getInstance().getLoginUserAccessToken(context))
    val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)
    if (httpResult.isNetSuccess) {
        httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, DiscussionTemplatesResponse::class.java))
    }
    return httpResult
}



/**
 * 群模板详情
 */
fun queryDiscussionFeaturesSync(context: Context, templateId: String): HttpResult {
    val url = String.format(UrlConstantManager.getInstance().discussionTemplateUrl(), templateId, LoginUserInfo.getInstance().getLoginUserAccessToken(context))
    val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)
    if (httpResult.isNetSuccess) {
        httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, DiscussionFeaturesResponse::class.java))
    }
    return httpResult
}


/**
 *  删除群模板成员标签
 * */
fun removeDiscussionTemplateFeaturesSync(context: Context, discussionId: String, featureId: String): HttpResult {
    val url = String.format(UrlConstantManager.getInstance().removeDiscussionFeatureUrl(), discussionId, featureId, LoginUserInfo.getInstance().getLoginUserAccessToken(context))
    val httpResult = HttpURLConnectionComponent.getInstance().deleteHttp(url)
    if (httpResult.isNetSuccess) {
        httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, DiscussionFeaturesResponse::class.java))
    }

    return httpResult
}


/**
 * 设置群模板 features 信息
 */
fun setDiscussionTemplateFeaturesSync(context: Context, discussionId: String, features: List<String>): HttpResult {
    val url = String.format(UrlConstantManager.getInstance().discussionSetTemplateFeaturesUrl(), discussionId, LoginUserInfo.getInstance().getLoginUserAccessToken(context))
    val postParams = features
            .map { JSONObject(it) }
            .let { JSONArray(it) }
            .toString()

    val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, postParams)
    if (httpResult.isNetSuccess) {
        httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, DiscussionFeaturesResponse::class.java))
    }

    return httpResult

}


/**
 * 设置群模板成员标签
 * */
fun setDiscussionMemberTagsSync(context: Context, discussionId: String, tags: List<DiscussionMemberTag>): HttpResult {
    val url = String.format(UrlConstantManager.getInstance().setDiscussionTagsUrl(), discussionId, LoginUserInfo.getInstance().getLoginUserAccessToken(context))
    val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(tags))
    if (httpResult.isNetSuccess) {
        httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, DiscussionTagsResponse::class.java))
    }

    return httpResult
}


/**
 *  删除群模板成员标签
 * */
fun removeDiscussionMemberTagSync(context: Context, discussionId: String, tagId: String): HttpResult {
    val url = String.format(UrlConstantManager.getInstance().removeDiscussionTagUrl(), discussionId, tagId, LoginUserInfo.getInstance().getLoginUserAccessToken(context))
    val httpResult = HttpURLConnectionComponent.getInstance().deleteHttp(url)
    if (httpResult.isNetSuccess) {
        httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, DiscussionTagsResponse::class.java))
    }

    return httpResult
}


/**
 * 设置群模板成员标签
 * */
fun setDiscussionTagMemberMapsSync(context: Context, discussionId: String, mapDiscussionTagRequest: MapDiscussionTagRequest): HttpResult {
    val url = String.format(UrlConstantManager.getInstance().setDiscussionTagMemberMapUrl(), discussionId, LoginUserInfo.getInstance().getLoginUserAccessToken(context))
    val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(mapDiscussionTagRequest))
    if (httpResult.isNetSuccess) {
        httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON::class.java))
    }

    return httpResult
}


/**
 * 设置群模板成员标签
 * */
fun modifyDiscussionTemplateSync(context: Context, discussionId: String, modifyDiscussionTemplateRequest: ModifyDiscussionTemplateRequest): HttpResult {
    val url = String.format(UrlConstantManager.getInstance().modifyDiscussionTemplateUrl(), discussionId, LoginUserInfo.getInstance().getLoginUserAccessToken(context))
    val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(modifyDiscussionTemplateRequest))
    if (httpResult.isNetSuccess) {
        httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON::class.java))
    }

    return httpResult
}


