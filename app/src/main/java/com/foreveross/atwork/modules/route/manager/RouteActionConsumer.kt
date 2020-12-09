package com.foreveross.atwork.modules.route.manager

import android.content.Context
import com.foreveross.atwork.modules.route.model.RouteParams

object RouteActionConsumer {

    fun route(context: Context, routeParams: RouteParams): Boolean {
        val routeAction = RouteActionFactory.produce(routeParams) ?: return false

        routeAction.action(context)
        return true

    }
}