package com.foreveross.atwork.infrastructure.support

import java.util.*
import kotlin.collections.ArrayList

class BiometricAuthenticationConfig: BaseConfig() {

    var gestureLockLowStrength : List<String> = ArrayList()

    var bioAuthLockEnableJustHighPriority = false

    var bioAuthProtectedIconNeedInAppView = true

    override fun parse(pro: Properties) {
        pro.getProperty("BIOMETRIC_AUTHENTICATION_CONFIG_GESTURE_LOCK_LOW_STRENGTH")?.apply {
            gestureLockLowStrength = split(",").toList()
        }


        pro.getProperty("BIOMETRIC_AUTHENTICATION_CONFIG_ENABLE_JUST_HIGH_PRIORITY")?.apply {
            bioAuthLockEnableJustHighPriority = toBoolean()
        }

        pro.getProperty("BIOMETRIC_AUTHENTICATION_CONFIG_PROTECTED_ICON_NEED_IN_APP_VIEW")?.apply {
            bioAuthProtectedIconNeedInAppView = toBoolean()
        }
    }
}