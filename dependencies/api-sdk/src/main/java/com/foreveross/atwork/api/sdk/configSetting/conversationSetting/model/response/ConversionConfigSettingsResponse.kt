package com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.response

import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingItem
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingParticipant
import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.google.gson.annotations.SerializedName

data class ConversionConfigSettingsResponse(

        @SerializedName("result")
        val result: ConversionSettingsResponseResult = ConversionSettingsResponseResult()


) : BasicResponseJSON()

data class ConversionSettingsResponseResult(

        @SerializedName("conversations")
        val conversations: List<ConversionConfigSettingItem>? = mutableListOf()


)

