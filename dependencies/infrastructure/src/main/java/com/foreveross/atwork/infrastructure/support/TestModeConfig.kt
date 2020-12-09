package com.foreveross.atwork.infrastructure.support

import com.foreveross.atwork.infrastructure.BaseApplicationLike
import java.util.*

class TestModeConfig : BaseConfig() {

    var isTestMode = false
        get() {
            if (BaseApplicationLike.sIsDebug) {
                return true
            }

            return field
        }


    var cmdShowTestConfigSetting = false

    override fun parse(pro: Properties) {
        pro.getProperty("TEST_MODE")?.apply {
            isTestMode = toBoolean()
        }


        pro.getProperty("TEST_MODE_CMD_SHOW_TEST_CONFIG_SETTING")?.apply {
            cmdShowTestConfigSetting = toBoolean()
        }
    }
}