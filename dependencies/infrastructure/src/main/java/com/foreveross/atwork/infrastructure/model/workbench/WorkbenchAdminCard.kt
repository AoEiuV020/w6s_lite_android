package com.foreveross.atwork.infrastructure.model.workbench

class WorkbenchAdminCard: WorkbenchCard() {

    companion object {
        val ID = "widget_manager".hashCode().toLong()
        const val DEFAULT_SORT_ORDER = -1
    }

    init {
        id = ID
        sortOrder = DEFAULT_SORT_ORDER
        type = WorkbenchCardType.ADMIN
        name = "管理控制（仅超级管理员可见）"
    }
}