package com.foreveross.atwork.modules.chat.component.multipart.item.reference

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.MultipartMessageDetailBasicCarrierView
import com.foreveross.atwork.modules.chat.component.multipart.item.content.reference.MultipartMessageDetailReferenceMultipartContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetaiReferenceMultipartView(context: Context): MultipartMessageDetailBasicCarrierView<ReferenceMessage, MultipartMessageDetailReferenceMultipartContentView>(context) {

    override fun newContentView(): MultipartMessageDetailReferenceMultipartContentView = MultipartMessageDetailReferenceMultipartContentView(context)

}