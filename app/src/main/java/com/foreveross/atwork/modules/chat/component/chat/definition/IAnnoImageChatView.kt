package com.foreveross.atwork.modules.chat.component.chat.definition

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface IAnnoImageChatView: IChatView {

    fun commentView(): TextView

    fun mediaContentView(): RecyclerView
}