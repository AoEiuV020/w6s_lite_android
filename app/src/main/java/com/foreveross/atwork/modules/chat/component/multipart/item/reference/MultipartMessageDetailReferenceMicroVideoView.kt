package com.foreveross.atwork.modules.chat.component.multipart.item.reference

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.MultipartMessageDetailBasicCarrierView
import com.foreveross.atwork.modules.chat.component.multipart.item.content.reference.MultipartMessageDetailReferenceMicroVideoContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailReferenceMicroVideoView(context: Context): MultipartMessageDetailBasicCarrierView<ReferenceMessage, MultipartMessageDetailReferenceMicroVideoContentView>(context) {

    override fun newContentView(): MultipartMessageDetailReferenceMicroVideoContentView = MultipartMessageDetailReferenceMicroVideoContentView(context)

}