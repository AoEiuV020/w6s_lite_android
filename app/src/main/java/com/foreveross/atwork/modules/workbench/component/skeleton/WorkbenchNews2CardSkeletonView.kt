package com.foreveross.atwork.modules.workbench.component.skeleton

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.foreveross.atwork.R
import kotlinx.android.synthetic.main.component_workbench_news2_card_skeleton.view.*

class WorkbenchNews2CardSkeletonView : WorkbenchBasicSkeletonView {


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun findViews() {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_news2_card_skeleton, this)

    }


    override fun cardRefreshView(): View? = tvCardRefresh


    override fun cardSkeletonView(): View? = rlSkeleton


    override fun cardEmptyView(): View? = tvNoData
}