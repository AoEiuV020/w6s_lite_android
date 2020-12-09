package com.foreveross.atwork.modules.chat.adapter

import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.*
import com.foreveross.atwork.modules.chat.component.history.*

class MessageHistoryAdapter(dataList: MutableList<ChatPostMessage>) : BaseQuickAdapter<ChatPostMessage, MessageHistoryItemViewHolder>(dataList) {


    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): MessageHistoryItemViewHolder {
        var messageItemView = when (viewType) {

            ArticleHistoryItemView.VIEW_TYPE_ARTICLE -> ArticleHistoryItemView(mContext)
            FileHistoryItemView.VIEW_TYPE_FILE -> FileHistoryItemView(mContext)
            GifHistoryItemView.VIEW_TYPE_GIF -> GifHistoryItemView(mContext)
            ImageHistoryItemView.VIEW_TYPE_IMAGE -> ImageHistoryItemView(mContext)
            MicroVideoHistoryItemView.VIEW_TYPE_MICRO_VIDEO -> MicroVideoHistoryItemView(mContext)
            VoiceHistoryItemView.VIEW_TYPE_VOICE -> VoiceHistoryItemView(mContext)
            TextHistoryItemView.VIEW_TYPE_TEXT -> TextHistoryItemView(mContext)
            else -> TextHistoryItemView(mContext)
        }

        return MessageHistoryItemViewHolder(messageItemView)
    }

    override fun getDefItemViewType(position: Int): Int {
        val msg = getItem(position)
        return when(msg) {
            is ArticleChatMessage -> ArticleHistoryItemView.VIEW_TYPE_ARTICLE
            is FileTransferChatMessage -> FileHistoryItemView.VIEW_TYPE_FILE
            is ImageChatMessage ->  {
                if(msg.isGif) {
                    GifHistoryItemView.VIEW_TYPE_GIF
                } else {
                    ImageHistoryItemView.VIEW_TYPE_IMAGE
                }
            }

            is MicroVideoChatMessage -> MicroVideoHistoryItemView.VIEW_TYPE_MICRO_VIDEO
            is VoiceChatMessage -> VoiceHistoryItemView.VIEW_TYPE_VOICE
            is TextChatMessage -> TextHistoryItemView.VIEW_TYPE_TEXT
            else -> -1
        }
    }

    override fun convert(helper: MessageHistoryItemViewHolder, item: ChatPostMessage) {
        helper.messageItemView.refreshView(item)
    }



}



class MessageHistoryItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
    var messageItemView: BasicHistoryItemView = itemView as BasicHistoryItemView
}




