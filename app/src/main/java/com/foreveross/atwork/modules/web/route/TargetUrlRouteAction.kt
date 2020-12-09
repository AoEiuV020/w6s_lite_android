package com.foreveross.atwork.modules.web.route

import android.content.Context
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.modules.app.activity.WebViewActivity
import com.foreveross.atwork.modules.route.action.RouteAction
import com.foreveross.atwork.modules.route.manager.RouteActionConsumer
import com.foreveross.atwork.modules.route.manager.RouteActionFactory
import com.foreveross.atwork.modules.route.model.RouteParams

class TargetUrlRouteAction(routeParams: RouteParams?) : RouteAction(routeParams) {


    override fun action(context: Context) {

        val openUrl = routeParams?.getEntryValue() ?: return


        val result = RouteActionConsumer.route(context, RouteParams.newRouteParams().uri(openUrl).build())

        if (result) {
            return
        }

        val webViewControlAction = WebViewControlAction.newAction()
                .setUrl(openUrl)
                .setNeedShare(false)
                .setHideTitle(false)
                .setBackHome(true)

        context.startActivity(WebViewActivity.getIntent(AtworkApplicationLike.baseContext, webViewControlAction))
    }


}