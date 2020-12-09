package com.foreveross.atwork.api.sdk.configSetting.userSetting.model

import com.google.gson.annotations.SerializedName

data class UserConfigSettings(

        @SerializedName("chat_settings")
        var chatSetting: UserConfigChatSettings? = null
)