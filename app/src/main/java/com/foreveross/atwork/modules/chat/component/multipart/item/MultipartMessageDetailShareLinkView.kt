package com.foreveross.atwork.modules.chat.component.multipart.item

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.content.commonMedia.MultipartMessageDetailShareLinkContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailShareLinkView(context: Context): MultipartMessageDetailBasicCarrierView<ShareChatMessage, MultipartMessageDetailShareLinkContentView>(context) {

    override fun newContentView(): MultipartMessageDetailShareLinkContentView = MultipartMessageDetailShareLinkContentView(context)

}