package com.foreveross.atwork.api.sdk.notice

import com.foreveross.atwork.api.sdk.app.model.LightNoticeData
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent
import com.foreveross.atwork.api.sdk.util.NetGsonHelper

object NoticeSyncNetService {


    fun queryNoticeData(url: String): HttpResult {
        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, LightNoticeData::class.java))
        }

        return httpResult
    }
}