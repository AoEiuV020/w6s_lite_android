package com.foreveross.atwork.modules.chat.component.chat.definition

import android.widget.TextView

interface IMultipartChatView: IChatView {

    fun titleView(): TextView

    fun descView(): TextView

}