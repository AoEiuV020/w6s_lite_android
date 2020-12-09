package com.foreveross.atwork.infrastructure.support

import java.util.*

class MessageConfig: BaseConfig() {

    var modeDefaultOfflineMessagesOnSessionStrategy = false

    override fun parse(pro: Properties) {
        pro.getProperty("MESSAGE_CONFIG_MODE_DEFAULT_OFFLINE_MESSAGES_ON_SESSION_STRATEGY")?.apply {
            modeDefaultOfflineMessagesOnSessionStrategy = toBoolean()
        }
    }
}