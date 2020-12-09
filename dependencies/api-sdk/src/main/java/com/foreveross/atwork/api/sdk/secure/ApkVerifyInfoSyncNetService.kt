package com.foreveross.atwork.api.sdk.secure

import android.content.Context
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent
import com.foreveross.atwork.api.sdk.secure.model.response.ApkVerifyInfoResponse
import com.foreveross.atwork.api.sdk.util.NetGsonHelper
import com.foreveross.atwork.infrastructure.utils.VerifyApkUtil

object ApkVerifyInfoSyncNetService {

    fun getApkVerifyInfo(context: Context): HttpResult {

        val keyToDoor = VerifyApkUtil.getApkMd5(context).toLowerCase()
        val url = String.format(UrlConstantManager.getInstance().apkVerifyInfoUrl, keyToDoor)
        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)
        if (httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, ApkVerifyInfoResponse::class.java))
        }

        return httpResult

    }
}