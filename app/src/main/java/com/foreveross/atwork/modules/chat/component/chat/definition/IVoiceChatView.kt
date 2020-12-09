package com.foreveross.atwork.modules.chat.component.chat.definition

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

interface IVoiceChatView: IChatView {

    fun voiceBgFlView() : FrameLayout

    fun voiceView(): ImageView

    fun durationView(): TextView
}