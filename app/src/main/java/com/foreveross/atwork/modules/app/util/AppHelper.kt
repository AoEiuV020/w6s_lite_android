@file: JvmName("AppHelper")

package com.foreveross.atwork.modules.app.util

import android.content.Context
import android.text.TextUtils
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.utils.ListUtil


/**
 * 判断应用是否已经安装
 *
 * @param packageName
 * @return
 */
fun isAppInstalled(context: Context, packageName: String): Boolean {
    try {

        val list = AtworkApplicationLike.getInstalledApps()
        if (ListUtil.isEmpty(list) || TextUtils.isEmpty(packageName)) {
            return false
        }
        if (list.contains(packageName)) {
            return true
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }

    return false

}

