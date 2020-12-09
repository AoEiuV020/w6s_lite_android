package com.foreveross.atwork.modules.chat.component.multipart.item

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.content.MultipartMessageDetailStickerContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailStickerView(context: Context): MultipartMessageDetailBasicCarrierView<StickerChatMessage, MultipartMessageDetailStickerContentView>(context) {

    override fun newContentView(): MultipartMessageDetailStickerContentView = MultipartMessageDetailStickerContentView(context)

}