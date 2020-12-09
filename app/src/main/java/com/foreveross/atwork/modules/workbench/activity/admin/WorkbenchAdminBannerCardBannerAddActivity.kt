package com.foreveross.atwork.modules.workbench.activity.admin

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.modules.workbench.fragment.admin.WorkbenchAdminBannerCardBannerAddFragment
import com.foreveross.atwork.support.SingleFragmentTransparentActivity

class WorkbenchAdminBannerCardBannerAddActivity: SingleFragmentTransparentActivity() {

    override fun createFragment(): Fragment = WorkbenchAdminBannerCardBannerAddFragment()


    companion object {
        fun getIntent(context: Context, currentAdminWorkbenchData: WorkbenchData, workbenchCardDetailData: WorkbenchCardDetailData): Intent {
            val intent = Intent(context, WorkbenchAdminBannerCardBannerAddActivity::class.java)
            intent.putExtra(WorkbenchCardDetailData::class.java.toString(), workbenchCardDetailData)
            intent.putExtra(WorkbenchData::class.java.toString(), currentAdminWorkbenchData)
            return intent
        }
    }
}