package com.foreveross.atwork.modules.chat.component.multipart.item.content

import android.content.Context
import android.view.LayoutInflater
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage
import com.foreveross.atwork.utils.ImageChatHelper
import kotlinx.android.synthetic.main.item_multipart_message_detail_sticker.view.*

class MultipartMessageDetailStickerContentView(context: Context): MultipartMessageDetailBasicContentView<StickerChatMessage>(context) {
    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_sticker, this)

    }

    override fun refreshUI(message: StickerChatMessage) {
        ImageChatHelper.initStickerContent(message, ivStickerMessage)
    }
}