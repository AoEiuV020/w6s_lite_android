package com.foreveross.atwork.infrastructure.beeworks.cdn

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class BeeWorksCdn (

        @SerializedName("enable")
        val enable: Boolean = false,

        @SerializedName("producer")
        val producer: String = StringUtils.EMPTY,

        @SerializedName("type")
        val type: String = StringUtils.EMPTY,

        @SerializedName("key")
        val key: String = StringUtils.EMPTY,

        @SerializedName("mediaUrl")
        val mediaUrl: String = StringUtils.EMPTY,

        @SerializedName("expireDuration")
        val expireDuration: Long = 0
)