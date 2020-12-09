package com.foreverht.workplus.module.discussion

import android.view.View

fun hitTest(v: View, x: Int, y: Int): Boolean {
    val tx = (v.translationX + 0.5f).toInt()
    val ty = (v.translationY + 0.5f).toInt()
    val left = v.left + tx
    val right = v.right + tx
    val top = v.top + ty
    val bottom = v.bottom + ty
    return x >= left && x <= right && y >= top && y <= bottom
}