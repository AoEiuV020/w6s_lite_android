package com.foreveross.atwork.modules.chat.component.multipart.item.content

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage
import com.foreveross.atwork.modules.chat.component.chat.definition.IAnnoImageChatView
import com.foreveross.atwork.modules.chat.presenter.AnnoImageChatViewRefreshUIPresenter
import com.foreveross.atwork.modules.image.activity.ImageSwitchInChatActivity
import kotlinx.android.synthetic.main.item_multipart_message_detail_anno_image.view.*

class MultipartMessageDetailAnnoImageContentView(context: Context): MultipartMessageDetailBasicContentView<AnnoImageChatMessage>(context), IAnnoImageChatView {

    private val presenter = AnnoImageChatViewRefreshUIPresenter(context, this)
    private var dataList: List<ChatPostMessage>? = null
    private var annoImageChatMessage: AnnoImageChatMessage? = null
    init {
        registerListener()
    }

    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_anno_image, this)

    }

    private fun registerListener() {
        presenter.adapter.setOnItemClickListener { adapter, view, position ->
            val imageChatMessage = annoImageChatMessage?.imageContentInfoMessages?.get(position)
            imageChatMessage?.let {
                ImageSwitchInChatActivity.showImageSwitchView(context, it, dataList, null)
            }
        }
    }

    fun refreshUI(message: AnnoImageChatMessage, dataList: List<ChatPostMessage>) {
        this.dataList = dataList
        this.annoImageChatMessage = message

        refreshUI(message)

    }

    override fun refreshUI(message: AnnoImageChatMessage) {
        presenter.refreshItemView(message)
    }

    override fun commentView(): TextView  = tvComment

    override fun mediaContentView(): RecyclerView = rvImageContent
}