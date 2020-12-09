package com.foreveross.atwork.infrastructure.model.workbench.data

import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig
import com.google.gson.annotations.SerializedName

class WorkbenchAdvertisementEachData(

        @SerializedName("total_count")
        val totalCount: Int = 0,

        @SerializedName("records")
        val records: List<AdvertisementConfig> = emptyList()

)