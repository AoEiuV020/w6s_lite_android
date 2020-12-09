@file: JvmName("BasePermissionHelperV2")
package com.foreveross.atwork.modules.login.util

import android.content.Context
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission

var needAskBasePermission = true

val defaultBasePermissions = arrayListOf(
        "android.permission.READ_PHONE_STATE",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE"
)

fun parseGroup(group: String): Array<String>? = when(group.toUpperCase()) {
    "STORAGE" -> Permission.Group.STORAGE
    "CONTACTS" -> Permission.Group.CONTACTS
    "LOCATION" -> Permission.Group.LOCATION
    "PHONE" -> Permission.Group.PHONE
    else -> null
}

fun getBasicPermissions(): Array<String>  {
    val basicPermissions = HashSet(defaultBasePermissions)

    BeeWorks.getInstance().basePermissions?.forEach { basePermissionConfigItem ->
        if(basePermissionConfigItem.enabled) {
            if(!StringUtils.isEmpty(basePermissionConfigItem.item)) {
                basicPermissions.add(basePermissionConfigItem.item)
            }

            if(!StringUtils.isEmpty(basePermissionConfigItem.group)) {
                parseGroup(basePermissionConfigItem.group!!)?.let { basicPermissions.addAll(it) }

            }


        } else {
            if(!StringUtils.isEmpty(basePermissionConfigItem.item)) {
                basicPermissions.remove(basePermissionConfigItem.item)
            }


            if(!StringUtils.isEmpty(basePermissionConfigItem.group)) {
                parseGroup(basePermissionConfigItem.group!!)?.let { basicPermissions.removeAll(it) }
            }
        }

    }

    return basicPermissions.toTypedArray()

}

fun requestPermissions(context: Context, action: () -> Unit){
    val permissionRequest = getBasicPermissions()
    if(permissionRequest.isEmpty()) {
        action()
        return
    }

    AndPermission
            .with(context)
            .runtime()
            .permission(permissionRequest)
            .onGranted { action() }
            .onDenied { action() }
            .start()
}