package com.foreveross.atwork.modules.chat.component.multipart.item.content.reference

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IStickerChatView
import com.foreveross.atwork.modules.chat.presenter.StickerChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_gif.view.tvReply
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_gif.view.tvTitle
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_sticker.view.*

class MultipartMessageDetailReferenceStickerContentView(context: Context) : MultipartMessageDetailReferenceContentView<StickerChatMessage>(context), IStickerChatView {



    init {
        chatViewRefreshUIPresenter = StickerChatViewRefreshUIPresenter(this)
    }

    override fun contentView(): ImageView = ivContent

    override fun authorNameView(): TextView = tvTitle

    override fun replyView(): TextView = tvReply

    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_reference_sticker, this)

    }



}