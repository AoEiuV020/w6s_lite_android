package com.foreveross.atwork.modules.search.adapter

import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.modules.search.component.SearchListItemView
import com.foreveross.atwork.modules.search.model.SearchMessageItem

/**
 *  create by reyzhang22 at 2020-02-06
 */
class SessionSearchAdapter(dataList: MutableList<SearchMessageItem>, key: String) : BaseQuickAdapter<SearchMessageItem, SessionSearchViewHolder>(dataList) {

    private var keyword = key
    private var dataList: MutableList<SearchMessageItem>? = null

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): SessionSearchViewHolder {
        var messageItemView = SearchListItemView(mContext)

        return SessionSearchViewHolder(messageItemView)
    }


    override fun convert(helper: SessionSearchViewHolder, item: SearchMessageItem) {
        helper.messageItemView.refreshView(item, keyword)
    }

    fun setData(dataList: MutableList<SearchMessageItem>, keyword: String) {
        this.keyword = keyword
        this.dataList = dataList
        notifyDataSetChanged()
    }


}



class SessionSearchViewHolder(itemView: View) : BaseViewHolder(itemView) {
    var messageItemView: SearchListItemView = itemView as SearchListItemView
}