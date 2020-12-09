package com.foreveross.atwork.modules.chat.component.multipart.item.reference

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.MultipartMessageDetailBasicCarrierView
import com.foreveross.atwork.modules.chat.component.multipart.item.content.reference.MultipartMessageDetailReferenceTextContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetaiReferenceTextView(context: Context): MultipartMessageDetailBasicCarrierView<ReferenceMessage, MultipartMessageDetailReferenceTextContentView>(context) {

    override fun newContentView(): MultipartMessageDetailReferenceTextContentView = MultipartMessageDetailReferenceTextContentView(context)

}