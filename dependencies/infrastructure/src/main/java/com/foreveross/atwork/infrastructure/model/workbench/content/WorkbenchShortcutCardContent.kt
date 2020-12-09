package com.foreveross.atwork.infrastructure.model.workbench.content

import com.foreveross.atwork.infrastructure.utils.ListUtil

class WorkbenchShortcutCardContent(
        override var widgetsId: Long,

        var itemList: List<WorkbenchShortcutCardItem>?

) : IWorkbenchCardContent {

    override fun isContentDataEmpty(): Boolean = ListUtil.isEmpty(itemList)
}