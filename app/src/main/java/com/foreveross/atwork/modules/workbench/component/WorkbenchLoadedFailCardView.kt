package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import kotlinx.android.synthetic.main.component_workbench_load_fail_card.view.*

class WorkbenchLoadedFailCardView: LinearLayout {

    var workbenchCardRefreshView: IWorkbenchCardRefreshView<out WorkbenchCard>? = null

    constructor(context: Context?) : super(context) {
        findViews()
        registerListener()
    }


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
        registerListener()
    }

    private fun findViews() {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_load_fail_card, this)

    }


    private fun registerListener() {
        tvCardRefresh.setOnClickListener {
            workbenchCardRefreshView?.let { it.refreshContentRemote(true) }
        }
    }
}