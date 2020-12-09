package com.foreveross.atwork.infrastructure.beeworks.voice

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class BeeworksVoiceAliyun(

        @SerializedName("keyId")
        val keyId: String = StringUtils.EMPTY,

        @SerializedName("keySecret")
        val keySecret: String = StringUtils.EMPTY,

        @SerializedName("appKey")
        val appKey: String = StringUtils.EMPTY
)