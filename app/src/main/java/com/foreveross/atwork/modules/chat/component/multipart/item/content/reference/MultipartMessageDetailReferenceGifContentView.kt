package com.foreveross.atwork.modules.chat.component.multipart.item.content.reference

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IGifChatView
import com.foreveross.atwork.modules.chat.presenter.GifChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_gif.view.*

class MultipartMessageDetailReferenceGifContentView(context: Context) : MultipartMessageDetailReferenceContentView<ImageChatMessage>(context), IGifChatView {



    init {
        chatViewRefreshUIPresenter = GifChatViewRefreshUIPresenter(this)
    }

    override fun contentView(): ImageView = givContent

    override fun authorNameView(): TextView = tvTitle

    override fun replyView(): TextView = tvReply

    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_reference_gif, this)

    }

    override fun gifTagView(): ImageView = ivTagGif


}