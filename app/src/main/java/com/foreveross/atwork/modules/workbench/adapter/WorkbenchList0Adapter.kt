package com.foreveross.atwork.modules.workbench.adapter

import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListItem
import com.foreveross.atwork.modules.workbench.component.WorkbenchListCard0ItemView

class WorkbenchList0Adapter(dataList: List<WorkbenchListItem>) : BaseQuickAdapter<WorkbenchListItem, WorkbenchListCard0ItemViewHolder>(dataList) {

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): WorkbenchListCard0ItemViewHolder {
        val cardItemView =  WorkbenchListCard0ItemView(mContext)
        return WorkbenchListCard0ItemViewHolder(cardItemView)
    }

    override fun convert(helper: WorkbenchListCard0ItemViewHolder, item: WorkbenchListItem) {
        helper.cardItemView.refreshView(item)
    }
}

class WorkbenchListCard0ItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
    var cardItemView = itemView as WorkbenchListCard0ItemView
}