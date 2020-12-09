package com.foreveross.atwork.api.sdk.workbench

import android.content.Context
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent
import com.foreveross.atwork.api.sdk.util.NetGsonHelper
import com.foreveross.atwork.api.sdk.workbench.model.request.WorkbenchHandleRequest
import com.foreveross.atwork.api.sdk.workbench.model.request.WorkbenchCardHandleRequest
import com.foreveross.atwork.api.sdk.workbench.model.request.WorkbenchModifyWorkbenchDefinitionRequest
import com.foreveross.atwork.api.sdk.workbench.model.response.*
import com.foreveross.atwork.infrastructure.model.advertisement.AdminAdvertisementConfig
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.JsonUtil

object WorkbenchSyncNetService {

    fun queryWorkbench(context: Context, orgCode: String, refreshTime: Long = -1): HttpResult {
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().queryWorkbenchUrl, orgCode, refreshTime, loginUserAccessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, WorkbenchQueryResponse::class.java))
        }

        return httpResult
    }


    fun queryWorkbenchList(context: Context, orgCode: String): HttpResult {
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().queryWorkbenchListUrl, orgCode, 0, 100, true, loginUserAccessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, WorkbenchQueryListResponse::class.java))
        }

        return httpResult
    }

    fun addWorkbench(context: Context, orgCode: String, workbenchHandleRequest: WorkbenchHandleRequest): HttpResult {
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().addWorkbenchUrl, orgCode, loginUserAccessToken)

        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(workbenchHandleRequest))
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, WorkbenchAdminAddResponse::class.java))
        }

        return httpResult

    }


    fun addCard(context: Context, orgCode: String, workbenchCardHandleRequest: WorkbenchCardHandleRequest): HttpResult {
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().addCardUrl, orgCode, loginUserAccessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(workbenchCardHandleRequest))
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, WorkbenchCardHandleResponse::class.java))
        }
        return httpResult
    }

    fun modifyCard(context: Context, orgCode: String, workbenchCardDetailData: WorkbenchCardDetailData): HttpResult {
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().modifyCardUrl, orgCode, workbenchCardDetailData.id, loginUserAccessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(workbenchCardDetailData))
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON::class.java))
        }
        return httpResult
    }

    fun modifyWorkbench(context: Context, orgCode: String, widgetId: String, workbenchHandleRequest: WorkbenchHandleRequest): HttpResult {
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().modifyWorkbenchUrl, orgCode, widgetId, loginUserAccessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(workbenchHandleRequest))
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON::class.java))
        }
        return httpResult
    }

    fun modifyWorkbenchDefinition(context: Context, orgCode: String, widgetId: String, request: WorkbenchModifyWorkbenchDefinitionRequest): HttpResult {
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().modifyWorkbenchDefinitionUrl, orgCode, widgetId, loginUserAccessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(request))
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON::class.java))
        }
        return httpResult
    }

    fun adminQueryWorkbench(context: Context, orgCode: String, widgetId: String): HttpResult {
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().adminQueryWorkbenchUrl, orgCode, widgetId, loginUserAccessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, WorkbenchAdminQueryResponse::class.java))
        }

        return httpResult
    }


    fun adminAddBannerItem(context: Context, orgCode: String, widgetId: String, workbenchAdminBannerHandleRequest: AdminAdvertisementConfig): HttpResult {
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().adminAddBannerItemUrl, orgCode, widgetId, loginUserAccessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(workbenchAdminBannerHandleRequest))
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, WorkbenchAdminAddBannerItemResponse::class.java))
        }

        return httpResult
    }

    fun adminPutawayBannerItem(context: Context, orgCode: String, widgetId: String, adminAdvertisementConfig: AdminAdvertisementConfig): HttpResult {
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().adminPutAwayBannerItemUrl, orgCode, adminAdvertisementConfig.id, "enable", widgetId, loginUserAccessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, "")
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, WorkbenchAdminAddBannerItemResponse::class.java))
        }
        return httpResult
    }


    fun adminPutoutBannerItem(context: Context, orgCode: String, widgetId: String, adminAdvertisementConfig: AdminAdvertisementConfig): HttpResult {
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().adminPutAwayBannerItemUrl, orgCode, adminAdvertisementConfig.id, "disable", widgetId, loginUserAccessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, "")
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, WorkbenchAdminAddBannerItemResponse::class.java))
        }
        return httpResult
    }


    fun adminDeleteBannerItem(context: Context, orgCode: String, widgetId: String, adminAdvertisementConfig: AdminAdvertisementConfig): HttpResult {
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().adminDeleteBannerItemUrl, orgCode, adminAdvertisementConfig.id, widgetId, loginUserAccessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().deleteHttp(url)

        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, BasicResponseJSON::class.java))
        }

        return httpResult
    }

    fun adminQueryBannerList(context: Context, orgCode: String, widgetId: String): HttpResult {
        val loginUserAccessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(context)
        val url = String.format(UrlConstantManager.getInstance().adminQueryBannerListUrl, orgCode, widgetId, 0, 100, loginUserAccessToken)
        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, WorkbenchAdminQueryBannerListResponse::class.java))
        }

        return httpResult
    }



    fun queryWorkbenchShortcutCardContent(url: String): HttpResult {
        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, WorkbenchQueryShortcutCardContentResponse::class.java))
        }

        return httpResult
    }


    fun queryWorkbenchListContent(url: String): HttpResult {
        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)
        if(httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, WorkbenchQueryListContentResponse::class.java))
        }

        return httpResult
    }

}