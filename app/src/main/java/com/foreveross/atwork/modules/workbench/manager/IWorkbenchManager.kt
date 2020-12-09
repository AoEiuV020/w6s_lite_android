package com.foreveross.atwork.modules.workbench.manager

import android.content.Context
import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.model.workbench.Workbench
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchContentEventType
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.modules.workbench.model.WorkbenchFilterCardsResult


const val REQUEST_INTERVAL = 30 * 60 * 1000

interface IWorkbenchManager {

    fun clear()

    fun clearRequestRecord()

    fun getCurrentOrgWorkbenchWithoutContent(): Workbench?

    fun initCurrentOrgWorkbench()

    fun getCurrentOrgWorkbench(onGetWorkbench: ((Workbench?) -> Unit))

    fun getOrgWorkbench(workbenchData: WorkbenchData, onGetWorkbench: ((Workbench?) -> Unit))

    fun isCurrentOrgWorkbenchLegal(): Boolean

    fun checkWorkbenchRemote(requestImmediately: Boolean = false, requestFinish: (() -> Unit)? = null)

    fun notifyRefresh()

    fun notifySwitchAdminPreviewWorkbench()

    fun route(context: Context, card: WorkbenchCard?, eventType: WorkbenchContentEventType, eventValue: String)


    fun filterDisplayAndSored(workbench: Workbench): WorkbenchFilterCardsResult

    fun handleClickAction(card: WorkbenchCard?, clickAction: (() -> Unit))

    fun getAppBundlesShouldDisplay(appContainer: List<AppBundles>): ArrayList<AppBundles>


    companion object {
        const val ACTION_REFRESH_WORKBENCH = "ACTION_REFRESH_WORKBENCH"

        const val ACTION_REFRESH_ADMIN_PREVIEW_WORKBENCH = "ACTION_REFRESH_ADMIN_PREVIEW_WORKBENCH"


    }
}