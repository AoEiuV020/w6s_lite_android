package com.foreveross.atwork.modules.workbench.adapter

import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.modules.workbench.component.WorkbenchAppContainer1ItemView
import com.foreveross.atwork.modules.workbench.model.WorkbenchAppContainerItemDataWrapper

class WorkbenchAppContainer1Adapter(val dataList: List<WorkbenchAppContainerItemDataWrapper>) : BaseQuickAdapter<WorkbenchAppContainerItemDataWrapper, WorkbenchAppContainer1ItemHolder>(dataList) {

    var workbenchCard: WorkbenchCard? = null

    var onRemoveItemAppBundleListener: ((position: Int) -> Unit)? = null

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): WorkbenchAppContainer1ItemHolder {
        val cardItemView =  WorkbenchAppContainer1ItemView(mContext)
        val workbenchAppContainer1ItemHolder = WorkbenchAppContainer1ItemHolder(cardItemView)

        cardItemView.ivAppRemoveView.setOnClickListener {
            onRemoveItemAppBundleListener?.invoke(workbenchAppContainer1ItemHolder.adapterPosition)
        }

        return workbenchAppContainer1ItemHolder
    }

    override fun convert(helper: WorkbenchAppContainer1ItemHolder, item: WorkbenchAppContainerItemDataWrapper) {
        helper.cardItemView.refreshView(item, workbenchCard)
    }
}

class WorkbenchAppContainer1ItemHolder(itemView: View) : BaseViewHolder(itemView) {
    var cardItemView = itemView as WorkbenchAppContainer1ItemView
}

