package com.foreveross.atwork.modules.dropbox.adapter

import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.dropbox.ShareItem
import com.foreveross.atwork.modules.dropbox.component.DropboxShareItem
import com.foreveross.atwork.modules.dropbox.component.DropboxTimelineItemView
import com.foreveross.atwork.modules.dropbox.route.ShareTimeLineList

/**
 *  create by reyzhang22 at 2019-11-12
 */
class DropboxShareItemAdapter(list: ShareTimeLineList<ShareItem>): BaseQuickAdapter<ShareItem, ShareItemHolder>(list as MutableList<ShareItem>) {
    override fun convert(helper: ShareItemHolder?, item: ShareItem) {
        if (helper?.getItem() is DropboxShareItem) {
            val dropboxShareItem = helper.getItem() as DropboxShareItem
            dropboxShareItem.setShareItem(item)
        } else {
            val timelineItem = helper?.getItem() as DropboxTimelineItemView
            timelineItem.setTimeLine(item.mName)
        }

    }

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): ShareItemHolder {
        var view: View?
        when (viewType) {
            1 -> view = DropboxShareItem(mContext)
            else -> view = DropboxTimelineItemView(mContext)
        }
        return ShareItemHolder(view)
    }

    override fun getDefItemViewType(position: Int): Int {
        val data = getItem(position)
        if (data!!.mIsTimeLine) {
            return 0
        }
        return 1
    }

}

class ShareItemHolder(itemView: View): BaseViewHolder(itemView) {

    fun getItem(): View {
        if (itemView is DropboxShareItem) {
            return itemView
        }
        return itemView as DropboxTimelineItemView
    }

}