package com.foreveross.atwork.modules.chat.component.chat.definition

import android.widget.ImageView
import android.widget.TextView

interface IShareLinkChatView: IChatView {

    fun titleView(): TextView

    fun summaryView(): TextView

    fun coverView(): ImageView
}