package com.foreveross.atwork.api.sdk.configSetting.userSetting.model

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName

data class UserConfigSettingsResponse(
        @SerializedName("result")
        val result: UserConfigSettings? = null

) : BasicResponseJSON()