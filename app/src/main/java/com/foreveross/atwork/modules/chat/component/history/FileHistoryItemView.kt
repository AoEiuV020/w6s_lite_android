package com.foreveross.atwork.modules.chat.component.history

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.modules.file.util.FileMediaTypeUtil
import kotlinx.android.synthetic.main.item_message_hsitory_file_view.view.*

class FileHistoryItemView: BasicHistoryItemView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.item_message_hsitory_file_view, this)
    }


    override fun getTimeView(): TextView {
        return tvTime

    }


    override fun refreshView(message: ChatPostMessage) {
        super.refreshView(message)

        if(message is FileTransferChatMessage) {
            ivCover.setImageResource(FileMediaTypeUtil.getFileTypeIcon(message))
            tvTitle.text = message.name
        }
    }


    companion object {
        const val VIEW_TYPE_FILE = 1
    }

}