package com.foreveross.atwork.modules.email.route

import android.content.Context
import com.foreveross.atwork.modules.route.action.RouteAction
import com.foreveross.atwork.modules.route.model.RouteParams
import com.foreveross.atwork.utils.IntentUtil

class SystemEmailRouteAction(routeParams: RouteParams?) : RouteAction(routeParams) {

    override fun action(context: Context) {
        IntentUtil.startRegisteredEmail(context, routeParams?.getUri())
    }
}