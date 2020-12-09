package com.foreveross.atwork.api.sdk.faceBio.aliyun

import android.content.Context
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.faceBio.aliyun.model.request.FaceBioFilmRequest
import com.foreveross.atwork.api.sdk.faceBio.aliyun.model.response.FaceBioFileResponse
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent
import com.foreveross.atwork.api.sdk.util.NetGsonHelper
import com.foreveross.atwork.infrastructure.model.face.aliyun.request.FaceBioAuthToggleRequest
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.JsonUtil

object FaceBioSyncNetService {

    /**
     * 开启/关闭人脸识别
     * @param context
     * @param faceBioAuthToggleRequest
     * @return httpResult
     * */
    fun faceBioAuthToggle(context: Context, faceBioAuthToggleRequest: FaceBioAuthToggleRequest): HttpResult {
        val userId = LoginUserInfo.getInstance().getLoginUserId(context)
        val accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().switchFaceCloudAuthUrl(), userId, accessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(faceBioAuthToggleRequest))


        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON::class.java))
        }

        return httpResult
    }


    /**
     * 设置人脸识别底片
     * @param context
     * @param request
     * @return httpResult
     * */
    fun setFaceBioFilm(context: Context, request: FaceBioFilmRequest): HttpResult {
        val userId = LoginUserInfo.getInstance().getLoginUserId(context)
        val accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().toggleFaceBioFilm(), userId, accessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(request))
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, FaceBioFileResponse::class.java))
        }

        return httpResult
    }

}