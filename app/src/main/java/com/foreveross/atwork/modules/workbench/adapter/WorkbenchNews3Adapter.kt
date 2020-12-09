package com.foreveross.atwork.modules.workbench.adapter

import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListItem
import com.foreveross.atwork.modules.workbench.component.WorkbenchNews3CardSliderItemView

class WorkbenchNews3Adapter(dataList: List<WorkbenchListItem>) : BaseQuickAdapter<WorkbenchListItem, WorkbenchNews3CardItemViewHolder>(dataList) {

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): WorkbenchNews3CardItemViewHolder {
        val cardItemView = WorkbenchNews3CardSliderItemView(mContext)
        return WorkbenchNews3CardItemViewHolder(cardItemView)
    }

    override fun convert(helper: WorkbenchNews3CardItemViewHolder, item: WorkbenchListItem) {
        helper.cardItemView.refreshViews(item)
    }
}

class WorkbenchNews3CardItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
    var cardItemView = itemView as WorkbenchNews3CardSliderItemView
}