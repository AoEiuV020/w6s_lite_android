package com.foreveross.atwork.modules.chat.presenter

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IChatView

abstract class IChatViewRefreshUIPresenter<T: ChatPostMessage>(chatVeew: IChatView) {

    abstract fun refreshItemView(chatPostMessage: T)
}