package com.foreveross.atwork.modules.chat.component.multipart.item.content

import android.content.Context
import android.view.LayoutInflater
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage
import com.foreveross.atwork.utils.ImageChatHelper
import kotlinx.android.synthetic.main.item_multipart_message_detail_gif.view.*

class MultipartMessageDetailGifContentView(context: Context): MultipartMessageDetailBasicContentView<ImageChatMessage>(context) {
    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_gif, this)

    }

    override fun refreshUI(message: ImageChatMessage) {
        ivGif.tag = message.deliveryId
        ImageChatHelper.showGif(context, ivGif, ivTagGif, message)
    }
}