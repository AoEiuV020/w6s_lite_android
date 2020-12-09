package com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.response

import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingItem
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName

data class ConversionConfigSettingResponse(

        @SerializedName("result")
        val result: ConversionConfigSettingItem? = null


) : BasicResponseJSON()

