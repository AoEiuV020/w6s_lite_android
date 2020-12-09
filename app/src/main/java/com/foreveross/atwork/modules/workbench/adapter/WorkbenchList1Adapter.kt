package com.foreveross.atwork.modules.workbench.adapter

import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListItem
import com.foreveross.atwork.modules.workbench.component.WorkbenchListCard1ItemView

class WorkbenchList1Adapter(dataList: List<WorkbenchListItem>) : BaseQuickAdapter<WorkbenchListItem, WorkbenchListCard1ItemViewHolder>(dataList) {

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): WorkbenchListCard1ItemViewHolder {
        val cardItemView =  WorkbenchListCard1ItemView(mContext)
        return WorkbenchListCard1ItemViewHolder(cardItemView)
    }

    override fun convert(helper: WorkbenchListCard1ItemViewHolder, item: WorkbenchListItem) {
        helper.cardItemView.refreshView(item)
    }
}

class WorkbenchListCard1ItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
    var cardItemView = itemView as WorkbenchListCard1ItemView
}