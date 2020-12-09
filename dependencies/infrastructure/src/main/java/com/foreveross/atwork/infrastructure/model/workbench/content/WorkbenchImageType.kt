package com.foreveross.atwork.infrastructure.model.workbench.content

import com.foreveross.atwork.infrastructure.utils.EnumLookupUtil

enum class WorkbenchImageType {

    URL,

    MEDIAID,

    INNER;


    companion object {

        fun parse(iconType: String?): WorkbenchImageType {
            if(null == iconType) {
                return WorkbenchImageType.URL
            }

            var type = EnumLookupUtil.lookup(WorkbenchImageType::class.java, iconType.toUpperCase())
            if(null == type) {
                type = WorkbenchImageType.URL
            }

            return type
        }
    }
}