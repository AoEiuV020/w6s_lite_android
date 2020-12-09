package com.foreveross.atwork.infrastructure.support.cdn

import com.foreveross.atwork.infrastructure.beeworks.BeeWorks
import com.foreveross.atwork.infrastructure.support.BaseConfig
import com.foreveross.atwork.infrastructure.utils.EnumLookupUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class CdnConfig (

        @SerializedName("enable")
        var enable: Boolean = false,

        @SerializedName("producer")
        var producer: CdnProducer? = null,

        @SerializedName("type")
        var type: String = StringUtils.EMPTY,

        @SerializedName("key")
        var key: String = StringUtils.EMPTY,

        @SerializedName("mediaUrl")
        var mediaUrl: String = StringUtils.EMPTY,

        @SerializedName("expireDuration")
        var expireDuration: Long = 0


): BaseConfig() {


    override fun parse() {
        BeeWorks.getInstance().config.beeWorksCdn?.let {

            enable = it.enable
            producer = EnumLookupUtil.lookup(CdnProducer::class.java, it.producer.toUpperCase())
            type = it.type
            key = it.key
            mediaUrl = it.mediaUrl
            expireDuration = it.expireDuration
        }
    }
}