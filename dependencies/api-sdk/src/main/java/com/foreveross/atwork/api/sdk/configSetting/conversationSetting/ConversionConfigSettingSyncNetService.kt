package com.foreveross.atwork.api.sdk.configSetting.conversationSetting

import android.content.Context
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingItem
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.response.ConversionConfigSettingsResponse
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent
import com.foreveross.atwork.api.sdk.util.NetGsonHelper
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.JsonUtil

object ConversionConfigSettingSyncNetService {


    /**
     * 批量更新会话设置
     * */
    fun setConversationsSetting(context: Context, conversionConfigSettingItems: List<ConversionConfigSettingItem>): HttpResult {

        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val loginUserId = LoginUserInfo.getInstance().getLoginUserId(context)
        val url = String.format(UrlConstantManager.getInstance().conversationsSetting, loginUserId, loginUserAccessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJsonList(conversionConfigSettingItems))

        if (httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON::class.java))
        }

        return httpResult

    }


    /**
     * 批量获取会话设置
     * */
    fun getConversationsSetting(context: Context): HttpResult {

        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val loginUserId = LoginUserInfo.getInstance().getLoginUserId(context)
        val url = String.format(UrlConstantManager.getInstance().conversationsSetting, loginUserId, loginUserAccessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)

        if (httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, ConversionConfigSettingsResponse::class.java))
        }

        return httpResult

    }


}