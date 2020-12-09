package com.foreveross.atwork.modules.workbench.activity.admin

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.modules.workbench.fragment.admin.WorkbenchAdminSortCardFragment
import com.foreveross.atwork.support.SingleFragmentTransparentActivity

class WorkbenchAdminSortCardActivity: SingleFragmentTransparentActivity() {

    override fun createFragment(): Fragment = WorkbenchAdminSortCardFragment()


    companion object {

        fun getIntent(context: Context, currentAdminWorkbenchData: WorkbenchData): Intent {
            val intent = Intent(context, WorkbenchAdminSortCardActivity::class.java)
            intent.putExtra(WorkbenchData::class.java.toString(), currentAdminWorkbenchData)
            return intent
        }
    }
}