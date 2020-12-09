package com.foreveross.atwork.modules.web.route

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.main.activity.MainActivity
import com.foreveross.atwork.modules.route.action.RouteAction
import com.foreveross.atwork.modules.route.model.RouteParams

class MainViewRouteAction(routeParams: RouteParams?) : RouteAction(routeParams) {

    override fun action(context: Context) {
        val tabSelect: String = routeParams?.getUri()?.getQueryParameter("tabSelect")?:return

        if(!StringUtils.isEmpty(tabSelect)) {
            val intent = Intent(MainActivity.ACTION_SELECT_TAB)
            intent.putExtra(MainActivity.DATA_TAB, tabSelect)

            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }


    }

}