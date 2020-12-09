package com.foreveross.atwork.api.sdk.advertisement.model.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig
import com.google.gson.annotations.SerializedName

class GetAdvertisesListResponse : BasicResponseJSON(){

    @SerializedName("result")
    var result: List<AdvertisementConfig>? = null
}