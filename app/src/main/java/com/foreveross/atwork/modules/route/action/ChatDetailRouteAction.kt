package com.foreveross.atwork.modules.route.action

import android.content.Context
import android.content.Intent
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity
import com.foreveross.atwork.modules.route.model.RouteParams
import com.foreveross.atwork.services.ImSocketService

/**
 *  create by reyzhang22 at 2019-10-29
 */
class ChatDetailRouteAction(routeParams: RouteParams?) : RouteAction(routeParams) {

    override fun action(context: Context) {
        val imIntent = Intent(context, ImSocketService::class.java)
        context.startService(imIntent)
        ImSocketService.checkConnection(context)
        val intent = ChatDetailActivity.getIntent(context, routeParams?.getFrom())
        context.startActivity(intent)
    }
}