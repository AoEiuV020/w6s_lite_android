package com.foreveross.atwork.infrastructure.beeworks

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class BeeWorksZoom (

    @SerializedName("appKey")
    var appKey: String = StringUtils.EMPTY,

    @SerializedName("appSecret")
    var appSecret: String = StringUtils.EMPTY,

    @SerializedName("webDomain")
    var webDomain: String = StringUtils.EMPTY,

    @SerializedName("basicUrl")
    var basicUrl:String = StringUtils.EMPTY,

    @SerializedName("url")
    var url: String = StringUtils.EMPTY,

    @SerializedName("inviteUrl")
    var inviteUrl: String = StringUtils.EMPTY,

    @SerializedName("detailUrl")
    var detailUrl: String = StringUtils.EMPTY,

    @SerializedName("sdk")
    var sdk: String = StringUtils.EMPTY,

    @SerializedName("enabled")
    var enabled: Boolean = false
)