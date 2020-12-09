package com.foreveross.atwork.modules.workbench.activity.admin

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.infrastructure.model.advertisement.AdminAdvertisementConfig
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.modules.workbench.fragment.admin.WorkbenchAdminBannerCardBannerDetailFragment
import com.foreveross.atwork.support.SingleFragmentTransparentActivity

class WorkbenchAdminBannerCardBannerDetailActivity: SingleFragmentTransparentActivity() {

    override fun createFragment(): Fragment = WorkbenchAdminBannerCardBannerDetailFragment()


    companion object {
        fun getIntent(context: Context,  workbenchData: WorkbenchData, workbenchCardDetailData: WorkbenchCardDetailData, adminAdvertisementConfig: AdminAdvertisementConfig): Intent {
            val intent = Intent(context, WorkbenchAdminBannerCardBannerDetailActivity::class.java)
            intent.putExtra(WorkbenchData::class.java.toString(), workbenchData)
            intent.putExtra(WorkbenchCardDetailData::class.java.toString(), workbenchCardDetailData)
            intent.putExtra(AdminAdvertisementConfig::class.java.toString(), adminAdvertisementConfig)
            return intent
        }
    }
}