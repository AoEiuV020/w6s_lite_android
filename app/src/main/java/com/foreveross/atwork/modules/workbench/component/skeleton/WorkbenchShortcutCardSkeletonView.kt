package com.foreveross.atwork.modules.workbench.component.skeleton

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.modules.workbench.adapter.skeleton.WorkbenchShortcutCardSkeletonAdapter
import kotlinx.android.synthetic.main.component_workbench_shortcut_card_skeleton.view.*

class WorkbenchShortcutCardSkeletonView : WorkbenchBasicSkeletonView {

    private lateinit var adapter: WorkbenchShortcutCardSkeletonAdapter

    constructor(context: Context) : super(context) {
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViews()
    }


    override fun findViews() {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_shortcut_card_skeleton, this)

    }

    private fun initViews() {
        adapter = WorkbenchShortcutCardSkeletonAdapter()
        rvShortcutSkeleton.adapter = adapter
    }

    fun refresh(type: WorkbenchCardType, count: Int) {
        val layoutManager = if (WorkbenchCardType.SHORTCUT_0 == type) {
            GridLayoutManager(context, count)
        } else {
            GridLayoutManager(context, 4)

        }

        rvShortcutSkeleton.layoutManager = layoutManager
        adapter.skeletonItemCount = count
        adapter.type = type
        adapter.notifyDataSetChanged()


    }

    override fun cardRefreshView(): View? = tvCardRefresh

    override fun cardEmptyView(): View?  = tvNoData

    override fun cardSkeletonView(): View? = flSkeleton

}