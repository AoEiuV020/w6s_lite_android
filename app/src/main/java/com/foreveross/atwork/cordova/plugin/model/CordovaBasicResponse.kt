package com.foreveross.atwork.cordova.plugin.model

import com.google.gson.annotations.SerializedName

open class CordovaBasicResponse(

        @SerializedName("code")
        var code: Int = -1,

        @SerializedName("message")
        var message: String? = null

)