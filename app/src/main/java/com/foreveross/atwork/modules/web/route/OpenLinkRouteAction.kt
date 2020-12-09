package com.foreveross.atwork.modules.web.route

import android.content.Context
import android.content.Intent
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.modules.app.activity.WebViewActivity
import com.foreveross.atwork.modules.route.action.RouteAction
import com.foreveross.atwork.modules.route.model.RouteParams

class OpenLinkRouteAction(routeParams: RouteParams?) : RouteAction(routeParams) {


    override fun getActionIntent(context: Context): Intent? {
        val openUrl: String? = routeParams?.getUri()?.getQueryParameter("url")

        openUrl?.apply {
            val webViewControlAction = WebViewControlAction.newAction()
                    .setUrl(this)
                    .setNeedShare(false)
                    .setHideTitle(true)


            val intent = WebViewActivity.getIntent(BaseApplicationLike.baseContext, webViewControlAction)
            return intent
        }

        return null
    }
}