package com.foreveross.atwork.infrastructure.utils.rom

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast


/**
 *  create by reyzhang22 at 2019-08-15
 */
object HuaweiRomUtils {


    /**
     * 去华为权限申请页面
     */
    @JvmStatic
    fun applyPermission(context: Context) {
        try {
            val intent = Intent()
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            var comp = ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity")//悬浮窗管理页面
            intent.setComponent(comp)
            if (RomUtil.getEmuiVersion() === 3.1) {
                //emui 3.1 的适配
                context.startActivity(intent)
            } else {
                //emui 3.0 的适配
                comp = ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity")//悬浮窗管理页面
                intent.setComponent(comp)
                context.startActivity(intent)
            }
        } catch (e: SecurityException) {
            val intent = Intent()
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val comp = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity")//华为权限管理，跳转到本app的权限管理页面,这个需要华为接口权限，未解决
            intent.setComponent(comp)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent()
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val comp = ComponentName("com.Android.settings", "com.android.settings.permission.TabItem")//权限管理页面 android4.4
            intent.setComponent(comp)
            context.startActivity(intent)
            e.printStackTrace()
        } catch (e: Exception) {
            //抛出异常时提示信息
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show()
        }

    }
}