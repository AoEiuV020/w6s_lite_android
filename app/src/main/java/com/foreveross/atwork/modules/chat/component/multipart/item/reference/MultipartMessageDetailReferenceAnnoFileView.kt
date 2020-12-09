package com.foreveross.atwork.modules.chat.component.multipart.item.reference

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.MultipartMessageDetailBasicCarrierView
import com.foreveross.atwork.modules.chat.component.multipart.item.content.reference.MultipartMessageDetailReferenceAnnoFileContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailReferenceAnnoFileView(context: Context): MultipartMessageDetailBasicCarrierView<ReferenceMessage, MultipartMessageDetailReferenceAnnoFileContentView>(context) {

    override fun newContentView(): MultipartMessageDetailReferenceAnnoFileContentView = MultipartMessageDetailReferenceAnnoFileContentView(context)

}