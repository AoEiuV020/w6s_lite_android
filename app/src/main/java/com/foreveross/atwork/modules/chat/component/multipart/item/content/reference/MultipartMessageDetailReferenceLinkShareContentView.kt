package com.foreveross.atwork.modules.chat.component.multipart.item.content.reference

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IShareLinkChatView
import com.foreveross.atwork.modules.chat.presenter.ShareLinkChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_share_link.view.*

class MultipartMessageDetailReferenceLinkShareContentView(context: Context) : MultipartMessageDetailReferenceContentView<ShareChatMessage>(context), IShareLinkChatView {



    init {
        chatViewRefreshUIPresenter = ShareLinkChatViewRefreshUIPresenter(this)
    }


    override fun authorNameView(): TextView = tvTitle

    override fun replyView(): TextView = tvReply

    override fun coverView(): ImageView = ivCover

    override fun titleView(): TextView = tvLinkTitle

    override fun summaryView(): TextView = tvLinkSummary

    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_reference_share_link, this)

    }


}