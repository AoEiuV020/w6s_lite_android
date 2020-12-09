package com.foreveross.atwork.modules.chat.presenter

import android.graphics.Color
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IFileChatView
import com.foreveross.atwork.modules.file.util.FileMediaTypeUtil
import com.foreveross.atwork.utils.ChatMessageHelper

open class FileChatViewRefreshUIPresenter(private val fileChatView: IFileChatView): IChatViewRefreshUIPresenter<FileTransferChatMessage>(fileChatView) {

    var normalFileNameColor: String = "#80000000"

    var expiredFileNameColor: String = "#000000"


    override fun refreshItemView(chatPostMessage: FileTransferChatMessage) {
        fileChatView.coverView().setImageResource(FileMediaTypeUtil.getFileTypeIcon(chatPostMessage))

        fileChatView.titleView().text = chatPostMessage.name

        if (ChatMessageHelper.isOverdue(chatPostMessage)) {
            fileChatView.titleView().setTextColor(Color.parseColor(expiredFileNameColor))
            fileChatView.coverView().alpha = 0.5f
        } else {
            fileChatView.titleView().setTextColor(Color.parseColor(normalFileNameColor))
            fileChatView.coverView().alpha = 1f

        }

        fileChatView.sizeView()?.apply {
            text = ChatMessageHelper.getMBOrKBString(chatPostMessage.size)
        }
    }


}