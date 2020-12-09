package com.foreveross.atwork.cordova.plugin.model

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class VoiceRecordResultResponse (

    @SerializedName("message")
    var message: String = StringUtils.EMPTY
)