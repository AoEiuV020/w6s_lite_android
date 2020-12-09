package com.foreveross.atwork.modules.chat.component.multipart.item.content.commonMedia

import android.content.Context
import android.view.LayoutInflater
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.content.MultipartMessageDetailBasicContentView

abstract class MultipartMessageDetailCommonMediaContentView<T: ChatPostMessage>(context: Context): MultipartMessageDetailBasicContentView<T>(context) {
    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_common_media, this)

    }

}