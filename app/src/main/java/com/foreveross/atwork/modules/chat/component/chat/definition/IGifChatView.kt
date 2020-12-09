package com.foreveross.atwork.modules.chat.component.chat.definition

import android.widget.ImageView

interface IGifChatView : IChatView {

    fun contentView(): ImageView

    fun gifTagView(): ImageView
}