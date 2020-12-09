package com.foreveross.atwork.modules.chat.presenter

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoFileTransferChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage
import com.foreveross.atwork.infrastructure.support.StringConstants
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.chat.component.chat.definition.ITextChatView

class TextChatViewRefreshUIPresenter(private val textChatView: ITextChatView): IChatViewRefreshUIPresenter<ChatPostMessage>(textChatView) {

    override fun refreshItemView(chatPostMessage: ChatPostMessage) {
        if (chatPostMessage is TextChatMessage) {
            textChatView.contentView().text = chatPostMessage.text
            return
        }


        if (chatPostMessage is ReferenceMessage) {
            textChatView.contentView().text = chatPostMessage.mReply
            return
        }

        if(chatPostMessage is AnnoFileTransferChatMessage) {
            textChatView.contentView().text = chatPostMessage.comment
            return
        }

        if(chatPostMessage is AnnoImageChatMessage) {
            if (!StringUtils.isEmpty(chatPostMessage.comment)) {
                textChatView.contentView().text = chatPostMessage.comment
            } else {
                textChatView.contentView().text = StringConstants.SESSION_CONTENT_IMG

            }
            return
        }
    }

}