@file: JvmName("KeyboardHeightHelper")
package com.foreveross.atwork.modules.chat.util

import com.foreveross.atwork.infrastructure.utils.DensityUtil

fun getFunctionAreaShowHeight(keyboardInputHeight: Int): Int {
    val px230dp = DensityUtil.dip2px(230f)
    return if (px230dp <= keyboardInputHeight) keyboardInputHeight else px230dp
}