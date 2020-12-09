package com.foreveross.atwork.modules.chat.component.multipart.item

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.content.MultipartMessageDetailImageContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailImageView(context: Context): MultipartMessageDetailBasicCarrierView<ImageChatMessage, MultipartMessageDetailImageContentView>(context) {

    override fun newContentView(): MultipartMessageDetailImageContentView = MultipartMessageDetailImageContentView(context)

}