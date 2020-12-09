package com.foreveross.atwork.cordova.plugin.model

import com.google.gson.annotations.SerializedName

class AdminQueryAppRequest(
        @SerializedName("app_id")
        val appId: String
)