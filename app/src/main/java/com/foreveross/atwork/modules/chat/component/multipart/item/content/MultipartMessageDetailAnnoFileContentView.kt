package com.foreveross.atwork.modules.chat.component.multipart.item.content

import android.content.Context
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoFileTransferChatMessage
import kotlinx.android.synthetic.main.item_multipart_message_detail_anno_file.view.*

class MultipartMessageDetailAnnoFileContentView(context: Context): MultipartMessageDetailBasicContentView<AnnoFileTransferChatMessage>(context) {


    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_anno_file, this)

    }

    override fun refreshUI(message: AnnoFileTransferChatMessage) {
        vChatFile.refreshFileItem(message)
        vChatFile.tvFileStatus.isVisible = false
        tvComment.text = message.comment

    }
}