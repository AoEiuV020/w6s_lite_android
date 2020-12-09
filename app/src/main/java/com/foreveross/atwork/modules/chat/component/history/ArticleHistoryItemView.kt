package com.foreveross.atwork.modules.chat.component.history

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleChatMessage
import com.foreveross.atwork.modules.chat.util.ArticleItemHelper
import com.foreveross.atwork.utils.ImageCacheHelper
import com.nostra13.universalimageloader.core.DisplayImageOptions
import kotlinx.android.synthetic.main.item_message_hsitory_article_view.view.*

class ArticleHistoryItemView: BasicHistoryItemView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun getTimeView(): TextView {
        return tvTime

    }

    override fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.item_message_hsitory_article_view, this)
    }


    override fun refreshView(message: ChatPostMessage) {
        super.refreshView(message)

        if(message is ArticleChatMessage) {
            tvContent.text = message.articles[0].title
            ImageCacheHelper.displayImage(ArticleItemHelper.getCoverUrl(message.articles[0]), ivCover, getDisplayImageOptions())
        }
    }

    private fun getDisplayImageOptions(): DisplayImageOptions {
        val builder = DisplayImageOptions.Builder()
        builder.cacheOnDisk(true)
        builder.cacheInMemory(true)
        builder.showImageOnLoading(R.mipmap.loading_history_article_bg)
        builder.showImageForEmptyUri(R.mipmap.loading_history_article_bg)
        builder.showImageOnFail(R.mipmap.loading_history_article_bg)
        return builder.build()
    }

    companion object {
        const val VIEW_TYPE_ARTICLE = 0
    }

}