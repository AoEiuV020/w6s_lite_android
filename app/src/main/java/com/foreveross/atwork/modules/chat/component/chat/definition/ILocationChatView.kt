package com.foreveross.atwork.modules.chat.component.chat.definition

import android.widget.ImageView
import android.widget.TextView

interface ILocationChatView: IChatView {

    fun coverView(): ImageView

    fun descView(): TextView
}