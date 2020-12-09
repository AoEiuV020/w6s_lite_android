package com.foreveross.atwork.modules.chat.component.multipart.item

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.content.MultipartMessageDetailVoiceContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailVoiceView(context: Context): MultipartMessageDetailBasicCarrierView<VoiceChatMessage, MultipartMessageDetailVoiceContentView>(context) {

    override fun newContentView(): MultipartMessageDetailVoiceContentView = MultipartMessageDetailVoiceContentView(context)

}