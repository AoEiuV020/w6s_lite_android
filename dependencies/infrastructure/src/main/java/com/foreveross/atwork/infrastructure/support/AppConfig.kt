package com.foreveross.atwork.infrastructure.support

import java.util.*

class AppConfig : BaseConfig(){


    var isForceUseCustomAppList = false

    var isNeedAppInSearch = true

    var canChatInServiceApp = true

    var needGuideByImage = false

    override fun parse(pro: Properties) {
        pro.getProperty("APP_CONFIG_FORCE_USE_CUSTOM_APP_LIST")?.apply {
            isForceUseCustomAppList = this.toBoolean()
        }

        pro.getProperty("APP_CONFIG_NEED_APP_IN_SEARCH")?.apply {
            isNeedAppInSearch = this.toBoolean()
        }

        pro.getProperty("APP_CONFIG_CAN_CHAT_IN_SERVICE_APP")?.apply {
            canChatInServiceApp = this.toBoolean()
        }


        pro.getProperty("APP_NEED_GUIDE_BY_IMAGE")?.apply {
            needGuideByImage = toBoolean()
        }

    }
}