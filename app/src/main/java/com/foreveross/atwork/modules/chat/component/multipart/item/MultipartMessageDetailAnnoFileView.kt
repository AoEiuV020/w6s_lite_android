package com.foreveross.atwork.modules.chat.component.multipart.item

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoFileTransferChatMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.content.MultipartMessageDetailAnnoFileContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailAnnoFileView(context: Context): MultipartMessageDetailBasicCarrierView<AnnoFileTransferChatMessage, MultipartMessageDetailAnnoFileContentView>(context) {

    override fun newContentView(): MultipartMessageDetailAnnoFileContentView = MultipartMessageDetailAnnoFileContentView(context)

}