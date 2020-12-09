package com.foreveross.atwork.api.sdk.device

import android.content.Context
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.device.model.request.HandleLoginDeviceRequest
import com.foreveross.atwork.api.sdk.device.model.request.ModifyLoginDeviceInfoRequest
import com.foreveross.atwork.api.sdk.device.model.response.QueryLoginDevicesInfoResponse
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent
import com.foreveross.atwork.api.sdk.util.NetGsonHelper
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.JsonUtil

object LoginDeviceSyncNetService {


    /**
     * 查询登录设备列表
     * */
    fun queryLoginDevices(context: Context): HttpResult {
        val accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val userId = LoginUserInfo.getInstance().getLoginUserId(context)
        val url = String.format(UrlConstantManager.getInstance().handleLoginDevicesUrl, userId, accessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)
        if (httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, QueryLoginDevicesInfoResponse::class.java))
        }

        return httpResult
    }

    /**
     * 设置设备免认证状态
     * */
    fun setLoginDevicesWithoutAuthStatus(context: Context, ids: List<String>): HttpResult {
        val accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val userId = LoginUserInfo.getInstance().getLoginUserId(context)

        val url = String.format(UrlConstantManager.getInstance().handleLoginDevicesUrl, userId, accessToken)

        val handleLoginDeviceRequest = HandleLoginDeviceRequest("auth", ids)

        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(handleLoginDeviceRequest))
        if (httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON::class.java))
        }

        return httpResult

    }


    /**
     * 设置设备需要认证状态
     * */
    fun setLoginDevicesNeedAuthStatus(context: Context, ids: List<String>): HttpResult {
        val accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val userId = LoginUserInfo.getInstance().getLoginUserId(context)

        val url = String.format(UrlConstantManager.getInstance().handleLoginDevicesUrl, userId, accessToken)

        val handleLoginDeviceRequest = HandleLoginDeviceRequest("unAuth", ids)

        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(handleLoginDeviceRequest))
        if (httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON::class.java))
        }

        return httpResult

    }

    /**
     * 删除设备
     * */
    fun removeLoginDevices(context: Context, ids: List<String>): HttpResult {
        val accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val userId = LoginUserInfo.getInstance().getLoginUserId(context)

        val url = String.format(UrlConstantManager.getInstance().handleLoginDevicesUrl, userId, accessToken)

        val handleLoginDeviceRequest = HandleLoginDeviceRequest("remove", ids)

        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(handleLoginDeviceRequest))
        if (httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON::class.java))
        }

        return httpResult

    }


    /**
     * 修改登录设备信息, 如设备名称
     * */
    fun modifyLoginDeviceInfo(context: Context, modifyLoginDeviceInfoRequest: ModifyLoginDeviceInfoRequest): HttpResult {
        val accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val userId = LoginUserInfo.getInstance().getLoginUserId(context)

        val url = String.format(UrlConstantManager.getInstance().handleLoginDevicesUrl, userId, accessToken)

        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(modifyLoginDeviceInfoRequest))
        if (httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON::class.java))
        }

        return httpResult
    }

}