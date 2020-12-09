package com.foreveross.atwork.modules.chat.component.multipart.item.content.reference

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IMultipartChatView
import com.foreveross.atwork.modules.chat.presenter.MultipartChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_multipart.view.*
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_text.view.tvReply
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_text.view.tvTitle

class MultipartMessageDetailReferenceMultipartContentView(context: Context) : MultipartMessageDetailReferenceContentView<MultipartChatMessage>(context), IMultipartChatView {



    init {
        chatViewRefreshUIPresenter = MultipartChatViewRefreshUIPresenter(this)
    }


    override fun authorNameView(): TextView = tvTitle

    override fun replyView(): TextView = tvReply

    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_reference_multipart, this)

    }

    override fun titleView(): TextView = tvMultipartTitle

    override fun descView(): TextView = tvMultipartContent


}