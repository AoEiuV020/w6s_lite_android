package com.foreveross.atwork.infrastructure.support

import java.util.*

class CircleConfig(

        var isCommandCircleEntryShowInPersonalInfoView: Boolean = false,

        var isNotifyShowInTab: Boolean = true
): BaseConfig() {

    override fun parse(pro: Properties) {
        pro.getProperty("CIRCLE_COMMAND_CIRCLE_ENTRY_SHOW_IN_PERSONAL_INFO_VIEW")?.apply {
            isCommandCircleEntryShowInPersonalInfoView = toBoolean()
        }


        pro.getProperty("CIRCLE_NOTIFY_SHOW_IN_TAB")?.apply {
            isNotifyShowInTab = toBoolean()
        }
    }
}