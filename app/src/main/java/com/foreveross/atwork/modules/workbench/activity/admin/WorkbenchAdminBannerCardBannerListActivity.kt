package com.foreveross.atwork.modules.workbench.activity.admin

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.model.advertisement.AdminAdvertisementConfig
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.modules.workbench.fragment.admin.WorkbenchAdminBannerCardBannerListFragment
import com.foreveross.atwork.support.SingleFragmentActivity

class WorkbenchAdminBannerCardBannerListActivity: SingleFragmentActivity() {

    override fun createFragment(): Fragment = WorkbenchAdminBannerCardBannerListFragment()

    companion object {

        const val ACTION_REFRESH_BANNER_LIST = "ACTION_REFRESH_BANNER_LIST"
        const val ACTION_DELETE_BANNER_ITEM = "ACTION_DELETE_BANNER_ITEM"

        fun refreshBannerList(adminAdvertisementConfig: AdminAdvertisementConfig? = null) {
            val intent = Intent(ACTION_REFRESH_BANNER_LIST)
            intent.putExtra(AdminAdvertisementConfig::class.java.toString(), adminAdvertisementConfig)
            LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).sendBroadcast(intent)
        }

        fun deleteBannerItem(adminAdvertisementConfig: AdminAdvertisementConfig) {
            val intent = Intent(ACTION_DELETE_BANNER_ITEM)
            intent.putExtra(AdminAdvertisementConfig::class.java.toString(), adminAdvertisementConfig)
            LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).sendBroadcast(intent)
        }

        fun getIntent(context: Context, currentAdminWorkbenchData: WorkbenchData, workbenchCardDetailData: WorkbenchCardDetailData): Intent {
            val intent = Intent(context, WorkbenchAdminBannerCardBannerListActivity::class.java)
            intent.putExtra(WorkbenchCardDetailData::class.java.toString(), workbenchCardDetailData)
            intent.putExtra(WorkbenchData::class.java.toString(), currentAdminWorkbenchData)
            return intent
        }
    }
}