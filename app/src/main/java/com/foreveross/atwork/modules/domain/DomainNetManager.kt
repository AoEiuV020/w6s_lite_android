package com.foreveross.atwork.modules.domain

import android.annotation.SuppressLint
import android.os.AsyncTask
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.BaseNetWorkListener
import com.foreveross.atwork.api.sdk.domain.DomainSyncNetService
import com.foreveross.atwork.api.sdk.domain.model.response.QueryMultiDomainsResponse
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler
import com.foreveross.atwork.infrastructure.model.domain.MultiDomainsItem
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo

object DomainNetManager {

    var multiDomainsData : List<MultiDomainsItem>? = null

    init {
        multiDomainsData = CommonShareInfo.getDomainsDataRemote(AtworkApplicationLike.baseContext)
    }

    fun getDomainNameSelected(): String? {
        val domainIdSelect = CommonShareInfo.getDomainId(AtworkApplicationLike.baseContext)
        return multiDomainsData
                ?.find {
                    domainIdSelect == it.domainId
                }?.domainName

    }



    @SuppressLint("StaticFieldLeak")
    fun getMultiDomainsRemote(listener: BaseNetWorkListener<List<MultiDomainsItem>>? = null) {
        object : AsyncTask<Void, Void, HttpResult>() {

            override fun doInBackground(vararg params: Void?): HttpResult {
                val httpResult = DomainSyncNetService.getMultiDomains()
                return httpResult
            }


            override fun onPostExecute(result: HttpResult) {
                if(result.isRequestSuccess) {
                    val queryMultiDomainsResponse = result.resultResponse as QueryMultiDomainsResponse
                    CommonShareInfo.setDomainsDataRemote(AtworkApplicationLike.baseContext, queryMultiDomainsResponse.result)
                    multiDomainsData = queryMultiDomainsResponse.result

                    listener?.onSuccess(queryMultiDomainsResponse.result)
                    return

                }

                NetworkHttpResultErrorHandler.handleHttpError(result, listener)
            }


        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())

    }
}