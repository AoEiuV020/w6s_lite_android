package com.foreveross.atwork.modules.workbench.activity.admin

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.modules.workbench.fragment.admin.WorkbenchAdminModifyAppEntryFragment
import com.foreveross.atwork.support.SingleFragmentTransparentActivity

class WorkbenchAdminModifyAppEntryActivity: SingleFragmentTransparentActivity() {

    override fun createFragment(): Fragment = WorkbenchAdminModifyAppEntryFragment()

    companion object {

        const val ACTION_REFRESH_TOTALLY = "ACTION_REFRESH_TOTALLY"

        @JvmStatic
        fun refreshAppBundleListTotally() {
            val intent = Intent(ACTION_REFRESH_TOTALLY)
            LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).sendBroadcast(intent)
        }

        fun getIntent(context: Context, workbenchCardDetailData: WorkbenchCardDetailData): Intent {
            val intent = Intent(context, WorkbenchAdminModifyAppEntryActivity::class.java)
            intent.putExtra(WorkbenchCardDetailData::class.java.toString(), workbenchCardDetailData)
            return intent
        }
    }
}