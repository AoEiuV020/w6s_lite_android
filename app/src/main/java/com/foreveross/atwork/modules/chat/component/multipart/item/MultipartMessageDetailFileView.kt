package com.foreveross.atwork.modules.chat.component.multipart.item

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.content.commonMedia.MultipartMessageDetailFileContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailFileView(context: Context): MultipartMessageDetailBasicCarrierView<FileTransferChatMessage, MultipartMessageDetailFileContentView>(context) {

    override fun newContentView(): MultipartMessageDetailFileContentView = MultipartMessageDetailFileContentView(context)

}