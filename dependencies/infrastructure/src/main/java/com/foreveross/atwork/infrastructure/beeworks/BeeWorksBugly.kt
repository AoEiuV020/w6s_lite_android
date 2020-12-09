package com.foreveross.atwork.infrastructure.beeworks

import com.google.gson.annotations.SerializedName

open class BeeWorksBugly(
        @SerializedName("enabled")
        val enabled: Boolean,

        @SerializedName("appId")
        val appId: String?,

        @SerializedName("secret")
        val secret: String?

)