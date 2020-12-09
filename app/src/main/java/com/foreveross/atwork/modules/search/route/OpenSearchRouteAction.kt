package com.foreveross.atwork.modules.search.route

import android.content.Context
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.route.action.RouteAction
import com.foreveross.atwork.modules.route.model.RouteParams
import com.foreveross.atwork.modules.search.activity.NewSearchActivity
import com.foreveross.atwork.modules.search.model.NewSearchControlAction
import com.foreveross.atwork.modules.search.model.SearchContent
import java.util.*

open class OpenSearchRouteAction(routeParams: RouteParams? = null) : RouteAction(routeParams) {


    override fun action(context: Context) {
        val path = routeParams?.getUri()?.path
        if(StringUtils.isEmpty(path)) {
            routeNewSearchActivity(context)
        }
    }

    private fun routeNewSearchActivity(context: Context) {
        val searchContentList = ArrayList<SearchContent>()
        searchContentList.add(SearchContent.SEARCH_USER)

        if (AtworkConfig.DISSCUSSION_CONFIG.isNeedEntry) {
            searchContentList.add(SearchContent.SEARCH_DISCUSSION)
        }

        if (AtworkConfig.APP_CONFIG.isNeedAppInSearch) {
            searchContentList.add(SearchContent.SEARCH_APP)
        }

        searchContentList.add(SearchContent.SEARCH_MESSAGES)

        if (DomainSettingsManager.getInstance().handleFileAssistantEnable()) {
            searchContentList.add(SearchContent.SEARCH_DEVICE)

        }

        val newSearchControlAction = NewSearchControlAction()
        newSearchControlAction.searchContentList = searchContentList.toTypedArray()

        val intent = NewSearchActivity.getIntent(context, newSearchControlAction)
        context.startActivity(intent)
    }
}

