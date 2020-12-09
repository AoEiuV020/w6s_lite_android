package com.foreveross.atwork.modules.workbench.adapter

import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.modules.workbench.component.WorkbenchAppContainer0ItemView
import com.foreveross.atwork.modules.workbench.model.WorkbenchAppContainerItemDataWrapper

class WorkbenchAppContainer0PerPageAdapter(val dataList: List<WorkbenchAppContainerItemDataWrapper>): BaseQuickAdapter<WorkbenchAppContainerItemDataWrapper, WorkbenchCardAppContainer0PerPageViewHolder>(dataList) {

    var workbenchCard: WorkbenchCard? = null
    var onRemoveItemAppBundleListener: ((position: Int) -> Unit)? = null

    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): WorkbenchCardAppContainer0PerPageViewHolder {
        val cardItemView =  WorkbenchAppContainer0ItemView(mContext)
        val viewHolder = WorkbenchCardAppContainer0PerPageViewHolder(cardItemView)
        cardItemView.ivAppRemoveView.setOnClickListener {
            onRemoveItemAppBundleListener?.invoke(viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun convert(helper: WorkbenchCardAppContainer0PerPageViewHolder, item: WorkbenchAppContainerItemDataWrapper) {
        helper.cardItemView.refreshView(item, workbenchCard)
    }

}

class WorkbenchCardAppContainer0PerPageViewHolder(itemView: View) : BaseViewHolder(itemView) {
    var cardItemView = itemView as WorkbenchAppContainer0ItemView
}