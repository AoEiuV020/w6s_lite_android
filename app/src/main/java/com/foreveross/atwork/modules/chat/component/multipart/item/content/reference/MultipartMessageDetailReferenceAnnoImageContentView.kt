package com.foreveross.atwork.modules.chat.component.multipart.item.content.reference

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IAnnoImageChatView
import com.foreveross.atwork.modules.chat.presenter.AnnoImageChatViewRefreshUIPresenter
import com.foreveross.atwork.modules.image.activity.ImageSwitchInChatActivity
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_anno_file.view.*
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_anno_file.view.tvComment
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_anno_image.view.*
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_micro_video.view.tvReply
import kotlinx.android.synthetic.main.item_multipart_message_detail_reference_micro_video.view.tvTitle

class MultipartMessageDetailReferenceAnnoImageContentView(context: Context) : MultipartMessageDetailReferenceContentView<AnnoImageChatMessage>(context), IAnnoImageChatView {

    var annoImageChatMessage: AnnoImageChatMessage? = null

    init {
        chatViewRefreshUIPresenter = AnnoImageChatViewRefreshUIPresenter(context,this)

        (chatViewRefreshUIPresenter as AnnoImageChatViewRefreshUIPresenter).adapter.setOnItemClickListener { adapter, view, position ->
            val imageChatMessage = annoImageChatMessage?.imageContentInfoMessages?.get(position)
            imageChatMessage?.let {
                ImageSwitchInChatActivity.showImageSwitchView(context, it, annoImageChatMessage?.imageContentInfoMessages as List<ChatPostMessage>, null)
            }
        }
    }


    override fun authorNameView(): TextView = tvTitle

    override fun replyView(): TextView = tvReply



    override fun commentView(): TextView = tvComment

    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_reference_anno_image, this)

    }

    override fun mediaContentView(): RecyclerView = rvImageContent

    override fun refreshUI(message: ReferenceMessage) {
        super.refreshUI(message)

        if(message.mReferencingMessage is AnnoImageChatMessage) {
            annoImageChatMessage = message.mReferencingMessage as AnnoImageChatMessage

        }
    }

}