package com.foreveross.atwork.modules.route.pay

import android.content.Context
import android.content.Intent
import com.foreveross.atwork.modules.route.action.RouteAction
import com.foreveross.atwork.modules.route.model.RouteParams


class WxPayRouteAction(routeParams: RouteParams?): RouteAction(routeParams) {

    override fun action(context: Context) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = routeParams?.getUri()
        context.startActivity(intent)
    }
}