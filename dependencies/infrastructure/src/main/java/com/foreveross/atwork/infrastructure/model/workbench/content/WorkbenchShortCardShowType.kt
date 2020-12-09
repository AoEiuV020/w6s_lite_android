package com.foreveross.atwork.infrastructure.model.workbench.content

import com.foreveross.atwork.infrastructure.utils.EnumLookupUtil

enum class WorkbenchShortCardShowType {

    ICON,

    NUMBER;


    companion object {
        fun parse(showType: String?): WorkbenchShortCardShowType {
            if(null == showType) {
                return WorkbenchShortCardShowType.NUMBER
            }

            var type = EnumLookupUtil.lookup(WorkbenchShortCardShowType::class.java, showType.toUpperCase())
            if(null == type) {
                type = WorkbenchShortCardShowType.NUMBER
            }

            return type
        }

    }
}