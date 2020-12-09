package com.foreveross.atwork.modules.chat.component.multipart.item

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.content.commonMedia.MultipartMessageDetailMicroVideoContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailMicroVideoView(context: Context): MultipartMessageDetailBasicCarrierView<MicroVideoChatMessage, MultipartMessageDetailMicroVideoContentView>(context) {

    override fun newContentView(): MultipartMessageDetailMicroVideoContentView = MultipartMessageDetailMicroVideoContentView(context)

}