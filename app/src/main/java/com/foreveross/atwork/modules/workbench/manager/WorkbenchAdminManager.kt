package com.foreveross.atwork.modules.workbench.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreverht.threadGear.HighPriorityCachedTreadPoolExecutor
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.BaseNetWorkListener
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler
import com.foreveross.atwork.api.sdk.workbench.WorkbenchSyncNetService
import com.foreveross.atwork.api.sdk.workbench.model.request.WorkbenchCardHandleRequest
import com.foreveross.atwork.api.sdk.workbench.model.request.WorkbenchHandleRequest
import com.foreveross.atwork.api.sdk.workbench.model.request.WorkbenchModifyWorkbenchDefinitionRequest
import com.foreveross.atwork.api.sdk.workbench.model.response.*
import com.foreveross.atwork.infrastructure.model.Employee
import com.foreveross.atwork.infrastructure.model.advertisement.AdminAdvertisementConfig
import com.foreveross.atwork.infrastructure.model.orgization.Organization
import com.foreveross.atwork.infrastructure.model.workbench.Workbench
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchDefinitionData
import com.foreveross.atwork.infrastructure.model.workbench.factory.WorkbenchFactory
import com.foreveross.atwork.infrastructure.shared.OrgPersonalShareInfo
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.TimeUtil
import com.foreveross.atwork.infrastructure.utils.extension.asType
import com.foreveross.atwork.manager.model.MultiResult
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminAdminActivity
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminBannerCardBannerListActivity

object WorkbenchAdminManager : IWorkbenchAdminManager {

    //暂时用在从主页面tab的WorkbenchFragment 中的AdminPreviewMode, 通过缓存跳转到相关页面做交互,  其他管理员的卡片数据, 主要还是通过广播来维护
    var cacheWorkbenchData: WorkbenchData? = null

    private val mockUrlMap = hashMapOf(
            WorkbenchCardType.LIST_0 to "http://tp.foreverht.com:9000/mock/demo-list",
            WorkbenchCardType.LIST_1 to "http://tp.foreverht.com:9000/mock/demo-list",
            WorkbenchCardType.NEWS_0 to "http://tp.foreverht.com:9000/mock/demo-list",
            WorkbenchCardType.NEWS_1 to "http://tp.foreverht.com:9000/mock/demo-list",
            WorkbenchCardType.NEWS_2 to "http://tp.foreverht.com:9000/mock/demo-list",
            WorkbenchCardType.NEWS_3 to "http://tp.foreverht.com:9000/mock/demo-list",
            WorkbenchCardType.SHORTCUT_0 to "http://tp.foreverht.com:9000/mock/demo-list",
            WorkbenchCardType.SHORTCUT_1 to "http://tp.foreverht.com:9000/mock/demo-list"

    )

    override fun clear() {
        cacheWorkbenchData = null
    }

