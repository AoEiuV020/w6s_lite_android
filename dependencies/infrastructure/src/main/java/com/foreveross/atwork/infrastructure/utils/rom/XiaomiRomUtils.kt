package com.foreveross.atwork.infrastructure.utils.rom

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS


/**
 *  create by reyzhang22 at 2019-08-15
 */
object XiaomiRomUtils {

    /**
     * 小米 ROM 权限申请
     */
    @JvmStatic
    fun applyPermission(context: Context) {
        when (RomUtil.getMiuiVersion()) {
            5 -> miuiPermissionActivity_V5(context)
            6, 7 -> miuiPermissionActivity_V6_V7(context)
            8 -> miuiPermissionActivity_V8(context)
            else -> miuiPermissionActivity_common(context)
        }
    }

    //小米 V5 版本 ROM权限申请
    private fun miuiPermissionActivity_V5(context: Context) {
        var intent: Intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (isIntentAvailable(intent!!, context)) {
            context.startActivity(intent)
        } else {
            FloatWindowPermissionUtil.commonRomPermissionApply(context)
        }
    }

    /**
     * 小米 V6 版本 ROM权限申请
     */
    private fun miuiPermissionActivity_V6_V7(context: Context) {
        val intent = getPermissionIntent(context, "com.miui.permcenter.permissions.AppPermissionsEditorActivity")

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent)
        } else {
            FloatWindowPermissionUtil.commonRomPermissionApply(context)
        }
    }


    /**
     * 小米 V8 版本 ROM权限申请
     */
    private fun miuiPermissionActivity_V8(context: Context) {
        var intent = getPermissionIntent(context, "com.miui.permcenter.permissions.PermissionsEditorActivity")

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent)
        } else {
            FloatWindowPermissionUtil.commonRomPermissionApply(context)
        }
    }

    private fun miuiPermissionActivity_common(context: Context) {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.setPackage("com.miui.securitycenter")
        intent.putExtra("extra_pkgname", context.packageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent)
        } else {
            FloatWindowPermissionUtil.commonRomPermissionApply(context)
        }
    }

    private fun getPermissionIntent(context: Context, className: String): Intent {
        var intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
        //        intent.setPackage("com.miui.securitycenter");
        intent.putExtra("extra_pkgname", context.packageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return intent
    }


    private fun isIntentAvailable(intent: Intent?, context: Context): Boolean {
        return if (intent == null) {
            false
        } else context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size > 0
    }


}