package com.foreveross.atwork.modules.workbench.adapter.admin.provider

import com.chad.library.adapter.base.entity.node.BaseExpandNode
import com.chad.library.adapter.base.entity.node.BaseNode
import com.foreveross.atwork.api.sdk.app.responseJson.QueryAppItemResultByAdmin
import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.model.app.admin.QueryAppItemResultEntry


class AppEntryParentNode(
        val appEntry: AppBundles,
        val queryAppItemResult: QueryAppItemResultByAdmin?,
        val appEntriesChildren: MutableList<BaseNode>,
        val maxSelected: Int = -1,
        var select: Boolean = false

) : BaseExpandNode() {

    override val childNode: MutableList<BaseNode>? = appEntriesChildren

    fun isScopeEmpty() = queryAppItemResult?.scopes?.isEmpty()


    fun getQueryAppItemResultEntryData(): QueryAppItemResultEntry? {
        return queryAppItemResult?.entries
                ?.find { appEntry.id == it.entryId }
    }
}


class AppEntryChildNode(
        val appEntry: AppBundles,
        val queryAppItemResult: QueryAppItemResultByAdmin?,
        val maxSelected: Int = -1,
        var select: Boolean = false

) : BaseNode() {

    override val childNode: MutableList<BaseNode>? = null

    fun isScopeEmpty() = queryAppItemResult?.scopes?.isEmpty()

    fun getQueryAppItemResultEntryData(): QueryAppItemResultEntry? {
        return queryAppItemResult?.entries
                ?.find { appEntry.id == it.entryId }
    }

}

