package com.foreveross.atwork.infrastructure.model.workbench.content

import com.foreveross.atwork.infrastructure.utils.EnumLookupUtil

enum class WorkbenchContentSystemEvent {

    CREATE_GROUP,

    QRCODE_SCAN,

    BING_MESSAGE,

    VOICE_MEETING,

    UNKNOWN;


    companion object {
        fun parse(key: String): WorkbenchContentSystemEvent {
            var event = EnumLookupUtil.lookup(WorkbenchContentSystemEvent::class.java, key.toUpperCase())
            if(null == event) {
                event = UNKNOWN
            }

            return event
        }
    }

}