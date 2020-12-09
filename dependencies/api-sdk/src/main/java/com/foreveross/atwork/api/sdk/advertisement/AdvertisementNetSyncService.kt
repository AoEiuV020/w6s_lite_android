package com.foreveross.atwork.api.sdk.advertisement

import android.content.Context
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.advertisement.model.response.GetAdvertisesListResponse
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementEvent
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.JsonUtil

object AdvertisementNetSyncService {

    fun getAdvertisements(context: Context, orgCode: String, kind: String): HttpResult {
        val accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().advertisements, orgCode, kind, accessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)

        if(httpResult.isNetSuccess) {
            httpResult.result(JsonUtil.fromJson(httpResult.result, GetAdvertisesListResponse::class.java))
        }

        return httpResult
    }


    fun postAdvertisementEvent(context: Context, advertisementEvent: AdvertisementEvent): HttpResult {
        val accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().postAdvertisementsEvent(), accessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, AdvertisementEvent.getGson().toJson(advertisementEvent))

        if(httpResult.isNetSuccess) {
            httpResult.result(JsonUtil.fromJson(httpResult.result, BasicResponseJSON::class.java))
        }

        return httpResult
    }
}
