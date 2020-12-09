package com.foreveross.atwork.infrastructure.model.workbench.content

import com.foreveross.atwork.infrastructure.utils.EnumLookupUtil

enum class WorkbenchContentEventType {

    SYSTEM,

    URL;


    companion object {

        fun parse(eventType: String?): WorkbenchContentEventType {

            if(null == eventType) {
                return WorkbenchContentEventType.URL
            }

            var type = EnumLookupUtil.lookup(WorkbenchContentEventType::class.java, eventType.toUpperCase())
            if(null == type) {
                type = WorkbenchContentEventType.URL
            }

            return type
        }

    }
}