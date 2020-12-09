package com.foreveross.atwork.modules.workbench.activity.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.modules.workbench.fragment.admin.WorkbenchAdminAddOrModifyFragment
import com.foreveross.atwork.support.SingleFragmentTransparentActivity

class WorkbenchAdminAddOrModifyActivity: SingleFragmentTransparentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun createFragment(): Fragment {
        return WorkbenchAdminAddOrModifyFragment()
    }


    companion object {

        const val MODE_ADD = 0
        const val MODE_MODIFY = 1

        const val DATA_MODE = "DATA_MODE"


        fun getIntent(context: Context, mode: Int = MODE_ADD, currentAdminWorkbenchData: WorkbenchData? = null): Intent {
            val intent = Intent(context, WorkbenchAdminAddOrModifyActivity::class.java)
            intent.putExtra(DATA_MODE, mode)
            currentAdminWorkbenchData?.let {
                intent.putExtra(WorkbenchData::class.java.toString(), currentAdminWorkbenchData)

            }
            return intent
        }
    }

}