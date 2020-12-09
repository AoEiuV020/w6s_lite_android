package com.foreveross.atwork.modules.chat.presenter

import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoFileTransferChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IAnnoFileChatView

class AnnoFileChatViewRefreshUIPresenter(private val fileChatView: IAnnoFileChatView): FileChatViewRefreshUIPresenter(fileChatView) {


    override fun refreshItemView(chatPostMessage: FileTransferChatMessage) {
        super.refreshItemView(chatPostMessage)

        if(chatPostMessage is AnnoFileTransferChatMessage) {
            fileChatView.commentView().text = chatPostMessage.comment
        }
    }
}