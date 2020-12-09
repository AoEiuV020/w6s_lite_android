package com.foreveross.atwork.modules.chat.component.multipart.item.content

import android.content.Context
import android.view.LayoutInflater
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage

class MultipartMessageDetailUnknownContentView(context: Context): MultipartMessageDetailBasicContentView<ChatPostMessage>(context) {
    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_unknown, this)

    }

    override fun refreshUI(message: ChatPostMessage) {

    }
}