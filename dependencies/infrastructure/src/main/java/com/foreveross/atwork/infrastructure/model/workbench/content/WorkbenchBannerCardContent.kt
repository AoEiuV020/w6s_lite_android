package com.foreveross.atwork.infrastructure.model.workbench.content

import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig
import com.foreveross.atwork.infrastructure.utils.ListUtil

class WorkbenchBannerCardContent(

        override var widgetsId: Long,

        var records: ArrayList<AdvertisementConfig> = arrayListOf()

) : IWorkbenchCardContent {

    override fun isContentDataEmpty(): Boolean = ListUtil.isEmpty(records)
}