package com.foreveross.atwork.infrastructure.model.workbench.content

interface IWorkbenchCardContent {
    var widgetsId: Long

    fun isContentDataEmpty(): Boolean
}