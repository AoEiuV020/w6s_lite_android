package com.foreveross.atwork.modules.workbench.activity.admin

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.modules.workbench.fragment.admin.WorkbenchAdminAddCardFragment
import com.foreveross.atwork.support.SingleFragmentTransparentActivity

class WorkbenchAdminAddCardActivity: SingleFragmentTransparentActivity() {

    override fun createFragment(): Fragment = WorkbenchAdminAddCardFragment()

    companion object {

        fun getIntent(context: Context, currentAdminWorkbenchData: WorkbenchData): Intent {
            val intent = Intent(context, WorkbenchAdminAddCardActivity::class.java)
            intent.putExtra(WorkbenchData::class.java.toString(), currentAdminWorkbenchData)
            return intent
        }
    }
}