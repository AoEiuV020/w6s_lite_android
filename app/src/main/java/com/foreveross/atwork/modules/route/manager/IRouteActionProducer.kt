package com.foreveross.atwork.modules.route.manager

import com.foreveross.atwork.modules.route.action.RouteAction
import com.foreveross.atwork.modules.route.model.RouteParams

interface IRouteActionProducer {
    fun produce(routeParams: RouteParams): RouteAction?

    fun canRoute(routeParams: RouteParams): Boolean = null != produce(routeParams)
}