package com.foreveross.atwork.api.sdk.configSetting.userSetting

import android.content.Context
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent
import com.foreveross.atwork.api.sdk.configSetting.userSetting.model.UserConfigSettings
import com.foreveross.atwork.api.sdk.configSetting.userSetting.model.UserConfigSettingsResponse
import com.foreveross.atwork.api.sdk.util.NetGsonHelper
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.JsonUtil
import org.json.JSONObject

object UserConfigSettingSyncNetService {


    /**
     * 获取用户设置(群聊助手等)
     *
     * @param context
     * */
    fun getUserSettings(context: Context): HttpResult {
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val loginUserId = LoginUserInfo.getInstance().getLoginUserId(context)

        val url = String.format(UrlConstantManager.getInstance().userSettings(), loginUserId, loginUserAccessToken)

        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)

        if (httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, UserConfigSettingsResponse::class.java))
        }

        return httpResult

    }



    /**
     * 提交用户设置(群聊助手等)
     *
     * @param context
     * @param userSettings
     * */
    fun setUserSettings(context: Context, userSettings: UserConfigSettings): HttpResult {
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val loginUserId = LoginUserInfo.getInstance().getLoginUserId(context)
        val url = String.format(UrlConstantManager.getInstance().userSettings(), loginUserId, loginUserAccessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(userSettings))

        if (httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON::class.java))
        }

        return httpResult
    }

    fun setDeviceSettings(context: Context?, silently: Boolean): HttpResult {
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val loginUserId = LoginUserInfo.getInstance().getLoginUserId(context)
        val url = String.format(UrlConstantManager.getInstance().setDevicesMode(), loginUserId, loginUserAccessToken)
        val json = JSONObject()
        json.put("silently", silently)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, json.toString())
        if (httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON::class.java))
        }
        return httpResult
    }

    fun logoutPc(context: Context?): HttpResult {
        val loginUserId = LoginUserInfo.getInstance().getLoginUserId(context)
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val pcDeviceId = PersonalShareInfo.getInstance().getPCDeviceId(context);
        val url = String.format(UrlConstantManager.getInstance().logoutPCUrl(), loginUserId, pcDeviceId, loginUserAccessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().deleteHttp(url)
        if (httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON::class.java))
        }
        return httpResult
    }
}