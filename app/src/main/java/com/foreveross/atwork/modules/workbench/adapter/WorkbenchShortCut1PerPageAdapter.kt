package com.foreveross.atwork.modules.workbench.adapter

import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchShortcutCardItem
import com.foreveross.atwork.modules.workbench.component.WorkbenchShortcutCardItemView

class WorkbenchShortCut1PerPageAdapter(dataList: List<WorkbenchShortcutCardItem>): BaseQuickAdapter<WorkbenchShortcutCardItem, WorkbenchCardShortCut1PerPageViewHolder>(dataList) {


    override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): WorkbenchCardShortCut1PerPageViewHolder {
        val cardItemView =  WorkbenchShortcutCardItemView(mContext)
        return WorkbenchCardShortCut1PerPageViewHolder(cardItemView)
    }

    override fun convert(helper: WorkbenchCardShortCut1PerPageViewHolder, item: WorkbenchShortcutCardItem) {
        helper.cardItemView.refresh(item)
    }

}

class WorkbenchCardShortCut1PerPageViewHolder(itemView: View) : BaseViewHolder(itemView) {
    var cardItemView = itemView as WorkbenchShortcutCardItemView
}