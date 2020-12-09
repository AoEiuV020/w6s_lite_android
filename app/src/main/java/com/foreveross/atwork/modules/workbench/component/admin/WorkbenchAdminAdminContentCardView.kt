package com.foreveross.atwork.modules.workbench.component.admin

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.modules.workbench.adapter.WorkbenchCardAdapter
import com.foreveross.atwork.modules.workbench.component.IWorkbenchCardRefreshView
import kotlinx.android.synthetic.main.component_workbench_admin_admin_content_card.view.*

class WorkbenchAdminAdminContentCardView: RelativeLayout {

    lateinit var cardView: IWorkbenchCardRefreshView<out WorkbenchCard>
    lateinit var ivAdminAction: ImageView

    constructor(context: Context, viewType: Int) : super(context) {
        findViews(context)

        cardView = WorkbenchCardAdapter.produceWorkbenchCardRefreshView(context, viewType)
        flContent.addView(cardView as View)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews(context)
    }

    private fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.component_workbench_admin_admin_content_card, this)
        ivAdminAction = view.findViewById(R.id.ivAdminAction)
    }

}