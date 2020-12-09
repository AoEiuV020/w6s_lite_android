package com.foreveross.atwork.modules.chat.component.multipart.item

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.content.commonMedia.MultipartMessageDetailShareLocationContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailShareLocationView(context: Context): MultipartMessageDetailBasicCarrierView<ShareChatMessage, MultipartMessageDetailShareLocationContentView>(context) {
    override fun newContentView(): MultipartMessageDetailShareLocationContentView  = MultipartMessageDetailShareLocationContentView(context)


}