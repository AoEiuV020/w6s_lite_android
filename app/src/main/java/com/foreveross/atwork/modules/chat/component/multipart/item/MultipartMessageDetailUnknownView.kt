package com.foreveross.atwork.modules.chat.component.multipart.item

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.content.MultipartMessageDetailUnknownContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailUnknownView(context: Context): MultipartMessageDetailBasicCarrierView<ChatPostMessage, MultipartMessageDetailUnknownContentView>(context) {

    override fun newContentView(): MultipartMessageDetailUnknownContentView = MultipartMessageDetailUnknownContentView(context)

}