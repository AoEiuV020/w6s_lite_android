package com.foreveross.atwork.modules.workbench.model

import com.foreveross.atwork.infrastructure.model.advertisement.AdminAdvertisementConfig

class WorkbenchAdminBannerCardBannerItemDataWrapper (
        var advertisementConfig: AdminAdvertisementConfig? = null

) {
    fun isItemDisplay(): Boolean = null != advertisementConfig
}