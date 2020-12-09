package com.foreveross.atwork.cordova.plugin.voice

import com.foreveross.atwork.cordova.plugin.model.CordovaBasicResponse
import com.google.gson.annotations.SerializedName

class TalkingRecognizeResult (

        @SerializedName("is_last")
        val isLast: Boolean,

        @SerializedName("is_split")
        val isSplit: Boolean = false


) : CordovaBasicResponse()