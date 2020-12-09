package com.foreveross.atwork.modules.chat.adapter

import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.modules.chat.component.SearchMessageContentItem
import com.foreveross.atwork.modules.chat.component.SearchResultDateItemView
import com.foreveross.atwork.modules.chat.data.SearchMessageItemData
import com.foreveross.atwork.modules.chat.data.SearchMessageTimeLineList

/**
 *  create by reyzhang22 at 2019-11-12
 */
class SearchMessageContentAdapter(list: SearchMessageTimeLineList<SearchMessageItemData>): BaseQuickAdapter<SearchMessageItemData, MessageItemHolder>(list as MutableList<SearchMessageItemData>) {

    private var keywords = ""

    override fun convert(helper: MessageItemHolder?, item: SearchMessageItemData) {
        if (helper?.getItem() is SearchMessageContentItem) {
            val messageItem = helper?.getItem() as SearchMessageContentItem
            messageItem.setSearchItem(item, keywords)
        } else {
            val timelineItem = helper?.getItem() as SearchResultDateItemView
            timelineItem.setTitle(item.mName)
        }

    }

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): MessageItemHolder {
        return MessageItemHolder(when (viewType) {
            1 -> SearchMessageContentItem(mContext)
            else -> SearchResultDateItemView(mContext)
        })
    }

    override fun getDefItemViewType(position: Int): Int {
        val data = getItem(position)
        if (data!!.mIsTimeLine) {
            return 0
        }
        return 1
    }

    fun setKeywords(keywords: String) {
        this.keywords = keywords
    }

}

class MessageItemHolder(itemView: View): BaseViewHolder(itemView) {

    fun getItem(): View {
        if (itemView is SearchMessageContentItem) {
            return itemView
        }
        return itemView as SearchResultDateItemView
    }

}