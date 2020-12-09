package com.foreveross.atwork.modules.chat.component.multipart.item.content.reference

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IAnnoFileChatView
import com.foreveross.atwork.modules.chat.presenter.AnnoFileChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_anno_file.view.*
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_file.view.ivCover
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_file.view.tvName
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_micro_video.view.tvReply
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_micro_video.view.tvTitle

class MultipartMessageDetailReferenceAnnoFileContentView(context: Context) : MultipartMessageDetailReferenceContentView<FileTransferChatMessage>(context), IAnnoFileChatView {



    init {
        chatViewRefreshUIPresenter = AnnoFileChatViewRefreshUIPresenter(this)
    }


    override fun authorNameView(): TextView = tvTitle

    override fun replyView(): TextView = tvReply

    override fun coverView(): ImageView = ivCover

    override fun titleView(): TextView = tvName

    override fun commentView(): TextView = tvComment

    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_reference_anno_file, this)

    }


}