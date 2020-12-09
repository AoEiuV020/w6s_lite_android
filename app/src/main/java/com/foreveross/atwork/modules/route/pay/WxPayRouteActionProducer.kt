package com.foreveross.atwork.modules.route.pay

import com.foreveross.atwork.modules.route.action.RouteAction
import com.foreveross.atwork.modules.route.manager.IRouteActionProducer
import com.foreveross.atwork.modules.route.model.RouteParams

class WxPayRouteActionProducer: IRouteActionProducer {

    override fun produce(routeParams: RouteParams): RouteAction? {
        if(true == routeParams.getUri()?.path?.contains("pay")) {
            return WxPayRouteAction(routeParams)
        }

        return null
    }
}