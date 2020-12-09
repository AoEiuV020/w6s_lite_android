package com.foreveross.atwork.modules.chat.component.history

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage
import com.foreveross.atwork.utils.ImageChatHelper
import kotlinx.android.synthetic.main.item_message_hsitory_image_view.view.*

class ImageHistoryItemView: BasicHistoryItemView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.item_message_hsitory_image_view, this)

    }

    override fun getTimeView(): TextView {
        return tvTime

    }


    override fun refreshView(message: ChatPostMessage) {
        super.refreshView(message)

        if(message is ImageChatMessage) {
            ImageChatHelper.initImageContent(message, ivImage)
        }
    }


    companion object {
        const val VIEW_TYPE_IMAGE = 3
    }

}