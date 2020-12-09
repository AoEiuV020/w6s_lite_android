package com.foreveross.atwork.infrastructure.model.workbench.content

import com.foreveross.atwork.infrastructure.utils.ListUtil

class WorkbenchListContent(

        override var widgetsId: Long,

        var itemList: List<WorkbenchListItem>?

):IWorkbenchCardContent {

    override fun isContentDataEmpty(): Boolean = ListUtil.isEmpty(itemList)
}