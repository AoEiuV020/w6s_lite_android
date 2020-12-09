package com.foreveross.atwork.infrastructure.utils.rom

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK


/**
 *  create by reyzhang22 at 2019-08-15
 */
object OppoRomUtils {

    /**
     * oppo ROM 权限申请
     */
    @JvmStatic
    fun applyPermission(context: Context) {
        //merge requestPermission from https://github.com/zhaozepeng/FloatWindowPermission/pull/26
        try {
            val intent = Intent()
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            val comp = ComponentName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity")
            intent.component = comp
            context.startActivity(intent)
        } catch (e: Exception) {
            FloatWindowPermissionUtil.commonRomPermissionApply(context)
        }

    }
}