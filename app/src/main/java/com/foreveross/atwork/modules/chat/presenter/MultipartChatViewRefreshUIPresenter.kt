package com.foreveross.atwork.modules.chat.presenter

import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IMultipartChatView

class MultipartChatViewRefreshUIPresenter(private val multipartChatView: IMultipartChatView): BasicMultipartChatViewRefreshUIPresenter(multipartChatView) {

    override fun refreshItemView(chatPostMessage: MultipartChatMessage) {
        super.refreshItemView(chatPostMessage)

        multipartChatView.descView().text = chatPostMessage.mContent


    }
}