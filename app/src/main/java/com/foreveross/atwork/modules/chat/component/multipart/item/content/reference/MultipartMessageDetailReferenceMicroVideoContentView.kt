package com.foreveross.atwork.modules.chat.component.multipart.item.content.reference

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IMicroVideoChatView
import com.foreveross.atwork.modules.chat.presenter.MicroVideoChatViewRefreshUIPresenter
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_micro_video.view.*

class MultipartMessageDetailReferenceMicroVideoContentView(context: Context) : MultipartMessageDetailReferenceContentView<MicroVideoChatMessage>(context), IMicroVideoChatView {



    init {
        chatViewRefreshUIPresenter = MicroVideoChatViewRefreshUIPresenter(context,this)
    }


    override fun authorNameView(): TextView = tvTitle

    override fun replyView(): TextView = tvReply

    override fun coverView(): ImageView = ivVideoCover

    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_reference_micro_video, this)

    }


}