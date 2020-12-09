package com.foreveross.atwork.modules.workbench.activity.admin

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.modules.workbench.fragment.admin.WorkbenchAdminModifyCardFragment
import com.foreveross.atwork.support.SingleFragmentTransparentActivity

class WorkbenchAdminModifyCardActivity: SingleFragmentTransparentActivity() {

    override fun createFragment(): Fragment = WorkbenchAdminModifyCardFragment()

    companion object {

        fun getIntent(context: Context, currentAdminWorkbenchData: WorkbenchData, workbenchCardDetailData: WorkbenchCardDetailData): Intent {
            val intent = Intent(context, WorkbenchAdminModifyCardActivity::class.java)
            intent.putExtra(WorkbenchCardDetailData::class.java.toString(), workbenchCardDetailData)
            intent.putExtra(WorkbenchData::class.java.toString(), currentAdminWorkbenchData)
            return intent
        }
    }
}