package com.foreveross.atwork.modules.route.model

import android.content.Context
import android.content.Intent
import com.foreveross.atwork.modules.route.action.RouteAction

class IntentRouteAction(

        var intent: Intent

) : RouteAction() {
    override fun getActionIntent(context: Context): Intent? {
        return intent
    }


}