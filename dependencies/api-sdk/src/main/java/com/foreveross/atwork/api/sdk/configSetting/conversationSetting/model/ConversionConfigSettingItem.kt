package com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model

import com.google.gson.annotations.SerializedName

open class ConversionConfigSettingItem(

        @SerializedName("participant")
        var participant: ConversionConfigSettingParticipant? = null,


        @SerializedName("sticky_enabled")
        var stickyEnabled: Boolean? = null,

        @SerializedName("notify_enabled")
        var notifyEnabled: Boolean? = null,


        @SerializedName("weixin_sync_enabled")
        var weixinSyncEnabled: Boolean? = null,

        @SerializedName("language")
        var language: String? = null


)