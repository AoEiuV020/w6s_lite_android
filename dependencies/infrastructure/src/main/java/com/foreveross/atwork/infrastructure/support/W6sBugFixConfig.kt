package com.foreveross.atwork.infrastructure.support

import java.util.*

class W6sBugFixConfig: BaseConfig() {

    var compatibilityAppDbColumnBiometricAuthentication = false

    override fun parse(pro: Properties) {
        pro.getProperty("W6SBUGFIX_CONFIG_COMPATIBILITY_APP_DB_COLUMN_BIOMETRIC_AUTHENTICATION").apply {
            compatibilityAppDbColumnBiometricAuthentication = toBoolean()
        }
    }

}