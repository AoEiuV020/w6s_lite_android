package com.foreveross.atwork.modules.chat.component.multipart.item.reference

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.MultipartMessageDetailBasicCarrierView
import com.foreveross.atwork.modules.chat.component.multipart.item.content.reference.MultipartMessageDetailReferenceLinkShareContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailReferenceShareLinkView(context: Context): MultipartMessageDetailBasicCarrierView<ReferenceMessage, MultipartMessageDetailReferenceLinkShareContentView>(context) {

    override fun newContentView(): MultipartMessageDetailReferenceLinkShareContentView = MultipartMessageDetailReferenceLinkShareContentView(context)

}