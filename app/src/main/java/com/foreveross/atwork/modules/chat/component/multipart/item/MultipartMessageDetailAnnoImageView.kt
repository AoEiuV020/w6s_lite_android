package com.foreveross.atwork.modules.chat.component.multipart.item

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.content.MultipartMessageDetailAnnoImageContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailAnnoImageView(context: Context): MultipartMessageDetailBasicCarrierView<AnnoImageChatMessage, MultipartMessageDetailAnnoImageContentView>(context) {

    override fun newContentView(): MultipartMessageDetailAnnoImageContentView = MultipartMessageDetailAnnoImageContentView(context)

}