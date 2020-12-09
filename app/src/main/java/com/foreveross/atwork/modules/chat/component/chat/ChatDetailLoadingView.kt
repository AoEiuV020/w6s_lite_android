package com.foreveross.atwork.modules.chat.component.chat

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.foreveross.atwork.infrastructure.utils.extension.dp2px

class ChatDetailLoadingView(context: Context): FrameLayout(context) {

    init {

        this.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 40f.dp2px)

        val progressBar = ProgressBar(context)
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER

        progressBar.layoutParams = layoutParams

        addView(progressBar)
    }
}