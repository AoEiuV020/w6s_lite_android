package com.foreveross.atwork.infrastructure.beeworks.voice

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class BeeworksVoice (

    @SerializedName("enabled")
    val enabled: Boolean = false,

    @SerializedName("sdk")
    val sdk: String = StringUtils.EMPTY,

    @SerializedName("aliyun")
    val aliyunSdk: BeeworksVoiceAliyun? = null
)