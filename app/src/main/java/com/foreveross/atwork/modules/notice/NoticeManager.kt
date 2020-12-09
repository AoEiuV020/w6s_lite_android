package com.foreveross.atwork.modules.notice

import android.annotation.SuppressLint
import android.os.AsyncTask
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreveross.atwork.api.sdk.app.model.LightNoticeData
import com.foreveross.atwork.api.sdk.net.RequestRemoteInterceptor
import com.foreveross.atwork.api.sdk.notice.NoticeSyncNetService
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.modules.web.util.UrlReplaceHelper

object NoticeManager: INoticeManager {


    @SuppressLint("StaticFieldLeak")
    override fun queryNoticeData(key: String, url: String, onGetResult: (LightNoticeData?)-> Unit) {

        if (!RequestRemoteInterceptor.checkLegal(key)) {

            LogUtil.e("is notice loading -> $url")
            return
        }

        LogUtil.e("start notice loading -> $url")

        RequestRemoteInterceptor.addInterceptRequestId(key)


        object : AsyncTask<Void, Void, LightNoticeData?>(){
            override fun doInBackground(vararg params: Void?): LightNoticeData? {
                val urlHandled = UrlReplaceHelper.replaceKeyParamsSync(url)
                val httpResult =  NoticeSyncNetService.queryNoticeData(urlHandled.url!!)

                RequestRemoteInterceptor.removeInterceptRequestId(key)

                if(httpResult.isRequestSuccess) {
                        return httpResult.resultResponse as LightNoticeData
                    }

                return null
            }

            override fun onPostExecute(result: LightNoticeData?) {
                onGetResult(result)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }
}