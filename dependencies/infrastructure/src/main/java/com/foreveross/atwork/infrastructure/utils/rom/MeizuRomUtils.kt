package com.foreveross.atwork.infrastructure.utils.rom

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import android.util.Log
import com.foreveross.atwork.infrastructure.utils.rom.FloatWindowPermissionUtil.COMMON_REQUEST_FLOAT_CODE


/**
 *  create by reyzhang22 at 2019-08-15
 */
object MeizuRomUtils {

    /**
     * 去魅族权限申请页面
     */
    @JvmStatic
    fun applyPermission(fragment: Fragment) {
        try {
            val intent = getPermissionIntent(fragment.context)
            fragment.startActivityForResult(intent, COMMON_REQUEST_FLOAT_CODE)
        } catch (e: Exception) {
            try {
                // 最新的魅族flyme 6.2.5 用上述方法获取权限失败, 不过又可以用下述方法获取权限了
                FloatWindowPermissionUtil.commonRomPermissionApply(fragment)
            } catch (eFinal: Exception) {
                Log.e("ROM", "获取悬浮窗权限失败, 通用获取方法失败, " + Log.getStackTraceString(eFinal))
            }

        }

    }

    /**
     * 去魅族权限申请页面
     */
    @JvmStatic
    fun applyPermission(activity: Activity) {
        try {
            val intent = getPermissionIntent(activity)
            activity.startActivityForResult(intent, COMMON_REQUEST_FLOAT_CODE)
        } catch (e: Exception) {
            try {
                // 最新的魅族flyme 6.2.5 用上述方法获取权限失败, 不过又可以用下述方法获取权限了
                FloatWindowPermissionUtil.commonRomPermissionApply(activity)
            } catch (eFinal: Exception) {
                Log.e("ROM", "获取悬浮窗权限失败, 通用获取方法失败, " + Log.getStackTraceString(eFinal))
            }

        }

    }

    private fun getPermissionIntent(context: Context?): Intent {
        val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
        intent.putExtra("packageName", context?.packageName)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        return intent
    }
}