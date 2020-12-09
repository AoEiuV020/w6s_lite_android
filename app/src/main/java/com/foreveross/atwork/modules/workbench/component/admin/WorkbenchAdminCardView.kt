package com.foreveross.atwork.modules.workbench.component.admin

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchAdminCard
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.modules.app.activity.WebViewActivity
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminAddOrModifyActivity
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminAdminActivity
import com.foreveross.atwork.modules.workbench.activity.admin.WorkbenchAdminSwitchActivity
import com.foreveross.atwork.modules.workbench.component.IWorkbenchCardRefreshView
import com.foreveross.atwork.utils.startActivityByNoAnimation
import kotlinx.android.synthetic.main.component_workbench_admin.view.*

class WorkbenchAdminCardView: RelativeLayout, IWorkbenchCardRefreshView<WorkbenchAdminCard> {

    override lateinit var workbenchCard: WorkbenchAdminCard

    constructor(context: Context) : super(context) {
        findViews(context)
        registerListener()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews(context)
        registerListener()
    }

     private fun findViews(context: Context) {
         LayoutInflater.from(context).inflate(R.layout.component_workbench_admin, this)
     }

    private fun registerListener() {

        llAdminOrg.setOnClickListener {
            val url = String.format(UrlConstantManager.getInstance().orgManagerUrl, PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext))
            val webViewControlAction = WebViewControlAction.newAction().setUrl(url).setNeedShare(false)
            val intent = WebViewActivity.getIntent(context, webViewControlAction)
            context.startActivity(intent)
        }

        llAdminAddWorkbench.setOnClickListener {
            context.startActivityByNoAnimation(WorkbenchAdminAddOrModifyActivity.getIntent(context))
        }

        llAdminSwitchWorkbench.setOnClickListener {
            context.startActivityByNoAnimation(WorkbenchAdminSwitchActivity.getIntent(context))
        }

        llAdminAdminWorkbench.setOnClickListener {
            context.startActivity(WorkbenchAdminAdminActivity.getIntent(context))
        }

    }

    override fun refresh(workbenchCard: WorkbenchAdminCard) {

    }

    override fun refreshView(workbenchCard: WorkbenchAdminCard) {
    }

    override fun getViewType(): Int = WorkbenchCardType.ADMIN.hashCode()
}