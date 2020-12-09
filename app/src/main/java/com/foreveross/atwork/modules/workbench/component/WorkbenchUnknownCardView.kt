package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType

class WorkbenchUnknownCardView: WorkbenchBasicCardView<WorkbenchCard> {


    override lateinit var workbenchCard: WorkbenchCard

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_unknown_card, this)
    }



    override fun refresh(workbenchCard: WorkbenchCard) {
        this.workbenchCard = workbenchCard
    }

    override fun refreshView(workbenchCard: WorkbenchCard) {
    }

    override fun getViewType(): Int = WorkbenchCardType.UNKNOWN.hashCode()
}