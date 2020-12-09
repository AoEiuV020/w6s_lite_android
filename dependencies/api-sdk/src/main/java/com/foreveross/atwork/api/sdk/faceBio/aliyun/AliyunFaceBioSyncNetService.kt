package com.foreveross.atwork.api.sdk.faceBio.aliyun

import android.content.Context
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.faceBio.aliyun.model.request.CheckFaceBioTicketRequest
import com.foreveross.atwork.api.sdk.faceBio.aliyun.model.request.FaceBioFilmRequest
import com.foreveross.atwork.api.sdk.faceBio.aliyun.model.response.CheckFaceBioTicketResponse
import com.foreveross.atwork.api.sdk.faceBio.aliyun.model.response.GetAliyunFaceBioTokenResponse
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent
import com.foreveross.atwork.api.sdk.util.NetGsonHelper
import com.foreveross.atwork.infrastructure.model.face.aliyun.request.GetAliyunFaceBioTokenRequest
import com.foreveross.atwork.infrastructure.model.face.aliyun.request.FaceBioAuthToggleRequest
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.JsonUtil

object AliyunFaceBioSyncNetService {


    /**
     * 认证获取 token
     * @param getAliyunFaceBioTokenRequest
     * @return httpResult
     * */
    fun getFaceBioToken(getAliyunFaceBioTokenRequest: GetAliyunFaceBioTokenRequest): HttpResult {
        val url = String.format(UrlConstantManager.getInstance().faceBioTokenUrl)

        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(getAliyunFaceBioTokenRequest))
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, GetAliyunFaceBioTokenResponse::class.java))
        }

        return httpResult
    }


    /**
     * 验证 ticket
     * @param checkFaceBioTicketRequest
     * @return httpResult
     * */
    fun checkFaceBioTicket(checkFaceBioTicketRequest: CheckFaceBioTicketRequest): HttpResult {
        val url = String.format(UrlConstantManager.getInstance().checkFaceBioTicketUrl(), checkFaceBioTicketRequest.ticketId)

        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, CheckFaceBioTicketResponse::class.java))
        }

        return httpResult
    }

}