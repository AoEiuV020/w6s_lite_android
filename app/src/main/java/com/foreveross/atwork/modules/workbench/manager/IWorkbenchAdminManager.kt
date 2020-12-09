package com.foreveross.atwork.modules.workbench.manager

import android.content.Context
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.BaseNetWorkListener
import com.foreveross.atwork.api.sdk.workbench.model.request.WorkbenchCardHandleRequest
import com.foreveross.atwork.api.sdk.workbench.model.request.WorkbenchHandleRequest
import com.foreveross.atwork.api.sdk.workbench.model.response.WorkbenchAdminAddBannerItemResult
import com.foreveross.atwork.infrastructure.model.advertisement.AdminAdvertisementConfig
import com.foreveross.atwork.infrastructure.model.workbench.Workbench
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.manager.model.MultiResult


interface IWorkbenchAdminManager {

   companion object {
      const val ACTION_REFRESH_CARD_LIST = "ACTION_REFRESH_CARD_LIST"
      const val ACTION_REFRESH_CARD_LIST_TOTALLY = "ACTION_REFRESH_CARD_LIST_TOTALLY"
      const val ACTION_REFRESH_UPDATE_CARD = "ACTION_REFRESH_UPDATE_CARD"
      const val DATA_WORKBENCH_DATA = "DATA_WORKBENCH_DATA"
      const val DATA_CARD_DATA = "DATA_CARD_DATA"
   }

   fun clear()

   fun notifyRefresh(workbenchData: WorkbenchData? = null)

   fun queryWorkbenchList(context: Context, orgCode: String, listener: BaseNetWorkListener<List<WorkbenchData>>)

   fun addWorkbench(context: Context, orgCode: String, workbenchHandleRequest: WorkbenchHandleRequest, listener: BaseCallBackNetWorkListener)

   fun modifyWorkbench(context: Context, orgCode: String, widgetId: String, workbenchHandleRequest: WorkbenchHandleRequest, originalWorkbenchData: WorkbenchData, listener: BaseCallBackNetWorkListener)

   fun addCard(context: Context, orgCode: String, workbenchCardHandleRequest: WorkbenchCardHandleRequest, originalWorkbenchData: WorkbenchData, listener: BaseCallBackNetWorkListener)

   fun modifyCard(context: Context, orgCode: String, workbenchCardDetailData: WorkbenchCardDetailData, listener: BaseCallBackNetWorkListener)

   fun deleteCard(context: Context, orgCode: String, widgetId: String, originalWorkbenchData: WorkbenchData, listener: BaseCallBackNetWorkListener)

   fun sortCards(context: Context, orgCode: String, cardListSorted: List<WorkbenchCard>, originalWorkbenchData: WorkbenchData, listener: BaseCallBackNetWorkListener)

   fun getCurrentAdminWorkbench(context: Context): Long

   fun setCurrentAdminWorkbench(context: Context, id: Long)

   fun queryWorkbench(context: Context, orgCode: String, widgetId: String, listener: BaseNetWorkListener<QueryWorkbenchAdminMultiResult>)

   fun adminQueryBannerList(context: Context, orgCode: String, widgetId: String, listener: BaseNetWorkListener<List<AdminAdvertisementConfig>>)

   fun adminAddBannerItem(context: Context, workbenchData: WorkbenchData, widgetId: String, workbenchAdminBannerHandleRequest: AdminAdvertisementConfig, listener: BaseNetWorkListener<WorkbenchAdminAddBannerItemResult>)

   fun adminPutawayBannerItem(context: Context, workbenchData: WorkbenchData, widgetId: String, adminAdvertisementConfig: AdminAdvertisementConfig, putaway: Boolean, listener: BaseCallBackNetWorkListener)

   fun adminDeleteBannerItem(context: Context, workbenchData: WorkbenchData, widgetId: String, adminAdvertisementConfig: AdminAdvertisementConfig, listener: BaseCallBackNetWorkListener)

   fun produce(it: WorkbenchData): Workbench

}

class QueryWorkbenchAdminMultiResult(var originalWorkbenchData: WorkbenchData? = null): MultiResult<Workbench>()
