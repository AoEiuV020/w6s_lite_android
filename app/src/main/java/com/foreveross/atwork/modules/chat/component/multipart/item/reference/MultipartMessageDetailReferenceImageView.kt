package com.foreveross.atwork.modules.chat.component.multipart.item.reference

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.MultipartMessageDetailBasicCarrierView
import com.foreveross.atwork.modules.chat.component.multipart.item.content.reference.MultipartMessageDetailReferenceImageContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailReferenceImageView(context: Context): MultipartMessageDetailBasicCarrierView<ReferenceMessage, MultipartMessageDetailReferenceImageContentView>(context) {

    override fun newContentView(): MultipartMessageDetailReferenceImageContentView = MultipartMessageDetailReferenceImageContentView(context)

}