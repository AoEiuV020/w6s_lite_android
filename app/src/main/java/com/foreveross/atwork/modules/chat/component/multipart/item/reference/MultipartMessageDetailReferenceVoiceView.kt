package com.foreveross.atwork.modules.chat.component.multipart.item.reference

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.MultipartMessageDetailBasicCarrierView
import com.foreveross.atwork.modules.chat.component.multipart.item.content.reference.MultipartMessageDetailReferenceVoiceContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailReferenceVoiceView(context: Context): MultipartMessageDetailBasicCarrierView<ReferenceMessage, MultipartMessageDetailReferenceVoiceContentView>(context) {

    lateinit var multipartMessageDetailReferenceVoiceContentView: MultipartMessageDetailReferenceVoiceContentView

    override fun newContentView(): MultipartMessageDetailReferenceVoiceContentView {
        multipartMessageDetailReferenceVoiceContentView = MultipartMessageDetailReferenceVoiceContentView(context)
        return multipartMessageDetailReferenceVoiceContentView
    }

}