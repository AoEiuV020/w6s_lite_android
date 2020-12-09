package com.foreveross.atwork.modules.workbench.activity.admin

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.modules.workbench.fragment.admin.WorkbenchAdminSetScopeFragment
import com.foreveross.atwork.support.SingleFragmentTransparentActivity

class WorkbenchAdminSetScopeActivity: SingleFragmentTransparentActivity() {

    override fun createFragment(): Fragment = WorkbenchAdminSetScopeFragment()


    companion object {

        fun getIntent(context: Context, currentAdminWorkbenchData: WorkbenchData): Intent {
            val intent = Intent(context, WorkbenchAdminSetScopeActivity::class.java)
            intent.putExtra(WorkbenchData::class.java.toString(), currentAdminWorkbenchData)
            return intent
        }
    }

}