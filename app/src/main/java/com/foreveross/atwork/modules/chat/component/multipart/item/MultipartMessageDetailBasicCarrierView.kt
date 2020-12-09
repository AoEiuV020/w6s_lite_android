package com.foreveross.atwork.modules.chat.component.multipart.item

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage
import com.foreveross.atwork.modules.chat.component.multipart.MultipartMessageDetailBasicView
import com.foreveross.atwork.modules.chat.component.multipart.item.content.MultipartMessageDetailAnnoImageContentView
import com.foreveross.atwork.modules.chat.component.multipart.item.content.MultipartMessageDetailBasicContentView
import kotlinx.android.synthetic.main.item_multipart_message_detail_basic.view.*

abstract class MultipartMessageDetailBasicCarrierView<T: ChatPostMessage, V: MultipartMessageDetailBasicContentView<T>>: MultipartMessageDetailBasicView<T> {

    lateinit var contentView: V

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_basic, this)
        contentView = newContentView()
        flCarrier.addView(contentView)
    }

    override fun refreshItemView(chatPostMessage: T, position: Int, messageList: List<ChatPostMessage>) {
        super.refreshItemView(chatPostMessage, position, messageList)
        if(contentView is MultipartMessageDetailAnnoImageContentView
                && chatPostMessage is AnnoImageChatMessage) {
            (contentView as MultipartMessageDetailAnnoImageContentView).refreshUI(chatPostMessage, messageList)

        } else {
            contentView.refreshUI(chatPostMessage)

        }
    }

    abstract fun newContentView(): V

    override fun nameView(): TextView = tvName

    override fun avatarView(): ImageView = ivChatLeftAvatar

    override fun timeView(): TextView = tvTime

    override fun bottomLineView(): View = vBottomLine
}