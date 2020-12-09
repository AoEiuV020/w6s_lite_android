package com.foreveross.atwork.modules.chat.adapter

import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.R
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.file.MediaItem
import com.foreveross.atwork.modules.chat.component.SearchMediasItemView
import com.foreveross.atwork.modules.chat.component.SearchMediasItemView.MediaItemParams
import com.foreveross.atwork.modules.chat.component.SearchResultDateItemView
import com.foreveross.atwork.modules.chat.data.SearchMediasTimeLineList
import com.foreveross.atwork.modules.chat.data.SearchMessageItemData

/**
 *  create by reyzhang22 at 2019-11-12
 */
class SearchChatMediasAdapter(list: SearchMediasTimeLineList<SearchMessageItemData>): BaseQuickAdapter<SearchMessageItemData, MediaItemHolder>(list as MutableList<SearchMessageItemData>) {
    private var mediaList = list
    private val selectedMap = mutableMapOf<String, MediaItem>()
    private var selectMode = false

    override fun convert(helper: MediaItemHolder?, item: SearchMessageItemData) {
        if (helper?.getItem() is SearchMediasItemView) {
            mediaList.mediaMap[item.mName]?.let {
                (helper.getItem() as SearchMediasItemView).setMediaItems(MediaItemParams(it, selectedMap, selectMode))
            }
            return
        }
        (helper?.getItem() as SearchResultDateItemView).apply {
            setTitle(item.mName)
            setTextColor(R.color.white)
            hideDiverLine()
            setTvBackground(R.drawable.shape_search_item_title_bg2)
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): MediaItemHolder {
        return MediaItemHolder(when (viewType) {
            1 -> SearchMediasItemView(mContext)
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


    fun getSelectedMap(): MutableMap<String, MediaItem> {
        return selectedMap
    }

    fun setSelectMode(selectMode: Boolean) {
        this.selectMode = selectMode
    }


    fun clearData() {
        selectedMap.clear()
        notifyDataSetChanged()
    }

}

class MediaItemHolder(itemView: View): BaseViewHolder(itemView) {

    fun getItem(): View {
        if (itemView is SearchMediasItemView) {
            return itemView
        }
        return itemView as SearchResultDateItemView
    }

}