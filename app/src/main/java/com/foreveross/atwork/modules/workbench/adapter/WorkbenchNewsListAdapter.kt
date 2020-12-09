package com.foreveross.atwork.modules.workbench.adapter

import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListItem
import com.foreveross.atwork.modules.workbench.component.WorkbenchNewsCardListItemView

class WorkbenchNewsListAdapter(var type: WorkbenchCardType, dataList: List<WorkbenchListItem>) : BaseQuickAdapter<WorkbenchListItem, WorkbenchNewsListCardItemViewHolder>(dataList) {

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): WorkbenchNewsListCardItemViewHolder {
        val cardItemView =  WorkbenchNewsCardListItemView(mContext)
        return WorkbenchNewsListCardItemViewHolder(cardItemView)
    }

    override fun convert(helper: WorkbenchNewsListCardItemViewHolder, item: WorkbenchListItem) {
        helper.cardItemView.refreshViews(helper.realPosition, item, type)
    }
}

class WorkbenchNewsListCardItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
    var cardItemView = itemView as WorkbenchNewsCardListItemView
}