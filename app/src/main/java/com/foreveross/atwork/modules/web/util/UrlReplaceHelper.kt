@file: JvmName("UrlReplaceHelper")
package com.foreveross.atwork.modules.web.util

import android.annotation.SuppressLint
import android.os.AsyncTask
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.cordova.CordovaAsyncNetService
import com.foreveross.atwork.api.sdk.cordova.responseJson.UserTicketResponseJSON
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper
import com.foreveross.atwork.modules.web.model.ReplaceResult

object UrlReplaceHelper {

    @SuppressLint("StaticFieldLeak")
    fun replaceKeyParams(url: String?, getReplaceResult: (ReplaceResult) -> Unit?) {
        object : AsyncTask<Void, Void, ReplaceResult>() {
            override fun doInBackground(vararg params: Void?): ReplaceResult {
                return replaceKeyParamsSync(url)
            }

            override fun onPostExecute(result: ReplaceResult) {
                getReplaceResult(result)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())

    }

    @JvmStatic
    fun replaceKeyParamsSync(url: String?): ReplaceResult {

        if(null == url) {
            return ReplaceResult()
        }

        var url = url
        val replaceResult = ReplaceResult()

        var fullReplaced = true
        if(url.contains("{{ticket}}")) {
            val getUserTicketUrl = String.format(UrlConstantManager.getInstance().ticketUrl, LoginUserInfo.getInstance().getLoginUserAccessToken(AtworkApplicationLike.baseContext))

            val httpResult = CordovaAsyncNetService.getUserTicketSync(AtworkApplicationLike.baseContext, null, getUserTicketUrl, 5 * 1000)
            if (httpResult.isRequestSuccess) {
                val userTicketResponseJSON = httpResult.resultResponse as UserTicketResponseJSON
                if (StringUtils.isEmpty(userTicketResponseJSON.userTicket)) {
                    fullReplaced = false
                } else {

                    url = url.replace("{{ticket}}", userTicketResponseJSON.userTicket)
                }


            } else {
                fullReplaced = false
            }

        }

        url = UrlHandleHelper.replaceConventionalKeyParams(AtworkApplicationLike.baseContext, url)

        replaceResult.url = url
        replaceResult.fullReplaced = fullReplaced

        return replaceResult
    }


}




