package com.foreveross.atwork.modules.app.route

import android.app.Activity
import android.content.Context
import com.foreveross.atwork.infrastructure.model.app.App
import com.foreveross.atwork.infrastructure.model.app.LightApp
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.CloneUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.app.manager.AppManager
import com.foreveross.atwork.modules.route.action.RouteAction
import com.foreveross.atwork.modules.route.model.RouteParams
import com.foreveross.atwork.modules.search.util.SearchHelper
import com.foreveross.atwork.utils.AtworkToast

open class OpenAppRouteAction(routeParams: RouteParams? = null): RouteAction(routeParams) {

    override fun action(context: Context) {
        val appId: String? = routeParams?.getUri()?.getQueryParameter("id")
        var orgCode: String? = routeParams?.getUri()?.getQueryParameter("orgCode")
        val routeUrl: String? = routeParams?.getUri()?.toString()?.substringAfterLast("routeUrl=", StringUtils.EMPTY)

        if(StringUtils.isEmpty(orgCode)) {
           orgCode = PersonalShareInfo.getInstance().getCurrentOrg(context)
        }

        appId?.let {
            AppManager.getInstance().queryApp(context, it, orgCode, object : AppManager.GetAppFromMultiListener {
                override fun onSuccess(app: App) {
                    val appCloned = CloneUtil.cloneTo<App>(app)
                    if(appCloned is LightApp) {
                        appCloned.mRouteUrl = routeUrl
                    }


                    if (context is Activity) {
                        SearchHelper.handleSearchResultCommonClick(context, it, appCloned, null, null)
                    }

                }

                override fun networkFail(errorCode: Int, errorMsg: String?) {
                    AtworkToast.sendToastDependOnActivity("应用不存在")
                }

            })
        }
    }
}