    override fun notifyRefresh(workbenchData: WorkbenchData?) {
        val intent = Intent(IWorkbenchAdminManager.ACTION_REFRESH_CARD_LIST)
        workbenchData?.let {
            intent.putExtra(IWorkbenchAdminManager.DATA_WORKBENCH_DATA, it)
        }
        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).sendBroadcast(intent)
    }


    @SuppressLint("StaticFieldLeak")
    override fun queryWorkbenchList(context: Context, orgCode: String, listener: BaseNetWorkListener<List<WorkbenchData>>) {
        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {
                return WorkbenchSyncNetService.queryWorkbenchList(context, orgCode)
            }

            override fun onPostExecute(result: HttpResult) {
                if (result.isRequestSuccess) {
                    listener.onSuccess((result.resultResponse as WorkbenchQueryListResponse).workbenchQueryResult?.records)
                    return
                }

                NetworkHttpResultErrorHandler.handleHttpError(result, listener)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @SuppressLint("StaticFieldLeak")
    override fun addWorkbench(context: Context, orgCode: String, workbenchHandleRequest: WorkbenchHandleRequest, listener: BaseCallBackNetWorkListener) {
        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {
                val result = WorkbenchSyncNetService.addWorkbench(context, orgCode, workbenchHandleRequest)

                if(!workbenchHandleRequest.disable && result.isRequestSuccess) {
                    val response = result.resultResponse as WorkbenchAdminAddResponse
                    setCurrentAdminWorkbench(context, response.id)

                    val workbenchData = WorkbenchData(
                            id = response.id,
                            domainId = AtworkConfig.DOMAIN_ID,
                            orgCode = orgCode,
                            name = workbenchHandleRequest.name,
                            enName = workbenchHandleRequest.enName,
                            twName = workbenchHandleRequest.twName,
                            remarks = workbenchHandleRequest.remarks,
                            disabled = workbenchHandleRequest.disable,
                            platforms = workbenchHandleRequest.platforms.toList(),
                            createTime = TimeUtil.getCurrentTimeInMillis()

                    )

                    notifyRefresh(workbenchData)
                }

                return result
            }

            override fun onPostExecute(result: HttpResult) {
                if (result.isRequestSuccess) {
                    listener.onSuccess()
                    return
                }

                NetworkHttpResultErrorHandler.handleHttpError(result, listener)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    @SuppressLint("StaticFieldLeak")
    override fun modifyWorkbench(context: Context, orgCode: String, widgetId: String, workbenchHandleRequest: WorkbenchHandleRequest, originalWorkbenchData: WorkbenchData, listener: BaseCallBackNetWorkListener) {
        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {
                val httpResult = WorkbenchSyncNetService.modifyWorkbench(context, orgCode, widgetId, workbenchHandleRequest)
                if (httpResult.isRequestSuccess) {

                    originalWorkbenchData.name = workbenchHandleRequest.name
                    originalWorkbenchData.enName = workbenchHandleRequest.enName
                    originalWorkbenchData.twName = workbenchHandleRequest.twName
                    originalWorkbenchData.remarks = workbenchHandleRequest.remarks
                    originalWorkbenchData.disabled = workbenchHandleRequest.disable
                    originalWorkbenchData.scopeRecord?.orgs = workbenchHandleRequest.scopesData
                            ?.mapNotNull { scope ->
                                scope.organization?.asType<Organization>()
                            }

                    originalWorkbenchData.scopeRecord?.employees = workbenchHandleRequest.scopesData
                            ?.mapNotNull { scope ->
                                scope.employee?.asType<Employee>()
                            }

                    notifyRefresh(workbenchData = originalWorkbenchData)

                }


                return httpResult
            }

            override fun onPostExecute(result: HttpResult) {
                if (result.isRequestSuccess) {
                    listener.onSuccess()
                    return
                }

                NetworkHttpResultErrorHandler.handleHttpError(result, listener)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    @SuppressLint("StaticFieldLeak")
    override fun addCard(context: Context, orgCode: String, workbenchCardHandleRequest: WorkbenchCardHandleRequest, originalWorkbenchData: WorkbenchData, listener: BaseCallBackNetWorkListener) {
        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {
                val multiResult = MultiResult<WorkbenchData>()
                var httpResultChain = WorkbenchSyncNetService.addCard(context, orgCode, workbenchCardHandleRequest)

                multiResult.httpResult = httpResultChain

                if (httpResultChain.isRequestSuccess) {
                    val workbenchCardHandleResponse = httpResultChain.resultResponse as WorkbenchCardHandleResponse
                    val originalWorkbenchDefinitionList = originalWorkbenchData.workbenchCards.toMutableList()
                    originalWorkbenchDefinitionList.sortBy { it.sortOrder }

//                    var sortOrderGrowth = 1
//                    if (originalWorkbenchDefinitionList.any { it.sortOrder == 0 }) {
//                        sortOrderGrowth = 2
//                    }

                    originalWorkbenchDefinitionList.forEachIndexed { index, workbenchDefinitionData ->
                        workbenchDefinitionData.sortOrder = (index + 2)
                    }
                    val newDefinitionInWorkbench = WorkbenchDefinitionData(
                            widgetId = workbenchCardHandleResponse.widgetId,
                            type = workbenchCardHandleRequest.type,
                            sortOrder = 1,
                            platforms = arrayListOf("ANDROID", "IOS")
                    )

                    originalWorkbenchDefinitionList.add(index = 0, element = newDefinitionInWorkbench)

                    httpResultChain = WorkbenchSyncNetService.modifyWorkbenchDefinition(context, orgCode, originalWorkbenchData.id.toString(), WorkbenchModifyWorkbenchDefinitionRequest(definitions = originalWorkbenchDefinitionList as ArrayList<WorkbenchDefinitionData>))

                    multiResult.httpResult = httpResultChain
                    if (httpResultChain.isRequestSuccess) {
                        originalWorkbenchData.workbenchCards = originalWorkbenchDefinitionList


                        val originalWorkbenchCardDetailDataList = originalWorkbenchData.workbenchCardDetailDataList.toMutableList()
                        val workbenchCardDetailData = WorkbenchCardDetailData(
                                id = workbenchCardHandleResponse.widgetId,
                                type = workbenchCardHandleRequest.type,
                                name = workbenchCardHandleRequest.name,
                                enName = workbenchCardHandleRequest.enName,
                                twName = workbenchCardHandleRequest.twName,
                                platforms = workbenchCardHandleRequest.platforms.toList(),
                                domainId = workbenchCardHandleRequest.domainId,
                                orgCode = orgCode,
                                workbenchCardDisplayDefinitions = workbenchCardHandleRequest.definition,
                                disabled = true
                        )

                        originalWorkbenchCardDetailDataList.add(index = 0, element = workbenchCardDetailData)

                        originalWorkbenchData.workbenchCardDetailDataList = originalWorkbenchCardDetailDataList

                        notifyRefresh(originalWorkbenchData)


                    }


                }
                return httpResultChain
            }

            override fun onPostExecute(result: HttpResult) {
                if (result.isRequestSuccess) {
                    listener.onSuccess()
                    return
                }

                NetworkHttpResultErrorHandler.handleHttpError(result, listener)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())

    }

    @SuppressLint("StaticFieldLeak")
    override fun modifyCard(context: Context, orgCode: String, workbenchCardDetailData: WorkbenchCardDetailData, listener: BaseCallBackNetWorkListener) {
        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {


                val httpResult = WorkbenchSyncNetService.modifyCard(context, orgCode, workbenchCardDetailData)
                if (httpResult.isRequestSuccess) {
                    WorkbenchAdminAdminActivity.refreshCard(workbenchCardDetailData)

                }

                return httpResult
            }

            override fun onPostExecute(result: HttpResult) {
                if (result.isRequestSuccess) {
                    listener.onSuccess()
                    return
                }

                NetworkHttpResultErrorHandler.handleHttpError(result, listener)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    @SuppressLint("StaticFieldLeak")
    override fun deleteCard(context: Context, orgCode: String, widgetId: String, originalWorkbenchData: WorkbenchData, listener: BaseCallBackNetWorkListener) {
        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {

                val workbenchDefinitionList = ArrayList(originalWorkbenchData.workbenchCards.filter { it.widgetId.toString() != widgetId })

                val httpResult = WorkbenchSyncNetService.modifyWorkbenchDefinition(context, orgCode, originalWorkbenchData.id.toString(), WorkbenchModifyWorkbenchDefinitionRequest(definitions = workbenchDefinitionList))
                if (httpResult.isRequestSuccess) {
                    originalWorkbenchData.workbenchCards = workbenchDefinitionList
                    originalWorkbenchData.workbenchCardDetailDataList = originalWorkbenchData.workbenchCardDetailDataList.filter { it.id.toString() != widgetId }

                    workbenchDefinitionList.sortBy { it.sortOrder }
                    workbenchDefinitionList.forEachIndexed { index, workbenchDefinitionData ->
                        workbenchDefinitionData.sortOrder = (index + 1)
                    }


                    notifyRefresh(originalWorkbenchData)
                }

                return httpResult
            }

            override fun onPostExecute(result: HttpResult) {
                if (result.isRequestSuccess) {
                    listener.onSuccess()
                    return
                }

                NetworkHttpResultErrorHandler.handleHttpError(result, listener)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }


    @SuppressLint("StaticFieldLeak")
    override fun sortCards(context: Context, orgCode: String, cardListSorted: List<WorkbenchCard>, originalWorkbenchData: WorkbenchData, listener: BaseCallBackNetWorkListener) {
        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {

                val workbenchDefinitionList = ArrayList<WorkbenchDefinitionData>()
                for (card in cardListSorted) {
                    originalWorkbenchData.workbenchCards
                            .find { it.widgetId == card.id }
                            ?.let { workbenchDefinitionList.add(it) }

                }

                //resort
                workbenchDefinitionList.forEachIndexed { index, workbenchDefinitionData ->
                    workbenchDefinitionData.sortOrder = (index + 1)
                }

                val httpResult = WorkbenchSyncNetService.modifyWorkbenchDefinition(context, orgCode, originalWorkbenchData.id.toString(), WorkbenchModifyWorkbenchDefinitionRequest(definitions = ArrayList(originalWorkbenchData.workbenchCards)))
                if (httpResult.isRequestSuccess) {
                    originalWorkbenchData.workbenchCards = workbenchDefinitionList

                    notifyRefresh(originalWorkbenchData)
                }

                return httpResult
            }

            override fun onPostExecute(result: HttpResult) {
                if (result.isRequestSuccess) {
                    listener.onSuccess()
                    return
                }

                NetworkHttpResultErrorHandler.handleHttpError(result, listener)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }


    override fun getCurrentAdminWorkbench(context: Context): Long {
        var currentAdminWorkbenchId = OrgPersonalShareInfo.getInstance().getCurrentAdminWorkbench(context, PersonalShareInfo.getInstance().getCurrentOrg(context))
        if (-1L == currentAdminWorkbenchId) {
            currentAdminWorkbenchId = WorkbenchManager.getCurrentOrgWorkbenchWithoutContent()?.id
                    ?: -1L
        }

        return currentAdminWorkbenchId
    }

    override fun setCurrentAdminWorkbench(context: Context, id: Long) {
        OrgPersonalShareInfo.getInstance().setCurrentAdminWorkbench(context, PersonalShareInfo.getInstance().getCurrentOrg(context), id)
    }

    @SuppressLint("StaticFieldLeak")
    override fun queryWorkbench(context: Context, orgCode: String, widgetId: String, listener: BaseNetWorkListener<QueryWorkbenchAdminMultiResult>) {
        object : AsyncTask<Void, Void, QueryWorkbenchAdminMultiResult>() {
            override fun doInBackground(vararg params: Void?): QueryWorkbenchAdminMultiResult {
                val httpResult = WorkbenchSyncNetService.adminQueryWorkbench(context, orgCode, widgetId)
                val multiResult = QueryWorkbenchAdminMultiResult()
                multiResult.httpResult = httpResult

                if (httpResult.isRequestSuccess) {
                    val workbenchResponseResult = (httpResult.resultResponse as WorkbenchAdminQueryResponse).result

                    workbenchResponseResult?.transferWorkbenchData(orgCode)?.let {
                        val workbench = produce(it)
                        multiResult.originalWorkbenchData = it
                        multiResult.localResult = workbench
                    }

                }
                return multiResult
            }

            override fun onPostExecute(result: QueryWorkbenchAdminMultiResult) {

                if (null != result.originalWorkbenchData) {
                    cacheWorkbenchData = result.originalWorkbenchData
                }

                if (null != result.localResult) {
                    listener.onSuccess(result)
                    return
                }

                NetworkHttpResultErrorHandler.handleHttpError(result.httpResult, listener)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());

    }

    @SuppressLint("StaticFieldLeak")
    override fun adminQueryBannerList(context: Context, orgCode: String, widgetId: String, listener: BaseNetWorkListener<List<AdminAdvertisementConfig>>) {
        object : AsyncTask<Void, Void, MultiResult<List<AdminAdvertisementConfig>>>() {
            override fun doInBackground(vararg params: Void?): MultiResult<List<AdminAdvertisementConfig>> {
                val httpResult = WorkbenchSyncNetService.adminQueryBannerList(context, orgCode, widgetId)
                val multiResult = MultiResult<List<AdminAdvertisementConfig>>()
                if (httpResult.isRequestSuccess) {
                    val response = httpResult.resultResponse as WorkbenchAdminQueryBannerListResponse
                    multiResult.localResult = response.result.parseAdminAdvertisementConfigs(orgCode)
                }

                multiResult.httpResult = httpResult

                return multiResult
            }

            override fun onPostExecute(multiResult: MultiResult<List<AdminAdvertisementConfig>>) {
                if (null != multiResult.localResult) {
                    listener.onSuccess(multiResult.localResult)
                    return
                }

                NetworkHttpResultErrorHandler.handleHttpError(multiResult.httpResult, listener)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }

    @SuppressLint("StaticFieldLeak")
    override fun adminAddBannerItem(context: Context, workbenchData: WorkbenchData, widgetId: String, workbenchAdminBannerHandleRequest: AdminAdvertisementConfig, listener: BaseNetWorkListener<WorkbenchAdminAddBannerItemResult>) {
        object : AsyncTask<Void, Void, HttpResult>() {

            override fun doInBackground(vararg params: Void?): HttpResult {
                val httpResult = WorkbenchSyncNetService.adminAddBannerItem(context, workbenchData.orgCode, widgetId, workbenchAdminBannerHandleRequest)
                if (httpResult.isRequestSuccess) {
                    val response = httpResult.resultResponse as WorkbenchAdminAddBannerItemResponse
                    workbenchAdminBannerHandleRequest.id = response.result.id
                    workbenchAdminBannerHandleRequest.domainId = response.result.domainId

                    WorkbenchAdminBannerCardBannerListActivity.refreshBannerList(workbenchAdminBannerHandleRequest)

                    notifyBannerDataChanged(workbenchData, workbenchAdminBannerHandleRequest)

                }
                return httpResult
            }

            override fun onPostExecute(result: HttpResult) {
                if (result.isRequestSuccess) {
                    val response = result.resultResponse as WorkbenchAdminAddBannerItemResponse

                    listener.onSuccess(response.result)
                    return
                }

                NetworkHttpResultErrorHandler.handleHttpError(result, listener)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }

    @SuppressLint("StaticFieldLeak")
    override fun adminPutawayBannerItem(context: Context, workbenchData: WorkbenchData, widgetId: String, adminAdvertisementConfig: AdminAdvertisementConfig, putaway: Boolean, listener: BaseCallBackNetWorkListener) {
        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {
                val httpResult = if (putaway) {
                    WorkbenchSyncNetService.adminPutawayBannerItem(context, workbenchData.orgCode, widgetId, adminAdvertisementConfig)
                } else {
                    WorkbenchSyncNetService.adminPutoutBannerItem(context, workbenchData.orgCode, widgetId, adminAdvertisementConfig)

                }

                if (httpResult.isRequestSuccess) {
                    adminAdvertisementConfig.disabled = !putaway
                    WorkbenchAdminBannerCardBannerListActivity.refreshBannerList(adminAdvertisementConfig)


                    notifyBannerDataChanged(workbenchData, adminAdvertisementConfig)

                }

                return httpResult
            }

            override fun onPostExecute(result: HttpResult) {
                if (result.isRequestSuccess) {
                    listener.onSuccess()
                    return
                }

                NetworkHttpResultErrorHandler.handleHttpError(result, listener)
            }

        }.executeOnExecutor(HighPriorityCachedTreadPoolExecutor.getInstance())
    }

    private fun notifyBannerDataRemoved(workbenchData: WorkbenchData, bannerId: String) {
        workbenchData.advertisements.removeAll { it.mId == bannerId }
        notifyRefresh(workbenchData)

    }

    private fun notifyBannerDataChanged(workbenchData: WorkbenchData, adminAdvertisementConfig: AdminAdvertisementConfig) {
        workbenchData.advertisements.removeAll { it.mId == adminAdvertisementConfig.id }

        //没有上架的, 通知显示新banner
        if (!adminAdvertisementConfig.disabled) {
            val advertisementConfig = adminAdvertisementConfig.parse()
            workbenchData.advertisements.add(advertisementConfig)
        }

        notifyRefresh(workbenchData)
    }


    @SuppressLint("StaticFieldLeak")
    override fun adminDeleteBannerItem(context: Context, workbenchData: WorkbenchData, widgetId: String, adminAdvertisementConfig: AdminAdvertisementConfig, listener: BaseCallBackNetWorkListener) {
        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {
                val httpResult = WorkbenchSyncNetService.adminDeleteBannerItem(context, workbenchData.orgCode, widgetId, adminAdvertisementConfig)
                if (httpResult.isRequestSuccess) {
                    WorkbenchAdminBannerCardBannerListActivity.deleteBannerItem(adminAdvertisementConfig)

                    adminAdvertisementConfig.id?.let { notifyBannerDataRemoved(workbenchData, it) }

                }
                return httpResult
            }

            override fun onPostExecute(result: HttpResult) {
                if (result.isRequestSuccess) {
                    listener.onSuccess()
                    return
                }

                NetworkHttpResultErrorHandler.handleHttpError(result, listener)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }

    override fun produce(it: WorkbenchData): Workbench {
        val workbenchData = WorkbenchFactory.produce(it, displayAll = true)


        workbenchData.workbenchCards.forEach { cardDetail ->
            cardDetail.adminDisplay = true

            //没有填写数据url, 则使用mock url 做展示
            if (!cardDetail.isCallbackUrlLegal()) {
                cardDetail.generateCallBackUrl(mockUrlMap[cardDetail.type])
            }
        }
        return workbenchData
    }


}


