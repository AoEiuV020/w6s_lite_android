package com.foreveross.atwork.modules.workbench.adapter.admin

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.component.recyclerview.BaseQuickAdapter
import com.foreveross.atwork.component.recyclerview.BaseViewHolder
import com.foreveross.atwork.infrastructure.model.orgization.Scope
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.extension.asType

class WorkbenchScopeSelectedListAdapter(dataList: List<Scope>): BaseQuickAdapter<Scope, WorkbenchScopeSelectedListItemViewHolder>(R.layout.component_workbench_item_scope_selected, dataList) {

    override fun convert(helper: WorkbenchScopeSelectedListItemViewHolder, item: Scope) {
        helper.tvName.text = item.name
    }
}


class WorkbenchScopeSelectedListItemViewHolder(itemView: View) : BaseViewHolder(itemView) {

    val tvName: TextView = itemView.findViewById(R.id.tvName)
    val tvCancel: ImageView = itemView.findViewById(R.id.ivCancel)
    val llRoot: LinearLayout = itemView.findViewById(R.id.llRoot)

    init {
        llRoot.background
                .asType<GradientDrawable>()
                ?.run {
                    mutate()
                    setColor((Color.parseColor("#DFF1FF")))
                    setStroke(DensityUtil.dip2px(1F), Color.parseColor("#DFF1FF"))
                }

    }

}

class WorkbenchScopeSelectedSpaceItemDecoration() : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {


        val divider = DensityUtil.dip2px(4.6f)
        outRect.right = divider
        outRect.bottom = divider


    }

}

