package com.foreveross.atwork.api.sdk.ocr

import android.content.Context
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent
import com.foreveross.atwork.api.sdk.ocr.model.OcrApiRequest
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.JsonUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil

class OcrSyncNetService {


    /**
     * workplus负责转发, 不关注返回参数
     * */
    fun postOcrSync(context: Context, ocrApiRequest: OcrApiRequest): HttpResult {
        val accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().octUrl, accessToken)

        LogUtil.e("post url ${url}")
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(ocrApiRequest))
        return httpResult
    }
}