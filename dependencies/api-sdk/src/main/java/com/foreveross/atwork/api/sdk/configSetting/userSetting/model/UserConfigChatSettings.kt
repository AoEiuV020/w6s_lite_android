package com.foreveross.atwork.api.sdk.configSetting.userSetting.model

import com.google.gson.annotations.SerializedName

data class UserConfigChatSettings(

        @SerializedName("chat_assistant_enabled")
        var chatAssistantEnabled: Boolean = true
)