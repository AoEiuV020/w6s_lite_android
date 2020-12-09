package com.foreveross.atwork.modules.chat.component.history

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage
import kotlinx.android.synthetic.main.item_message_hsitory_text_view.view.*

class TextHistoryItemView: BasicHistoryItemView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.item_message_hsitory_text_view, this)

    }

    override fun getTimeView(): TextView {
        return tvTime

    }


    override fun refreshView(message: ChatPostMessage) {
        super.refreshView(message)

        if(message is TextChatMessage) {
            tvContent.text = message.text
        }
    }


    companion object {
        const val VIEW_TYPE_TEXT = 5
    }

}