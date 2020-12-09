package com.foreveross.atwork.infrastructure.support

import java.util.*

/**
 *  create by reyzhang22 at 2020/3/10
 */
const val GITEE_CONFIG_PRO_KEY = "GITEE_CONFIG_ENABLE"
class GiteeConfig(
        var enable: Boolean = false
): BaseConfig() {

    override fun parse(pro: Properties) {
        if (pro.contains(GITEE_CONFIG_PRO_KEY)) {
            enable =  pro.getProperty(GITEE_CONFIG_PRO_KEY)!!.toBoolean()
        }
    }
}