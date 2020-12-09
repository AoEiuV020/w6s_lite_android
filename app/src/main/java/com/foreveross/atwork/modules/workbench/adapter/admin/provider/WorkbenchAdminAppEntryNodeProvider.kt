package com.foreveross.atwork.modules.workbench.adapter.admin.provider

import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.app.responseJson.QueryAppItemResultByAdmin
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.app.activity.WebViewActivity
import com.foreveross.atwork.utils.AtworkToast

abstract class WorkbenchAdminAppEntryNodeProvider: BaseNodeProvider() {

    protected fun handleClick(data: BaseNode) {

        if(true == isScopeEmpty(data) && !isSelect(data)) {
            return
        }

//        allNodeUnSelect()
        if(isSelectWillBeMax(data)) {
            AtworkToast.showResToast(R.string.only_allow_to_add_apps, getMaxSelected(data))
            return
        }

        handleSelect(data)

        getAdapter()?.notifyDataSetChanged()
    }

    private fun isSelectWillBeMax(data: BaseNode): Boolean {
        val maxSelected = getMaxSelected(data)
        if(-1 == maxSelected) {
            return false
        }

        if(isSelect(data)) {
           return false
        }

        if(maxSelected >= getSelectCount() + 1) {
            return false
        }

        return true
    }

    private fun getMaxSelected(data: BaseNode): Int {
        var maxSelected = -1
        if (data is AppEntryParentNode) {
            maxSelected = data.maxSelected
        } else if (data is AppEntryChildNode) {
            maxSelected = data.maxSelected
        }

        return maxSelected
    }

    private fun getSelectCount() = getAdapter()?.data?.count { isSelect(it) } ?: 0



//    private fun allNodeUnSelect() {
//        getAdapter()?.data?.forEach { node ->
//            select(node, select = false)
//            if (node is AppEntryParentNode) {
//                node.appEntriesChildren.forEach {
//                    select(it, select = false)
//                }
//            }
//        }
//    }

    protected fun routeAdminAppModifyUrl(data: BaseNode) {
        val url = String.format(UrlConstantManager.getInstance().adminAppModifyUrl,
                LoginUserInfo.getInstance().getLoginUserId(AtworkApplicationLike.baseContext),
                getAppOrgCode(data),
                getAppId(data)
        )


        val webViewAction = WebViewControlAction.newAction().setUrl(url).setNeedShare(false)
        context.startActivity(WebViewActivity.getIntent(context, webViewAction))
    }

    private fun getAppBundles(data: BaseNode): AppBundles? {
        if (data is AppEntryParentNode) {
            return data.appEntry
        }

        if (data is AppEntryChildNode) {
            return data.appEntry
        }

        return null
    }

    private fun getAppOrgCode(data: BaseNode) = getAppBundles(data)?.mOrgId ?: PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext)

    protected fun getAppId(data: BaseNode) = getAppBundles(data)?.appId ?: StringUtils.EMPTY

    private fun handleSelect(data: BaseNode) {
        if (data is AppEntryParentNode) {
            data.select = !data.select
        } else if (data is AppEntryChildNode) {
            data.select = !data.select
        }
    }

    private fun isSelect(data: BaseNode): Boolean {
        if (data is AppEntryParentNode) {
            return data.select
        }

        if (data is AppEntryChildNode) {
            return data.select
        }

        return false
    }

    private fun isScopeEmpty(data: BaseNode): Boolean? {
        if(data is AppEntryParentNode) {
            return data.isScopeEmpty()
        }

        if(data is AppEntryChildNode) {
            return data.isScopeEmpty()
        }
        return null
    }

    private fun getQueryAppItemResultByAdminData(data: BaseNode): QueryAppItemResultByAdmin? {
        if(data is AppEntryParentNode) {
            return data.queryAppItemResult
        }

        if(data is AppEntryChildNode) {
            return data.queryAppItemResult
        }

        return null
    }

}