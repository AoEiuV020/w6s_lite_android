package com.foreveross.atwork.modules.route.action

import android.content.Context
import android.content.Intent
import com.foreveross.atwork.modules.route.model.RouteParams

abstract class RouteAction(

    open var routeParams: RouteParams? = null

) {

    open fun action(context: Context) {
        getActionIntent(context)?.let { context.startActivity(it) }
    }

    open fun getActionIntent(context: Context): Intent? {
        return null
    }
}