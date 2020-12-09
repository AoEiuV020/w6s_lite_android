@file: JvmName("UrlRouteHelper")

package com.foreveross.atwork.modules.app.route

import android.content.Context
import android.net.Uri
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.app.activity.WebViewActivity
import com.foreveross.atwork.modules.route.manager.RouteActionConsumer
import com.foreveross.atwork.modules.route.model.RouteParams

fun  routeUrl(context: Context, webViewControlAction: WebViewControlAction) {
    routeUrl(context,webViewControlAction,false)
}


fun  routeUrl(context: Context, webViewControlAction: WebViewControlAction,autoAuthCordova:Boolean = false) {
    if(StringUtils.isEmpty(webViewControlAction.mUrl)) {
        return
    }

    val weburi = Uri.parse(webViewControlAction.mUrl)
    val routeParams = RouteParams.newRouteParams().apply { uri = weburi }.build()

    val result = RouteActionConsumer.route(context, routeParams)


    if(!result) {
        val intent = WebViewActivity.getIntent(context, webViewControlAction,autoAuthCordova)
        context.startActivity(intent)
    }
}