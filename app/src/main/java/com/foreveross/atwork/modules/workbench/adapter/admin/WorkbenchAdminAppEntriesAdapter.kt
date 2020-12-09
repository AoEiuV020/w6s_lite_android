package com.foreveross.atwork.modules.workbench.adapter.admin

import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.foreveross.atwork.modules.workbench.adapter.admin.provider.AppEntryChildNode
import com.foreveross.atwork.modules.workbench.adapter.admin.provider.AppEntryParentNode
import com.foreveross.atwork.modules.workbench.adapter.admin.provider.WorkbenchAdminAppEntryChildNodeProvider
import com.foreveross.atwork.modules.workbench.adapter.admin.provider.WorkbenchAdminAppEntryParentNodeProvider

class WorkbenchAdminAppEntriesAdapter: BaseNodeAdapter() {

    init {
        addNodeProvider(WorkbenchAdminAppEntryParentNodeProvider())
        addNodeProvider(WorkbenchAdminAppEntryChildNodeProvider())
    }

    override fun getItemType(data: List<BaseNode>, position: Int): Int = when(data[position]) {
        is AppEntryParentNode -> 0
        is AppEntryChildNode -> 1
        else -> -1
    }
}