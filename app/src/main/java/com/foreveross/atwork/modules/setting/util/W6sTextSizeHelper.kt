@file: JvmName("W6sTextSizeHelper")

package com.foreveross.atwork.modules.setting.util

import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo

fun getWebviewTextSizeSetNative(): Int = (100 + (getWebviewTextSizeLevel() - 1).toDouble() * 0.1 * 100.0).toInt()

fun getWebviewTextSizeLevel(): Int {
    val context = AtworkApplicationLike.baseContext

    if(PersonalShareInfo.getInstance().getCommonTextSizeSyncWebview(context)) {
        return PersonalShareInfo.getInstance().getTextSizeLevel(context)
    }

    val webviewTextSizeLevel = PersonalShareInfo.getInstance().getWebviewTextSizeLevel(context)

    return webviewTextSizeLevel
}