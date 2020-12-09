package com.foreveross.atwork.api.sdk.domain

import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.domain.model.response.QueryMultiDomainsResponse
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent
import com.foreveross.atwork.api.sdk.util.NetGsonHelper

object DomainSyncNetService {

    fun getMultiDomains(): HttpResult {
        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(UrlConstantManager.getInstance().queryMultiDomainsUrl)
        if (httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, QueryMultiDomainsResponse::class.java))
        }

        return httpResult
    }
}