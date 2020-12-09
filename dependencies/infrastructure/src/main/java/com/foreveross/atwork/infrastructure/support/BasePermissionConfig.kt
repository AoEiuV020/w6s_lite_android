package com.foreveross.atwork.infrastructure.support

import java.util.*

class BasePermissionConfig: BaseConfig() {

    var isNeedPermissionExternalStorage = true
    var isNeedPermissionReadPhoneState = true
    var isNeedPermissionCamera = true

    override fun parse(pro: Properties) {

        pro.getProperty("PERMISSION_EXTERNAL_STORAGE")?.apply {
            isNeedPermissionExternalStorage = toBoolean()
        }
        pro.getProperty("PERMISSION_READ_PHONE_STATE")?.apply {
            isNeedPermissionReadPhoneState = toBoolean()
        }
        pro.getProperty("PERMISSION_CAMERA")?.apply {
            isNeedPermissionCamera = toBoolean()
        }


    }
}