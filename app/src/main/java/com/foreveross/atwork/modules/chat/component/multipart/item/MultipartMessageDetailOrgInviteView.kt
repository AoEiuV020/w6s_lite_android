package com.foreveross.atwork.modules.chat.component.multipart.item

import android.annotation.SuppressLint
import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.content.commonMedia.MultipartMessageDetailOrgInviteContentView

@SuppressLint("ViewConstructor")
class MultipartMessageDetailOrgInviteView(context: Context): MultipartMessageDetailBasicCarrierView<ShareChatMessage, MultipartMessageDetailOrgInviteContentView>(context) {

    override fun newContentView(): MultipartMessageDetailOrgInviteContentView = MultipartMessageDetailOrgInviteContentView(context)

}