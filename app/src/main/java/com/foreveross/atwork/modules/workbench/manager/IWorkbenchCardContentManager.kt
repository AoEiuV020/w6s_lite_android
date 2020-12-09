package com.foreveross.atwork.modules.workbench.manager

import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListContent
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchShortcutCardContent

interface IWorkbenchCardContentManager {

    fun clearRequestRecord()

    fun removeRequestRecord(card: WorkbenchCard)

    fun checkBannerMediaDownloadSilently(records: ArrayList<AdvertisementConfig>)


    fun requestShortcutCardContent(card: WorkbenchCard, immediate: Boolean = false, onGetResult: (Long, WorkbenchShortcutCardContent?) -> Unit): Boolean


    fun requestListContent(card: WorkbenchCard, immediate: Boolean = false, onGetResult: (Long, WorkbenchListContent?) -> Unit): Boolean

    companion object {
        const val ACTION_REFRESH_WORKBENCH_BANNER_ADVERTISEMENTS = "ACTION_REFRESH_WORKBENCH_BANNER_ADVERTISEMENTS"
        const val DATA_ADVERTISEMENTS = "DATA_ADVERTISEMENTS"
    }
}