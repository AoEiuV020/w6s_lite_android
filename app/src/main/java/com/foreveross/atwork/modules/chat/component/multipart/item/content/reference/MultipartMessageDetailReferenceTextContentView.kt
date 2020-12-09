package com.foreveross.atwork.modules.chat.component.multipart.item.content.reference

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.ITextChatView
import com.foreveross.atwork.modules.chat.presenter.TextChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_text.view.*

class MultipartMessageDetailReferenceTextContentView(context: Context) : MultipartMessageDetailReferenceContentView<ChatPostMessage>(context), ITextChatView {


    init {
        chatViewRefreshUIPresenter = TextChatViewRefreshUIPresenter(this)
    }

    override fun contentView(): TextView = tvContent

    override fun authorNameView(): TextView = tvTitle

    override fun replyView(): TextView = tvReply

    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_reference_text, this)

    }


}