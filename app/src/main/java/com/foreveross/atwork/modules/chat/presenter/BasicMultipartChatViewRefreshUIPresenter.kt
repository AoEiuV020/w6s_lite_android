package com.foreveross.atwork.modules.chat.presenter

import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IMultipartChatView
import com.foreveross.atwork.modules.chat.util.MultipartMsgHelper

abstract class BasicMultipartChatViewRefreshUIPresenter(private val multipartChatView: IMultipartChatView): IChatViewRefreshUIPresenter<MultipartChatMessage>(multipartChatView) {

    override fun refreshItemView(chatPostMessage: MultipartChatMessage) {
        multipartChatView.titleView().text = MultipartMsgHelper.getTitle(chatPostMessage)
    }

}