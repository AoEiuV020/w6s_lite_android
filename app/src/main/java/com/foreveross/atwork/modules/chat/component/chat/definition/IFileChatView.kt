package com.foreveross.atwork.modules.chat.component.chat.definition

import android.widget.ImageView
import android.widget.TextView

interface IFileChatView: IChatView {

    fun titleView(): TextView

    fun coverView(): ImageView

    fun sizeView(): TextView? = null
}