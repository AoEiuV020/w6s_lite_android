package com.foreveross.atwork.modules.workbench.activity.admin

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.modules.workbench.fragment.admin.WorkbenchAdminAdminFragment
import com.foreveross.atwork.modules.workbench.manager.IWorkbenchAdminManager
import com.foreveross.atwork.support.SingleFragmentActivity

class WorkbenchAdminAdminActivity: SingleFragmentActivity() {

    override fun createFragment(): Fragment = WorkbenchAdminAdminFragment()

    companion object {



        fun refreshCardListTotally() {
            val intent = Intent(IWorkbenchAdminManager.ACTION_REFRESH_CARD_LIST_TOTALLY)
            LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).sendBroadcast(intent)
        }

        fun refreshCard(workbenchCardDetailData: WorkbenchCardDetailData) {
            val intent = Intent(IWorkbenchAdminManager.ACTION_REFRESH_UPDATE_CARD)
            intent.putExtra(IWorkbenchAdminManager.DATA_CARD_DATA, workbenchCardDetailData)
            LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).sendBroadcast(intent)
        }

        fun getIntent(context: Context): Intent {
            val intent = Intent(context, WorkbenchAdminAdminActivity::class.java)
            return intent
        }
    }
}