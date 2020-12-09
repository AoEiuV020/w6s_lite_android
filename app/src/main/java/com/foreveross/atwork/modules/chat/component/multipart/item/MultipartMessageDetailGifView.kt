package com.foreveross.atwork.modules.chat.component.multipart.item

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.content.MultipartMessageDetailGifContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailGifView(context: Context): MultipartMessageDetailBasicCarrierView<ImageChatMessage, MultipartMessageDetailGifContentView>(context) {

    override fun newContentView(): MultipartMessageDetailGifContentView = MultipartMessageDetailGifContentView(context)

}