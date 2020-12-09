@file: JvmName("CmdGatherSyncNetService")

package com.foreveross.atwork.api.sdk.gather

import android.content.Context
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent


fun postData(context: Context, dataUrl : String, data: String): HttpResult {
    val httpResult = HttpURLConnectionComponent.getInstance().postHttp(dataUrl, data)
    return httpResult
}