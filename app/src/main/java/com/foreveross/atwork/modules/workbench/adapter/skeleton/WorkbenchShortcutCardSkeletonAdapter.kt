package com.foreveross.atwork.modules.workbench.adapter.skeleton

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType

class WorkbenchShortcutCardSkeletonAdapter: RecyclerView.Adapter<WorkbenchShortcutCardSkeletonAdapter.WorkbenchShortcutCardSkeletonItemViewHolder>() {


    var skeletonItemCount = 0

    var type: WorkbenchCardType = WorkbenchCardType.SHORTCUT_0



    override fun getItemCount(): Int = skeletonItemCount



    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): WorkbenchShortcutCardSkeletonItemViewHolder {
        return WorkbenchShortcutCardSkeletonItemViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.component_workbench_item_shortcut_card_skeleton, viewGroup, false))
    }

    override fun onBindViewHolder(holder: WorkbenchShortcutCardSkeletonItemViewHolder, position: Int) {
        if(WorkbenchCardType.SHORTCUT_0 == type) {
            holder.tvCardItemTitleHolder.isVisible = false
            return
        }

        holder.tvCardItemTitleHolder.isVisible = true

    }



    class WorkbenchShortcutCardSkeletonItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCardItemTitleHolder = itemView.findViewById<TextView>(R.id.tvCardItemTitle)
    }


}