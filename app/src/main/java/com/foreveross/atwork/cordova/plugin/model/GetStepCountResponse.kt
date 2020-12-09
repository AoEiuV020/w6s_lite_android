package com.foreveross.atwork.cordova.plugin.model

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class GetStepCountResponse {

    @SerializedName("steps")
    var steps = 0L


    @SerializedName("distances")
    var distances = StringUtils.EMPTY
}